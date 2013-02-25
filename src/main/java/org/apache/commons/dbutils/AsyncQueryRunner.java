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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Executes SQL queries with pluggable strategies for handling
 * <code>ResultSet</code>s.  This class is thread safe.
 *
 * @see ResultSetHandler
 * @since 1.4
 */
public class AsyncQueryRunner extends AbstractQueryRunner {

    private final ExecutorService executorService;
    private final QueryRunner queryRunner;

    /**
     * Constructor for AsyncQueryRunner which uses a provided ExecutorService and underlying QueryRunner.
     *
     * @param executorService the {@code ExecutorService} instance used to run JDBC invocations concurrently.
     * @param queryRunner the {@code QueryRunner} instance to use for the queries.
     * @since DbUtils 1.5
     */
    public AsyncQueryRunner(ExecutorService executorService, QueryRunner queryRunner) {
        this.executorService = executorService;
        this.queryRunner = queryRunner;
    }

    /**
     * Execute a batch of SQL INSERT, UPDATE, or DELETE queries.
     *
     * @param conn The <code>Connection</code> to use to run the query.  The caller is
     * responsible for closing this Connection.
     * @param sql The SQL to execute.
     * @param params An array of query replacement parameters.  Each row in
     * this array is one set of batch replacement values.
     * @return A <code>Future</code> which returns the number of rows updated per statement.
     * @throws SQLException if a database access error occurs
     */
    public Future<int[]> batch(final Connection conn, final String sql, final Object[][] params) throws SQLException {
        return executorService.submit(new Callable<int[]>() {

            @Override
            public int[] call() throws Exception {
                return queryRunner.batch(conn, sql, params);
            }

        });
    }

    /**
     * Execute a batch of SQL INSERT, UPDATE, or DELETE queries.  The
     * <code>Connection</code> is retrieved from the <code>DataSource</code>
     * set in the constructor.  This <code>Connection</code> must be in
     * auto-commit mode or the update will not be saved.
     *
     * @param sql The SQL to execute.
     * @param params An array of query replacement parameters.  Each row in
     * this array is one set of batch replacement values.
     * @return A <code>Future</code> which returns the number of rows updated per statement.
     * @throws SQLException if a database access error occurs
     */
    public Future<int[]> batch(final String sql, final Object[][] params) throws SQLException {
        return executorService.submit(new Callable<int[]>() {

            @Override
            public int[] call() throws Exception {
                return queryRunner.batch(sql, params);
            }

        });
    }

    /**
     * Execute an SQL SELECT query with replacement parameters.  The
     * caller is responsible for closing the connection.
     * @param <T> The type of object that the handler returns
     * @param conn The connection to execute the query in.
     * @param sql The query to execute.
     * @param rsh The handler that converts the results into an object.
     * @param params The replacement parameters.
     * @return A <code>Future</code> which returns the result of the query call.
     * @throws SQLException if a database access error occurs
     */
    public <T> Future<T> query(final Connection conn, final String sql, final ResultSetHandler<T> rsh, final Object... params)
        throws SQLException {
        return executorService.submit(new Callable<T>() {

            @Override
            public T call() throws Exception {
                return queryRunner.query(conn, sql, rsh, params);
            }

        });
    }

    /**
     * Execute an SQL SELECT query without any replacement parameters.  The
     * caller is responsible for closing the connection.
     * @param <T> The type of object that the handler returns
     * @param conn The connection to execute the query in.
     * @param sql The query to execute.
     * @param rsh The handler that converts the results into an object.
     * @return A <code>Future</code> which returns the result of the query call.
     * @throws SQLException if a database access error occurs
     */
    public <T> Future<T> query(final Connection conn, final String sql, final ResultSetHandler<T> rsh) throws SQLException {
        return executorService.submit(new Callable<T>() {

            @Override
            public T call() throws Exception {
                return queryRunner.query(conn, sql, rsh);
            }

        });
    }

    /**
     * Executes the given SELECT SQL query and returns a result object.
     * The <code>Connection</code> is retrieved from the
     * <code>DataSource</code> set in the constructor.
     * @param <T> The type of object that the handler returns
     * @param sql The SQL statement to execute.
     * @param rsh The handler used to create the result object from
     * the <code>ResultSet</code>.
     * @param params Initialize the PreparedStatement's IN parameters with
     * this array.
     * @return A <code>Future</code> which returns the result of the query call.
     * @throws SQLException if a database access error occurs
     */
    public <T> Future<T> query(final String sql, final ResultSetHandler<T> rsh, final Object... params) throws SQLException {
        return executorService.submit(new Callable<T>() {

            @Override
            public T call() throws Exception {
                return queryRunner.query(sql, rsh, params);
            }

        });
    }

    /**
     * Executes the given SELECT SQL without any replacement parameters.
     * The <code>Connection</code> is retrieved from the
     * <code>DataSource</code> set in the constructor.
     * @param <T> The type of object that the handler returns
     * @param sql The SQL statement to execute.
     * @param rsh The handler used to create the result object from
     * the <code>ResultSet</code>.
     *
     * @return A <code>Future</code> which returns the result of the query call.
     * @throws SQLException if a database access error occurs
     */
    public <T> Future<T> query(final String sql, final ResultSetHandler<T> rsh) throws SQLException {
        return executorService.submit(new Callable<T>() {

            @Override
            public T call() throws Exception {
                return queryRunner.query(sql, rsh);
            }

        });
    }

    /**
     * Execute an SQL INSERT, UPDATE, or DELETE query without replacement
     * parameters.
     *
     * @param conn The connection to use to run the query.
     * @param sql The SQL to execute.
     * @return A <code>Future</code> which returns the number of rows updated.
     * @throws SQLException if a database access error occurs
     */
    public Future<Integer> update(final Connection conn, final String sql) throws SQLException {
        return executorService.submit(new Callable<Integer>() {

            @Override
            public Integer call() throws Exception {
                return queryRunner.update(conn, sql);
            }

        });
    }

    /**
     * Execute an SQL INSERT, UPDATE, or DELETE query with a single replacement
     * parameter.
     *
     * @param conn The connection to use to run the query.
     * @param sql The SQL to execute.
     * @param param The replacement parameter.
     * @return A <code>Future</code> which returns the number of rows updated.
     * @throws SQLException if a database access error occurs
     */
    public Future<Integer> update(final Connection conn, final String sql, final Object param) throws SQLException {
        return executorService.submit(new Callable<Integer>() {

            @Override
            public Integer call() throws Exception {
                return queryRunner.update(conn, sql, param);
            }

        });
    }

    /**
     * Execute an SQL INSERT, UPDATE, or DELETE query.
     *
     * @param conn The connection to use to run the query.
     * @param sql The SQL to execute.
     * @param params The query replacement parameters.
     * @return A <code>Future</code> which returns the number of rows updated.
     * @throws SQLException if a database access error occurs
     */
    public Future<Integer> update(final Connection conn, final String sql, final Object... params) throws SQLException {
        return executorService.submit(new Callable<Integer>() {

            @Override
            public Integer call() throws Exception {
                return queryRunner.update(conn, sql, params);
            }

        });
    }

    /**
     * Executes the given INSERT, UPDATE, or DELETE SQL statement without
     * any replacement parameters. The <code>Connection</code> is retrieved
     * from the <code>DataSource</code> set in the constructor.  This
     * <code>Connection</code> must be in auto-commit mode or the update will
     * not be saved.
     *
     * @param sql The SQL statement to execute.
     * @throws SQLException if a database access error occurs
     * @return A <code>Future</code> which returns the number of rows updated.
     */
    public Future<Integer> update(final String sql) throws SQLException {
        return executorService.submit(new Callable<Integer>() {

            @Override
            public Integer call() throws Exception {
                return queryRunner.update(sql);
            }

        });
    }

    /**
     * Executes the given INSERT, UPDATE, or DELETE SQL statement with
     * a single replacement parameter.  The <code>Connection</code> is
     * retrieved from the <code>DataSource</code> set in the constructor.
     * This <code>Connection</code> must be in auto-commit mode or the
     * update will not be saved.
     *
     * @param sql The SQL statement to execute.
     * @param param The replacement parameter.
     * @throws SQLException if a database access error occurs
     * @return A <code>Future</code> which returns the number of rows updated.
     */
    public Future<Integer> update(final String sql, final Object param) throws SQLException {
        return executorService.submit(new Callable<Integer>() {

            @Override
            public Integer call() throws Exception {
                return queryRunner.update(sql, param);
            }

        });
    }

    /**
     * Executes the given INSERT, UPDATE, or DELETE SQL statement.  The
     * <code>Connection</code> is retrieved from the <code>DataSource</code>
     * set in the constructor.  This <code>Connection</code> must be in
     * auto-commit mode or the update will not be saved.
     *
     * @param sql The SQL statement to execute.
     * @param params Initializes the PreparedStatement's IN (i.e. '?')
     * parameters.
     * @throws SQLException if a database access error occurs
     * @return A <code>Future</code> which returns the number of rows updated.
     */
    public Future<Integer> update(final String sql, final Object... params) throws SQLException {
        return executorService.submit(new Callable<Integer>() {

            @Override
            public Integer call() throws Exception {
                return queryRunner.update(sql, params);
            }

        });
    }

    /**
     * Executes {@link QueryRunner#insert(String, ResultSetHandler)} asynchronously.
     *
     * @see QueryRunner#insert(String, ResultSetHandler)
     * @since 1.6
     */
    public <T> Future<T> insert(final String sql, final ResultSetHandler<T> rsh) throws SQLException {
        return executorService.submit(new Callable<T>() {

            @Override
            public T call() throws Exception {
                return queryRunner.insert(sql, rsh);
            }

        });
    }

    /**
     * Executes {@link QueryRunner#insert(String, ResultSetHandler, Object...)} asynchronously.
     *
     * @see QueryRunner#insert(String, ResultSetHandler, Object...)
     * @since 1.6
     */
    public <T> Future<T> insert(final String sql, final ResultSetHandler<T> rsh, final Object... params) throws SQLException {
        return executorService.submit(new Callable<T>() {

            @Override
            public T call() throws Exception {
                return queryRunner.insert(sql, rsh, params);
            }
        });
    }

    /**
     * Executes {@link QueryRunner#insert(Connection, String, ResultSetHandler)} asynchronously.
     *
     * @see QueryRunner#insert(Connection, String, ResultSetHandler)
     * @since 1.6
     */
    public <T> Future<T> insert(final Connection conn, final String sql, final ResultSetHandler<T> rsh) throws SQLException {
        return executorService.submit(new Callable<T>() {

            @Override
            public T call() throws Exception {
                return queryRunner.insert(conn, sql, rsh);
            }
        });
    }

    /**
     * Executes {@link QueryRunner#insert(Connection, String, ResultSetHandler, Object...)} asynchronously.
     *
     * @see QueryRunner#insert(Connection, String, ResultSetHandler, Object...)
     * @since 1.6
     */
    public <T> Future<T> insert(final Connection conn, final String sql, final ResultSetHandler<T> rsh, final Object... params) throws SQLException {
        return executorService.submit(new Callable<T>() {

            @Override
            public T call() throws Exception {
                return queryRunner.insert(conn, sql, rsh, params);
            }
        });
    }

}
