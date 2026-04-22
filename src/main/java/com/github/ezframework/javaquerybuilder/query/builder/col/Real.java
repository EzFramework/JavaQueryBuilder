package com.github.ezframework.javaquerybuilder.query.builder.col;

import com.github.ezframework.javaquerybuilder.query.builder.ColumnType;

/**
 * Column type shorthand for {@code REAL} (standard SQL double-precision alias).
 *
 * <p>Delegates to {@link ColumnType#REAL}. Use modifier methods on the
 * returned {@link ColumnType} to append column-level constraints:</p>
 *
 * <pre>{@code
 * .column("weight", Real.of().notNull())
 * }</pre>
 *
 * @author EzFramework
 * @version 1.1.0
 * @see ColumnType#REAL
 */
public final class Real {

    /** Prevent instantiation. */
    private Real() {
    }

    /**
     * Returns the {@code REAL} column type.
     *
     * @return a {@link ColumnType} representing {@code REAL}
     */
    public static ColumnType of() {
        return ColumnType.REAL;
    }
}
