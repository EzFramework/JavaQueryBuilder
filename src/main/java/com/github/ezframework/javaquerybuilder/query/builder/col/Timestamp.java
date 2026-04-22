package com.github.ezframework.javaquerybuilder.query.builder.col;

import com.github.ezframework.javaquerybuilder.query.builder.ColumnType;

/**
 * Column type shorthand for {@code TIMESTAMP} and {@code TIMESTAMP(p)}.
 *
 * <p>Delegates to {@link ColumnType#TIMESTAMP} (no-arg) or
 * {@link ColumnType#timestamp(int)} (with fractional-seconds precision).
 * Use modifier methods on the returned {@link ColumnType} to append
 * column-level constraints:</p>
 *
 * <pre>{@code
 * .column("created_at", Timestamp.of())
 * .column("updated_at", Timestamp.of(3).notNull())
 * }</pre>
 *
 * @author EzFramework
 * @version 1.1.0
 * @see ColumnType#TIMESTAMP
 * @see ColumnType#timestamp(int)
 */
public final class Timestamp {

    /** Prevent instantiation. */
    private Timestamp() {
    }

    /**
     * Returns the {@code TIMESTAMP} column type without fractional-seconds precision.
     *
     * @return a {@link ColumnType} representing {@code TIMESTAMP}
     */
    public static ColumnType of() {
        return ColumnType.TIMESTAMP;
    }

    /**
     * Returns the {@code TIMESTAMP(precision)} column type.
     *
     * @param precision number of fractional-seconds digits (0–6)
     * @return a {@link ColumnType} representing {@code TIMESTAMP(precision)}
     */
    public static ColumnType of(final int precision) {
        return ColumnType.timestamp(precision);
    }
}
