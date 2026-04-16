package com.github.ezframework.javaquerybuilder.query.builder;

import org.junit.jupiter.api.Test;
import com.github.ezframework.javaquerybuilder.query.sql.SqlResult;

import static org.junit.jupiter.api.Assertions.*;

public class CreateBuilderTest {
    @Test
    void buildsBasicCreateTable() {
        SqlResult result = new CreateBuilder()
            .table("users")
            .column("id", "INT")
            .column("name", "VARCHAR(255)")
            .primaryKey("id")
            .build();
        assertEquals("CREATE TABLE users (id INT, name VARCHAR(255), PRIMARY KEY (id))", result.getSql());
        assertTrue(result.getParameters().isEmpty());
    }

    @Test
    void buildsWithIfNotExists() {
        SqlResult result = new CreateBuilder()
            .table("items")
            .column("id", "INT")
            .ifNotExists()
            .build();
        assertEquals("CREATE TABLE IF NOT EXISTS items (id INT)", result.getSql());
    }

    @Test
    void throwsIfNoTableOrColumns() {
        assertThrows(IllegalStateException.class, () -> new CreateBuilder().build());
        assertThrows(IllegalStateException.class, () -> new CreateBuilder().table("t").build());
    }
}
