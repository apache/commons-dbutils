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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.sql.DataSource;

/**
 * Executes SQL queries with pluggable strategies for handling
 * <code>ResultSet</code>s.  This class is thread safe.
 *
 * @see ResultSetHandler
 * @since 1.4
 */
public class AsyncQueryRunner extends AbstractQueryRunner {

    private final ExecutorService executorService;

    /**
     * Constructor for AsyncQueryRunner.
     *
     * @param executorService the {@code ExecutorService} instance used to run JDBC invocations concurrently.
     */
    public AsyncQueryRunner(ExecutorService executorService) {
        this(null, false, executorService);
    }

    /**
     * Constructor for AsyncQueryRunner, allows workaround for Oracle drivers
     * @param pmdKnownBroken Oracle drivers don't support {@link java.sql.ParameterMetaData#getParameterType(int) };
     * if <code>pmdKnownBroken</code> is set to true, we won't even try it; if false, we'll try it,
     * and if it breaks, we'll remember not to use it again.
     * @param executorService the {@code ExecutorService} instance used to run JDBC invocations concurrently.
     */
    public AsyncQueryRunner(boolean pmdKnownBroken, ExecutorService executorService) {
        this(null, pmdKnownBroken, executorService);
    }

    /**
     * Constructor for AsyncQueryRunner which takes a <code>DataSource</code>.  Methods that do not take a
     * <code>Connection</code> parameter will retrieve connections from this
     * <code>DataSource</code>.
     *
     * @param ds The <code>DataSource</code> to retrieve connections from.
     * @param executorService the {@code ExecutorService} instance used to run JDBC invocations concurrently.
     */
    public AsyncQueryRunner(DataSource ds, ExecutorService executorService) {
        this(ds, false, executorService);
    }

    /**
     * Constructor for QueryRunner, allows workaround for Oracle drivers.  Methods that do not take a
     * <code>Connection</code> parameter will retrieve connections from this
     * <code>DataSource</code>.
     *
     * @param ds The <code>DataSource</code> to retrieve connections from.
     * @param pmdKnownBroken Oracle drivers don't support {@link java.sql.ParameterMetaData#getParameterType(int) };
     * if <code>pmdKnownBroken</code> is set to true, we won't even try it; if false, we'll try it,
     * and if it breaks, we'll remember not to use it again.
     * @param executorService the {@code ExecutorService} instance used to run JDBC invocations concurrently.
     */
    public AsyncQueryRunner(DataSource ds, boolean pmdKnownBroken, ExecutorService executorService) {
        super(ds, pmdKnownBroken);
        this.executorService = executorService;
    }

    /**
     * Class that encapsulates the continuation for batch calls.
     */
    protected class BatchCallableStatement implements Callable<int[]> {
        private String sql;
        private Object[][] params;
        private Connection conn;
        private boolean closeConn;
        private PreparedStatement ps;

        /**
         * Creates a new BatchCallableStatement instance.
         *
         * @param sql The SQL statement to execute.
         * @param params An array of query replacement parameters.  Each row in
         *        this array is one set of batch replacement values.
         * @param conn The connection to use for the batch call.
         * @param closeConn True if the connection should be closed, false otherwise.
         * @param ps The {@link PreparedStatement} to be executed.
         */
        public BatchCallableStatement(String sql, Object[][] params, Connection conn, boolean closeConn, PreparedStatement ps) {
            this.sql = sql;
            this.params = params;
            this.conn = conn;
            this.closeConn = closeConn;
            this.ps = ps;
        }

        /**
         * The actual call to executeBatch.
         *
         * @return an array of update counts containing one element for each command in the batch.
         * @throws SQLException if a database access error occurs or one of the commands sent to the database fails.
         * @see PreparedStatement#executeBatch()
         */
        public int[] call() throws SQLException {
            int[] ret = null;

            try {
                ret = ps.executeBatch();
            } catch (SQLException e) {
                rethrow(e, sql, (Object[])params);
            } finally {
                close(ps);
                if (closeConn) {
                    close(conn);
                }
            }

            return ret;
        }
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
    public Future<int[]> batch(Connection conn, String sql, Object[][] params) throws SQLException {
        return executorService.submit(this.batch(conn, false, sql, params));
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
    public Future<int[]> batch(String sql, Object[][] params) throws SQLException {
        Connection conn = this.prepareConnection();

        return executorService.submit(this.batch(conn, true, sql, params));
    }

    /**
     * Creates a continuation for a batch call, and returns it in a <code>Callable</code>.
     * @param conn The connection to use for the batch call.
     * @param closeConn True if the connection should be closed, false otherwise.
     * @param sql The SQL statement to execute.
     * @param params An array of query replacement parameters.  Each row in
     * this array is one set of batch replacement values.
     * @return A <code>Callable</code> which returns the number of rows updated per statement.
     * @throws SQLException If there are database or parameter errors.
     */
    private Callable<int[]> batch(Connection conn, boolean closeConn, String sql, Object[][] params) throws SQLException {
        if (conn == null) {
            throw new SQLException("Null connection");
        }

        if (sql == null) {
            if (closeConn) {
                close(conn);
            }
            throw new SQLException("Null SQL statement");
        }

        if (params == null) {
            if (closeConn) {
                close(conn);
            }
            throw new SQLException("Null parameters. If parameters aren't need, pass an empty array.");
        }

        PreparedStatement stmt = null;
        Callable<int[]> ret = null;
        try {
            stmt = this.prepareStatement(conn, sql);

            for (int i = 0; i < params.length; i++) {
                this.fillStatement(stmt, params[i]);
                stmt.addBatch();
            }

            ret = new BatchCallableStatement(sql, params, conn, closeConn, stmt);

        } catch (SQLException e) {
            close(stmt);
            close(conn);
            this.rethrow(e, sql, (Object[])params);
        }

        return ret;
    }

    /**
     * Class that encapsulates the continuation for query calls.
     * @param <T> The type of the result from the call to handle.
     */
    protected class QueryCallableStatement<T> implements Callable<T> {
        private String sql;
        private Object[] params;
        private Connection conn;
        private boolean closeConn;
        private PreparedStatement ps;
        private ResultSetHandler<T> rsh;

        /**
         * Creates a new {@code QueryCallableStatement} instance.
         *
         * @param conn The connection to use for the batch call.
         * @param closeConn True if the connection should be closed, false otherwise.
         * @param ps The {@link PreparedStatement} to be executed.
         * @param rsh The handler that converts the results into an object.
         * @param sql The SQL statement to execute.
         * @param params An array of query replacement parameters.  Each row in
         *        this array is one set of batch replacement values.
         */
        public QueryCallableStatement(Connection conn, boolean closeConn, PreparedStatement ps,
                ResultSetHandler<T> rsh, String sql, Object... params) {
            this.sql = sql;
            this.params = params;
            this.conn = conn;
            this.closeConn = closeConn;
            this.ps = ps;
            this.rsh = rsh;
        }

        /**
         * The actual call to {@code handle()} method.
         *
         * @return an array of update counts containing one element for each command in the batch.
         * @throws SQLException if a database access error occurs.
         * @see ResultSetHandler#handle(ResultSet)
         */
        public T call() throws SQLException {
            ResultSet rs = null;
            T ret = null;

            try {
                rs = wrap(ps.executeQuery());
                ret = rsh.handle(rs);
            } catch (SQLException e) {
                rethrow(e, sql, params);
            } finally {
                try {
                    close(rs);
                } finally {
                    close(ps);
                    if (closeConn) {
                        close(conn);
                    }
                }
            }

            return ret;
        }

    }

    /**
     * Creates a continuation for a query call, and returns it in a <code>Callable</code>.
     * @param conn The connection to use for the query call.
     * @param closeConn True if the connection should be closed, false otherwise.
     * @param sql The SQL statement to execute.
     * @param params An array of query replacement parameters.  Each row in
     * this array is one set of query replacement values.
     * @return A <code>Callable</code> which returns the result of the query call.
     * @throws SQLException If there are database or parameter errors.
     */
    private <T> Callable<T> query(Connection conn, boolean closeConn, String sql, ResultSetHandler<T> rsh, Object... params)
            throws SQLException {
        PreparedStatement stmt = null;
        Callable<T> ret = null;

        if (conn == null) {
            throw new SQLException("Null connection");
        }

        if (sql == null) {
            if (closeConn) {
                close(conn);
            }
            throw new SQLException("Null SQL statement");
        }

        if (rsh == null) {
            if (closeConn) {
                close(conn);
            }
            throw new SQLException("Null ResultSetHandler");
        }

        try {
            stmt = this.prepareStatement(conn, sql);
            this.fillStatement(stmt, params);

            ret = new QueryCallableStatement<T>(conn, closeConn, stmt, rsh, sql, params);

        } catch (SQLException e) {
            close(stmt);
            if (closeConn) {
                close(conn);
            }
            this.rethrow(e, sql, params);
        }

        return ret;
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
    public <T> Future<T> query(Connection conn, String sql, ResultSetHandler<T> rsh, Object... params) throws SQLException {
        return executorService.submit(query(conn, false, sql, rsh, params));
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
    public <T> Future<T> query(Connection conn, String sql, ResultSetHandler<T> rsh) throws SQLException {
        return executorService.submit(this.query(conn, false, sql, rsh, (Object[]) null));
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
    public <T> Future<T> query(String sql, ResultSetHandler<T> rsh, Object... params) throws SQLException {
        Connection conn = this.prepareConnection();
        return executorService.submit(this.query(conn, true, sql, rsh, params));
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
    public <T> Future<T> query(String sql, ResultSetHandler<T> rsh) throws SQLException {
        Connection conn = this.prepareConnection();
        return executorService.submit(this.query(conn, true, sql, rsh, (Object[]) null));
    }

    /**
     * Class that encapsulates the continuation for update calls.
     */
    protected class UpdateCallableStatement implements Callable<Integer> {
        private String sql;
        private Object[] params;
        private Connection conn;
        private boolean closeConn;
        private PreparedStatement ps;

        /**
         *
         *
         * @param conn The connection to use for the batch call.
         * @param closeConn True if the connection should be closed, false otherwise.
         * @param ps The {@link PreparedStatement} to be executed.
         * @param sql The SQL statement to execute.
         * @param params An array of query replacement parameters.  Each row in
         *        this array is one set of batch replacement values.
         */
        public UpdateCallableStatement(Connection conn, boolean closeConn, PreparedStatement ps, String sql, Object... params) {
            this.sql = sql;
            this.params = params;
            this.conn = conn;
            this.closeConn = closeConn;
            this.ps = ps;
        }

        /**
         * The actual call to {@code executeUpdate()} method.
         *
         * @return either (1) the row count for SQL Data Manipulation Language (DML) statements or
         *                (2) 0 for SQL statements that return nothing
         * @throws SQLException if a database access error occurs.
         * @see PreparedStatement#executeUpdate()
         */
        public Integer call() throws SQLException {
            int rows = 0;

            try {
                rows = ps.executeUpdate();
            } catch (SQLException e) {
                rethrow(e, sql, params);
            } finally {
                close(ps);
                if (closeConn) {
                    close(conn);
                }
            }

            return Integer.valueOf(rows);
        }

    }

    /**
     * Creates a continuation for an update call, and returns it in a <code>Callable</code>.
     * @param conn The connection to use for the update call.
     * @param closeConn True if the connection should be closed, false otherwise.
     * @param sql The SQL statement to execute.
     * @param params An array of update replacement parameters.  Each row in
     * this array is one set of update replacement values.
     * @return A <code>Callable</code> which returns the number of rows updated.
     * @throws SQLException If there are database or parameter errors.
     */
    private Callable<Integer> update(Connection conn, boolean closeConn, String sql, Object... params) throws SQLException {
        PreparedStatement stmt = null;
        Callable<Integer> ret = null;

        if (conn == null) {
            throw new SQLException("Null connection");
        }

        if (sql == null) {
            if (closeConn) {
                close(conn);
            }
            throw new SQLException("Null SQL statement");
        }

        try {
            stmt = this.prepareStatement(conn, sql);
            this.fillStatement(stmt, params);

            ret = new UpdateCallableStatement(conn, closeConn, stmt, sql, params);

        } catch (SQLException e) {
            close(stmt);
            if (closeConn) {
                close(conn);
            }
            this.rethrow(e, sql, params);
        }

        return ret;
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
    public Future<Integer> update(Connection conn, String sql) throws SQLException {
        return executorService.submit(this.update(conn, false, sql, (Object[]) null));
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
    public Future<Integer> update(Connection conn, String sql, Object param) throws SQLException {
        return executorService.submit(this.update(conn, false, sql, new Object[]{param}));
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
    public Future<Integer> update(Connection conn, String sql, Object... params) throws SQLException {
        return executorService.submit(this.update(conn, false, sql, params));
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
    public Future<Integer> update(String sql) throws SQLException {
        Connection conn = this.prepareConnection();
        return executorService.submit(this.update(conn, true, sql, (Object[]) null));
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
    public Future<Integer> update(String sql, Object param) throws SQLException {
        Connection conn = this.prepareConnection();
        return executorService.submit(this.update(conn, true, sql, new Object[]{param}));
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
    public Future<Integer> update(String sql, Object... params) throws SQLException {
        Connection conn = this.prepareConnection();
        return executorService.submit(this.update(conn, true, sql, params));
    }

}
