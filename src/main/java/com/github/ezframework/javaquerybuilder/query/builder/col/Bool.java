package com.github.ezframework.javaquerybuilder.query.builder.col;

import com.github.ezframework.javaquerybuilder.query.builder.ColumnType;

/**
 * Column type shorthand for {@code BOOLEAN}.
 *
 * <p>Named {@code Bool} to avoid shadowing {@link java.lang.Boolean}.
 * Delegates to {@link ColumnType#BOOLEAN}. Use modifier methods on the
 * returned {@link ColumnType} to append column-level constraints:</p>
 *
 * <pre>{@code
 * .column("active", Bool.of().notNull().defaultValue("true"))
 * }</pre>
 *
 * @author EzFramework
 * @version 1.1.0
 * @see ColumnType#BOOLEAN
 */
public final class Bool {

    /** Prevent instantiation. */
    private Bool() {
    }

    /**
     * Returns the {@code BOOLEAN} column type.
     *
     * @return a {@link ColumnType} representing {@code BOOLEAN}
     */
    public static ColumnType of() {
        return ColumnType.BOOLEAN;
    }
}
