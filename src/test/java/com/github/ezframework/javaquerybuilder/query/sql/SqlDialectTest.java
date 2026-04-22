package com.github.ezframework.javaquerybuilder.query.sql;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SqlDialectTest {
    @Test
    void standardDialectIsSingleton() {
        assertSame(SqlDialect.STANDARD, SqlDialect.STANDARD);
    }
    @Test
    void mysqlDialectIsSingleton() {
        assertSame(SqlDialect.MYSQL, SqlDialect.MYSQL);
    }
    @Test
    void sqliteDialectIsSingleton() {
        assertSame(SqlDialect.SQLITE, SqlDialect.SQLITE);
    }

    @Test
    void postgresqlDialectIsSingleton() {
        assertSame(SqlDialect.POSTGRESQL, SqlDialect.POSTGRESQL);
    }
}
