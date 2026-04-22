package com.github.ezframework.javaquerybuilder.query.builder.col;

import com.github.ezframework.javaquerybuilder.query.builder.ColumnType;

/**
 * Column type shorthand for {@code TEXT} (unbounded character string).
 *
 * <p>Delegates to {@link ColumnType#TEXT}. Use modifier methods on the
 * returned {@link ColumnType} to append column-level constraints:</p>
 *
 * <pre>{@code
 * .column("body", Text.of().notNull())
 * }</pre>
 *
 * @author EzFramework
 * @version 1.1.0
 * @see ColumnType#TEXT
 */
public final class Text {

    /** Prevent instantiation. */
    private Text() {
    }

    /**
     * Returns the {@code TEXT} column type.
     *
     * @return a {@link ColumnType} representing {@code TEXT}
     */
    public static ColumnType of() {
        return ColumnType.TEXT;
    }
}
