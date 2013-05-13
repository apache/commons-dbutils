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
package org.apache.commons.dbutils2;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

/**
 * Executes SQL queries with pluggable strategies for handling
 * <code>ResultSet</code>s.  This class is thread safe.
 *
 * @see ResultSetHandler
 */
public class QueryRunner {
    /**
     * The DataSource to retrieve connections from.
     */
    private final DataSource ds;

    /**
     * Constructor for QueryRunner.
     */
    public QueryRunner() {
        ds = null;
    }

    /**
     * Constructor for QueryRunner that takes a <code>DataSource</code> to use.
     *
     * Methods that do not take a <code>Connection</code> parameter will retrieve connections from this
     * <code>DataSource</code>.
     *
     * @param ds The <code>DataSource</code> to retrieve connections from.
     */
    public QueryRunner(final DataSource ds) {
        this.ds = ds;
    }

    /**
     * Returns the <code>DataSource</code> this runner is using.
     * <code>QueryRunner</code> methods always call this method to get the
     * <code>DataSource</code> so subclasses can provide specialized behavior.
     *
     * @return DataSource the runner is using
     */
    public DataSource getDataSource() {
        return this.ds;
    }

    /**
     * Factory method that creates and initializes a <code>Connection</code>
     * object. <code>QueryRunner</code> methods always call this method to
     * retrieve connections from its DataSource. Subclasses can override this
     * method to provide special <code>Connection</code> configuration if
     * needed. This implementation simply calls <code>ds.getConnection()</code>.
     *
     * @return An initialized <code>Connection</code>.
     * @throws SQLException if a database access error occurs
     */
    protected Connection prepareConnection() throws SQLException {
        if (this.getDataSource() == null) {
            throw new SQLException(
                    "QueryRunner requires a DataSource to be "
                            + "invoked in this way, or a Connection should be passed in");
        }
        return this.getDataSource().getConnection();
    }

    /**
     * Close a <code>Connection</code>. This implementation avoids closing if
     * null and does <strong>not</strong> suppress any exceptions. Subclasses
     * can override to provide special handling like logging.
     *
     * @param conn Connection to close
     * @throws SQLException if a database access error occurs
     */
    private void close(Connection conn) throws SQLException {
        DbUtils.close(conn);
    }

    /**
     * Creates an {@link BatchExecutor} for the given SQL.
     * <code>Connection</code> is retrieved from the <code>DataSource</code>
     * set in the constructor.  This <code>Connection</code> must be in
     * auto-commit mode or the insert will not be saved. The <code>Connection</code> is
     * closed after the call.
     *
     * @param sql The SQL statement to execute.
     *
     * @return An {@link BatchExecutor} for this SQL statement.
     * @throws SQLException If there are database or parameter errors.
     */
    public BatchExecutor batch(String sql) throws SQLException {
        return this.batch(this.prepareConnection(), true, sql);
    }

    /**
     * Creates an {@link BatchExecutor} for the given SQL statement and connection.
     * The connection is <b>NOT</b> closed after execution.
     *
     * @param conn The connection to use for the batch call.
     * @param sql The SQL statement to execute.
     *
     * @return An {@link BatchExecutor} for this SQL statement.
     * @throws SQLException If there are database or parameter errors.
     */
    public BatchExecutor batch(Connection conn, String sql) throws SQLException {
        return this.batch(conn, true, sql);
    }

    /**
     * Creates an {@link BatchExecutor} for the given SQL statement and connection.
     *
     * @param conn The connection to use for the batch call.
     * @param closeConn True if the connection should be closed, false otherwise.
     * @param sql The SQL statement to execute.
     *
     * @return An {@link BatchExecutor} for this SQL statement.
     * @throws SQLException If there are database or parameter errors.
     */
    public BatchExecutor batch(Connection conn, boolean closeConn, String sql) throws SQLException {
        if (conn == null) {
            throw new SQLException("Null connection");
        }

        if (sql == null) {
            if (closeConn) {
                close(conn);
            }
            throw new SQLException("Null SQL statement");
        }

        return new BatchExecutor(conn, sql, closeConn);
    }

    /**
     * Creates an {@link QueryExecutor} for the given SQL.
     * <code>Connection</code> is retrieved from the <code>DataSource</code>
     * set in the constructor.  This <code>Connection</code> must be in
     * auto-commit mode or the insert will not be saved. The <code>Connection</code> is
     * closed after the call.
     *
     * @param sql The SQL statement to execute.
     *
     * @return A {@link QueryExecutor} for this SQL statement.
     * @throws SQLException If there are database or parameter errors.
     */
    public QueryExecutor query(String sql) throws SQLException {
        return this.query(this.prepareConnection(), true, sql);
    }

    /**
     * Creates an {@link QueryExecutor} for the given SQL statement and connection.
     * The connection is <b>NOT</b> closed after execution.
     *
     * @param conn The connection to use for the update call.
     * @param sql The SQL statement to execute.
     *
     * @return An {@link QueryExecutor} for this SQL statement.
     * @throws SQLException If there are database or parameter errors.
     */
    public QueryExecutor query(Connection conn, String sql) throws SQLException {
        return this.query(conn, false, sql);
    }

    /**
     * Creates an {@link QueryExecutor} for the given SQL statement and connection.
     *
     * @param conn The connection to use for the query call.
     * @param closeConn True if the connection should be closed, false otherwise.
     * @param sql The SQL statement to execute.
     *
     * @return An {@link QueryExecutor} for this SQL statement.
     * @throws SQLException If there are database or parameter errors.
     */
    public QueryExecutor query(Connection conn, boolean closeConn, String sql) throws SQLException {
        if (conn == null) {
            throw new SQLException("Null connection");
        }

        if (sql == null) {
            if (closeConn) {
                close(conn);
            }
            throw new SQLException("Null SQL statement");
        }

        return new QueryExecutor(conn, sql, closeConn);
    }

    /**
     * Creates an {@link UpdateExecutor} for the given SQL.
     * <code>Connection</code> is retrieved from the <code>DataSource</code>
     * set in the constructor.  This <code>Connection</code> must be in
     * auto-commit mode or the insert will not be saved. The <code>Connection</code> is
     * closed after the call.
     *
     * @param sql The SQL statement to execute.
     *
     * @return An {@link UpdateExecutor} for this SQL statement.
     * @throws SQLException if a database access error occurs
     */
    public UpdateExecutor update(String sql) throws SQLException {
        return this.update(this.prepareConnection(), true, sql);
    }

    /**
     * Creates an {@link UpdateExecutor} for the given SQL statement and connection.
     * The connection is <b>NOT</b> closed after execution.
     *
     * @param conn The connection to use for the update call.
     * @param sql The SQL statement to execute.
     *
     * @return An {@link UpdateExecutor} for this SQL statement.
     * @throws SQLException If there are database or parameter errors.
     */
    public UpdateExecutor update(Connection conn, String sql) throws SQLException {
        return this.update(conn, false, sql);
    }

    /**
     * Creates an {@link UpdateExecutor} for the given SQL statement and connection.
     *
     * @param conn The connection to use for the update call.
     * @param closeConn True if the connection should be closed, false otherwise.
     * @param sql The SQL statement to execute.
     *
     * @return An {@link UpdateExecutor} for this SQL statement.
     * @throws SQLException If there are database or parameter errors.
     */
    public UpdateExecutor update(Connection conn, boolean closeConn, String sql) throws SQLException {
        if (conn == null) {
            throw new SQLException("Null connection");
        }

        if (sql == null) {
            if (closeConn) {
                close(conn);
            }
            throw new SQLException("Null SQL statement");
        }

        return new UpdateExecutor(conn, sql, closeConn);
    }

    /**
     * Creates an {@link InsertExecutor} for the given SQL.
     * <code>Connection</code> is retrieved from the <code>DataSource</code>
     * set in the constructor.  This <code>Connection</code> must be in
     * auto-commit mode or the insert will not be saved. The <code>Connection</code> is
     * closed after the call.
     *
     * @param sql The SQL statement to execute.
     *
     * @return An {@link InsertExecutor} for this SQL statement.
     * @throws SQLException If there are database or parameter errors.
     */
    public InsertExecutor insert(String sql) throws SQLException {
        return insert(this.prepareConnection(), true, sql);
    }

    /**
     * Creates an {@link InsertExecutor} for the given SQL and connection
     * The connection is <b>NOT</b> closed after execution.
     *
     * @param conn The connection to use for the query call.
     * @param sql The SQL statement to execute.
     *
     * @return An {@link InsertExecutor} for this SQL statement.
     * @throws SQLException If there are database or parameter errors.
     */
    public InsertExecutor insert(Connection conn, String sql) throws SQLException {
        return insert(conn, false, sql);
    }

    /**
     * Creates an {@link InsertExecutor} for the given SQL and connection.
     *
     * @param conn The connection to use for the insert call.
     * @param closeConn True if the connection should be closed, false otherwise.
     * @param sql The SQL statement to execute.
     *
     * @return An {@link InsertExecutor} for this SQL statement.
     * @throws SQLException If there are database or parameter errors.
     */
    public InsertExecutor insert(Connection conn, boolean closeConn, String sql) throws SQLException {
        if (conn == null) {
            throw new SQLException("Null connection");
        }

        if (sql == null) {
            if (closeConn) {
                close(conn);
            }
            throw new SQLException("Null SQL statement");
        }

        return new InsertExecutor(conn, sql, closeConn);
    }
}
