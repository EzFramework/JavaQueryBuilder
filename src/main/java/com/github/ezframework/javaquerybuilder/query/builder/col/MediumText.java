package com.github.ezframework.javaquerybuilder.query.builder.col;

import com.github.ezframework.javaquerybuilder.query.builder.ColumnType;

/**
 * Column type shorthand for {@code MEDIUMTEXT} (up to 16 MB; MySQL / MariaDB).
 *
 * <p>Delegates to {@link ColumnType#MEDIUMTEXT}. Use modifier methods on the
 * returned {@link ColumnType} to append column-level constraints:</p>
 *
 * <pre>{@code
 * .column("content", MediumText.of())
 * }</pre>
 *
 * @author EzFramework
 * @version 1.1.0
 * @see ColumnType#MEDIUMTEXT
 */
public final class MediumText {

    /** Prevent instantiation. */
    private MediumText() {
    }

    /**
     * Returns the {@code MEDIUMTEXT} column type.
     *
     * @return a {@link ColumnType} representing {@code MEDIUMTEXT}
     */
    public static ColumnType of() {
        return ColumnType.MEDIUMTEXT;
    }
}
