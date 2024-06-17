package com.metatrope.jdbc.shim;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.metatrope.jdbc.common.QueryEngine;
import com.metatrope.jdbc.common.model.SqlRequest;
import com.metatrope.jdbc.common.model.SqlResponse;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.jupiter.api.Test;

public class ShimStatementTest {
    class FakeQueryEngine implements QueryEngine {

        @Override
        public SqlResponse executeQuery(SqlRequest request) throws SQLException {
            assertEquals("SELECT * FROM account", request.getSql());
            return new SqlResponse();
        }

    }

    @Test
    public void testExecuteSimpleQuery() throws ClassNotFoundException, SQLException {
        try (Connection c = new ShimConnection("jdbc:shim:test", new FakeQueryEngine())) {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM account");
            assertNotNull(rs);
        }
    }
}
