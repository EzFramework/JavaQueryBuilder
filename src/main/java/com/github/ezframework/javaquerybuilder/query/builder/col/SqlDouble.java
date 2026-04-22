package com.github.ezframework.javaquerybuilder.query.builder.col;

import com.github.ezframework.javaquerybuilder.query.builder.ColumnType;

/**
 * Column type shorthand for {@code DOUBLE} (double-precision).
 *
 * <p>Named {@code SqlDouble} to avoid shadowing {@link java.lang.Double}.
 * Delegates to {@link ColumnType#DOUBLE}. Use modifier methods on the
 * returned {@link ColumnType} to append column-level constraints:</p>
 *
 * <pre>{@code
 * .column("latitude", SqlDouble.of().notNull())
 * }</pre>
 *
 * @author EzFramework
 * @version 1.1.0
 * @see ColumnType#DOUBLE
 */
public final class SqlDouble {

    /** Prevent instantiation. */
    private SqlDouble() {
    }

    /**
     * Returns the {@code DOUBLE} column type.
     *
     * @return a {@link ColumnType} representing {@code DOUBLE}
     */
    public static ColumnType of() {
        return ColumnType.DOUBLE;
    }
}
