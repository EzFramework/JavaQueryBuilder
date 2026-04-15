package com.github.ezframework.javaquerybuilder.query.builder;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.github.ezframework.javaquerybuilder.query.sql.SqlDialect;
import com.github.ezframework.javaquerybuilder.query.sql.SqlResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UpdateBuilderTest {

    @Test
    void buildsUpdateStatement() {
        assertNotNull(new UpdateBuilder().table("users").set("name", "Bob").whereEquals("id", 2).build());
    }

    @Test
    void buildSqlIsCorrect() {
        SqlResult result = new UpdateBuilder().table("users")
                .set("name", "Alice")
                .whereEquals("id", 1)
                .build();
        assertEquals("UPDATE users SET name = ? WHERE id = ?", result.getSql());
        assertEquals(List.of("Alice", 1), result.getParameters());
    }

    @Test
    void buildSqlWithMultipleSetColumns() {
        SqlResult result = new UpdateBuilder().table("t")
                .set("a", 1)
                .set("b", "two")
                .whereEquals("id", 99)
                .build();
        assertTrue(result.getSql().contains("a = ?"));
        assertTrue(result.getSql().contains(", b = ?"));
        assertEquals(List.of(1, "two", 99), result.getParameters());
    }

    @Test
    void buildSqlWithOrWhereEquals() {
        SqlResult result = new UpdateBuilder().table("t")
                .set("active", false)
                .whereEquals("role", "guest")
                .orWhereEquals("role", "temp")
                .build();
        assertTrue(result.getSql().contains("OR role = ?"));
        assertEquals(List.of(false, "guest", "temp"), result.getParameters());
    }

    @Test
    void buildSqlWithGreaterThanOrEquals() {
        SqlResult result = new UpdateBuilder().table("t")
                .set("flagged", true)
                .whereGreaterThanOrEquals("score", 100)
                .build();
        assertNotNull(result);
        assertTrue(result.getSql().startsWith("UPDATE t SET flagged = ?"));
        assertEquals(List.of(true, 100), result.getParameters());
    }

    @Test
    void buildSqlWithNoConditions() {
        SqlResult result = new UpdateBuilder().table("items").set("archived", true).build();
        assertEquals("UPDATE items SET archived = ?", result.getSql());
        assertEquals(List.of(true), result.getParameters());
    }

    @Test
    void buildWithDialect() {
        SqlResult result = new UpdateBuilder().table("t").set("x", 1).build(SqlDialect.MYSQL);
        assertNotNull(result);
        assertTrue(result.getSql().startsWith("UPDATE t SET"));
    }
}
