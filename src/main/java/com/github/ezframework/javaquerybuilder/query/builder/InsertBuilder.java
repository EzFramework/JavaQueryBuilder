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

    public InsertBuilder into(String table) {
        this.table = table;
        return this;
    }

    public InsertBuilder value(String column, Object value) {
        columns.add(column);
        values.add(value);
        return this;
    }

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
