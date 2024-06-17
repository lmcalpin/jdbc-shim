package com.metatrope.jdbc.odata;

import com.metatrope.jdbc.common.BaseStatementAdapter;
import com.metatrope.jdbc.common.model.SqlResponse;

import java.sql.ResultSet;

public class ODataStatement extends BaseStatementAdapter<ODataConnection> {
    public ODataStatement(ODataConnection connection) {
        super(connection);
    }

    @Override
    public ResultSet newResultSet(ODataConnection connection, SqlResponse response) {
        return new ODataResultSet(connection, this, response);
    }
}
