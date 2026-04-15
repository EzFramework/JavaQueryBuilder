package com.github.ezframework.javaquerybuilder.query.sql;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SqlResultTest {
    @Test
    void defaultSqlResultIsNotNull() {
        SqlResult result = new SqlResult();
        assertNotNull(result.getSql());
        assertNotNull(result.getParameters());
    }
}
