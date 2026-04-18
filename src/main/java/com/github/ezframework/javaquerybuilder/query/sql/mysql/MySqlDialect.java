package com.github.ezframework.javaquerybuilder.query.sql.mysql;

import com.github.ezframework.javaquerybuilder.query.sql.AbstractSqlDialect;

/**
 * MySQL SQL dialect.
 *
 * <p>Wraps every identifier (table names, column names) in back-tick quotes
 * so that reserved words and mixed-case names are always safe to use.
 *
 * @author EzFramework
 * @version 1.0.0
 */
public class MySqlDialect extends AbstractSqlDialect {

    @Override
    protected String quoteIdentifier(String name) {
        return "`" + name + "`";
    }

    @Override
    protected boolean supportsDeleteLimit() {
        return true;
    }
}
