---
layout: home
title: JavaQueryBuilder
nav_order: 1
description: "A lightweight, fluent Java library for building parameterized SQL queries"
permalink: /
---

# JavaQueryBuilder

[![JitPack](https://jitpack.io/v/EzFramework/JavaQueryBuilder.svg)](https://jitpack.io/#EzFramework/JavaQueryBuilder)
[![GitHub Packages](https://img.shields.io/badge/GitHub_Packages-1.1.0-blue?logo=github)](https://github.com/EzFramework/JavaQueryBuilder/packages)

**JavaQueryBuilder** is a lightweight, fluent Java library for building
parameterized SQL queries and filtering in-memory data.
No runtime dependencies required.

---

## Features

- **Fluent SELECT builder**: `from`, `select`, `distinct`, `where*`,
  `orderBy`, `groupBy`, `havingRaw`, `limit`, `offset`
- **DML builders**: `InsertBuilder`, `UpdateBuilder`, `DeleteBuilder`, `CreateBuilder`
- **Parameterized-only**: user values always go through `?` bind parameters; SQL injection
  is structurally impossible
- **18 operators**: equality, comparison, `LIKE`, `ILIKE` (PostgreSQL), `NULL` checks, `IN`, `BETWEEN`,
  `EXISTS`, and subquery operators
- **Subquery support**: `WHERE col IN (SELECT ...)`, `WHERE EXISTS (SELECT ...)`,
  `WHERE NOT EXISTS (SELECT ...)`, scalar `WHERE col = (SELECT ...)`,
  FROM-derived table, JOIN subquery, and scalar `SELECT` items
- **Four SQL dialects**: `STANDARD` (ANSI), `MYSQL` (back-tick quoting), `SQLITE` (double-quote), `POSTGRESQL` (double-quote + `ILIKE` + `RETURNING`)
- **Global and per-query configuration** via `QueryBuilderDefaults`: preset dialect, default
  columns, limit, offset, and LIKE wrapping once at application startup
- **In-memory filtering**: `QueryableStorage` functional interface applies the same `Query`
  to flat-map collections without touching a database
- **Zero runtime dependencies**: pure Java 25+, nothing to shade or exclude

---

## Quick start

**1. Add JavaQueryBuilder via JitPack:**

```xml
<repositories>
  <repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
  </repository>
</repositories>

<dependency>
  <groupId>com.github.EzFramework</groupId>
  <artifactId>JavaQueryBuilder</artifactId>
  <version>1.1.0</version>
</dependency>
```

**2. Build a SELECT query:**

```java
import com.github.ezframework.javaquerybuilder.query.builder.QueryBuilder;
import com.github.ezframework.javaquerybuilder.query.sql.SqlDialect;
import com.github.ezframework.javaquerybuilder.query.sql.SqlResult;

SqlResult result = new QueryBuilder()
    .from("users")
    .select("id", "name", "email")
    .whereEquals("status", "active")
    .whereGreaterThan("age", 18)
    .orderBy("name", true)
    .limit(20)
    .buildSql(SqlDialect.MYSQL);

String sql        = result.getSql();        // SELECT `id`, `name`, `email` FROM `users` WHERE ...
List<?> params    = result.getParameters(); // ["active", 18]
```

**3. Build an INSERT:**

```java
SqlResult insert = QueryBuilder.insertInto("users")
    .value("name", "Alice")
    .value("email", "alice@example.com")
    .build();
```

**4. Update with a condition:**

```java
SqlResult update = QueryBuilder.update("users")
    .set("status", "inactive")
    .whereEquals("id", 42)
    .build();
```

---

## Documentation

| Page | What it covers |
|------|----------------|
| [Installation](installation) | Maven, Gradle, JitPack, GitHub Packages |
| [Queries]({{ site.baseurl }}/queries/) | SELECT, INSERT, UPDATE, DELETE, CREATE TABLE builders |
| [Conditions](conditions) | All 18 operators, `Condition`, `ConditionEntry`, `Connector` |
| [Subqueries](subqueries) | All six subquery variants |
| [SQL Dialects]({{ site.baseurl }}/sql-dialects/) | `STANDARD`, `MYSQL`, `SQLITE`, `POSTGRESQL`, `SqlResult`, dialect matrix |
| [Configuration](configuration) | `QueryBuilderDefaults`: global and per-query dialect, columns, limit, LIKE wrapping |
| [In-Memory Filtering](in-memory) | `QueryableStorage`: filter collections without a database |
| [Exceptions](exceptions) | Error hierarchy and handling patterns |
| [API Reference](api-reference) | Full public-method tables for every class |
