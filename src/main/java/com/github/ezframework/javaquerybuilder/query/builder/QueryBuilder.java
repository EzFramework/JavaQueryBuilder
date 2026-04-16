package com.github.ezframework.javaquerybuilder.query.builder;

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

/**
 * Fluent builder for SQL SELECT queries and gateway to all other DML/DDL builders.
 *
 * <p>Use the static factory methods to obtain a builder for any SQL statement type:
 * <pre>
 *   // SELECT
 *   QueryBuilder.from("users").whereEquals("id", 1).buildSql();
 *
 *   // INSERT
 *   QueryBuilder.insertInto("users").value("name", "Alice").build();
 *
 *   // UPDATE
 *   QueryBuilder.update("users").set("name", "Bob").whereEquals("id", 1).build();
 *
 *   // DELETE
 *   QueryBuilder.deleteFrom("users").whereEquals("id", 1).build();
 *
 *   // CREATE TABLE
 *   QueryBuilder.createTable("users").column("id", "INT").primaryKey("id").build();
 * </pre>
 *
 * @author EzFramework
 * @version 1.0.0
 */
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
     * Returns a new {@link InsertBuilder}.
     *
     * @return a new InsertBuilder
     */
    public static InsertBuilder insert() {
        return new InsertBuilder();
    }

    /**
     * Returns a new {@link InsertBuilder} pre-configured for the given table.
     *
     * @param table the table to insert into
     * @return a new InsertBuilder targeting {@code table}
     */
    public static InsertBuilder insertInto(String table) {
        return new InsertBuilder().into(table);
    }

    /**
     * Returns a new {@link UpdateBuilder}.
     *
     * @return a new UpdateBuilder
     */
    public static UpdateBuilder update() {
        return new UpdateBuilder();
    }

    /**
     * Returns a new {@link UpdateBuilder} pre-configured for the given table.
     *
     * @param table the table to update
     * @return a new UpdateBuilder targeting {@code table}
     */
    public static UpdateBuilder update(String table) {
        return new UpdateBuilder().table(table);
    }

    /**
     * Returns a new {@link DeleteBuilder}.
     *
     * @return a new DeleteBuilder
     */
    public static DeleteBuilder delete() {
        return new DeleteBuilder();
    }

    /**
     * Returns a new {@link DeleteBuilder} pre-configured for the given table.
     *
     * @param table the table to delete from
     * @return a new DeleteBuilder targeting {@code table}
     */
    public static DeleteBuilder deleteFrom(String table) {
        return new DeleteBuilder().from(table);
    }

    /**
     * Returns a new {@link CreateBuilder} for DDL CREATE TABLE statements.
     *
     * @return a new CreateBuilder
     */
    public static CreateBuilder createTable() {
        return new CreateBuilder();
    }

    /**
     * Returns a new {@link CreateBuilder} pre-configured for the given table name.
     *
     * @param table the table to create
     * @return a new CreateBuilder targeting {@code table}
     */
    public static CreateBuilder createTable(String table) {
        return new CreateBuilder().table(table);
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

    /**
     * Specifies the columns to include in the SELECT clause.
     *
     * @param columns one or more column names to select
     * @return this builder instance for chaining
     */
    public QueryBuilder select(String... columns) {
        selectColumns.addAll(Arrays.asList(columns));
        return this;
    }

    /**
     * Adds {@code DISTINCT} to the SELECT clause.
     *
     * @return this builder instance for chaining
     */
    public QueryBuilder distinct() {
        isDistinct = true;
        return this;
    }

    /**
     * Adds an equality ({@code =}) WHERE condition joined with AND.
     *
     * @param column the column name
     * @param value  the value to compare against
     * @return this builder instance for chaining
     */
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

    /**
     * Adds an equality ({@code =}) WHERE condition joined with OR.
     *
     * @param column the column name
     * @param value  the value to compare against
     * @return this builder instance for chaining
     */
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

    /**
     * Adds a {@code LIKE} WHERE condition joined with AND.
     *
     * @param column the column name
     * @param value  the LIKE pattern
     * @return this builder instance for chaining
     */
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

    /**
     * Adds a {@code NOT LIKE} WHERE condition joined with AND.
     *
     * @param column the column name
     * @param value  the LIKE pattern
     * @return this builder instance for chaining
     */
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

    /**
     * Adds an {@code EXISTS} WHERE condition joined with AND.
     *
     * @param column the column name
     * @return this builder instance for chaining
     */
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

    /**
     * Adds an {@code IS NULL} WHERE condition joined with AND.
     *
     * @param column the column name
     * @return this builder instance for chaining
     */
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

    /**
     * Adds an {@code IS NOT NULL} WHERE condition joined with AND.
     *
     * @param column the column name
     * @return this builder instance for chaining
     */
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

    /**
     * Adds an {@code IN} WHERE condition joined with AND.
     *
     * @param column the column name
     * @param values the list of values
     * @return this builder instance for chaining
     */
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

    /**
     * Adds a {@code NOT IN} WHERE condition joined with AND.
     *
     * @param column the column name
     * @param values the list of values
     * @return this builder instance for chaining
     */
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

    /**
     * Adds a {@code BETWEEN} WHERE condition joined with AND.
     *
     * @param column the column name
     * @param a      the lower bound (inclusive)
     * @param b      the upper bound (inclusive)
     * @return this builder instance for chaining
     */
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

    /**
     * Adds a greater-than ({@code >}) WHERE condition joined with AND.
     *
     * @param column the column name
     * @param value  the value to compare against
     * @return this builder instance for chaining
     */
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

    /**
     * Adds a greater-than-or-equal ({@code >=}) WHERE condition joined with AND.
     *
     * @param column the column name
     * @param value  the value to compare against
     * @return this builder instance for chaining
     */
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

    /**
     * Adds a less-than-or-equal ({@code <=}) WHERE condition joined with AND.
     *
     * @param column the column name
     * @param value  the value to compare against
     * @return this builder instance for chaining
     */
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

    /**
     * Adds one or more columns to the GROUP BY clause.
     *
     * @param columns the column names
     * @return this builder instance for chaining
     */
    public QueryBuilder groupBy(String... columns) {
        groupByColumns.addAll(Arrays.asList(columns));
        return this;
    }

    /**
     * Adds a column to the ORDER BY clause.
     *
     * @param column the column name
     * @param asc    {@code true} for ascending, {@code false} for descending
     * @return this builder instance for chaining
     */
    public QueryBuilder orderBy(String column, boolean asc) {
        orderByColumns.add(column);
        orderByAsc.add(asc);
        return this;
    }

    /**
     * Sets the row limit.
     *
     * @param limit the maximum number of rows to return
     * @return this builder instance for chaining
     */
    public QueryBuilder limit(int limit) {
        this.limit = limit;
        return this;
    }

    /**
     * Sets the row offset.
     *
     * @param offset the number of rows to skip
     * @return this builder instance for chaining
     */
    public QueryBuilder offset(int offset) {
        this.offset = offset;
        return this;
    }

    /**
     * Sets a raw HAVING clause.
     *
     * @param clause the raw SQL HAVING clause (e.g. {@code "COUNT(*) > 5"})
     * @return this builder instance for chaining
     */
    public QueryBuilder havingRaw(String clause) {
        this.havingRaw = clause;
        return this;
    }

    /**
     * Builds a {@link Query} object from the current builder state.
     *
     * @return an immutable {@link Query} representing the SELECT statement
     */
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
