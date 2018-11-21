package com.waes.jsonCompare.exceptions;

import java.io.Serializable;


/**
 * Exception for case that Part is missing for a data
 * @author Omer Hanci
 */
public class PartsMissingException extends Throwable implements Serializable {
    private String message;

    public PartsMissingException(String message) {
        this.message = message;
    }

    public PartsMissingException(Exception e) {
        this.message = e.getMessage();
    }

    public String getMessage() {
        return this.message;
    }
}