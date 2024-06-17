package com.metatrope.jdbc.odata;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ODataDriver implements Driver {
    private static final Logger logger = Logger.getLogger(ODataDriver.class.getName());

    private static int MAJOR_VERSION = 1;
    private static int MINOR_VERSION = 0;

    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        if (!acceptsURL(url))
            throw new SQLException("Invalid url: " + url);
        return new ODataConnection(url, info);
    }

    @Override
    public boolean acceptsURL(String url) throws SQLException {
        return ODataJdbcUrl.isValidJODataBCConnectionURL(url);
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
        return new DriverPropertyInfo[] {};
    }

    @Override
    public int getMajorVersion() {
        return MAJOR_VERSION;
    }

    @Override
    public int getMinorVersion() {
        return MINOR_VERSION;
    }

    @Override
    public boolean jdbcCompliant() {
        return true;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return logger;
    }

    static {
        try {
            DriverManager.registerDriver(new ODataDriver());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to register the driver", e);
        }
    }
}
