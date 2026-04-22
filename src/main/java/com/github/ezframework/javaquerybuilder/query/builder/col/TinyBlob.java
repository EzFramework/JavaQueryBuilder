package com.github.ezframework.javaquerybuilder.query.builder.col;

import com.github.ezframework.javaquerybuilder.query.builder.ColumnType;

/**
 * Column type shorthand for {@code TINYBLOB} (up to 255 bytes; MySQL / MariaDB).
 *
 * <p>Delegates to {@link ColumnType#TINYBLOB}. Use modifier methods on the
 * returned {@link ColumnType} to append column-level constraints:</p>
 *
 * <pre>{@code
 * .column("icon", TinyBlob.of())
 * }</pre>
 *
 * @author EzFramework
 * @version 1.1.0
 * @see ColumnType#TINYBLOB
 */
public final class TinyBlob {

    /** Prevent instantiation. */
    private TinyBlob() {
    }

    /**
     * Returns the {@code TINYBLOB} column type.
     *
     * @return a {@link ColumnType} representing {@code TINYBLOB}
     */
    public static ColumnType of() {
        return ColumnType.TINYBLOB;
    }
}
