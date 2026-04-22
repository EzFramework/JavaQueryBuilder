---
title: DELETE
parent: Queries
nav_order: 4
permalink: /queries/delete/
description: "Building DELETE statements with DeleteBuilder"
---

# DELETE
{: .no_toc }

## Table of contents
{: .no_toc .text-delta }

1. TOC
{:toc}

---

## Overview

`DeleteBuilder` builds parameterized `DELETE FROM … WHERE` statements:

```java
import com.github.ezframework.javaquerybuilder.query.builder.QueryBuilder;
import com.github.ezframework.javaquerybuilder.query.sql.SqlResult;

SqlResult result = QueryBuilder.deleteFrom("sessions")
    .whereEquals("user_id", 99)
    .build();

// → DELETE FROM sessions WHERE user_id = ?
// Parameters: [99]
```

---

## Multiple conditions

```java
SqlResult result = QueryBuilder.deleteFrom("logs")
    .whereLessThan("created_at", "2025-01-01")
    .whereEquals("level", "debug")
    .build();
// → DELETE FROM logs WHERE created_at < ? AND level = ?
```

---

## IN / NOT IN

```java
SqlResult result = QueryBuilder.deleteFrom("users")
    .whereIn("status", List.of("banned", "deleted"))
    .build();
// → DELETE FROM users WHERE status IN (?, ?)

SqlResult result2 = QueryBuilder.deleteFrom("products")
    .whereNotIn("category", List.of("archive", "draft"))
    .build();
// → DELETE FROM products WHERE category NOT IN (?, ?)
```

---

## BETWEEN

```java
SqlResult result = QueryBuilder.deleteFrom("events")
    .whereBetween("score", 0, 10)
    .build();
// → DELETE FROM events WHERE score BETWEEN ? AND ?
```

---

## Dialect-aware delete (LIMIT support)

MySQL and SQLite support a `LIMIT` clause on `DELETE`. Pass a `Query` built with
a `limit()` call to `renderDelete` on the appropriate dialect:

```java
Query q = new QueryBuilder()
    .from("logs")
    .whereLessThan("age", 30)
    .limit(100)
    .build();

// MySQL — LIMIT honored, back-tick quoting applied
SqlResult mysql = SqlDialect.MYSQL.renderDelete(q);
// → DELETE FROM `logs` WHERE `age` < ? LIMIT 100

// SQLite — LIMIT honored, double-quote quoting applied
SqlResult sqlite = SqlDialect.SQLITE.renderDelete(q);
// → DELETE FROM "logs" WHERE "age" < ? LIMIT 100

// STANDARD — LIMIT dropped
SqlResult std = SqlDialect.STANDARD.renderDelete(q);
// → DELETE FROM logs WHERE age < ?
```

Alternatively, use `DeleteBuilder.build(SqlDialect)` which also calls
`renderDelete` internally:

```java
SqlResult result = QueryBuilder.deleteFrom("logs")
    .whereLessThan("age", 30)
    .build(SqlDialect.MYSQL);
// → DELETE FROM `logs` WHERE `age` < ?
// (limit requires a Query built via QueryBuilder.build().limit())
```

---

## RETURNING (PostgreSQL)

`RETURNING` on DELETE is rendered by the dialect — it is appended only when
`SqlDialect.POSTGRESQL` (or a dialect that overrides `supportsReturning()`) is
used.

```java
SqlResult result = QueryBuilder.deleteFrom("users")
    .whereEquals("id", 99)
    .returning("id", "email")
    .build(SqlDialect.POSTGRESQL);

// → DELETE FROM "users" WHERE "id" = ? RETURNING id, email
// Parameters: [99]
```

---

## Method reference

| Method | Returns | Description |
|--------|---------|-------------|
| `from(String table)` | `DeleteBuilder` | Set target table |
| `whereEquals(col, val)` | `DeleteBuilder` | `WHERE col = ?` (AND) |
| `whereNotEquals(col, val)` | `DeleteBuilder` | `WHERE col != ?` (AND) |
| `whereGreaterThan(col, val)` | `DeleteBuilder` | `WHERE col > ?` (AND) |
| `whereGreaterThanOrEquals(col, val)` | `DeleteBuilder` | `WHERE col >= ?` (AND) |
| `whereLessThan(col, val)` | `DeleteBuilder` | `WHERE col < ?` (AND) |
| `whereLessThanOrEquals(col, val)` | `DeleteBuilder` | `WHERE col <= ?` (AND) |
| `whereIn(col, List<?>)` | `DeleteBuilder` | `WHERE col IN (...)` (AND) |
| `whereNotIn(col, List<?>)` | `DeleteBuilder` | `WHERE col NOT IN (...)` (AND) |
| `whereBetween(col, from, to)` | `DeleteBuilder` | `WHERE col BETWEEN ? AND ?` (AND) |
| `returning(String... cols)` | `DeleteBuilder` | Append `RETURNING col1, col2, ...` (PostgreSQL only; use with `SqlDialect.POSTGRESQL`) |
| `build()` | `SqlResult` | Render with standard dialect |
| `build(SqlDialect dialect)` | `SqlResult` | Render with specified dialect |
