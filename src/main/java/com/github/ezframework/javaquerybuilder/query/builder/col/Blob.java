package com.github.ezframework.javaquerybuilder.query.builder.col;

import com.github.ezframework.javaquerybuilder.query.builder.ColumnType;

/**
 * Column type shorthand for {@code BLOB} (binary large object).
 *
 * <p>Delegates to {@link ColumnType#BLOB}. Use modifier methods on the
 * returned {@link ColumnType} to append column-level constraints:</p>
 *
 * <pre>{@code
 * .column("data", Blob.of())
 * }</pre>
 *
 * @author EzFramework
 * @version 1.1.0
 * @see ColumnType#BLOB
 */
public final class Blob {

    /** Prevent instantiation. */
    private Blob() {
    }

    /**
     * Returns the {@code BLOB} column type.
     *
     * @return a {@link ColumnType} representing {@code BLOB}
     */
    public static ColumnType of() {
        return ColumnType.BLOB;
    }
}
