package com.github.ezframework.javaquerybuilder.query.builder.col;

import com.github.ezframework.javaquerybuilder.query.builder.ColumnType;

/**
 * Column type shorthand for {@code BINARY(length)} (fixed-length binary string).
 *
 * <p>Delegates to {@link ColumnType#binary(int)}. Use modifier methods on the
 * returned {@link ColumnType} to append column-level constraints:</p>
 *
 * <pre>{@code
 * .column("hash", Binary.of(32).notNull())
 * }</pre>
 *
 * @author EzFramework
 * @version 1.1.0
 * @see ColumnType#binary(int)
 */
public final class Binary {

    /** Prevent instantiation. */
    private Binary() {
    }

    /**
     * Returns the {@code BINARY(length)} column type.
     *
     * @param length number of bytes; must be positive
     * @return a {@link ColumnType} representing {@code BINARY(length)}
     */
    public static ColumnType of(final int length) {
        return ColumnType.binary(length);
    }
}
