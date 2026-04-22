package com.github.ezframework.javaquerybuilder.query.condition;

/**
 * Comparison operators supported by {@link Condition}.
 *
 * @author EzFramework
 * @version 1.0.0
 */
public enum Operator {
    /** Equality ({@code =}). */
    EQ,
    /** Not equal ({@code !=}). */
    NEQ,
    /** Greater than ({@code >}). */
    GT,
    /** Greater than or equal ({@code >=}). */
    GTE,
    /** Less than ({@code <}). */
    LT,
    /** Less than or equal ({@code <=}). */
    LTE,
    /** Substring match ({@code LIKE '%value%'}). */
    LIKE,
    /** Negated substring match ({@code NOT LIKE '%value%'}). */
    NOT_LIKE,
    /** Column is not null ({@code IS NOT NULL}); same as {@link #IS_NOT_NULL}. */
    EXISTS,
    /** Column is null ({@code IS NULL}). */
    IS_NULL,
    /** Column is not null ({@code IS NOT NULL}). */
    IS_NOT_NULL,
    /** Collection membership ({@code IN (...)}). */
    IN,
    /** Negated collection membership ({@code NOT IN (...)}). */
    NOT_IN,
    /** Inclusive range check ({@code BETWEEN ? AND ?}). */
    BETWEEN,
    /**
     * True SQL {@code EXISTS (SELECT ...)} — value must be a
     * {@link com.github.ezframework.javaquerybuilder.query.Query}.
     */
    EXISTS_SUBQUERY,
    /**
     * True SQL {@code NOT EXISTS (SELECT ...)} — value must be a
     * {@link com.github.ezframework.javaquerybuilder.query.Query}.
     */
    NOT_EXISTS_SUBQUERY,
    /**
     * PostgreSQL case-insensitive {@code ILIKE} substring match.
     *
     * <p>Only rendered correctly by
     * {@link com.github.ezframework.javaquerybuilder.query.sql.postgresql.PostgreSqlDialect};
     * using this operator with any other dialect produces no output.
     */
    ILIKE,
    /**
     * PostgreSQL case-insensitive {@code NOT ILIKE} substring match.
     *
     * <p>Only rendered correctly by
     * {@link com.github.ezframework.javaquerybuilder.query.sql.postgresql.PostgreSqlDialect};
     * using this operator with any other dialect produces no output.
     */
    NOT_ILIKE
}
