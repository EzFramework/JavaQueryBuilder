---
title: API Reference
nav_order: 10
description: "Complete public method tables for every class and interface in JavaQueryBuilder"
---

# API Reference
{: .no_toc }

## Table of contents
{: .no_toc .text-delta }

1. TOC
{:toc}

---

## builder

### `QueryBuilder`

Main entry point for SELECT queries and static gateway to DML builders.

**Static factory methods**

| Method | Returns | Description |
|--------|---------|-------------|
| `insert()` | `InsertBuilder` | New `InsertBuilder` |
| `insertInto(String table)` | `InsertBuilder` | New `InsertBuilder` pre-set to `table` |
| `update()` | `UpdateBuilder` | New `UpdateBuilder` |
| `update(String table)` | `UpdateBuilder` | New `UpdateBuilder` pre-set to `table` |
| `delete()` | `DeleteBuilder` | New `DeleteBuilder` |
| `deleteFrom(String table)` | `DeleteBuilder` | New `DeleteBuilder` pre-set to `table` |
| `createTable()` | `CreateBuilder` | New `CreateBuilder` |
| `createTable(String table)` | `CreateBuilder` | New `CreateBuilder` pre-set to `table` |

**SELECT builder methods**

| Method | Returns | Description |
|--------|---------|-------------|
| `from(String table)` | `QueryBuilder` | Set source table |
| `select(String... columns)` | `QueryBuilder` | Add columns to SELECT clause; omit for `SELECT *` |
| `distinct()` | `QueryBuilder` | Add `DISTINCT` to SELECT |
| `whereEquals(col, val)` | `QueryBuilder` | `WHERE col = ?` (AND) |
| `orWhereEquals(col, val)` | `QueryBuilder` | `WHERE col = ?` (OR) |
| `whereNotEquals(col, val)` | `QueryBuilder` | `WHERE col != ?` (AND) |
| `whereGreaterThan(col, val)` | `QueryBuilder` | `WHERE col > ?` (AND) |
| `whereGreaterThanOrEquals(col, val)` | `QueryBuilder` | `WHERE col >= ?` (AND) |
| `whereLessThan(col, val)` | `QueryBuilder` | `WHERE col < ?` (AND) |
| `whereLessThanOrEquals(col, val)` | `QueryBuilder` | `WHERE col <= ?` (AND) |
| `whereLike(col, String val)` | `QueryBuilder` | `WHERE col LIKE ?` (AND) |
| `whereNotLike(col, String val)` | `QueryBuilder` | `WHERE col NOT LIKE ?` (AND) |
| `whereNull(col)` | `QueryBuilder` | `WHERE col IS NULL` (AND) |
| `whereNotNull(col)` | `QueryBuilder` | `WHERE col IS NOT NULL` (AND) |
| `whereExists(col)` | `QueryBuilder` | `WHERE col IS NOT NULL` (AND) |
| `whereIn(col, List<?>)` | `QueryBuilder` | `WHERE col IN (...)` (AND) |
| `whereNotIn(col, List<?>)` | `QueryBuilder` | `WHERE col NOT IN (...)` (AND) |
| `whereBetween(col, a, b)` | `QueryBuilder` | `WHERE col BETWEEN ? AND ?` (AND) |
| `whereInSubquery(col, Query)` | `QueryBuilder` | `WHERE col IN (SELECT ...)` (AND) |
| `whereEqualsSubquery(col, Query)` | `QueryBuilder` | `WHERE col = (SELECT ...)` (AND) |
| `whereExistsSubquery(Query)` | `QueryBuilder` | `WHERE EXISTS (SELECT ...)` (AND) |
| `whereNotExistsSubquery(Query)` | `QueryBuilder` | `WHERE NOT EXISTS (SELECT ...)` (AND) |
| `fromSubquery(Query, String alias)` | `QueryBuilder` | Replace `FROM` with a derived-table subquery |
| `joinSubquery(Query, String alias, String on)` | `QueryBuilder` | `INNER JOIN (SELECT ...) AS alias ON ...` |
| `selectSubquery(Query, String alias)` | `QueryBuilder` | Add `(SELECT ...) AS alias` to SELECT list |
| `groupBy(String... columns)` | `QueryBuilder` | Add `GROUP BY` columns |
| `havingRaw(String clause)` | `QueryBuilder` | Set raw `HAVING` SQL fragment |
| `orderBy(String col, boolean asc)` | `QueryBuilder` | Add `ORDER BY` column; `true` = ASC |
| `limit(int n)` | `QueryBuilder` | Set `LIMIT` |
| `offset(int n)` | `QueryBuilder` | Set `OFFSET` |
| `build()` | `Query` | Build a `Query` object (no SQL rendered yet) |
| `buildSql()` | `SqlResult` | Render SELECT using table set via `from()`, standard dialect |
| `buildSql(String table)` | `SqlResult` | Render SELECT for explicit `table`, standard dialect |
| `buildSql(String table, SqlDialect)` | `SqlResult` | Render SELECT for explicit `table` and dialect |

---

### `SelectBuilder`

Lower-level SELECT builder that produces `SqlResult` directly (no `Query` intermediary).

| Method | Returns | Description |
|--------|---------|-------------|
| `from(String table)` | `SelectBuilder` | Set source table |
| `select(String... columns)` | `SelectBuilder` | Add SELECT columns |
| `distinct()` | `SelectBuilder` | Add `DISTINCT` |
| `whereEquals(col, val)` | `SelectBuilder` | `WHERE col = ?` |
| `whereIn(col, List<?>)` | `SelectBuilder` | `WHERE col IN (...)` |
| `whereLike(col, String val)` | `SelectBuilder` | `WHERE col LIKE ?` |
| `groupBy(String... columns)` | `SelectBuilder` | Add `GROUP BY` |
| `orderBy(String col, boolean asc)` | `SelectBuilder` | Add `ORDER BY` |
| `limit(int n)` | `SelectBuilder` | Set `LIMIT` |
| `offset(int n)` | `SelectBuilder` | Set `OFFSET` |
| `build(SqlDialect)` | `SqlResult` | Render SELECT with given dialect |

---

### `InsertBuilder`

| Method | Returns | Description |
|--------|---------|-------------|
| `into(String table)` | `InsertBuilder` | Set target table |
| `value(String col, Object val)` | `InsertBuilder` | Add a column/value pair |
| `build()` | `SqlResult` | Render with standard dialect |
| `build(SqlDialect)` | `SqlResult` | Render with specified dialect |

---

### `UpdateBuilder`

| Method | Returns | Description |
|--------|---------|-------------|
| `table(String table)` | `UpdateBuilder` | Set target table |
| `set(String col, Object val)` | `UpdateBuilder` | Add a SET pair |
| `whereEquals(col, val)` | `UpdateBuilder` | `WHERE col = ?` (AND) |
| `orWhereEquals(col, val)` | `UpdateBuilder` | `WHERE col = ?` (OR) |
| `whereGreaterThanOrEquals(col, int val)` | `UpdateBuilder` | `WHERE col >= ?` (AND) |
| `build()` | `SqlResult` | Render with standard dialect |
| `build(SqlDialect)` | `SqlResult` | Render with specified dialect |

---

### `DeleteBuilder`

| Method | Returns | Description |
|--------|---------|-------------|
| `from(String table)` | `DeleteBuilder` | Set target table |
| `whereEquals(col, val)` | `DeleteBuilder` | `WHERE col = ?` (AND) |
| `whereNotEquals(col, val)` | `DeleteBuilder` | `WHERE col != ?` (AND) |
| `whereGreaterThan(col, val)` | `DeleteBuilder` | `WHERE col > ?` (AND) |
| `whereGreaterThanOrEquals(col, val)` | `DeleteBuilder` | `WHERE col >= ?` (AND) |
| `whereLessThan(col, val)` | `DeleteBuilder` | `WHERE col < ?` (AND) |
| `whereLessThanOrEquals(col, val)` | `DeleteBuilder` | `WHERE col <= ?` (AND) |
| `whereIn(col, List<?>)` | `DeleteBuilder` | `WHERE col IN (...)` (AND); throws `IllegalArgumentException` if list is null/empty |
| `whereNotIn(col, List<?>)` | `DeleteBuilder` | `WHERE col NOT IN (...)` (AND); throws `IllegalArgumentException` if list is null/empty |
| `whereBetween(col, from, to)` | `DeleteBuilder` | `WHERE col BETWEEN ? AND ?` (AND) |
| `build()` | `SqlResult` | Render with standard dialect |
| `build(SqlDialect)` | `SqlResult` | Render with specified dialect |

---

### `CreateBuilder`

| Method | Returns | Description |
|--------|---------|-------------|
| `table(String name)` | `CreateBuilder` | Set table name |
| `column(String name, String sqlType)` | `CreateBuilder` | Add column definition |
| `primaryKey(String name)` | `CreateBuilder` | Declare a primary key column |
| `ifNotExists()` | `CreateBuilder` | Add `IF NOT EXISTS` |
| `build()` | `SqlResult` | Render; throws `IllegalStateException` if table or columns are missing |
| `build(SqlDialect)` | `SqlResult` | Render with specified dialect |

---

## condition

### `Operator`

Enum of comparison operators. See [Conditions](conditions) for the full table.

| Constant | SQL |
|----------|-----|
| `EQ` | `= ?` |
| `NEQ` | `!= ?` |
| `GT` | `> ?` |
| `GTE` | `>= ?` |
| `LT` | `< ?` |
| `LTE` | `<= ?` |
| `LIKE` | `LIKE ?` |
| `NOT_LIKE` | `NOT LIKE ?` |
| `EXISTS` | `IS NOT NULL` |
| `IS_NULL` | `IS NULL` |
| `IS_NOT_NULL` | `IS NOT NULL` |
| `IN` | `IN (...)` |
| `NOT_IN` | `NOT IN (...)` |
| `BETWEEN` | `BETWEEN ? AND ?` |
| `EXISTS_SUBQUERY` | `EXISTS (SELECT ...)` |
| `NOT_EXISTS_SUBQUERY` | `NOT EXISTS (SELECT ...)` |

---

### `Condition`

| Member | Description |
|--------|-------------|
| `Condition(Operator op, Object value)` | Create a condition; `value` may be `null` |
| `getOperator()` | Returns the `Operator` |
| `getValue()` | Returns the comparison value (`null`, scalar, `List<?>`, or `Query`) |
| `matches(Map<String,Object> map, String key)` | Evaluate against an in-memory attribute map |

---

### `ConditionEntry`

| Member | Description |
|--------|-------------|
| `ConditionEntry(String col, Condition cond, Connector connector)` | Create a condition entry |
| `getColumn()` | Column name (`null` for EXISTS-subquery conditions) |
| `getCondition()` | The wrapped `Condition` |
| `getConnector()` | `AND` or `OR` |

---

### `Connector`

| Constant | SQL keyword |
|----------|-------------|
| `AND` | `AND` |
| `OR` | `OR` |

---

## query

### `Query`

Immutable data holder produced by `QueryBuilder.build()`. All fields have
getters and setters; setters are used exclusively by the builders.

| Getter | Type | Description |
|--------|------|-------------|
| `getTable()` | `String` | Source table name |
| `getSelectColumns()` | `List<String>` | Columns in SELECT clause; empty = `SELECT *` |
| `isDistinct()` | `boolean` | Whether `DISTINCT` is active |
| `getConditions()` | `List<ConditionEntry>` | WHERE conditions |
| `getGroupBy()` | `List<String>` | GROUP BY columns |
| `getHavingRaw()` | `String` | Raw HAVING fragment |
| `getOrderBy()` | `List<String>` | ORDER BY columns |
| `getOrderByAsc()` | `List<Boolean>` | True = ASC per ORDER BY entry |
| `getLimit()` | `Integer` | LIMIT value; `0` or negative = no limit |
| `getOffset()` | `Integer` | OFFSET value |
| `getFromSubquery()` | `Query` | FROM-derived subquery; `null` for plain table |
| `getFromAlias()` | `String` | Alias for FROM subquery |
| `getJoins()` | `List<JoinClause>` | JOIN clauses |
| `getSelectSubqueries()` | `List<ScalarSelectItem>` | Scalar SELECT subquery items |

---

### `JoinClause`

| Member | Description |
|--------|-------------|
| `JoinClause(Type, String table, String on)` | Plain-table join |
| `JoinClause(Type, Query subquery, String alias, String on)` | Subquery (derived-table) join |
| `getType()` | `JoinClause.Type` — `INNER`, `LEFT`, `RIGHT`, or `CROSS` |
| `getTable()` | Table name for plain-table join; `null` for subquery join |
| `getSubquery()` | Subquery for derived-table join; `null` for plain-table join |
| `getAlias()` | Alias for derived-table join |
| `getOnCondition()` | Raw SQL `ON` fragment |

---

### `ScalarSelectItem`

| Member | Description |
|--------|-------------|
| `ScalarSelectItem(Query subquery, String alias)` | Create a scalar SELECT item |
| `getSubquery()` | The subquery to embed |
| `getAlias()` | Column alias in SELECT clause |

---

### `QueryableStorage`

Functional interface for in-memory filtering.

```java
@FunctionalInterface
public interface QueryableStorage {
    List<String> query(Query q) throws Exception;
}
```

---

## sql

### `SqlDialect`

| Member | Description |
|--------|-------------|
| `STANDARD` | ANSI SQL — no identifier quoting |
| `MYSQL` | MySQL — back-tick quoting; DELETE LIMIT supported |
| `SQLITE` | SQLite — double-quote quoting; DELETE LIMIT supported |
| `render(Query)` | Render a SELECT query to `SqlResult` |
| `renderDelete(Query)` | Render a DELETE query to `SqlResult` |

---

### `SqlResult`

| Method | Returns | Description |
|--------|---------|-------------|
| `getSql()` | `String` | Rendered SQL with `?` placeholders |
| `getParameters()` | `List<Object>` | Bind parameters in placeholder order |

---

## exception

### `QueryBuilderException`

| Constructor | Description |
|-------------|-------------|
| `QueryBuilderException()` | No-message default |
| `QueryBuilderException(String message)` | Simple message |
| `QueryBuilderException(String message, Throwable cause)` | Wraps another exception |
| `QueryBuilderException(Throwable cause)` | Re-throws |

---

### `QueryException`

| Constructor | Description |
|-------------|-------------|
| `QueryException()` | No-message default |
| `QueryException(String message)` | Simple message |
| `QueryException(String message, Throwable cause)` | Wraps another exception |
| `QueryException(Throwable cause)` | Re-throws |

---

### `QueryRenderException`

| Constructor | Description |
|-------------|-------------|
| `QueryRenderException()` | No-message default |
| `QueryRenderException(String message)` | Simple message |
| `QueryRenderException(String message, Throwable cause)` | Wraps another exception |
| `QueryRenderException(Throwable cause)` | Re-throws |
