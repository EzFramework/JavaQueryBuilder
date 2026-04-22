---
title: Queries
nav_order: 3
has_children: true
permalink: /queries/
description: "Building SQL queries with JavaQueryBuilder's fluent API"
---

# Queries
{: .no_toc }

`QueryBuilder` is the entry point for all statement types. It provides a fluent
API for SELECT queries on its instance, and static factory methods that return
dedicated builder objects for DML and DDL statements.

---

## Statement types

| Statement | Builder | Entry point |
|-----------|---------|-------------|
| [SELECT]({{ site.baseurl }}/queries/select/) | `QueryBuilder` | `new QueryBuilder()` |
| [INSERT]({{ site.baseurl }}/queries/insert/) | `InsertBuilder` | `QueryBuilder.insertInto(table)` |
| [UPDATE]({{ site.baseurl }}/queries/update/) | `UpdateBuilder` | `QueryBuilder.update(table)` |
| [DELETE]({{ site.baseurl }}/queries/delete/) | `DeleteBuilder` | `QueryBuilder.deleteFrom(table)` |
| [CREATE TABLE]({{ site.baseurl }}/queries/create/) | `CreateBuilder` | `QueryBuilder.createTable(table)` |

---

## SqlResult

Every builder returns a `SqlResult` from its `build()` or `buildSql()` method:

| Method | Returns | Description |
|--------|---------|-------------|
| `getSql()` | `String` | The rendered SQL with `?` placeholders |
| `getParameters()` | `List<Object>` | Bind parameters in order of appearance |

All user-supplied values are placed in the `?` bind-parameter list and are
**never** concatenated into the SQL string. See
[SQL Dialects]({{ site.baseurl }}/sql-dialects/) for how identifier quoting
behaves across database targets.
