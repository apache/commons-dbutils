/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.dbutils;

import java.time.Duration;

/**
 * Configuration options for a {@link java.sql.Statement} when preparing statements in {@code QueryRunner}.
 */
public class StatementConfiguration {
    /**
     * Builder class for {@code StatementConfiguration} for more flexible construction.
     */
    public static final class Builder {
        private Integer fetchDirection;
        private Integer fetchSize;
        private Integer maxRows;
        private Duration queryTimeout;
        private Integer maxFieldSize;

        /**
         * @return A new and configured {@link StatementConfiguration}.
         */
        public StatementConfiguration build() {
            return new StatementConfiguration(fetchDirection, fetchSize, maxFieldSize, maxRows, queryTimeout);
        }

        /**
         * @param fetchDirection The direction for fetching rows from database tables.
         * @return This builder for chaining.
         * @see StatementConfiguration#getFetchDirection()
         */
        public Builder fetchDirection(final Integer fetchDirection) {
            this.fetchDirection = fetchDirection;
            return this;
        }

        /**
         * @param fetchSize The number of rows that should be fetched from the database when more rows are needed.
         * @return This builder for chaining.
         * @see StatementConfiguration#getFetchSize()
         */
        public Builder fetchSize(final Integer fetchSize) {
            this.fetchSize = fetchSize;
            return this;
        }

        /**
         * @param maxFieldSize The maximum number of bytes that can be returned for character and binary column values.
         * @return This builder for chaining.
         * @see StatementConfiguration#getMaxFieldSize()
         */
        public Builder maxFieldSize(final Integer maxFieldSize) {
            this.maxFieldSize = maxFieldSize;
            return this;
        }

        /**
         * @param maxRows The maximum number of rows that a {@code ResultSet} can produce.
         * @return This builder for chaining.
         * @see StatementConfiguration#getMaxRows()
         */
        public Builder maxRows(final Integer maxRows) {
            this.maxRows = maxRows;
            return this;
        }

        /**
         * @param queryTimeout The number of seconds the driver will wait for execution.
         * @return This builder for chaining.
         * @see StatementConfiguration#getQueryTimeoutDuration()
         * @since 1.8.0
         */
        public Builder queryTimeout(final Duration queryTimeout) {
            this.queryTimeout = queryTimeout;
            return this;
        }

        /**
         * @param queryTimeout The number of seconds the driver will wait for execution.
         * @return This builder for chaining.
         * @see StatementConfiguration#getQueryTimeout()
         * @deprecated Use {@link #queryTimeout(Duration)}.
         */
        @Deprecated
        public Builder queryTimeout(final Integer queryTimeout) {
            this.queryTimeout = queryTimeout != null ? Duration.ofSeconds(queryTimeout) : null;
            return this;
        }
    }

    private final Integer fetchDirection;
    private final Integer fetchSize;
    private final Integer maxFieldSize;
    private final Integer maxRows;
    private final Duration queryTimeout;

    /**
     * Constructor for {@code StatementConfiguration}.  For more flexibility, use {@link Builder}.
     *
     * @param fetchDirection The direction for fetching rows from database tables.
     * @param fetchSize The number of rows that should be fetched from the database when more rows are needed.
     * @param maxFieldSize The maximum number of bytes that can be returned for character and binary column values.
     * @param maxRows The maximum number of rows that a {@code ResultSet} can produce.
     * @param queryTimeout The number of seconds the driver will wait for execution.
     * @since 1.8.0
     */
    public StatementConfiguration(final Integer fetchDirection, final Integer fetchSize,
                                  final Integer maxFieldSize, final Integer maxRows,
                                  final Duration queryTimeout) {
        this.fetchDirection = fetchDirection;
        this.fetchSize = fetchSize;
        this.maxFieldSize = maxFieldSize;
        this.maxRows = maxRows;
        if (queryTimeout != null && queryTimeout.getSeconds() > Integer.MAX_VALUE) {
            throw new IllegalArgumentException(String.format("queryTimeout overflow: %d > %,d", queryTimeout.getSeconds(), Integer.MAX_VALUE));
        }
        this.queryTimeout = queryTimeout;
    }

    /**
     * Constructor for {@code StatementConfiguration}.  For more flexibility, use {@link Builder}.
     *
     * @param fetchDirection The direction for fetching rows from database tables.
     * @param fetchSize The number of rows that should be fetched from the database when more rows are needed.
     * @param maxFieldSize The maximum number of bytes that can be returned for character and binary column values.
     * @param maxRows The maximum number of rows that a {@code ResultSet} can produce.
     * @param queryTimeout The number of seconds the driver will wait for execution.
     * @deprecated Use {@link StatementConfiguration#StatementConfiguration(Integer, Integer, Integer, Integer, Duration)}.
     */
    @Deprecated
    public StatementConfiguration(final Integer fetchDirection, final Integer fetchSize,
                                  final Integer maxFieldSize, final Integer maxRows,
                                  final Integer queryTimeout) {
        this(fetchDirection, fetchSize, maxFieldSize, maxRows, Duration.ofSeconds(queryTimeout));
    }

    /** Does nothing. */
    @Override
    protected final void finalize() {
        // SpotBugs CT_CONSTRUCTOR_THROW
    }

    /**
     * Gets the fetch direction.
     *
     * @return The direction to fetch or null if not set.
     */
    public Integer getFetchDirection() {
        return fetchDirection;
    }

    /**
     * Gets the fetch size.
     *
     * @return The fetch size or null if not set.
     */
    public Integer getFetchSize() {
        return fetchSize;
    }

    /**
     * Gets the max field size.
     *
     * @return The max field size or null if not set.
     */
    public Integer getMaxFieldSize() {
        return maxFieldSize;
    }

    /**
     * Gets the max rows.
     *
     * @return The max rows or null if not set.
     */
    public Integer getMaxRows() {
        return maxRows;
    }

    /**
     * Gets the query timeout.
     *
     * @return The query timeout or null if not set.
     * @deprecated Use {@link #getQueryTimeoutDuration()}.
     */
    @Deprecated
    public Integer getQueryTimeout() {
        return queryTimeout != null ? (int) queryTimeout.getSeconds() : null;
    }

    /**
     * Gets the query timeout.
     *
     * @return The query timeout or null if not set.
     * @since 1.8.0
     */
    public Duration getQueryTimeoutDuration() {
        return queryTimeout;
    }

    /**
     * Whether fetch direction is set.
     *
     * @return true if set, false otherwise.
     */
    public boolean isFetchDirectionSet() {
        return fetchDirection != null;
    }

    /**
     * Whether fetch size is set.
     *
     * @return true if set, false otherwise.
     */
    public boolean isFetchSizeSet() {
        return fetchSize != null;
    }

    /**
     * Whether max field size is set.
     *
     * @return true if set, false otherwise.
     */
    public boolean isMaxFieldSizeSet() {
        return maxFieldSize != null;
    }

    /**
     * Whether max rows is set.
     *
     * @return true if set, false otherwise.
     */
    public boolean isMaxRowsSet() {
        return maxRows != null;
    }

    /**
     * Whether query timeout is set.
     *
     * @return true if set, false otherwise.
     */
    public boolean isQueryTimeoutSet() {
        return queryTimeout != null;
    }
}
