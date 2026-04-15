package com.github.ezframework.javaquerybuilder.query;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class QueryableStorageTest {
    @Test
    void functionalInterfaceWorks() throws Exception {
        QueryableStorage storage = q -> List.of("a", "b");
        List<String> result = storage.query(new Query());
        assertEquals(List.of("a", "b"), result);
    }
}
