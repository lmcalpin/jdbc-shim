package com.metatrope.jdbc.odata;

import com.metatrope.jdbc.common.BaseConnectionAdapter;
import com.metatrope.jdbc.common.BasePreparedStatementAdapter;
import com.metatrope.jdbc.common.BaseStatementAdapter;
import com.metatrope.jdbc.common.model.SqlResponse;

import java.sql.ResultSet;

public class ODataPreparedStatement extends BasePreparedStatementAdapter {
    public ODataPreparedStatement(BaseConnectionAdapter connection, String sql) {
        super(connection, sql);
    }

    @Override
    protected ResultSet newResultSet(BaseConnectionAdapter connection, BasePreparedStatementAdapter ps, SqlResponse response) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ResultSet newResultSet(BaseConnectionAdapter conn, BaseStatementAdapter stmt, com.metatrope.jdbc.common.model.SqlResponse response) {
        // TODO Auto-generated method stub
        return null;
    }
}
