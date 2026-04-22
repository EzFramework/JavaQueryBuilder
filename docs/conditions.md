---
title: Conditions
nav_order: 4
description: "Operators, Condition, ConditionEntry, Connector AND/OR, and the orWhere* pattern"
---

# Conditions
{: .no_toc }

## Table of contents
{: .no_toc .text-delta }

1. TOC
{:toc}

---

## Overview

Every `where*` call on a builder creates a `ConditionEntry` with three properties:

- a **column name** (or `null` for EXISTS subquery conditions)
- a **`Condition`** (operator + value)
- a **`Connector`** (`AND` or `OR`)

The first condition in a builder always uses `AND` as its connector. Subsequent
conditions use `AND` by default; call the `orWhere*` variant to use `OR`.

---

## Operator enum

`Operator` is the single source of truth for all supported comparison operators.

| Constant | SQL rendering | Notes |
| ---------- | --------------- | ------- |
| `EQ` | `col = ?` | Equality |
| `NEQ` | `col != ?` | Not equal |
| `GT` | `col > ?` | Greater than |
| `GTE` | `col >= ?` | Greater than or equal |
| `LT` | `col < ?` | Less than |
| `LTE` | `col <= ?` | Less than or equal |
| `LIKE` | `col LIKE ?` | Substring match |
| `NOT_LIKE` | `col NOT LIKE ?` | Negated substring match |
| `EXISTS` | `col IS NOT NULL` | Column existence check (alias for `IS_NOT_NULL`) |
| `IS_NULL` | `col IS NULL` | Column is null |
| `IS_NOT_NULL` | `col IS NOT NULL` | Column is not null |
| `IN` | `col IN (...)` | Collection membership; value is a `List<?>` or a `Query` subquery |
| `NOT_IN` | `col NOT IN (...)` | Negated collection membership |
| `BETWEEN` | `col BETWEEN ? AND ?` | Inclusive range; value is a two-element `List<?>` |
| `EXISTS_SUBQUERY` | `EXISTS (SELECT ...)` | Value is a `Query`; column is `null` |
| `NOT_EXISTS_SUBQUERY` | `NOT EXISTS (SELECT ...)` | Value is a `Query`; column is `null` |
| `ILIKE` | `col ILIKE ?` | Case-insensitive substring match — **PostgreSQL only** |
| `NOT_ILIKE` | `col NOT ILIKE ?` | Negated case-insensitive match — **PostgreSQL only** |

---

## Builder method to operator mapping

The table below maps every `QueryBuilder` `where*` method to its `Operator`
constant and the SQL it generates.

| Builder method | Operator | Generated SQL fragment |
| ---------------- | ---------- | ------------------------ |
| `whereEquals(col, val)` | `EQ` | `col = ?` |
| `orWhereEquals(col, val)` | `EQ` | `OR col = ?` |
| `whereNotEquals(col, val)` | `NEQ` | `col != ?` |
| `whereGreaterThan(col, val)` | `GT` | `col > ?` |
| `whereGreaterThanOrEquals(col, val)` | `GTE` | `col >= ?` |
| `whereLessThan(col, val)` | `LT` | `col < ?` |
| `whereLessThanOrEquals(col, val)` | `LTE` | `col <= ?` |
| `whereLike(col, val)` | `LIKE` | `col LIKE ?` |
| `whereNotLike(col, val)` | `NOT_LIKE` | `col NOT LIKE ?` |
| `whereILike(col, val)` | `ILIKE` | `col ILIKE ?` (PostgreSQL only) |
| `orWhereILike(col, val)` | `ILIKE` | `OR col ILIKE ?` (PostgreSQL only) |
| `whereNull(col)` | `IS_NULL` | `col IS NULL` |
| `whereNotNull(col)` | `IS_NOT_NULL` | `col IS NOT NULL` |
| `whereExists(col)` | `EXISTS` | `col IS NOT NULL` |
| `whereIn(col, List<?>)` | `IN` | `col IN (?, ?, ...)` |
| `whereNotIn(col, List<?>)` | `NOT_IN` | `col NOT IN (?, ?, ...)` |
| `whereBetween(col, a, b)` | `BETWEEN` | `col BETWEEN ? AND ?` |
| `whereInSubquery(col, Query)` | `IN` | `col IN (SELECT ...)` |
| `whereEqualsSubquery(col, Query)` | `EQ` | `col = (SELECT ...)` |
| `whereExistsSubquery(Query)` | `EXISTS_SUBQUERY` | `EXISTS (SELECT ...)` |
| `whereNotExistsSubquery(Query)` | `NOT_EXISTS_SUBQUERY` | `NOT EXISTS (SELECT ...)` |

---

## AND vs OR connector

### Default behavior (AND)

All `where*` methods use `AND`:

```java
new QueryBuilder()
    .from("users")
    .whereEquals("role", "admin")
    .whereEquals("active", true)
// → WHERE role = ? AND active = ?
```

### OR conditions

```java
new QueryBuilder()
    .from("users")
    .whereEquals("role", "admin")
    .orWhereEquals("role", "moderator")
// → WHERE role = ? OR role = ?
```

### Mixing AND and OR

Conditions are rendered in the order they are added. There is no explicit
grouping with parentheses at the builder level.

```java
new QueryBuilder()
    .from("users")
    .whereEquals("active", true)
    .whereEquals("role", "admin")
    .orWhereEquals("role", "moderator")
// → WHERE active = ? AND role = ? OR role = ?
```

---

## Condition class

`Condition` pairs an `Operator` with its comparison value.

| Member | Type | Description |
| -------- | ------ | ------------- |
| `Condition(Operator op, Object value)` | constructor | Create a condition; `value` may be `null` for `IS_NULL`, `IS_NOT_NULL`, `EXISTS` |
| `getOperator()` | `Operator` | The operator for this condition |
| `getValue()` | `Object` | The comparison value (`null`, scalar, `List<?>`, or `Query`) |
| `matches(Map<String,Object> map, String key)` | `boolean` | Evaluate against an in-memory attribute map |

The `matches` method is used by `QueryableStorage` for in-memory filtering
without a database.

---

## ConditionEntry class

`ConditionEntry` wraps a `Condition` with its column name and `Connector`.

| Member | Type | Description |
| -------- | ------ | ------------- |
| `ConditionEntry(String column, Condition condition, Connector connector)` | constructor | Create a condition entry |
| `getColumn()` | `String` | The column name (`null` for `EXISTS_SUBQUERY` / `NOT_EXISTS_SUBQUERY`) |
| `getCondition()` | `Condition` | The wrapped condition |
| `getConnector()` | `Connector` | `AND` or `OR` |

---

## Connector enum

| Constant | SQL keyword |
| ---------- | ------------- |
| `AND` | `AND` |
| `OR` | `OR` |
