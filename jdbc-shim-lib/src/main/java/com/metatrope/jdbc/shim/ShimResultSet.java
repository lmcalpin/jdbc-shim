package com.metatrope.jdbc.shim;

import com.metatrope.jdbc.common.BaseResultSetAdapter;
import com.metatrope.jdbc.common.model.SqlResponse;

public class ShimResultSet extends BaseResultSetAdapter {
    public ShimResultSet(ShimConnection connection, ShimStatement statement, SqlResponse response) {
        super(connection, statement, response);
    }
}
