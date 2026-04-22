package com.github.ezframework.javaquerybuilder.query.builder.col;

import com.github.ezframework.javaquerybuilder.query.builder.ColumnType;

/**
 * Column type shorthand for {@code LONGBLOB} (up to 4 GB; MySQL / MariaDB).
 *
 * <p>Delegates to {@link ColumnType#LONGBLOB}. Use modifier methods on the
 * returned {@link ColumnType} to append column-level constraints:</p>
 *
 * <pre>{@code
 * .column("file_data", LongBlob.of())
 * }</pre>
 *
 * @author EzFramework
 * @version 1.1.0
 * @see ColumnType#LONGBLOB
 */
public final class LongBlob {

    /** Prevent instantiation. */
    private LongBlob() {
    }

    /**
     * Returns the {@code LONGBLOB} column type.
     *
     * @return a {@link ColumnType} representing {@code LONGBLOB}
     */
    public static ColumnType of() {
        return ColumnType.LONGBLOB;
    }
}
