package com.metatrope.jdbc.odata;

import com.metatrope.jdbc.common.BaseConnectionAdapter;
import com.metatrope.jdbc.common.JdbcUrl;
import com.metatrope.jdbc.common.QueryEngine;
import com.metatrope.jdbc.odata.client.OlingoQueryEngine;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Properties;

public class ODataConnection extends BaseConnectionAdapter {
    private static final String JDBC_URL_PREFIX = "jdbc:odata:";

    public ODataConnection(String url, Properties info) {
        super(url, info);
    }

    @Override
    protected JdbcUrl newJdbcUrl(String url) {
        return new JdbcUrl(url, JDBC_URL_PREFIX);
    }

    @Override
    protected QueryEngine newQueryEngine(JdbcUrl jdbcUrl) {
        return new OlingoQueryEngine(jdbcUrl);
    }

    @Override
    protected Statement newStatement() {
        return new ODataStatement(this);
    }

    @Override
    protected PreparedStatement newPreparedStatement(String sql) {
        return new ODataPreparedStatement(this, sql);
    }

    @Override
    protected DatabaseMetaData newDatabaseMetaData() {
        return new ODataDatabaseMetaData(this);
    }
}
