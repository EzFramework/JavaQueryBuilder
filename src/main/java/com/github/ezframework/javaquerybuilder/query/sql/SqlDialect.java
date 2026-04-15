package com.github.ezframework.javaquerybuilder.query.sql;

import com.github.ezframework.javaquerybuilder.query.Query;
import com.github.ezframework.javaquerybuilder.query.sql.mysql.MySqlDialect;
import com.github.ezframework.javaquerybuilder.query.sql.sqlite.SqliteDialect;

/**
 * Strategy for rendering a {@link Query} to a SQL string.
 *
 * <p>Use the built-in constants for the most common dialects:
 * {@link #STANDARD}, {@link #MYSQL}, or {@link #SQLITE}.
 * For custom behaviour, extend {@link AbstractSqlDialect} and override
 * {@link AbstractSqlDialect#quoteIdentifier(String)}.
 *
 * @author EzFramework
 * @version 1.0.0
 */
public interface SqlDialect {

    /** Standard (ANSI) SQL — identifiers are not quoted. */
    SqlDialect STANDARD = new AbstractSqlDialect();

    /** MySQL dialect — identifiers are wrapped in back-tick quotes. */
    SqlDialect MYSQL = new MySqlDialect();

    /** SQLite dialect — identifiers are wrapped in double-quote characters. */
    SqlDialect SQLITE = new SqliteDialect();

    /**
     * Renders the given query to a parameterized {@link SqlResult}.
     *
     * @param query the query to render
     * @return the SQL result containing the SQL string and bound parameters
     */
    SqlResult render(Query query);
}
