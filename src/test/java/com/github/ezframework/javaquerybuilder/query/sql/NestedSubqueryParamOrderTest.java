package com.github.ezframework.javaquerybuilder.query.sql;

import java.util.List;

import com.github.ezframework.javaquerybuilder.query.Query;
import com.github.ezframework.javaquerybuilder.query.builder.QueryBuilder;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

/**
 * Tests verifying deterministic parameter ordering across nested and combined subqueries.
 *
 * <p>Contract: parameters are emitted in document order:
 * scalar SELECT params → FROM params → JOIN params → WHERE params.
 */
public class NestedSubqueryParamOrderTest {

    @Test
    void scalarThenWhere_paramOrder() {
        final Query scalar = new QueryBuilder()
            .from("a").select("x").whereEquals("ak", "A").build();

        final SqlResult result = new QueryBuilder()
            .from("t")
            .select("col")
            .selectSubquery(scalar, "alias")
            .whereEquals("col", "W")
            .buildSql("t", SqlDialect.STANDARD);

        assertIterableEquals(List.of("A", "W"), result.getParameters());
    }

    @Test
    void fromSubqueryThenWhere_paramOrder() {
        final Query fromQ = new QueryBuilder()
            .from("inner_t").select("v").whereEquals("fk", "F").build();

        final SqlResult result = new QueryBuilder()
            .fromSubquery(fromQ, "d")
            .whereEquals("v", "W")
            .buildSql(null, SqlDialect.STANDARD);

        assertIterableEquals(List.of("F", "W"), result.getParameters());
    }

    @Test
    void joinThenWhere_paramOrder() {
        final Query joinQ = new QueryBuilder()
            .from("j").select("jid").whereEquals("jk", "J").build();

        final SqlResult result = new QueryBuilder()
            .from("t")
            .joinSubquery(joinQ, "jt", "t.id = jt.jid")
            .whereEquals("t.status", "active")
            .buildSql("t", SqlDialect.STANDARD);

        assertIterableEquals(List.of("J", "active"), result.getParameters());
    }

    @Test
    void scalarFromJoinWhere_fullOrder() {
        final Query scalar = new QueryBuilder()
            .from("s").select("sv").whereEquals("sk", "S").build();
        final Query fromQ = new QueryBuilder()
            .from("f").select("fv").whereEquals("fk", "F").build();
        final Query joinQ = new QueryBuilder()
            .from("jt").select("jv").whereEquals("jk", "J").build();

        final SqlResult result = new QueryBuilder()
            .fromSubquery(fromQ, "d")
            .select("d.fv")
            .selectSubquery(scalar, "sv")
            .joinSubquery(joinQ, "jd", "d.fv = jd.jv")
            .whereEquals("d.fv", "W")
            .buildSql(null, SqlDialect.STANDARD);

        // expected order: scalar(S), FROM(F), JOIN(J), WHERE(W)
        assertIterableEquals(List.of("S", "F", "J", "W"), result.getParameters());
    }

    @Test
    void nestedSubquery_threeLevel_paramOrder() {
        // level-3 innermost
        final Query l3 = new QueryBuilder()
            .from("deep").select("id").whereEquals("lv", 3).build();
        // level-2
        final Query l2 = new QueryBuilder()
            .from("mid").select("id").whereInSubquery("ref", l3).build();
        // level-1 outer WHERE IN subquery
        final SqlResult result = new QueryBuilder()
            .from("top")
            .whereInSubquery("id", l2)
            .buildSql("top", SqlDialect.STANDARD);

        assertEquals(
            "SELECT * FROM top WHERE id IN "
                + "(SELECT id FROM mid WHERE ref IN (SELECT id FROM deep WHERE lv = ?))",
            result.getSql()
        );
        assertIterableEquals(List.of(3), result.getParameters());
    }
}
