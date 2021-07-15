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

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Arrays;

import javax.sql.DataSource;

/**
 * The base class for QueryRunner &amp; AsyncQueryRunner. This class is thread safe.
 *
 * @since 1.4 (mostly extracted from QueryRunner)
 */
public abstract class AbstractQueryRunner {
    /**
     * Is {@link ParameterMetaData#getParameterType(int)} broken (have we tried
     * it yet)?
     */
    private volatile boolean pmdKnownBroken = false;

    /**
     * The DataSource to retrieve connections from.
     * @deprecated Access to this field should be through {@link #getDataSource()}.
     */
    @Deprecated
    protected final DataSource ds;

    /**
     * Configuration to use when preparing statements.
     */
    private final StatementConfiguration stmtConfig;

    /**
     * Default constructor, sets pmdKnownBroken to false, ds to null and stmtConfig to null.
     */
    public AbstractQueryRunner() {
        ds = null;
        this.stmtConfig = null;
    }

    /**
     * Constructor to control the use of {@code ParameterMetaData}.
     *
     * @param pmdKnownBroken
     *            Some drivers don't support
     *            {@link ParameterMetaData#getParameterType(int) }; if
     *            {@code pmdKnownBroken} is set to true, we won't even try
     *            it; if false, we'll try it, and if it breaks, we'll remember
     *            not to use it again.
     */
    public AbstractQueryRunner(final boolean pmdKnownBroken) {
        this.pmdKnownBroken = pmdKnownBroken;
        ds = null;
        this.stmtConfig = null;
    }

    /**
     * Constructor to provide a {@code DataSource}. Methods that do not
     * take a {@code Connection} parameter will retrieve connections from
     * this {@code DataSource}.
     *
     * @param ds
     *            The {@code DataSource} to retrieve connections from.
     */
    public AbstractQueryRunner(final DataSource ds) {
        this.ds = ds;
        this.stmtConfig = null;
    }

    /**
     * Constructor to provide a {@code DataSource} and control the use of
     * {@code ParameterMetaData}. Methods that do not take a
     * {@code Connection} parameter will retrieve connections from this
     * {@code DataSource}.
     *
     * @param ds
     *            The {@code DataSource} to retrieve connections from.
     * @param pmdKnownBroken
     *            Some drivers don't support
     *            {@link ParameterMetaData#getParameterType(int) }; if
     *            {@code pmdKnownBroken} is set to true, we won't even try
     *            it; if false, we'll try it, and if it breaks, we'll remember
     *            not to use it again.
     */
    public AbstractQueryRunner(final DataSource ds, final boolean pmdKnownBroken) {
        this.pmdKnownBroken = pmdKnownBroken;
        this.ds = ds;
        this.stmtConfig = null;
    }

    /**
     * Constructor for QueryRunner that takes a {@code DataSource}, a {@code StatementConfiguration}, and
     * controls the use of {@code ParameterMetaData}.  Methods that do not take a {@code Connection} parameter
     * will retrieve connections from this {@code DataSource}.
     *
     * @param ds The {@code DataSource} to retrieve connections from.
     * @param pmdKnownBroken Some drivers don't support {@link java.sql.ParameterMetaData#getParameterType(int) };
     * if {@code pmdKnownBroken} is set to true, we won't even try it; if false, we'll try it,
     * and if it breaks, we'll remember not to use it again.
     * @param stmtConfig The configuration to apply to statements when they are prepared.
     */
    public AbstractQueryRunner(final DataSource ds, final boolean pmdKnownBroken, final StatementConfiguration stmtConfig) {
        this.pmdKnownBroken = pmdKnownBroken;
        this.ds = ds;
        this.stmtConfig = stmtConfig;
    }

    /**
     * Constructor for QueryRunner that takes a {@code DataSource} to use and a {@code StatementConfiguration}.
     *
     * Methods that do not take a {@code Connection} parameter will retrieve connections from this
     * {@code DataSource}.
     *
     * @param ds The {@code DataSource} to retrieve connections from.
     * @param stmtConfig The configuration to apply to statements when they are prepared.
     */
    public AbstractQueryRunner(final DataSource ds, final StatementConfiguration stmtConfig) {
        this.ds = ds;
        this.stmtConfig = stmtConfig;
    }

    /**
     * Constructor for QueryRunner that takes a {@code StatementConfiguration} to configure statements when
     * preparing them.
     *
     * @param stmtConfig The configuration to apply to statements when they are prepared.
     */
    public AbstractQueryRunner(final StatementConfiguration stmtConfig) {
        this.ds = null;
        this.stmtConfig = stmtConfig;
    }

    /**
     * Close a {@code Connection}. This implementation avoids closing if
     * null and does <strong>not</strong> suppress any exceptions. Subclasses
     * can override to provide special handling like logging.
     *
     * @param conn
     *            Connection to close
     * @throws SQLException
     *             if a database access error occurs
     * @since DbUtils 1.1
     */
    protected void close(final Connection conn) throws SQLException {
        DbUtils.close(conn);
    }

    /**
     * Close a {@code ResultSet}. This implementation avoids closing if
     * null and does <strong>not</strong> suppress any exceptions. Subclasses
     * can override to provide special handling like logging.
     *
     * @param rs
     *            ResultSet to close
     * @throws SQLException
     *             if a database access error occurs
     * @since DbUtils 1.1
     */
    protected void close(final ResultSet rs) throws SQLException {
        DbUtils.close(rs);
    }

    /**
     * Close a {@code Statement}. This implementation avoids closing if
     * null and does <strong>not</strong> suppress any exceptions. Subclasses
     * can override to provide special handling like logging.
     *
     * @param stmt
     *            Statement to close
     * @throws SQLException
     *             if a database access error occurs
     * @since DbUtils 1.1
     */
    protected void close(final Statement stmt) throws SQLException {
        DbUtils.close(stmt);
    }

    /**
     * Calls {@link DbUtils#closeQuietly(Connection)}.
     *
     * @param conn Connection to close.
     * @since 2.0
     */
    protected void closeQuietly(final Connection conn) {
        DbUtils.closeQuietly(conn);
    }

    /**
     * Calls {@link DbUtils#closeQuietly(ResultSet)}.
     *
     * @param rs ResultSet to close.
     * @since 2.0
     */
    protected void closeQuietly(final ResultSet rs) {
        DbUtils.closeQuietly(rs);
    }

    /**
     * Calls {@link DbUtils#closeQuietly(Statement)}.
     *
     * @param statement ResultSet to close.
     * @since 2.0
     */
    protected void closeQuietly(final Statement statement) {
        DbUtils.closeQuietly(statement);
    }

    private void configureStatement(final Statement stmt) throws SQLException {

        if (stmtConfig != null) {
            if (stmtConfig.isFetchDirectionSet()) {
                stmt.setFetchDirection(stmtConfig.getFetchDirection());
            }

            if (stmtConfig.isFetchSizeSet()) {
                stmt.setFetchSize(stmtConfig.getFetchSize());
            }

            if (stmtConfig.isMaxFieldSizeSet()) {
                stmt.setMaxFieldSize(stmtConfig.getMaxFieldSize());
            }

            if (stmtConfig.isMaxRowsSet()) {
                stmt.setMaxRows(stmtConfig.getMaxRows());
            }

            if (stmtConfig.isQueryTimeoutSet()) {
                stmt.setQueryTimeout(stmtConfig.getQueryTimeout());
            }
        }
    }

    /**
     * Get the {@code ParameterMetaData} of the prepared statement, if the {@code pmdKnownBroken}
     * is set to false.
     *
     * @param stmt
     *            PreparedStatement of which to query the metadata of parameters
     * @return the metadata of parameters
     * @throws SQLException
     *            if a database access error occurs
     */
    public ParameterMetaData getParameterMetaData(final PreparedStatement stmt) throws SQLException {
        ParameterMetaData pmd = null;
        if (!pmdKnownBroken) {
            try {
                pmd = stmt.getParameterMetaData();
            } catch (final SQLFeatureNotSupportedException ex) {
                pmdKnownBroken = true;
            }
        }
        return pmd;
    }

    /**
     * Fill the {@code PreparedStatement} replacement parameters with the
     * given objects.
     *
     * @param stmt
     *            PreparedStatement to fill
     * @param params
     *            Query replacement parameters; {@code null} is a valid
     *            value to pass in.
     * @throws SQLException
     *             if a database access error occurs
     */
    public void fillStatement(final PreparedStatement stmt, final Object... params) throws SQLException {
        ParameterMetaData pmd = null;
        if (!pmdKnownBroken) {
            pmd = this.getParameterMetaData(stmt);
        }
        fillStatement(stmt, pmd, params);
    }

    /**
     * Fill the {@code PreparedStatement} replacement parameters with the
     * given objects, and prefetched parameter metadata.
     *
     * @param stmt
     *            PreparedStatement to fill
     * @param pmd
     *            Prefetched parameter metadata
     * @param params
     *            Query replacement parameters; {@code null} is a valid
     *            value to pass in.
     * @throws SQLException
     *             if a database access error occurs
     */
    public void fillStatement(final PreparedStatement stmt, final ParameterMetaData pmd, final Object... params)
            throws SQLException {

        // check the parameter count, if we can
        if (!pmdKnownBroken) {
            try {
                if (pmd == null) { // can be returned by implementations that don't support the method
                    pmdKnownBroken = true;
                } else {
                    final int stmtCount = pmd.getParameterCount();
                    final int paramsCount = params == null ? 0 : params.length;

                    if (stmtCount != paramsCount) {
                        throw new SQLException("Wrong number of parameters: expected "
                                + stmtCount + ", was given " + paramsCount);
                    }
                }
            } catch (final SQLFeatureNotSupportedException ex) {
                pmdKnownBroken = true;
            }
            // TODO see DBUTILS-117: would it make sense to catch any other SQLEx types here?
        }

        // nothing to do here
        if (params == null) {
            return;
        }

        CallableStatement call = null;
        if (stmt instanceof CallableStatement) {
            call = (CallableStatement) stmt;
        }

        for (int i = 0; i < params.length; i++) {
            if (params[i] != null) {
                if (call != null && params[i] instanceof OutParameter) {
                    ((OutParameter)params[i]).register(call, i + 1);
                } else {
                    stmt.setObject(i + 1, params[i]);
                }
            } else {
                // VARCHAR works with many drivers regardless
                // of the actual column type. Oddly, NULL and
                // OTHER don't work with Oracle's drivers.
                int sqlType = Types.VARCHAR;
                if (!pmdKnownBroken) {
                    // TODO see DBUTILS-117: does it make sense to catch SQLEx here?
                    try {
                        /*
                         * It's not possible for pmdKnownBroken to change from
                         * true to false, (once true, always true) so pmd cannot
                         * be null here.
                         */
                        sqlType = pmd.getParameterType(i + 1);
                    } catch (final SQLException e) {
                        pmdKnownBroken = true;
                    }
                }
                stmt.setNull(i + 1, sqlType);
            }
        }
    }

    /**
     * Fill the {@code PreparedStatement} replacement parameters with the
     * given object's bean property values.
     *
     * @param stmt
     *            PreparedStatement to fill
     * @param bean
     *            a JavaBean object
     * @param properties
     *            an ordered array of properties; this gives the order to insert
     *            values in the statement
     * @throws SQLException
     *             if a database access error occurs
     */
    public void fillStatementWithBean(final PreparedStatement stmt, final Object bean,
            final PropertyDescriptor[] properties) throws SQLException {
        final Object[] params = new Object[properties.length];
        for (int i = 0; i < properties.length; i++) {
            final PropertyDescriptor property = properties[i];
            Object value = null;
            final Method method = property.getReadMethod();
            if (method == null) {
                throw new RuntimeException("No read method for bean property "
                        + bean.getClass() + " " + property.getName());
            }
            try {
                value = method.invoke(bean);
            } catch (final IllegalArgumentException e) {
                throw new RuntimeException(
                        "Couldn't invoke method with 0 arguments: " + method, e);
            } catch (final InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException("Couldn't invoke method: " + method,
                        e);
            }
            params[i] = value;
        }
        fillStatement(stmt, params);
    }

    /**
     * Fill the {@code PreparedStatement} replacement parameters with the
     * given object's bean property values.
     *
     * @param stmt
     *            PreparedStatement to fill
     * @param bean
     *            A JavaBean object
     * @param propertyNames
     *            An ordered array of property names (these should match the
     *            getters/setters); this gives the order to insert values in the
     *            statement
     * @throws SQLException
     *             If a database access error occurs
     */
    public void fillStatementWithBean(final PreparedStatement stmt, final Object bean,
            final String... propertyNames) throws SQLException {
        PropertyDescriptor[] descriptors;
        try {
            descriptors = Introspector.getBeanInfo(bean.getClass())
                    .getPropertyDescriptors();
        } catch (final IntrospectionException e) {
            throw new RuntimeException("Couldn't introspect bean "
                    + bean.getClass().toString(), e);
        }
        final PropertyDescriptor[] sorted = new PropertyDescriptor[propertyNames.length];
        for (int i = 0; i < propertyNames.length; i++) {
            final String propertyName = propertyNames[i];
            if (propertyName == null) {
                throw new NullPointerException("propertyName can't be null: "
                        + i);
            }
            boolean found = false;
            for (final PropertyDescriptor descriptor : descriptors) {
                if (propertyName.equals(descriptor.getName())) {
                    sorted[i] = descriptor;
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new RuntimeException("Couldn't find bean property: "
                        + bean.getClass() + " " + propertyName);
            }
        }
        fillStatementWithBean(stmt, bean, sorted);
    }

    /**
     * Returns the {@code DataSource} this runner is using.
     * {@code QueryRunner} methods always call this method to get the
     * {@code DataSource} so subclasses can provide specialized behavior.
     *
     * @return DataSource the runner is using
     */
    public DataSource getDataSource() {
        return this.ds;
    }

    /**
     * Some drivers don't support
     * {@link ParameterMetaData#getParameterType(int) }; if
     * {@code pmdKnownBroken} is set to true, we won't even try it; if
     * false, we'll try it, and if it breaks, we'll remember not to use it
     * again.
     *
     * @return the flag to skip (or not)
     *         {@link ParameterMetaData#getParameterType(int) }
     * @since 1.4
     */
    public boolean isPmdKnownBroken() {
        return pmdKnownBroken;
    }

    /**
     * Factory method that creates and initializes a
     * {@code CallableStatement} object for the given SQL.
     * {@code QueryRunner} methods always call this method to prepare
     * callable statements for them. Subclasses can override this method to
     * provide special CallableStatement configuration if needed. This
     * implementation simply calls {@code conn.prepareCall(sql)}.
     *
     * @param conn
     *            The {@code Connection} used to create the
     *            {@code CallableStatement}
     * @param sql
     *            The SQL statement to prepare.
     * @return An initialized {@code CallableStatement}.
     * @throws SQLException
     *             if a database access error occurs
     */
    protected CallableStatement prepareCall(final Connection conn, final String sql)
            throws SQLException {

        return conn.prepareCall(sql);
    }

    /**
     * Factory method that creates and initializes a {@code Connection}
     * object. {@code QueryRunner} methods always call this method to
     * retrieve connections from its DataSource. Subclasses can override this
     * method to provide special {@code Connection} configuration if
     * needed. This implementation simply calls {@code ds.getConnection()}.
     *
     * @return An initialized {@code Connection}.
     * @throws SQLException
     *             if a database access error occurs
     * @since DbUtils 1.1
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
     * Factory method that creates and initializes a
     * {@code PreparedStatement} object for the given SQL.
     * {@code QueryRunner} methods always call this method to prepare
     * statements for them. Subclasses can override this method to provide
     * special PreparedStatement configuration if needed. This implementation
     * simply calls {@code conn.prepareStatement(sql)}.
     *
     * @param conn
     *            The {@code Connection} used to create the
     *            {@code PreparedStatement}
     * @param sql
     *            The SQL statement to prepare.
     * @return An initialized {@code PreparedStatement}.
     * @throws SQLException
     *             if a database access error occurs
     */
    protected PreparedStatement prepareStatement(final Connection conn, final String sql)
            throws SQLException {

        @SuppressWarnings("resource")
        final
        PreparedStatement ps = conn.prepareStatement(sql);
        try {
            configureStatement(ps);
        } catch (final SQLException e) {
            ps.close();
            throw e;
        }
        return ps;
    }

    /**
     * Factory method that creates and initializes a
     * {@code PreparedStatement} object for the given SQL.
     * {@code QueryRunner} methods always call this method to prepare
     * statements for them. Subclasses can override this method to provide
     * special PreparedStatement configuration if needed. This implementation
     * simply calls {@code conn.prepareStatement(sql, returnedKeys)}
     * which will result in the ability to retrieve the automatically-generated
     * keys from an auto_increment column.
     *
     * @param conn
     *            The {@code Connection} used to create the
     *            {@code PreparedStatement}
     * @param sql
     *            The SQL statement to prepare.
     * @param returnedKeys
     *            Flag indicating whether to return generated keys or not.
     *
     * @return An initialized {@code PreparedStatement}.
     * @throws SQLException
     *             if a database access error occurs
     * @since 1.6
     */
    protected PreparedStatement prepareStatement(final Connection conn, final String sql, final int returnedKeys)
            throws SQLException {

        @SuppressWarnings("resource")
        final
        PreparedStatement ps = conn.prepareStatement(sql, returnedKeys);
        try {
            configureStatement(ps);
        } catch (final SQLException e) {
            ps.close();
            throw e;
        }
        return ps;
    }

    /**
     * Throws a new exception with a more informative error message.
     *
     * @param cause
     *            The original exception that will be chained to the new
     *            exception when it's rethrown.
     *
     * @param sql
     *            The query that was executing when the exception happened.
     *
     * @param params
     *            The query replacement parameters; {@code null} is a valid
     *            value to pass in.
     *
     * @throws SQLException
     *             if a database access error occurs
     */
    protected void rethrow(final SQLException cause, final String sql, final Object... params)
            throws SQLException {

        String causeMessage = cause.getMessage();
        if (causeMessage == null) {
            causeMessage = "";
        }
        final StringBuilder msg = new StringBuilder(causeMessage);

        msg.append(" Query: ");
        msg.append(sql);
        msg.append(" Parameters: ");

        if (params == null) {
            msg.append("[]");
        } else {
            msg.append(Arrays.deepToString(params));
        }

        final SQLException e = new SQLException(msg.toString(), cause.getSQLState(),
                cause.getErrorCode());
        e.setNextException(cause);

        throw e;
    }

    /**
     * Wrap the {@code ResultSet} in a decorator before processing it. This
     * implementation returns the {@code ResultSet} it is given without any
     * decoration.
     *
     * <p>
     * Often, the implementation of this method can be done in an anonymous
     * inner class like this:
     * </p>
     *
     * <pre>
     * QueryRunner run = new QueryRunner() {
     *     protected ResultSet wrap(ResultSet rs) {
     *         return StringTrimmedResultSet.wrap(rs);
     *     }
     * };
     * </pre>
     *
     * @param rs
     *            The {@code ResultSet} to decorate; never
     *            {@code null}.
     * @return The {@code ResultSet} wrapped in some decorator.
     */
    protected ResultSet wrap(final ResultSet rs) {
        return rs;
    }

}
