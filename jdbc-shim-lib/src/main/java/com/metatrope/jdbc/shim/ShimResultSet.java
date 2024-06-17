package com.metatrope.jdbc.shim;

import com.metatrope.jdbc.common.BaseConnectionAdapter;
import com.metatrope.jdbc.common.BaseResultSetAdapter;
import com.metatrope.jdbc.common.model.SqlResponse;

public class ShimResultSet extends BaseResultSetAdapter {
    public ShimResultSet(BaseConnectionAdapter connection, ShimPreparedStatement statement, SqlResponse response) {
        super(connection, statement, response);
    }

    public ShimResultSet(BaseConnectionAdapter connection, ShimStatement statement, SqlResponse response) {
        super(connection, statement, response);
    }
}
