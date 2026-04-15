package com.github.ezframework.javaquerybuilder.query.exception;

/**
 * Exception thrown for query rendering errors.
 *
 * @author EzFramework
 * @version 1.0.0
 */
public class QueryRenderException extends Exception {

    /** Default constructor. */
    public QueryRenderException() {
        super();
    }

    /**
     * Constructor with message.
     * @param message the error message
     */
    public QueryRenderException(String message) {
        super(message);
    }

    /**
     * Constructor with message and cause.
     * @param message the error message
     * @param cause the cause of the exception
     */
    public QueryRenderException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with cause.
     * @param cause the cause of the exception
     */
    public QueryRenderException(Throwable cause) {
        super(cause);
    }
}
