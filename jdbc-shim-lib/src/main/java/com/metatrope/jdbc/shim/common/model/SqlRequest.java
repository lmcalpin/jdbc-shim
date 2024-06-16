package com.metatrope.jdbc.shim.common.model;

import com.metatrope.jdbc.shim.Parameter;

import java.util.Map;

public class SqlRequest {
    private String sql;
    private Map<Integer, Parameter> parameters;

    public SqlRequest(String sql) {
        this.sql = sql;
    }

    public SqlRequest(String sql, Map<Integer, Parameter> parameters) {
        this.sql = sql;
        this.parameters = parameters;
    }

    protected SqlRequest() {
    }

    public String getSql() {
        return sql;
    }

    public Map<Integer, Parameter> getParameters() {
        return parameters;
    }
}
