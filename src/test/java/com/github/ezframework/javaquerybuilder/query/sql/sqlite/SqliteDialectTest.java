package com.github.ezframework.javaquerybuilder.query.sql.sqlite;

import java.util.List;

import com.github.ezframework.javaquerybuilder.query.builder.QueryBuilder;
import com.github.ezframework.javaquerybuilder.query.sql.SqlResult;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SqliteDialectTest {

    @Test
    void canInstantiateSqliteDialect() {
        SqliteDialect dialect = new SqliteDialect();
        assertNotNull(dialect);
    }

    @Test
    void quotesTableNameWithDoubleQuotes() {
        final SqliteDialect dialect = new SqliteDialect();
        final SqlResult result = new QueryBuilder().from("orders").buildSql("orders", dialect);
        assertTrue(result.getSql().startsWith("SELECT * FROM \"orders\""));
    }

    @Test
    void quotesColumnNamesWithDoubleQuotes() {
        final SqliteDialect dialect = new SqliteDialect();
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
        final SqliteDialect dialect = new SqliteDialect();
        final SqlResult result = new QueryBuilder()
            .orderBy("created_at", true)
            .buildSql("events", dialect);
        assertTrue(result.getSql().contains("ORDER BY \"created_at\" ASC"));
    }
}

