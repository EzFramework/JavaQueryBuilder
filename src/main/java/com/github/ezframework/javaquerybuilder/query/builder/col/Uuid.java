package com.github.ezframework.javaquerybuilder.query.builder.col;

import com.github.ezframework.javaquerybuilder.query.builder.ColumnType;

/**
 * Column type shorthand for {@code UUID} (universally unique identifier).
 *
 * <p>Delegates to {@link ColumnType#UUID}. Use modifier methods on the
 * returned {@link ColumnType} to append column-level constraints:</p>
 *
 * <pre>{@code
 * .column("id", Uuid.of().notNull())
 * }</pre>
 *
 * @author EzFramework
 * @version 1.1.0
 * @see ColumnType#UUID
 */
public final class Uuid {

    /** Prevent instantiation. */
    private Uuid() {
    }

    /**
     * Returns the {@code UUID} column type.
     *
     * @return a {@link ColumnType} representing {@code UUID}
     */
    public static ColumnType of() {
        return ColumnType.UUID;
    }
}
