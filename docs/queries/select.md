---
title: SELECT
parent: Queries
nav_order: 1
permalink: /queries/select/
description: "Building SELECT queries with the fluent QueryBuilder API"
---

# SELECT
{: .no_toc }

## Table of contents
{: .no_toc .text-delta }

1. TOC
{:toc}

---

## Overview

`QueryBuilder` provides a fluent API for composing SELECT statements. Chain
method calls and finish with `build()` (returns a `Query` object) or
`buildSql()` (returns a `SqlResult` ready for execution).

```java
import com.github.ezframework.javaquerybuilder.query.builder.QueryBuilder;
import com.github.ezframework.javaquerybuilder.query.sql.SqlDialect;
import com.github.ezframework.javaquerybuilder.query.sql.SqlResult;

SqlResult result = new QueryBuilder()
    .from("users")
    .select("id", "name", "email")
    .whereEquals("status", "active")
    .orderBy("name", true)
    .limit(50)
    .buildSql(SqlDialect.MYSQL);

String sql     = result.getSql();
List<?> params = result.getParameters();
```

For DML statements (INSERT, UPDATE, DELETE, CREATE TABLE) use the static factory
methods. See [INSERT]({{ site.baseurl }}/queries/insert/),
[UPDATE]({{ site.baseurl }}/queries/update/),
[DELETE]({{ site.baseurl }}/queries/delete/), and
[CREATE TABLE]({{ site.baseurl }}/queries/create/).

---

## Setting the table

```java
new QueryBuilder().from("orders")
```

---

## Selecting columns

```java
// SELECT * (default — no columns specified)
new QueryBuilder().from("users")

// SELECT id, name
new QueryBuilder().from("users").select("id", "name")

// DISTINCT
new QueryBuilder().from("users").distinct().select("country")
```

---

## Filtering with `where*`

All `where*` methods are joined with `AND` by default. Use the `orWhere*`
variants to join with `OR`.

```java
// WHERE status = 'active'
.whereEquals("status", "active")

// WHERE status != 'banned'
.whereNotEquals("status", "banned")

// WHERE age > 18
.whereGreaterThan("age", 18)

// WHERE age >= 18
.whereGreaterThanOrEquals("age", 18)

// WHERE price < 100
.whereLessThan("price", 100)

// WHERE price <= 100
.whereLessThanOrEquals("price", 100)

// WHERE name LIKE '%Alice%'
.whereLike("name", "Alice")

// WHERE name NOT LIKE '%bot%'
.whereNotLike("name", "bot")

// WHERE deleted_at IS NULL
.whereNull("deleted_at")

// WHERE verified_at IS NOT NULL
.whereNotNull("verified_at")

// WHERE country IS NOT NULL  (alias for whereNotNull)
.whereExists("country")

// WHERE status IN ('active', 'pending')
.whereIn("status", List.of("active", "pending"))

// WHERE status NOT IN ('banned', 'deleted')
.whereNotIn("status", List.of("banned", "deleted"))

// WHERE price BETWEEN 10 AND 99
.whereBetween("price", 10, 99)
```

### OR conditions

Every `where*` method has an `orWhere*` counterpart:

```java
new QueryBuilder()
    .from("users")
    .whereEquals("role", "admin")
    .orWhereEquals("role", "moderator")
// → WHERE role = ? OR role = ?
```

---

## ILIKE (PostgreSQL)

For case-insensitive LIKE matching on PostgreSQL, use `whereILike` and
`orWhereILike`:

```java
SqlResult result = new QueryBuilder()
    .from("users")
    .whereILike("email", "alice")
    .buildSql(SqlDialect.POSTGRESQL);
// → SELECT * FROM "users" WHERE "email" ILIKE ?
// Parameters: ["%alice%"]

// OR variant
new QueryBuilder()
    .from("users")
    .whereEquals("role", "admin")
    .orWhereILike("name", "bot")
    .buildSql(SqlDialect.POSTGRESQL);
// → SELECT * FROM "users" WHERE "role" = ? OR "name" ILIKE ?
```

---

## Ordering

```java
// ORDER BY name ASC
.orderBy("name", true)

// ORDER BY created_at DESC
.orderBy("created_at", false)

// Multiple columns: ORDER BY level DESC, name ASC
.orderBy("level", false)
.orderBy("name", true)
```

---

## Grouping

```java
// GROUP BY country
.groupBy("country")

// GROUP BY country, city
.groupBy("country", "city")
```

### HAVING

Pass a raw SQL fragment with no value interpolation. Use static expressions only:

```java
.groupBy("category")
.havingRaw("COUNT(*) > 5")
```

{: .warning }
> `havingRaw` accepts a raw SQL string. Never pass user-supplied input here.
> Use only static, known-safe expressions.

---

## LIMIT and OFFSET

```java
// First 20 rows
.limit(20)

// Rows 41–60 (page 3 of 20)
.limit(20).offset(40)
```

---

## Building the result

### `build()` — returns a `Query`

`build()` produces a `Query` object which can be passed to a `SqlDialect` later,
used for in-memory filtering with `QueryableStorage`, or inspected directly:

```java
Query q = new QueryBuilder()
    .from("products")
    .whereGreaterThan("stock", 0)
    .build();
```

### `buildSql()` — returns a `SqlResult`

`buildSql()` renders the `Query` immediately using the standard ANSI dialect.
Use the overloads to specify a table or dialect explicitly:

```java
// Table set via from(), standard dialect
SqlResult r1 = builder.buildSql();

// Explicit table, standard dialect
SqlResult r2 = builder.buildSql("orders");

// Explicit table and dialect
SqlResult r3 = builder.buildSql("orders", SqlDialect.MYSQL);
```

See [SQL Dialects]({{ site.baseurl }}/sql-dialects/) for the dialect options and
identifier-quoting differences across database targets.

---

## Subquery methods

`QueryBuilder` also exposes methods for embedding subqueries:

| Method | What it adds |
| -------- | ------------- |
| `whereInSubquery(col, subquery)` | `WHERE col IN (SELECT ...)` |
| `whereEqualsSubquery(col, subquery)` | `WHERE col = (SELECT ...)` |
| `whereExistsSubquery(subquery)` | `WHERE EXISTS (SELECT ...)` |
| `whereNotExistsSubquery(subquery)` | `WHERE NOT EXISTS (SELECT ...)` |
| `fromSubquery(subquery, alias)` | `FROM (SELECT ...) AS alias` |
| `joinSubquery(subquery, alias, on)` | `INNER JOIN (SELECT ...) AS alias ON ...` |
| `selectSubquery(subquery, alias)` | `(SELECT ...) AS alias` in SELECT clause |

See [Subqueries]({{ site.baseurl }}/subqueries/) for full examples.

---

## Security

Every value passed to a `where*` method is placed in the `?` bind-parameter
list of the rendered `SqlResult`. It is never concatenated into the SQL string.

```java
// Safe even if userInput contains SQL metacharacters
String userInput = "'; DROP TABLE users; --";

SqlResult r = new QueryBuilder()
    .from("users")
    .whereEquals("name", userInput)
    .buildSql();

// r.getSql()        → "SELECT * FROM users WHERE name = ?"
// r.getParameters() → ["'; DROP TABLE users; --"]
```

{: .warning }
> Column names and table names are **not** parameterized. Always use static,
> known-safe strings for those arguments. Never forward user input as a
> column or table name.
