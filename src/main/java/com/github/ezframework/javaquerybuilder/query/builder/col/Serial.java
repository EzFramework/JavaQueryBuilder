package com.github.ezframework.javaquerybuilder.query.builder.col;

import com.github.ezframework.javaquerybuilder.query.builder.ColumnType;

/**
 * Column type shorthand for {@code SERIAL} (auto-incrementing 4-byte integer; PostgreSQL).
 *
 * <p>Delegates to {@link ColumnType#SERIAL}. Use modifier methods on the
 * returned {@link ColumnType} to append column-level constraints:</p>
 *
 * <pre>{@code
 * .column("id", Serial.of())
 * }</pre>
 *
 * @author EzFramework
 * @version 1.1.0
 * @see ColumnType#SERIAL
 */
public final class Serial {

    /** Prevent instantiation. */
    private Serial() {
    }

    /**
     * Returns the {@code SERIAL} column type.
     *
     * @return a {@link ColumnType} representing {@code SERIAL}
     */
    public static ColumnType of() {
        return ColumnType.SERIAL;
    }
}
