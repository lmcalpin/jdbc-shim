package com.metatrope.jdbc.shim;

import static java.util.Objects.requireNonNull;

public class Parameter {
    private final String value;
    private final Type type;

    Parameter(String value, Type type) {
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
