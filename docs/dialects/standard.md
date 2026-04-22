---
title: STANDARD
parent: SQL Dialects
nav_order: 1
permalink: /sql-dialects/standard/
description: "ANSI SQL dialect — no identifier quoting, no dialect-specific extensions"
---

# STANDARD Dialect
{: .no_toc }

## Table of contents
{: .no_toc .text-delta }

1. TOC
{:toc}

---

## Overview

`SqlDialect.STANDARD` produces ANSI-compliant SQL with no identifier quoting.
It is the default dialect used by `buildSql()` and `build()` when no dialect
is specified.

| Feature | Value |
| --------- | ------- |
| Identifier quoting | None |
| DELETE LIMIT | Not supported |
| ILIKE | Not supported |
| RETURNING on DELETE | Not supported |

---

## SELECT

`STANDARD` is the implicit default; you can pass it explicitly for clarity:

```java
SqlResult r = new QueryBuilder()
    .from("users")
    .select("id", "name")
    .whereEquals("status", "active")
    .buildSql(SqlDialect.STANDARD);
// → SELECT id, name FROM users WHERE status = ?
// Parameters: ["active"]

// Equivalent — no dialect argument means STANDARD
SqlResult r2 = new QueryBuilder()
    .from("users")
    .whereEquals("id", 1)
    .buildSql();
// → SELECT * FROM users WHERE id = ?
```

---

## DML statements

`STANDARD` is also the default for all DML builders when calling `build()` with
no argument:

```java
SqlResult insert = QueryBuilder.insertInto("orders")
    .value("product_id", 7)
    .value("qty", 2)
    .build();
// → INSERT INTO orders (product_id, qty) VALUES (?, ?)

SqlResult update = QueryBuilder.update("orders")
    .set("qty", 3)
    .whereEquals("id", 1)
    .build();
// → UPDATE orders SET qty = ? WHERE id = ?

SqlResult delete = QueryBuilder.deleteFrom("sessions")
    .whereEquals("expired", true)
    .build();
// → DELETE FROM sessions WHERE expired = ?
```

---

## DELETE LIMIT

`STANDARD` does not support a `LIMIT` clause on `DELETE`. If a limit is set on
the `Query` it is silently ignored:

```java
Query q = new QueryBuilder()
    .from("sessions")
    .whereEquals("expired", true)
    .limit(500)
    .build();

SqlResult r = SqlDialect.STANDARD.renderDelete(q);
// → DELETE FROM sessions WHERE expired = ?
// (LIMIT 500 is dropped)
```

---

## When to use

- Any ANSI-compatible database that does not need quoted identifiers
- Unit tests and integration tests where quoting does not matter
- When you want the simplest rendered SQL for debugging
