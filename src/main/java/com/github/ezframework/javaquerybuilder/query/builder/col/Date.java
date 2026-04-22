package com.github.ezframework.javaquerybuilder.query.builder.col;

import com.github.ezframework.javaquerybuilder.query.builder.ColumnType;

/**
 * Column type shorthand for {@code DATE} (calendar date without time-of-day).
 *
 * <p>Delegates to {@link ColumnType#DATE}. Use modifier methods on the
 * returned {@link ColumnType} to append column-level constraints:</p>
 *
 * <pre>{@code
 * .column("birth_date", Date.of().notNull())
 * }</pre>
 *
 * @author EzFramework
 * @version 1.1.0
 * @see ColumnType#DATE
 */
public final class Date {

    /** Prevent instantiation. */
    private Date() {
    }

    /**
     * Returns the {@code DATE} column type.
     *
     * @return a {@link ColumnType} representing {@code DATE}
     */
    public static ColumnType of() {
        return ColumnType.DATE;
    }
}
