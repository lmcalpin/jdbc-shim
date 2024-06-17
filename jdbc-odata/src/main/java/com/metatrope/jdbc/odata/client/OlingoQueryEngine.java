package com.metatrope.jdbc.odata.client;

import com.metatrope.jdbc.common.JdbcUrl;
import com.metatrope.jdbc.common.QueryEngine;
import com.metatrope.jdbc.common.model.SqlRequest;
import com.metatrope.jdbc.common.model.SqlResponse;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntitySetRequest;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientEntitySet;
import org.apache.olingo.client.api.domain.ClientProperty;
import org.apache.olingo.client.api.uri.URIBuilder;
import org.apache.olingo.client.core.ODataClientFactory;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectItem;

public class OlingoQueryEngine implements QueryEngine {
    private final JdbcUrl jdbcUrl;
    public static final ODataClient client = ODataClientFactory.getClient();

    public OlingoQueryEngine(JdbcUrl jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    @Override
    public SqlResponse executeQuery(SqlRequest sqlRequest) throws SQLException {
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
                    Column col = ((Column)selectExpr);
                    selectedColumnNames.add(col.getColumnName());
                } else {
                    throw new SQLException("Unsupported sql statement");
                }
            }
            Table table = (Table) select.getFromItem();
            String tableName = table.getName();
            Expression expression = select.getWhere();
            String filter = asFilterQueryOption(expression);
            URIBuilder uriBuilder = client.newURIBuilder(jdbcUrl.getServiceRoot()).appendEntitySetSegment(tableName);
            if (filter != null) {
                uriBuilder = uriBuilder.filter(filter);
            }
            if (selectedColumnNames != null && !selectedColumnNames.isEmpty()) {
                uriBuilder = uriBuilder.select(selectedColumnNames.toArray(new String[0]));
            }
            ODataEntitySetRequest<ClientEntitySet> request = client.getRetrieveRequestFactory().getEntitySetRequest(uriBuilder.build());
            final ODataRetrieveResponse<ClientEntitySet> response = request.execute();
            final ClientEntitySet entitySet = response.getBody();
            SqlResponse res = toSqlResponse(entitySet);
            return res;
        } catch (JSQLParserException e) {
            throw new SQLException("Failed to parse sql");
        }
    }

    protected SqlResponse toSqlResponse(ClientEntitySet entitySet) {
        boolean initializedColumnNames = false;
        List<String> columnNames = new ArrayList<>();
        List<List<Object>> rows = new ArrayList<>();
        for (ClientEntity entity : entitySet.getEntities()) {
            List<ClientProperty> properties = entity.getProperties();
            List<Object> row = new ArrayList<>();
            for (ClientProperty p : properties) {
                if (!initializedColumnNames) {
                    columnNames.add(p.getName());
                }
                row.add(p.getValue().asPrimitive().toValue());
            }
            ;
            initializedColumnNames = true;
            rows.add(row);
        }
        return new SqlResponse(columnNames, rows);
    }

    protected String asFilterQueryOption(Expression expression) {
        if (expression == null)
            return null;
        var visitor = new ODataExpressionVisitor();
        expression.accept(visitor);
        return visitor.getURIFilter();
    }
}
