package com.github.ezframework.javaquerybuilder.query.condition;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ConnectorTest {
    @Test
    void enumValuesAreCorrect() {
        assertEquals(Connector.AND, Connector.valueOf("AND"));
        assertEquals(Connector.OR, Connector.valueOf("OR"));
    }
}
