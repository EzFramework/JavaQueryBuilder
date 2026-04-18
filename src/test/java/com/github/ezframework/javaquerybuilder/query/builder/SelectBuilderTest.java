package com.github.ezframework.javaquerybuilder.query.builder;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import com.github.ezframework.javaquerybuilder.query.sql.SqlDialect;
import com.github.ezframework.javaquerybuilder.query.sql.SqlResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SelectBuilderTest {

    @Test
    void testSimpleSelect() {
        SqlResult sql = new SelectBuilder()
                .from("users")
                .select("id", "name")
                .build();
        assertEquals("SELECT id, name FROM users", sql.getSql());
        assertTrue(sql.getParameters().isEmpty());
    }

    @Test
    void testSelectAll() {
        SqlResult sql = new SelectBuilder()
                .from("users")
                .build();
        assertEquals("SELECT * FROM users", sql.getSql());
        assertTrue(sql.getParameters().isEmpty());
    }

    @Test
    void testDistinct() {
        SqlResult sql = new SelectBuilder()
                .from("users")
                .select("email")
                .distinct()
                .build();
        assertEquals("SELECT DISTINCT email FROM users", sql.getSql());
    }

    @Test
    void testWhereEquals() {
        SqlResult sql = new SelectBuilder()
                .from("users")
                .whereEquals("id", 42)
                .build();
        assertEquals("SELECT * FROM users WHERE id = ?", sql.getSql());
        assertEquals(Collections.singletonList(42), sql.getParameters());
    }

    @Test
    void testWhereIn() {
        SqlResult sql = new SelectBuilder()
                .from("users")
                .whereIn("id", Arrays.asList(1, 2, 3))
                .build();
        assertEquals("SELECT * FROM users WHERE id IN (?, ?, ?)", sql.getSql());
        assertEquals(Arrays.asList(1, 2, 3), sql.getParameters());
    }

    @Test
    void testWhereLike() {
        SqlResult sql = new SelectBuilder()
                .from("users")
                .whereLike("name", "bob")
                .build();
        assertEquals("SELECT * FROM users WHERE name LIKE ?", sql.getSql());
        assertEquals(Collections.singletonList("%bob%"), sql.getParameters());
    }

    @Test
    void testGroupByOrderByLimitOffset() {
        SqlResult sql = new SelectBuilder()
                .from("users")
                .select("id", "name")
                .groupBy("name")
                .orderBy("id", false)
                .limit(10)
                .offset(5)
                .build();
        assertEquals("SELECT id, name FROM users GROUP BY name ORDER BY id DESC LIMIT 10 OFFSET 5", sql.getSql());
    }

    @Test
    void testMissingTableThrows() {
        final SelectBuilder builder = new SelectBuilder();
        final IllegalStateException ex = assertThrows(IllegalStateException.class, builder::build);
        assertTrue(ex.getMessage().contains("Table name"));
    }

    @Test
    void testBuildWithDialect() {
        SqlResult sql = new SelectBuilder()
                .from("users")
                .select("id")
                .whereEquals("active", true)
                .build(SqlDialect.STANDARD);
        // SelectBuilder does not apply dialect-based identifier quoting
        assertEquals("SELECT id FROM users WHERE active = ?", sql.getSql());
        assertEquals(Collections.singletonList(true), sql.getParameters());
    }

    @Test
    void testMultipleWhereConditions() {
        SqlResult sql = new SelectBuilder()
                .from("products")
                .whereEquals("category", "electronics")
                .whereEquals("in_stock", true)
                .build();
        assertEquals("SELECT * FROM products WHERE category = ? AND in_stock = ?", sql.getSql());
        assertEquals(Arrays.asList("electronics", true), sql.getParameters());
    }

    @Test
    void testMultipleOrderByColumns() {
        SqlResult sql = new SelectBuilder()
                .from("employees")
                .orderBy("department", true)
                .orderBy("salary", false)
                .build();
        assertEquals("SELECT * FROM employees ORDER BY department ASC, salary DESC", sql.getSql());
    }

    @Test
    void testMultipleGroupByColumns() {
        SqlResult sql = new SelectBuilder()
                .from("sales")
                .select("region", "product")
                .groupBy("region", "product")
                .build();
        assertEquals("SELECT region, product FROM sales GROUP BY region, product", sql.getSql());
    }

    @Test
    void testLimitWithoutOffset() {
        SqlResult sql = new SelectBuilder().from("t").limit(25).build();
        assertEquals("SELECT * FROM t LIMIT 25", sql.getSql());
    }

    @Test
    void testOffsetWithoutLimit() {
        SqlResult sql = new SelectBuilder().from("t").offset(10).build();
        assertEquals("SELECT * FROM t OFFSET 10", sql.getSql());
    }

    @Test
    void testWhereEqualsChainsCorrectly() {
        SqlResult sql = new SelectBuilder()
                .from("t")
                .whereEquals("a", 1)
                .whereEquals("b", 2)
                .whereEquals("c", 3)
                .build();
        assertEquals("SELECT * FROM t WHERE a = ? AND b = ? AND c = ?", sql.getSql());
        assertEquals(Arrays.asList(1, 2, 3), sql.getParameters());
    }
}
