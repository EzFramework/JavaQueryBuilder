package com.github.ezframework.javaquerybuilder.query.builder.col;

import com.github.ezframework.javaquerybuilder.query.builder.ColumnType;

/**
 * Column type shorthand for {@code MEDIUMBLOB} (up to 16 MB; MySQL / MariaDB).
 *
 * <p>Delegates to {@link ColumnType#MEDIUMBLOB}. Use modifier methods on the
 * returned {@link ColumnType} to append column-level constraints:</p>
 *
 * <pre>{@code
 * .column("thumbnail", MediumBlob.of())
 * }</pre>
 *
 * @author EzFramework
 * @version 1.1.0
 * @see ColumnType#MEDIUMBLOB
 */
public final class MediumBlob {

    /** Prevent instantiation. */
    private MediumBlob() {
    }

    /**
     * Returns the {@code MEDIUMBLOB} column type.
     *
     * @return a {@link ColumnType} representing {@code MEDIUMBLOB}
     */
    public static ColumnType of() {
        return ColumnType.MEDIUMBLOB;
    }
}
