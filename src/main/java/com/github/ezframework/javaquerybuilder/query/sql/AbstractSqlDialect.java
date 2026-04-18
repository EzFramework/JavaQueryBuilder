package com.github.ezframework.javaquerybuilder.query.sql;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.ezframework.javaquerybuilder.query.Query;
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
     * Hook for dialects that support a `LIMIT` clause on DELETE statements
     * (for example, MySQL). The default implementation returns {@code false},
     * meaning the base renderer will ignore `limit` on `Query` for DELETE.
     * Subclasses that want to enable `LIMIT` should override this method.
     *
     * @return {@code true} if the dialect appends a `LIMIT` to DELETE statements
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
        appendSelectColumns(sql, query);
        sql.append(" FROM ").append(quoteIdentifier(query.getTable()));
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

    private void appendSelectColumns(StringBuilder sql, Query query) {
        if (query.getSelectColumns().isEmpty()) {
            sql.append("*");
        } else {
            sql.append(query.getSelectColumns().stream()
                .map(this::quoteIdentifier).collect(Collectors.joining(", ")));
        }
    }

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
            sql.append(quoteIdentifier(entry.getColumn())).append(" ");
            appendConditionFragment(sql, params, entry);
        }
    }

    @SuppressWarnings("unchecked")
    protected void appendConditionFragment(StringBuilder sql, List<Object> params, ConditionEntry entry) {
        final Operator op = entry.getCondition().getOperator();
        if (COMPARISON_OPERATORS.containsKey(op)) {
            sql.append(COMPARISON_OPERATORS.get(op));
            params.add(entry.getCondition().getValue());
            return;
        }
        switch (op) {
            case LIKE:
                sql.append("LIKE ?");
                params.add("%" + entry.getCondition().getValue() + "%");
                break;
            case NOT_LIKE:
                sql.append("NOT LIKE ?");
                params.add("%" + entry.getCondition().getValue() + "%");
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
            case IN: {
                final List<?> inVals = (List<?>) entry.getCondition().getValue();
                sql.append("IN (").append(String.join(", ", Collections.nCopies(inVals.size(), "?"))).append(")");
                params.addAll(inVals);
                break;
            }
            case NOT_IN: {
                final List<?> notInVals = (List<?>) entry.getCondition().getValue();
                sql.append("NOT IN (")
                    .append(String.join(", ", Collections.nCopies(notInVals.size(), "?")))
                    .append(")");
                params.addAll(notInVals);
                break;
            }
            case BETWEEN: {
                final List<?> betweenVals = (List<?>) entry.getCondition().getValue();
                sql.append("BETWEEN ? AND ?");
                params.add(betweenVals.get(0));
                params.add(betweenVals.get(1));
                break;
            }
            default:
                break;
        }
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
