package com.waes.jsonCompare.enums;

public enum ResponseType {
    CREATED("Created") ,
    UPDATED("Updated") ,
    ERRORED("Error");

    private final String type;

    ResponseType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }
}
