package com.github.ezframework.javaquerybuilder.query.condition;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class OperatorTest {
    @Test
    void allOperatorsPresent() {
        assertNotNull(Operator.valueOf("EQ"));
        assertNotNull(Operator.valueOf("LIKE"));
        assertNotNull(Operator.valueOf("IN"));
        assertNotNull(Operator.valueOf("BETWEEN"));
        assertNotNull(Operator.valueOf("IS_NULL"));
        assertNotNull(Operator.valueOf("IS_NOT_NULL"));
    }
}
