# JavaQueryBuilder – Project Guidelines

## Code Style

Enforced by Checkstyle ([checkstyle.xml](checkstyle.xml)) — runs automatically on `mvn validate`. Violations fail the build.

- **Indentation**: 4 spaces; no tabs
- **Line length**: max 120 characters
- **Trailing whitespace**: none allowed
- **Imports**: grouped (`java`, `javax`, `org`, `com`), no star imports, ordered and separated by blank line
- **Braces**: required on all blocks (`NeedBraces`), same-line opening (`LeftCurly`)
- **Complexity**: cyclomatic complexity ≤ 10 per method; method body ≤ 60 lines; class fan-out ≤ 20
- **Magic numbers**: flag literals other than `-1, 0, 1, 2` outside field declarations and annotations
- **`FinalLocalVariable`**: declare local variables `final` whenever they are not reassigned
- Test classes (`*Test.java`): Javadoc and complexity rules are suppressed via [suppressions.xml](suppressions.xml)

### JavaDoc Requirements

All public API classes, interfaces, enums, and their methods must have complete Javadoc:

```java
/**
 * One clear summary sentence ending with a period.
 *
 * @param column the column name to filter on
 * @param value  the value to compare against
 * @return this builder instance for chaining
 * @throws IllegalArgumentException if column is null or blank
 */
public QueryBuilder whereEquals(String column, Object value) { … }
```

- `@author` and `@version` required on type-level Javadoc
- `@param` required for every parameter, `@return` for every non-void method
- `@throws` required for every checked and declared unchecked exception
- First sentence must be a meaningful summary — no generic openers like `"A {@code X} is a …"`

## Architecture

Package `com.github.ezframework.javaquerybuilder.query`:

| Class / Type | Role |
|---|---|
| `QueryBuilder` | Fluent builder for SELECT queries; returns a `Query` |
| `DeleteBuilder`, `InsertBuilder`, `UpdateBuilder` | Fluent builders for DML statements |
| `Query` | Immutable data holder (columns, conditions, limit, offset, …) |
| `Condition` / `ConditionEntry` | Per-field condition (operator + value) with in-memory match support |
| `Operator` | Enum of 14 comparison operators (`EQ`, `LIKE`, `BETWEEN`, …) |
| `Connector` | `AND` / `OR` enum for joining conditions |
| `sql.SqlDialect` | Strategy: `STANDARD`, `MYSQL`, `SQLITE` |
| `sql.AbstractSqlDialect` | Base SQL rendering logic |

**Key conventions**:
- Builder methods **always return `this`** (or the builder type) for fluent chaining.
- All SQL values must go through the parameterized SQL path — never concatenate user input into SQL strings.
- `Operator` is the single source of truth for supported operators; add new operators there first.

## Build and Test

```bash
# Full build: checkstyle → compile → test → JaCoCo report → coverage check → package
mvn clean verify

# Checkstyle only
mvn checkstyle:check

# Tests only (skips coverage threshold)
mvn test

# Coverage report (generated after mvn verify or mvn test)
open target/site/jacoco/index.html
```

**Coverage target**: 80% instruction coverage enforced by JaCoCo on `mvn verify`. New code must maintain or improve coverage.

## Conventions

- Fluent builder methods use verb prefixes: `where*`, `orderBy`, `groupBy`, `select`, `from`
- `OR` variants are named `orWhere*` — the first condition in a builder always uses `AND` as connector
- Exception hierarchy: `QueryBuilderException` → `QueryException` / `QueryRenderException` in `query.exception`
- Test class names match source class names with `Test` suffix and live in the same sub-package under `src/test/`

## Packaging and Distribution

- **GitHub Packages**: published automatically on GitHub Release creation via [.github/workflows/publish.yml](.github/workflows/publish.yml)
- **JitPack**: available at `https://jitpack.io/#EzFramework/JavaQueryBuilder`; build config in [jitpack.yml](jitpack.yml)
- Both `-sources.jar` and `-javadoc.jar` are attached to every release
