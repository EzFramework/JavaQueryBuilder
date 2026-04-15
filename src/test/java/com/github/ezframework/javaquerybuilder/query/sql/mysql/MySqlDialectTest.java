package com.github.ezframework.javaquerybuilder.query.sql.mysql;

import java.util.List;

import com.github.ezframework.javaquerybuilder.query.builder.QueryBuilder;
import com.github.ezframework.javaquerybuilder.query.sql.SqlResult;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MySqlDialectTest {

    @Test
    void canInstantiateMySqlDialect() {
        MySqlDialect dialect = new MySqlDialect();
        assertNotNull(dialect);
    }

    @Test
    void quotesTableNameWithBackticks() {
        final MySqlDialect dialect = new MySqlDialect();
        final SqlResult result = new QueryBuilder().from("orders").buildSql("orders", dialect);
        assertTrue(result.getSql().startsWith("SELECT * FROM `orders`"));
    }

    @Test
    void quotesColumnNamesWithBackticks() {
        final MySqlDialect dialect = new MySqlDialect();
        final SqlResult result = new QueryBuilder()
            .select("id", "name")
            .whereEquals("status", "active")
            .buildSql("users", dialect);
        assertTrue(result.getSql().contains("`id`, `name`"));
        assertTrue(result.getSql().contains("`status` = ?"));
        assertEquals(List.of("active"), result.getParameters());
    }

    @Test
    void quotesOrderByColumnsWithBackticks() {
        final MySqlDialect dialect = new MySqlDialect();
        final SqlResult result = new QueryBuilder()
            .orderBy("created_at", false)
            .buildSql("events", dialect);
        assertTrue(result.getSql().contains("ORDER BY `created_at` DESC"));
    }
}

