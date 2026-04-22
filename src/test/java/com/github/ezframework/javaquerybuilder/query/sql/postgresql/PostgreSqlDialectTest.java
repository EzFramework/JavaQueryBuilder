package com.github.ezframework.javaquerybuilder.query.sql.postgresql;

import java.util.List;

import com.github.ezframework.javaquerybuilder.query.builder.DeleteBuilder;
import com.github.ezframework.javaquerybuilder.query.builder.InsertBuilder;
import com.github.ezframework.javaquerybuilder.query.builder.QueryBuilder;
import com.github.ezframework.javaquerybuilder.query.builder.UpdateBuilder;
import com.github.ezframework.javaquerybuilder.query.sql.SqlDialect;
import com.github.ezframework.javaquerybuilder.query.sql.SqlResult;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PostgreSqlDialectTest {

    @Test
    void canInstantiatePostgreSqlDialect() {
        final PostgreSqlDialect dialect = new PostgreSqlDialect();
        assertNotNull(dialect);
    }

    @Test
    void quotesTableNameWithDoubleQuotes() {
        final PostgreSqlDialect dialect = new PostgreSqlDialect();
        final SqlResult result = new QueryBuilder().from("orders").buildSql("orders", dialect);
        assertTrue(result.getSql().startsWith("SELECT * FROM \"orders\""));
    }

    @Test
    void quotesColumnNamesWithDoubleQuotes() {
        final PostgreSqlDialect dialect = new PostgreSqlDialect();
        final SqlResult result = new QueryBuilder()
            .select("id", "name")
            .whereEquals("status", "active")
            .buildSql("users", dialect);
        assertTrue(result.getSql().contains("\"id\", \"name\""));
        assertTrue(result.getSql().contains("\"status\" = ?"));
        assertEquals(List.of("active"), result.getParameters());
    }

    @Test
    void quotesOrderByColumnsWithDoubleQuotes() {
        final PostgreSqlDialect dialect = new PostgreSqlDialect();
        final SqlResult result = new QueryBuilder()
            .orderBy("created_at", false)
            .buildSql("events", dialect);
        assertTrue(result.getSql().contains("ORDER BY \"created_at\" DESC"));
    }

    @Test
    void rendersILikeCondition() {
        final PostgreSqlDialect dialect = new PostgreSqlDialect();
        final SqlResult result = new QueryBuilder()
            .whereILike("email", "alice")
            .buildSql("users", dialect);
        assertTrue(result.getSql().contains("\"email\" ILIKE ?"));
        assertEquals(List.of("%alice%"), result.getParameters());
    }

    @Test
    void rendersNotILikeCondition() {
        final PostgreSqlDialect dialect = new PostgreSqlDialect();
        final SqlResult result = new QueryBuilder()
            .whereNotLike("email", "spam")
            .buildSql("users", dialect);
        assertTrue(result.getSql().contains("NOT LIKE ?"));
    }

    @Test
    void rendersOrWhereILike() {
        final PostgreSqlDialect dialect = new PostgreSqlDialect();
        final SqlResult result = new QueryBuilder()
            .whereEquals("role", "admin")
            .orWhereILike("name", "john")
            .buildSql("users", dialect);
        assertTrue(result.getSql().contains("OR \"name\" ILIKE ?"));
    }

    @Test
    void deleteDoesNotAppendLimit() {
        final PostgreSqlDialect dialect = new PostgreSqlDialect();
        final SqlResult result = new DeleteBuilder()
            .from("events")
            .whereEquals("id", 42)
            .build(dialect);
        assertFalse(result.getSql().contains("LIMIT"));
    }

    @Test
    void deleteReturningAppendsClause() {
        final PostgreSqlDialect dialect = new PostgreSqlDialect();
        final SqlResult result = new DeleteBuilder()
            .from("users")
            .whereEquals("id", 99)
            .returning("id", "email")
            .build(dialect);
        assertTrue(result.getSql().endsWith("RETURNING id, email"),
            "Expected SQL to end with RETURNING clause, got: " + result.getSql());
    }

    @Test
    void deleteReturningNotRenderedForOtherDialects() {
        final SqlResult result = new DeleteBuilder()
            .from("users")
            .whereEquals("id", 1)
            .returning("id")
            .build(SqlDialect.STANDARD);
        assertFalse(result.getSql().contains("RETURNING"));
    }

    @Test
    void insertReturningAppendsClause() {
        final SqlResult result = new InsertBuilder()
            .into("users")
            .value("name", "Alice")
            .returning("id", "created_at")
            .build();
        assertTrue(result.getSql().endsWith("RETURNING id, created_at"),
            "Expected SQL to end with RETURNING clause, got: " + result.getSql());
    }

    @Test
    void insertWithoutReturningHasNoClause() {
        final SqlResult result = new InsertBuilder()
            .into("users")
            .value("name", "Bob")
            .build();
        assertFalse(result.getSql().contains("RETURNING"));
    }

    @Test
    void updateReturningAppendsClause() {
        final SqlResult result = new UpdateBuilder()
            .table("users")
            .set("name", "Charlie")
            .whereEquals("id", 7)
            .returning("id", "updated_at")
            .build();
        assertTrue(result.getSql().endsWith("RETURNING id, updated_at"),
            "Expected SQL to end with RETURNING clause, got: " + result.getSql());
    }

    @Test
    void updateWithoutReturningHasNoClause() {
        final SqlResult result = new UpdateBuilder()
            .table("users")
            .set("status", "active")
            .build();
        assertFalse(result.getSql().contains("RETURNING"));
    }
}
