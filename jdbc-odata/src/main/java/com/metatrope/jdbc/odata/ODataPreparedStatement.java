package com.metatrope.jdbc.odata;

import com.metatrope.jdbc.common.BasePreparedStatementAdapter;
import com.metatrope.jdbc.common.model.SqlResponse;

import java.sql.ResultSet;

public class ODataPreparedStatement extends BasePreparedStatementAdapter<ODataConnection> {
    public ODataPreparedStatement(ODataConnection connection, String sql) {
        super(connection, sql);
    }

    @Override
    public ResultSet newResultSet(ODataConnection connection, SqlResponse response) {
        return new ODataResultSet(connection, this, response);
    }
}
