package com.github.ezframework.javaquerybuilder.query;

/**
 * Represents a scalar subquery item in a SELECT clause.
 *
 * <p>When included in a query's select list, the subquery is rendered as
 * {@code (SELECT ...) AS alias} in the SELECT clause.
 *
 * @author EzFramework
 * @version 1.0.0
 */
public final class ScalarSelectItem {

    /** The subquery to embed in the SELECT clause. */
    private final Query subquery;

    /** The alias for the scalar subquery column. */
    private final String alias;

    /**
     * Creates a scalar select item.
     *
     * @param subquery the subquery to embed
     * @param alias    the column alias
     */
    public ScalarSelectItem(Query subquery, String alias) {
        this.subquery = subquery;
        this.alias = alias;
    }

    /**
     * Returns the subquery to embed in the SELECT clause.
     *
     * @return the subquery
     */
    public Query getSubquery() {
        return subquery;
    }

    /**
     * Returns the alias for the scalar subquery column.
     *
     * @return the alias
     */
    public String getAlias() {
        return alias;
    }
}
