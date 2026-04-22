---
title: UPDATE
parent: Queries
nav_order: 3
permalink: /queries/update/
description: "Building UPDATE statements with UpdateBuilder"
---

# UPDATE
{: .no_toc }

## Table of contents
{: .no_toc .text-delta }

1. TOC
{:toc}

---

## Overview

`UpdateBuilder` builds parameterized `UPDATE … SET … WHERE` statements:

```java
import com.github.ezframework.javaquerybuilder.query.builder.QueryBuilder;
import com.github.ezframework.javaquerybuilder.query.sql.SqlResult;

SqlResult result = QueryBuilder.update("users")
    .set("status", "inactive")
    .set("updated_at", "2026-01-01")
    .whereEquals("id", 42)
    .build();

// → UPDATE users SET status = ?, updated_at = ? WHERE id = ?
// Parameters: ["inactive", "2026-01-01", 42]
```

---

## Multiple conditions

```java
SqlResult result = QueryBuilder.update("products")
    .set("price", 9.99)
    .whereEquals("category", "sale")
    .whereGreaterThanOrEquals("stock", 1)
    .build();
// → UPDATE products SET price = ? WHERE category = ? AND stock >= ?
```

---

## OR condition

```java
SqlResult result = QueryBuilder.update("users")
    .set("role", "user")
    .whereEquals("role", "guest")
    .orWhereEquals("role", "temp")
    .build();
// → UPDATE users SET role = ? WHERE role = ? OR role = ?
```

---

## RETURNING (PostgreSQL)

`RETURNING` is appended inline after the `WHERE` clause. The caller is
responsible for using a PostgreSQL connection; the clause is emitted regardless
of which dialect is passed to `build()`.

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

## Method reference

| Method | Returns | Description |
|--------|---------|-------------|
| `table(String table)` | `UpdateBuilder` | Set target table |
| `set(String col, Object val)` | `UpdateBuilder` | Add a SET column/value pair |
| `whereEquals(String col, Object val)` | `UpdateBuilder` | `WHERE col = ?` (AND) |
| `orWhereEquals(String col, Object val)` | `UpdateBuilder` | `WHERE col = ?` (OR) |
| `whereGreaterThanOrEquals(String col, int val)` | `UpdateBuilder` | `WHERE col >= ?` (AND) |
| `returning(String... cols)` | `UpdateBuilder` | Append `RETURNING col1, col2, ...` (PostgreSQL only) |
| `build()` | `SqlResult` | Render with standard dialect |
| `build(SqlDialect dialect)` | `SqlResult` | Render with specified dialect |
