package com.github.ezframework.javaquerybuilder.query.sql;

import java.util.List;

import com.github.ezframework.javaquerybuilder.query.Query;
import com.github.ezframework.javaquerybuilder.query.builder.QueryBuilder;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

/**
 * Tests for scalar SELECT subqueries: SELECT col, (SELECT ...) AS alias.
 */
public class ScalarSelectSubqueryTest {

    @Test
    void scalarSelectSubquery_standard() {
        final Query inner = new QueryBuilder()
            .from("order_counts")
            .select("cnt")
            .whereEquals("user_id", 1)
            .build();

        final SqlResult result = new QueryBuilder()
            .from("users")
            .select("id", "name")
            .selectSubquery(inner, "order_count")
            .buildSql("users", SqlDialect.STANDARD);

        assertEquals(
            "SELECT id, name, "
                + "(SELECT cnt FROM order_counts WHERE user_id = ?) AS order_count FROM users",
            result.getSql()
        );
        // scalar subquery params come first (before FROM/WHERE params)
        assertIterableEquals(List.of(1), result.getParameters());
    }

    @Test
    void scalarSelectSubquery_mysql_quotesAlias() {
        final Query inner = new QueryBuilder()
            .from("totals")
            .select("amount")
            .whereEquals("type", "sale")
            .build();

        final SqlResult result = new QueryBuilder()
            .from("customers")
            .select("id")
            .selectSubquery(inner, "sales_total")
            .buildSql("customers", SqlDialect.MYSQL);

        assertEquals(
            "SELECT `id`, (SELECT `amount` FROM `totals` WHERE `type` = ?) AS `sales_total` FROM `customers`",
            result.getSql()
        );
        assertIterableEquals(List.of("sale"), result.getParameters());
    }

    @Test
    void scalarSelectSubquery_onlySubquery_noPlainCols() {
        final Query inner = new QueryBuilder()
            .from("meta")
            .select("val")
            .build();

        final SqlResult result = new QueryBuilder()
            .from("x")
            .selectSubquery(inner, "m")
            .buildSql("x", SqlDialect.STANDARD);

        assertEquals("SELECT (SELECT val FROM meta) AS m FROM x", result.getSql());
        assertIterableEquals(List.of(), result.getParameters());
    }
}
