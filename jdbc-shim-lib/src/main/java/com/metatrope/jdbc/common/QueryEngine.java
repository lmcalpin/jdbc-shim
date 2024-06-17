package com.metatrope.jdbc.common;

import com.metatrope.jdbc.common.model.SqlRequest;
import com.metatrope.jdbc.common.model.SqlResponse;

import java.sql.SQLException;

public interface QueryEngine {
    public SqlResponse executeQuery(SqlRequest request) throws SQLException;
}
