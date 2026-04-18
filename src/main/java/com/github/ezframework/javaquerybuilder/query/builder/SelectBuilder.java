package com.github.ezframework.javaquerybuilder.query.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.ezframework.javaquerybuilder.query.QueryBuilderDefaults;
import com.github.ezframework.javaquerybuilder.query.condition.Condition;
import com.github.ezframework.javaquerybuilder.query.condition.ConditionEntry;
import com.github.ezframework.javaquerybuilder.query.condition.Connector;
import com.github.ezframework.javaquerybuilder.query.condition.Operator;
import com.github.ezframework.javaquerybuilder.query.sql.SqlDialect;
import com.github.ezframework.javaquerybuilder.query.sql.SqlResult;

/**
 * Builder for SQL SELECT statements.
 *
 * @author EzFramework
 * @version 1.0.0
 */
public class SelectBuilder {
    /**
     * The table to select from.
     */
    private String table;

    /**
     * The columns to select.
     */
    private final List<String> columns = new ArrayList<>();

    /**
     * The WHERE conditions.
     */
    private final List<ConditionEntry> conditions = new ArrayList<>();

    /**
     * The GROUP BY columns.
     */
    private final List<String> groupBy = new ArrayList<>();

    /**
     * The ORDER BY columns.
     */
    private final List<String> orderBy = new ArrayList<>();

    /**
     * The ORDER BY directions (true for ASC, false for DESC).
     */
    private final List<Boolean> orderByAsc = new ArrayList<>();

    /**
     * The LIMIT value.
     */
    private int limit = -1;

    /**
     * The OFFSET value.
     */
    private int offset = -1;

    /**
     * Whether to use DISTINCT.
     */
    private boolean distinct = false;

    /** The defaults configuration for this builder instance. */
    private QueryBuilderDefaults queryBuilderDefaults = QueryBuilderDefaults.global();

    /**
     * Overrides the defaults configuration for this builder instance.
     *
     * @param defaults the defaults to apply; must not be {@code null}
     * @return this builder instance for chaining
     * @throws NullPointerException if {@code defaults} is {@code null}
     */
    public SelectBuilder withDefaults(final QueryBuilderDefaults defaults) {
        if (defaults == null) {
            throw new NullPointerException("QueryBuilderDefaults must not be null");
        }
        this.queryBuilderDefaults = defaults;
        return this;
    }

    /**
     * Sets the table to select from.
     * @param table the table name
     * @return this builder
     */
    public SelectBuilder from(String table) {
        this.table = table;
        return this;
    }

    /**
     * Adds columns to select. If none are added, defaults to *.
     * @param columns the column names
     * @return this builder
     */
    public SelectBuilder select(String... columns) {
        this.columns.addAll(Arrays.asList(columns));
        return this;
    }

    /**
     * Sets DISTINCT.
     * @return this builder
     */
    public SelectBuilder distinct() {
        this.distinct = true;
        return this;
    }

    /**
     * Adds a WHERE condition.
     * @param column the column name
     * @param value the value to compare
     * @return this builder
     */
    public SelectBuilder whereEquals(String column, Object value) {
        conditions.add(new ConditionEntry(
                column,
                new Condition(Operator.EQ, value),
                conditions.isEmpty() ? Connector.AND : Connector.AND));
        return this;
    }

    /**
     * Adds a WHERE IN condition.
     * @param column the column name
     * @param values the values for IN
     * @return this builder
     */
    public SelectBuilder whereIn(String column, List<?> values) {
        conditions.add(new ConditionEntry(
                column,
                new Condition(Operator.IN, values),
                conditions.isEmpty() ? Connector.AND : Connector.AND));
        return this;
    }

    /**
     * Adds a WHERE LIKE condition.
     * @param column the column name
     * @param value the value for LIKE
     * @return this builder
     */
    public SelectBuilder whereLike(String column, String value) {
        conditions.add(new ConditionEntry(
                column,
                new Condition(Operator.LIKE, value),
                conditions.isEmpty() ? Connector.AND : Connector.AND));
        return this;
    }

    /**
     * Adds a GROUP BY clause.
     * @param columns the columns to group by
     * @return this builder
     */
    public SelectBuilder groupBy(String... columns) {
        groupBy.addAll(Arrays.asList(columns));
        return this;
    }

    /**
     * Adds an ORDER BY clause.
     * @param column the column to order by
     * @param asc true for ASC, false for DESC
     * @return this builder
     */
    public SelectBuilder orderBy(String column, boolean asc) {
        orderBy.add(column);
        orderByAsc.add(asc);
        return this;
    }

    /**
     * Sets the LIMIT.
     * @param limit the limit
     * @return this builder
     */
    public SelectBuilder limit(int limit) {
        this.limit = limit;
        return this;
    }

    /**
     * Sets the OFFSET.
     * @param offset the offset
     * @return this builder
     */
    public SelectBuilder offset(int offset) {
        this.offset = offset;
        return this;
    }

    /**
     * Builds the SQL SELECT statement.
     * @return the SQL result
     * @throws IllegalStateException if table is not set
     */
    public SqlResult build() {
        return build(null);
    }

    /**
     * Builds the SQL SELECT statement with a dialect.
     * @param dialect the SQL dialect (optional)
     * @return the SQL result
     * @throws IllegalStateException if table is not set
     * @throws UnsupportedOperationException if an unsupported operator is used
     */
    public SqlResult build(SqlDialect dialect) {
        if (table == null) {
            throw new IllegalStateException("Table name is required");
        }
        final StringBuilder sql = new StringBuilder();
        final List<Object> params = new ArrayList<>();
        buildSelectClause(sql);
        buildFromClause(sql);
        buildWhereClause(sql, params);
        buildGroupByClause(sql);
        buildOrderByClause(sql);
        buildLimitOffsetClause(sql);
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

    private void buildSelectClause(final StringBuilder sql) {
        sql.append("SELECT ");
        if (distinct) {
            sql.append("DISTINCT ");
        }
        if (columns.isEmpty()) {
            sql.append(queryBuilderDefaults.getDefaultColumns());
        } else {
            sql.append(String.join(", ", columns));
        }
    }

    private void buildFromClause(final StringBuilder sql) {
        sql.append(" FROM ").append(table);
    }

    private void buildWhereClause(final StringBuilder sql, final List<Object> params) {
        if (conditions.isEmpty()) {
            return;
        }
        sql.append(" WHERE ");
        for (int i = 0; i < conditions.size(); i++) {
            if (i > 0) {
                sql.append(" AND ");
            }
            final ConditionEntry cond = conditions.get(i);
            sql.append(cond.getColumn()).append(" ");
            final Operator op = cond.getCondition().getOperator();
            if (op == Operator.EQ) {
                sql.append("= ?");
                params.add(cond.getCondition().getValue());
            } else if (op == Operator.IN) {
                final List<?> inVals = (List<?>) cond.getCondition().getValue();
                sql.append("IN (");
                for (int j = 0; j < inVals.size(); j++) {
                    if (j > 0) {
                        sql.append(", ");
                    }
                    sql.append("?");
                    params.add(inVals.get(j));
                }
                sql.append(")");
            } else if (op == Operator.LIKE) {
                sql.append("LIKE ?");
                final String likeVal = queryBuilderDefaults.getLikePrefix()
                    + cond.getCondition().getValue()
                    + queryBuilderDefaults.getLikeSuffix();
                params.add(likeVal);
            } else {
                throw new UnsupportedOperationException(
                        "Operator not supported: " + cond.getCondition().getOperator());
            }
        }
    }

    private void buildGroupByClause(final StringBuilder sql) {
        if (!groupBy.isEmpty()) {
            sql.append(" GROUP BY ").append(String.join(", ", groupBy));
        }
    }

    private void buildOrderByClause(final StringBuilder sql) {
        if (!orderBy.isEmpty()) {
            sql.append(" ORDER BY ");
            for (int i = 0; i < orderBy.size(); i++) {
                if (i > 0) {
                    sql.append(", ");
                }
                sql.append(orderBy.get(i)).append(orderByAsc.get(i) ? " ASC" : " DESC");
            }
        }
    }

    private void buildLimitOffsetClause(final StringBuilder sql) {
        final int resolvedLimit = (limit == -1 && queryBuilderDefaults.getDefaultLimit() >= 0)
            ? queryBuilderDefaults.getDefaultLimit() : limit;
        final int resolvedOffset = (offset == -1 && queryBuilderDefaults.getDefaultOffset() >= 0)
            ? queryBuilderDefaults.getDefaultOffset() : offset;
        if (resolvedLimit >= 0) {
            sql.append(" LIMIT ").append(resolvedLimit);
        }
        if (resolvedOffset >= 0) {
            sql.append(" OFFSET ").append(resolvedOffset);
        }
    }
}
