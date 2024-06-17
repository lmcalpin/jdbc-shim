package com.metatrope.jdbc.shim;

import com.metatrope.jdbc.common.BaseConnectionAdapter;
import com.metatrope.jdbc.common.BasePreparedStatementAdapter;
import com.metatrope.jdbc.common.model.SqlResponse;

import java.sql.ResultSet;

public class ShimPreparedStatement extends BasePreparedStatementAdapter {
    public ShimPreparedStatement(ShimConnection connection, String sql) {
        super(connection, sql);
    }

    @Override
    public ResultSet newResultSet(BaseConnectionAdapter connection, SqlResponse response) {
        return new ShimResultSet(connection, this, response);
    }
}
