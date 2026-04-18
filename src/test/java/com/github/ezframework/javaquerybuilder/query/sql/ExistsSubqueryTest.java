package com.github.ezframework.javaquerybuilder.query.sql;

import java.util.List;

import com.github.ezframework.javaquerybuilder.query.Query;
import com.github.ezframework.javaquerybuilder.query.builder.QueryBuilder;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

/**
 * Tests for EXISTS / NOT EXISTS subquery conditions.
 */
public class ExistsSubqueryTest {

    @Test
    void whereExistsSubquery_standard() {
        final Query inner = new QueryBuilder()
            .from("orders")
            .select("id")
            .whereEquals("user_id", 42)
            .build();

        final SqlResult result = new QueryBuilder()
            .from("users")
            .whereExistsSubquery(inner)
            .buildSql("users", SqlDialect.STANDARD);

        assertEquals(
            "SELECT * FROM users WHERE EXISTS (SELECT id FROM orders WHERE user_id = ?)",
            result.getSql()
        );
        assertIterableEquals(List.of(42), result.getParameters());
    }

    @Test
    void whereNotExistsSubquery_standard() {
        final Query inner = new QueryBuilder()
            .from("bans")
            .select("user_id")
            .whereEquals("active", true)
            .build();

        final SqlResult result = new QueryBuilder()
            .from("users")
            .whereNotExistsSubquery(inner)
            .buildSql("users", SqlDialect.STANDARD);

        assertEquals(
            "SELECT * FROM users WHERE NOT EXISTS (SELECT user_id FROM bans WHERE active = ?)",
            result.getSql()
        );
        assertIterableEquals(List.of(true), result.getParameters());
    }

    @Test
    void whereExistsSubquery_mysql_quotesIdentifiers() {
        final Query inner = new QueryBuilder()
            .from("subscriptions")
            .select("uid")
            .whereEquals("plan", "pro")
            .build();

        final SqlResult result = new QueryBuilder()
            .from("accounts")
            .whereExistsSubquery(inner)
            .buildSql("accounts", SqlDialect.MYSQL);

        assertEquals(
            "SELECT * FROM `accounts` WHERE EXISTS "
                + "(SELECT `uid` FROM `subscriptions` WHERE `plan` = ?)",
            result.getSql()
        );
        assertIterableEquals(List.of("pro"), result.getParameters());
    }

    @Test
    void existsSubquery_paramsFromMultipleConditions() {
        final Query inner = new QueryBuilder()
            .from("events")
            .select("user_id")
            .whereEquals("type", "login")
            .build();

        final SqlResult result = new QueryBuilder()
            .from("users")
            .whereEquals("status", "active")
            .whereExistsSubquery(inner)
            .buildSql("users", SqlDialect.STANDARD);

        assertEquals(
            "SELECT * FROM users WHERE status = ? AND EXISTS "
                + "(SELECT user_id FROM events WHERE type = ?)",
            result.getSql()
        );
        assertIterableEquals(List.of("active", "login"), result.getParameters());
    }
}
