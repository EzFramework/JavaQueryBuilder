package com.github.ezframework.javaquerybuilder.query.builder;

import java.util.ArrayList;
import java.util.List;

import com.github.ezframework.javaquerybuilder.query.Query;
import com.github.ezframework.javaquerybuilder.query.QueryBuilderDefaults;
import com.github.ezframework.javaquerybuilder.query.condition.Condition;
import com.github.ezframework.javaquerybuilder.query.condition.ConditionEntry;
import com.github.ezframework.javaquerybuilder.query.condition.Connector;
import com.github.ezframework.javaquerybuilder.query.condition.Operator;
import com.github.ezframework.javaquerybuilder.query.sql.SqlDialect;
import com.github.ezframework.javaquerybuilder.query.sql.SqlResult;

/**
 * Builder for SQL DELETE statements.
 *
 * @author EzFramework
 * @version 1.0.0
 */
public class DeleteBuilder {

    /** The table to delete from. */
    private String table;

    /** The WHERE conditions. */
    private final List<ConditionEntry> conditions = new ArrayList<>();

    /** The RETURNING columns (PostgreSQL only). */
    private final List<String> returningColumns = new ArrayList<>();

    /** The defaults configuration for this builder instance. */
    private QueryBuilderDefaults queryBuilderDefaults = QueryBuilderDefaults.global();

    /**
     * Overrides the defaults configuration for this builder instance.
     *
     * @param defaults the defaults to apply; must not be {@code null}
     * @return this builder instance for chaining
     * @throws NullPointerException if {@code defaults} is {@code null}
     */
    public DeleteBuilder withDefaults(final QueryBuilderDefaults defaults) {
        if (defaults == null) {
            throw new NullPointerException("QueryBuilderDefaults must not be null");
        }
        this.queryBuilderDefaults = defaults;
        return this;
    }

    /**
     * Sets the table to delete from.
     * @param table the table name
     * @return this builder
     */
    public DeleteBuilder from(String table) {
        this.table = table;
        return this;
    }

    /**
     * Adds an equality WHERE condition.
     * @param column the column name
     * @param value the value to compare
     * @return this builder
     */
    public DeleteBuilder whereEquals(String column, Object value) {

        conditions.add(new ConditionEntry(column, new Condition(Operator.EQ, value), Connector.AND));

        return this;
    }

    /**
     * Adds a less-than WHERE condition.
     * @param column the column name
     * @param value the value to compare
     * @return this builder
     */
    public DeleteBuilder whereLessThan(String column, Object value) {

        conditions.add(new ConditionEntry(column, new Condition(Operator.LT, value), Connector.AND));

        return this;
    }

    /**
     * Adds an IN WHERE condition.
     * @param column the column name
     * @param values the collection of values for the IN clause
     * @return this builder
     * @throws IllegalArgumentException if values is null or empty
     */
    public DeleteBuilder whereIn(String column, List<?> values) {
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("IN value list must not be null or empty");
        }
        conditions.add(new ConditionEntry(column, new Condition(Operator.IN, values), Connector.AND));
        return this;
    }

    /**
     * Adds a NOT IN WHERE condition.
     * @param column the column name
     * @param values the collection of values for the NOT IN clause
     * @return this builder
     * @throws IllegalArgumentException if values is null or empty
     */
    public DeleteBuilder whereNotIn(String column, List<?> values) {
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("NOT IN value list must not be null or empty");
        }
        conditions.add(new ConditionEntry(column, new Condition(Operator.NOT_IN, values), Connector.AND));
        return this;
    }

    /**
     * Adds a BETWEEN WHERE condition.
     * @param column the column name
     * @param from the lower bound value (inclusive)
     * @param to the upper bound value (inclusive)
     * @return this builder
     */
    public DeleteBuilder whereBetween(String column, Object from, Object to) {
        conditions.add(new ConditionEntry(column, new Condition(Operator.BETWEEN, List.of(from, to)), Connector.AND));
        return this;
    }

    /**
     * Adds a greater-than WHERE condition.
     * @param column the column name
     * @param value the value to compare
     * @return this builder
     */
    public DeleteBuilder whereGreaterThan(String column, Object value) {
        conditions.add(new ConditionEntry(column, new Condition(Operator.GT, value), Connector.AND));
        return this;
    }

    /**
     * Adds a greater-than-or-equals WHERE condition.
     * @param column the column name
     * @param value the value to compare
     * @return this builder
     */
    public DeleteBuilder whereGreaterThanOrEquals(String column, Object value) {
        conditions.add(new ConditionEntry(column, new Condition(Operator.GTE, value), Connector.AND));
        return this;
    }

    /**
     * Adds a less-than-or-equals WHERE condition.
     * @param column the column name
     * @param value the value to compare
     * @return this builder
     */
    public DeleteBuilder whereLessThanOrEquals(String column, Object value) {
        conditions.add(new ConditionEntry(column, new Condition(Operator.LTE, value), Connector.AND));
        return this;
    }

    /**
     * Adds a not-equals WHERE condition.
     * @param column the column name
     * @param value the value to compare
     * @return this builder
     */
    public DeleteBuilder whereNotEquals(String column, Object value) {
        conditions.add(new ConditionEntry(column, new Condition(Operator.NEQ, value), Connector.AND));
        return this;
    }

    /**
     * Adds a {@code WHERE col IN (SELECT ...)} condition using a subquery.
     *
     * @param column   the column to test
     * @param subquery the subquery whose result set provides the IN values
     * @return this builder
     */
    public DeleteBuilder whereInSubquery(String column, Query subquery) {
        conditions.add(new ConditionEntry(column, new Condition(Operator.IN, subquery), Connector.AND));
        return this;
    }

    /**
     * Adds a {@code WHERE EXISTS (SELECT ...)} condition.
     *
     * @param subquery the subquery to test for existence
     * @return this builder
     */
    public DeleteBuilder whereExistsSubquery(Query subquery) {
        conditions.add(new ConditionEntry(null,
            new Condition(Operator.EXISTS_SUBQUERY, subquery), Connector.AND));
        return this;
    }

    /**
     * Specifies the columns to include in a {@code RETURNING} clause (PostgreSQL only).
     *
     * <p>This is only rendered when the active dialect supports {@code RETURNING}
     * (i.e., {@link com.github.ezframework.javaquerybuilder.query.sql.SqlDialect#POSTGRESQL}).
     *
     * @param columns one or more column names; must not be {@code null} or empty
     * @return this builder instance for chaining
     */
    public DeleteBuilder returning(final String... columns) {
        returningColumns.addAll(java.util.Arrays.asList(columns));
        return this;
    }

    /**
     * Builds the SQL DELETE statement using standard SQL.
     *
     * @return the SQL result
     */
    public SqlResult build() {
        return build(null);
    }

    /**
     * Builds the SQL DELETE statement using the given dialect.
     * When {@code dialect} is {@code null}, the dialect from the configured
     * {@link QueryBuilderDefaults} is used.
     *
     * @param dialect the SQL dialect (may be {@code null} to use the configured default)
     * @return the SQL result
     */
    public SqlResult build(final SqlDialect dialect) {
        final Query q = toQuery();
        return (dialect != null ? dialect : queryBuilderDefaults.getDialect()).renderDelete(q);
    }

    private Query toQuery() {
        final Query q = new Query();
        q.setTable(table);
        q.setConditions(new ArrayList<>(conditions));
        q.setLimit(-1);
        q.setReturningColumns(new ArrayList<>(returningColumns));
        return q;
    }
}
