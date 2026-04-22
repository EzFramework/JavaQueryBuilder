package com.github.ezframework.javaquerybuilder.query.builder.col;

import com.github.ezframework.javaquerybuilder.query.builder.ColumnType;

/**
 * Column type shorthand for {@code TINYINT}.
 *
 * <p>Delegates to {@link ColumnType#TINYINT}. Use modifier methods on the
 * returned {@link ColumnType} to append column-level constraints:</p>
 *
 * <pre>{@code
 * .column("flag", TinyInt.of().notNull())
 * }</pre>
 *
 * @author EzFramework
 * @version 1.1.0
 * @see ColumnType#TINYINT
 */
public final class TinyInt {

    /** Prevent instantiation. */
    private TinyInt() {
    }

    /**
     * Returns the {@code TINYINT} column type.
     *
     * @return a {@link ColumnType} representing {@code TINYINT}
     */
    public static ColumnType of() {
        return ColumnType.TINYINT;
    }
}
