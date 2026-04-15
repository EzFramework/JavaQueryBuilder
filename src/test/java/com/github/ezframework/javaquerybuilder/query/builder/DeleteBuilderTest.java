package com.github.ezframework.javaquerybuilder.query.builder;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.github.ezframework.javaquerybuilder.query.sql.SqlDialect;
import com.github.ezframework.javaquerybuilder.query.sql.SqlResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
}
