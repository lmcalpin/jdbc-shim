package com.metatrope.jdbc.odata.client;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.select.PlainSelect;

public class ODataExpressionVisitorTest {
    @Test
    public void testAnd() throws JSQLParserException {
        PlainSelect select = (PlainSelect) CCJSqlParserUtil.parse("SELECT * FROM Test WHERE foo = 3 AND bar = 'baz' AND x = y");
        var visitor = new ODataExpressionVisitor();
        select.getWhere().accept(visitor);
        String filter = visitor.getURIFilter();
        assertEquals("foo eq 3 and bar eq 'baz' and x eq y", filter);
    }

    @Test
    public void testOr() throws JSQLParserException {
        PlainSelect select = (PlainSelect) CCJSqlParserUtil.parse("SELECT * FROM Test WHERE foo = 3 OR bar = 'baz' OR x = y");
        var visitor = new ODataExpressionVisitor();
        select.getWhere().accept(visitor);
        String filter = visitor.getURIFilter();
        assertEquals("foo eq 3 or bar eq 'baz' or x eq y", filter);
    }

    @Test
    public void testLessThan() throws JSQLParserException {
        PlainSelect select = (PlainSelect) CCJSqlParserUtil.parse("SELECT * FROM Test WHERE foo < 42");
        var visitor = new ODataExpressionVisitor();
        select.getWhere().accept(visitor);
        String filter = visitor.getURIFilter();
        assertEquals("foo lt 42", filter);
    }
}
