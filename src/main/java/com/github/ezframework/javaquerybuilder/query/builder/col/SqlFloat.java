package com.github.ezframework.javaquerybuilder.query.builder.col;

import com.github.ezframework.javaquerybuilder.query.builder.ColumnType;

/**
 * Column type shorthand for {@code FLOAT} (single-precision).
 *
 * <p>Named {@code SqlFloat} to avoid shadowing {@link java.lang.Float}.
 * Delegates to {@link ColumnType#FLOAT}. Use modifier methods on the
 * returned {@link ColumnType} to append column-level constraints:</p>
 *
 * <pre>{@code
 * .column("score", SqlFloat.of().notNull())
 * }</pre>
 *
 * @author EzFramework
 * @version 1.1.0
 * @see ColumnType#FLOAT
 */
public final class SqlFloat {

    /** Prevent instantiation. */
    private SqlFloat() {
    }

    /**
     * Returns the {@code FLOAT} column type.
     *
     * @return a {@link ColumnType} representing {@code FLOAT}
     */
    public static ColumnType of() {
        return ColumnType.FLOAT;
    }
}
