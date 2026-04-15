package com.github.ezframework.javaquerybuilder.query.sql;

import com.github.ezframework.javaquerybuilder.query.builder.QueryBuilder;
import com.github.ezframework.javaquerybuilder.query.sql.SqlResult;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AbstractSqlDialectTest {

    static class DummyDialect extends AbstractSqlDialect {}

    @Test
    void canInstantiateDummyDialect() {
        DummyDialect dialect = new DummyDialect();
        assertNotNull(dialect);
    }

    @Test
    void rendersBasicSelectAll() {
        final DummyDialect dialect = new DummyDialect();
        final SqlResult result = new QueryBuilder().from("users").buildSql("users", dialect);
        assertEquals("SELECT * FROM users", result.getSql());
    }

    @Test
    void rendersSelectWithNamedColumns() {
        final DummyDialect dialect = new DummyDialect();
        final SqlResult result = new QueryBuilder()
            .select("id", "email")
            .buildSql("accounts", dialect);
        assertEquals("SELECT id, email FROM accounts", result.getSql());
    }
}

