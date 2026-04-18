package feature.query;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.github.ezframework.javaquerybuilder.query.JoinClause;
import com.github.ezframework.javaquerybuilder.query.Query;
import com.github.ezframework.javaquerybuilder.query.builder.QueryBuilder;
import com.github.ezframework.javaquerybuilder.query.condition.Condition;
import com.github.ezframework.javaquerybuilder.query.condition.ConditionEntry;
import com.github.ezframework.javaquerybuilder.query.condition.Connector;
import com.github.ezframework.javaquerybuilder.query.condition.Operator;
import com.github.ezframework.javaquerybuilder.query.sql.SqlDialect;
import com.github.ezframework.javaquerybuilder.query.sql.SqlResult;

/**
 * Execution matrix: builds every SQL variant supported by the builders and executes
 * it against an in-memory SQLite database to confirm valid, parseable SQL for every
 * operator / clause combination across SELECT, INSERT, UPDATE, DELETE, and CREATE TABLE.
 *
 * <p>Schema and seed data are committed once in {@link #setUpDatabase()}. All DML test
 * cases run inside a savepoint that is rolled back after the assertion, leaving the
 * database state intact for the full test run.
 */
public class SqlExecutionMatrixTest {

    private static final SqlDialect DB = SqlDialect.SQLITE;

    private static Connection conn;

    @BeforeAll
    static void setUpDatabase() throws SQLException {
        conn = DriverManager.getConnection("jdbc:sqlite::memory:");
        conn.setAutoCommit(false);
        exec("CREATE TABLE users (id INTEGER PRIMARY KEY, name TEXT NOT NULL,"
            + " email TEXT, age INTEGER, score REAL, active INTEGER, status TEXT)");
        exec("CREATE TABLE orders (id INTEGER PRIMARY KEY, user_id INTEGER,"
            + " amount REAL, status TEXT)");
        // Seed: permanent read-only rows used by SELECT tests
        exec("INSERT INTO users VALUES (1,'Alice','alice@example.com',30,150.0,1,'active')");
        exec("INSERT INTO users VALUES (2,'Bob','bob@other.com',25,80.0,0,'inactive')");
        exec("INSERT INTO users VALUES (3,'Carol',NULL,35,200.0,1,'active')");
        exec("INSERT INTO users VALUES (4,'Dave','dave@example.com',22,60.0,1,'active')");
        exec("INSERT INTO users VALUES (5,'Eve','eve@other.com',40,300.0,1,'active')");
        exec("INSERT INTO orders VALUES (1,1,99.50,'shipped')");
        exec("INSERT INTO orders VALUES (2,1,149.00,'pending')");
        exec("INSERT INTO orders VALUES (3,2,55.00,'cancelled')");
        exec("INSERT INTO orders VALUES (4,3,200.00,'shipped')");
        exec("INSERT INTO orders VALUES (5,4,10.00,'pending')");
        // DML workspace rows exclusively targeted by UPDATE/DELETE cases
        exec("INSERT INTO users VALUES (100,'Tmp1',NULL,20,0.0,0,'temp')");
        exec("INSERT INTO users VALUES (101,'Tmp2',NULL,21,0.0,0,'temp')");
        exec("INSERT INTO users VALUES (102,'Tmp3',NULL,22,0.0,0,'temp')");
        exec("INSERT INTO users VALUES (103,'Tmp4',NULL,23,0.0,0,'temp')");
        exec("INSERT INTO orders VALUES (100,100,0.0,'temp')");
        exec("INSERT INTO orders VALUES (101,101,0.0,'temp')");
        conn.commit();
    }

    @AfterAll
    static void closeDatabase() throws SQLException {
        if (conn != null) {
            conn.close();
        }
    }

    // -------------------------------------------------------------------------
    // SELECT matrix
    // -------------------------------------------------------------------------

    @ParameterizedTest(name = "[{index}] SELECT - {0}")
    @MethodSource("selectCases")
    void selectMatrixRuns(String name, SqlResult r) throws SQLException {
        run(r);
    }

    static Stream<Arguments> selectCases() {
        return Stream.of(
            c("*", b().from("users").buildSql("users", DB)),
            c("columns", b().select("id", "name").from("users").buildSql("users", DB)),
            c("DISTINCT", b().select("status").distinct().from("users").buildSql("users", DB)),
            c("WHERE EQ",
                b().from("users").whereEquals("status", "active").buildSql("users", DB)),
            c("WHERE NEQ", withCond("status", Operator.NEQ, "active")),
            c("WHERE GT",
                b().from("users").whereGreaterThan("age", 20).buildSql("users", DB)),
            c("WHERE GTE",
                b().from("users").whereGreaterThanOrEquals("age", 20).buildSql("users", DB)),
            c("WHERE LT", withCond("age", Operator.LT, 35)),
            c("WHERE LTE",
                b().from("users").whereLessThanOrEquals("age", 40).buildSql("users", DB)),
            c("WHERE LIKE",
                b().from("users").whereLike("email", "example").buildSql("users", DB)),
            c("WHERE NOT LIKE",
                b().from("users").whereNotLike("email", "other").buildSql("users", DB)),
            c("WHERE IS NULL",
                b().from("users").whereNull("email").buildSql("users", DB)),
            c("WHERE IS NOT NULL",
                b().from("users").whereNotNull("email").buildSql("users", DB)),
            c("WHERE EXISTS (IS NOT NULL)",
                b().from("users").whereExists("email").buildSql("users", DB)),
            c("WHERE IN list",
                b().from("users")
                    .whereIn("status", List.of("active", "inactive")).buildSql("users", DB)),
            c("WHERE NOT IN list",
                b().from("users")
                    .whereNotIn("id", List.of(99, 100, 101)).buildSql("users", DB)),
            c("WHERE BETWEEN",
                b().from("users").whereBetween("age", 20, 40).buildSql("users", DB)),
            c("WHERE AND",
                b().from("users")
                    .whereEquals("active", 1).whereEquals("status", "active")
                    .buildSql("users", DB)),
            c("WHERE OR",
                b().from("users")
                    .whereEquals("status", "active").orWhereEquals("status", "inactive")
                    .buildSql("users", DB)),
            c("GROUP BY",
                b().select("status").from("users").groupBy("status").buildSql("users", DB)),
            c("HAVING",
                b().select("status").from("users").groupBy("status")
                    .havingRaw("COUNT(*) >= 0").buildSql("users", DB)),
            c("ORDER BY ASC",
                b().from("users").orderBy("name", true).buildSql("users", DB)),
            c("ORDER BY DESC",
                b().from("users").orderBy("age", false).buildSql("users", DB)),
            c("LIMIT", b().from("users").limit(2).buildSql("users", DB)),
            c("LIMIT OFFSET",
                b().from("users").limit(10).offset(1).buildSql("users", DB)),
            c("WHERE IN subquery",
                b().from("users")
                    .whereInSubquery("id",
                        b().select("user_id").from("orders")
                            .whereEquals("status", "shipped").build())
                    .buildSql("users", DB)),
            c("WHERE NOT IN subquery",
                withCond("id", Operator.NOT_IN,
                    b().select("user_id").from("orders")
                        .whereEquals("status", "shipped").build())),
            c("WHERE EQ subquery",
                b().from("users")
                    .whereEqualsSubquery("id",
                        b().select("user_id").from("orders").whereEquals("id", 1).build())
                    .buildSql("users", DB)),
            c("WHERE EXISTS subquery",
                b().from("users")
                    .whereExistsSubquery(
                        b().select("id").from("orders")
                            .whereEquals("status", "shipped").build())
                    .buildSql("users", DB)),
            c("WHERE NOT EXISTS subquery",
                b().from("users")
                    .whereNotExistsSubquery(
                        b().select("id").from("orders").whereEquals("status", "none").build())
                    .buildSql("users", DB)),
            c("FROM subquery",
                b().select("name")
                    .fromSubquery(b().select("name").from("users").build(), "u")
                    .buildSql(null, DB)),
            c("INNER JOIN table", joinTable()),
            c("INNER JOIN subquery",
                b().from("users")
                    .joinSubquery(
                        b().select("user_id", "amount").from("orders").build(),
                        "o", "users.id = o.user_id")
                    .buildSql("users", DB)),
            c("Scalar SELECT subquery",
                b().select("id")
                    .selectSubquery(
                        b().select("id").from("orders").limit(1).build(), "frst")
                    .from("users").buildSql("users", DB)),
            c("Complex",
                b().select("id", "name").from("users")
                    .whereEquals("active", 1).whereGreaterThan("age", 18)
                    .orderBy("name", true).limit(5).buildSql("users", DB))
        );
    }

    // -------------------------------------------------------------------------
    // INSERT matrix
    // -------------------------------------------------------------------------

    @ParameterizedTest(name = "[{index}] INSERT - {0}")
    @MethodSource("insertCases")
    void insertMatrixRuns(String name, SqlResult r) throws SQLException {
        runInTx(r);
    }

    static Stream<Arguments> insertCases() {
        return Stream.of(
            c("single row",
                QueryBuilder.insertInto("users")
                    .value("id", 200).value("name", "MatIns1")
                    .value("email", "m1@test.com").value("age", 20)
                    .value("score", 10.0).value("active", 1).value("status", "active")
                    .build()),
            c("multi-column",
                QueryBuilder.insertInto("orders")
                    .value("id", 200).value("user_id", 1)
                    .value("amount", 50.0).value("status", "pending")
                    .build())
        );
    }

    // -------------------------------------------------------------------------
    // UPDATE matrix
    // -------------------------------------------------------------------------

    @ParameterizedTest(name = "[{index}] UPDATE - {0}")
    @MethodSource("updateCases")
    void updateMatrixRuns(String name, SqlResult r) throws SQLException {
        runInTx(r);
    }

    static Stream<Arguments> updateCases() {
        return Stream.of(
            c("WHERE EQ",
                QueryBuilder.update("users")
                    .set("status", "active").whereEquals("id", 100).build()),
            c("WHERE GTE",
                QueryBuilder.update("users")
                    .set("active", 1).whereGreaterThanOrEquals("id", 100).build()),
            c("WHERE OR",
                QueryBuilder.update("users")
                    .set("status", "temp").whereEquals("id", 101).orWhereEquals("id", 102)
                    .build()),
            c("multi-SET",
                QueryBuilder.update("users")
                    .set("score", 99.0).set("active", 0).whereEquals("id", 103).build())
        );
    }

    // -------------------------------------------------------------------------
    // DELETE matrix
    // -------------------------------------------------------------------------

    @ParameterizedTest(name = "[{index}] DELETE - {0}")
    @MethodSource("deleteCases")
    void deleteMatrixRuns(String name, SqlResult r) throws SQLException {
        runInTx(r);
    }

    static Stream<Arguments> deleteCases() {
        return Stream.of(
            c("WHERE EQ",
                QueryBuilder.deleteFrom("users").whereEquals("id", 100).build(DB)),
            c("WHERE LT",
                QueryBuilder.deleteFrom("orders").whereLessThan("amount", 1.0).build(DB)),
            c("WHERE IN list",
                QueryBuilder.deleteFrom("users")
                    .whereIn("id", List.of(101, 102)).build(DB)),
            c("WHERE NOT IN list",
                QueryBuilder.deleteFrom("users")
                    .whereNotIn("status", List.of("active", "inactive")).build(DB)),
            c("WHERE BETWEEN",
                QueryBuilder.deleteFrom("users").whereBetween("id", 103, 103).build(DB)),
            c("WHERE NEQ",
                QueryBuilder.deleteFrom("users").whereNotEquals("status", "active").build(DB)),
            c("WHERE GT",
                QueryBuilder.deleteFrom("users").whereGreaterThan("id", 200).build(DB)),
            c("WHERE GTE",
                QueryBuilder.deleteFrom("users").whereGreaterThanOrEquals("id", 201).build(DB)),
            c("WHERE LTE",
                QueryBuilder.deleteFrom("users").whereLessThanOrEquals("id", 0).build(DB)),
            c("WHERE IN subquery",
                QueryBuilder.deleteFrom("users")
                    .whereInSubquery("id",
                        b().select("user_id").from("orders")
                            .whereEquals("status", "temp").build())
                    .build(DB)),
            c("WHERE EXISTS subquery",
                QueryBuilder.deleteFrom("users")
                    .whereExistsSubquery(
                        b().select("id").from("users").whereEquals("id", 9999).build())
                    .build(DB))
        );
    }

    // -------------------------------------------------------------------------
    // CREATE TABLE matrix
    // -------------------------------------------------------------------------

    @ParameterizedTest(name = "[{index}] CREATE - {0}")
    @MethodSource("createCases")
    void createMatrixRuns(String name, SqlResult r) throws SQLException {
        runInTx(r);
    }

    static Stream<Arguments> createCases() {
        return Stream.of(
            c("IF NOT EXISTS",
                QueryBuilder.createTable("mat_test_1")
                    .column("id", "INTEGER PRIMARY KEY").column("name", "TEXT")
                    .ifNotExists().build()),
            c("with PRIMARY KEY",
                QueryBuilder.createTable("mat_test_2")
                    .column("id", "INTEGER").column("val", "TEXT")
                    .primaryKey("id").build())
        );
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static Arguments c(String name, SqlResult r) {
        return Arguments.of(name, r);
    }

    private static QueryBuilder b() {
        return new QueryBuilder();
    }

    /**
     * Builds a {@code SELECT * FROM users WHERE col op val} result using the DB
     * dialect. Used for operators that are not directly exposed on {@link QueryBuilder}
     * (e.g. {@link Operator#NEQ}, {@link Operator#LT}).
     */
    private static SqlResult withCond(String col, Operator op, Object val) {
        Query q = new QueryBuilder().from("users").build();
        q.getConditions().add(new ConditionEntry(col, new Condition(op, val), Connector.AND));
        return DB.render(q);
    }

    /**
     * Builds a {@code SELECT users.name, orders.amount FROM users INNER JOIN orders} result
     * using a manually-constructed {@link JoinClause} (plain-table join).
     */
    private static SqlResult joinTable() {
        Query q = new QueryBuilder().select("users.name", "orders.amount").from("users").build();
        q.getJoins().add(
            new JoinClause(JoinClause.Type.INNER, "orders", "users.id = orders.user_id"));
        return DB.render(q);
    }

    /** Executes {@code r} against {@link #conn} using a bound {@link PreparedStatement}. */
    private static void run(SqlResult r) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(r.getSql())) {
            List<Object> params = r.getParameters();
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            ps.execute();
        }
    }

    /**
     * Executes {@code r} inside a savepoint that is always rolled back, preserving
     * the seed data for the remaining test cases.
     */
    private static void runInTx(SqlResult r) throws SQLException {
        Savepoint sp = conn.setSavepoint();
        try {
            run(r);
        } finally {
            conn.rollback(sp);
        }
    }

    /** Executes raw DDL / DML SQL directly. */
    private static void exec(String sql) throws SQLException {
        try (Statement st = conn.createStatement()) {
            st.execute(sql);
        }
    }
}
