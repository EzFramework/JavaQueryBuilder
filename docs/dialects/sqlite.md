---
title: SQLite
parent: SQL Dialects
nav_order: 3
permalink: /sql-dialects/sqlite/
description: "SQLite dialect — double-quote identifier quoting and DELETE LIMIT support"
---

# SQLite Dialect
{: .no_toc }

## Table of contents
{: .no_toc .text-delta }

1. TOC
{:toc}

---

## Overview

`SqlDialect.SQLITE` wraps all table and column identifiers in double-quotes
(`"`), which is the SQL standard quoting character that SQLite follows. It also
supports the `LIMIT` clause on `DELETE` statements.

| Feature | Value |
|---------|-------|
| Identifier quoting | Double-quote `"` |
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
    .buildSql(SqlDialect.SQLITE);
// → SELECT "id", "name", "email" FROM "users" WHERE "status" = ? ORDER BY "name" ASC
// Parameters: ["active"]
```

---

## DELETE with LIMIT

```java
Query q = new QueryBuilder()
    .from("cache")
    .whereLessThan("expires_at", "2025-01-01")
    .limit(500)
    .build();

SqlResult result = SqlDialect.SQLITE.renderDelete(q);
// → DELETE FROM "cache" WHERE "expires_at" < ? LIMIT 500
// Parameters: ["2025-01-01"]
```

Or via `DeleteBuilder`:

```java
SqlResult result = QueryBuilder.deleteFrom("sessions")
    .whereEquals("expired", true)
    .build(SqlDialect.SQLITE);
// → DELETE FROM "sessions" WHERE "expired" = ?
```

---

## Identifier quoting coverage

Double-quote quoting is applied by the dialect to identifiers in SELECT and
DELETE statements. INSERT and UPDATE builders render their own SQL and do not
apply dialect quoting to column or table names.

---

## When to use

- SQLite databases
- Any SQL-standard database that uses double-quote identifiers
- When `DELETE … LIMIT` batching is needed
