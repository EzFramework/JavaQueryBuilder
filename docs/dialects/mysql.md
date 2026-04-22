---
title: MySQL
parent: SQL Dialects
nav_order: 2
permalink: /sql-dialects/mysql/
description: "MySQL dialect — back-tick identifier quoting and DELETE LIMIT support"
---

# MySQL Dialect
{: .no_toc }

## Table of contents
{: .no_toc .text-delta }

1. TOC
{:toc}

---

## Overview

`SqlDialect.MYSQL` wraps all table and column identifiers in back-ticks
(`` ` ``). It also supports the `LIMIT` clause on `DELETE` statements.
Quoting is applied to SELECT and DELETE queries rendered through this dialect.

| Feature | Value |
| --------- | ------- |
| Identifier quoting | Back-tick `` ` `` |
| DELETE LIMIT | Supported |
| ILIKE | Not supported |
| RETURNING on DELETE | Not supported |

---

## SELECT

```java
SqlResult r = new QueryBuilder()
    .from("users")
    .select("id", "name", "email")
    .whereEquals("status", "active")
    .orderBy("name", true)
    .buildSql(SqlDialect.MYSQL);
// → SELECT `id`, `name`, `email` FROM `users` WHERE `status` = ? ORDER BY `name` ASC
// Parameters: ["active"]
```

---

## DELETE with LIMIT

```java
Query q = new QueryBuilder()
    .from("logs")
    .whereLessThan("created_at", "2025-01-01")
    .limit(1000)
    .build();

SqlResult result = SqlDialect.MYSQL.renderDelete(q);
// → DELETE FROM `logs` WHERE `created_at` < ? LIMIT 1000
// Parameters: ["2025-01-01"]
```

Or via `DeleteBuilder`:

```java
SqlResult result = QueryBuilder.deleteFrom("sessions")
    .whereEquals("expired", true)
    .build(SqlDialect.MYSQL);
// → DELETE FROM `sessions` WHERE `expired` = ?
```

---

## Identifier quoting coverage

Back-tick quoting is applied by the dialect to identifiers in SELECT and DELETE
statements. INSERT and UPDATE builders render their own SQL and do not apply
dialect quoting to column or table names.

---

## When to use

- MySQL and MariaDB
- Any database that uses back-tick quoting convention
- When `DELETE … LIMIT` batching is needed
