package com.metatrope.jdbc.shim;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.metatrope.jdbc.common.QueryEngine;
import com.metatrope.jdbc.common.model.SqlRequest;
import com.metatrope.jdbc.common.model.SqlResponse;
import com.metatrope.jdbc.common.model.Type;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.google.common.collect.Lists;

import org.junit.jupiter.api.Test;

public class ShimPreparedStatementTest {
    class FakeQueryEngine implements QueryEngine {

        @Override
        public SqlResponse executeQuery(SqlRequest request) throws SQLException {
            assertEquals("SELECT * FROM account WHERE val = ? AND valInt = ?", request.getSql());
            assertEquals("test", request.getParameters().get(1).getValue());
            assertEquals(Type.STRING, request.getParameters().get(1).getType());
            assertEquals("42", request.getParameters().get(2).getValue());
            assertEquals(Type.INTEGER, request.getParameters().get(2).getType());
            return new SqlResponse(Lists.newArrayList(), Lists.newArrayList());
        }

    }

    @Test
    public void testExecuteQuery() throws ClassNotFoundException, SQLException {
        try (Connection c = new ShimConnection("jdbc:shim:test", new FakeQueryEngine())) {
            PreparedStatement ps = c.prepareStatement("SELECT * FROM account WHERE val = ? AND valInt = ?");
            ps.setString(1, "test");
            ps.setInt(2, 42);
            ResultSet rs = ps.executeQuery();
            assertNotNull(rs);
        }
    }
}
