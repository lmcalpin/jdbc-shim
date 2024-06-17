package com.metatrope.jdbc.odata;

import com.metatrope.jdbc.common.BaseResultSetAdapter;
import com.metatrope.jdbc.common.model.SqlResponse;

public class ODataResultSet extends BaseResultSetAdapter {
    public ODataResultSet(ODataConnection connection, ODataStatement statement, SqlResponse response) {
        super(connection, statement, response);
    }

    public ODataResultSet(ODataConnection connection, ODataPreparedStatement statement, SqlResponse response) {
        super(connection, statement, response);
    }
}
