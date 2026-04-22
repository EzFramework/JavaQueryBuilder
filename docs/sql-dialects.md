---
title: SQL Dialects
nav_order: 7
description: "SqlDialect, SqlResult, AbstractSqlDialect, and the dialect matrix"
---

# SQL Dialects
{: .no_toc }

## Table of contents
{: .no_toc .text-delta }

1. TOC
{:toc}

---

## Overview

`SqlDialect` is a strategy interface that converts a `Query` object into a
parameterized `SqlResult`. Three built-in dialects are provided as constants
on the interface:

| Constant | Identifier quoting | DELETE LIMIT | ILIKE | RETURNING |
|----------|--------------------|--------------| ------|----------|
| `SqlDialect.STANDARD` | None (ANSI) | Not supported | No | No |
| `SqlDialect.MYSQL` | Back-tick `` ` `` | Supported | No | No |
| `SqlDialect.SQLITE` | Double-quote `"` | Supported | No | No |
| `SqlDialect.POSTGRESQL` | Double-quote `"` | Not supported | Yes | Yes (DELETE) |

---

## Using a dialect

Pass a dialect to `buildSql()` on the builder:

```java
// Standard ANSI
SqlResult r1 = new QueryBuilder()
    .from("users")
    .whereEquals("id", 1)
    .buildSql();
// → SELECT * FROM users WHERE id = ?

// MySQL: back-tick quoted identifiers
SqlResult r2 = new QueryBuilder()
    .from("users")
    .whereEquals("id", 1)
    .buildSql(SqlDialect.MYSQL);
// → SELECT * FROM `users` WHERE `id` = ?

// SQLite: double-quoted identifiers
SqlResult r3 = new QueryBuilder()
    .from("users")
    .whereEquals("id", 1)
    .buildSql(SqlDialect.SQLITE);
// → SELECT * FROM "users" WHERE "id" = ?

// PostgreSQL: double-quoted identifiers + ILIKE support
SqlResult r4 = new QueryBuilder()
    .from("users")
    .whereILike("email", "alice")
    .buildSql(SqlDialect.POSTGRESQL);
// → SELECT * FROM "users" WHERE "email" ILIKE ?
// Parameters: ["%alice%"]
```

---

## SqlResult

`SqlResult` is returned by every `build()` / `buildSql()` call. It carries the
rendered SQL string and the ordered bind-parameter list.

| Method | Returns | Description |
|--------|---------|-------------|
| `getSql()` | `String` | The rendered SQL with `?` placeholders |
| `getParameters()` | `List<Object>` | Bind parameters in the order they appear in the SQL |

```java
SqlResult result = new QueryBuilder()
    .from("products")
    .whereEquals("active", true)
    .whereGreaterThan("stock", 0)
    .buildSql(SqlDialect.MYSQL);

String sql        = result.getSql();
// → SELECT * FROM `products` WHERE `active` = ? AND `stock` > ?

List<Object> params = result.getParameters();
// → [true, 0]
```

---

## Rendering DELETE statements

Use `renderDelete(Query)` on a dialect instance to produce `DELETE FROM ...`
statements. This respects the `LIMIT` clause on dialects that support it and
the `RETURNING` clause on PostgreSQL.

```java
Query q = new QueryBuilder()
    .from("sessions")
    .whereEquals("expired", true)
    .limit(500)
    .build();

// Standard: LIMIT ignored
SqlResult std = SqlDialect.STANDARD.renderDelete(q);
// → DELETE FROM sessions WHERE expired = ?

// MySQL: LIMIT honored
SqlResult my = SqlDialect.MYSQL.renderDelete(q);
// → DELETE FROM `sessions` WHERE `expired` = ? LIMIT 500

// SQLite: LIMIT honored
SqlResult sq = SqlDialect.SQLITE.renderDelete(q);
// → DELETE FROM "sessions" WHERE "expired" = ? LIMIT 500
```

For PostgreSQL `RETURNING`, use `DeleteBuilder.returning()` — see [DML Builders](dml-builders#returning-clause-postgresql).

---

## Dialect matrix

The same `Query` produces different SQL across dialects due to identifier quoting:

| Feature | STANDARD | MYSQL | SQLITE | POSTGRESQL |
|---------|----------|-------|--------|------------|
| Table quoting | `users` | `` `users` `` | `"users"` | `"users"` |
| Column quoting | `id` | `` `id` `` | `"id"` | `"id"` |
| DELETE LIMIT | No | Yes | Yes | No |
| ILIKE / NOT ILIKE | No | No | No | Yes |
| RETURNING on DELETE | No | No | No | Yes |
| Parameter syntax | `?` | `?` | `?` | `?` |

---

## AbstractSqlDialect

`AbstractSqlDialect` implements the shared rendering logic for SELECT and DELETE
queries. It is the base class for `MySqlDialect`, `SqliteDialect`, and
`PostgreSqlDialect`.

**Subquery parameter ordering**: parameters are collected depth-first in this
order:

1. SELECT-list scalar subquery parameters (left to right)
2. FROM subquery parameters
3. JOIN subquery parameters (left to right)
4. WHERE condition subquery parameters (top to bottom)

To create a fully custom dialect, extend `AbstractSqlDialect` and override any
combination of `quoteIdentifier`, `supportsDeleteLimit`, `supportsReturning`,
and `appendConditionFragment`.

---

## SqlDialect interface

| Member | Description |
|--------|-------------|
| `SqlDialect.STANDARD` | ANSI SQL constant instance |
| `SqlDialect.MYSQL` | MySQL dialect constant instance |
| `SqlDialect.SQLITE` | SQLite dialect constant instance |
| `SqlDialect.POSTGRESQL` | PostgreSQL dialect constant instance |
| `render(Query)` | Render a `SELECT` query to `SqlResult` |
| `renderDelete(Query)` | Render a `DELETE` query to `SqlResult`; observes `LIMIT` and `RETURNING` on supporting dialects |
