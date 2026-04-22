package com.github.ezframework.javaquerybuilder.query.builder.col;

import com.github.ezframework.javaquerybuilder.query.builder.ColumnType;

/**
 * Column type shorthand for {@code VARBINARY(length)} (variable-length binary string).
 *
 * <p>Delegates to {@link ColumnType#varBinary(int)}. Use modifier methods on the
 * returned {@link ColumnType} to append column-level constraints:</p>
 *
 * <pre>{@code
 * .column("token", VarBinary.of(128).notNull())
 * }</pre>
 *
 * @author EzFramework
 * @version 1.1.0
 * @see ColumnType#varBinary(int)
 */
public final class VarBinary {

    /** Prevent instantiation. */
    private VarBinary() {
    }

    /**
     * Returns the {@code VARBINARY(length)} column type.
     *
     * @param length maximum number of bytes; must be positive
     * @return a {@link ColumnType} representing {@code VARBINARY(length)}
     */
    public static ColumnType of(final int length) {
        return ColumnType.varBinary(length);
    }
}
