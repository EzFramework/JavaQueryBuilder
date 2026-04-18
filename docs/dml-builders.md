---
title: DML Builders
nav_order: 4
description: "INSERT, UPDATE, DELETE, and CREATE TABLE builders"
---

# DML Builders
{: .no_toc }

## Table of contents
{: .no_toc .text-delta }

1. TOC
{:toc}

---

## Overview

`QueryBuilder` provides static factory methods that return dedicated builder
objects for every DML and DDL statement type:

| Factory method | Builder | Statement |
|----------------|---------|-----------|
| `QueryBuilder.insertInto(table)` | `InsertBuilder` | `INSERT INTO` |
| `QueryBuilder.update(table)` | `UpdateBuilder` | `UPDATE` |
| `QueryBuilder.deleteFrom(table)` | `DeleteBuilder` | `DELETE FROM` |
| `QueryBuilder.createTable(table)` | `CreateBuilder` | `CREATE TABLE` |

Each builder returns a `SqlResult` from its `build()` method, giving you the
rendered SQL string and the ordered bind-parameter list.

---

## InsertBuilder

### Basic insert

```java
SqlResult result = QueryBuilder.insertInto("users")
    .value("name", "Alice")
    .value("email", "alice@example.com")
    .value("age", 30)
    .build();

// result.getSql()        → INSERT INTO users (name, email, age) VALUES (?, ?, ?)
// result.getParameters() → ["Alice", "alice@example.com", 30]
```

### Dialect-aware insert

```java
SqlResult result = QueryBuilder.insertInto("users")
    .value("name", "Bob")
    .build(SqlDialect.MYSQL);
```

### Method reference

| Method | Returns | Description |
|--------|---------|-------------|
| `into(String table)` | `InsertBuilder` | Set target table (also available via factory) |
| `value(String col, Object val)` | `InsertBuilder` | Add a column/value pair |
| `build()` | `SqlResult` | Render with standard dialect |
| `build(SqlDialect dialect)` | `SqlResult` | Render with specified dialect |

---

## UpdateBuilder

### Basic update

```java
SqlResult result = QueryBuilder.update("users")
    .set("status", "inactive")
    .set("updated_at", "2026-01-01")
    .whereEquals("id", 42)
    .build();

// → UPDATE users SET status = ?, updated_at = ? WHERE id = ?
// Parameters: ["inactive", "2026-01-01", 42]
```

### Multiple conditions

```java
SqlResult result = QueryBuilder.update("products")
    .set("price", 9.99)
    .whereEquals("category", "sale")
    .whereGreaterThanOrEquals("stock", 1)
    .build();
```

### OR condition

```java
SqlResult result = QueryBuilder.update("users")
    .set("role", "user")
    .whereEquals("role", "guest")
    .orWhereEquals("role", "temp")
    .build();
// → UPDATE users SET role = ? WHERE role = ? OR role = ?
```

### Method reference

| Method | Returns | Description |
|--------|---------|-------------|
| `table(String table)` | `UpdateBuilder` | Set target table |
| `set(String col, Object val)` | `UpdateBuilder` | Add a SET column/value pair |
| `whereEquals(String col, Object val)` | `UpdateBuilder` | `WHERE col = ?` (AND) |
| `orWhereEquals(String col, Object val)` | `UpdateBuilder` | `WHERE col = ?` (OR) |
| `whereGreaterThanOrEquals(String col, int val)` | `UpdateBuilder` | `WHERE col >= ?` (AND) |
| `build()` | `SqlResult` | Render with standard dialect |
| `build(SqlDialect dialect)` | `SqlResult` | Render with specified dialect |

---

## DeleteBuilder

### Basic delete

```java
SqlResult result = QueryBuilder.deleteFrom("sessions")
    .whereEquals("user_id", 99)
    .build();

// → DELETE FROM sessions WHERE user_id = ?
// Parameters: [99]
```

### Multiple conditions

```java
SqlResult result = QueryBuilder.deleteFrom("logs")
    .whereLessThan("created_at", "2025-01-01")
    .whereEquals("level", "debug")
    .build();
```

### IN / NOT IN

```java
SqlResult result = QueryBuilder.deleteFrom("users")
    .whereIn("status", List.of("banned", "deleted"))
    .build();
// → DELETE FROM users WHERE status IN (?, ?)
```

### BETWEEN

```java
SqlResult result = QueryBuilder.deleteFrom("events")
    .whereBetween("score", 0, 10)
    .build();
// → DELETE FROM events WHERE score BETWEEN ? AND ?
```

### Dialect-aware delete (with LIMIT support)

MySQL and SQLite support a `LIMIT` clause on `DELETE`. Use `renderDelete` via
the dialect directly when you have a `Query` object:

```java
Query q = new QueryBuilder()
    .from("logs")
    .whereLessThan("age", 30)
    .limit(100)
    .build();

SqlResult result = SqlDialect.MYSQL.renderDelete(q);
// → DELETE FROM `logs` WHERE `age` < ? LIMIT 100
```

### Method reference

| Method | Returns | Description |
|--------|---------|-------------|
| `from(String table)` | `DeleteBuilder` | Set target table |
| `whereEquals(col, val)` | `DeleteBuilder` | `WHERE col = ?` (AND) |
| `whereNotEquals(col, val)` | `DeleteBuilder` | `WHERE col != ?` (AND) |
| `whereGreaterThan(col, val)` | `DeleteBuilder` | `WHERE col > ?` (AND) |
| `whereGreaterThanOrEquals(col, val)` | `DeleteBuilder` | `WHERE col >= ?` (AND) |
| `whereLessThan(col, val)` | `DeleteBuilder` | `WHERE col < ?` (AND) |
| `whereLessThanOrEquals(col, val)` | `DeleteBuilder` | `WHERE col <= ?` (AND) |
| `whereIn(col, List<?>)` | `DeleteBuilder` | `WHERE col IN (...)` (AND) |
| `whereNotIn(col, List<?>)` | `DeleteBuilder` | `WHERE col NOT IN (...)` (AND) |
| `whereBetween(col, from, to)` | `DeleteBuilder` | `WHERE col BETWEEN ? AND ?` (AND) |
| `build()` | `SqlResult` | Render with standard dialect |
| `build(SqlDialect dialect)` | `SqlResult` | Render with specified dialect |

---

## CreateBuilder

### Basic CREATE TABLE

```java
SqlResult result = QueryBuilder.createTable("users")
    .column("id",    "INT")
    .column("name",  "VARCHAR(64)")
    .column("email", "VARCHAR(255)")
    .primaryKey("id")
    .build();

// → CREATE TABLE users (id INT, name VARCHAR(64), email VARCHAR(255), PRIMARY KEY (id))
```

### IF NOT EXISTS

```java
SqlResult result = QueryBuilder.createTable("sessions")
    .ifNotExists()
    .column("token",      "VARCHAR(128)")
    .column("user_id",    "INT")
    .column("expires_at", "TIMESTAMP")
    .primaryKey("token")
    .build();

// → CREATE TABLE IF NOT EXISTS sessions (token VARCHAR(128), ..., PRIMARY KEY (token))
```

### Composite primary key

```java
SqlResult result = QueryBuilder.createTable("user_roles")
    .column("user_id", "INT")
    .column("role_id", "INT")
    .primaryKey("user_id")
    .primaryKey("role_id")
    .build();
// → ... PRIMARY KEY (user_id, role_id)
```

### Method reference

| Method | Returns | Description |
|--------|---------|-------------|
| `table(String name)` | `CreateBuilder` | Set table name |
| `column(String name, String sqlType)` | `CreateBuilder` | Add column definition |
| `primaryKey(String name)` | `CreateBuilder` | Declare a primary key column |
| `ifNotExists()` | `CreateBuilder` | Add `IF NOT EXISTS` guard |
| `build()` | `SqlResult` | Render; throws `IllegalStateException` if table or columns are missing |
| `build(SqlDialect dialect)` | `SqlResult` | Render with specified dialect |
