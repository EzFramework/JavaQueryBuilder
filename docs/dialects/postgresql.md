---
title: PostgreSQL
parent: SQL Dialects
nav_order: 4
permalink: /sql-dialects/postgresql/
description: "PostgreSQL dialect — double-quote quoting, ILIKE operators, and RETURNING on DELETE"
---

# PostgreSQL Dialect
{: .no_toc }

## Table of contents
{: .no_toc .text-delta }

1. TOC
{:toc}

---

## Overview

`SqlDialect.POSTGRESQL` wraps all table and column identifiers in double-quotes
and adds two PostgreSQL-specific features: case-insensitive `ILIKE` / `NOT ILIKE`
operators on SELECT queries, and a `RETURNING` clause on `DELETE` statements.

| Feature | Value |
| --------- | ------- |
| Identifier quoting | Double-quote `"` |
| DELETE LIMIT | Not supported |
| ILIKE / NOT ILIKE | Supported |
| RETURNING on DELETE | Supported |

---

## SELECT with double-quote quoting

```java
SqlResult r = new QueryBuilder()
    .from("users")
    .select("id", "name", "email")
    .whereEquals("status", "active")
    .orderBy("name", true)
    .buildSql(SqlDialect.POSTGRESQL);
// → SELECT "id", "name", "email" FROM "users" WHERE "status" = ? ORDER BY "name" ASC
// Parameters: ["active"]
```

---

## ILIKE — case-insensitive LIKE

`ILIKE` is a PostgreSQL extension for case-insensitive pattern matching.
Use `whereILike` / `orWhereILike` on `QueryBuilder` or `SelectBuilder`:

```java
// Match email containing "alice" (any case)
SqlResult r = new QueryBuilder()
    .from("users")
    .whereILike("email", "alice")
    .buildSql(SqlDialect.POSTGRESQL);
// → SELECT * FROM "users" WHERE "email" ILIKE ?
// Parameters: ["%alice%"]

// Combine with other conditions
SqlResult r2 = new QueryBuilder()
    .from("articles")
    .whereEquals("published", true)
    .whereILike("title", "java")
    .buildSql(SqlDialect.POSTGRESQL);
// → SELECT * FROM "articles" WHERE "published" = ? AND "title" ILIKE ?

// OR variant
SqlResult r3 = new QueryBuilder()
    .from("users")
    .whereEquals("role", "admin")
    .orWhereILike("name", "bot")
    .buildSql(SqlDialect.POSTGRESQL);
// → SELECT * FROM "users" WHERE "role" = ? OR "name" ILIKE ?
```

{: .note }
> `whereILike` and `orWhereILike` use the `ILIKE` operator which is only
> rendered correctly by `SqlDialect.POSTGRESQL`. Passing a different dialect
> will render the operator as-is without case-insensitive behaviour.

---

## DELETE with RETURNING

`RETURNING` on DELETE is rendered by the dialect — it is appended only when
`SqlDialect.POSTGRESQL` is active:

```java
SqlResult result = QueryBuilder.deleteFrom("users")
    .whereEquals("id", 99)
    .returning("id", "email")
    .build(SqlDialect.POSTGRESQL);

// → DELETE FROM "users" WHERE "id" = ? RETURNING id, email
// Parameters: [99]
```

Without the PostgreSQL dialect the `RETURNING` columns are ignored even if set:

```java
// RETURNING is silently dropped for non-supporting dialects
SqlResult result = QueryBuilder.deleteFrom("users")
    .whereEquals("id", 99)
    .returning("id", "email")
    .build(SqlDialect.STANDARD);
// → DELETE FROM users WHERE id = ?
```

---

## INSERT with RETURNING

For INSERT, `RETURNING` is appended inline regardless of dialect — the caller
is responsible for using a PostgreSQL connection:

```java
SqlResult result = QueryBuilder.insertInto("users")
    .value("name", "Alice")
    .value("email", "alice@example.com")
    .returning("id", "created_at")
    .build();
// → INSERT INTO users (name, email) VALUES (?, ?) RETURNING id, created_at
// Parameters: ["Alice", "alice@example.com"]
```

---

## UPDATE with RETURNING

Same inline behaviour as INSERT:

```java
SqlResult result = QueryBuilder.update("users")
    .set("status", "active")
    .whereEquals("id", 7)
    .returning("id", "updated_at")
    .build();
// → UPDATE users SET status = ? WHERE id = ? RETURNING id, updated_at
// Parameters: ["active", 7]
```

---

## Identifier quoting coverage

Double-quote quoting is applied by the dialect to identifiers in SELECT and
DELETE statements. INSERT and UPDATE builders render their own SQL and do not
apply dialect quoting to column or table names.

---

## When to use

- PostgreSQL databases
- When you need case-insensitive LIKE matching (`ILIKE`)
- When you need to retrieve affected row values from a DELETE in a single
  round-trip (`RETURNING`)
