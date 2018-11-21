package com.waes.jsonCompare.enums;

/**
 * enum for response type of save function
 * @author Omer Hanci
 */
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
