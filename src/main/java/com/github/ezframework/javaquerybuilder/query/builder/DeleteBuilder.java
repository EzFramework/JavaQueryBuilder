package com.github.ezframework.javaquerybuilder.query.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    /** Simple comparison operators mapped to their SQL fragments. */
    private static final Map<Operator, String> SIMPLE_OPS = Map.of(
        Operator.EQ, " = ?",
        Operator.NEQ, " != ?",
        Operator.GT, " > ?",
        Operator.GTE, " >= ?",
        Operator.LT, " < ?",
        Operator.LTE, " <= ?"
    );

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
     * Adds an IN WHERE condition.
     * @param column the column name
     * @param values the collection of values for the IN clause
     * @return this builder
     * @throws IllegalArgumentException if values is null or empty
     */
    public DeleteBuilder whereIn(String column, List<?> values) {
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("IN value list must not be null or empty");
        }
        conditions.add(new ConditionEntry(column, new Condition(Operator.IN, values), Connector.AND));
        return this;
    }

    /**
     * Adds a NOT IN WHERE condition.
     * @param column the column name
     * @param values the collection of values for the NOT IN clause
     * @return this builder
     * @throws IllegalArgumentException if values is null or empty
     */
    public DeleteBuilder whereNotIn(String column, List<?> values) {
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("NOT IN value list must not be null or empty");
        }
        conditions.add(new ConditionEntry(column, new Condition(Operator.NOT_IN, values), Connector.AND));
        return this;
    }

    /**
     * Adds a BETWEEN WHERE condition.
     * @param column the column name
     * @param from the lower bound value (inclusive)
     * @param to the upper bound value (inclusive)
     * @return this builder
     */
    public DeleteBuilder whereBetween(String column, Object from, Object to) {
        conditions.add(new ConditionEntry(column, new Condition(Operator.BETWEEN, List.of(from, to)), Connector.AND));
        return this;
    }

    /**
     * Adds a greater-than WHERE condition.
     * @param column the column name
     * @param value the value to compare
     * @return this builder
     */
    public DeleteBuilder whereGreaterThan(String column, Object value) {
        conditions.add(new ConditionEntry(column, new Condition(Operator.GT, value), Connector.AND));
        return this;
    }

    /**
     * Adds a greater-than-or-equals WHERE condition.
     * @param column the column name
     * @param value the value to compare
     * @return this builder
     */
    public DeleteBuilder whereGreaterThanOrEquals(String column, Object value) {
        conditions.add(new ConditionEntry(column, new Condition(Operator.GTE, value), Connector.AND));
        return this;
    }

    /**
     * Adds a less-than-or-equals WHERE condition.
     * @param column the column name
     * @param value the value to compare
     * @return this builder
     */
    public DeleteBuilder whereLessThanOrEquals(String column, Object value) {
        conditions.add(new ConditionEntry(column, new Condition(Operator.LTE, value), Connector.AND));
        return this;
    }

    /**
     * Adds a not-equals WHERE condition.
     * @param column the column name
     * @param value the value to compare
     * @return this builder
     */
    public DeleteBuilder whereNotEquals(String column, Object value) {
        conditions.add(new ConditionEntry(column, new Condition(Operator.NEQ, value), Connector.AND));
        return this;
    }

    /**
     * Builds the SQL DELETE statement.
     *
     * @return the SQL result
     */
    public SqlResult build() {
        return build(null);
    }

    /**
     * Builds the SQL DELETE statement using the given dialect.
     *
     * @param dialect the SQL dialect (may be null for standard SQL)
     * @return the SQL result
     */
    public SqlResult build(final SqlDialect dialect) {
        final StringBuilder sql = new StringBuilder();
        final List<Object> params = new ArrayList<>();
        sql.append("DELETE FROM ").append(table);
        if (!conditions.isEmpty()) {
            sql.append(" WHERE ");
            for (int j = 0; j < conditions.size(); j++) {
                final ConditionEntry cond = conditions.get(j);
                if (j > 0) {
                    sql.append(" ").append(cond.getConnector().name()).append(" ");
                }
                appendDmlCondition(sql, params, cond);
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

    private void appendDmlCondition(StringBuilder sql, List<Object> params, ConditionEntry cond) {
        final Operator op = cond.getCondition().getOperator();
        sql.append(cond.getColumn());
        if (SIMPLE_OPS.containsKey(op)) {
            sql.append(SIMPLE_OPS.get(op));
            params.add(cond.getCondition().getValue());
        } else if (op == Operator.IN) {
            handleInOperator(sql, params, cond);
        } else if (op == Operator.NOT_IN) {
            handleNotInOperator(sql, params, cond);
        } else if (op == Operator.BETWEEN) {
            handleBetweenOperator(sql, params, cond);
        } else {
            sql.append(" = ?");
            params.add(cond.getCondition().getValue());
        }
    }

    /**
     * Handles the NOT IN operator for SQL generation.
     *
     * @param sql the SQL string builder
     * @param params the parameter list
     * @param cond the condition entry
     * @throws UnsupportedOperationException if the value list is null or empty
     */
    private void handleNotInOperator(StringBuilder sql, List<Object> params, ConditionEntry cond) {
        @SuppressWarnings("unchecked")
        final List<?> notInValues = (List<?>) cond.getCondition().getValue();
        if (notInValues == null || notInValues.isEmpty()) {
            throw new UnsupportedOperationException("NOT IN value list must not be null or empty");
        }
        sql.append(" NOT IN (");
        for (int j = 0; j < notInValues.size(); j++) {
            if (j > 0) {
                sql.append(", ");
            }
            sql.append("?");
            params.add(notInValues.get(j));
        }
        sql.append(")");
    }

    /**
     * Handles the BETWEEN operator for SQL generation.
     *
     * @param sql the SQL string builder
     * @param params the parameter list
     * @param cond the condition entry
     * @throws UnsupportedOperationException if the value list is null or not exactly two values
     */
    private void handleBetweenOperator(StringBuilder sql, List<Object> params, ConditionEntry cond) {
        @SuppressWarnings("unchecked")
        final List<?> betweenValues = (List<?>) cond.getCondition().getValue();
        if (betweenValues == null || betweenValues.size() != 2) {
            throw new UnsupportedOperationException("BETWEEN requires exactly two values");
        }
        sql.append(" BETWEEN ? AND ?");
        params.add(betweenValues.get(0));
        params.add(betweenValues.get(1));
    }
    /**
     * Handles the IN operator for SQL generation.
     * Extracted to reduce cyclomatic complexity.
     *
     * @param sql the SQL string builder
     * @param params the list of SQL parameters
     * @param cond the condition entry containing the IN values
     * @throws UnsupportedOperationException if the IN value list is null or empty
     */

    private void handleInOperator(StringBuilder sql, List<Object> params, ConditionEntry cond) {
        @SuppressWarnings("unchecked")
        final List<?> inValues = (List<?>) cond.getCondition().getValue();
        if (inValues == null || inValues.isEmpty()) {
            throw new UnsupportedOperationException("IN value list must not be null or empty");
        }
        sql.append(" IN (");
        for (int j = 0; j < inValues.size(); j++) {
            if (j > 0) {
                sql.append(", ");
            }
            sql.append("?");
            params.add(inValues.get(j));
        }
        sql.append(")");
    }
}
