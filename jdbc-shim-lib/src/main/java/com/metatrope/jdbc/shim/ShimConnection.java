package com.metatrope.jdbc.shim;

import com.metatrope.jdbc.common.BaseConnectionAdapter;
import com.metatrope.jdbc.common.JdbcUrl;
import com.metatrope.jdbc.common.QueryEngine;
import com.metatrope.jdbc.shim.client.ShimQueryEngine;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Properties;

public class ShimConnection extends BaseConnectionAdapter {
    public static final String JDBC_URL_PREFIX = "jdbc:shim:";

    public ShimConnection(String url, QueryEngine queryEngineOverride) {
        super(url, queryEngineOverride);
    }

    public ShimConnection(String url, Properties info) {
        super(url, info);
    }

    @Override
    protected JdbcUrl newJdbcUrl(String url) {
        return new JdbcUrl(url, JDBC_URL_PREFIX);
    }

    @Override
    protected QueryEngine newQueryEngine(JdbcUrl url) {
        return new ShimQueryEngine(url);
    }

    @Override
    protected Statement newStatement() {
        return new ShimStatement(this);
    }

    @Override
    protected PreparedStatement newPreparedStatement(String sql) {
        return new ShimPreparedStatement(this, sql);
    }

    @Override
    protected DatabaseMetaData newDatabaseMetaData() {
        return new ShimDatabaseMetaData(this);
    }
}
