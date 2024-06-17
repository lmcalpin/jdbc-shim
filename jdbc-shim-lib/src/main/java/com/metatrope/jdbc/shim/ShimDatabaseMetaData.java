package com.metatrope.jdbc.shim;

import com.metatrope.jdbc.common.BaseConnectionAdapter;
import com.metatrope.jdbc.common.BaseDatabaseMetaData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ShimDatabaseMetaData extends BaseDatabaseMetaData {
    public ShimDatabaseMetaData(BaseConnectionAdapter connection) {
        super(connection);
    }

    @Override
    public String getUserName() throws SQLException {
        try (ResultSet rs = executeQuery("SELECT current_user")) {
            rs.next();
            return rs.getString(1);
        }
    }

    private ResultSet executeQuery(String sql) throws SQLException {
        try (Statement statement = getConnection().createStatement()) {
            ResultSet rs = statement.executeQuery(sql);
            return rs;
        }
    }
}
