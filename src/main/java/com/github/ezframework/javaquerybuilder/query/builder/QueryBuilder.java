package com.github.ezframework.javaquerybuilder.query.builder;

import com.github.ezframework.javaquerybuilder.query.builder.InsertBuilder;
import com.github.ezframework.javaquerybuilder.query.builder.UpdateBuilder;
import com.github.ezframework.javaquerybuilder.query.builder.DeleteBuilder;
import com.github.ezframework.javaquerybuilder.query.builder.CreateBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.ezframework.javaquerybuilder.query.Query;
import com.github.ezframework.javaquerybuilder.query.condition.Condition;
import com.github.ezframework.javaquerybuilder.query.condition.ConditionEntry;
import com.github.ezframework.javaquerybuilder.query.condition.Connector;
import com.github.ezframework.javaquerybuilder.query.condition.Operator;
import com.github.ezframework.javaquerybuilder.query.sql.SqlDialect;
import com.github.ezframework.javaquerybuilder.query.sql.SqlResult;

public class QueryBuilder {

    /** The columns to select. */
    private final List<String> selectColumns = new ArrayList<>();

    /** The WHERE conditions. */
    private final List<ConditionEntry> conditions = new ArrayList<>();

    /** The GROUP BY columns. */
    private final List<String> groupByColumns = new ArrayList<>();

    /** The ORDER BY columns. */
    private final List<String> orderByColumns = new ArrayList<>();

    /** The ORDER BY directions (true for ASC, false for DESC). */
    private final List<Boolean> orderByAsc = new ArrayList<>();

    /** Whether the query is DISTINCT. */
    private boolean isDistinct = false;

    /** The LIMIT value. */
    private int limit = -1;

    /** The OFFSET value. */
    private int offset = -1;

    /** The HAVING clause (raw SQL). */
    private String havingRaw = null;

    /** The source table for SELECT. */
    private String table = null;

    /**
     * Creates a new InsertBuilder.
     * @return a new InsertBuilder
     */
    public static InsertBuilder insert() {
        return new InsertBuilder();
    }

    /**
     * Creates a new UpdateBuilder.
     * @return a new UpdateBuilder
     */
    public static UpdateBuilder update() {
        return new UpdateBuilder();
    }

    /**
     * Creates a new DeleteBuilder.
     * @return a new DeleteBuilder
     */
    public static DeleteBuilder delete() {
        return new DeleteBuilder();
    }

    /**
     * Creates a new CreateBuilder for CREATE TABLE statements.
     * @return a new CreateBuilder
     */
    public static CreateBuilder createTable() {
        return new CreateBuilder();
    }

    /**
     * Sets the source table for the SELECT query.
     *
     * @param fromTable the table name
     * @return this builder instance for chaining
     */
    public QueryBuilder from(String fromTable) {
        this.table = fromTable;
        return this;
    }

    public QueryBuilder select(String... columns) {
        selectColumns.addAll(Arrays.asList(columns));
        return this;
    }

    public QueryBuilder distinct() {
        isDistinct = true;
        return this;
    }

    public QueryBuilder whereEquals(String column, Object value) {
        conditions.add(
            new ConditionEntry(
                column,
                new Condition(Operator.EQ, value),
                conditions.isEmpty() ? Connector.AND : Connector.AND
            )
        );
        return this;
    }

    public QueryBuilder orWhereEquals(String column, Object value) {
        conditions.add(
            new ConditionEntry(
                column,
                new Condition(Operator.EQ, value),
                Connector.OR
            )
        );
        return this;
    }

    public QueryBuilder whereLike(String column, String value) {
        conditions.add(
            new ConditionEntry(
                column,
                new Condition(Operator.LIKE, value),
                conditions.isEmpty() ? Connector.AND : Connector.AND
            )
        );
        return this;
    }

    public QueryBuilder whereNotLike(String column, String value) {
        conditions.add(
            new ConditionEntry(
                column,
                new Condition(Operator.NOT_LIKE, value),
                conditions.isEmpty() ? Connector.AND : Connector.AND
            )
        );
        return this;
    }

    public QueryBuilder whereExists(String column) {
        conditions.add(
            new ConditionEntry(
                column,
                new Condition(Operator.EXISTS, null),
                conditions.isEmpty() ? Connector.AND : Connector.AND
            )
        );
        return this;
    }

    public QueryBuilder whereNull(String column) {
        conditions.add(
            new ConditionEntry(
                column,
                new Condition(Operator.IS_NULL, null),
                conditions.isEmpty() ? Connector.AND : Connector.AND
            )
        );
        return this;
    }

    public QueryBuilder whereNotNull(String column) {
        conditions.add(
            new ConditionEntry(
                column,
                new Condition(Operator.IS_NOT_NULL, null),
                conditions.isEmpty() ? Connector.AND : Connector.AND
            )
        );
        return this;
    }

    public QueryBuilder whereIn(String column, List<?> values) {
        conditions.add(
            new ConditionEntry(
                column,
                new Condition(Operator.IN, values),
                conditions.isEmpty() ? Connector.AND : Connector.AND
            )
        );
        return this;
    }

    public QueryBuilder whereNotIn(String column, List<?> values) {
        conditions.add(
            new ConditionEntry(
                column,
                new Condition(Operator.NOT_IN, values),
                conditions.isEmpty() ? Connector.AND : Connector.AND
            )
        );
        return this;
    }

    public QueryBuilder whereBetween(String column, Object a, Object b) {
        conditions.add(
            new ConditionEntry(
                column,
                new Condition(Operator.BETWEEN, Arrays.asList(a, b)),
                conditions.isEmpty() ? Connector.AND : Connector.AND
            )
        );
        return this;
    }

    public QueryBuilder whereGreaterThan(String column, Object value) {
        conditions.add(
            new ConditionEntry(
                column,
                new Condition(Operator.GT, value),
                conditions.isEmpty() ? Connector.AND : Connector.AND
            )
        );
        return this;
    }

    public QueryBuilder whereGreaterThanOrEquals(String column, Object value) {
        conditions.add(
            new ConditionEntry(
                column,
                new Condition(Operator.GTE, value),
                conditions.isEmpty() ? Connector.AND : Connector.AND
            )
        );
        return this;
    }

    public QueryBuilder whereLessThanOrEquals(String column, Object value) {
        conditions.add(
            new ConditionEntry(
                column,
                new Condition(Operator.LTE, value),
                conditions.isEmpty() ? Connector.AND : Connector.AND
            )
        );
        return this;
    }

    public QueryBuilder groupBy(String... columns) {
        groupByColumns.addAll(Arrays.asList(columns));
        return this;
    }

    public QueryBuilder orderBy(String column, boolean asc) {
        orderByColumns.add(column);
        orderByAsc.add(asc);
        return this;
    }

    public QueryBuilder limit(int limit) {
        this.limit = limit;
        return this;
    }

    public QueryBuilder offset(int offset) {
        this.offset = offset;
        return this;
    }

    public QueryBuilder havingRaw(String clause) {
        this.havingRaw = clause;
        return this;
    }

    public Query build() {
        final Query q = new Query();
        q.setLimit(limit);
        q.setOffset(offset);
        q.setConditions(new ArrayList<>(conditions));
        q.setGroupBy(new ArrayList<>(groupByColumns));
        q.setOrderBy(new ArrayList<>(orderByColumns));
        q.setOrderByAsc(new ArrayList<>(orderByAsc));
        q.setTable(table);
        q.setSelectColumns(new ArrayList<>(selectColumns));
        q.setDistinct(isDistinct);
        q.setHavingRaw(havingRaw);
        return q;
    }

    /**
     * Builds the SQL SELECT statement using the table set via {@link #from(String)}.
     *
     * @return the SQL result
     * @throws IllegalStateException if no table was set via {@link #from(String)}
     */
    public SqlResult buildSql() {
        if (table == null) {
            throw new IllegalStateException("No table specified. Call from(table) before buildSql().");
        }
        return buildSql(table, null);
    }

    /**
     * Builds the SQL SELECT statement for the given table using the standard dialect.
     *
     * @param table the table to select from
     * @return the SQL result
     */
    public SqlResult buildSql(String table) {
        return buildSql(table, null);
    }

    /**
     * Builds the SQL SELECT statement for the given table using the specified dialect.
     *
     * @param table   the table to select from
     * @param dialect the SQL dialect to use; if {@code null}, {@link SqlDialect#STANDARD} is used
     * @return the SQL result
     */
    public SqlResult buildSql(String table, SqlDialect dialect) {
        final Query q = build();
        q.setTable(table);
        return (dialect != null ? dialect : SqlDialect.STANDARD).render(q);
    }
}
