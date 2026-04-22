package com.github.ezframework.javaquerybuilder.query.builder.col;

import com.github.ezframework.javaquerybuilder.query.builder.ColumnType;

/**
 * Column type shorthand for {@code BIGSERIAL} (auto-incrementing 8-byte integer; PostgreSQL).
 *
 * <p>Delegates to {@link ColumnType#BIGSERIAL}. Use modifier methods on the
 * returned {@link ColumnType} to append column-level constraints:</p>
 *
 * <pre>{@code
 * .column("id", BigSerial.of())
 * }</pre>
 *
 * @author EzFramework
 * @version 1.1.0
 * @see ColumnType#BIGSERIAL
 */
public final class BigSerial {

    /** Prevent instantiation. */
    private BigSerial() {
    }

    /**
     * Returns the {@code BIGSERIAL} column type.
     *
     * @return a {@link ColumnType} representing {@code BIGSERIAL}
     */
    public static ColumnType of() {
        return ColumnType.BIGSERIAL;
    }
}
