/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//dbutils/src/java/org/apache/commons/dbutils/QueryRunner.java,v 1.2 2003/11/03 00:38:21 dgraham Exp $
 * $Revision: 1.2 $
 * $Date: 2003/11/03 00:38:21 $
 * 
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package org.apache.commons.dbutils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;

import javax.sql.DataSource;

/**
 * Executes SQL queries with pluggable strategies for handling 
 * <code>ResultSet</code>s.  This class is thread safe.
 * 
 * @see ResultSetHandler
 * 
 * @author Henri Yandell
 * @author Juozas Baliuka
 * @author Steven Caswell
 * @author David Graham
 */
public class QueryRunner {

    /**
     * The DataSource to retrieve connections from.
     */
    protected DataSource ds = null;

    /**
     * Constructor for QueryRunner.
     */
    public QueryRunner() {
        super();
    }

    /**
     * Constructor for QueryRunner.  Methods that do not take a 
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
     * Fill the <code>PreparedStatement</code> replacement parameters with 
     * the given objects.
     * @param stmt
     * @param params Query replacement parameters; <code>null</code> is a valid
     * value to pass in.
     * @throws SQLException
     */
    protected void fillStatement(PreparedStatement stmt, Object[] params)
        throws SQLException {

        if (params == null) {
            return;
        }

        for (int i = 0; i < params.length; i++) {
            if (params[i] != null) {
                stmt.setObject(i + 1, params[i]);
            } else {
                stmt.setNull(i + 1, Types.OTHER);
            }
        }
    }

    /**
     * Returns the <code>DataSource</code> this runner is using.
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
     * @throws SQLException
     */
    protected PreparedStatement prepareStatement(Connection conn, String sql)
        throws SQLException {
            
        return conn.prepareStatement(sql);
    }

    /**
     * Execute an SQL SELECT query with a single replacement parameter.  The
     * caller is responsible for connection cleanup.
     * 
     * @param conn The connection to execute the query in.
     * @param sql The query to execute.
     * @param param The replacement parameter.
     * @param rsh The handler that converts the results into an object.
     * @return The object returned by the handler.
     * @throws SQLException
     */
    public Object query(
        Connection conn,
        String sql,
        Object param,
        ResultSetHandler rsh)
        throws SQLException {

        return this.query(conn, sql, new Object[] { param }, rsh);
    }

    /**
     * Execute an SQL SELECT query with replacement parameters.  The
     * caller is responsible for connection cleanup.
     * 
     * @param conn The connection to execute the query in.
     * @param sql The query to execute.
     * @param params The replacement parameters.
     * @param rsh The handler that converts the results into an object.
     * @return The object returned by the handler.
     * @throws SQLException
     */
    public Object query(
        Connection conn,
        String sql,
        Object[] params,
        ResultSetHandler rsh)
        throws SQLException {

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
            DbUtils.close(rs);
            DbUtils.close(stmt);
        }

        return result;
    }

    /**
     * Execute an SQL SELECT query without any replacement parameters.  The
     * caller is responsible for connection cleanup.
     * 
     * @param conn The connection to execute the query in.
     * @param sql The query to execute.
     * @param rsh The handler that converts the results into an object.
     * @return The object returned by the handler.
     * @throws SQLException
     */
    public Object query(Connection conn, String sql, ResultSetHandler rsh)
        throws SQLException {

        return this.query(conn, sql, (Object[]) null, rsh);
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
     * @throws SQLException
     */
    public Object query(String sql, Object param, ResultSetHandler rsh)
        throws SQLException {

        return this.query(sql, new Object[] { param }, rsh);
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
     * @throws SQLException
     */
    public Object query(String sql, Object[] params, ResultSetHandler rsh)
        throws SQLException {

        Connection conn = this.ds.getConnection();

        Object result = null;

        try {
            result = this.query(conn, sql, params, rsh);

        } finally {
            DbUtils.close(conn);
        }

        return result;
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
     * @throws SQLException
     */
    public Object query(String sql, ResultSetHandler rsh) throws SQLException {
        return this.query(sql, (Object[]) null, rsh);
    }

    /**
     * Throws a new exception with a more informative error message.
     * 
     * @param cause The original exception that will be chained to the new 
     * exception when it's rethrown. 
     * 
     * @param sql The query that was executing when the exception happened.
     * @param params The query replacement paramaters; <code>null</code> is a 
     * valid value to pass in.
     * 
     * @throws SQLException
     */
    protected void rethrow(SQLException cause, String sql, Object[] params)
        throws SQLException {

        StringBuffer msg =
            new StringBuffer(cause.getMessage() + " in query " + sql);
            
        if (params != null) {
            msg.append(Arrays.asList(params).toString());
        }

        SQLException newsqle = new SQLException(msg.toString());
        newsqle.setNextException(cause);

        throw newsqle;
    }

    /**
     * Sets the <code>DataSource</code> this runner will use to get
     * database connections from.  This should be called after creating a
     * runner with the default constructor if you intend to use the
     * execute methods without passing in a <code>Connection</code>.
     * 
     * @param dataSource The DataSource to use.
     */
    public void setDataSource(DataSource dataSource) {
        this.ds = dataSource;
    }

    /**
     * Execute an SQL INSERT, UPDATE, or DELETE query without replacement
     * parameters.
     * 
     * @param conn The connection to use to run the query.
     * @param sql The SQL to execute.
     * @return The number of rows updated.
     * @throws SQLException
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
     * @throws SQLException
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
     * @throws SQLException
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
            DbUtils.close(stmt);
        }

        return rows;
    }

    /**
     * Executes the given INSERT, UPDATE, or DELETE SQL statement without
     * any replacement parameters.  The statement is executed in its own 
     * transaction that will be committed or rolled back depending on any 
     * SQLExceptions thrown.  The <code>Connection</code> is retrieved from the
     * <code>DataSource</code> set in the constructor.
     * 
     * @param sql The SQL statement to execute.
     * @throws SQLException
     * @return The number of rows updated.
     */
    public int update(String sql) throws SQLException {
        return this.update(sql, (Object[]) null);
    }

    /**
     * Executes the given INSERT, UPDATE, or DELETE SQL statement with
     * a single replacement parameter.  The statement is executed in its own 
     * transaction that will be committed or rolled back depending on any 
     * SQLExceptions thrown.  The <code>Connection</code> is retrieved from the
     * <code>DataSource</code> set in the constructor.
     * 
     * @param sql The SQL statement to execute.
     * @param param The replacement parameter.
     * @throws SQLException
     * @return The number of rows updated.
     */
    public int update(String sql, Object param) throws SQLException {
        return this.update(sql, new Object[] { param });
    }

    /**
     * Executes the given INSERT, UPDATE, or DELETE SQL statement.  The 
     * statement is executed in its own transaction that will be committed or 
     * rolled back depending on any SQLExceptions thrown.  The 
     * <code>Connection</code> is retrieved from the <code>DataSource</code> 
     * set in the constructor.
     * 
     * @param sql The SQL statement to execute.
     * @param params Initializes the PreparedStatement's IN (ie. '?') 
     * parameters.
     * @throws SQLException
     * @return The number of rows updated.
     */
    public int update(String sql, Object[] params) throws SQLException {

        Connection conn = this.ds.getConnection();
        int rows = 0;

        try {
            conn.setAutoCommit(false); // single transaction.
            rows = this.update(conn, sql, params);
            conn.commit();

        } catch (SQLException e) {
            DbUtils.rollback(conn);
            throw e;

        } finally {
            DbUtils.close(conn);
        }

        return rows;
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
     *     protected void wrap(ResultSet rs) {
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

}
