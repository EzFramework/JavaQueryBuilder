package com.github.ezframework.javaquerybuilder.query;

import java.util.List;

@FunctionalInterface
public interface QueryableStorage {
    List<String> query(Query q) throws Exception;
}
