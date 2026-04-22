package com.github.ezframework.javaquerybuilder.query.sql.postgresql;

import java.util.List;

import com.github.ezframework.javaquerybuilder.query.Query;
import com.github.ezframework.javaquerybuilder.query.condition.ConditionEntry;
import com.github.ezframework.javaquerybuilder.query.condition.Operator;
import com.github.ezframework.javaquerybuilder.query.sql.AbstractSqlDialect;

/**
 * PostgreSQL SQL dialect.
 *
 * <p>Extends {@link AbstractSqlDialect} with PostgreSQL-specific behaviour:
 * <ul>
 *   <li>Identifiers are wrapped in double-quote characters (SQL standard quoting,
 *       required for mixed-case names and reserved words in PostgreSQL).</li>
 *   <li>{@code ILIKE} and {@code NOT ILIKE} operators for case-insensitive pattern
 *       matching ({@link Operator#ILIKE}, {@link Operator#NOT_ILIKE}).</li>
 *   <li>A {@code RETURNING} clause is appended to {@code DELETE} statements when
 *       returning columns have been specified on the query.</li>
 * </ul>
 *
 * @author EzFramework
 * @version 1.0.0
 */
public class PostgreSqlDialect extends AbstractSqlDialect {

    @Override
    protected String quoteIdentifier(final String name) {
        return "\"" + name + "\"";
    }

    @Override
    protected boolean supportsDeleteLimit() {
        return false;
    }

    @Override
    protected boolean supportsReturning() {
        return true;
    }

    /**
     * Appends the operator and value fragment for a single condition.
     *
     * <p>Handles {@link Operator#ILIKE} and {@link Operator#NOT_ILIKE} in addition
     * to the operators supported by the base class. All other operators are
     * delegated to {@link AbstractSqlDialect#appendConditionFragment}.
     *
     * @param sql    the SQL string builder
     * @param params the bound-parameter list
     * @param entry  the condition entry to render
     * @param query  the source query (used for LIKE wrapping configuration)
     */
    @Override
    protected void appendConditionFragment(
            final StringBuilder sql,
            final List<Object> params,
            final ConditionEntry entry,
            final Query query) {
        final Operator op = entry.getCondition().getOperator();
        final Object val = entry.getCondition().getValue();
        if (op == Operator.ILIKE) {
            sql.append("ILIKE ?");
            params.add(query.getLikePrefix() + val + query.getLikeSuffix());
        } else if (op == Operator.NOT_ILIKE) {
            sql.append("NOT ILIKE ?");
            params.add(query.getLikePrefix() + val + query.getLikeSuffix());
        } else {
            super.appendConditionFragment(sql, params, entry, query);
        }
    }
}
