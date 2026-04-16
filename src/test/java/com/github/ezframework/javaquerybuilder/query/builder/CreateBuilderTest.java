package com.github.ezframework.javaquerybuilder.query.builder;

import org.junit.jupiter.api.Test;

import com.github.ezframework.javaquerybuilder.query.sql.SqlDialect;
import com.github.ezframework.javaquerybuilder.query.sql.SqlResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Test
    void throwsWhenNullTableWithColumns() {
        assertThrows(IllegalStateException.class,
                () -> new CreateBuilder().column("id", "INT").build());
    }

    @Test
    void buildsWithDialect() {
        SqlResult result = new CreateBuilder()
            .table("users")
            .column("id", "INT")
            .build(SqlDialect.MYSQL);
        assertEquals("CREATE TABLE users (id INT)", result.getSql());
        assertTrue(result.getParameters().isEmpty());
    }

    @Test
    void buildsWithCompositePrimaryKey() {
        SqlResult result = new CreateBuilder()
            .table("user_roles")
            .column("user_id", "INT")
            .column("role_id", "INT")
            .primaryKey("user_id")
            .primaryKey("role_id")
            .build();
        assertEquals(
            "CREATE TABLE user_roles (user_id INT, role_id INT, PRIMARY KEY (user_id, role_id))",
            result.getSql());
    }

    @Test
    void parametersAreAlwaysEmpty() {
        SqlResult result = new CreateBuilder()
            .table("t")
            .column("id", "INT")
            .build();
        assertTrue(result.getParameters().isEmpty());
    }

    @Test
    void buildsMultipleColumnsWithoutPrimaryKey() {
        SqlResult result = new CreateBuilder()
            .table("logs")
            .column("id", "BIGINT")
            .column("message", "TEXT")
            .column("created_at", "TIMESTAMP")
            .build();
        assertEquals("CREATE TABLE logs (id BIGINT, message TEXT, created_at TIMESTAMP)", result.getSql());
    }
}
