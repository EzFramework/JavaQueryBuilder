---
title: Subqueries
nav_order: 5
description: "All six subquery variants: IN, EXISTS, NOT EXISTS, scalar, FROM-derived table, JOIN, and scalar SELECT"
---

# Subqueries
{: .no_toc }

## Table of contents
{: .no_toc .text-delta }

1. TOC
{:toc}

---

## Overview

JavaQueryBuilder supports six distinct ways to embed a subquery into a SELECT
statement. Every subquery is represented as a `Query` object built by a nested
`QueryBuilder` call.

| Method | SQL produced |
| -------- | ------------- |
| `whereInSubquery(col, sub)` | `WHERE col IN (SELECT ...)` |
| `whereEqualsSubquery(col, sub)` | `WHERE col = (SELECT ...)` |
| `whereExistsSubquery(sub)` | `WHERE EXISTS (SELECT ...)` |
| `whereNotExistsSubquery(sub)` | `WHERE NOT EXISTS (SELECT ...)` |
| `fromSubquery(sub, alias)` | `FROM (SELECT ...) AS alias` |
| `joinSubquery(sub, alias, on)` | `INNER JOIN (SELECT ...) AS alias ON ...` |
| `selectSubquery(sub, alias)` | `(SELECT ...) AS alias` in SELECT clause |

---

## WHERE col IN (SELECT ...)

Use `whereInSubquery` to filter rows where a column value appears in the result
set of another query.

```java
Query activeUserIds = new QueryBuilder()
    .from("users")
    .select("id")
    .whereEquals("active", true)
    .build();

SqlResult result = new QueryBuilder()
    .from("orders")
    .whereInSubquery("user_id", activeUserIds)
    .buildSql();
// → SELECT * FROM orders WHERE user_id IN (SELECT id FROM users WHERE active = ?)
// Parameters: [true]
```

---

## WHERE col = (SELECT ...)

Use `whereEqualsSubquery` when the subquery returns a single scalar value to
compare against.

```java
Query maxPrice = new QueryBuilder()
    .from("products")
    .select("MAX(price)")
    .build();

SqlResult result = new QueryBuilder()
    .from("products")
    .whereEqualsSubquery("price", maxPrice)
    .buildSql();
// → SELECT * FROM products WHERE price = (SELECT MAX(price) FROM products)
```

---

## WHERE EXISTS (SELECT ...)

Use `whereExistsSubquery` to test whether the subquery returns at least one row.

```java
Query sub = new QueryBuilder()
    .from("orders")
    .select("1")
    .whereEquals("status", "pending")
    .build();

SqlResult result = new QueryBuilder()
    .from("users")
    .whereExistsSubquery(sub)
    .buildSql();
// → SELECT * FROM users WHERE EXISTS (SELECT 1 FROM orders WHERE status = ?)
// Parameters: ["pending"]
```

---

## WHERE NOT EXISTS (SELECT ...)

Use `whereNotExistsSubquery` to select rows only when the subquery returns no
results.

```java
Query sub = new QueryBuilder()
    .from("orders")
    .select("1")
    .whereEquals("user_id", 7)
    .build();

SqlResult result = new QueryBuilder()
    .from("users")
    .whereNotExistsSubquery(sub)
    .buildSql();
// → SELECT * FROM users WHERE NOT EXISTS (SELECT 1 FROM orders WHERE user_id = ?)
// Parameters: [7]
```

---

## FROM derived table

Use `fromSubquery` to replace the table source with a subquery.

```java
Query inner = new QueryBuilder()
    .from("events")
    .select("user_id", "COUNT(*) AS event_count")
    .groupBy("user_id")
    .build();

SqlResult result = new QueryBuilder()
    .fromSubquery(inner, "event_stats")
    .select("user_id", "event_count")
    .whereGreaterThan("event_count", 5)
    .buildSql();
// → SELECT user_id, event_count
//   FROM (SELECT user_id, COUNT(*) AS event_count FROM events GROUP BY user_id) event_stats
//   WHERE event_count > ?
// Parameters: [5]
```

---

## INNER JOIN subquery

Use `joinSubquery` to join a derived table against the main query:

```java
Query teamCounts = new QueryBuilder()
    .from("memberships")
    .select("team_id", "COUNT(*) AS member_count")
    .groupBy("team_id")
    .build();

SqlResult result = new QueryBuilder()
    .from("teams")
    .select("teams.name", "tc.member_count")
    .joinSubquery(teamCounts, "tc", "tc.team_id = teams.id")
    .buildSql();
// → SELECT teams.name, tc.member_count
//   FROM teams
//   INNER JOIN (SELECT team_id, COUNT(*) AS member_count FROM memberships GROUP BY team_id) tc
//     ON tc.team_id = teams.id
```

{: .note }
> `joinSubquery` always produces `INNER JOIN`. For other join types, construct
> a `JoinClause` manually using `JoinClause.Type.LEFT`, `RIGHT`, or `CROSS`.

---

## Scalar SELECT item

Use `selectSubquery` to embed a scalar subquery as a computed column in the
SELECT list.

```java
Query orderCount = new QueryBuilder()
    .from("orders")
    .select("COUNT(*)")
    .whereEqualsSubquery("user_id",
        new QueryBuilder().from("users").select("id").whereEquals("id", 1).build())
    .build();

SqlResult result = new QueryBuilder()
    .from("users")
    .select("id", "name")
    .selectSubquery(orderCount, "order_count")
    .buildSql();
// → SELECT id, name, (SELECT COUNT(*) FROM orders WHERE user_id = (SELECT id FROM users WHERE id = ?)) AS order_count
//   FROM users
```

---

## Parameter ordering

When subqueries are nested, parameters are collected depth-first in the order
the subqueries appear in the rendered SQL. The outermost query's own parameters
follow after all subquery parameters at the same level.

```java
Query sub = new QueryBuilder()
    .from("teams")
    .select("id")
    .whereEquals("name", "Engineering")
    .build();

SqlResult result = new QueryBuilder()
    .from("users")
    .whereInSubquery("team_id", sub)
    .whereEquals("active", true)
    .buildSql();

// Parameters: ["Engineering", true]
//              ^^^^^^^^^^^^^^ from sub  ^^^^^  from outer
```
