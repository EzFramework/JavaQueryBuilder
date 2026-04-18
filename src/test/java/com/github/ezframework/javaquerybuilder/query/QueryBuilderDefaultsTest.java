package com.github.ezframework.javaquerybuilder.query;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.ezframework.javaquerybuilder.query.builder.DeleteBuilder;
import com.github.ezframework.javaquerybuilder.query.builder.QueryBuilder;
import com.github.ezframework.javaquerybuilder.query.builder.SelectBuilder;
import com.github.ezframework.javaquerybuilder.query.sql.SqlDialect;
import com.github.ezframework.javaquerybuilder.query.sql.SqlResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class QueryBuilderDefaultsTest {

    private QueryBuilderDefaults savedGlobal;

    @BeforeEach
    void saveGlobal() {
        savedGlobal = QueryBuilderDefaults.global();
    }

    @AfterEach
    void restoreGlobal() {
        QueryBuilderDefaults.setGlobal(savedGlobal);
    }

    // --- Default values ---

    @Test
    void globalHasCanonicalDefaultValues() {
        QueryBuilderDefaults defaults = QueryBuilderDefaults.global();
        assertSame(SqlDialect.STANDARD, defaults.getDialect());
        assertEquals("*", defaults.getDefaultColumns());
        assertEquals(-1, defaults.getDefaultLimit());
        assertEquals(-1, defaults.getDefaultOffset());
        assertEquals("%", defaults.getLikePrefix());
        assertEquals("%", defaults.getLikeSuffix());
    }

    @Test
    void builderProducesInstanceWithCanonicalDefaults() {
        QueryBuilderDefaults defaults = QueryBuilderDefaults.builder().build();
        assertSame(SqlDialect.STANDARD, defaults.getDialect());
        assertEquals("*", defaults.getDefaultColumns());
        assertEquals(-1, defaults.getDefaultLimit());
        assertEquals(-1, defaults.getDefaultOffset());
        assertEquals("%", defaults.getLikePrefix());
        assertEquals("%", defaults.getLikeSuffix());
    }

    @Test
    void builderFromSourceCopiesAllFields() {
        QueryBuilderDefaults source = QueryBuilderDefaults.builder()
            .dialect(SqlDialect.MYSQL)
            .defaultColumns("id, name")
            .defaultLimit(50)
            .defaultOffset(10)
            .likePrefix(">>")
            .likeSuffix("<<")
            .build();

        QueryBuilderDefaults copy = QueryBuilderDefaults.builder(source).build();

        assertSame(SqlDialect.MYSQL, copy.getDialect());
        assertEquals("id, name", copy.getDefaultColumns());
        assertEquals(50, copy.getDefaultLimit());
        assertEquals(10, copy.getDefaultOffset());
        assertEquals(">>", copy.getLikePrefix());
        assertEquals("<<", copy.getLikeSuffix());
        assertNotSame(source, copy);
    }

    // --- Null guards ---

    @Test
    void setGlobalNullThrowsNullPointerException() {
        assertThrows(NullPointerException.class, () -> QueryBuilderDefaults.setGlobal(null));
    }

    @Test
    void builderFromNullSourceThrowsNullPointerException() {
        assertThrows(NullPointerException.class, () -> QueryBuilderDefaults.builder(null));
    }

    @Test
    void builderDialectSetToNullThrowsNullPointerException() {
        assertThrows(NullPointerException.class,
            () -> QueryBuilderDefaults.builder().dialect(null));
    }

    @Test
    void builderDefaultColumnsSetToNullThrowsNullPointerException() {
        assertThrows(NullPointerException.class,
            () -> QueryBuilderDefaults.builder().defaultColumns(null));
    }

    @Test
    void builderLikePrefixSetToNullThrowsNullPointerException() {
        assertThrows(NullPointerException.class,
            () -> QueryBuilderDefaults.builder().likePrefix(null));
    }

    @Test
    void builderLikeSuffixSetToNullThrowsNullPointerException() {
        assertThrows(NullPointerException.class,
            () -> QueryBuilderDefaults.builder().likeSuffix(null));
    }

    @Test
    void withDefaultsNullThrowsNullPointerExceptionOnQueryBuilder() {
        assertThrows(NullPointerException.class,
            () -> new QueryBuilder().withDefaults(null));
    }

    @Test
    void withDefaultsNullThrowsNullPointerExceptionOnDeleteBuilder() {
        assertThrows(NullPointerException.class,
            () -> new DeleteBuilder().withDefaults(null));
    }

    @Test
    void withDefaultsNullThrowsNullPointerExceptionOnSelectBuilder() {
        assertThrows(NullPointerException.class,
            () -> new SelectBuilder().withDefaults(null));
    }

    // --- Global dialect applied by new builders ---

    @Test
    void globalDialectIsUsedByNewlyCreatedQueryBuilder() {
        QueryBuilderDefaults.setGlobal(
            QueryBuilderDefaults.builder().dialect(SqlDialect.MYSQL).build());

        SqlResult result = new QueryBuilder().from("users").buildSql();

        // MySQL dialect wraps identifiers in backticks
        assertTrue(result.getSql().contains("`users`"),
            "Expected MySQL backtick quoting, got: " + result.getSql());
    }

    @Test
    void setGlobalUpdatesGlobalInstance() {
        QueryBuilderDefaults custom = QueryBuilderDefaults.builder()
            .dialect(SqlDialect.SQLITE).build();
        QueryBuilderDefaults.setGlobal(custom);
        assertSame(custom, QueryBuilderDefaults.global());
    }

    // --- Per-instance withDefaults overrides global ---

    @Test
    void withDefaultsDialectOverridesGlobalForThatInstance() {
        // Global stays STANDARD; only this instance uses MYSQL
        QueryBuilderDefaults mysql = QueryBuilderDefaults.builder()
            .dialect(SqlDialect.MYSQL).build();

        SqlResult mysqlResult = new QueryBuilder().withDefaults(mysql).from("orders").buildSql();
        SqlResult stdResult = new QueryBuilder().from("orders").buildSql();

        assertTrue(mysqlResult.getSql().contains("`orders`"),
            "Expected MySQL quoting: " + mysqlResult.getSql());
        assertTrue(stdResult.getSql().contains("orders") && !stdResult.getSql().contains("`orders`"),
            "Expected standard quoting: " + stdResult.getSql());
    }

    @Test
    void explicitDialectParameterWinsOverDefaultsDialect() {
        // withDefaults sets MYSQL, but explicit buildSql(table, SQLITE) should win
        QueryBuilderDefaults mysql = QueryBuilderDefaults.builder()
            .dialect(SqlDialect.MYSQL).build();

        SqlResult result = new QueryBuilder()
            .withDefaults(mysql)
            .buildSql("users", SqlDialect.SQLITE);

        // SQLite wraps identifiers in double-quotes
        assertTrue(result.getSql().contains("\"users\""),
            "Expected SQLite double-quote quoting: " + result.getSql());
    }

    // --- Default SELECT columns ---

    @Test
    void defaultColumnsAppearsInSelectWhenNoneSpecified() {
        QueryBuilderDefaults defaults = QueryBuilderDefaults.builder()
            .defaultColumns("id, name").build();

        SqlResult result = new QueryBuilder().withDefaults(defaults).from("users").buildSql();

        assertTrue(result.getSql().startsWith("SELECT id, name FROM"),
            "Expected custom default columns: " + result.getSql());
    }

    @Test
    void explicitSelectColumnsWinsOverDefaultColumns() {
        QueryBuilderDefaults defaults = QueryBuilderDefaults.builder()
            .defaultColumns("id, name").build();

        SqlResult result = new QueryBuilder().withDefaults(defaults)
            .select("age")
            .from("users").buildSql();

        assertTrue(result.getSql().startsWith("SELECT age FROM"),
            "Explicit select should win: " + result.getSql());
    }

    // --- Default limit and offset ---

    @Test
    void defaultLimitAppliedWhenBuilderLimitNotSet() {
        QueryBuilderDefaults defaults = QueryBuilderDefaults.builder()
            .defaultLimit(100).build();

        SqlResult result = new QueryBuilder().withDefaults(defaults).from("users").buildSql();

        assertTrue(result.getSql().contains("LIMIT 100"),
            "Expected LIMIT 100: " + result.getSql());
    }

    @Test
    void explicitLimitWinsOverDefaultLimit() {
        QueryBuilderDefaults defaults = QueryBuilderDefaults.builder()
            .defaultLimit(100).build();

        SqlResult result = new QueryBuilder().withDefaults(defaults)
            .limit(25)
            .from("users").buildSql();

        assertTrue(result.getSql().contains("LIMIT 25"),
            "Expected LIMIT 25: " + result.getSql());
        assertTrue(!result.getSql().contains("LIMIT 100"),
            "Default limit must not appear when explicit limit is set");
    }

    @Test
    void defaultOffsetAppliedWhenBuilderOffsetNotSet() {
        QueryBuilderDefaults defaults = QueryBuilderDefaults.builder()
            .defaultOffset(20).build();

        SqlResult result = new QueryBuilder().withDefaults(defaults).from("users").buildSql();

        assertTrue(result.getSql().contains("OFFSET 20"),
            "Expected OFFSET 20: " + result.getSql());
    }

    @Test
    void explicitOffsetWinsOverDefaultOffset() {
        QueryBuilderDefaults defaults = QueryBuilderDefaults.builder()
            .defaultOffset(20).build();

        SqlResult result = new QueryBuilder().withDefaults(defaults)
            .offset(5)
            .from("users").buildSql();

        assertTrue(result.getSql().contains("OFFSET 5"),
            "Expected OFFSET 5: " + result.getSql());
    }

    // --- LIKE prefix / suffix ---

    @Test
    void likeParamUsesCustomPrefixAndSuffix() {
        QueryBuilderDefaults defaults = QueryBuilderDefaults.builder()
            .likePrefix(">>")
            .likeSuffix("<<")
            .build();

        SqlResult result = new QueryBuilder().withDefaults(defaults)
            .whereLike("name", "bob")
            .from("users").buildSql();

        List<Object> params = result.getParameters();
        assertEquals(1, params.size());
        assertEquals(">>bob<<", params.get(0));
    }

    @Test
    void likeParamWithEmptyPrefixAndSuffixIsUnwrapped() {
        QueryBuilderDefaults defaults = QueryBuilderDefaults.builder()
            .likePrefix("")
            .likeSuffix("")
            .build();

        SqlResult result = new QueryBuilder().withDefaults(defaults)
            .whereLike("name", "bob")
            .from("users").buildSql();

        List<Object> params = result.getParameters();
        assertEquals(1, params.size());
        assertEquals("bob", params.get(0));
    }

    // --- DeleteBuilder ---

    @Test
    void deleteBuilderWithDefaultsDialectIsUsed() {
        QueryBuilderDefaults mysql = QueryBuilderDefaults.builder()
            .dialect(SqlDialect.MYSQL).build();

        SqlResult result = new DeleteBuilder()
            .withDefaults(mysql)
            .from("orders")
            .whereEquals("id", 1)
            .build();

        assertTrue(result.getSql().contains("`orders`"),
            "Expected MySQL backtick quoting on table: " + result.getSql());
        assertTrue(result.getSql().contains("`id`"),
            "Expected MySQL backtick quoting on column: " + result.getSql());
    }

    // --- SelectBuilder ---

    @Test
    void selectBuilderDefaultColumnsApplied() {
        QueryBuilderDefaults defaults = QueryBuilderDefaults.builder()
            .defaultColumns("id, status").build();

        SqlResult result = new SelectBuilder().withDefaults(defaults).from("users").build();

        assertTrue(result.getSql().startsWith("SELECT id, status FROM"),
            "Expected custom default columns: " + result.getSql());
    }

    @Test
    void selectBuilderDefaultLimitApplied() {
        QueryBuilderDefaults defaults = QueryBuilderDefaults.builder()
            .defaultLimit(30).build();

        SqlResult result = new SelectBuilder().withDefaults(defaults).from("users").build();

        assertTrue(result.getSql().contains("LIMIT 30"),
            "Expected LIMIT 30: " + result.getSql());
    }

    @Test
    void selectBuilderDefaultOffsetApplied() {
        QueryBuilderDefaults defaults = QueryBuilderDefaults.builder()
            .defaultLimit(10)
            .defaultOffset(5)
            .build();

        SqlResult result = new SelectBuilder().withDefaults(defaults).from("users").build();

        assertTrue(result.getSql().contains("OFFSET 5"),
            "Expected OFFSET 5: " + result.getSql());
    }

    @Test
    void selectBuilderLikeWrapsWithDefaults() {
        QueryBuilderDefaults defaults = QueryBuilderDefaults.builder()
            .likePrefix("{{")
            .likeSuffix("}}")
            .build();

        SqlResult result = new SelectBuilder().withDefaults(defaults)
            .from("users")
            .whereLike("name", "alice")
            .build();

        List<Object> params = result.getParameters();
        assertEquals(1, params.size());
        assertEquals("{{alice}}", params.get(0));
    }
}
