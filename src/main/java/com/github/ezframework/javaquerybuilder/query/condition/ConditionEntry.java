package com.github.ezframework.javaquerybuilder.query.condition;

/**
 * An immutable entry in a WHERE clause, pairing a column name with a
 * {@link Condition} and the {@link Connector} that joins it to the preceding entry.
 *
 * <p>The connector of the <em>first</em> entry in a condition list is ignored by
 * all renderers — only entries at index ≥ 1 use their connector.
 *
 * @author EzFramework
 * @version 1.0.0
 */
public final class ConditionEntry {

    /**
     * The column (or field) name this entry targets.
     */
    private final String column;

    /**
     * The condition to apply to the column.
     */
    private final Condition condition;

    /**
     * The connector joining this entry to the previous one.
     */
    private final Connector connector;

    /**
     * Constructs a new condition entry.
     *
     * @param column    column (or field) name — must not be {@code null} or blank
     * @param condition the condition to apply — must not be {@code null}
     * @param connector how this entry joins to the previous one in the clause
     */
    public ConditionEntry(String column, Condition condition, Connector connector) {
        this.column = column;
        this.condition = condition;
        this.connector = connector;
    }

    /**
     * Returns the column name this condition targets.
     *
     * @return column name
     */
    public String getColumn() {
        return column;
    }

    /**
     * Returns the condition (operator + optional value).
     *
     * @return the condition
     */
    public Condition getCondition() {
        return condition;
    }

    /**
     * Returns the connector that joins this entry to the previous one.
     *
     * @return the connector
     */
    public Connector getConnector() {
        return connector;
    }
}
