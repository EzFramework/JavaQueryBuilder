# JavaQueryBuilder

A lightweight, fluent Java library for building parameterized SQL queries and filtering in-memory data, no runtime dependencies required.

## Features

- Fluent, readable builder API for SELECT, INSERT, UPDATE, DELETE, and CREATE TABLE
- All values are parameterized, safe from SQL injection by design
- Supports all common operators: `=`, `!=`, `>`, `>=`, `<`, `<=`, `LIKE`, `NOT LIKE`, `IN`, `NOT IN`, `BETWEEN`, `IS NULL`, `IS NOT NULL`
- Column selection, `DISTINCT`, `GROUP BY`, `ORDER BY`, `LIMIT`, and `OFFSET`
- SQL dialect support: Standard, MySQL, SQLite
- In-memory filtering via `QueryableStorage`
- Zero runtime dependencies, pure Java 21+

## Installation

Add the dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.github.EzFramework</groupId>
    <artifactId>JavaQueryBuilder</artifactId>
    <version>1.0.4</version>
</dependency>
```

Add the repository of Jitpack to your `pom.xml`:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

## Quick Start

### SELECT

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

String sql          = result.getSql();        // parameterized SQL string
List<Object> params = result.getParameters(); // bound values

// sql    → "SELECT id, name, email FROM users WHERE status = ? AND name LIKE ? AND age > ? ORDER BY name ASC LIMIT 25 OFFSET 50"
// params → ["active", "%alice%", 18]
```

Pass directly to a JDBC `PreparedStatement`:

```java
PreparedStatement stmt = connection.prepareStatement(result.getSql());
List<Object> params = result.getParameters();
for (int i = 0; i < params.size(); i++) {
    stmt.setObject(i + 1, params.get(i));
}
ResultSet rs = stmt.executeQuery();
```

### INSERT

```java
SqlResult result = QueryBuilder.insertInto("users")
    .value("name", "Alice")
    .value("email", "alice@example.com")
    .value("age", 30)
    .build();

// sql    → "INSERT INTO users (name, email, age) VALUES (?, ?, ?)"
// params → ["Alice", "alice@example.com", 30]
```

### UPDATE

```java
SqlResult result = QueryBuilder.update("users")
    .set("status", "inactive")
    .set("updated_at", "2026-04-16")
    .whereEquals("id", 42)
    .build();

// sql    → "UPDATE users SET status = ?, updated_at = ? WHERE id = ?"
// params → ["inactive", "2026-04-16", 42]
```

### DELETE

```java
SqlResult result = QueryBuilder.deleteFrom("sessions")
    .whereEquals("user_id", 42)
    .whereLessThan("expires_at", "2026-01-01")
    .build();

// sql    → "DELETE FROM sessions WHERE user_id = ? AND expires_at < ?"
// params → [42, "2026-01-01"]
```

### CREATE TABLE

```java
SqlResult result = QueryBuilder.createTable("users")
    .column("id", "INT")
    .column("name", "VARCHAR(255)")
    .column("email", "VARCHAR(255)")
    .primaryKey("id")
    .ifNotExists()
    .build();

// sql → "CREATE TABLE IF NOT EXISTS users (id INT, name VARCHAR(255), email VARCHAR(255), PRIMARY KEY (id))"
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

| Builder method | SQL clause | Available on |
|---|---|---|
| `whereEquals(col, val)` | `col = ?` | QueryBuilder, DeleteBuilder, UpdateBuilder, SelectBuilder |
| `orWhereEquals(col, val)` | `OR col = ?` | QueryBuilder, UpdateBuilder |
| `whereNotEquals(col, val)` | `col != ?` | QueryBuilder, DeleteBuilder |
| `whereGreaterThan(col, val)` | `col > ?` | QueryBuilder, DeleteBuilder |
| `whereGreaterThanOrEquals(col, val)` | `col >= ?` | QueryBuilder, DeleteBuilder, UpdateBuilder |
| `whereLessThan(col, val)` | `col < ?` | QueryBuilder, DeleteBuilder |
| `whereLessThanOrEquals(col, val)` | `col <= ?` | QueryBuilder, DeleteBuilder |
| `whereLike(col, substr)` | `col LIKE ?` (wrapped with `%`) | QueryBuilder, SelectBuilder |
| `whereNotLike(col, substr)` | `col NOT LIKE ?` (wrapped with `%`) | QueryBuilder |
| `whereExists(col)` | `col IS NOT NULL` | QueryBuilder |
| `whereNull(col)` | `col IS NULL` | QueryBuilder |
| `whereNotNull(col)` | `col IS NOT NULL` | QueryBuilder |
| `whereIn(col, list)` | `col IN (?, ?, ...)` | QueryBuilder, DeleteBuilder, SelectBuilder |
| `whereNotIn(col, list)` | `col NOT IN (?, ?, ...)` | QueryBuilder, DeleteBuilder |
| `whereBetween(col, a, b)` | `col BETWEEN ? AND ?` | QueryBuilder, DeleteBuilder |

## Builder Reference

### QueryBuilder (SELECT)

`QueryBuilder` is the main entry point for SELECT queries. It also provides static factory methods for all other statement types.

```java
new QueryBuilder()
    // --- column selection (optional, defaults to *) ---
    .select("id", "name", "created_at")
    .distinct()                             // adds DISTINCT

    // --- conditions (all joined with AND unless or* variant used) ---
    .whereEquals("status", "active")        // status = ?
    .orWhereEquals("status", "pending")     // OR status = ?
    .whereNotEquals("role", "banned")       // role != ?
    .whereGreaterThan("age", 18)            // age > ?
    .whereGreaterThanOrEquals("age", 18)    // age >= ?
    .whereLessThan("score", 1000)           // score < ?
    .whereLessThanOrEquals("score", 1000)   // score <= ?
    .whereLike("username", "john")          // username LIKE '%john%'
    .whereNotLike("email", "spam")          // email NOT LIKE '%spam%'
    .whereExists("verified_at")             // verified_at IS NOT NULL
    .whereNull("deleted_at")               // deleted_at IS NULL
    .whereNotNull("email")                 // email IS NOT NULL
    .whereIn("country", List.of("US", "CA"))
    .whereNotIn("role", List.of("bot", "banned"))
    .whereBetween("created_at", from, to)

    // --- sorting and grouping ---
    .groupBy("department")
    .havingRaw("COUNT(*) > 5")
    .orderBy("created_at", false)           // false = DESC
    .orderBy("name", true)                  // true  = ASC

    // --- pagination ---
    .limit(25)
    .offset(50)

    // --- output ---
    .from("users")                          // sets table for build() / no-arg buildSql()
    .build()                                // → Query  (for in-memory use)
    .buildSql("users")                      // → SqlResult (for JDBC use)
    .buildSql("users", SqlDialect.MYSQL);   // → SqlResult with dialect quoting
```

### Static Factory Methods

All DML/DDL builders are accessible from `QueryBuilder` without needing to import the individual classes:

```java
// INSERT
QueryBuilder.insert()                      // new InsertBuilder
QueryBuilder.insertInto("tableName")       // new InsertBuilder pre-configured with table

// UPDATE
QueryBuilder.update()                      // new UpdateBuilder
QueryBuilder.update("tableName")           // new UpdateBuilder pre-configured with table

// DELETE
QueryBuilder.delete()                      // new DeleteBuilder
QueryBuilder.deleteFrom("tableName")       // new DeleteBuilder pre-configured with table

// CREATE TABLE
QueryBuilder.createTable()                 // new CreateBuilder
QueryBuilder.createTable("tableName")      // new CreateBuilder pre-configured with table
```

### InsertBuilder

```java
QueryBuilder.insertInto("users")
    .value("name", "Alice")
    .value("email", "alice@example.com")
    .build();                              // → SqlResult
```

### UpdateBuilder

```java
QueryBuilder.update("users")
    .set("name", "Alice")
    .set("status", "active")
    .whereEquals("id", 1)
    .orWhereEquals("email", "alice@example.com")
    .whereGreaterThanOrEquals("age", 18)
    .build();                              // → SqlResult
```

### DeleteBuilder

```java
QueryBuilder.deleteFrom("users")
    .whereEquals("id", 1)
    .whereNotEquals("role", "admin")
    .whereGreaterThan("age", 0)
    .whereGreaterThanOrEquals("score", 10)
    .whereLessThan("attempts", 5)
    .whereLessThanOrEquals("balance", 0)
    .whereIn("status", List.of("expired", "banned"))
    .whereNotIn("plan", List.of("premium", "trial"))
    .whereBetween("created_at", from, to)
    .build();                              // → SqlResult
```

### CreateBuilder

```java
QueryBuilder.createTable("orders")
    .column("id", "BIGINT")
    .column("user_id", "INT")
    .column("total", "DECIMAL(10,2)")
    .column("created_at", "TIMESTAMP")
    .primaryKey("id")
    .ifNotExists()
    .build();                              // → SqlResult
```

### SelectBuilder

`SelectBuilder` provides a standalone, self-contained SELECT builder (no `Query` object):

```java
new SelectBuilder()
    .from("users")
    .select("id", "name")
    .distinct()
    .whereEquals("active", true)
    .whereIn("role", List.of("admin", "editor"))
    .whereLike("name", "alice")
    .groupBy("department")
    .orderBy("name", true)
    .limit(10)
    .offset(20)
    .build();                              // → SqlResult
```

## SQL Dialects

By default, `buildSql(table)` uses `SqlDialect.STANDARD` (no identifier quoting). Pass a second argument to use a different dialect:

```java
// Standard SQL (default)
SqlResult result = new QueryBuilder()
    .whereEquals("status", "active")
    .buildSql("users");
// → SELECT * FROM users WHERE status = ?

// MySQL
SqlResult result = new QueryBuilder()
    .select("id", "name")
    .whereEquals("status", "active")
    .buildSql("users", SqlDialect.MYSQL);
// → SELECT `id`, `name` FROM `users` WHERE `status` = ?

// SQLite
SqlResult result = new QueryBuilder()
    .whereEquals("status", "active")
    .buildSql("users", SqlDialect.SQLITE);
// → SELECT * FROM "users" WHERE "status" = ?
```

| Dialect | Identifier quoting | Boolean values | DELETE LIMIT |
|---|---|---|---|
| `SqlDialect.STANDARD` | none | `true` / `false` | no |
| `SqlDialect.MYSQL` | back-ticks `` `col` `` | `true` / `false` | yes |
| `SqlDialect.SQLITE` | double quotes `"col"` | `1` / `0` | yes |

MySQL back-tick quoting safely handles reserved words and case-sensitive identifiers. SQLite double-quote quoting serves the same purpose, and Java booleans are converted to `1`/`0` to match SQLite's integer-backed boolean storage.

### Dialect-aware DELETE rendering

Every `SqlDialect` exposes a `renderDelete(Query query)` method that generates a fully dialect-aware `DELETE FROM ... WHERE ...` statement, identifier quoting and all. MySQL and SQLite also append a `LIMIT` clause when one is set on the query; the standard dialect silently ignores it.

```java
// Obtain a Query from any QueryBuilder call
Query query = new QueryBuilder()
    .from("users")
    .whereEquals("id", 42)
    .limit(1)
    .build();

// Standard — no quoting, no LIMIT
SqlDialect.STANDARD.renderDelete(query);
// → DELETE FROM users WHERE id = ?   params=[42]

// MySQL — back-tick quoting + LIMIT
SqlDialect.MYSQL.renderDelete(query);
// → DELETE FROM `users` WHERE `id` = ? LIMIT 1   params=[42]

// SQLite — double-quote quoting + LIMIT
SqlDialect.SQLITE.renderDelete(query);
// → DELETE FROM "users" WHERE "id" = ? LIMIT 1   params=[42]
```

## How SQL Generation Works

`buildSql(table)` (or `query.toSql(table)`) translates the `Query` into a single-line parameterized SQL string. All values are returned separately as a `List<Object>` — the SQL string itself only contains `?` placeholders, so **user-supplied values are never interpolated into the string**. This makes it inherently safe against SQL injection when used with a `PreparedStatement`.

`LIKE` values are automatically wrapped with `%` on both sides so `whereLike("name", "alice")` becomes `name LIKE ?` with parameter `%alice%`.


## License

MIT