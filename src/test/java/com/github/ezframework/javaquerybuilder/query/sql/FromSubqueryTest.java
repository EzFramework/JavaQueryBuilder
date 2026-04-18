package com.github.ezframework.javaquerybuilder.query.sql;

import java.util.List;

import com.github.ezframework.javaquerybuilder.query.Query;
import com.github.ezframework.javaquerybuilder.query.builder.QueryBuilder;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

/**
 * Tests for FROM (SELECT ...) AS alias derived-table queries.
 */
public class FromSubqueryTest {

    @Test
    void fromSubquery_standard() {
        final Query inner = new QueryBuilder()
            .from("raw_data")
            .select("id", "value")
            .whereEquals("archived", false)
            .build();

        final SqlResult result = new QueryBuilder()
            .fromSubquery(inner, "d")
            .select("id", "value")
            .buildSql(null, SqlDialect.STANDARD);

        assertEquals(
            "SELECT id, value FROM (SELECT id, value FROM raw_data WHERE archived = ?) AS d",
            result.getSql()
        );
        assertIterableEquals(List.of(false), result.getParameters());
    }

    @Test
    void fromSubquery_mysql_quotesAlias() {
        final Query inner = new QueryBuilder()
            .from("logs")
            .select("user_id")
            .build();

        final SqlResult result = new QueryBuilder()
            .fromSubquery(inner, "recent")
            .select("user_id")
            .buildSql(null, SqlDialect.MYSQL);

        assertEquals(
            "SELECT `user_id` FROM (SELECT `user_id` FROM `logs`) AS `recent`",
            result.getSql()
        );
        assertIterableEquals(List.of(), result.getParameters());
    }

    @Test
    void fromSubquery_sqlite_quotesAlias() {
        final Query inner = new QueryBuilder()
            .from("events")
            .select("ts", "user_id")
            .build();

        final SqlResult result = new QueryBuilder()
            .fromSubquery(inner, "ev")
            .buildSql(null, SqlDialect.SQLITE);

        assertEquals(
            "SELECT * FROM (SELECT \"ts\", \"user_id\" FROM \"events\") AS \"ev\"",
            result.getSql()
        );
    }

    @Test
    void fromSubquery_withWhereOnOuter() {
        final Query inner = new QueryBuilder()
            .from("sales")
            .select("region", "amount")
            .build();

        final SqlResult result = new QueryBuilder()
            .fromSubquery(inner, "s")
            .select("region")
            .whereEquals("amount", 100)
            .buildSql(null, SqlDialect.STANDARD);

        assertEquals(
            "SELECT region FROM (SELECT region, amount FROM sales) AS s WHERE amount = ?",
            result.getSql()
        );
        assertIterableEquals(List.of(100), result.getParameters());
    }
}
