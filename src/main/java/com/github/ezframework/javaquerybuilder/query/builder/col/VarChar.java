package com.github.ezframework.javaquerybuilder.query.builder.col;

import com.github.ezframework.javaquerybuilder.query.builder.ColumnType;

/**
 * Column type shorthand for {@code VARCHAR(length)}.
 *
 * <p>Delegates to {@link ColumnType#varChar(int)}. Use modifier methods on the
 * returned {@link ColumnType} to append column-level constraints:</p>
 *
 * <pre>{@code
 * .column("username", VarChar.of(64).notNull().unique())
 * }</pre>
 *
 * @author EzFramework
 * @version 1.1.0
 * @see ColumnType#varChar(int)
 */
public final class VarChar {

    /** Prevent instantiation. */
    private VarChar() {
    }

    /**
     * Returns the {@code VARCHAR(length)} column type.
     *
     * @param length maximum number of characters; must be positive
     * @return a {@link ColumnType} representing {@code VARCHAR(length)}
     */
    public static ColumnType of(final int length) {
        return ColumnType.varChar(length);
    }
}
