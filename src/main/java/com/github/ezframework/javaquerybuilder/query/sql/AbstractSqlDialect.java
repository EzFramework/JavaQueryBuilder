package com.github.ezframework.javaquerybuilder.query.sql;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.ezframework.javaquerybuilder.query.JoinClause;
import com.github.ezframework.javaquerybuilder.query.Query;
import com.github.ezframework.javaquerybuilder.query.ScalarSelectItem;
import com.github.ezframework.javaquerybuilder.query.condition.ConditionEntry;
import com.github.ezframework.javaquerybuilder.query.condition.Operator;

/**
 * Base SQL rendering logic for SELECT and DELETE queries.
 *
 * <p>Implements the standard (ANSI) SQL dialect and provides shared helpers for
 * rendering {@code WHERE} clauses. Subclasses may override
 * {@link #quoteIdentifier(String)} to apply dialect-specific identifier quoting
 * and {@link #supportsDeleteLimit()} to enable dialect-specific DELETE
 * {@code LIMIT} behaviour.
 *
 * <p>Subquery support — parameter ordering contract:
 * <ol>
 *   <li>SELECT-list scalar subquery parameters (left to right)</li>
 *   <li>FROM subquery parameters</li>
 *   <li>JOIN subquery parameters (left to right)</li>
 *   <li>WHERE condition subquery parameters (top to bottom)</li>
 * </ol>
 *
 * @author EzFramework
 * @version 1.0.0
 */
public class AbstractSqlDialect implements SqlDialect {

    /** Simple comparison operators mapped to their SQL fragment with a placeholder. */
    private static final Map<Operator, String> COMPARISON_OPERATORS = Map.of(
        Operator.EQ, "= ?",
        Operator.NEQ, "!= ?",
        Operator.GT, "> ?",
        Operator.GTE, ">= ?",
        Operator.LT, "< ?",
        Operator.LTE, "<= ?"
    );

    /**
     * Returns the identifier with optional dialect-specific quoting applied.
     *
     * <p>The default implementation returns the identifier unchanged.
     * Override in a subclass to add quoting (e.g., back-ticks for MySQL).
     *
     * @param name the identifier to quote
     * @return the quoted identifier, or the original name if no quoting is applied
     */
    protected String quoteIdentifier(String name) {
        return name;
    }

    @Override
    public SqlResult renderDelete(Query query) {
        final StringBuilder sql = new StringBuilder();
        final List<Object> params = new ArrayList<>();
        sql.append("DELETE FROM ").append(quoteIdentifier(query.getTable()));
        appendWhereClause(sql, params, query);

        if (supportsDeleteLimit() && query.getLimit() != null && query.getLimit() >= 0) {
            sql.append(" LIMIT ").append(query.getLimit());
        }

        final String sqlStr = sql.toString();
        final List<Object> paramsCopy = Collections.unmodifiableList(new ArrayList<>(params));
        return new SqlResult() {
            @Override
            public String getSql() {
                return sqlStr;
            }

            @Override
            public List<Object> getParameters() {
                return paramsCopy;
            }
        };
    }

    /**
     * Hook for dialects that support a {@code LIMIT} clause on DELETE statements
     * (for example, MySQL). The default implementation returns {@code false},
     * meaning the base renderer will ignore {@code limit} on {@link Query} for DELETE.
     * Subclasses that want to enable {@code LIMIT} should override this method.
     *
     * @return {@code true} if the dialect appends a {@code LIMIT} to DELETE statements
     */
    protected boolean supportsDeleteLimit() {
        return false;
    }

    @Override
    public SqlResult render(Query query) {
        final StringBuilder sql = new StringBuilder();
        final List<Object> params = new ArrayList<>();
        sql.append("SELECT ");
        if (query.isDistinct()) {
            sql.append("DISTINCT ");
        }
        appendSelectColumns(sql, params, query);
        appendFromClause(sql, params, query);
        appendJoinClauses(sql, params, query);
        appendWhereClause(sql, params, query);
        if (!query.getGroupBy().isEmpty()) {
            final String cols = query.getGroupBy().stream()
                .map(this::quoteIdentifier).collect(Collectors.joining(", "));
            sql.append(" GROUP BY ").append(cols);
        }
        if (query.getHavingRaw() != null) {
            sql.append(" HAVING ").append(query.getHavingRaw());
        }
        appendOrderByClause(sql, query);
        if (query.getLimit() >= 0) {
            sql.append(" LIMIT ").append(query.getLimit());
        }
        if (query.getOffset() >= 0) {
            sql.append(" OFFSET ").append(query.getOffset());
        }
        final String sqlStr = sql.toString();
        final List<Object> paramsCopy = Collections.unmodifiableList(new ArrayList<>(params));
        return new SqlResult() {
            @Override
            public String getSql() {
                return sqlStr;
            }

            @Override
            public List<Object> getParameters() {
                return paramsCopy;
            }
        };
    }

    private void appendSelectColumns(StringBuilder sql, List<Object> params, Query query) {
        final List<String> cols = query.getSelectColumns();
        final List<ScalarSelectItem> subItems = query.getSelectSubqueries();
        if (cols.isEmpty() && subItems.isEmpty()) {
            sql.append("*");
            return;
        }
        final List<String> fragments = new ArrayList<>();
        for (final String col : cols) {
            fragments.add(quoteIdentifier(col));
        }
        for (final ScalarSelectItem item : subItems) {
            final SqlResult sub = render(item.getSubquery());
            params.addAll(sub.getParameters());
            fragments.add("(" + sub.getSql() + ") AS " + quoteIdentifier(item.getAlias()));
        }
        sql.append(String.join(", ", fragments));
    }

    private void appendFromClause(StringBuilder sql, List<Object> params, Query query) {
        if (query.getFromSubquery() != null) {
            final SqlResult sub = render(query.getFromSubquery());
            params.addAll(sub.getParameters());
            sql.append(" FROM (").append(sub.getSql()).append(") AS ")
               .append(quoteIdentifier(query.getFromAlias()));
        } else {
            sql.append(" FROM ").append(quoteIdentifier(query.getTable()));
        }
    }

    private void appendJoinClauses(StringBuilder sql, List<Object> params, Query query) {
        for (final JoinClause join : query.getJoins()) {
            sql.append(" ").append(join.getType().name()).append(" JOIN ");
            if (join.getSubquery() != null) {
                final SqlResult sub = render(join.getSubquery());
                params.addAll(sub.getParameters());
                sql.append("(").append(sub.getSql()).append(") AS ")
                   .append(quoteIdentifier(join.getAlias()));
            } else {
                sql.append(quoteIdentifier(join.getTable()));
            }
            if (join.getOnCondition() != null) {
                sql.append(" ON ").append(join.getOnCondition());
            }
        }
    }

    /**
     * Appends a {@code WHERE} clause to {@code sql}, collecting bound parameters into
     * {@code params}. For conditions whose column is {@code null} (e.g. EXISTS subquery
     * conditions), the column fragment is omitted.
     *
     * @param sql    the SQL string builder
     * @param params the bound-parameter list
     * @param query  the source query
     */
    protected void appendWhereClause(StringBuilder sql, List<Object> params, Query query) {
        final List<ConditionEntry> conditions = query.getConditions();
        if (conditions.isEmpty()) {
            return;
        }
        sql.append(" WHERE ");
        for (int i = 0; i < conditions.size(); i++) {
            final ConditionEntry entry = conditions.get(i);
            if (i > 0) {
                sql.append(" ").append(entry.getConnector().name()).append(" ");
            }
            if (entry.getColumn() != null) {
                sql.append(quoteIdentifier(entry.getColumn())).append(" ");
            }
            appendConditionFragment(sql, params, entry);
        }
    }

    /**
     * Appends the operator and value fragment for a single condition, collecting bound
     * parameters. Handles scalar values, {@link java.util.List}-valued operators, and
     * {@link Query}-valued subquery operators.
     *
     * @param sql    the SQL string builder
     * @param params the bound-parameter list
     * @param entry  the condition entry to render
     */
    @SuppressWarnings("unchecked")
    protected void appendConditionFragment(StringBuilder sql, List<Object> params, ConditionEntry entry) {
        final Operator op = entry.getCondition().getOperator();
        final Object val = entry.getCondition().getValue();

        if (val instanceof Query) {
            appendSubqueryCondition(sql, params, op, (Query) val);
            return;
        }
        if (COMPARISON_OPERATORS.containsKey(op)) {
            sql.append(COMPARISON_OPERATORS.get(op));
            params.add(val);
            return;
        }
        appendNonComparisonFragment(sql, params, op, val);
    }

    private void appendSubqueryCondition(
            StringBuilder sql, List<Object> params, Operator op, Query subquery) {
        if (op == Operator.EXISTS_SUBQUERY) {
            appendSubqueryExists(sql, params, subquery, false);
        } else if (op == Operator.NOT_EXISTS_SUBQUERY) {
            appendSubqueryExists(sql, params, subquery, true);
        } else if (op == Operator.IN) {
            appendSubqueryIn(sql, params, subquery, false);
        } else if (op == Operator.NOT_IN) {
            appendSubqueryIn(sql, params, subquery, true);
        } else if (COMPARISON_OPERATORS.containsKey(op)) {
            appendSubqueryComparison(sql, params, op, subquery);
        }
    }

    private void appendSubqueryExists(
            StringBuilder sql, List<Object> params, Query subquery, boolean negate) {
        final SqlResult sub = render(subquery);
        params.addAll(sub.getParameters());
        if (negate) {
            sql.append("NOT EXISTS (").append(sub.getSql()).append(")");
        } else {
            sql.append("EXISTS (").append(sub.getSql()).append(")");
        }
    }

    private void appendSubqueryIn(
            StringBuilder sql, List<Object> params, Query subquery, boolean negate) {
        final SqlResult sub = render(subquery);
        params.addAll(sub.getParameters());
        if (negate) {
            sql.append("NOT IN (").append(sub.getSql()).append(")");
        } else {
            sql.append("IN (").append(sub.getSql()).append(")");
        }
    }

    private void appendSubqueryComparison(
            StringBuilder sql, List<Object> params, Operator op, Query subquery) {
        final SqlResult sub = render(subquery);
        params.addAll(sub.getParameters());
        final String fragment = COMPARISON_OPERATORS.get(op).replace("?", "(" + sub.getSql() + ")");
        sql.append(fragment);
    }

    @SuppressWarnings("unchecked")
    private void appendNonComparisonFragment(
            StringBuilder sql, List<Object> params, Operator op, Object val) {
        switch (op) {
            case LIKE:
                sql.append("LIKE ?");
                params.add("%" + val + "%");
                break;
            case NOT_LIKE:
                sql.append("NOT LIKE ?");
                params.add("%" + val + "%");
                break;
            case EXISTS:
                sql.append("IS NOT NULL");
                break;
            case IS_NULL:
                sql.append("IS NULL");
                break;
            case IS_NOT_NULL:
                sql.append("IS NOT NULL");
                break;
            case IN:
                appendInList(sql, params, (List<?>) val, false);
                break;
            case NOT_IN:
                appendInList(sql, params, (List<?>) val, true);
                break;
            case BETWEEN:
                final List<?> between = (List<?>) val;
                sql.append("BETWEEN ? AND ?");
                params.add(between.get(0));
                params.add(between.get(1));
                break;
            default:
                break;
        }
    }

    private void appendInList(
            StringBuilder sql, List<Object> params, List<?> values, boolean negate) {
        final String placeholders = String.join(", ", Collections.nCopies(values.size(), "?"));
        if (negate) {
            sql.append("NOT IN (").append(placeholders).append(")");
        } else {
            sql.append("IN (").append(placeholders).append(")");
        }
        params.addAll(values);
    }

    private void appendOrderByClause(StringBuilder sql, Query query) {
        if (query.getOrderBy().isEmpty()) {
            return;
        }
        sql.append(" ORDER BY ");
        for (int i = 0; i < query.getOrderBy().size(); i++) {
            if (i > 0) {
                sql.append(", ");
            }
            sql.append(quoteIdentifier(query.getOrderBy().get(i)))
                .append(query.getOrderByAsc().get(i) ? " ASC" : " DESC");
        }
    }
}
