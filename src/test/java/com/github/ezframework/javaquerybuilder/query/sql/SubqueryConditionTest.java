package com.github.ezframework.javaquerybuilder.query.sql;

import java.util.List;

import com.github.ezframework.javaquerybuilder.query.Query;
import com.github.ezframework.javaquerybuilder.query.builder.QueryBuilder;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

/**
 * Tests for IN/NOT IN with a subquery as the RHS value.
 */
public class SubqueryConditionTest {

    @Test
    void inSubquery_standardDialect() {
        final Query inner = new QueryBuilder()
            .from("orders")
            .select("customer_id")
            .whereEquals("status", "active")
            .build();

        final SqlResult result = new QueryBuilder()
            .from("customers")
            .whereInSubquery("id", inner)
            .buildSql("customers", SqlDialect.STANDARD);

        assertEquals(
            "SELECT * FROM customers WHERE id IN (SELECT customer_id FROM orders WHERE status = ?)",
            result.getSql()
        );
        assertIterableEquals(List.of("active"), result.getParameters());
    }

    @Test
    void notInSubquery_standardDialect() {
        final Query inner = new QueryBuilder()
            .from("banned")
            .select("user_id")
            .build();

        final Query outer = new QueryBuilder()
            .from("users")
            .select("id", "name")
            .build();

        outer.getConditions().add(
            new com.github.ezframework.javaquerybuilder.query.condition.ConditionEntry(
                "id",
                new com.github.ezframework.javaquerybuilder.query.condition.Condition(
                    com.github.ezframework.javaquerybuilder.query.condition.Operator.NOT_IN, inner),
                com.github.ezframework.javaquerybuilder.query.condition.Connector.AND
            )
        );

        final SqlResult r = SqlDialect.STANDARD.render(outer);
        assertEquals(
            "SELECT id, name FROM users WHERE id NOT IN (SELECT user_id FROM banned)",
            r.getSql()
        );
        assertIterableEquals(List.of(), r.getParameters());
    }

    @Test
    void inSubqueryWithParamsFromBoth_mysql() {
        final Query inner = new QueryBuilder()
            .from("sessions")
            .select("user_id")
            .whereEquals("type", "premium")
            .build();

        final SqlResult result = new QueryBuilder()
            .from("users")
            .whereEquals("active", true)
            .whereInSubquery("id", inner)
            .buildSql("users", SqlDialect.MYSQL);

        assertEquals(
            "SELECT * FROM `users` WHERE `active` = ? AND `id` IN "
                + "(SELECT `user_id` FROM `sessions` WHERE `type` = ?)",
            result.getSql()
        );
        assertIterableEquals(List.of(true, "premium"), result.getParameters());
    }

    @Test
    void equalsSubquery() {
        final Query inner = new QueryBuilder()
            .from("config")
            .select("value")
            .whereEquals("key", "max_age")
            .build();

        final SqlResult result = new QueryBuilder()
            .from("users")
            .whereEqualsSubquery("age", inner)
            .buildSql("users", SqlDialect.STANDARD);

        assertEquals(
            "SELECT * FROM users WHERE age = (SELECT value FROM config WHERE key = ?)",
            result.getSql()
        );
        assertIterableEquals(List.of("max_age"), result.getParameters());
    }

    @Test
    void inSubquery_sqlite_quotesAlias() {
        final Query inner = new QueryBuilder()
            .from("src")
            .select("ref_id")
            .build();

        final SqlResult result = new QueryBuilder()
            .from("main")
            .whereInSubquery("id", inner)
            .buildSql("main", SqlDialect.SQLITE);

        assertEquals(
            "SELECT * FROM \"main\" WHERE \"id\" IN (SELECT \"ref_id\" FROM \"src\")",
            result.getSql()
        );
    }
}
