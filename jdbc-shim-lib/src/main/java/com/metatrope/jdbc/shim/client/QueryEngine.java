package com.metatrope.jdbc.shim.client;

import com.metatrope.jdbc.shim.common.model.SqlRequest;
import com.metatrope.jdbc.shim.common.model.SqlResponse;

import java.sql.SQLException;

public interface QueryEngine {
    public SqlResponse executeQuery(SqlRequest request) throws SQLException;
}
