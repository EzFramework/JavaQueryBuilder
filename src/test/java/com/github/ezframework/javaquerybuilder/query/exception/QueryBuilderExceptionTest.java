package com.github.ezframework.javaquerybuilder.query.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class QueryBuilderExceptionTest {
    @Test
    void canBeConstructedWithMessage() {
        QueryBuilderException ex = new QueryBuilderException("fail");
        assertEquals("fail", ex.getMessage());
    }

    @Test
    void canBeConstructedWithCause() {
        Throwable t = new RuntimeException();
        QueryBuilderException ex = new QueryBuilderException(t);
        assertEquals(t, ex.getCause());
    }
}
