package com.metatrope.jdbc.common.model;

import java.util.Collections;
import java.util.List;

public class SqlRequest {
    private String sql;
    private List<Parameter> parameters;

    public SqlRequest(String sql) {
        this.sql = sql;
    }

    public SqlRequest(String sql, List<Parameter> parameters) {
        this.sql = sql;
        this.parameters = parameters;
    }

    protected SqlRequest() {
    }

    public String getSql() {
        return sql;
    }

    public List<Parameter> getParameters() {
        if (parameters == null) {
            return Collections.emptyList();
        }
        return parameters;
    }
}
