package com.github.ezframework.javaquerybuilder.query.builder.col;

import com.github.ezframework.javaquerybuilder.query.builder.ColumnType;

/**
 * Column type shorthand for {@code NUMERIC(precision, scale)}.
 *
 * <p>Equivalent to {@link Decimal} in most databases. Delegates to
 * {@link ColumnType#numeric(int, int)}. Use modifier methods on the
 * returned {@link ColumnType} to append column-level constraints:</p>
 *
 * <pre>{@code
 * .column("amount", Numeric.of(15, 4).notNull())
 * }</pre>
 *
 * @author EzFramework
 * @version 1.1.0
 * @see ColumnType#numeric(int, int)
 */
public final class Numeric {

    /** Prevent instantiation. */
    private Numeric() {
    }

    /**
     * Returns the {@code NUMERIC(precision, scale)} column type.
     *
     * @param precision total number of significant digits; must be positive
     * @param scale     number of digits after the decimal point; must be &ge; 0
     * @return a {@link ColumnType} representing {@code NUMERIC(precision, scale)}
     */
    public static ColumnType of(final int precision, final int scale) {
        return ColumnType.numeric(precision, scale);
    }
}
