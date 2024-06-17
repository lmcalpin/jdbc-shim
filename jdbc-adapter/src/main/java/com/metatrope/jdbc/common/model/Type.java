package com.metatrope.jdbc.common.model;

import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;

public enum Type {
    BYTE, BYTES, BOOLEAN, SHORT, INTEGER, LONG, FLOAT, DOUBLE, DECIMAL, DATE, TIME, DATETIME, TIMESTAMP, STRING;

    public static Type fromSqlType(int sqlType) throws SQLException {
        switch (sqlType) {
        case java.sql.Types.INTEGER:
            return INTEGER;
        case java.sql.Types.LONGNVARCHAR:
        case java.sql.Types.LONGVARCHAR:
        case java.sql.Types.VARCHAR:
            return STRING;
        }
        throw new SQLFeatureNotSupportedException();
    }
    
    public int toSqlType() {
        switch (this) {
        case INTEGER:
            return java.sql.Types.INTEGER;
        default:
            return java.sql.Types.VARCHAR;
        }
    }
}
