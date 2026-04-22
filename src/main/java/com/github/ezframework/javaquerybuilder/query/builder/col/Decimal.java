package com.github.ezframework.javaquerybuilder.query.builder.col;

import com.github.ezframework.javaquerybuilder.query.builder.ColumnType;

/**
 * Column type shorthand for {@code DECIMAL(precision, scale)}.
 *
 * <p>Delegates to {@link ColumnType#decimal(int, int)}. Use modifier methods on the
 * returned {@link ColumnType} to append column-level constraints:</p>
 *
 * <pre>{@code
 * .column("price", Decimal.of(10, 2).notNull())
 * }</pre>
 *
 * @author EzFramework
 * @version 1.1.0
 * @see ColumnType#decimal(int, int)
 */
public final class Decimal {

    /** Prevent instantiation. */
    private Decimal() {
    }

    /**
     * Returns the {@code DECIMAL(precision, scale)} column type.
     *
     * @param precision total number of significant digits; must be positive
     * @param scale     number of digits after the decimal point; must be &ge; 0
     * @return a {@link ColumnType} representing {@code DECIMAL(precision, scale)}
     */
    public static ColumnType of(final int precision, final int scale) {
        return ColumnType.decimal(precision, scale);
    }
}
