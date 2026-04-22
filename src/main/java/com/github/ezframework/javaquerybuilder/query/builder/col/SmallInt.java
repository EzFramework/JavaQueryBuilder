package com.github.ezframework.javaquerybuilder.query.builder.col;

import com.github.ezframework.javaquerybuilder.query.builder.ColumnType;

/**
 * Column type shorthand for {@code SMALLINT}.
 *
 * <p>Delegates to {@link ColumnType#SMALLINT}. Use modifier methods on the
 * returned {@link ColumnType} to append column-level constraints:</p>
 *
 * <pre>{@code
 * .column("count", SmallInt.of().notNull())
 * }</pre>
 *
 * @author EzFramework
 * @version 1.1.0
 * @see ColumnType#SMALLINT
 */
public final class SmallInt {

    /** Prevent instantiation. */
    private SmallInt() {
    }

    /**
     * Returns the {@code SMALLINT} column type.
     *
     * @return a {@link ColumnType} representing {@code SMALLINT}
     */
    public static ColumnType of() {
        return ColumnType.SMALLINT;
    }
}
