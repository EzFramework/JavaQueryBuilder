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
    BETWEEN
}
