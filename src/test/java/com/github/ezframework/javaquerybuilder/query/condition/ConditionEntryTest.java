package com.github.ezframework.javaquerybuilder.query.condition;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ConditionEntryTest {
    @Test
    void storesColumnConditionAndConnector() {
        Condition c = new Condition(Operator.EQ, 123);
        ConditionEntry entry = new ConditionEntry("id", c, Connector.AND);
        assertEquals("id", entry.getColumn());
        assertEquals(c, entry.getCondition());
        assertEquals(Connector.AND, entry.getConnector());
    }
}
