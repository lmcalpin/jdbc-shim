package com.metatrope.jdbc.odata.client;

import java.util.List;
import java.util.Stack;

import net.sf.jsqlparser.expression.AllValue;
import net.sf.jsqlparser.expression.AnalyticExpression;
import net.sf.jsqlparser.expression.AnyComparisonExpression;
import net.sf.jsqlparser.expression.ArrayConstructor;
import net.sf.jsqlparser.expression.ArrayExpression;
import net.sf.jsqlparser.expression.CaseExpression;
import net.sf.jsqlparser.expression.CastExpression;
import net.sf.jsqlparser.expression.CollateExpression;
import net.sf.jsqlparser.expression.ConnectByRootOperator;
import net.sf.jsqlparser.expression.DateTimeLiteralExpression;
import net.sf.jsqlparser.expression.DateValue;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.expression.ExtractExpression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.HexValue;
import net.sf.jsqlparser.expression.IntervalExpression;
import net.sf.jsqlparser.expression.JdbcNamedParameter;
import net.sf.jsqlparser.expression.JdbcParameter;
import net.sf.jsqlparser.expression.JsonAggregateFunction;
import net.sf.jsqlparser.expression.JsonExpression;
import net.sf.jsqlparser.expression.JsonFunction;
import net.sf.jsqlparser.expression.KeepExpression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.MySQLGroupConcat;
import net.sf.jsqlparser.expression.NextValExpression;
import net.sf.jsqlparser.expression.NotExpression;
import net.sf.jsqlparser.expression.NullValue;
import net.sf.jsqlparser.expression.NumericBind;
import net.sf.jsqlparser.expression.OracleHierarchicalExpression;
import net.sf.jsqlparser.expression.OracleHint;
import net.sf.jsqlparser.expression.OracleNamedFunctionParameter;
import net.sf.jsqlparser.expression.OverlapsCondition;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.RangeExpression;
import net.sf.jsqlparser.expression.RowConstructor;
import net.sf.jsqlparser.expression.RowGetExpression;
import net.sf.jsqlparser.expression.SignedExpression;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.TimeKeyExpression;
import net.sf.jsqlparser.expression.TimeValue;
import net.sf.jsqlparser.expression.TimestampValue;
import net.sf.jsqlparser.expression.TimezoneExpression;
import net.sf.jsqlparser.expression.TranscodingFunction;
import net.sf.jsqlparser.expression.TrimFunction;
import net.sf.jsqlparser.expression.UserVariable;
import net.sf.jsqlparser.expression.VariableAssignment;
import net.sf.jsqlparser.expression.WhenClause;
import net.sf.jsqlparser.expression.XMLSerializeExpr;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseAnd;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseLeftShift;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseOr;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseRightShift;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseXor;
import net.sf.jsqlparser.expression.operators.arithmetic.Concat;
import net.sf.jsqlparser.expression.operators.arithmetic.Division;
import net.sf.jsqlparser.expression.operators.arithmetic.IntegerDivision;
import net.sf.jsqlparser.expression.operators.arithmetic.Modulo;
import net.sf.jsqlparser.expression.operators.arithmetic.Multiplication;
import net.sf.jsqlparser.expression.operators.arithmetic.Subtraction;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.conditional.XorExpression;
import net.sf.jsqlparser.expression.operators.relational.Between;
import net.sf.jsqlparser.expression.operators.relational.ContainedBy;
import net.sf.jsqlparser.expression.operators.relational.Contains;
import net.sf.jsqlparser.expression.operators.relational.DoubleAnd;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExistsExpression;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.FullTextSearch;
import net.sf.jsqlparser.expression.operators.relational.GeometryDistance;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.IsBooleanExpression;
import net.sf.jsqlparser.expression.operators.relational.IsDistinctExpression;
import net.sf.jsqlparser.expression.operators.relational.IsNullExpression;
import net.sf.jsqlparser.expression.operators.relational.JsonOperator;
import net.sf.jsqlparser.expression.operators.relational.LikeExpression;
import net.sf.jsqlparser.expression.operators.relational.Matches;
import net.sf.jsqlparser.expression.operators.relational.MemberOfExpression;
import net.sf.jsqlparser.expression.operators.relational.MinorThan;
import net.sf.jsqlparser.expression.operators.relational.MinorThanEquals;
import net.sf.jsqlparser.expression.operators.relational.NotEqualsTo;
import net.sf.jsqlparser.expression.operators.relational.RegExpMatchOperator;
import net.sf.jsqlparser.expression.operators.relational.SimilarToExpression;
import net.sf.jsqlparser.expression.operators.relational.TSQLLeftJoin;
import net.sf.jsqlparser.expression.operators.relational.TSQLRightJoin;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.ParenthesedSelect;
import net.sf.jsqlparser.statement.select.Select;

public class ODataExpressionVisitor implements ExpressionVisitor {
    private List<Token> tokenStack;

    public enum TokenType {
        LITERAL, PROPERTY, OPERATOR;
    }

    public class Token {
        public String value;
        public TokenType type;

        public Token(String value) {
            this(value, TokenType.LITERAL);
        }

        public Token(String value, TokenType type) {
            this.value = value;
            this.type = type;
        }
    }

    public ODataExpressionVisitor() {
        this.tokenStack = new Stack<>();
    }

    public String getURIFilter() {
        StringBuilder sb = new StringBuilder();
        for (Token token : tokenStack) {
            sb.append(token.value);
            sb.append(' ');
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    @Override
    public void visit(BitwiseRightShift aThis) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void visit(BitwiseLeftShift aThis) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void visit(NullValue nullValue) {
        tokenStack.add(new Token("null", TokenType.LITERAL));
    }

    @Override
    public void visit(Function function) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void visit(SignedExpression signedExpression) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void visit(JdbcParameter jdbcParameter) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void visit(JdbcNamedParameter jdbcNamedParameter) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void visit(DoubleValue doubleValue) {
        tokenStack.add(new Token(doubleValue.toString(), TokenType.LITERAL));
    }

    @Override
    public void visit(LongValue longValue) {
        tokenStack.add(new Token(longValue.toString(), TokenType.LITERAL));
    }

    @Override
    public void visit(HexValue hexValue) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void visit(DateValue dateValue) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void visit(TimeValue timeValue) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void visit(TimestampValue timestampValue) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void visit(Parenthesis parenthesis) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void visit(StringValue stringValue) {
        tokenStack.add(new Token(stringValue.toString(), TokenType.LITERAL));
    }

    @Override
    public void visit(Addition addition) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void visit(Division division) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void visit(IntegerDivision division) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void visit(Multiplication multiplication) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void visit(Subtraction subtraction) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void visit(AndExpression andExpression) {
        andExpression.getLeftExpression().accept(this);
        tokenStack.add(new Token("and", TokenType.OPERATOR));
        andExpression.getRightExpression().accept(this);
    }

    @Override
    public void visit(OrExpression orExpression) {
        orExpression.getLeftExpression().accept(this);
        tokenStack.add(new Token("or", TokenType.OPERATOR));
        orExpression.getRightExpression().accept(this);
    }

    @Override
    public void visit(XorExpression orExpression) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void visit(Between between) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void visit(OverlapsCondition overlapsCondition) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void visit(EqualsTo equalsTo) {
        equalsTo.getLeftExpression().accept(this);
        tokenStack.add(new Token("eq", TokenType.OPERATOR));
        equalsTo.getRightExpression().accept(this);
    }

    @Override
    public void visit(GreaterThan greaterThan) {
        greaterThan.getLeftExpression().accept(this);
        tokenStack.add(new Token("gt", TokenType.OPERATOR));
        greaterThan.getRightExpression().accept(this);
    }

    @Override
    public void visit(GreaterThanEquals greaterThanEquals) {
        greaterThanEquals.getLeftExpression().accept(this);
        tokenStack.add(new Token("ge", TokenType.OPERATOR));
        greaterThanEquals.getRightExpression().accept(this);
    }

    @Override
    public void visit(InExpression inExpression) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void visit(FullTextSearch fullTextSearch) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void visit(IsNullExpression isNullExpression) {
        isNullExpression.getLeftExpression().accept(this);
        tokenStack.add(new Token("eq null", TokenType.OPERATOR));
    }

    @Override
    public void visit(IsBooleanExpression isBooleanExpression) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void visit(LikeExpression likeExpression) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void visit(MinorThan minorThan) {
        minorThan.getLeftExpression().accept(this);
        tokenStack.add(new Token("lt", TokenType.OPERATOR));
        minorThan.getRightExpression().accept(this);
    }

    @Override
    public void visit(MinorThanEquals minorThanEquals) {
        minorThanEquals.getLeftExpression().accept(this);
        tokenStack.add(new Token("le", TokenType.OPERATOR));
        minorThanEquals.getRightExpression().accept(this);
    }

    @Override
    public void visit(NotEqualsTo notEqualsTo) {
        notEqualsTo.getLeftExpression().accept(this);
        tokenStack.add(new Token("ne", TokenType.OPERATOR));
        notEqualsTo.getRightExpression().accept(this);
    }

    @Override
    public void visit(DoubleAnd doubleAnd) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void visit(Contains contains) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void visit(ContainedBy containedBy) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void visit(ParenthesedSelect selectBody) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void visit(Column tableColumn) {
        // TODO: validate column names
        tokenStack.add(new Token(tableColumn.getColumnName(), TokenType.PROPERTY));
    }

    @Override
    public void visit(CaseExpression caseExpression) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void visit(WhenClause whenClause) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void visit(ExistsExpression existsExpression) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void visit(MemberOfExpression memberOfExpression) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void visit(AnyComparisonExpression anyComparisonExpression) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void visit(Concat concat) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void visit(Matches matches) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void visit(BitwiseAnd bitwiseAnd) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void visit(BitwiseOr bitwiseOr) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void visit(BitwiseXor bitwiseXor) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void visit(CastExpression cast) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void visit(Modulo modulo) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void visit(AnalyticExpression aexpr) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void visit(ExtractExpression eexpr) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void visit(IntervalExpression iexpr) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void visit(OracleHierarchicalExpression oexpr) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void visit(RegExpMatchOperator rexpr) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void visit(JsonExpression jsonExpr) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void visit(JsonOperator jsonExpr) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void visit(UserVariable var) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void visit(NumericBind bind) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void visit(KeepExpression aexpr) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void visit(MySQLGroupConcat groupConcat) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void visit(ExpressionList<?> expressionList) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void visit(RowConstructor<?> rowConstructor) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void visit(RowGetExpression rowGetExpression) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void visit(OracleHint hint) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void visit(TimeKeyExpression timeKeyExpression) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void visit(DateTimeLiteralExpression literal) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void visit(NotExpression aThis) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void visit(NextValExpression aThis) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void visit(CollateExpression aThis) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void visit(SimilarToExpression aThis) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void visit(ArrayExpression aThis) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void visit(ArrayConstructor aThis) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void visit(VariableAssignment aThis) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void visit(XMLSerializeExpr aThis) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void visit(TimezoneExpression aThis) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void visit(JsonAggregateFunction aThis) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void visit(JsonFunction aThis) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void visit(ConnectByRootOperator aThis) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void visit(OracleNamedFunctionParameter aThis) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void visit(AllColumns allColumns) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void visit(AllTableColumns allTableColumns) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void visit(AllValue allValue) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void visit(IsDistinctExpression isDistinctExpression) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void visit(GeometryDistance geometryDistance) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void visit(Select selectBody) {

    }

    @Override
    public void visit(TranscodingFunction transcodingFunction) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void visit(TrimFunction trimFunction) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void visit(RangeExpression rangeExpression) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void visit(TSQLLeftJoin tsqlLeftJoin) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void visit(TSQLRightJoin tsqlRightJoin) {
        throw new UnsupportedOperationException();

    }

}
