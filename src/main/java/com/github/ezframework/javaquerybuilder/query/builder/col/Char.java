package com.github.ezframework.javaquerybuilder.query.builder.col;

import com.github.ezframework.javaquerybuilder.query.builder.ColumnType;

/**
 * Column type shorthand for {@code CHAR(length)} (fixed-length character string).
 *
 * <p>Delegates to {@link ColumnType#charType(int)}. Use modifier methods on the
 * returned {@link ColumnType} to append column-level constraints:</p>
 *
 * <pre>{@code
 * .column("country_code", Char.of(2).notNull())
 * }</pre>
 *
 * @author EzFramework
 * @version 1.1.0
 * @see ColumnType#charType(int)
 */
public final class Char {

    /** Prevent instantiation. */
    private Char() {
    }

    /**
     * Returns the {@code CHAR(length)} column type.
     *
     * @param length number of characters; must be positive
     * @return a {@link ColumnType} representing {@code CHAR(length)}
     */
    public static ColumnType of(final int length) {
        return ColumnType.charType(length);
    }
}
