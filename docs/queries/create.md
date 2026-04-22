---
title: CREATE TABLE
parent: Queries
nav_order: 5
permalink: /queries/create/
description: "Building CREATE TABLE statements with CreateBuilder"
---

# CREATE TABLE
{: .no_toc }

## Table of contents
{: .no_toc .text-delta }

1. TOC
{:toc}

---

## Overview

`CreateBuilder` builds `CREATE TABLE` statements with optional `IF NOT EXISTS`
guards and composite primary keys. Column types can be specified as raw SQL
strings or as type-safe `ColumnType` constants and factory methods.

```java
import com.github.ezframework.javaquerybuilder.query.builder.ColumnType;
import com.github.ezframework.javaquerybuilder.query.builder.QueryBuilder;
import com.github.ezframework.javaquerybuilder.query.sql.SqlResult;

SqlResult result = QueryBuilder.createTable("users")
    .column("id",         ColumnType.INT.notNull().autoIncrement())
    .column("username",   ColumnType.varChar(64).notNull().unique())
    .column("balance",    ColumnType.decimal(10, 2))
    .column("created_at", ColumnType.TIMESTAMP)
    .primaryKey("id")
    .build();

// → CREATE TABLE users (
//       id         INT NOT NULL AUTO_INCREMENT,
//       username   VARCHAR(64) NOT NULL UNIQUE,
//       balance    DECIMAL(10, 2),
//       created_at TIMESTAMP,
//       PRIMARY KEY (id))
```

---

## ColumnType — type-safe column definitions

`ColumnType` provides pre-defined constants for all common SQL types, factory
methods for parameterised types, and chainable modifier methods for column-level
constraints.

### Fixed-width types

| Constant | SQL | Notes |
|----------|-----|-------|
| `ColumnType.TINYINT` | `TINYINT` | 1-byte integer; MySQL / MariaDB |
| `ColumnType.SMALLINT` | `SMALLINT` | 2-byte integer |
| `ColumnType.INT` | `INT` | 4-byte integer |
| `ColumnType.INTEGER` | `INTEGER` | Alias for `INT` in most databases |
| `ColumnType.BIGINT` | `BIGINT` | 8-byte integer |
| `ColumnType.FLOAT` | `FLOAT` | Single-precision float |
| `ColumnType.DOUBLE` | `DOUBLE` | Double-precision float |
| `ColumnType.REAL` | `REAL` | Standard SQL alias for `DOUBLE` |
| `ColumnType.BOOLEAN` | `BOOLEAN` | Boolean value |
| `ColumnType.TEXT` | `TEXT` | Unbounded text |
| `ColumnType.TINYTEXT` | `TINYTEXT` | Up to 255 chars; MySQL / MariaDB |
| `ColumnType.MEDIUMTEXT` | `MEDIUMTEXT` | Up to 16 MB; MySQL / MariaDB |
| `ColumnType.LONGTEXT` | `LONGTEXT` | Up to 4 GB; MySQL / MariaDB |
| `ColumnType.CLOB` | `CLOB` | Character large object (standard SQL) |
| `ColumnType.BLOB` | `BLOB` | Binary large object |
| `ColumnType.TINYBLOB` | `TINYBLOB` | Up to 255 bytes; MySQL / MariaDB |
| `ColumnType.MEDIUMBLOB` | `MEDIUMBLOB` | Up to 16 MB; MySQL / MariaDB |
| `ColumnType.LONGBLOB` | `LONGBLOB` | Up to 4 GB; MySQL / MariaDB |
| `ColumnType.DATE` | `DATE` | Calendar date |
| `ColumnType.TIME` | `TIME` | Time-of-day |
| `ColumnType.DATETIME` | `DATETIME` | Date + time; MySQL / MariaDB / SQLite |
| `ColumnType.TIMESTAMP` | `TIMESTAMP` | Date + time; often auto-updated |
| `ColumnType.JSON` | `JSON` | JSON document; MySQL 5.7+, PG 9.2+, SQLite 3.38+ |
| `ColumnType.SERIAL` | `SERIAL` | Auto-increment 4-byte int (PostgreSQL) |
| `ColumnType.BIGSERIAL` | `BIGSERIAL` | Auto-increment 8-byte int (PostgreSQL) |
| `ColumnType.UUID` | `UUID` | UUID; native on PostgreSQL |

### Parameterised factory methods

| Method | SQL output | Description |
|--------|-----------|-------------|
| `ColumnType.varChar(n)` | `VARCHAR(n)` | Variable-length string up to `n` chars |
| `ColumnType.charType(n)` | `CHAR(n)` | Fixed-length string of `n` chars |
| `ColumnType.decimal(p, s)` | `DECIMAL(p, s)` | Exact decimal with `p` total and `s` fraction digits |
| `ColumnType.numeric(p, s)` | `NUMERIC(p, s)` | Exact numeric; equivalent to `DECIMAL` in most databases |
| `ColumnType.binary(n)` | `BINARY(n)` | Fixed-length binary of `n` bytes |
| `ColumnType.varBinary(n)` | `VARBINARY(n)` | Variable-length binary up to `n` bytes |
| `ColumnType.timestamp(p)` | `TIMESTAMP(p)` | Timestamp with `p` fractional-seconds digits (0–6) |

### Modifier methods (chainable)

Modifiers return a new `ColumnType` instance and can be chained in any order:

| Method | Appends | Example |
|--------|---------|---------|
| `.notNull()` | `NOT NULL` | `ColumnType.INT.notNull()` → `INT NOT NULL` |
| `.unique()` | `UNIQUE` | `ColumnType.varChar(64).unique()` → `VARCHAR(64) UNIQUE` |
| `.autoIncrement()` | `AUTO_INCREMENT` | `ColumnType.INT.notNull().autoIncrement()` → `INT NOT NULL AUTO_INCREMENT` |
| `.defaultValue(val)` | `DEFAULT val` | `ColumnType.BOOLEAN.defaultValue("false")` → `BOOLEAN DEFAULT false` |

{: .warning }
> `.defaultValue(val)` inserts the string verbatim into the SQL. Use only
> static, known-safe values (e.g. `"0"`, `"false"`, `"CURRENT_TIMESTAMP"`).
> Never pass user-supplied input.

### Custom types

For database-specific types not covered by the built-in constants, pass a raw
SQL string directly to `column()` or use the public `ColumnType` constructor:

```java
// Raw SQL string (backward compatible)
.column("geom", "GEOMETRY NOT NULL")

// Public constructor for custom type
.column("geom", new ColumnType("GEOMETRY NOT NULL"))
```

---

## IF NOT EXISTS

```java
SqlResult result = QueryBuilder.createTable("sessions")
    .ifNotExists()
    .column("token",      ColumnType.varChar(128).notNull())
    .column("user_id",    ColumnType.INT)
    .column("expires_at", ColumnType.TIMESTAMP)
    .primaryKey("token")
    .build();

// → CREATE TABLE IF NOT EXISTS sessions (
//       token      VARCHAR(128) NOT NULL,
//       user_id    INT,
//       expires_at TIMESTAMP,
//       PRIMARY KEY (token))
```

---

## Composite primary key

```java
SqlResult result = QueryBuilder.createTable("user_roles")
    .column("user_id", ColumnType.INT)
    .column("role_id", ColumnType.INT)
    .primaryKey("user_id")
    .primaryKey("role_id")
    .build();

// → CREATE TABLE user_roles (user_id INT, role_id INT, PRIMARY KEY (user_id, role_id))
```

---

## Method reference

| Method | Returns | Description |
|--------|---------|-------------|
| `table(String name)` | `CreateBuilder` | Set table name |
| `column(String name, String sqlType)` | `CreateBuilder` | Add a column with a raw SQL type string |
| `column(String name, ColumnType type)` | `CreateBuilder` | Add a column with a type-safe `ColumnType` |
| `primaryKey(String name)` | `CreateBuilder` | Declare a primary key column |
| `ifNotExists()` | `CreateBuilder` | Add `IF NOT EXISTS` guard |
| `build()` | `SqlResult` | Render; throws `IllegalStateException` if table or columns are missing |
| `build(SqlDialect dialect)` | `SqlResult` | Render with specified dialect |

| `build(SqlDialect dialect)` | `SqlResult` | Render with specified dialect |
