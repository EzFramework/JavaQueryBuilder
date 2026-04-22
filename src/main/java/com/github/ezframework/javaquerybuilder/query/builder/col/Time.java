package com.github.ezframework.javaquerybuilder.query.builder.col;

import com.github.ezframework.javaquerybuilder.query.builder.ColumnType;

/**
 * Column type shorthand for {@code TIME} (time-of-day without date).
 *
 * <p>Delegates to {@link ColumnType#TIME}. Use modifier methods on the
 * returned {@link ColumnType} to append column-level constraints:</p>
 *
 * <pre>{@code
 * .column("start_time", Time.of().notNull())
 * }</pre>
 *
 * @author EzFramework
 * @version 1.1.0
 * @see ColumnType#TIME
 */
public final class Time {

    /** Prevent instantiation. */
    private Time() {
    }

    /**
     * Returns the {@code TIME} column type.
     *
     * @return a {@link ColumnType} representing {@code TIME}
     */
    public static ColumnType of() {
        return ColumnType.TIME;
    }
}
