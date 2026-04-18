---
title: Exceptions
nav_order: 9
description: "Exception hierarchy, when each exception is thrown, and handling patterns"
---

# Exceptions
{: .no_toc }

## Table of contents
{: .no_toc .text-delta }

1. TOC
{:toc}

---

## Hierarchy

The library defines three standalone checked exceptions in the
`com.github.ezframework.javaquerybuilder.query.exception` package.
None extends the others — each signals a distinct failure mode.

```text
Exception
  ├── QueryBuilderException  — general builder or configuration error
  ├── QueryException         — query-level runtime error
  └── QueryRenderException   — SQL rendering error
```

All three share the same four constructor signatures.

---

## QueryBuilderException

Thrown for general errors produced during query building or configuration.

```java
try {
    // ... builder usage that may throw
} catch (QueryBuilderException e) {
    log.error("Builder error: {}", e.getMessage(), e);
}
```

**Constructors:**

| Constructor | Use case |
|-------------|----------|
| `QueryBuilderException()` | No-message default |
| `QueryBuilderException(String message)` | Simple message |
| `QueryBuilderException(String message, Throwable cause)` | Wraps another exception |
| `QueryBuilderException(Throwable cause)` | Re-throws without adding a message |

---

## QueryException

Thrown for runtime errors at the query level — for example, when a
`QueryableStorage` implementation encounters an error during in-memory
evaluation.

```java
try {
    List<String> ids = store.query(q);
} catch (QueryException e) {
    log.error("Query evaluation failed: {}", e.getMessage(), e);
}
```

**Constructors:**

| Constructor | Use case |
|-------------|----------|
| `QueryException()` | No-message default |
| `QueryException(String message)` | Simple message |
| `QueryException(String message, Throwable cause)` | Wraps another exception |
| `QueryException(Throwable cause)` | Re-throws without adding a message |

---

## QueryRenderException

Thrown when a `Query` cannot be rendered to SQL — for example, if required
fields are missing or the query state is inconsistent at render time.

```java
try {
    SqlResult result = dialect.render(query);
} catch (QueryRenderException e) {
    log.error("SQL rendering failed: {}", e.getMessage(), e);
}
```

**Constructors:**

| Constructor | Use case |
|-------------|----------|
| `QueryRenderException()` | No-message default |
| `QueryRenderException(String message)` | Simple message |
| `QueryRenderException(String message, Throwable cause)` | Wraps another exception |
| `QueryRenderException(Throwable cause)` | Re-throws without adding a message |

---

## Best practices

### Catch the most specific type first

```java
try {
    SqlResult result = dialect.render(query);
    List<String> ids = store.query(q);
}
catch (QueryRenderException e) {
    // Rendering failed — log and return a safe error response
}
catch (QueryException e) {
    // In-memory evaluation failed
}
catch (QueryBuilderException e) {
    // Configuration or builder error
}
```

### Do not expose raw exception messages to API callers

Exception messages may contain internal column names or values. Map exceptions
to safe, generic responses before returning them to external clients.

```java
// CORRECT — map to a safe API response
catch (QueryRenderException e) {
    return Response.serverError().entity("Query rendering error").build();
}

// WRONG — leaks internal details
catch (QueryRenderException e) {
    return Response.serverError().entity(e.getMessage()).build();
}
```

### `CreateBuilder` throws `IllegalStateException`

`CreateBuilder.build()` throws `IllegalStateException` (not a checked exception)
when the table name or columns are missing. Guard these calls with a null/empty
check before building:

```java
if (table != null && !columns.isEmpty()) {
    SqlResult result = QueryBuilder.createTable(table)
        // ... columns ...
        .build();
}
```

### `DeleteBuilder` throws `IllegalArgumentException`

`DeleteBuilder.whereIn()` and `whereNotIn()` throw `IllegalArgumentException`
when the value list is `null` or empty. Validate the list before calling:

```java
if (ids != null && !ids.isEmpty()) {
    SqlResult result = QueryBuilder.deleteFrom("users")
        .whereIn("id", ids)
        .build();
}
```
