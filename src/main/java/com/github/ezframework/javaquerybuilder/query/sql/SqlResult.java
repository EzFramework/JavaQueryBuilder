package com.github.ezframework.javaquerybuilder.query.sql;

import java.util.Collections;
import java.util.List;

/**
 * Represents the result of a SQL query, including the SQL string and parameters.
 *
 * @author EzFramework
 * @version 1.0.0
 */
public class SqlResult {

    /**
     * Returns the SQL string.
     *
     * @return the SQL string
     */
    public String getSql() {
        return "";
    }

    /**
     * Returns the list of parameters for the SQL query.
     *
     * @return the list of parameters
     */
    public List<Object> getParameters() {
        return Collections.emptyList();
    }
}
