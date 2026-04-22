---
title: Configuration
nav_order: 7
description: "QueryBuilderDefaults: global and per-query preset for dialect, columns, limit, offset, and LIKE wrapping"
---

# Configuration
{: .no_toc }

## Table of contents
{: .no_toc .text-delta }

1. TOC
{:toc}

---

## Overview

`QueryBuilderDefaults` is an immutable configuration object that holds the
defaults applied by every builder (`QueryBuilder`, `SelectBuilder`,
`DeleteBuilder`) when no explicit value is provided.

You configure it once at application startup and every builder created
afterward automatically honours those settings. You can also override the
defaults for a single builder instance using `.withDefaults()`.

**Configurable settings:**

| Setting | Default | Description |
| --------- | --------- | ------------- |
| `dialect` | `SqlDialect.STANDARD` | SQL dialect used for identifier quoting |
| `defaultColumns` | `"*"` | Column expression used in `SELECT` when none are specified |
| `defaultLimit` | `-1` (no limit) | `LIMIT` applied when the builder has no `.limit()` call |
| `defaultOffset` | `-1` (no offset) | `OFFSET` applied when the builder has no `.offset()` call |
| `likePrefix` | `"%"` | Prefix wrapped around values in `LIKE` conditions |
| `likeSuffix` | `"%"` | Suffix wrapped around values in `LIKE` conditions |

---

## Setting a global dialect

Call `QueryBuilderDefaults.setGlobal()` once at startup. All builders created
after the call will use the new defaults.

```java
import com.github.ezframework.javaquerybuilder.query.QueryBuilderDefaults;
import com.github.ezframework.javaquerybuilder.query.sql.SqlDialect;

// Use SQLite for every query in this application
QueryBuilderDefaults.setGlobal(
    QueryBuilderDefaults.builder()
        .dialect(SqlDialect.SQLITE)
        .build()
);
```

After this call you no longer need to pass a dialect to `buildSql()`:

```java
SqlResult result = new QueryBuilder()
    .from("users")
    .whereEquals("id", 1)
    .buildSql();
// SELECT * FROM "users" WHERE "id" = ?
```

---

## Full configuration example

```java
QueryBuilderDefaults.setGlobal(
    QueryBuilderDefaults.builder()
        .dialect(SqlDialect.MYSQL)
        .defaultColumns("id, name, created_at")
        .defaultLimit(100)
        .defaultOffset(0)
        .likePrefix("%")
        .likeSuffix("%")
        .build()
);
```

---

## Per-query override

Use `.withDefaults()` on any builder to override the global configuration for
that one query. Any explicit value you set on the builder (such as `.limit()`)
always beats the defaults.

```java
// Override to use SQLite for this query only
SqlResult result = new QueryBuilder()
    .withDefaults(
        QueryBuilderDefaults.builder(QueryBuilderDefaults.global())
            .dialect(SqlDialect.SQLITE)
            .build()
    )
    .from("users")
    .buildSql();
// SELECT * FROM "users"
```

{: .note }
> `QueryBuilderDefaults.builder(source)` copies all settings from an existing
> instance so you only need to override the fields you want to change.

---

## Explicit dialect argument always wins

Passing a dialect directly to `buildSql()` or `build(SqlDialect)` takes
precedence over both the global defaults and any `.withDefaults()` setting.

```java
QueryBuilderDefaults.setGlobal(
    QueryBuilderDefaults.builder().dialect(SqlDialect.MYSQL).build()
);

// Explicit argument beats the global setting
SqlResult result = new QueryBuilder()
    .from("users")
    .buildSql("users", SqlDialect.SQLITE);
// Uses SQLite quoting regardless of the global setting
```

---

## Custom LIKE wrapping

`whereLike` wraps the value with `%` on both sides by default. Change the
wrapping globally or per query:

```java
// No wrapping (exact LIKE match)
QueryBuilderDefaults.builder()
    .likePrefix("")
    .likeSuffix("")
    .build();

// Suffix-only (starts-with search)
QueryBuilderDefaults.builder()
    .likePrefix("")
    .likeSuffix("%")
    .build();
```

---

## Restoring defaults

`QueryBuilderDefaults.builder()` always starts from the canonical
out-of-the-box values. To reset the global configuration:

```java
QueryBuilderDefaults.setGlobal(QueryBuilderDefaults.builder().build());
```

---

## API summary

### `QueryBuilderDefaults` (static methods)

| Method | Returns | Description |
| -------- | --------- | ------------- |
| `global()` | `QueryBuilderDefaults` | The current JVM-wide defaults instance |
| `setGlobal(defaults)` | `void` | Replace the JVM-wide defaults; throws `NullPointerException` if `null` |
| `builder()` | `Builder` | New builder pre-filled with canonical defaults |
| `builder(source)` | `Builder` | New builder copied from `source`; throws `NullPointerException` if `null` |

### `QueryBuilderDefaults` (instance getters)

| Method | Returns | Description |
| -------- | --------- | ------------- |
| `getDialect()` | `SqlDialect` | The configured SQL dialect |
| `getDefaultColumns()` | `String` | Default SELECT column expression |
| `getDefaultLimit()` | `int` | Default LIMIT value; `-1` means none |
| `getDefaultOffset()` | `int` | Default OFFSET value; `-1` means none |
| `getLikePrefix()` | `String` | Prefix for LIKE values |
| `getLikeSuffix()` | `String` | Suffix for LIKE values |

### `QueryBuilderDefaults.Builder`

| Method | Returns | Description |
| -------- | --------- | ------------- |
| `dialect(SqlDialect)` | `Builder` | Set dialect; throws `NullPointerException` if `null` |
| `defaultColumns(String)` | `Builder` | Set default SELECT columns; throws `NullPointerException` if `null` |
| `defaultLimit(int)` | `Builder` | Set default LIMIT; pass `-1` to disable |
| `defaultOffset(int)` | `Builder` | Set default OFFSET; pass `-1` to disable |
| `likePrefix(String)` | `Builder` | Set LIKE prefix; throws `NullPointerException` if `null` |
| `likeSuffix(String)` | `Builder` | Set LIKE suffix; throws `NullPointerException` if `null` |
| `build()` | `QueryBuilderDefaults` | Build the immutable configuration object |

### Builders that support `withDefaults()`

All three builders throw `NullPointerException` if `null` is passed.

| Builder | Method signature |
| --------- | ----------------- |
| `QueryBuilder` | `withDefaults(QueryBuilderDefaults defaults)` |
| `SelectBuilder` | `withDefaults(QueryBuilderDefaults defaults)` |
| `DeleteBuilder` | `withDefaults(QueryBuilderDefaults defaults)` |
