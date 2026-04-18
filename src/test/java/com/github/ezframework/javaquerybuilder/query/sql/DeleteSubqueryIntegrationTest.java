package com.github.ezframework.javaquerybuilder.query.sql;

import java.util.List;

import com.github.ezframework.javaquerybuilder.query.Query;
import com.github.ezframework.javaquerybuilder.query.builder.DeleteBuilder;
import com.github.ezframework.javaquerybuilder.query.builder.QueryBuilder;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

/**
 * Integration tests for the Jaloquent use-case: DELETE with subquery IN condition.
 *
 * <p>Demonstrates that {@link DeleteBuilder#whereInSubquery(String, Query)} produces
 * a single DELETE statement with correct parameterised SQL across all dialects,
 * replacing the substring-hack approach previously used in Jaloquent.
 */
public class DeleteSubqueryIntegrationTest {

    @Test
    void deleteWhereInSubquery_standard() {
        final Query subquery = new QueryBuilder()
            .from("other")
            .select("id")
            .whereEquals("status", "obsolete")
            .build();

        final SqlResult result = new DeleteBuilder()
            .from("t")
            .whereInSubquery("id", subquery)
            .build(SqlDialect.STANDARD);

        assertEquals(
            "DELETE FROM t WHERE id IN (SELECT id FROM other WHERE status = ?)",
            result.getSql()
        );
        assertIterableEquals(List.of("obsolete"), result.getParameters());
    }

    @Test
    void deleteWhereInSubquery_mysql_quotesAll() {
        final Query subquery = new QueryBuilder()
            .from("archive")
            .select("user_id")
            .whereEquals("year", 2023)
            .build();

        final SqlResult result = new DeleteBuilder()
            .from("users")
            .whereInSubquery("id", subquery)
            .build(SqlDialect.MYSQL);

        assertEquals(
            "DELETE FROM `users` WHERE `id` IN (SELECT `user_id` FROM `archive` WHERE `year` = ?)",
            result.getSql()
        );
        assertIterableEquals(List.of(2023), result.getParameters());
    }

    @Test
    void deleteWhereInSubquery_sqlite_quotesAll() {
        final Query subquery = new QueryBuilder()
            .from("removed")
            .select("ref")
            .whereEquals("flag", 1)
            .build();

        final SqlResult result = new DeleteBuilder()
            .from("items")
            .whereInSubquery("id", subquery)
            .build(SqlDialect.SQLITE);

        assertEquals(
            "DELETE FROM \"items\" WHERE \"id\" IN (SELECT \"ref\" FROM \"removed\" WHERE \"flag\" = ?)",
            result.getSql()
        );
        assertIterableEquals(List.of(1), result.getParameters());
    }

    @Test
    void deleteWhereScalarAndSubquery_paramsOrdered() {
        final Query subquery = new QueryBuilder()
            .from("blacklist")
            .select("uid")
            .whereEquals("reason", "fraud")
            .build();

        final SqlResult result = new DeleteBuilder()
            .from("accounts")
            .whereEquals("active", false)
            .whereInSubquery("user_id", subquery)
            .build(SqlDialect.STANDARD);

        assertEquals(
            "DELETE FROM accounts WHERE active = ? AND user_id IN "
                + "(SELECT uid FROM blacklist WHERE reason = ?)",
            result.getSql()
        );
        assertIterableEquals(List.of(false, "fraud"), result.getParameters());
    }

    @Test
    void deleteWhereExistsSubquery() {
        final Query subquery = new QueryBuilder()
            .from("orders")
            .select("id")
            .whereEquals("account_id", 99)
            .build();

        final SqlResult result = new DeleteBuilder()
            .from("pending")
            .whereExistsSubquery(subquery)
            .build(SqlDialect.STANDARD);

        assertEquals(
            "DELETE FROM pending WHERE EXISTS (SELECT id FROM orders WHERE account_id = ?)",
            result.getSql()
        );
        assertIterableEquals(List.of(99), result.getParameters());
    }
}
