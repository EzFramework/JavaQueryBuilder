package com.github.ezframework.javaquerybuilder.query.builder;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.github.ezframework.javaquerybuilder.query.condition.Condition;
import com.github.ezframework.javaquerybuilder.query.condition.ConditionEntry;
import com.github.ezframework.javaquerybuilder.query.condition.Connector;
import com.github.ezframework.javaquerybuilder.query.condition.Operator;
import com.github.ezframework.javaquerybuilder.query.sql.SqlDialect;
import com.github.ezframework.javaquerybuilder.query.sql.SqlResult;

/**
 * Builder for SQL UPDATE statements.
 *
 * @author EzFramework
 * @version 1.0.0
 */
public class UpdateBuilder {

    /** The table to update. */
    private String table;

    /** The columns and values to set. */
    private final Map<String, Object> setColumns = new LinkedHashMap<>();

    /** The WHERE conditions. */
    private final List<ConditionEntry> conditions = new ArrayList<>();

    /**
     * Sets the table to update.
     * @param table the table name
     * @return this builder
     */
    public UpdateBuilder table(String table) {
        this.table = table;
        return this;
    }

    /**
     * Adds a column and value to set.
     * @param column the column name
     * @param value the value to set
     * @return this builder
     */
    public UpdateBuilder set(String column, Object value) {
        setColumns.put(column, value);
        return this;
    }

    /**
     * Adds an equality WHERE condition.
     * @param column the column name
     * @param value the value to compare
     * @return this builder
     */
    public UpdateBuilder whereEquals(String column, Object value) {
        conditions.add(new ConditionEntry(column, new Condition(Operator.EQ, value),
            conditions.isEmpty() ? Connector.AND : Connector.AND));
        return this;
    }

    /**
     * Adds an OR equality WHERE condition.
     * @param column the column name
     * @param value the value to compare
     * @return this builder
     */
    public UpdateBuilder orWhereEquals(String column, Object value) {
        conditions.add(new ConditionEntry(column, new Condition(Operator.EQ, value), Connector.OR));
        return this;
    }

    /**
     * Adds a greater-than-or-equals WHERE condition.
     * @param column the column name
     * @param value the value to compare
     * @return this builder
     */
    public UpdateBuilder whereGreaterThanOrEquals(String column, int value) {
        conditions.add(new ConditionEntry(column, new Condition(Operator.GTE, value),
            conditions.isEmpty() ? Connector.AND : Connector.AND));
        return this;
    }

    /**
     * Builds the SQL UPDATE statement.
     * @return the SQL result
     */
    public SqlResult build() {
        return build(null);
    }

    /**
     * Builds the SQL UPDATE statement with a dialect.
     * @param dialect the SQL dialect
     * @return the SQL result
     */
    public SqlResult build(SqlDialect dialect) {
        final StringBuilder sql = new StringBuilder();
        final List<Object> params = new ArrayList<>();

        sql.append("UPDATE ").append(table).append(" SET ");
        int i = 0;
        for (Map.Entry<String, Object> entry : setColumns.entrySet()) {
            if (i++ > 0) {
                sql.append(", ");
            }
            sql.append(entry.getKey()).append(" = ?");
            params.add(entry.getValue());
        }

        if (!conditions.isEmpty()) {
            sql.append(" WHERE ");
            for (int j = 0; j < conditions.size(); j++) {
                final ConditionEntry cond = conditions.get(j);
                if (j > 0) {
                    sql.append(" ").append(cond.getConnector().name()).append(" ");
                }
                sql.append(cond.getColumn()).append(" = ?");
                params.add(cond.getCondition().getValue());
            }
        }

        return new SqlResult() {
            @Override
            public String getSql() {
                return sql.toString();
            }

            @Override
            public List<Object> getParameters() {
                return params;
            }
        };
    }
}
