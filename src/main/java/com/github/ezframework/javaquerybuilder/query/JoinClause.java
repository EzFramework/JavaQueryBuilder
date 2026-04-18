package com.github.ezframework.javaquerybuilder.query;

/**
 * Represents a single JOIN entry in a SELECT query, which may target either a
 * plain table name or a derived-table subquery.
 *
 * <p>Exactly one of {@link #getTable()} or {@link #getSubquery()} will be non-null.
 * When {@link #getSubquery()} is non-null, {@link #getAlias()} provides the alias
 * used in the rendered SQL (e.g. {@code JOIN (SELECT ...) AS alias ON ...}).
 *
 * @author EzFramework
 * @version 1.0.0
 */
public final class JoinClause {

    /**
     * The join type.
     */
    public enum Type {
        /** {@code INNER JOIN}. */
        INNER,
        /** {@code LEFT JOIN}. */
        LEFT,
        /** {@code RIGHT JOIN}. */
        RIGHT,
        /** {@code CROSS JOIN}. */
        CROSS
    }

    /** The join type. */
    private final Type type;

    /** The table name for a plain table join; {@code null} for subquery joins. */
    private final String table;

    /** The subquery for a derived-table join; {@code null} for plain table joins. */
    private final Query subquery;

    /** The alias used when the join target is a subquery. */
    private final String alias;

    /** Raw SQL fragment for the ON condition, e.g. {@code "t.id = other.id"}. */
    private final String onCondition;

    /**
     * Creates a plain-table JOIN clause.
     *
     * @param type        the join type
     * @param table       the target table name
     * @param onCondition the raw SQL ON condition
     */
    public JoinClause(Type type, String table, String onCondition) {
        this.type = type;
        this.table = table;
        this.subquery = null;
        this.alias = null;
        this.onCondition = onCondition;
    }

    /**
     * Creates a subquery (derived-table) JOIN clause.
     *
     * @param type        the join type
     * @param subquery    the subquery to join against
     * @param alias       the alias for the derived table
     * @param onCondition the raw SQL ON condition
     */
    public JoinClause(Type type, Query subquery, String alias, String onCondition) {
        this.type = type;
        this.table = null;
        this.subquery = subquery;
        this.alias = alias;
        this.onCondition = onCondition;
    }

    /**
     * Returns the join type.
     *
     * @return the join type
     */
    public Type getType() {
        return type;
    }

    /**
     * Returns the target table name for a plain table join, or {@code null} for a subquery join.
     *
     * @return the table name, or {@code null}
     */
    public String getTable() {
        return table;
    }

    /**
     * Returns the subquery for a derived-table join, or {@code null} for a plain table join.
     *
     * @return the subquery, or {@code null}
     */
    public Query getSubquery() {
        return subquery;
    }

    /**
     * Returns the alias used for the derived table in a subquery join, or {@code null}
     * for a plain table join.
     *
     * @return the alias, or {@code null}
     */
    public String getAlias() {
        return alias;
    }

    /**
     * Returns the raw SQL ON condition fragment.
     *
     * @return the ON condition SQL fragment
     */
    public String getOnCondition() {
        return onCondition;
    }
}
