package com.metatrope.jdbc.common.model;

import static java.util.Objects.requireNonNull;

public class Parameter {
    private String value;
    private Type type;
    
    protected Parameter() {}

    public Parameter(String value, Type type) {
        this.value = requireNonNull(value, "value is null");
        this.type = requireNonNull(type, "type is null");
    }

    public String getValue() {
        return value;
    }

    public Type getType() {
        return type;
    }
}
