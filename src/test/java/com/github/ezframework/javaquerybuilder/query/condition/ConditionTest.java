package com.github.ezframework.javaquerybuilder.query.condition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConditionTest {

    @Test
    void createsConditionWithOperatorAndValue() {
        Condition c = new Condition(Operator.EQ, "foo");
        assertEquals(Operator.EQ, c.getOperator());
        assertEquals("foo", c.getValue());
    }

    @Test
    void supportsNullValue() {
        Condition c = new Condition(Operator.IS_NULL, null);
        assertEquals(Operator.IS_NULL, c.getOperator());
        assertNull(c.getValue());
    }

    @Test
    void matchesEqReturnsTrueForEqualValues() {
        Condition c = new Condition(Operator.EQ, "hello");
        assertTrue(c.matches(Map.of("k", "hello"), "k"));
    }

    @Test
    void matchesEqReturnsFalseForDifferentValues() {
        Condition c = new Condition(Operator.EQ, "hello");
        assertFalse(c.matches(Map.of("k", "world"), "k"));
    }

    @Test
    void matchesEqReturnsTrueWhenBothNull() {
        Condition c = new Condition(Operator.EQ, null);
        Map<String, Object> map = new HashMap<>();
        map.put("k", null);
        assertTrue(c.matches(map, "k"));
    }

    @Test
    void matchesEqReturnsFalseWhenMapNullButConditionNonNull() {
        Condition c = new Condition(Operator.EQ, "x");
        Map<String, Object> map = new HashMap<>();
        map.put("k", null);
        assertFalse(c.matches(map, "k"));
    }

    @Test
    void matchesNeqReturnsFalseForEqualValues() {
        Condition c = new Condition(Operator.NEQ, "hello");
        assertFalse(c.matches(Map.of("k", "hello"), "k"));
    }

    @Test
    void matchesNeqReturnsTrueForDifferentValues() {
        Condition c = new Condition(Operator.NEQ, "hello");
        assertTrue(c.matches(Map.of("k", "world"), "k"));
    }

    @Test
    void matchesNeqReturnsTrueWhenMapNullButConditionNonNull() {
        Condition c = new Condition(Operator.NEQ, "x");
        Map<String, Object> map = new HashMap<>();
        map.put("k", null);
        assertTrue(c.matches(map, "k"));
    }

    @Test
    void matchesNeqReturnsFalseWhenBothNull() {
        Condition c = new Condition(Operator.NEQ, null);
        Map<String, Object> map = new HashMap<>();
        map.put("k", null);
        assertFalse(c.matches(map, "k"));
    }

    @Test
    void matchesExistsReturnsTrueWhenKeyPresent() {
        Condition c = new Condition(Operator.EXISTS, null);
        assertTrue(c.matches(Map.of("k", "v"), "k"));
    }

    @Test
    void matchesExistsReturnsFalseWhenKeyAbsent() {
        Condition c = new Condition(Operator.EXISTS, null);
        assertFalse(c.matches(Map.of("other", "v"), "missing"));
    }

    @Test
    void matchesLikeReturnsTrueForContainedSubstring() {
        Condition c = new Condition(Operator.LIKE, "ell");
        assertTrue(c.matches(Map.of("k", "hello"), "k"));
    }

    @Test
    void matchesLikeReturnsFalseForMissingSubstring() {
        Condition c = new Condition(Operator.LIKE, "xyz");
        assertFalse(c.matches(Map.of("k", "hello"), "k"));
    }

    @Test
    void matchesLikeReturnsFalseWhenMapValueNull() {
        Condition c = new Condition(Operator.LIKE, "x");
        Map<String, Object> map = new HashMap<>();
        map.put("k", null);
        assertFalse(c.matches(map, "k"));
    }

    @Test
    void matchesLikeReturnsFalseWhenConditionValueNull() {
        Condition c = new Condition(Operator.LIKE, null);
        assertFalse(c.matches(Map.of("k", "hello"), "k"));
    }

    @Test
    void matchesInReturnsTrueForMatchingFirstElement() {
        Condition c = new Condition(Operator.IN, List.of("a", "b"));
        assertTrue(c.matches(Map.of("k", "a"), "k"));
    }

    @Test
    void matchesInReturnsFalseWhenConditionValueNull() {
        Condition c = new Condition(Operator.IN, null);
        assertFalse(c.matches(Map.of("k", "a"), "k"));
    }

    @Test
    void matchesDefaultReturnsFalseForUnsupportedOperator() {
        Condition c = new Condition(Operator.GT, 5);
        assertFalse(c.matches(Map.of("k", 10), "k"));
    }
}
