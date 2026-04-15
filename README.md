# JavaQueryBuilder

![CI](https://github.com/${{github.repository}}/actions/workflows/ci.yml/badge.svg)

This project uses GitHub Actions for continuous integration. See `.github/workflows/ci.yml` for details.

A lightweight, fluent Java library for building SQL queries and filtering in-memory data — no runtime dependencies required.

## Features

- Fluent, readable builder API
- Generates parameterized SQL `SELECT` statements (safe from SQL injection)
- Supports all common operators: `=`, `!=`, `>`, `<`, `LIKE`, `IN`, `BETWEEN`, `IS NOT NULL`
- Column selection, `GROUP BY`, `ORDER BY`, `LIMIT`, and `OFFSET`
- In-memory filtering via `QueryableStorage`
- Zero runtime dependencies — pure Java 21

## Installation

Add the dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.github.EzFramework</groupId>
    <artifactId>java-query-builder</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Quick Start

### Generating SQL

```java
SqlResult result = new QueryBuilder()
    .select("id", "name", "email")
    .whereEquals("status", "active")
    .whereLike("name", "alice")
    .whereGreaterThan("age", 18)
    .orderBy("name", true)
    .limit(25)
    .offset(50)
    .buildSql("users");

String sql    = result.getSql();        // the parameterized SQL string
List<Object> params = result.getParameters(); // the bound values

// sql     → "SELECT id, name, email FROM users WHERE status = ? AND name LIKE ? AND age > ? ORDER BY name ASC LIMIT 25 OFFSET 50"
// params  → ["active", "%alice%", 18]
```

Pass `sql` and `params` directly to a JDBC `PreparedStatement`:

```java
PreparedStatement stmt = connection.prepareStatement(result.getSql());
List<Object> params = result.getParameters();
for (int i = 0; i < params.size(); i++) {
    stmt.setObject(i + 1, params.get(i));
}
ResultSet rs = stmt.executeQuery();
```

### In-Memory Filtering

Implement `QueryableStorage` on your storage class to enable query-based lookups without a database:

```java
public class UserStore implements QueryableStorage {
    private final Map<String, Map<String, Object>> rows = new HashMap<>();

    @Override
    public List<String> query(Query q) {
        return rows.entrySet().stream()
            .filter(e -> q.getConditions().entrySet().stream()
                .allMatch(c -> c.getValue().matches(e.getValue(), c.getKey())))
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }
}
```

## All Operators

| Builder method | SQL clause | In-memory |
|---|---|---|
| `whereEquals(col, val)` | `col = ?` | `col.equals(val)` |
| `whereNotEquals(col, val)` | `col != ?` | `!col.equals(val)` |
| `whereGreaterThan(col, val)` | `col > ?` | `col > val` |
| `whereLessThan(col, val)` | `col < ?` | `col < val` |
| `whereLike(col, substr)` | `col LIKE ?` (value wrapped with `%`) | `col.contains(substr)` |
| `whereExists(col)` | `col IS NOT NULL` | `map.containsKey(col)` |
| `whereIn(col, collection)` | `col IN (?, ?, ...)` | `collection.contains(col)` |
| `whereBetween(col, a, b)` | `col BETWEEN ? AND ?` | `a <= col <= b` |

## Full Builder Reference

```java
new QueryBuilder()
    // --- column selection (optional, defaults to *) ---
    .select("id", "name", "created_at")

    // --- conditions ---
    .whereEquals("status", "active")
    .whereNotEquals("role", "banned")
    .whereGreaterThan("age", 18)
    .whereLessThan("score", 1000)
    .whereLike("username", "john")          // SQL: username LIKE '%john%'
    .whereExists("email")                   // SQL: email IS NOT NULL
    .whereIn("country", List.of("US", "CA", "GB"))
    .whereBetween("created_at", startDate, endDate)

    // --- sorting and grouping ---
    .groupBy("department")
    .orderBy("created_at", false)           // false = DESC
    .orderBy("name", true)                  // true  = ASC

    // --- pagination ---
    .limit(25)
    .offset(50)

    // --- output ---
    .build()                                // → Query  (for in-memory use)
    .buildSql("users");                     // → SqlResult (for JDBC use)
```

## SQL Dialects

By default, `buildSql(table)` uses `SqlDialect.STANDARD` (no identifier quoting). Pass a second argument to use a different dialect:

```java
// Standard SQL
SqlResult sql = new QueryBuilder()
    .whereEquals("status", "active")
    .buildSql("users");                        // SqlDialect.STANDARD

// SQLite
SqlResult sql = new QueryBuilder()
    .whereEquals("status", "active")
    .buildSql("users", SqlDialect.SQLITE);
// → SELECT * FROM "users" WHERE "status" = ?
```

| Dialect | `SqlDialect.STANDARD` | `SqlDialect.SQLITE` |
|---|---|---|
| Identifier quoting | none | double quotes `"col"` |
| Boolean values | `true` / `false` | `1` / `0` |

SQLite wraps every table and column name in double quotes, which safely handles reserved words and names with special characters. Java booleans are converted to `1`/`0` to match SQLite's integer-backed boolean storage.

## How SQL Generation Works

`buildSql(table)` (or `query.toSql(table)`) translates the `Query` into a single-line parameterized SQL string. All values are returned separately as a `List<Object>` — the SQL string itself only contains `?` placeholders, so **user-supplied values are never interpolated into the string**. This makes it inherently safe against SQL injection when used with a `PreparedStatement`.

`LIKE` values are automatically wrapped with `%` on both sides so `whereLike("name", "alice")` becomes `name LIKE ?` with parameter `%alice%`.



## Project Layout

```
src/main/java/com/skyblockexp/ezframework/query/
├── QueryBuilder.java       Fluent builder — start here
├── Query.java              Query data model; delegates rendering to SqlDialect
├── SqlResult.java          Holds the generated SQL string and parameter list
├── SqlDialect.java         Strategy interface (STANDARD / SQLITE constants)
├── AbstractSqlDialect.java Shared rendering logic — extend to add new dialects
├── StandardSqlDialect.java Standard SQL (no quoting)
├── SqliteDialect.java      SQLite (double-quote identifiers, boolean → 0/1)
├── Condition.java          Single field condition (operator + value)
├── Operator.java           Enum of supported operators
└── QueryableStorage.java   Interface for in-memory query execution
```

## License

MIT

## Test Coverage

This project uses [JaCoCo](https://www.jacoco.org/jacoco/) for test coverage analysis.

### How to Generate Coverage Report

1. Run all tests and generate the coverage report:
   ```sh
   mvn test jacoco:report
   ```
2. Open the HTML report:
   - Open `target/site/jacoco/index.html` in your browser to view detailed coverage metrics.

## Adding Tests
- Add JUnit tests under `src/test/java/com/github/ezframework/javaquerybuilder/query/` and its subpackages.
- Focus on covering all core features, edge cases, and error handling.
