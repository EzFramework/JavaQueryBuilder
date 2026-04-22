package com.github.ezframework.javaquerybuilder.query.builder.col;

import com.github.ezframework.javaquerybuilder.query.builder.ColumnType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ColTypesTest {

    // ── Fixed integer types ───────────────────────────────────────────────

    @Test
    void tinyIntReturnsTinyint() {
        assertEquals("TINYINT", TinyInt.of().toSql());
    }

    @Test
    void smallIntReturnsSmallint() {
        assertEquals("SMALLINT", SmallInt.of().toSql());
    }

    @Test
    void intReturnsInt() {
        assertEquals("INT", Int.of().toSql());
    }

    @Test
    void bigIntReturnsBigint() {
        assertEquals("BIGINT", BigInt.of().toSql());
    }

    // ── Fixed floating-point types ────────────────────────────────────────

    @Test
    void sqlFloatReturnsFloat() {
        assertEquals("FLOAT", SqlFloat.of().toSql());
    }

    @Test
    void sqlDoubleReturnsDouble() {
        assertEquals("DOUBLE", SqlDouble.of().toSql());
    }

    @Test
    void realReturnsReal() {
        assertEquals("REAL", Real.of().toSql());
    }

    // ── Boolean ───────────────────────────────────────────────────────────

    @Test
    void boolReturnsBoolean() {
        assertEquals("BOOLEAN", Bool.of().toSql());
    }

    // ── Fixed text types ──────────────────────────────────────────────────

    @Test
    void textReturnsText() {
        assertEquals("TEXT", Text.of().toSql());
    }

    @Test
    void tinyTextReturnsTinytext() {
        assertEquals("TINYTEXT", TinyText.of().toSql());
    }

    @Test
    void mediumTextReturnsMediumtext() {
        assertEquals("MEDIUMTEXT", MediumText.of().toSql());
    }

    @Test
    void longTextReturnsLongtext() {
        assertEquals("LONGTEXT", LongText.of().toSql());
    }

    @Test
    void clobReturnsClob() {
        assertEquals("CLOB", Clob.of().toSql());
    }

    // ── Binary types ──────────────────────────────────────────────────────

    @Test
    void blobReturnsBlob() {
        assertEquals("BLOB", Blob.of().toSql());
    }

    @Test
    void tinyBlobReturnsTinyblob() {
        assertEquals("TINYBLOB", TinyBlob.of().toSql());
    }

    @Test
    void mediumBlobReturnsMediumblob() {
        assertEquals("MEDIUMBLOB", MediumBlob.of().toSql());
    }

    @Test
    void longBlobReturnsLongblob() {
        assertEquals("LONGBLOB", LongBlob.of().toSql());
    }

    // ── Date / time types ─────────────────────────────────────────────────

    @Test
    void dateReturnsDate() {
        assertEquals("DATE", Date.of().toSql());
    }

    @Test
    void timeReturnsTime() {
        assertEquals("TIME", Time.of().toSql());
    }

    @Test
    void dateTimeReturnsDatetime() {
        assertEquals("DATETIME", DateTime.of().toSql());
    }

    @Test
    void timestampNoArgReturnsTimestamp() {
        assertEquals("TIMESTAMP", Timestamp.of().toSql());
    }

    @Test
    void timestampWithPrecisionReturnsPrecision() {
        assertEquals("TIMESTAMP(3)", Timestamp.of(3).toSql());
    }

    // ── Miscellaneous types ───────────────────────────────────────────────

    @Test
    void jsonReturnsJson() {
        assertEquals("JSON", Json.of().toSql());
    }

    @Test
    void serialReturnsSerial() {
        assertEquals("SERIAL", Serial.of().toSql());
    }

    @Test
    void bigSerialReturnsBigserial() {
        assertEquals("BIGSERIAL", BigSerial.of().toSql());
    }

    @Test
    void uuidReturnsUuid() {
        assertEquals("UUID", Uuid.of().toSql());
    }

    // ── Parameterised types ───────────────────────────────────────────────

    @Test
    void varCharBuildsCorrectSql() {
        assertEquals("VARCHAR(64)", VarChar.of(64).toSql());
    }

    @Test
    void charBuildsCorrectSql() {
        assertEquals("CHAR(2)", Char.of(2).toSql());
    }

    @Test
    void decimalBuildsCorrectSql() {
        assertEquals("DECIMAL(10, 2)", Decimal.of(10, 2).toSql());
    }

    @Test
    void numericBuildsCorrectSql() {
        assertEquals("NUMERIC(15, 4)", Numeric.of(15, 4).toSql());
    }

    @Test
    void binaryBuildsCorrectSql() {
        assertEquals("BINARY(32)", Binary.of(32).toSql());
    }

    @Test
    void varBinaryBuildsCorrectSql() {
        assertEquals("VARBINARY(128)", VarBinary.of(128).toSql());
    }

    // ── Modifier chaining ─────────────────────────────────────────────────

    @Test
    void modifiersChainOnColType() {
        final ColumnType result = Int.of().notNull().autoIncrement();
        assertEquals("INT NOT NULL AUTO_INCREMENT", result.toSql());
    }

    @Test
    void varCharWithModifiers() {
        final ColumnType result = VarChar.of(64).notNull().unique();
        assertEquals("VARCHAR(64) NOT NULL UNIQUE", result.toSql());
    }
}
