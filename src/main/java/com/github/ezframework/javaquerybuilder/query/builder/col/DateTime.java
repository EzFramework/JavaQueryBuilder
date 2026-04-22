package com.github.ezframework.javaquerybuilder.query.builder.col;

import com.github.ezframework.javaquerybuilder.query.builder.ColumnType;

/**
 * Column type shorthand for {@code DATETIME} (date and time without timezone;
 * MySQL / MariaDB / SQLite).
 *
 * <p>Delegates to {@link ColumnType#DATETIME}. Use modifier methods on the
 * returned {@link ColumnType} to append column-level constraints:</p>
 *
 * <pre>{@code
 * .column("scheduled_at", DateTime.of().notNull())
 * }</pre>
 *
 * @author EzFramework
 * @version 1.1.0
 * @see ColumnType#DATETIME
 */
public final class DateTime {

    /** Prevent instantiation. */
    private DateTime() {
    }

    /**
     * Returns the {@code DATETIME} column type.
     *
     * @return a {@link ColumnType} representing {@code DATETIME}
     */
    public static ColumnType of() {
        return ColumnType.DATETIME;
    }
}
