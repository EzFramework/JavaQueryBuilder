package com.github.ezframework.javaquerybuilder.query.builder;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.github.ezframework.javaquerybuilder.query.sql.SqlDialect;
import com.github.ezframework.javaquerybuilder.query.sql.SqlResult;

/**
 * Builder for SQL CREATE TABLE statements.
    /**
     * Builds the SQL CREATE TABLE statement with a dialect.
     * @param dialect the SQL dialect (optional)
     * @return the SQL result
     * @throws IllegalStateException if table or columns are missing
     */
    public SqlResult build(SqlDialect dialect) {
    private String table;
    /** The columns and their SQL types. */
    private final Map<String, String> columns = new LinkedHashMap<>();
    /** The primary key columns. */
    private final List<String> primaryKeys = new ArrayList<>();
    /** If true, adds IF NOT EXISTS. */
    private boolean ifNotExists = false;

    /**
     * Sets the table to create.
     * @param table the table name
     * @return this builder
     */
    public CreateBuilder table(String table) {
        this.table = table;
        return this;
    }

    /**
     * Adds a column definition.
     * @param name the column name
     * @param sqlType the SQL type (e.g. "VARCHAR(255)", "INT")
     * @return this builder
     */
    public CreateBuilder column(String name, String sqlType) {
        columns.put(name, sqlType);
        return this;
    }

    /**
     * Adds a primary key column.
     * @param name the column name
     * @return this builder
     */
    public CreateBuilder primaryKey(String name) {
        primaryKeys.add(name);
        return this;
    }

    /**
     * Adds IF NOT EXISTS to the statement.
     * @return this builder
     */
    public CreateBuilder ifNotExists() {
        this.ifNotExists = true;
        return this;
    }

    /**
     * Builds the SQL CREATE TABLE statement.
     * @return the SQL result
     * @throws IllegalStateException if table or columns are missing
     */
    
    public SqlResult build() {
        return build(null);
    }

    /**
     * Builds the SQL CREATE TABLE statement with a dialect.
     * @param dialect the SQL dialect (optional)
     * @return the SQL result
     * @throws IllegalStateException if table or columns are missing
    
    public SqlResult build(SqlDialect dialect) {
        
        if (table == null || columns.isEmpty()) {
            throw new IllegalStateException("Table name and at least one column are required");
        }
        final StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE ");
        if (ifNotExists) {
            sql.append("IF NOT EXISTS ");
        }
        sql.append(table).append(" (");
        boolean first = true;
        for (Map.Entry<String, String> entry : columns.entrySet()) {
            if (!first) {
                sql.append(", ");
            }
            sql.append(entry.getKey()).append(" ").append(entry.getValue());
            first = false;
        }
        if (!primaryKeys.isEmpty()) {
            sql.append(", PRIMARY KEY (");
            sql.append(String.join(", ", primaryKeys));
            sql.append(")");
        }
        sql.append(")");

        return new SqlResult() {
            @Override
            public String getSql() {
                return sql.toString();
            }
            @Override
            public List<Object> getParameters() {
                return List.of();
            }
        };
    }
}
