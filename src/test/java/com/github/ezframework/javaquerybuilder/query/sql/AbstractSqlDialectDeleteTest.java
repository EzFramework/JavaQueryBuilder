package com.github.ezframework.javaquerybuilder.query.sql;

import java.util.List;

import com.github.ezframework.javaquerybuilder.query.builder.QueryBuilder;
import com.github.ezframework.javaquerybuilder.query.Query;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AbstractSqlDialectDeleteTest {

    static class DummyDialect extends AbstractSqlDialect {}

    @Test
    void deleteParametersMatchSelectParameters() {
        final DummyDialect dialect = new DummyDialect();
        final com.github.ezframework.javaquerybuilder.query.condition.ConditionEntry ae =
            new com.github.ezframework.javaquerybuilder.query.condition.ConditionEntry("a",
                new com.github.ezframework.javaquerybuilder.query.condition.Condition(
                    com.github.ezframework.javaquerybuilder.query.condition.Operator.EQ, 1),
                com.github.ezframework.javaquerybuilder.query.condition.Connector.AND);

        final com.github.ezframework.javaquerybuilder.query.condition.ConditionEntry ce =
            new com.github.ezframework.javaquerybuilder.query.condition.ConditionEntry("c",
                new com.github.ezframework.javaquerybuilder.query.condition.Condition(
                    com.github.ezframework.javaquerybuilder.query.condition.Operator.GT, 2),
                com.github.ezframework.javaquerybuilder.query.condition.Connector.AND);

        final com.github.ezframework.javaquerybuilder.query.condition.ConditionEntry inE =
            new com.github.ezframework.javaquerybuilder.query.condition.ConditionEntry("l",
                new com.github.ezframework.javaquerybuilder.query.condition.Condition(
                    com.github.ezframework.javaquerybuilder.query.condition.Operator.IN, List.of(6, 7)),
                com.github.ezframework.javaquerybuilder.query.condition.Connector.AND);

        final com.github.ezframework.javaquerybuilder.query.condition.ConditionEntry betweenE =
            new com.github.ezframework.javaquerybuilder.query.condition.ConditionEntry("n",
                new com.github.ezframework.javaquerybuilder.query.condition.Condition(
                    com.github.ezframework.javaquerybuilder.query.condition.Operator.BETWEEN, List.of(10, 11)),
                com.github.ezframework.javaquerybuilder.query.condition.Connector.AND);

        final Query q = new Query();
        q.setTable("t");
        q.setConditions(List.of(ae, ce, inE, betweenE));

        final SqlResult select = dialect.render(q);
        final SqlResult del = dialect.renderDelete(q);

        assertTrue(del.getSql().startsWith("DELETE FROM t"));
        assertTrue(del.getSql().contains("WHERE"));
        assertTrue(del.getSql().contains("?"));
        assertEquals(select.getParameters(), del.getParameters());
    }

    @Test
    void sqliteAndMysqlApplyQuotingAndLimitBehavior() {
        final QueryBuilder qb = new QueryBuilder().from("users").whereEquals("id", 42).limit(5);
        final Query q = qb.build();
        q.setTable("users");
        final com.github.ezframework.javaquerybuilder.query.sql.mysql.MySqlDialect my =
            new com.github.ezframework.javaquerybuilder.query.sql.mysql.MySqlDialect();
        final SqlResult myDel = my.renderDelete(q);
        assertTrue(myDel.getSql().startsWith("DELETE FROM `users`"));
        assertTrue(myDel.getSql().contains("LIMIT 5"));

        final SqlDialect sqlite = new com.github.ezframework.javaquerybuilder.query.sql.sqlite.SqliteDialect();
        final SqlResult sqDel = sqlite.renderDelete(q);
        assertTrue(sqDel.getSql().startsWith("DELETE FROM \"users\""));
        assertTrue(sqDel.getSql().contains("LIMIT 5"));

        final DummyDialect d = new DummyDialect();
        final SqlResult dDel = d.renderDelete(q);
        assertTrue(dDel.getSql().startsWith("DELETE FROM users"));
        assertFalse(dDel.getSql().contains("LIMIT"));
    }

    @Test
    void inClauseGeneratesCorrectPlaceholderCountAndOrdering() {
        final DummyDialect dialect = new DummyDialect();
        final QueryBuilder qb = new QueryBuilder().from("items").whereIn("id", List.of(1, 2, 3));
        final Query q = qb.build();
        q.setTable("items");

        final SqlResult del = dialect.renderDelete(q);
        assertEquals("DELETE FROM items WHERE id IN (?, ?, ?)", del.getSql());
        assertEquals(List.of(1, 2, 3), del.getParameters());
    }
}
