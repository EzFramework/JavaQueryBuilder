---
title: INSERT
parent: Queries
nav_order: 2
permalink: /queries/insert/
description: "Building INSERT statements with InsertBuilder"
---

# INSERT
{: .no_toc }

## Table of contents
{: .no_toc .text-delta }

1. TOC
{:toc}

---

## Overview

`InsertBuilder` builds parameterized `INSERT INTO` statements. Create one via
the `QueryBuilder` factory method:

```java
import com.github.ezframework.javaquerybuilder.query.builder.QueryBuilder;
import com.github.ezframework.javaquerybuilder.query.sql.SqlResult;

SqlResult result = QueryBuilder.insertInto("users")
    .value("name", "Alice")
    .value("email", "alice@example.com")
    .value("age", 30)
    .build();

// result.getSql()        → INSERT INTO users (name, email, age) VALUES (?, ?, ?)
// result.getParameters() → ["Alice", "alice@example.com", 30]
```

---

## Basic insert

```java
SqlResult result = QueryBuilder.insertInto("orders")
    .value("product_id", 7)
    .value("qty", 2)
    .value("status", "pending")
    .build();
// → INSERT INTO orders (product_id, qty, status) VALUES (?, ?, ?)
// Parameters: [7, 2, "pending"]
```

---

## RETURNING (PostgreSQL)

`RETURNING` is appended inline after the closing `)`. The caller is responsible
for using a PostgreSQL connection; the clause is emitted regardless of which
dialect is passed to `build()`.

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

## Method reference

| Method | Returns | Description |
| -------- | --------- | ------------- |
| `into(String table)` | `InsertBuilder` | Set target table (also available via factory) |
| `value(String col, Object val)` | `InsertBuilder` | Add a column/value pair |
| `returning(String... cols)` | `InsertBuilder` | Append `RETURNING col1, col2, ...` (PostgreSQL only) |
| `build()` | `SqlResult` | Render with standard dialect |
| `build(SqlDialect dialect)` | `SqlResult` | Render with specified dialect |
