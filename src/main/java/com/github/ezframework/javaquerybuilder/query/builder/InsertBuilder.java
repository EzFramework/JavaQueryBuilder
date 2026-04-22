package com.github.ezframework.javaquerybuilder.query.builder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.ezframework.javaquerybuilder.query.sql.SqlDialect;
import com.github.ezframework.javaquerybuilder.query.sql.SqlResult;

/**
 * Builder for SQL INSERT statements.
 *
 * @author EzFramework
 * @version 1.0.0
 */
public class InsertBuilder {

    /** The table to insert into. */
    private String table;

    /** The columns to insert values into. */
    private final List<String> columns = new ArrayList<>();

    /** The values to insert. */
    private final List<Object> values = new ArrayList<>();

    /** The RETURNING columns (PostgreSQL only). */
    private final List<String> returningColumns = new ArrayList<>();

    /**
     * Sets the table to insert into.
     *
     * @param table the table name
     * @return this builder instance for chaining
     */
    public InsertBuilder into(String table) {
        this.table = table;
        return this;
    }

    /**
     * Adds a column-value pair to the INSERT statement.
     *
     * @param column the column name
     * @param value  the value to insert
     * @return this builder instance for chaining
     */
    public InsertBuilder value(String column, Object value) {
        columns.add(column);
        values.add(value);
        return this;
    }

    /**
     * Specifies the columns to include in a {@code RETURNING} clause (PostgreSQL only).
     *
     * <p>The {@code RETURNING} clause is appended unconditionally to the SQL string;
     * it is the caller's responsibility to use a PostgreSQL connection.
     *
     * @param columns one or more column names; must not be {@code null} or empty
     * @return this builder instance for chaining
     */
    public InsertBuilder returning(final String... columns) {
        returningColumns.addAll(java.util.Arrays.asList(columns));
        return this;
    }

    /**
     * Builds the SQL INSERT statement using the default dialect.
     *
     * @return the SQL result
     */
    public SqlResult build() {
        return build(null);
    }

    /**
     * Builds the SQL INSERT statement.
     *
     * @param dialect the SQL dialect (optional)
     * @return the SQL result
     */
    public SqlResult build(SqlDialect dialect) {
        final StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO ").append(table).append(" (");
        sql.append(String.join(", ", columns));
        sql.append(") VALUES (");
        sql.append(String.join(", ", Collections.nCopies(values.size(), "?")));
        sql.append(")");
        if (!returningColumns.isEmpty()) {
            sql.append(" RETURNING ").append(String.join(", ", returningColumns));
        }
        return new SqlResult() {
            @Override
            public String getSql() {
                return sql.toString();
            }

            @Override
            public List<Object> getParameters() {
                return values;
            }
        };
    }
}
