package feature.query;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.github.ezframework.javaquerybuilder.query.builder.QueryBuilder;
import com.github.ezframework.javaquerybuilder.query.sql.SqlDialect;
import com.github.ezframework.javaquerybuilder.query.sql.SqlResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Feature test: End-to-end SQL generation for complex queries.
 */
public class QueryBuilderFeatureTest {

    @Test
    @DisplayName("Builds a complex SELECT with multiple conditions and dialect")
    void buildsComplexSelectWithDialect() {
        SqlResult result = new QueryBuilder()
            .select("id", "name", "email")
            .from("users")
            .whereEquals("status", "active")
            .whereLike("email", "@example.com")
            .whereGreaterThan("score", 100)
            .orderBy("created_at", false)
            .limit(5)
            .offset(10)
            .buildSql("users", SqlDialect.MYSQL);
        final String sql = result.getSql();
        assertTrue(sql.contains("SELECT `id`, `name`, `email` FROM `users`"), sql);
        assertTrue(sql.contains("`status` = ?"), sql);
        assertTrue(sql.contains("`email` LIKE ?"), sql);
        assertTrue(sql.contains("`score` > ?"), sql);
        assertTrue(sql.contains("ORDER BY `created_at` DESC"), sql);
        assertTrue(sql.contains("LIMIT 5"), sql);
        assertTrue(sql.contains("OFFSET 10"), sql);
        assertEquals(List.of("active", "%@example.com%", 100), result.getParameters());
    }

    @Test
    @DisplayName("Throws when building SQL without table")
    void throwsWhenNoTable() {
        final QueryBuilder builder = new QueryBuilder().select("id");
        assertThrows(IllegalStateException.class, builder::buildSql);
    }

    @Test
    @DisplayName("insertInto shortcut produces correct parameterized INSERT")
    void insertIntoShortcutEndToEnd() {
        SqlResult result = QueryBuilder.insertInto("users")
            .value("name", "Alice")
            .value("email", "alice@example.com")
            .value("age", 30)
            .build();
        assertEquals("INSERT INTO users (name, email, age) VALUES (?, ?, ?)", result.getSql());
        assertEquals(List.of("Alice", "alice@example.com", 30), result.getParameters());
    }

    @Test
    @DisplayName("deleteFrom shortcut with multiple conditions produces correct DELETE")
    void deleteFromShortcutEndToEnd() {
        SqlResult result = QueryBuilder.deleteFrom("audit_log")
            .whereLessThan("created_at", "2024-01-01")
            .whereNotEquals("level", "ERROR")
            .build();
        assertEquals("DELETE FROM audit_log WHERE created_at < ? AND level != ?", result.getSql());
        assertEquals(List.of("2024-01-01", "ERROR"), result.getParameters());
    }

    @Test
    @DisplayName("deleteFrom shortcut with IN and NOT IN produces correct DELETE")
    void deleteFromWithInAndNotIn() {
        SqlResult result = QueryBuilder.deleteFrom("sessions")
            .whereIn("status", List.of("expired", "revoked"))
            .whereNotIn("user_id", List.of(1, 2, 3))
            .build();
        assertEquals(
            "DELETE FROM sessions WHERE status IN (?, ?) AND user_id NOT IN (?, ?, ?)",
            result.getSql());
        assertEquals(List.of("expired", "revoked", 1, 2, 3), result.getParameters());
    }

    @Test
    @DisplayName("update shortcut with set and where produces correct UPDATE")
    void updateShortcutEndToEnd() {
        SqlResult result = QueryBuilder.update("subscribers")
            .set("active", false)
            .set("unsubscribed_at", "2026-04-16")
            .whereEquals("email", "spam@example.com")
            .build();
        final String sql = result.getSql();
        assertTrue(sql.startsWith("UPDATE subscribers SET"), sql);
        assertTrue(sql.contains("active = ?"), sql);
        assertTrue(sql.contains("unsubscribed_at = ?"), sql);
        assertTrue(sql.contains("WHERE email = ?"), sql);
        assertTrue(result.getParameters().contains("spam@example.com"));
    }

    @Test
    @DisplayName("createTable shortcut with composite PK and IF NOT EXISTS")
    void createTableShortcutEndToEnd() {
        SqlResult result = QueryBuilder.createTable("permissions")
            .column("user_id", "INT")
            .column("resource_id", "INT")
            .column("granted_at", "TIMESTAMP")
            .primaryKey("user_id")
            .primaryKey("resource_id")
            .ifNotExists()
            .build();
        assertEquals(
            "CREATE TABLE IF NOT EXISTS permissions"
            + " (user_id INT, resource_id INT, granted_at TIMESTAMP,"
            + " PRIMARY KEY (user_id, resource_id))",
            result.getSql());
        assertTrue(result.getParameters().isEmpty());
    }

    @Test
    @DisplayName("deleteFrom with BETWEEN produces correct range DELETE")
    void deleteFromWithBetween() {
        SqlResult result = QueryBuilder.deleteFrom("metrics")
            .whereBetween("recorded_at", "2024-01-01", "2024-12-31")
            .build();
        assertEquals("DELETE FROM metrics WHERE recorded_at BETWEEN ? AND ?", result.getSql());
        assertEquals(List.of("2024-01-01", "2024-12-31"), result.getParameters());
    }
}
