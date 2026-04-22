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
guards and composite primary keys:

```java
import com.github.ezframework.javaquerybuilder.query.builder.QueryBuilder;
import com.github.ezframework.javaquerybuilder.query.sql.SqlResult;

SqlResult result = QueryBuilder.createTable("users")
    .column("id",    "INT")
    .column("name",  "VARCHAR(64)")
    .column("email", "VARCHAR(255)")
    .primaryKey("id")
    .build();

// → CREATE TABLE users (id INT, name VARCHAR(64), email VARCHAR(255), PRIMARY KEY (id))
```

---

## IF NOT EXISTS

```java
SqlResult result = QueryBuilder.createTable("sessions")
    .ifNotExists()
    .column("token",      "VARCHAR(128)")
    .column("user_id",    "INT")
    .column("expires_at", "TIMESTAMP")
    .primaryKey("token")
    .build();

// → CREATE TABLE IF NOT EXISTS sessions (token VARCHAR(128), user_id INT, expires_at TIMESTAMP, PRIMARY KEY (token))
```

---

## Composite primary key

```java
SqlResult result = QueryBuilder.createTable("user_roles")
    .column("user_id", "INT")
    .column("role_id", "INT")
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
| `column(String name, String sqlType)` | `CreateBuilder` | Add a column definition |
| `primaryKey(String name)` | `CreateBuilder` | Declare a primary key column |
| `ifNotExists()` | `CreateBuilder` | Add `IF NOT EXISTS` guard |
| `build()` | `SqlResult` | Render; throws `IllegalStateException` if table or columns are missing |
| `build(SqlDialect dialect)` | `SqlResult` | Render with specified dialect |
