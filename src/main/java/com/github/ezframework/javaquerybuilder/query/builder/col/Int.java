package com.github.ezframework.javaquerybuilder.query.builder.col;

import com.github.ezframework.javaquerybuilder.query.builder.ColumnType;

/**
 * Column type shorthand for {@code INT}.
 *
 * <p>Delegates to {@link ColumnType#INT}. Use modifier methods on the
 * returned {@link ColumnType} to append column-level constraints:</p>
 *
 * <pre>{@code
 * .column("id", Int.of().notNull().autoIncrement())
 * }</pre>
 *
 * @author EzFramework
 * @version 1.1.0
 * @see ColumnType#INT
 */
public final class Int {

    /** Prevent instantiation. */
    private Int() {
    }

    /**
     * Returns the {@code INT} column type.
     *
     * @return a {@link ColumnType} representing {@code INT}
     */
    public static ColumnType of() {
        return ColumnType.INT;
    }
}
