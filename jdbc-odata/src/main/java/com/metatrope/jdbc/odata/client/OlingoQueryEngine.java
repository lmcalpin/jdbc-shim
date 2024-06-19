package com.metatrope.jdbc.odata.client;

import com.metatrope.jdbc.common.JdbcUrl;
import com.metatrope.jdbc.common.QueryEngine;
import com.metatrope.jdbc.common.model.SqlRequest;
import com.metatrope.jdbc.common.model.SqlResponse;

import java.net.URI;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntitySetRequest;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientEntitySet;
import org.apache.olingo.client.api.domain.ClientProperty;
import org.apache.olingo.client.core.ODataClientFactory;

public class OlingoQueryEngine implements QueryEngine {
    private final JdbcUrl jdbcUrl;
    public static final ODataClient client = ODataClientFactory.getClient();
    private static final SqlConverter sqlConverter = new SqlConverter(client);

    public OlingoQueryEngine(JdbcUrl jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    @Override
    public SqlResponse executeQuery(SqlRequest sqlRequest) throws SQLException {
        URI uri = sqlConverter.convert(jdbcUrl.getServiceRoot(), sqlRequest);
        ODataEntitySetRequest<ClientEntitySet> request = client.getRetrieveRequestFactory().getEntitySetRequest(uri);
        final ODataRetrieveResponse<ClientEntitySet> response = request.execute();
        final ClientEntitySet entitySet = response.getBody();
        SqlResponse res = toSqlResponse(entitySet);
        return res;
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
            initializedColumnNames = true;
            rows.add(row);
        }
        return new SqlResponse(columnNames, rows);
    }
}
