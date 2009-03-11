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
import java.sql.Connection;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Arrays;

import javax.sql.DataSource;

/**
 * Executes SQL queries with pluggable strategies for handling 
 * <code>ResultSet</code>s.  This class is thread safe.
 * 
 * @see ResultSetHandler
 */
public class QueryRunner {

    /**
     * Is {@link ParameterMetaData#getParameterType(int)} broken (have we tried it yet)?
     */
    private volatile boolean pmdKnownBroken = false;
    
    /**
     * The DataSource to retrieve connections from.
     */
    protected final DataSource ds;

    /**
     * Constructor for QueryRunner.
     */
    public QueryRunner() {
        super();
        ds = null;
    }

    /**
     * Constructor for QueryRunner, allows workaround for Oracle drivers
     * @param pmdKnownBroken Oracle drivers don't support {@link ParameterMetaData#getParameterType(int) };
     * if <code>pmdKnownBroken</code> is set to true, we won't even try it; if false, we'll try it,
     * and if it breaks, we'll remember not to use it again.
     */
    public QueryRunner(boolean pmdKnownBroken) {
        super();
        this.pmdKnownBroken = pmdKnownBroken; 
        ds = null;
    }
    
    /**
     * Constructor for QueryRunner, allows workaround for Oracle drivers.  Methods that do not take a 
     * <code>Connection</code> parameter will retrieve connections from this
     * <code>DataSource</code>.
     * 
     * @param ds The <code>DataSource</code> to retrieve connections from.
     */
    public QueryRunner(DataSource ds) {
        super();
        this.ds = ds;
    }
    
    /**
     * Constructor for QueryRunner, allows workaround for Oracle drivers.  Methods that do not take a 
     * <code>Connection</code> parameter will retrieve connections from this
     * <code>DataSource</code>.
     * 
     * @param ds The <code>DataSource</code> to retrieve connections from.
     * @param pmdKnownBroken Oracle drivers don't support {@link ParameterMetaData#getParameterType(int) };
     * if <code>pmdKnownBroken</code> is set to true, we won't even try it; if false, we'll try it,
     * and if it breaks, we'll remember not to use it again.
     */
    public QueryRunner(DataSource ds, boolean pmdKnownBroken) {
        super();
        this.pmdKnownBroken = pmdKnownBroken;
        this.ds = ds;
    }
    
    /**
     * Execute a batch of SQL INSERT, UPDATE, or DELETE queries.
     * 
     * @param conn The Connection to use to run the query.  The caller is
     * responsible for closing this Connection.
     * @param sql The SQL to execute.
     * @param params An array of query replacement parameters.  Each row in
     * this array is one set of batch replacement values. 
     * @return The number of rows updated per statement.
     * @throws SQLException if a database access error occurs
     * @since DbUtils 1.1
     */
    public int[] batch(Connection conn, String sql, Object[][] params)
        throws SQLException {

        PreparedStatement stmt = null;
        int[] rows = null;
        try {
            stmt = this.prepareStatement(conn, sql);

            for (int i = 0; i < params.length; i++) {
                this.fillStatement(stmt, params[i]);
                stmt.addBatch();
            }
            rows = stmt.executeBatch();

        } catch (SQLException e) {
            this.rethrow(e, sql, params);
        } finally {
            close(stmt);
        }

        return rows;
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
     * @return The number of rows updated per statement.
     * @throws SQLException if a database access error occurs
     * @since DbUtils 1.1
     */
    public int[] batch(String sql, Object[][] params) throws SQLException {
        Connection conn = this.prepareConnection();

        try {
            return this.batch(conn, sql, params);
        } finally {
            close(conn);
        }
    }

    /**
     * Fill the <code>PreparedStatement</code> replacement parameters with 
     * the given objects.
     * @param stmt PreparedStatement to fill
     * @param params Query replacement parameters; <code>null</code> is a valid
     * value to pass in.
     * @throws SQLException if a database access error occurs
     */
    public void fillStatement(PreparedStatement stmt, Object[] params)
        throws SQLException {

        if (params == null) {
            return;
        }
        
        ParameterMetaData pmd = stmt.getParameterMetaData();
        if (pmd.getParameterCount() < params.length) {
            throw new SQLException("Too many parameters: expected "
                    + pmd.getParameterCount() + ", was given " + params.length);
        }
        for (int i = 0; i < params.length; i++) {
            if (params[i] != null) {
                stmt.setObject(i + 1, params[i]);
            } else {
                // VARCHAR works with many drivers regardless
                // of the actual column type.  Oddly, NULL and 
                // OTHER don't work with Oracle's drivers.
                int sqlType = Types.VARCHAR;
                if (!pmdKnownBroken) {
                    try {
                        sqlType = pmd.getParameterType(i + 1);
                    } catch (SQLException e) {
                        pmdKnownBroken = true;
                    }
                }
                stmt.setNull(i + 1, sqlType);
            }
        }
    }

    /**
     * Fill the <code>PreparedStatement</code> replacement parameters with the
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
    public void fillStatementWithBean(PreparedStatement stmt, Object bean,
            PropertyDescriptor[] properties) throws SQLException {
        Object[] params = new Object[properties.length];
        for (int i = 0; i < properties.length; i++) {
            PropertyDescriptor property = properties[i];
            Object value = null;
            Method method = property.getReadMethod();
            if (method == null) {
                throw new RuntimeException("No read method for bean property "
                        + bean.getClass() + " " + property.getName());
            }
            try {
                value = method.invoke(bean, new Object[0]);
            } catch (InvocationTargetException e) {
                throw new RuntimeException("Couldn't invoke method: " + method, e);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Couldn't invoke method with 0 arguments: " + method, e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Couldn't invoke method: " + method, e);
            } 
            params[i] = value;
        }
        fillStatement(stmt, params);
    }

    /**
     * Fill the <code>PreparedStatement</code> replacement parameters with the
     * given object's bean property values.
     * 
     * @param stmt
     *            PreparedStatement to fill
     * @param bean
     *            a JavaBean object
     * @param propertyNames
     *            an ordered array of property names (these should match the
     *            getters/setters); this gives the order to insert values in the
     *            statement
     * @throws SQLException
     *             if a database access error occurs
     */
    public void fillStatementWithBean(PreparedStatement stmt, Object bean,
            String[] propertyNames) throws SQLException {
        PropertyDescriptor[] descriptors;
        try {
            descriptors = Introspector.getBeanInfo(bean.getClass())
                    .getPropertyDescriptors();
        } catch (IntrospectionException e) {
            throw new RuntimeException("Couldn't introspect bean " + bean.getClass().toString(), e);
        }
        PropertyDescriptor[] sorted = new PropertyDescriptor[propertyNames.length];
        for (int i = 0; i < propertyNames.length; i++) {
            String propertyName = propertyNames[i];
            if (propertyName == null) {
                throw new NullPointerException("propertyName can't be null: " + i);
            }
            boolean found = false;
            for (int j = 0; j < descriptors.length; j++) {
                PropertyDescriptor descriptor = descriptors[j];
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
     * Returns the <code>DataSource</code> this runner is using.  
     * <code>QueryRunner</code> methods always call this method to get the
     * <code>DataSource</code> so subclasses can provide specialized
     * behavior.
     *
     * @return DataSource the runner is using
     */
    public DataSource getDataSource() {
        return this.ds;
    }

    /**
     * Factory method that creates and initializes a 
     * <code>PreparedStatement</code> object for the given SQL.  
     * <code>QueryRunner</code> methods always call this method to prepare 
     * statements for them.  Subclasses can override this method to provide 
     * special PreparedStatement configuration if needed.  This implementation
     * simply calls <code>conn.prepareStatement(sql)</code>.
     *  
     * @param conn The <code>Connection</code> used to create the 
     * <code>PreparedStatement</code>
     * @param sql The SQL statement to prepare.
     * @return An initialized <code>PreparedStatement</code>.
     * @throws SQLException if a database access error occurs
     */
    protected PreparedStatement prepareStatement(Connection conn, String sql)
        throws SQLException {
            
        return conn.prepareStatement(sql);
    }
    
    /**
     * Factory method that creates and initializes a 
     * <code>Connection</code> object.  <code>QueryRunner</code> methods 
     * always call this method to retrieve connections from its DataSource.  
     * Subclasses can override this method to provide 
     * special <code>Connection</code> configuration if needed.  This 
     * implementation simply calls <code>ds.getConnection()</code>.
     * 
     * @return An initialized <code>Connection</code>.
     * @throws SQLException if a database access error occurs
     * @since DbUtils 1.1
     */
    protected Connection prepareConnection() throws SQLException {
        if(this.getDataSource() == null) {
            throw new SQLException("QueryRunner requires a DataSource to be " +
                "invoked in this way, or a Connection should be passed in");
        }
        return this.getDataSource().getConnection();
    }

    /**
     * Execute an SQL SELECT query with a single replacement parameter. The
     * caller is responsible for closing the connection.
     * 
     * @param conn The connection to execute the query in.
     * @param sql The query to execute.
     * @param param The replacement parameter.
     * @param rsh The handler that converts the results into an object.
     * @return The object returned by the handler.
     * @throws SQLException if a database access error occurs
     * @deprecated Use {@link #query(Connection,String,ResultSetHandler,Object[])} instead
     */
    public Object query(Connection conn, String sql, Object param,
            ResultSetHandler rsh) throws SQLException {

        return this.query(conn, sql, rsh, new Object[] { param });
    }

    /**
     * Execute an SQL SELECT query with replacement parameters.  The
     * caller is responsible for closing the connection.
     * 
     * @param conn The connection to execute the query in.
     * @param sql The query to execute.
     * @param params The replacement parameters.
     * @param rsh The handler that converts the results into an object.
     * @return The object returned by the handler.
     * @throws SQLException if a database access error occurs
     * @deprecated Use {@link #query(Connection,String,ResultSetHandler,Object[])} instead
     */
    public Object query(Connection conn, String sql, Object[] params,
            ResultSetHandler rsh) throws SQLException {
                return query(conn, sql, rsh, params);
            }

    /**
     * Execute an SQL SELECT query with replacement parameters.  The
     * caller is responsible for closing the connection.
     * 
     * @param conn The connection to execute the query in.
     * @param sql The query to execute.
     * @param rsh The handler that converts the results into an object.
     * @param params The replacement parameters.
     * @return The object returned by the handler.
     * @throws SQLException if a database access error occurs
     */
    public Object query(Connection conn, String sql, ResultSetHandler rsh,
            Object[] params) throws SQLException {

        PreparedStatement stmt = null;
        ResultSet rs = null;
        Object result = null;

        try {
            stmt = this.prepareStatement(conn, sql);
            this.fillStatement(stmt, params);
            rs = this.wrap(stmt.executeQuery());
            result = rsh.handle(rs);

        } catch (SQLException e) {
            this.rethrow(e, sql, params);

        } finally {
            try {
                close(rs);
            } finally {
                close(stmt);
            }
        }

        return result;
    }

    /**
     * Execute an SQL SELECT query without any replacement parameters.  The
     * caller is responsible for closing the connection.
     * 
     * @param conn The connection to execute the query in.
     * @param sql The query to execute.
     * @param rsh The handler that converts the results into an object.
     * @return The object returned by the handler.
     * @throws SQLException if a database access error occurs
     */
    public Object query(Connection conn, String sql, ResultSetHandler rsh)
        throws SQLException {

        return this.query(conn, sql, rsh, (Object[]) null);
    }

    /**
     * Executes the given SELECT SQL with a single replacement parameter.
     * The <code>Connection</code> is retrieved from the
     * <code>DataSource</code> set in the constructor.
     * 
     * @param sql The SQL statement to execute.
     * @param param The replacement parameter.
     * @param rsh The handler used to create the result object from 
     * the <code>ResultSet</code>.
     * 
     * @return An object generated by the handler.
     * @throws SQLException if a database access error occurs
     * @deprecated Use {@link #query(String,ResultSetHandler,Object[])} instead
     */
    public Object query(String sql, Object param, ResultSetHandler rsh)
        throws SQLException {

        return this.query(sql, rsh, new Object[] { param });
    }

    /**
     * Executes the given SELECT SQL query and returns a result object.
     * The <code>Connection</code> is retrieved from the 
     * <code>DataSource</code> set in the constructor.
     * 
     * @param sql The SQL statement to execute.
     * @param params Initialize the PreparedStatement's IN parameters with 
     * this array.
     * 
     * @param rsh The handler used to create the result object from 
     * the <code>ResultSet</code>.
     * 
     * @return An object generated by the handler.
     * @throws SQLException if a database access error occurs
     * @deprecated Use {@link #query(String,ResultSetHandler,Object[])} instead
     */
    public Object query(String sql, Object[] params, ResultSetHandler rsh)
        throws SQLException {
            return query(sql, rsh, params);
        }

    /**
     * Executes the given SELECT SQL query and returns a result object.
     * The <code>Connection</code> is retrieved from the 
     * <code>DataSource</code> set in the constructor.
     * 
     * @param sql The SQL statement to execute.
     * @param rsh The handler used to create the result object from 
     * the <code>ResultSet</code>.
     * @param params Initialize the PreparedStatement's IN parameters with 
     * this array.
     * @return An object generated by the handler.
     * @throws SQLException if a database access error occurs
     */
    public Object query(String sql, ResultSetHandler rsh, Object[] params)
        throws SQLException {

        Connection conn = this.prepareConnection();

        try {
            return this.query(conn, sql, rsh, params);
        } finally {
            close(conn);
        }
    }

    /**
     * Executes the given SELECT SQL without any replacement parameters.
     * The <code>Connection</code> is retrieved from the
     * <code>DataSource</code> set in the constructor.
     * 
     * @param sql The SQL statement to execute.
     * @param rsh The handler used to create the result object from 
     * the <code>ResultSet</code>.
     * 
     * @return An object generated by the handler.
     * @throws SQLException if a database access error occurs
     */
    public Object query(String sql, ResultSetHandler rsh) throws SQLException {
        return this.query(sql, rsh, (Object[]) null);
    }

    /**
     * Throws a new exception with a more informative error message.
     * 
     * @param cause The original exception that will be chained to the new 
     * exception when it's rethrown. 
     * 
     * @param sql The query that was executing when the exception happened.
     * 
     * @param params The query replacement parameters; <code>null</code> is a 
     * valid value to pass in.
     * 
     * @throws SQLException if a database access error occurs
     */
    protected void rethrow(SQLException cause, String sql, Object[] params)
        throws SQLException {

        String causeMessage = cause.getMessage();
        if (causeMessage == null) {
            causeMessage = "";
        }
        StringBuffer msg = new StringBuffer(causeMessage);

        msg.append(" Query: ");
        msg.append(sql);
        msg.append(" Parameters: ");

        if (params == null) {
            msg.append("[]");
        } else {
            msg.append(Arrays.asList(params));
        }

        SQLException e = new SQLException(msg.toString(), cause.getSQLState(),
                cause.getErrorCode());
        e.setNextException(cause);

        throw e;
    }

    /**
     * Execute an SQL INSERT, UPDATE, or DELETE query without replacement
     * parameters.
     * 
     * @param conn The connection to use to run the query.
     * @param sql The SQL to execute.
     * @return The number of rows updated.
     * @throws SQLException if a database access error occurs
     */
    public int update(Connection conn, String sql) throws SQLException {
        return this.update(conn, sql, (Object[]) null);
    }

    /**
     * Execute an SQL INSERT, UPDATE, or DELETE query with a single replacement
     * parameter.
     * 
     * @param conn The connection to use to run the query.
     * @param sql The SQL to execute.
     * @param param The replacement parameter.
     * @return The number of rows updated.
     * @throws SQLException if a database access error occurs
     */
    public int update(Connection conn, String sql, Object param)
        throws SQLException {

        return this.update(conn, sql, new Object[] { param });
    }

    /**
     * Execute an SQL INSERT, UPDATE, or DELETE query.
     * 
     * @param conn The connection to use to run the query.
     * @param sql The SQL to execute.
     * @param params The query replacement parameters.
     * @return The number of rows updated.
     * @throws SQLException if a database access error occurs
     */
    public int update(Connection conn, String sql, Object[] params)
        throws SQLException {

        PreparedStatement stmt = null;
        int rows = 0;

        try {
            stmt = this.prepareStatement(conn, sql);
            this.fillStatement(stmt, params);
            rows = stmt.executeUpdate();

        } catch (SQLException e) {
            this.rethrow(e, sql, params);

        } finally {
            close(stmt);
        }

        return rows;
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
     * @return The number of rows updated.
     */
    public int update(String sql) throws SQLException {
        return this.update(sql, (Object[]) null);
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
     * @return The number of rows updated.
     */
    public int update(String sql, Object param) throws SQLException {
        return this.update(sql, new Object[] { param });
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
     * @return The number of rows updated.
     */
    public int update(String sql, Object[] params) throws SQLException {
        Connection conn = this.prepareConnection();

        try {
            return this.update(conn, sql, params);
        } finally {
            close(conn);
        }
    }
    
    /**
     * Wrap the <code>ResultSet</code> in a decorator before processing it.
     * This implementation returns the <code>ResultSet</code> it is given
     * without any decoration.
     *
     * <p>
     * Often, the implementation of this method can be done in an anonymous 
     * inner class like this:
     * </p>
     * <pre> 
     * QueryRunner run = new QueryRunner() {
     *     protected ResultSet wrap(ResultSet rs) {
     *         return StringTrimmedResultSet.wrap(rs);
     *     }
     * };
     * </pre>
     * 
     * @param rs The <code>ResultSet</code> to decorate; never 
     * <code>null</code>.
     * @return The <code>ResultSet</code> wrapped in some decorator. 
     */
    protected ResultSet wrap(ResultSet rs) {
        return rs;
    }
    
    /**
     * Close a <code>Connection</code>.  This implementation avoids closing if 
     * null and does <strong>not</strong> suppress any exceptions.  Subclasses
     * can override to provide special handling like logging.
     * @param conn Connection to close
     * @throws SQLException if a database access error occurs
     * @since DbUtils 1.1
     */
    protected void close(Connection conn) throws SQLException {
        DbUtils.close(conn);
    }
    
    /**
     * Close a <code>Statement</code>.  This implementation avoids closing if 
     * null and does <strong>not</strong> suppress any exceptions.  Subclasses
     * can override to provide special handling like logging.
     * @param stmt Statement to close
     * @throws SQLException if a database access error occurs
     * @since DbUtils 1.1
     */
    protected void close(Statement stmt) throws SQLException {
        DbUtils.close(stmt);
    }

    /**
     * Close a <code>ResultSet</code>.  This implementation avoids closing if 
     * null and does <strong>not</strong> suppress any exceptions.  Subclasses
     * can override to provide special handling like logging.
     * @param rs ResultSet to close
     * @throws SQLException if a database access error occurs
     * @since DbUtils 1.1
     */
    protected void close(ResultSet rs) throws SQLException {
        DbUtils.close(rs);
    }

}
