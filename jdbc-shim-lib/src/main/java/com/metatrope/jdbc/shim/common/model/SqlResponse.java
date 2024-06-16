package com.metatrope.jdbc.shim.common.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SqlResponse {
    private String uuid = UUID.randomUUID().toString();
    private String error;
    private List<String> columnNames;
    private List<List<Object>> results = new ArrayList<>();

    public SqlResponse() {
    }

    public SqlResponse(List<String> columnNames, List<List<Object>> results) {
        this.columnNames = columnNames;
        this.results = results;
    }

    public SqlResponse(Exception e) {
        this.error = e.getMessage();
    }

    public String getUuid() {
        return uuid;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public List<String> getColumnNames() {
        return columnNames;
    }

    public List<List<Object>> getResults() {
        return results;
    }
}
