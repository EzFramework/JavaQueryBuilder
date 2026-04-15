package com.github.ezframework.javaquerybuilder.query.condition;

/**
 * Logical connector used to join adjacent WHERE conditions.
 *
 * <p>Each {@link ConditionEntry} carries a connector that specifies how it is
 * joined to its predecessor in the WHERE clause. The connector of the first
 * entry in a list is always ignored by the renderer.
 *
 * <pre>{@code
 * // Produces: WHERE status = ? AND age > ? OR role = ?
 * new QueryBuilder()
 *     .whereEquals("status", "active")          // AND (ignored — first entry)
 *     .whereGreaterThan("age", 18)              // AND
 *     .orWhereEquals("role", "admin")           // OR
 *     .buildSql("users");
 * }</pre>
 *
 * @author EzFramework
 * @version 1.0.0
 */
public enum Connector {
    /** Join this condition to the previous one with {@code AND}. */
    AND,
    /** Join this condition to the previous one with {@code OR}. */
    OR
}
