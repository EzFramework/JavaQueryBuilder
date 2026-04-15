package com.github.ezframework.javaquerybuilder.query.builder;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.github.ezframework.javaquerybuilder.query.Query;
import com.github.ezframework.javaquerybuilder.query.sql.SqlDialect;
import com.github.ezframework.javaquerybuilder.query.sql.SqlResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class QueryBuilderTest {

    @Test
    void buildsBasicSelectQuery() {
        Query query = new QueryBuilder().select("id", "name")
                .whereEquals("status", "active")
                .orderBy("name", true)
                .limit(10)
                .offset(5)
                .build();
        assertNotNull(query);
        assertEquals(10, query.getLimit());
        assertEquals(5, query.getOffset());
    }

    @Test
    void supportsChainedWhereClauses() {
        Query query = new QueryBuilder().whereEquals("id", 1)
                .whereLike("name", "bob")
                .whereExists("active")
                .build();
        assertNotNull(query.getConditions());
        assertTrue(query.getConditions().size() >= 3);
    }

    @Test
    void buildSqlSelectStar() {
        SqlResult result = new QueryBuilder().buildSql("users");
        assertEquals("SELECT * FROM users", result.getSql());
        assertTrue(result.getParameters().isEmpty());
    }

    @Test
    void buildSqlSelectNamedColumns() {
        SqlResult result = new QueryBuilder().select("id", "name").buildSql("users");
        assertEquals("SELECT id, name FROM users", result.getSql());
    }

    @Test
    void buildSqlWithDistinct() {
        SqlResult result = new QueryBuilder().distinct().buildSql("users");
        assertEquals("SELECT DISTINCT * FROM users", result.getSql());
    }

    @Test
    void buildSqlWithEqualityCondition() {
        SqlResult result = new QueryBuilder().whereEquals("id", 42).buildSql("users");
        assertEquals("SELECT * FROM users WHERE id = ?", result.getSql());
        assertEquals(List.of(42), result.getParameters());
    }

    @Test
    void buildSqlWithLikeCondition() {
        SqlResult result = new QueryBuilder().whereLike("name", "alice").buildSql("users");
        assertTrue(result.getSql().contains("name LIKE ?"));
        assertEquals(List.of("%alice%"), result.getParameters());
    }

    @Test
    void buildSqlWithNotLikeCondition() {
        SqlResult result = new QueryBuilder().whereNotLike("email", "test").buildSql("users");
        assertTrue(result.getSql().contains("email NOT LIKE ?"));
        assertEquals(List.of("%test%"), result.getParameters());
    }

    @Test
    void buildSqlWithExistsCondition() {
        SqlResult result = new QueryBuilder().whereExists("activated_at").buildSql("users");
        assertTrue(result.getSql().contains("activated_at IS NOT NULL"));
        assertTrue(result.getParameters().isEmpty());
    }

    @Test
    void buildSqlWithIsNullCondition() {
        SqlResult result = new QueryBuilder().whereNull("deleted_at").buildSql("users");
        assertTrue(result.getSql().contains("deleted_at IS NULL"));
        assertTrue(result.getParameters().isEmpty());
    }

    @Test
    void buildSqlWithIsNotNullCondition() {
        SqlResult result = new QueryBuilder().whereNotNull("email").buildSql("users");
        assertTrue(result.getSql().contains("email IS NOT NULL"));
        assertTrue(result.getParameters().isEmpty());
    }

    @Test
    void buildSqlWithInCondition() {
        SqlResult result = new QueryBuilder().whereIn("status", List.of("a", "b", "c")).buildSql("t");
        assertTrue(result.getSql().contains("status IN (?, ?, ?)"));
        assertEquals(List.of("a", "b", "c"), result.getParameters());
    }

    @Test
    void buildSqlWithNotInCondition() {
        SqlResult result = new QueryBuilder().whereNotIn("role", List.of("admin", "super")).buildSql("t");
        assertTrue(result.getSql().contains("role NOT IN (?, ?)"));
        assertEquals(List.of("admin", "super"), result.getParameters());
    }

    @Test
    void buildSqlWithBetweenCondition() {
        SqlResult result = new QueryBuilder().whereBetween("age", 18, 65).buildSql("users");
        assertTrue(result.getSql().contains("age BETWEEN ? AND ?"));
        assertEquals(List.of(18, 65), result.getParameters());
    }

    @Test
    void buildSqlWithGreaterThanCondition() {
        SqlResult result = new QueryBuilder().whereGreaterThan("score", 100).buildSql("t");
        assertTrue(result.getSql().contains("score > ?"));
        assertEquals(List.of(100), result.getParameters());
    }

    @Test
    void buildSqlWithGreaterThanOrEqualsCondition() {
        SqlResult result = new QueryBuilder().whereGreaterThanOrEquals("age", 18).buildSql("t");
        assertTrue(result.getSql().contains("age >= ?"));
        assertEquals(List.of(18), result.getParameters());
    }

    @Test
    void buildSqlWithLessThanOrEqualsCondition() {
        SqlResult result = new QueryBuilder().whereLessThanOrEquals("price", 99).buildSql("t");
        assertTrue(result.getSql().contains("price <= ?"));
        assertEquals(List.of(99), result.getParameters());
    }

    @Test
    void buildSqlWithMultipleAndConditions() {
        SqlResult result = new QueryBuilder()
                .whereEquals("status", "active")
                .whereEquals("role", "user")
                .buildSql("users");
        assertTrue(result.getSql().contains("status = ?"));
        assertTrue(result.getSql().contains("AND role = ?"));
        assertEquals(List.of("active", "user"), result.getParameters());
    }

    @Test
    void buildSqlWithOrCondition() {
        SqlResult result = new QueryBuilder()
                .whereEquals("role", "admin")
                .orWhereEquals("role", "super")
                .buildSql("users");
        assertTrue(result.getSql().contains("OR role = ?"));
        assertEquals(List.of("admin", "super"), result.getParameters());
    }

    @Test
    void buildSqlWithGroupBy() {
        SqlResult result = new QueryBuilder().select("dept").groupBy("dept").buildSql("employees");
        assertTrue(result.getSql().contains("GROUP BY dept"));
    }

    @Test
    void buildSqlWithHavingRaw() {
        SqlResult result = new QueryBuilder().groupBy("dept").havingRaw("COUNT(*) > 5").buildSql("t");
        assertTrue(result.getSql().contains("HAVING COUNT(*) > 5"));
    }

    @Test
    void buildSqlWithOrderByAsc() {
        SqlResult result = new QueryBuilder().orderBy("name", true).buildSql("t");
        assertTrue(result.getSql().contains("ORDER BY name ASC"));
    }

    @Test
    void buildSqlWithOrderByDesc() {
        SqlResult result = new QueryBuilder().orderBy("created_at", false).buildSql("t");
        assertTrue(result.getSql().contains("ORDER BY created_at DESC"));
    }

    @Test
    void buildSqlWithMultipleOrderByColumns() {
        SqlResult result = new QueryBuilder().orderBy("name", true).orderBy("age", false).buildSql("t");
        assertTrue(result.getSql().contains("name ASC, age DESC"));
    }

    @Test
    void buildSqlWithLimitAndOffset() {
        SqlResult result = new QueryBuilder().limit(10).offset(20).buildSql("t");
        assertTrue(result.getSql().contains("LIMIT 10"));
        assertTrue(result.getSql().contains("OFFSET 20"));
    }

    @Test
    void buildSqlWithDialectArgument() {
        SqlResult result = new QueryBuilder().whereEquals("id", 1).buildSql("users", SqlDialect.MYSQL);
        assertNotNull(result);
        assertTrue(result.getSql().contains("FROM `users`"));
        assertTrue(result.getSql().contains("`id` = ?"));
    }

    @Test
    void fromStoresTableUsedByBuildAndNoArgBuildSql() {
        QueryBuilder builder = new QueryBuilder().from("accounts").select("id");
        assertEquals("accounts", builder.build().getTable());
        SqlResult result = builder.buildSql();
        assertEquals("SELECT id FROM accounts", result.getSql());
    }

    @Test
    void buildSqlNoArgThrowsWhenNoTableSet() {
        assertThrows(IllegalStateException.class, () -> new QueryBuilder().buildSql());
    }
}
