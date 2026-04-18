package com.github.ezframework.javaquerybuilder.query.sql;

import java.util.List;

import com.github.ezframework.javaquerybuilder.query.Query;
import com.github.ezframework.javaquerybuilder.query.builder.QueryBuilder;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

/**
 * Tests for JOIN (SELECT ...) AS alias subquery joins.
 */
public class JoinSubqueryTest {

    @Test
    void joinSubquery_standard() {
        final Query inner = new QueryBuilder()
            .from("order_totals")
            .select("user_id", "total")
            .whereEquals("year", 2025)
            .build();

        final SqlResult result = new QueryBuilder()
            .from("users")
            .select("users.id", "ot.total")
            .joinSubquery(inner, "ot", "users.id = ot.user_id")
            .buildSql("users", SqlDialect.STANDARD);

        assertEquals(
            "SELECT users.id, ot.total FROM users INNER JOIN "
                + "(SELECT user_id, total FROM order_totals WHERE year = ?) AS ot ON users.id = ot.user_id",
            result.getSql()
        );
        assertIterableEquals(List.of(2025), result.getParameters());
    }

    @Test
    void joinSubquery_mysql_quotesAlias() {
        final Query inner = new QueryBuilder()
            .from("scores")
            .select("player_id", "pts")
            .build();

        final SqlResult result = new QueryBuilder()
            .from("players")
            .joinSubquery(inner, "sc", "players.id = sc.player_id")
            .buildSql("players", SqlDialect.MYSQL);

        assertEquals(
            "SELECT * FROM `players` INNER JOIN (SELECT `player_id`, `pts` FROM `scores`) AS `sc`"
                + " ON players.id = sc.player_id",
            result.getSql()
        );
    }

    @Test
    void joinSubquery_paramsInCorrectOrder() {
        final Query inner = new QueryBuilder()
            .from("stats")
            .select("uid")
            .whereEquals("month", "2025-01")
            .build();

        final SqlResult result = new QueryBuilder()
            .from("users")
            .whereEquals("active", true)
            .joinSubquery(inner, "s", "users.id = s.uid")
            .buildSql("users", SqlDialect.STANDARD);

        // JOIN params appear before WHERE params in the rendered list
        // because JOINs are appended to the SQL and params before WHERE
        assertEquals(
            "SELECT * FROM users INNER JOIN "
                + "(SELECT uid FROM stats WHERE month = ?) AS s ON users.id = s.uid"
                + " WHERE active = ?",
            result.getSql()
        );
        assertIterableEquals(List.of("2025-01", true), result.getParameters());
    }
}
