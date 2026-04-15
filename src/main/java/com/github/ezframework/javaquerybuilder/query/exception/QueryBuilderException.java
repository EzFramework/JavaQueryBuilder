package com.github.ezframework.javaquerybuilder.query.exception;

/**
 * Exception thrown for query builder errors.
 *
 * @author EzFramework
 * @version 1.0.0
 */
public class QueryBuilderException extends Exception {

    /** Default constructor. */
    public QueryBuilderException() {
        super();
    }

    /**
     * Constructor with message.
     * @param message the error message
     */
    public QueryBuilderException(String message) {
        super(message);
    }

    /**
     * Constructor with message and cause.
     * @param message the error message
     * @param cause the cause of the exception
     */
    public QueryBuilderException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with cause.
     * @param cause the cause of the exception
     */
    public QueryBuilderException(Throwable cause) {
        super(cause);
    }
}
