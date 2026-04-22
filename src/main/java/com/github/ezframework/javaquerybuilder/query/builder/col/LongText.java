package com.github.ezframework.javaquerybuilder.query.builder.col;

import com.github.ezframework.javaquerybuilder.query.builder.ColumnType;

/**
 * Column type shorthand for {@code LONGTEXT} (up to 4 GB; MySQL / MariaDB).
 *
 * <p>Delegates to {@link ColumnType#LONGTEXT}. Use modifier methods on the
 * returned {@link ColumnType} to append column-level constraints:</p>
 *
 * <pre>{@code
 * .column("document", LongText.of())
 * }</pre>
 *
 * @author EzFramework
 * @version 1.1.0
 * @see ColumnType#LONGTEXT
 */
public final class LongText {

    /** Prevent instantiation. */
    private LongText() {
    }

    /**
     * Returns the {@code LONGTEXT} column type.
     *
     * @return a {@link ColumnType} representing {@code LONGTEXT}
     */
    public static ColumnType of() {
        return ColumnType.LONGTEXT;
    }
}
