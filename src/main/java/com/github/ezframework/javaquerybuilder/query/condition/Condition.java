package com.github.ezframework.javaquerybuilder.query.condition;

import java.util.Map;

/**
 * A single field condition used by {@link com.github.ezframework.javaquerybuilder.query.Query}.
 *
 * @author EzFramework
 * @version 1.0.0
 */
public class Condition {

    /** The operator for this condition. */
    private final Operator op;

    /** The value for comparison. */
    private final Object value;

    /**
     * Create a condition.
     * @param op operator
     * @param value comparison value (may be null for some operators)
     */
    public Condition(Operator op, Object value) {
        this.op = op;
        this.value = value;
    }

    /**
     * Return the operator for this condition.
     *
     * @return the operator
     */
    public Operator getOperator() {
        return op;
    }

    /**
     * Return the comparison value for this condition.
     *
     * @return the value used for comparison (may be null)
     */
    public Object getValue() {
        return value;
    }

    /**
     * Evaluate whether the given map's value for `key` satisfies this condition.
     *
     * @param map source attributes
     * @param key field name to check
     * @return true if the condition matches
     */
    @SuppressWarnings("unchecked")
    public boolean matches(Map<String, Object> map, String key) {
        final boolean exists = map.containsKey(key);
        final Object v = map.get(key);
        switch (op) {
            case EXISTS:
                return exists;
            case EQ:
                if (v == null) {
                    return value == null;
                }
                return v.equals(value);
            case NEQ:
                if (v == null) {
                    return value != null;
                }
                return !v.equals(value);
            case LIKE:
                return matchesLike(v);
            case IN:
                return matchesIn(v);
            default:
                return false;
        }
    }

    private boolean matchesLike(Object v) {
        if (v == null || value == null) {
            return false;
        }
        return v.toString().contains(value.toString());
    }

    @SuppressWarnings("unchecked")
    private boolean matchesIn(Object v) {
        if (value == null) {
            return false;
        }
        for (final Object item : (Iterable<?>) value) {
            return item.equals(v);
        }
        return false;
    }
}
