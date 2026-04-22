package com.github.ezframework.javaquerybuilder.query.builder;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ColumnTypeTest {

    // ── Constants ────────────────────────────────────────────────────────

    @Test
    void intConstantHasCorrectSql() {
        assertEquals("INT", ColumnType.INT.toSql());
    }

    @Test
    void bigintConstantHasCorrectSql() {
        assertEquals("BIGINT", ColumnType.BIGINT.toSql());
    }

    @Test
    void smallintConstantHasCorrectSql() {
        assertEquals("SMALLINT", ColumnType.SMALLINT.toSql());
    }

    @Test
    void tinyintConstantHasCorrectSql() {
        assertEquals("TINYINT", ColumnType.TINYINT.toSql());
    }

    @Test
    void integerConstantHasCorrectSql() {
        assertEquals("INTEGER", ColumnType.INTEGER.toSql());
    }

    @Test
    void floatConstantHasCorrectSql() {
        assertEquals("FLOAT", ColumnType.FLOAT.toSql());
    }

    @Test
    void doubleConstantHasCorrectSql() {
        assertEquals("DOUBLE", ColumnType.DOUBLE.toSql());
    }

    @Test
    void realConstantHasCorrectSql() {
        assertEquals("REAL", ColumnType.REAL.toSql());
    }

    @Test
    void booleanConstantHasCorrectSql() {
        assertEquals("BOOLEAN", ColumnType.BOOLEAN.toSql());
    }

    @Test
    void textConstantHasCorrectSql() {
        assertEquals("TEXT", ColumnType.TEXT.toSql());
    }

    @Test
    void blobConstantHasCorrectSql() {
        assertEquals("BLOB", ColumnType.BLOB.toSql());
    }

    @Test
    void dateConstantHasCorrectSql() {
        assertEquals("DATE", ColumnType.DATE.toSql());
    }

    @Test
    void timeConstantHasCorrectSql() {
        assertEquals("TIME", ColumnType.TIME.toSql());
    }

    @Test
    void datetimeConstantHasCorrectSql() {
        assertEquals("DATETIME", ColumnType.DATETIME.toSql());
    }

    @Test
    void timestampConstantHasCorrectSql() {
        assertEquals("TIMESTAMP", ColumnType.TIMESTAMP.toSql());
    }

    @Test
    void jsonConstantHasCorrectSql() {
        assertEquals("JSON", ColumnType.JSON.toSql());
    }

    @Test
    void serialConstantHasCorrectSql() {
        assertEquals("SERIAL", ColumnType.SERIAL.toSql());
    }

    @Test
    void bigserialConstantHasCorrectSql() {
        assertEquals("BIGSERIAL", ColumnType.BIGSERIAL.toSql());
    }

    @Test
    void uuidConstantHasCorrectSql() {
        assertEquals("UUID", ColumnType.UUID.toSql());
    }

    // ── Factory methods ──────────────────────────────────────────────────

    @Test
    void varCharFactoryBuildsCorrectSql() {
        assertEquals("VARCHAR(255)", ColumnType.varChar(255).toSql());
    }

    @Test
    void charTypeFactoryBuildsCorrectSql() {
        assertEquals("CHAR(10)", ColumnType.charType(10).toSql());
    }

    @Test
    void decimalFactoryBuildsCorrectSql() {
        assertEquals("DECIMAL(10, 2)", ColumnType.decimal(10, 2).toSql());
    }

    @Test
    void numericFactoryBuildsCorrectSql() {
        assertEquals("NUMERIC(8, 4)", ColumnType.numeric(8, 4).toSql());
    }

    @Test
    void binaryFactoryBuildsCorrectSql() {
        assertEquals("BINARY(16)", ColumnType.binary(16).toSql());
    }

    @Test
    void varBinaryFactoryBuildsCorrectSql() {
        assertEquals("VARBINARY(512)", ColumnType.varBinary(512).toSql());
    }

    @Test
    void timestampWithPrecisionFactoryBuildsCorrectSql() {
        assertEquals("TIMESTAMP(6)", ColumnType.timestamp(6).toSql());
    }

    // ── Modifier methods ─────────────────────────────────────────────────

    @Test
    void notNullAppendsSuffix() {
        assertEquals("INT NOT NULL", ColumnType.INT.notNull().toSql());
    }

    @Test
    void uniqueAppendsSuffix() {
        assertEquals("VARCHAR(64) UNIQUE", ColumnType.varChar(64).unique().toSql());
    }

    @Test
    void autoIncrementAppendsSuffix() {
        assertEquals("INT AUTO_INCREMENT", ColumnType.INT.autoIncrement().toSql());
    }

    @Test
    void defaultValueAppendsSuffix() {
        assertEquals("BOOLEAN DEFAULT false", ColumnType.BOOLEAN.defaultValue("false").toSql());
    }

    @Test
    void modifiersCombineInOrder() {
        final String expected = "INT NOT NULL AUTO_INCREMENT";
        assertEquals(expected, ColumnType.INT.notNull().autoIncrement().toSql());
    }

    @Test
    void varCharWithNotNullAndUnique() {
        final String expected = "VARCHAR(64) NOT NULL UNIQUE";
        assertEquals(expected, ColumnType.varChar(64).notNull().unique().toSql());
    }

    // ── toString / public constructor ────────────────────────────────────

    @Test
    void toStringMatchesToSql() {
        assertEquals(ColumnType.TEXT.toSql(), ColumnType.TEXT.toString());
    }

    @Test
    void toStringOnFactoryMatchesToSql() {
        final ColumnType ct = ColumnType.varChar(128);
        assertEquals(ct.toSql(), ct.toString());
    }

    @Test
    void publicConstructorAcceptsCustomType() {
        assertEquals("GEOMETRY", new ColumnType("GEOMETRY").toSql());
    }

    // ── CreateBuilder integration ────────────────────────────────────────

    @Test
    void createBuilderAcceptsColumnTypeOverload() {
        final String sql = new CreateBuilder()
            .table("users")
            .column("id",       ColumnType.INT.notNull().autoIncrement())
            .column("username", ColumnType.varChar(64).notNull())
            .column("score",    ColumnType.decimal(5, 2))
            .primaryKey("id")
            .build()
            .getSql();
        assertEquals(
            "CREATE TABLE users ("
                + "id INT NOT NULL AUTO_INCREMENT, "
                + "username VARCHAR(64) NOT NULL, "
                + "score DECIMAL(5, 2), "
                + "PRIMARY KEY (id))",
            sql);
    }
}
