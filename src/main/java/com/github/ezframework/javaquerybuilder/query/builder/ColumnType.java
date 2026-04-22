package com.github.ezframework.javaquerybuilder.query.builder;

/**
 * Type-safe column type definitions for use with
 * {@link CreateBuilder#column(String, ColumnType)}.
 *
 * <p>Use the pre-defined constants for fixed-width types, the static factory
 * methods for parameterised types, and the modifier methods to append
 * column-level constraints:</p>
 *
 * <pre>{@code
 * QueryBuilder.createTable("users")
 *     .column("id",         ColumnType.INT.notNull().autoIncrement())
 *     .column("username",   ColumnType.varChar(64).notNull().unique())
 *     .column("balance",    ColumnType.decimal(10, 2))
 *     .column("created_at", ColumnType.TIMESTAMP)
 *     .primaryKey("id")
 *     .build();
 * // → CREATE TABLE users (
 * //       id         INT NOT NULL AUTO_INCREMENT,
 * //       username   VARCHAR(64) NOT NULL UNIQUE,
 * //       balance    DECIMAL(10, 2),
 * //       created_at TIMESTAMP,
 * //       PRIMARY KEY (id))
 * }</pre>
 *
 * <p>Raw SQL type strings are still accepted by
 * {@link CreateBuilder#column(String, String)} for any type not covered here.</p>
 *
 * @author EzFramework
 * @version 1.1.0
 */
public final class ColumnType {

    // ── Exact-width integer types ──────────────────────────────────────────

    /** 1-byte signed integer ({@code TINYINT}). Supported by MySQL, MariaDB. */
    public static final ColumnType TINYINT = new ColumnType("TINYINT");

    /** 2-byte signed integer ({@code SMALLINT}). */
    public static final ColumnType SMALLINT = new ColumnType("SMALLINT");

    /** 4-byte signed integer ({@code INT}). */
    public static final ColumnType INT = new ColumnType("INT");

    /** 4-byte signed integer ({@code INTEGER}). Alias for {@link #INT} in most databases. */
    public static final ColumnType INTEGER = new ColumnType("INTEGER");

    /** 8-byte signed integer ({@code BIGINT}). */
    public static final ColumnType BIGINT = new ColumnType("BIGINT");

    // ── Floating-point types ───────────────────────────────────────────────

    /** Single-precision floating point ({@code FLOAT}). */
    public static final ColumnType FLOAT = new ColumnType("FLOAT");

    /** Double-precision floating point ({@code DOUBLE}). */
    public static final ColumnType DOUBLE = new ColumnType("DOUBLE");

    /** Double-precision floating point ({@code REAL}). Standard SQL alias for {@link #DOUBLE}. */
    public static final ColumnType REAL = new ColumnType("REAL");

    // ── Boolean ───────────────────────────────────────────────────────────

    /** Boolean value ({@code BOOLEAN}). */
    public static final ColumnType BOOLEAN = new ColumnType("BOOLEAN");

    // ── Fixed-size text types ──────────────────────────────────────────────

    /**
     * Unbounded text ({@code TEXT}).
     * For length-limited strings use {@link #varChar(int)}.
     */
    public static final ColumnType TEXT = new ColumnType("TEXT");

    /** Small text up to 255 characters ({@code TINYTEXT}). MySQL / MariaDB only. */
    public static final ColumnType TINYTEXT = new ColumnType("TINYTEXT");

    /** Medium text up to 16 MB ({@code MEDIUMTEXT}). MySQL / MariaDB only. */
    public static final ColumnType MEDIUMTEXT = new ColumnType("MEDIUMTEXT");

    /** Large text up to 4 GB ({@code LONGTEXT}). MySQL / MariaDB only. */
    public static final ColumnType LONGTEXT = new ColumnType("LONGTEXT");

    /** Character large object ({@code CLOB}). Standard SQL; map to TEXT on MySQL. */
    public static final ColumnType CLOB = new ColumnType("CLOB");

    // ── Binary types ───────────────────────────────────────────────────────

    /** Binary large object ({@code BLOB}). */
    public static final ColumnType BLOB = new ColumnType("BLOB");

    /** Small binary up to 255 bytes ({@code TINYBLOB}). MySQL / MariaDB only. */
    public static final ColumnType TINYBLOB = new ColumnType("TINYBLOB");

    /** Medium binary up to 16 MB ({@code MEDIUMBLOB}). MySQL / MariaDB only. */
    public static final ColumnType MEDIUMBLOB = new ColumnType("MEDIUMBLOB");

    /** Large binary up to 4 GB ({@code LONGBLOB}). MySQL / MariaDB only. */
    public static final ColumnType LONGBLOB = new ColumnType("LONGBLOB");

    // ── Date and time types ────────────────────────────────────────────────

    /** Calendar date without time-of-day ({@code DATE}). */
    public static final ColumnType DATE = new ColumnType("DATE");

    /** Time-of-day without date ({@code TIME}). */
    public static final ColumnType TIME = new ColumnType("TIME");

    /** Date and time without timezone ({@code DATETIME}). MySQL / MariaDB / SQLite. */
    public static final ColumnType DATETIME = new ColumnType("DATETIME");

    /** Date and time, often auto-updated ({@code TIMESTAMP}). */
    public static final ColumnType TIMESTAMP = new ColumnType("TIMESTAMP");

    // ── Miscellaneous types ────────────────────────────────────────────────

    /** JSON document ({@code JSON}). MySQL 5.7+, PostgreSQL 9.2+, SQLite 3.38+. */
    public static final ColumnType JSON = new ColumnType("JSON");

    /**
     * Auto-incrementing 4-byte integer ({@code SERIAL}).
     * PostgreSQL only; equivalent to {@code INT NOT NULL AUTO_INCREMENT} on MySQL.
     */
    public static final ColumnType SERIAL = new ColumnType("SERIAL");

    /**
     * Auto-incrementing 8-byte integer ({@code BIGSERIAL}).
     * PostgreSQL only; equivalent to {@code BIGINT NOT NULL AUTO_INCREMENT} on MySQL.
     */
    public static final ColumnType BIGSERIAL = new ColumnType("BIGSERIAL");

    /**
     * Universally unique identifier ({@code UUID}).
     * Native on PostgreSQL; stored as {@code CHAR(36)} or {@code BINARY(16)} on others.
     */
    public static final ColumnType UUID = new ColumnType("UUID");

    // ── Internal state ─────────────────────────────────────────────────────

    /** The SQL fragment representing this column type definition. */
    private final String sql;

    /**
     * Creates a {@code ColumnType} from an arbitrary SQL type fragment.
     *
     * <p>Use this constructor for database-specific types not covered by the
     * pre-defined constants, e.g. {@code new ColumnType("GEOMETRY")}.</p>
     *
     * @param sql the SQL type string; must not be {@code null} or blank
     */
    public ColumnType(final String sql) {
        this.sql = sql;
    }

    // ── Parameterised factory methods ──────────────────────────────────────

    /**
     * Variable-length character string: {@code VARCHAR(length)}.
     *
     * @param length maximum number of characters; must be positive
     * @return a new {@code ColumnType} representing {@code VARCHAR(length)}
     */
    public static ColumnType varChar(final int length) {
        return new ColumnType("VARCHAR(" + length + ")");
    }

    /**
     * Fixed-length character string: {@code CHAR(length)}.
     *
     * @param length number of characters; must be positive
     * @return a new {@code ColumnType} representing {@code CHAR(length)}
     */
    public static ColumnType charType(final int length) {
        return new ColumnType("CHAR(" + length + ")");
    }

    /**
     * Exact decimal number: {@code DECIMAL(precision, scale)}.
     *
     * @param precision total number of significant digits; must be positive
     * @param scale     number of digits after the decimal point; must be &ge; 0
     * @return a new {@code ColumnType} representing {@code DECIMAL(precision, scale)}
     */
    public static ColumnType decimal(final int precision, final int scale) {
        return new ColumnType("DECIMAL(" + precision + ", " + scale + ")");
    }

    /**
     * Exact numeric value: {@code NUMERIC(precision, scale)}.
     * Equivalent to {@link #decimal(int, int)} in most databases.
     *
     * @param precision total number of significant digits; must be positive
     * @param scale     number of digits after the decimal point; must be &ge; 0
     * @return a new {@code ColumnType} representing {@code NUMERIC(precision, scale)}
     */
    public static ColumnType numeric(final int precision, final int scale) {
        return new ColumnType("NUMERIC(" + precision + ", " + scale + ")");
    }

    /**
     * Fixed-length binary string: {@code BINARY(length)}.
     *
     * @param length number of bytes; must be positive
     * @return a new {@code ColumnType} representing {@code BINARY(length)}
     */
    public static ColumnType binary(final int length) {
        return new ColumnType("BINARY(" + length + ")");
    }

    /**
     * Variable-length binary string: {@code VARBINARY(length)}.
     *
     * @param length maximum number of bytes; must be positive
     * @return a new {@code ColumnType} representing {@code VARBINARY(length)}
     */
    public static ColumnType varBinary(final int length) {
        return new ColumnType("VARBINARY(" + length + ")");
    }

    /**
     * Timestamp with fractional seconds precision: {@code TIMESTAMP(precision)}.
     * Useful for {@code TIMESTAMP(6)} (microseconds) on MySQL and PostgreSQL.
     *
     * @param precision fractional seconds digits (0–6); must be between 0 and 6 inclusive
     * @return a new {@code ColumnType} representing {@code TIMESTAMP(precision)}
     */
    public static ColumnType timestamp(final int precision) {
        return new ColumnType("TIMESTAMP(" + precision + ")");
    }

    // ── Column-level constraint modifiers ──────────────────────────────────

    /**
     * Appends {@code NOT NULL} to this column type.
     *
     * @return a new {@code ColumnType} with {@code NOT NULL} appended
     */
    public ColumnType notNull() {
        return new ColumnType(sql + " NOT NULL");
    }

    /**
     * Appends {@code UNIQUE} to this column type.
     *
     * @return a new {@code ColumnType} with {@code UNIQUE} appended
     */
    public ColumnType unique() {
        return new ColumnType(sql + " UNIQUE");
    }

    /**
     * Appends {@code AUTO_INCREMENT} to this column type (MySQL / MariaDB).
     * Use {@link #SERIAL} or {@link #BIGSERIAL} for PostgreSQL auto-increment columns.
     *
     * @return a new {@code ColumnType} with {@code AUTO_INCREMENT} appended
     */
    public ColumnType autoIncrement() {
        return new ColumnType(sql + " AUTO_INCREMENT");
    }

    /**
     * Appends {@code DEFAULT value} to this column type.
     *
     * <p>The {@code value} string is inserted verbatim into the SQL column definition.
     * Use only static, known-safe values here (e.g. {@code "0"}, {@code "false"},
     * {@code "CURRENT_TIMESTAMP"}). Never pass user-supplied input.</p>
     *
     * @param value the default SQL expression or literal to append; must not be {@code null}
     * @return a new {@code ColumnType} with {@code DEFAULT value} appended
     */
    public ColumnType defaultValue(final String value) {
        return new ColumnType(sql + " DEFAULT " + value);
    }

    // ── Accessors ──────────────────────────────────────────────────────────

    /**
     * Returns the SQL fragment for this column type, including any modifiers.
     *
     * @return the SQL column type string (e.g. {@code "VARCHAR(64) NOT NULL"})
     */
    public String toSql() {
        return sql;
    }

    /**
     * Returns the SQL fragment for this column type.
     * Equivalent to {@link #toSql()}.
     *
     * @return the SQL column type string
     */
    @Override
    public String toString() {
        return sql;
    }
}
