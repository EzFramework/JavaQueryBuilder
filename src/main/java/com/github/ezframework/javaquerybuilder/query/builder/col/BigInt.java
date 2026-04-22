package com.github.ezframework.javaquerybuilder.query.builder.col;

import com.github.ezframework.javaquerybuilder.query.builder.ColumnType;

/**
 * Column type shorthand for {@code BIGINT}.
 *
 * <p>Delegates to {@link ColumnType#BIGINT}. Use modifier methods on the
 * returned {@link ColumnType} to append column-level constraints:</p>
 *
 * <pre>{@code
 * .column("user_id", BigInt.of().notNull())
 * }</pre>
 *
 * @author EzFramework
 * @version 1.1.0
 * @see ColumnType#BIGINT
 */
public final class BigInt {

    /** Prevent instantiation. */
    private BigInt() {
    }

    /**
     * Returns the {@code BIGINT} column type.
     *
     * @return a {@link ColumnType} representing {@code BIGINT}
     */
    public static ColumnType of() {
        return ColumnType.BIGINT;
    }
}
