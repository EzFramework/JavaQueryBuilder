package com.github.ezframework.javaquerybuilder.query.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class QueryRenderExceptionTest {
    @Test
    void canBeConstructedWithMessage() {
        QueryRenderException ex = new QueryRenderException("fail");
        assertEquals("fail", ex.getMessage());
    }

    @Test
    void canBeConstructedWithCause() {
        Throwable t = new RuntimeException();
        QueryRenderException ex = new QueryRenderException(t);
        assertEquals(t, ex.getCause());
    }
}
