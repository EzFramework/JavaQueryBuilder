package com.github.ezframework.javaquerybuilder.query.sql.sqlite;

import com.github.ezframework.javaquerybuilder.query.sql.AbstractSqlDialect;

/**
 * SQLite SQL dialect.
 *
 * <p>Wraps every identifier (table names, column names) in double-quote
 * characters as required by the SQL standard and supported by SQLite.
 *
 * @author EzFramework
 * @version 1.0.0
 */
public class SqliteDialect extends AbstractSqlDialect {

    @Override
    protected String quoteIdentifier(String name) {
        return "\"" + name + "\"";
    }
}
