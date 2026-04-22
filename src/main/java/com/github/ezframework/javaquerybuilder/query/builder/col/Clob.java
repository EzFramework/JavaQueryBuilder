package com.github.ezframework.javaquerybuilder.query.builder.col;

import com.github.ezframework.javaquerybuilder.query.builder.ColumnType;

/**
 * Column type shorthand for {@code CLOB} (character large object; standard SQL).
 *
 * <p>Delegates to {@link ColumnType#CLOB}. Use modifier methods on the
 * returned {@link ColumnType} to append column-level constraints:</p>
 *
 * <pre>{@code
 * .column("report", Clob.of())
 * }</pre>
 *
 * @author EzFramework
 * @version 1.1.0
 * @see ColumnType#CLOB
 */
public final class Clob {

    /** Prevent instantiation. */
    private Clob() {
    }

    /**
     * Returns the {@code CLOB} column type.
     *
     * @return a {@link ColumnType} representing {@code CLOB}
     */
    public static ColumnType of() {
        return ColumnType.CLOB;
    }
}
