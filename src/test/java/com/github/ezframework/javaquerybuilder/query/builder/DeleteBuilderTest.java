// ...existing code...
package com.github.ezframework.javaquerybuilder.query.builder;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.github.ezframework.javaquerybuilder.query.sql.SqlDialect;
import com.github.ezframework.javaquerybuilder.query.sql.SqlResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DeleteBuilderTest {

    @Test
    void buildsDeleteStatement() {
        assertNotNull(new DeleteBuilder().from("users").whereEquals("id", 1).build());
    }

    @Test
    void buildSqlEqualityCondition() {
        SqlResult result = new DeleteBuilder().from("users").whereEquals("id", 5).build();
        assertEquals("DELETE FROM users WHERE id = ?", result.getSql());
        assertEquals(List.of(5), result.getParameters());
    }

    @Test
    void buildSqlLessThanCondition() {
        SqlResult result = new DeleteBuilder().from("orders").whereLessThan("age", 18).build();
        assertTrue(result.getSql().contains("age < ?"));
        assertEquals(List.of(18), result.getParameters());
    }

    @Test
    void buildSqlWithMultipleConditions() {
        SqlResult result = new DeleteBuilder().from("t")
                .whereEquals("status", "old")
                .whereLessThan("score", 0)
                .build();
        assertTrue(result.getSql().contains("AND score < ?"));
        assertEquals(List.of("old", 0), result.getParameters());
    }

    @Test
    void buildSqlWithNoConditions() {
        SqlResult result = new DeleteBuilder().from("sessions").build();
        assertEquals("DELETE FROM sessions", result.getSql());
        assertTrue(result.getParameters().isEmpty());
    }

    @Test
    void buildWithDialect() {
        SqlResult result = new DeleteBuilder().from("t").whereEquals("id", 1).build(SqlDialect.STANDARD);
        assertNotNull(result);
        assertTrue(result.getSql().startsWith("DELETE FROM t"));
    }

    @Test
    void buildSqlInCondition() {
        SqlResult result = new DeleteBuilder().from("users").whereIn("id", List.of(1, 2, 3)).build();
        assertEquals("DELETE FROM users WHERE id IN (?, ?, ?)", result.getSql());
        assertEquals(List.of(1, 2, 3), result.getParameters());
    }

    @Test
    void whereInNullThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new DeleteBuilder().from("t").whereIn("id", null));
    }

    @Test
    void whereInEmptyThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new DeleteBuilder().from("t").whereIn("id", List.of()));
    }

    @Test
    void buildSqlNotInCondition() {
        SqlResult result = new DeleteBuilder().from("users").whereNotIn("id", List.of(1, 2)).build();
        assertEquals("DELETE FROM users WHERE id NOT IN (?, ?)", result.getSql());
        assertEquals(List.of(1, 2), result.getParameters());
    }

    @Test
    void whereNotInNullThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new DeleteBuilder().from("t").whereNotIn("id", null));
    }

    @Test
    void whereNotInEmptyThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new DeleteBuilder().from("t").whereNotIn("id", List.of()));
    }

    @Test
    void buildSqlBetweenCondition() {
        SqlResult result = new DeleteBuilder().from("logs").whereBetween("age", 10, 20).build();
        assertEquals("DELETE FROM logs WHERE age BETWEEN ? AND ?", result.getSql());
        assertEquals(List.of(10, 20), result.getParameters());
    }

    @Test
    void buildSqlGreaterThanCondition() {
        SqlResult result = new DeleteBuilder().from("t").whereGreaterThan("score", 100).build();
        assertEquals("DELETE FROM t WHERE score > ?", result.getSql());
        assertEquals(List.of(100), result.getParameters());
    }

    @Test
    void buildSqlGreaterThanOrEqualsCondition() {
        SqlResult result = new DeleteBuilder().from("t").whereGreaterThanOrEquals("age", 18).build();
        assertEquals("DELETE FROM t WHERE age >= ?", result.getSql());
        assertEquals(List.of(18), result.getParameters());
    }

    @Test
    void buildSqlLessThanOrEqualsCondition() {
        SqlResult result = new DeleteBuilder().from("t").whereLessThanOrEquals("price", 99).build();
        assertEquals("DELETE FROM t WHERE price <= ?", result.getSql());
        assertEquals(List.of(99), result.getParameters());
    }

    @Test
    void buildSqlNotEqualsCondition() {
        SqlResult result = new DeleteBuilder().from("t").whereNotEquals("status", "deleted").build();
        assertEquals("DELETE FROM t WHERE status != ?", result.getSql());
        assertEquals(List.of("deleted"), result.getParameters());
    }

    @Test
    void buildSqlMultipleOperatorsCombined() {
        SqlResult result = new DeleteBuilder().from("orders")
                .whereGreaterThan("total", 0)
                .whereLessThanOrEquals("age", 30)
                .whereNotEquals("status", "active")
                .build();
        assertTrue(result.getSql().contains("WHERE total > ?"));
        assertTrue(result.getSql().contains("AND age <= ?"));
        assertTrue(result.getSql().contains("AND status != ?"));
        assertEquals(List.of(0, 30, "active"), result.getParameters());
    }
}
