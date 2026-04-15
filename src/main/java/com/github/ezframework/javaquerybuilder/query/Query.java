package com.github.ezframework.javaquerybuilder.query;

import java.util.ArrayList;
import java.util.List;

import com.github.ezframework.javaquerybuilder.query.condition.ConditionEntry;

/**
 * Represents a built query with conditions, grouping, and ordering.
 *
 * @author EzFramework
 * @version 1.0.0
 */
public class Query {

    /** The source table for the query. */
    private String table;

    /** The LIMIT value for the query. */
    private int limit = 0;

    /** The OFFSET value for the query. */
    private int offset = 0;

    /** The list of WHERE conditions. */
    private List<ConditionEntry> conditions = new ArrayList<>();

    /** The list of GROUP BY columns. */
    private List<String> groupBy = new ArrayList<>();

    /** The list of ORDER BY columns. */
    private List<String> orderBy = new ArrayList<>();

    /** The ORDER BY directions; element at index i is true for ASC, false for DESC. */
    private List<Boolean> orderByAsc = new ArrayList<>();

    /** The columns to select; empty means SELECT *. */
    private List<String> selectColumns = new ArrayList<>();

    /** Whether the SELECT is DISTINCT. */
    private boolean distinct = false;

    /** The HAVING clause (raw SQL fragment). */
    private String havingRaw = null;

    /**
     * Gets the source table for the query.
     *
     * @return the table name, or {@code null} if not set
     */
    public String getTable() {
        return table;
    }

    /**
     * Sets the source table for the query.
     *
     * @param table the table name
     */
    public void setTable(String table) {
        this.table = table;
    }

    /**
     * Sets the LIMIT value.
     *
     * @param limit the maximum number of rows to return
     */
    public void setLimit(int limit) {
        this.limit = limit;
    }

    /**
     * Gets the LIMIT value.
     *
     * @return the LIMIT value
     */
    public Integer getLimit() {
        return limit;
    }

    /**
     * Sets the OFFSET value.
     *
     * @param offset the number of rows to skip
     */
    public void setOffset(int offset) {
        this.offset = offset;
    }

    /**
     * Gets the OFFSET value.
     *
     * @return the OFFSET value
     */
    public Integer getOffset() {
        return offset;
    }

    /**
     * Gets the list of WHERE conditions.
     *
     * @return the list of WHERE conditions
     */
    public List<ConditionEntry> getConditions() {
        return conditions;
    }

    /**
     * Sets the list of WHERE conditions.
     *
     * @param conditions the list of WHERE conditions
     */
    public void setConditions(List<ConditionEntry> conditions) {
        this.conditions = conditions;
    }

    /**
     * Gets the list of GROUP BY columns.
     *
     * @return the list of GROUP BY columns
     */
    public List<String> getGroupBy() {
        return groupBy;
    }

    /**
     * Sets the list of GROUP BY columns.
     *
     * @param groupBy the list of GROUP BY columns
     */
    public void setGroupBy(List<String> groupBy) {
        this.groupBy = groupBy;
    }

    /**
     * Gets the list of ORDER BY columns.
     *
     * @return the list of ORDER BY columns
     */
    public List<String> getOrderBy() {
        return orderBy;
    }

    /**
     * Sets the list of ORDER BY columns.
     *
     * @param orderBy the list of ORDER BY columns
     */
    public void setOrderBy(List<String> orderBy) {
        this.orderBy = orderBy;
    }

    /**
     * Gets the ORDER BY directions.
     *
     * @return the list of ORDER BY directions; true for ASC, false for DESC
     */
    public List<Boolean> getOrderByAsc() {
        return orderByAsc;
    }

    /**
     * Sets the ORDER BY directions.
     *
     * @param orderByAsc the list of ORDER BY directions
     */
    public void setOrderByAsc(List<Boolean> orderByAsc) {
        this.orderByAsc = orderByAsc;
    }

    /**
     * Gets the columns to select.
     *
     * @return the list of column names; empty means SELECT *
     */
    public List<String> getSelectColumns() {
        return selectColumns;
    }

    /**
     * Sets the columns to select.
     *
     * @param selectColumns the list of column names
     */
    public void setSelectColumns(List<String> selectColumns) {
        this.selectColumns = selectColumns;
    }

    /**
     * Returns whether the SELECT is DISTINCT.
     *
     * @return true if the query uses DISTINCT
     */
    public boolean isDistinct() {
        return distinct;
    }

    /**
     * Sets whether the SELECT is DISTINCT.
     *
     * @param distinct true to enable DISTINCT
     */
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    /**
     * Gets the raw HAVING clause.
     *
     * @return the HAVING clause string, or {@code null} if not set
     */
    public String getHavingRaw() {
        return havingRaw;
    }

    /**
     * Sets the raw HAVING clause.
     *
     * @param havingRaw the HAVING clause SQL fragment
     */
    public void setHavingRaw(String havingRaw) {
        this.havingRaw = havingRaw;
    }
}
