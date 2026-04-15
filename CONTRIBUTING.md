# Contributing to JavaQueryBuilder

## Getting Started

1. Fork and clone the repository
2. Ensure Java 21 and Maven 3.8+ are installed
3. Run `mvn clean verify` — all checks must pass before you start

## Branching

- Branch from `main`
- Use descriptive branch names: `feature/add-join-support`, `fix/between-operator-null`
- Open a pull request against `main`

## Pull Request Requirements

All of the following must pass before a PR is merged:

| Check | Command | Gating |
|---|---|---|
| Checkstyle | runs during `mvn validate` | build fails on any violation |
| Tests | `mvn test` | all tests must pass |
| Coverage | `mvn verify` | ≥ 80% instruction coverage (JaCoCo) |

**Do not use `--forks-enforced` or `-Dcheckstyle.skip` to bypass checks.**

## Code Style

See [AGENTS.md](AGENTS.md) for the full style guide. The short version:

- 4-space indentation, max 120 chars per line, no trailing whitespace
- Full Javadoc on every public class and method (`@param`, `@return`, `@throws`)
- Declare locals `final` when not reassigned
- Cyclomatic complexity ≤ 10; method length ≤ 60 lines

Run `mvn checkstyle:check` locally before pushing.

## Writing Tests

- Place tests in `src/test/…` mirroring the source package
- Test method names should describe the behaviour: `buildsSelectWithDistinct()`, `throwsOnNullColumn()`
- Cover both happy paths and edge cases (null inputs, empty lists, boundary values)
- Aim to keep or improve the 80% instruction coverage threshold

## Adding New Operators

1. Add the value to the `Operator` enum with a Javadoc comment
2. Handle the new operator in `Condition#matches()` for in-memory filtering
3. Handle it in `AbstractSqlDialect` (and dialect subclasses if behaviour differs)
4. Add tests for all three layers

## Releasing

Releases are published to **GitHub Packages** and available via **JitPack** automatically:

1. Draft a new GitHub Release with a semantic version tag (e.g. `v1.1.0`)
2. Publishing the release triggers [`.github/workflows/publish.yml`](.github/workflows/publish.yml)
3. The workflow deploys the JAR, `-sources.jar`, and `-javadoc.jar` to GitHub Packages
4. JitPack picks up the tag and builds automatically from [jitpack.yml](jitpack.yml)

## Reporting Issues

Open a GitHub Issue with a minimal reproducible example. For security issues see the security section in [README.md](README.md).
