package com.metatrope.jdbc.shim;

import com.metatrope.jdbc.common.BaseConnectionAdapter;
import com.metatrope.jdbc.common.BaseStatementAdapter;
import com.metatrope.jdbc.common.model.SqlResponse;

import java.sql.ResultSet;

public class ShimStatement extends BaseStatementAdapter  {
    public ShimStatement(ShimConnection connection) {
        super(connection);
    }

    @Override
    public ResultSet newResultSet(BaseConnectionAdapter connection, SqlResponse response) {
        return new ShimResultSet(connection, this, response);
    }
}
