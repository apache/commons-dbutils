/*
 * Copyright (C) 2016 Yong Zhu.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.github.drinkjava2.dbutilspro.inline;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.dbutils.OutParameter;
import org.apache.commons.dbutils.ResultSetHandler;

import com.github.drinkjava2.dbutilspro.DbPro;
import com.github.drinkjava2.dbutilspro.DbRuntimeException;
import com.github.drinkjava2.dbutilspro.improve.ImprovedQueryRunner;
import com.github.drinkjava2.jtransactions.ConnectionManager;

/**
 * InlineQueryRunner added In-line style SQL support for QueryRunner
 * 
 * @author Yong Zhu
 * @since 1.7.0
 */
public class InlineQueryRunner extends ImprovedQueryRunner {

	public InlineQueryRunner() {
		super();
	}

	public InlineQueryRunner(DataSource ds) {
		super(ds);
	}

	public InlineQueryRunner(DataSource ds, ConnectionManager cm) {
		super(ds, cm);
	}

	/**
	 * A ThreadLocal variant for temporally store parameters in current Thread
	 */
	protected static ThreadLocal<ArrayList<Object>> inlineThreadlocalParamCache = new ThreadLocal<ArrayList<Object>>() {
		@Override
		protected ArrayList<Object> initialValue() {
			return new ArrayList<Object>();
		}
	};

	/** Clear all in-line parameters stored in ThreadLocal */
	protected static void clearParams() {
		inlineThreadlocalParamCache.get().clear();
	}

	/**
	 * Clear all ThreadLocal parameters first, then cache parameters in ThreadLocal
	 * and return an empty String, a non parameter param0() call equal to
	 * clearAllInlineThreadlocalParams()
	 */
	public static String param0(Object... parameters) {
		clearParams();
		return param(parameters);
	}

	/**
	 * Cache parameters in ThreadLocal and return an empty String
	 */
	public static String param(Object... parameters) {
		for (Object o : parameters)
			inlineThreadlocalParamCache.get().add(o);
		return "";
	}

	/**
	 * Clear all ThreadLocal parameters first, then cache parameters in ThreadLocal,
	 * then return a "?" String
	 */
	public static String question0(Object... parameters) {
		clearParams();
		return question(parameters);
	}

	/**
	 * Cache parameters in ThreadLocal and return a "?" String
	 */
	public static String question(Object... parameters) {
		for (Object o : parameters)
			inlineThreadlocalParamCache.get().add(o);
		return "?";
	}

	/**
	 * Create "values(?,?,?...,?)" String according how many SQL parameters be
	 * cached in ThreadLocal
	 */
	public static String valuesQuesions() {
		int howManyQuestionMarks = inlineThreadlocalParamCache.get().size();
		StringBuilder sb = new StringBuilder("values(");
		for (int i = 0; i < howManyQuestionMarks; i++) {
			if (i != howManyQuestionMarks - 1)
				sb.append("?,");
			else
				sb.append("?");
		}
		sb.append(")");
		return sb.toString();
	}

	/**
	 * Build a SqlAndParams instance by given in-line style SQL and parameters
	 * stored in ThreadLocal
	 * 
	 * @param inlineSQL
	 * @return SqlAndParams instance
	 */
	protected SqlAndParams inlineToSqlAndParams(String... inlineSQL) {
		try {
			String sql = null;
			if (inlineSQL != null) {
				StringBuilder sb = new StringBuilder("");
				for (String str : inlineSQL)
					sb.append(str);
				sql = sb.toString();
			}
			ArrayList<Object> params = inlineThreadlocalParamCache.get();
			SqlAndParams sp = new SqlAndParams();
			sp.setSql(sql);
			sp.setParams(params.toArray(new Object[params.size()]));
			return sp;
		} finally {
			clearParams();
		}
	}

	/**
	 * Usually used to translate a Bean to "field1,field2,...fieldx" format or
	 * "field1=?,field2=?.... fieldx=?" format inLineSQL String piece, and save bean
	 * property parameters in ThreadLocal
	 * 
	 * @param bean The Bean will be transfer to SQL piece
	 * @param conditionStr The condition String
	 * @param separatorStr The separator String
	 * @return a SQL piece and store all bean properties as parameters in
	 *         ThreadLocaled
	 */
	public static String inline(Object bean, String conditionStr, String separatorStr) {
		DbRuntimeException.assertNotNull(bean, "DbProBeanUtils bean can not be null");
		Class<?> beanClass = bean.getClass();
		BeanInfo beanInfo = null;
		PropertyDescriptor[] pds = null;
		try {
			beanInfo = Introspector.getBeanInfo(beanClass);
			pds = beanInfo.getPropertyDescriptors();
		} catch (Exception e) {
			throw new DbRuntimeException("DbProBeanUtils  fail to get bean Properties.", e);
		}
		if (pds == null || pds.length < 1)
			return "";

		StringBuilder sb = new StringBuilder();
		Object[] params = new Object[pds.length - 1];
		int i = 0;
		for (PropertyDescriptor pd : pds) {
			String fieldName = pd.getName();
			if (!"class".equals(fieldName)) {
				Method md = pd.getReadMethod();
				try {
					Object value = md.invoke(bean);
					sb.append(fieldName).append(conditionStr).append(separatorStr);
					params[i++] = value;
				} catch (Exception e) {
					throw new DbRuntimeException("DbProBeanUtils fail to get bean Properties.", e);
				}
			}
		}
		sb.setLength(sb.length() - separatorStr.length());
		for (Object param : params) {
			DbPro.param(param);
		}
		return sb.toString();
	}

	/**
	 * Same function like inline() method but clean ThreadLocal cache first
	 */
	public static String inline0(Object bean, String conditionStr, String separatorStr) {
		DbPro.param0();
		return inline(bean, conditionStr, separatorStr);
	}

	// ======================================================
	// =============== SQL methods below ====================
	// ======================================================

	/**
	 * Execute an In-Line style SQL SELECT query. The caller is responsible for
	 * closing the connection.
	 * @param <T> The type of object that the handler returns
	 * @param conn The connection to execute the query in.
	 * @param rsh The handler that converts the results into an object.
	 * @param inlineSQL The in-line style SQL and parameters.
	 * @return The object returned by the handler.
	 * @throws SQLException if a database access error occurs
	 */
	public <T> T inlineQuery(Connection conn, ResultSetHandler<T> rsh, String... inlineSQL) throws SQLException {
		SqlAndParams sp = inlineToSqlAndParams(inlineSQL);
		return this.<T>query(conn, sp.getSql(), rsh, sp.getParams());
	}

	/**
	 * Executes the In-Line Style given SELECT SQL query and returns a result
	 * object. The <code>Connection</code> is retrieved from the
	 * <code>DataSource</code> set in the constructor.
	 * @param <T> The type of object that the handler returns
	 * @param rsh The handler used to create the result object from the
	 *            <code>ResultSet</code>.
	 * @param inlineSQL the in-line style SQL and parameters
	 * @return An object generated by the handler.
	 * @throws SQLException if a database access error occurs
	 */
	public <T> T inlineQuery(ResultSetHandler<T> rsh, String... inlineSQL) throws SQLException {
		SqlAndParams sp = inlineToSqlAndParams(inlineSQL);
		return this.<T>query(sp.getSql(), rsh, sp.getParams());
	}

	/**
	 * Execute an In-Line style SQL INSERT, UPDATE, or DELETE query.
	 *
	 * @param conn The connection to use to run the query.
	 * @param inlineSQL The in-line style SQL and parameters
	 * @return The number of rows updated.
	 * @throws SQLException if a database access error occurs
	 */
	public int inlineUpdate(Connection conn, String... inlineSQL) throws SQLException {
		SqlAndParams sp = inlineToSqlAndParams(inlineSQL);
		return update(conn, sp.getSql(), sp.getParams());
	}

	/**
	 * Executes the In-Line Style given INSERT, UPDATE, or DELETE SQL statement. The
	 * <code>Connection</code> is retrieved from the <code>DataSource</code> set in
	 * the constructor. This <code>Connection</code> must be in auto-commit mode or
	 * the update will not be saved.
	 * @param inlineSQL The in-line style SQL and parameters
	 * @throws SQLException if a database access error occurs
	 * @return The number of rows updated.
	 */
	public int inlineUpdate(String... inlineSQL) throws SQLException {
		SqlAndParams sp = inlineToSqlAndParams(inlineSQL);
		return this.update(sp.getSql(), sp.getParams());
	}

	/**
	 * Executes the In-Line Style given INSERT SQL statement. The
	 * <code>Connection</code> is retrieved from the <code>DataSource</code> set in
	 * the constructor. This <code>Connection</code> must be in auto-commit mode or
	 * the insert will not be saved.
	 * @param <T> The type of object that the handler returns
	 * @param rsh The handler used to create the result object from the
	 *            <code>ResultSet</code> of auto-generated keys.
	 * @param inlineSQL The in-line style SQL and parameters
	 * @return An object generated by the handler.
	 * @throws SQLException if a database access error occurs
	 */
	public <T> T inlineInsert(ResultSetHandler<T> rsh, String... inlineSQL) throws SQLException {
		SqlAndParams sp = inlineToSqlAndParams(inlineSQL);
		return insert(sp.getSql(), rsh, sp.getParams());
	}

	/**
	 * Execute an In-Line style SQL INSERT query.
	 * @param <T> The type of object that the handler returns
	 * @param conn The connection to use to run the query.
	 * @param rsh The handler used to create the result object from the
	 *            <code>ResultSet</code> of auto-generated keys.
	 * @param inlineSQL The in-line style SQL and parameters.
	 * @return An object generated by the handler.
	 * @throws SQLException if a database access error occurs
	 */
	public <T> T inlineInsert(Connection conn, ResultSetHandler<T> rsh, String... inlineSQL) throws SQLException {
		SqlAndParams sp = inlineToSqlAndParams(inlineSQL);
		return insert(conn, sp.getSql(), rsh, sp.getParams());
	}

	/**
	 * Execute an In-Line style SQL statement, including a stored procedure call,
	 * which does not return any result sets. Any parameters which are instances of
	 * {@link OutParameter} will be registered as OUT parameters.
	 * <p>
	 * Use this method when invoking a stored procedure with OUT parameters that
	 * does not return any result sets. If you are not invoking a stored procedure,
	 * or the stored procedure has no OUT parameters, consider using
	 * {@link #inlineUpdate(java.sql.Connection, java.lang.String...) }. If the
	 * stored procedure returns result sets, use
	 * {@link #inlineExecute(java.sql.Connection, org.apache.commons.dbutils.ResultSetHandler, java.lang.String...) }.
	 *
	 * @param conn The connection to use to run the query.
	 * @param inlineSQL The in-line style SQL and parameters.
	 * @return The number of rows updated.
	 * @throws SQLException if a database access error occurs
	 */
	public int inlineExecute(Connection conn, String... inlineSQL) throws SQLException {
		SqlAndParams sp = inlineToSqlAndParams(inlineSQL);
		return this.execute(conn, sp.getSql(), sp.getParams());
	}

	/**
	 * Execute an In-Line style SQL statement, including a stored procedure call,
	 * which does not return any result sets. Any parameters which are instances of
	 * {@link OutParameter} will be registered as OUT parameters.
	 * <p>
	 * Use this method when invoking a stored procedure with OUT parameters that
	 * does not return any result sets. If you are not invoking a stored procedure,
	 * or the stored procedure has no OUT parameters, consider using
	 * {@link #inlineUpdate(java.lang.String...) }. If the stored procedure returns
	 * result sets, use
	 * {@link #inlineExecute(org.apache.commons.dbutils.ResultSetHandler, java.lang.String...) }.
	 * <p>
	 * The <code>Connection</code> is retrieved from the <code>DataSource</code> set
	 * in the constructor. This <code>Connection</code> must be in auto-commit mode
	 * or the update will not be saved.
	 *
	 * @param inlineSQL The in-line style SQL and parameters.
	 * @throws SQLException if a database access error occurs
	 * @return The number of rows updated.
	 */
	public int inlineExecute(String... inlineSQL) throws SQLException {
		SqlAndParams sp = inlineToSqlAndParams(inlineSQL);
		return this.execute(sp.getSql(), sp.getParams());
	}

	/**
	 * Execute an In-Line style SQL statement, including a stored procedure call,
	 * which returns one or more result sets. Any parameters which are instances of
	 * {@link OutParameter} will be registered as OUT parameters.
	 * <p>
	 * Use this method when: a) running SQL statements that return multiple result
	 * sets; b) invoking a stored procedure that return result sets and OUT
	 * parameters. Otherwise you may wish to use
	 * {@link #inlineQuery(java.sql.Connection, org.apache.commons.dbutils.ResultSetHandler, java.lang.String...) }
	 * (if there are no OUT parameters) or
	 * {@link #inlineExecute(java.sql.Connection, java.lang.String...) } (if there
	 * are no result sets).
	 *
	 * @param <T> The type of object that the handler returns
	 * @param conn The connection to use to run the query.
	 * @param rsh The result set handler
	 * @param inlineSQL The in-line style SQL and parameters.
	 * @return A list of objects generated by the handler
	 * @throws SQLException if a database access error occurs
	 */
	public <T> List<T> inlineExecute(Connection conn, ResultSetHandler<T> rsh, String... inlineSQL)
			throws SQLException {
		SqlAndParams sp = inlineToSqlAndParams(inlineSQL);
		return this.execute(conn, sp.getSql(), rsh, sp.getParams());
	}

	/**
	 * Execute an In-Line style SQL statement, including a stored procedure call,
	 * which returns one or more result sets. Any parameters which are instances of
	 * {@link OutParameter} will be registered as OUT parameters.
	 * <p>
	 * Use this method when: a) running SQL statements that return multiple result
	 * sets; b) invoking a stored procedure that return result sets and OUT
	 * parameters. Otherwise you may wish to use
	 * {@link #inlineQuery(org.apache.commons.dbutils.ResultSetHandler, java.lang.String...) }
	 * (if there are no OUT parameters) or
	 * {@link #inlineExecute(java.lang.String...) } (if there are no result sets).
	 *
	 * @param <T> The type of object that the handler returns
	 * @param rsh The result set handler
	 * @param inlineSQL The in-line style SQL and parameters
	 * @return A list of objects generated by the handler
	 * @throws SQLException if a database access error occurs
	 */
	public <T> List<T> inlineExecute(ResultSetHandler<T> rsh, String... inlineSQL) throws SQLException {
		SqlAndParams sp = inlineToSqlAndParams(inlineSQL);
		return this.execute(sp.getSql(), rsh, sp.getParams());
	}

}
