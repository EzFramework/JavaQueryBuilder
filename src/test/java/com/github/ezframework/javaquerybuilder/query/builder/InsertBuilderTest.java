package com.github.ezframework.javaquerybuilder.query.builder;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.github.ezframework.javaquerybuilder.query.sql.SqlDialect;
import com.github.ezframework.javaquerybuilder.query.sql.SqlResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class InsertBuilderTest {

    @Test
    void buildsInsertStatement() {
        assertNotNull(new InsertBuilder().into("users").value("name", "Alice").build());
    }

    @Test
    void buildSqlIsCorrect() {
        SqlResult result = new InsertBuilder().into("users")
                .value("name", "Alice")
                .value("age", 30)
                .build();
        assertEquals("INSERT INTO users (name, age) VALUES (?, ?)", result.getSql());
        assertEquals(List.of("Alice", 30), result.getParameters());
    }

    @Test
    void buildWithDialect() {
        SqlResult result = new InsertBuilder().into("items").value("label", "x").build(SqlDialect.SQLITE);
        assertNotNull(result);
        assertEquals("INSERT INTO items (label) VALUES (?)", result.getSql());
    }
}
