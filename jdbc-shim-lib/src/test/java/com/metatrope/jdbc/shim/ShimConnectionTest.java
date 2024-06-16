package com.metatrope.jdbc.shim;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;

public class ShimConnectionTest {
    static {
        try {
            Class.forName(ShimDriver.class.getName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testCreateConnection() throws ClassNotFoundException, SQLException {
        Connection c = DriverManager.getConnection("jdbc:shim:test", "user", "password");
        assertNotNull(c);
    }

    @Test
    public void testInvalidScheme() throws ClassNotFoundException, SQLException {
        assertThrows(SQLException.class, () -> {
            DriverManager.getConnection("jdbc:foo:test", "user", "password");
        });
    }
}
