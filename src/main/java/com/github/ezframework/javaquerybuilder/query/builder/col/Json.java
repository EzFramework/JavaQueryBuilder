package com.github.ezframework.javaquerybuilder.query.builder.col;

import com.github.ezframework.javaquerybuilder.query.builder.ColumnType;

/**
 * Column type shorthand for {@code JSON} documents.
 *
 * <p>Delegates to {@link ColumnType#JSON}. Use modifier methods on the
 * returned {@link ColumnType} to append column-level constraints:</p>
 *
 * <pre>{@code
 * .column("metadata", Json.of().notNull())
 * }</pre>
 *
 * @author EzFramework
 * @version 1.1.0
 * @see ColumnType#JSON
 */
public final class Json {

    /** Prevent instantiation. */
    private Json() {
    }

    /**
     * Returns the {@code JSON} column type.
     *
     * @return a {@link ColumnType} representing {@code JSON}
     */
    public static ColumnType of() {
        return ColumnType.JSON;
    }
}
