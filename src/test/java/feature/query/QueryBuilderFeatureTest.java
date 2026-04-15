package feature.query;

import com.github.ezframework.javaquerybuilder.query.builder.QueryBuilder;
import com.github.ezframework.javaquerybuilder.query.sql.SqlDialect;
import com.github.ezframework.javaquerybuilder.query.sql.SqlResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
        String sql = result.getSql();
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
        QueryBuilder builder = new QueryBuilder().select("id");
        assertThrows(IllegalStateException.class, builder::buildSql);
    }
}
