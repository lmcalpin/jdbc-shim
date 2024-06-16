package com.metatrope.jdbc.shim;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.metatrope.jdbc.shim.client.QueryEngine;
import com.metatrope.jdbc.shim.common.model.SqlRequest;
import com.metatrope.jdbc.shim.common.model.SqlResponse;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.google.common.collect.Lists;

import org.junit.jupiter.api.Test;

public class ShimResultSetTest {
    class FakeQueryEngine implements QueryEngine {

        @Override
        public SqlResponse executeQuery(SqlRequest request) throws SQLException {
            assertEquals("SELECT * FROM account ORDER BY i ASC", request.getSql());
            List<List<Object>> results = new ArrayList<>();
            for (int i = 0; i < 42; i++) {
                results.add(Lists.newArrayList(UUID.randomUUID().toString(), "foo", i));
            }
            List<String> columnNames = Lists.newArrayList("guid", "name", "val");
            return new SqlResponse(columnNames, results);
        }

    }

    @Test
    public void testExecuteSimpleQuery() throws ClassNotFoundException, SQLException {
        try (Connection c = new ShimConnection("jdbc:shim:test", new FakeQueryEngine())) {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM account ORDER BY i ASC");
            assertNotNull(rs);
            for (int i = 0; i < 42; i++) {
                assertTrue(rs.next());
                assertEquals("foo", rs.getString(2));
                int rsi = rs.getInt(3);
                assertTrue(i == rsi);
            }
        }
    }
}
