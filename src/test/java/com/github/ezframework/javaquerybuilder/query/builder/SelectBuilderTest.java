package com.github.ezframework.javaquerybuilder.query.builder;

import com.github.ezframework.javaquerybuilder.query.sql.SqlResult;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SelectBuilder.
 */
class SelectBuilderTest {

    @Test
    void testSimpleSelect() {
        SqlResult sql = new SelectBuilder()
                .from("users")
                .select("id", "name")
                .build();
        assertEquals("SELECT id, name FROM users", sql.getSql());
        assertTrue(sql.getParameters().isEmpty());
    }

    @Test
    void testSelectAll() {
        SqlResult sql = new SelectBuilder()
                .from("users")
                .build();
        assertEquals("SELECT * FROM users", sql.getSql());
        assertTrue(sql.getParameters().isEmpty());
    }

    @Test
    void testDistinct() {
        SqlResult sql = new SelectBuilder()
                .from("users")
                .select("email")
                .distinct()
                .build();
        assertEquals("SELECT DISTINCT email FROM users", sql.getSql());
    }

    @Test
    void testWhereEquals() {
        SqlResult sql = new SelectBuilder()
                .from("users")
                .whereEquals("id", 42)
                .build();
        assertEquals("SELECT * FROM users WHERE id = ?", sql.getSql());
        assertEquals(Collections.singletonList(42), sql.getParameters());
    }

    @Test
    void testWhereIn() {
        SqlResult sql = new SelectBuilder()
                .from("users")
                .whereIn("id", Arrays.asList(1, 2, 3))
                .build();
        assertEquals("SELECT * FROM users WHERE id IN (?, ?, ?)", sql.getSql());
        assertEquals(Arrays.asList(1, 2, 3), sql.getParameters());
    }

    @Test
    void testWhereLike() {
        SqlResult sql = new SelectBuilder()
                .from("users")
                .whereLike("name", "%bob%")
                .build();
        assertEquals("SELECT * FROM users WHERE name LIKE ?", sql.getSql());
        assertEquals(Collections.singletonList("%bob%"), sql.getParameters());
    }

    @Test
    void testGroupByOrderByLimitOffset() {
        SqlResult sql = new SelectBuilder()
                .from("users")
                .select("id", "name")
                .groupBy("name")
                .orderBy("id", false)
                .limit(10)
                .offset(5)
                .build();
        assertEquals("SELECT id, name FROM users GROUP BY name ORDER BY id DESC LIMIT 10 OFFSET 5", sql.getSql());
    }

    @Test
    void testMissingTableThrows() {
        SelectBuilder builder = new SelectBuilder();
        IllegalStateException ex = assertThrows(IllegalStateException.class, builder::build);
        assertTrue(ex.getMessage().contains("Table name"));
    }
}
