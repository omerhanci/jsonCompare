package com.waes.jsonCompare.exceptions;

import java.io.Serializable;

/**
 * Exception for case that Data is not found in db
 * @author Omer Hanci
 */
public class DataNotFoundException extends Throwable implements Serializable {
    private String message;

    public DataNotFoundException(String message) {
        this.message = message;
    }

    public DataNotFoundException(Exception e) {
        this.message = e.getMessage();
    }

    public String getMessage() {
        return this.message;
    }
}