---
title: In-Memory Filtering
nav_order: 9
description: "Filtering in-memory collections with QueryableStorage"
---

# In-Memory Filtering
{: .no_toc }

## Table of contents
{: .no_toc .text-delta }

1. TOC
{:toc}

---

## Overview

`QueryableStorage` is a functional interface that lets you apply the same
`Query` object you would pass to a SQL database to an in-memory collection
instead. This is useful for unit testing, local caching layers, or any scenario
where you want a consistent filtering API regardless of the backing store.

```java
@FunctionalInterface
public interface QueryableStorage {
    List<String> query(Query q) throws Exception;
}
```

The interface intentionally returns `List<String>` (string IDs) so that the
caller controls how records are loaded by ID after filtering.

---

## How it works

Each `ConditionEntry` in the `Query` holds a `Condition` with an `Operator` and
a value. `Condition.matches(Map<String, Object>, String key)` evaluates the
condition against an attribute map. No SQL dialect or database connection is
required.

The `QueryableStorage` implementation is responsible for:

1. Iterating over the local collection.
2. Calling `condition.matches(attributes, column)` for each item.
3. Respecting `AND` / `OR` connectors between conditions.
4. Applying `ORDER BY`, `LIMIT`, and `OFFSET` if desired.
5. Returning the IDs of matching records.

---

## Example implementation

```java
import com.github.ezframework.javaquerybuilder.query.Query;
import com.github.ezframework.javaquerybuilder.query.QueryableStorage;
import com.github.ezframework.javaquerybuilder.query.condition.ConditionEntry;
import com.github.ezframework.javaquerybuilder.query.condition.Connector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InMemoryStore implements QueryableStorage {

    private final Map<String, Map<String, Object>> data;

    public InMemoryStore(Map<String, Map<String, Object>> data) {
        this.data = data;
    }

    @Override
    public List<String> query(Query q) {
        final List<String> result = new ArrayList<>();

        for (Map.Entry<String, Map<String, Object>> entry : data.entrySet()) {
            final String id          = entry.getKey();
            final Map<String, Object> attrs = entry.getValue();

            if (matches(attrs, q.getConditions())) {
                result.add(id);
            }
        }

        // Respect LIMIT / OFFSET
        final int offset = q.getOffset() != null ? q.getOffset() : 0;
        final int limit  = q.getLimit()  != null ? q.getLimit()  : 0;

        final List<String> sliced = result.subList(
            Math.min(offset, result.size()),
            result.size()
        );
        return limit > 0
            ? sliced.subList(0, Math.min(limit, sliced.size()))
            : sliced;
    }

    private boolean matches(Map<String, Object> attrs, List<ConditionEntry> conditions) {
        if (conditions.isEmpty()) {
            return true;
        }
        boolean result = true;
        for (int i = 0; i < conditions.size(); i++) {
            final ConditionEntry entry = conditions.get(i);
            final boolean condResult =
                entry.getCondition().matches(attrs, entry.getColumn());

            if (i == 0) {
                result = condResult;
            } else if (entry.getConnector() == Connector.OR) {
                result = result || condResult;
            } else {
                result = result && condResult;
            }
        }
        return result;
    }
}
```

---

## Using the store

```java
Map<String, Map<String, Object>> data = Map.of(
    "1", Map.of("name", "Alice", "role", "admin",  "active", true),
    "2", Map.of("name", "Bob",   "role", "user",   "active", true),
    "3", Map.of("name", "Carol", "role", "admin",  "active", false)
);

QueryableStorage store = new InMemoryStore(data);

Query q = new QueryBuilder()
    .whereEquals("role", "admin")
    .whereEquals("active", true)
    .build();

List<String> ids = store.query(q);
// → ["1"]  (Alice: admin + active)
```

---

## Supported operators in-memory

`Condition.matches` evaluates the following operators against an attribute map:

| Operator | In-memory behaviour |
|----------|---------------------|
| `EQ` | `Objects.equals(stored, value)` |
| `NEQ` | `!Objects.equals(stored, value)` |
| `GT` / `GTE` / `LT` / `LTE` | Numeric comparison; coerces `Long`/`Integer`/`Double` as needed |
| `LIKE` | `stored.toString().contains(value)` (substring match) |
| `NOT_LIKE` | Negated `LIKE` |
| `IS_NULL` | `!map.containsKey(key) \|\| map.get(key) == null` |
| `IS_NOT_NULL` / `EXISTS` | `map.containsKey(key) && map.get(key) != null` |
| `IN` | `((List<?>) value).contains(stored)` |
| `NOT_IN` | `!((List<?>) value).contains(stored)` |
| `BETWEEN` | `stored >= list.get(0) && stored <= list.get(1)` |

Subquery operators (`EXISTS_SUBQUERY`, `NOT_EXISTS_SUBQUERY`) are not meaningful
in an in-memory context and return `false` by default.
