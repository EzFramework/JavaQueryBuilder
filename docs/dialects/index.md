---
title: SQL Dialects
nav_order: 6
has_children: true
permalink: /sql-dialects/
description: "SqlDialect, SqlResult, AbstractSqlDialect, and the dialect comparison matrix"
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
parameterized `SqlResult`. Four built-in dialects are provided as constants
on the interface:

| Constant | Page | Identifier quoting | DELETE LIMIT | ILIKE | RETURNING |
|----------|------|--------------------|--------------|-------|-----------|
| `SqlDialect.STANDARD` | [STANDARD]({{ site.baseurl }}/sql-dialects/standard/) | None (ANSI) | No | No | No |
| `SqlDialect.MYSQL` | [MySQL]({{ site.baseurl }}/sql-dialects/mysql/) | Back-tick `` ` `` | Yes | No | No |
| `SqlDialect.SQLITE` | [SQLite]({{ site.baseurl }}/sql-dialects/sqlite/) | Double-quote `"` | Yes | No | No |
| `SqlDialect.POSTGRESQL` | [PostgreSQL]({{ site.baseurl }}/sql-dialects/postgresql/) | Double-quote `"` | No | Yes | Yes (DELETE) |

---

## Dialect matrix

The same `Query` produces different SQL across dialects due to identifier
quoting:

| Feature | STANDARD | MYSQL | SQLITE | POSTGRESQL |
|---------|----------|-------|--------|------------|
| Table quoting | `users` | `` `users` `` | `"users"` | `"users"` |
| Column quoting | `id` | `` `id` `` | `"id"` | `"id"` |
| DELETE LIMIT | No | Yes | Yes | No |
| ILIKE / NOT ILIKE | No | No | No | Yes |
| RETURNING on DELETE | No | No | No | Yes |
| Parameter syntax | `?` | `?` | `?` | `?` |

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
