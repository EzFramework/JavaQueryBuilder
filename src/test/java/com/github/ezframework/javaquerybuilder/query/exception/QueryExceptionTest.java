package com.github.ezframework.javaquerybuilder.query.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class QueryExceptionTest {
    @Test
    void canBeConstructedWithMessage() {
        QueryException ex = new QueryException("fail");
        assertEquals("fail", ex.getMessage());
    }

    @Test
    void canBeConstructedWithCause() {
        Throwable t = new RuntimeException();
        QueryException ex = new QueryException(t);
        assertEquals(t, ex.getCause());
    }
}
