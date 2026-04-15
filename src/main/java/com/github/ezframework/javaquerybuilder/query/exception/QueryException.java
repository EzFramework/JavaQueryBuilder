package com.github.ezframework.javaquerybuilder.query.exception;

/**
 * Exception thrown for query-related errors.
 *
 * @author EzFramework
 * @version 1.0.0
 */
public class QueryException extends Exception {

    /** Default constructor. */
    public QueryException() {
        super();
    }

    /**
     * Constructor with message.
     * @param message the error message
     */
    public QueryException(String message) {
        super(message);
    }

    /**
     * Constructor with message and cause.
     * @param message the error message
     * @param cause the cause of the exception
     */
    public QueryException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with cause.
     * @param cause the cause of the exception
     */
    public QueryException(Throwable cause) {
        super(cause);
    }
}
