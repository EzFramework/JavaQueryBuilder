package com.github.ezframework.javaquerybuilder.query;

import com.github.ezframework.javaquerybuilder.query.sql.SqlDialect;

/**
 * Immutable configuration object that holds the default values applied by
 * every builder ({@code QueryBuilder}, {@code DeleteBuilder},
 * {@code SelectBuilder}) when no explicit value is provided.
 *
 * <p>Usage — configure once at application startup:
 * <pre>{@code
 * QueryBuilderDefaults.setGlobal(
 *     QueryBuilderDefaults.builder()
 *         .dialect(SqlDialect.MYSQL)
 *         .defaultColumns("id, name")
 *         .defaultLimit(100)
 *         .likePrefix("%")
 *         .likeSuffix("%")
 *         .build()
 * );
 * }</pre>
 *
 * <p>Override per-builder-instance:
 * <pre>{@code
 * SqlResult result = new QueryBuilder()
 *     .withDefaults(QueryBuilderDefaults.builder(QueryBuilderDefaults.global())
 *         .dialect(SqlDialect.SQLITE)
 *         .build())
 *     .from("users")
 *     .buildSql();
 * }</pre>
 *
 * @author EzFramework
 * @version 1.0.0
 */
public final class QueryBuilderDefaults {

    /** Default SELECT-all token used when no columns are specified. */
    static final String WILDCARD = "*";

    /** Sentinel value meaning "no limit / no offset". */
    static final int NO_LIMIT = -1;

    /** JVM-wide defaults instance; captured by builders at construction time. */
    private static volatile QueryBuilderDefaults globalInstance = new QueryBuilderDefaults();

    /** The SQL dialect used for rendering. */
    private final SqlDialect dialect;

    /** Column list used when the builder has no explicit {@code select()} call. */
    private final String defaultColumns;

    /**
     * Default LIMIT applied when the builder has no explicit {@code limit()} call.
     * {@code -1} means no default limit is applied.
     */
    private final int defaultLimit;

    /**
     * Default OFFSET applied when the builder has no explicit {@code offset()} call.
     * {@code -1} means no default offset is applied.
     */
    private final int defaultOffset;

    /** Prefix prepended to the value for {@code LIKE} and {@code NOT LIKE} conditions. */
    private final String likePrefix;

    /** Suffix appended to the value for {@code LIKE} and {@code NOT LIKE} conditions. */
    private final String likeSuffix;

    /**
     * Private constructor used by {@link Builder#build()}.
     * All fields default to the canonical out-of-the-box values when created
     * via the no-arg path.
     */
    private QueryBuilderDefaults() {
        this.dialect = SqlDialect.STANDARD;
        this.defaultColumns = WILDCARD;
        this.defaultLimit = NO_LIMIT;
        this.defaultOffset = NO_LIMIT;
        this.likePrefix = "%";
        this.likeSuffix = "%";
    }

    /**
     * Private constructor used by {@link Builder#build()} to set all fields.
     *
     * @param builder the builder whose values are copied into this instance
     */
    private QueryBuilderDefaults(final Builder builder) {
        this.dialect = builder.dialect;
        this.defaultColumns = builder.defaultColumns;
        this.defaultLimit = builder.defaultLimit;
        this.defaultOffset = builder.defaultOffset;
        this.likePrefix = builder.likePrefix;
        this.likeSuffix = builder.likeSuffix;
    }

    // -----------------------------------------------------------------------
    // Global accessor
    // -----------------------------------------------------------------------

    /**
     * Returns the current JVM-wide defaults instance.
     * Every builder captures this value at construction time.
     *
     * @return the global {@link QueryBuilderDefaults} instance; never {@code null}
     */
    public static QueryBuilderDefaults global() {
        return globalInstance;
    }

    /**
     * Replaces the JVM-wide defaults instance.
     * The change is visible to all builders created after this call.
     * Builders already constructed are unaffected.
     *
     * @param defaults the new global defaults; must not be {@code null}
     * @throws NullPointerException if {@code defaults} is {@code null}
     */
    public static void setGlobal(final QueryBuilderDefaults defaults) {
        if (defaults == null) {
            throw new NullPointerException("Global QueryBuilderDefaults must not be null");
        }
        globalInstance = defaults;
    }

    // -----------------------------------------------------------------------
    // Factory methods
    // -----------------------------------------------------------------------

    /**
     * Creates a new {@link Builder} pre-filled with the canonical out-of-the-box
     * defaults (STANDARD dialect, {@code "*"} columns, no limit, no offset,
     * {@code "%"} LIKE wrapping).
     *
     * @return a new builder initialised with default values
     */
    public static Builder builder() {
        return new Builder(new QueryBuilderDefaults());
    }

    /**
     * Creates a new {@link Builder} pre-filled with all values copied from
     * {@code source}. Useful for partial-override patterns.
     *
     * @param source the instance whose values are copied; must not be {@code null}
     * @return a new builder initialised from {@code source}
     * @throws NullPointerException if {@code source} is {@code null}
     */
    public static Builder builder(final QueryBuilderDefaults source) {
        if (source == null) {
            throw new NullPointerException("Source QueryBuilderDefaults must not be null");
        }
        return new Builder(source);
    }

    // -----------------------------------------------------------------------
    // Getters
    // -----------------------------------------------------------------------

    /**
     * Returns the SQL dialect used by builders that do not receive an explicit
     * dialect at build time.
     *
     * @return the configured dialect; never {@code null}
     */
    public SqlDialect getDialect() {
        return dialect;
    }

    /**
     * Returns the column expression used in {@code SELECT} when no columns are
     * specified on the builder.
     *
     * @return the default column expression; never {@code null}
     */
    public String getDefaultColumns() {
        return defaultColumns;
    }

    /**
     * Returns the default {@code LIMIT} value.
     * {@code -1} means no limit is applied as a default.
     *
     * @return the default limit; {@code -1} if none
     */
    public int getDefaultLimit() {
        return defaultLimit;
    }

    /**
     * Returns the default {@code OFFSET} value.
     * {@code -1} means no offset is applied as a default.
     *
     * @return the default offset; {@code -1} if none
     */
    public int getDefaultOffset() {
        return defaultOffset;
    }

    /**
     * Returns the prefix prepended to values for {@code LIKE} and
     * {@code NOT LIKE} conditions.
     *
     * @return the LIKE prefix; never {@code null}
     */
    public String getLikePrefix() {
        return likePrefix;
    }

    /**
     * Returns the suffix appended to values for {@code LIKE} and
     * {@code NOT LIKE} conditions.
     *
     * @return the LIKE suffix; never {@code null}
     */
    public String getLikeSuffix() {
        return likeSuffix;
    }

    // -----------------------------------------------------------------------
    // Inner Builder
    // -----------------------------------------------------------------------

    /**
     * Mutable builder for constructing {@link QueryBuilderDefaults} instances.
     * Obtain via {@link QueryBuilderDefaults#builder()} or
     * {@link QueryBuilderDefaults#builder(QueryBuilderDefaults)}.
     *
     * @author EzFramework
     * @version 1.0.0
     */
    public static final class Builder {

        /** The SQL dialect; defaults to {@code SqlDialect.STANDARD}. */
        private SqlDialect dialect;

        /** The column expression for SELECT *; defaults to {@code "*"}. */
        private String defaultColumns;

        /** The default LIMIT; defaults to {@code -1}. */
        private int defaultLimit;

        /** The default OFFSET; defaults to {@code -1}. */
        private int defaultOffset;

        /** The LIKE prefix; defaults to {@code "%"}. */
        private String likePrefix;

        /** The LIKE suffix; defaults to {@code "%"}. */
        private String likeSuffix;

        /**
         * Constructs a {@code Builder} by copying all values from {@code source}.
         *
         * @param source the template instance; must not be {@code null}
         */
        private Builder(final QueryBuilderDefaults source) {
            this.dialect = source.dialect;
            this.defaultColumns = source.defaultColumns;
            this.defaultLimit = source.defaultLimit;
            this.defaultOffset = source.defaultOffset;
            this.likePrefix = source.likePrefix;
            this.likeSuffix = source.likeSuffix;
        }

        /**
         * Sets the SQL dialect.
         *
         * @param sqlDialect the dialect to use; must not be {@code null}
         * @return this builder for chaining
         * @throws NullPointerException if {@code sqlDialect} is {@code null}
         */
        public Builder dialect(final SqlDialect sqlDialect) {
            if (sqlDialect == null) {
                throw new NullPointerException("Dialect must not be null");
            }
            this.dialect = sqlDialect;
            return this;
        }

        /**
         * Sets the default column expression used in {@code SELECT} when no
         * explicit columns are provided.
         *
         * @param columns the column expression, e.g. {@code "*"} or {@code "id, name"}
         * @return this builder for chaining
         * @throws NullPointerException if {@code columns} is {@code null}
         */
        public Builder defaultColumns(final String columns) {
            if (columns == null) {
                throw new NullPointerException("Default columns must not be null");
            }
            this.defaultColumns = columns;
            return this;
        }

        /**
         * Sets the default {@code LIMIT} applied when no explicit limit is set on
         * a builder. Use {@code -1} to disable the default limit.
         *
         * @param limit the default limit value; {@code -1} for none
         * @return this builder for chaining
         */
        public Builder defaultLimit(final int limit) {
            this.defaultLimit = limit;
            return this;
        }

        /**
         * Sets the default {@code OFFSET} applied when no explicit offset is set
         * on a builder. Use {@code -1} to disable the default offset.
         *
         * @param offset the default offset value; {@code -1} for none
         * @return this builder for chaining
         */
        public Builder defaultOffset(final int offset) {
            this.defaultOffset = offset;
            return this;
        }

        /**
         * Sets the prefix prepended to values in {@code LIKE} and
         * {@code NOT LIKE} conditions.
         *
         * @param prefix the LIKE prefix; must not be {@code null}
         * @return this builder for chaining
         * @throws NullPointerException if {@code prefix} is {@code null}
         */
        public Builder likePrefix(final String prefix) {
            if (prefix == null) {
                throw new NullPointerException("LIKE prefix must not be null");
            }
            this.likePrefix = prefix;
            return this;
        }

        /**
         * Sets the suffix appended to values in {@code LIKE} and
         * {@code NOT LIKE} conditions.
         *
         * @param suffix the LIKE suffix; must not be {@code null}
         * @return this builder for chaining
         * @throws NullPointerException if {@code suffix} is {@code null}
         */
        public Builder likeSuffix(final String suffix) {
            if (suffix == null) {
                throw new NullPointerException("LIKE suffix must not be null");
            }
            this.likeSuffix = suffix;
            return this;
        }

        /**
         * Builds a new {@link QueryBuilderDefaults} from the current state of
         * this builder.
         *
         * @return a new immutable {@link QueryBuilderDefaults} instance
         */
        public QueryBuilderDefaults build() {
            return new QueryBuilderDefaults(this);
        }
    }
}
