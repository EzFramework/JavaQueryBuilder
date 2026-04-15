package com.github.ezframework.javaquerybuilder.query.builder;

import java.util.ArrayList;
import java.util.List;

import com.github.ezframework.javaquerybuilder.query.condition.Condition;
import com.github.ezframework.javaquerybuilder.query.condition.ConditionEntry;
import com.github.ezframework.javaquerybuilder.query.condition.Connector;
import com.github.ezframework.javaquerybuilder.query.condition.Operator;
import com.github.ezframework.javaquerybuilder.query.sql.SqlDialect;
import com.github.ezframework.javaquerybuilder.query.sql.SqlResult;

/**
 * Builder for SQL DELETE statements.
 *
 * @author EzFramework
 * @version 1.0.0
 */
public class DeleteBuilder {

    /** The table to delete from. */
    private String table;

    /** The WHERE conditions. */
    private final List<ConditionEntry> conditions = new ArrayList<>();

    /**
     * Sets the table to delete from.
     * @param table the table name
     * @return this builder
     */
    public DeleteBuilder from(String table) {
        this.table = table;
        return this;
    }

    /**
     * Adds an equality WHERE condition.
     * @param column the column name
     * @param value the value to compare
     * @return this builder
     */
    public DeleteBuilder whereEquals(String column, Object value) {

        conditions.add(new ConditionEntry(column, new Condition(Operator.EQ, value), Connector.AND));

        return this;
    }

    /**
     * Adds a less-than WHERE condition.
     * @param column the column name
     * @param value the value to compare
     * @return this builder
     */
    public DeleteBuilder whereLessThan(String column, Object value) {

        conditions.add(new ConditionEntry(column, new Condition(Operator.LT, value), Connector.AND));

        return this;
    }

    /**
     * Builds the SQL DELETE statement.
     *
     * @return the SQL result
     * @throws UnsupportedOperationException if the operation is not supported
     */
    public SqlResult build() {
        return build(null);
    }

    /**
     * Builds the SQL DELETE statement with a dialect.
     *
     * @param dialect the SQL dialect
     * @return the SQL result
     * @throws UnsupportedOperationException if the operator is not supported
     */
    public SqlResult build(SqlDialect dialect) {
        final StringBuilder sql = new StringBuilder();
        final List<Object> params = new ArrayList<>();

        sql.append("DELETE FROM ").append(table);
        if (!conditions.isEmpty()) {
            sql.append(" WHERE ");
            for (int i = 0; i < conditions.size(); i++) {
                final ConditionEntry cond = conditions.get(i);
                if (i > 0) {
                    sql.append(" ").append(cond.getConnector().name()).append(" ");
                }
                final Operator op = cond.getCondition().getOperator();
                sql.append(cond.getColumn());
                switch (op) {
                    case EQ:
                        sql.append(" = ?");
                        params.add(cond.getCondition().getValue());
                        break;
                    case LT:
                        sql.append(" < ?");
                        params.add(cond.getCondition().getValue());
                        break;
                    default:
                        throw new UnsupportedOperationException("Operator not supported in DeleteBuilder: " + op);
                }
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
