package com.github.ezframework.javaquerybuilder.query.builder.col;

import com.github.ezframework.javaquerybuilder.query.builder.ColumnType;

/**
 * Column type shorthand for {@code TINYTEXT} (up to 255 characters; MySQL / MariaDB).
 *
 * <p>Delegates to {@link ColumnType#TINYTEXT}. Use modifier methods on the
 * returned {@link ColumnType} to append column-level constraints:</p>
 *
 * <pre>{@code
 * .column("note", TinyText.of())
 * }</pre>
 *
 * @author EzFramework
 * @version 1.1.0
 * @see ColumnType#TINYTEXT
 */
public final class TinyText {

    /** Prevent instantiation. */
    private TinyText() {
    }

    /**
     * Returns the {@code TINYTEXT} column type.
     *
     * @return a {@link ColumnType} representing {@code TINYTEXT}
     */
    public static ColumnType of() {
        return ColumnType.TINYTEXT;
    }
}
