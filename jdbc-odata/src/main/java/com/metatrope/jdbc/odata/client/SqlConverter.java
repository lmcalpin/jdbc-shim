package com.metatrope.jdbc.odata.client;

import com.metatrope.jdbc.common.model.SqlRequest;

import java.net.URI;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.uri.URIBuilder;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectItem;

public class SqlConverter {
    private final ODataClient client;

    public SqlConverter(ODataClient client) {
        this.client = client;
    }

    public URI convert(String serviceRoot, SqlRequest sqlRequest) throws SQLException {
        try {
            PlainSelect select = (PlainSelect) CCJSqlParserUtil.parse(sqlRequest.getSql());
            List<SelectItem<?>> selectItems = select.getSelectItems();
            List<String> selectedColumnNames = new ArrayList<>();
            for (SelectItem<?> selectItem : selectItems) {
                Expression selectExpr = selectItem.getExpression();
                if (selectExpr instanceof AllColumns) {
                    selectedColumnNames = null;
                    break;
                } else if (selectExpr instanceof Column) {
                    Column col = ((Column) selectExpr);
                    selectedColumnNames.add(col.getColumnName());
                } else {
                    throw new SQLException("Unsupported sql statement");
                }
            }
            Table table = (Table) select.getFromItem();
            String tableName = table.getName();
            Expression expression = select.getWhere();
            String filter = asFilterQueryOption(expression);
            URIBuilder uriBuilder = client.newURIBuilder(serviceRoot).appendEntitySetSegment(tableName);
            if (filter != null) {
                uriBuilder = uriBuilder.filter(filter);
            }
            if (selectedColumnNames != null && !selectedColumnNames.isEmpty()) {
                uriBuilder = uriBuilder.select(selectedColumnNames.toArray(new String[0]));
            }
            return uriBuilder.build();
        } catch (JSQLParserException e) {
            throw new SQLException("Failed to parse sql");
        }
    }

    protected String asFilterQueryOption(Expression expression) {
        if (expression == null)
            return null;
        var visitor = new ODataExpressionVisitor();
        expression.accept(visitor);
        return visitor.getURIFilter();
    }
}
