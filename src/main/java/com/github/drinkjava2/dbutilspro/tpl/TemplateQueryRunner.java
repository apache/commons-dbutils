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
package com.github.drinkjava2.dbutilspro.tpl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.dbutils.OutParameter;
import org.apache.commons.dbutils.ResultSetHandler;

import com.github.drinkjava2.dbutilspro.inline.InlineQueryRunner;
import com.github.drinkjava2.dbutilspro.inline.SqlAndParams;
import com.github.drinkjava2.jtransactions.ConnectionManager;

/**
 * TemplateQueryRunner added SQL Template support for QueryRunner
 * 
 * @author Yong Zhu
 * @since 1.7.0
 */
public class TemplateQueryRunner extends InlineQueryRunner {

	public TemplateQueryRunner() {
		super();
	}

	public TemplateQueryRunner(DataSource ds) {
		super(ds);
	}

	public TemplateQueryRunner(DataSource ds, ConnectionManager cm) {
		super(ds, cm);
	}

	private SqlTemplateEngine sqlTemplateEngine = BasicSqlTemplate.instance();

	/**
	 * Return current SqlTemplateEngine
	 */
	public SqlTemplateEngine getSqlTemplateEngine() {
		return sqlTemplateEngine;
	}

	/**
	 * Set a SqlTemplateEngine, if not set will default use a BasicSqlTemplate
	 * instance as SQL template engine
	 * @param sqlTemplateEngine
	 */
	public void setSqlTemplateEngine(SqlTemplateEngine sqlTemplateEngine) {
		this.sqlTemplateEngine = sqlTemplateEngine;
	}

	/**
	 * A ThreadLocal variant for temporally store parameter Map in current Thread
	 */
	private static ThreadLocal<Map<String, Object>> templateThreadlocalParamMapCache = new ThreadLocal<Map<String, Object>>() {
		@Override
		protected Map<String, Object> initialValue() {
			return new HashMap<String, Object>();
		}
	};

	/**
	 * A ThreadLocal variant for temporally store parameter key names which is a
	 * direct-replace type parameter in current Thread
	 */
	protected static ThreadLocal<Set<String>> directReplaceKeysCache = new ThreadLocal<Set<String>>() {
		@Override
		protected Set<String> initialValue() {
			return new HashSet<String>();
		}
	};

	/**
	 * Clear all template ThreadLocal parameters
	 */
	protected static void clearBind() {
		templateThreadlocalParamMapCache.get().clear();
		directReplaceKeysCache.get().clear();
	}

	/**
	 * Clear all template ThreadLocal parameters, return an empty String ""
	 */
	public static String bind0() {
		clearBind();
		return "";
	}

	/**
	 * Clear all template ThreadLocal parameters, put a name-value pair into
	 * ThreadLocal parameter Map, return an empty String ""
	 */
	public static String put0(String name, Object value) {
		clearBind();
		return put(name, value);
	}

	/**
	 * Put a name-value pair into ThreadLocal parameter Map, return an empty String
	 * ""
	 */
	public static String put(String name, Object value) {
		templateThreadlocalParamMapCache.get().put(name, value);
		return "";
	}

	/**
	 * Clear all template ThreadLocal parameters, then put a name-value into
	 * ThreadLocal parameter Map, return an empty String, Note: use replace() method
	 * the value will directly replace text in template
	 */
	public static String replace0(String name, Object value) {
		clearBind();
		return replace(name, value);
	}

	/**
	 * put a name-value into ThreadLocal parameter Map, return an empty String,
	 * Note: use replace() method the value will directly replace text in template
	 */
	public static String replace(String name, Object value) {
		templateThreadlocalParamMapCache.get().put(name, value);
		directReplaceKeysCache.get().add(name);
		return "";
	}

	/**
	 * Build a SqlAndParams instance by given template style SQL and parameters
	 * stored in ThreadLocal
	 * 
	 * @param sqlTemplate
	 * @return SqlAndParams instance
	 */
	protected SqlAndParams templateToSqlAndParams(String... sqlTemplate) {
		try {
			String sql = null;
			if (sqlTemplate != null) {
				StringBuilder sb = new StringBuilder("");
				for (String str : sqlTemplate)
					sb.append(str);
				sql = sb.toString();
			}
			Map<String, Object> paramMap = templateThreadlocalParamMapCache.get();
			return sqlTemplateEngine.render(sql, paramMap, directReplaceKeysCache.get());
		} finally {
			clearBind();
		}
	}

	// ======================================================
	// =============== SQL methods below ====================
	// ======================================================

	/**
	 * Execute an SQL template SELECT query. The caller is responsible for closing
	 * the connection.
	 * @param <T> The type of object that the handler returns
	 * @param conn The connection to execute the query in.
	 * @param rsh The handler that converts the results into an object.
	 * @param templateSQL the SQL template.
	 * @return The object returned by the handler.
	 * @throws SQLException if a database access error occurs
	 */
	public <T> T templateQuery(Connection conn, ResultSetHandler<T> rsh, String... templateSQL) throws SQLException {
		SqlAndParams sp = templateToSqlAndParams(templateSQL);
		return this.<T>query(conn, sp.getSql(), rsh, sp.getParams());
	}

	/**
	 * Executes the template style given SELECT SQL query and returns a result
	 * object. The <code>Connection</code> is retrieved from the
	 * <code>DataSource</code> set in the constructor.
	 * @param <T> The type of object that the handler returns
	 * @param rsh The handler used to create the result object from the
	 *            <code>ResultSet</code>.
	 * @param templateSQL the SQL template
	 * @return An object generated by the handler.
	 * @throws SQLException if a database access error occurs
	 */
	public <T> T templateQuery(ResultSetHandler<T> rsh, String... templateSQL) throws SQLException {
		SqlAndParams sp = templateToSqlAndParams(templateSQL);
		return this.<T>query(sp.getSql(), rsh, sp.getParams());
	}

	/**
	 * Execute an SQL template INSERT, UPDATE, or DELETE query.
	 *
	 * @param conn The connection to use to run the query.
	 * @param templateSQL the SQL template
	 * @return The number of rows updated.
	 * @throws SQLException if a database access error occurs
	 */
	public int templateUpdate(Connection conn, String... templateSQL) throws SQLException {
		SqlAndParams sp = templateToSqlAndParams(templateSQL);
		return update(conn, sp.getSql(), sp.getParams());
	}

	/**
	 * Executes the template style given INSERT, UPDATE, or DELETE SQL statement.
	 * The <code>Connection</code> is retrieved from the <code>DataSource</code> set
	 * in the constructor. This <code>Connection</code> must be in auto-commit mode
	 * or the update will not be saved.
	 * @param templateSQL the SQL template
	 * @throws SQLException if a database access error occurs
	 * @return The number of rows updated.
	 */
	public int templateUpdate(String... templateSQL) throws SQLException {
		SqlAndParams sp = templateToSqlAndParams(templateSQL);
		return this.update(sp.getSql(), sp.getParams());
	}

	/**
	 * Executes the template style given INSERT SQL statement. The
	 * <code>Connection</code> is retrieved from the <code>DataSource</code> set in
	 * the constructor. This <code>Connection</code> must be in auto-commit mode or
	 * the insert will not be saved.
	 * @param <T> The type of object that the handler returns
	 * @param rsh The handler used to create the result object from the
	 *            <code>ResultSet</code> of auto-generated keys.
	 * @param templateSQL the SQL template
	 * @return An object generated by the handler.
	 * @throws SQLException if a database access error occurs
	 */
	public <T> T templateInsert(ResultSetHandler<T> rsh, String... templateSQL) throws SQLException {
		SqlAndParams sp = templateToSqlAndParams(templateSQL);
		return insert(sp.getSql(), rsh, sp.getParams());
	}

	/**
	 * Execute an SQL template INSERT query.
	 * @param <T> The type of object that the handler returns
	 * @param conn The connection to use to run the query.
	 * @param rsh The handler used to create the result object from the
	 *            <code>ResultSet</code> of auto-generated keys.
	 * @param templateSQL the SQL template.
	 * @return An object generated by the handler.
	 * @throws SQLException if a database access error occurs
	 */
	public <T> T templateInsert(Connection conn, ResultSetHandler<T> rsh, String... templateSQL) throws SQLException {
		SqlAndParams sp = templateToSqlAndParams(templateSQL);
		return insert(conn, sp.getSql(), rsh, sp.getParams());
	}

	/**
	 * Execute an SQL template statement, including a stored procedure call, which
	 * does not return any result sets. Any parameters which are instances of
	 * {@link OutParameter} will be registered as OUT parameters.
	 * <p>
	 * Use this method when invoking a stored procedure with OUT parameters that
	 * does not return any result sets. If you are not invoking a stored procedure,
	 * or the stored procedure has no OUT parameters, consider using
	 * {@link #templateUpdate(java.sql.Connection, java.lang.String...) }. If the
	 * stored procedure returns result sets, use
	 * {@link #templateExecute(java.sql.Connection, org.apache.commons.dbutils.ResultSetHandler, java.lang.String...) }.
	 *
	 * @param conn The connection to use to run the query.
	 * @param templateSQL the SQL template.
	 * @return The number of rows updated.
	 * @throws SQLException if a database access error occurs
	 */
	public int templateExecute(Connection conn, String... templateSQL) throws SQLException {
		SqlAndParams sp = templateToSqlAndParams(templateSQL);
		return this.execute(conn, sp.getSql(), sp.getParams());
	}

	/**
	 * Execute an SQL template statement, including a stored procedure call, which
	 * does not return any result sets. Any parameters which are instances of
	 * {@link OutParameter} will be registered as OUT parameters.
	 * <p>
	 * Use this method when invoking a stored procedure with OUT parameters that
	 * does not return any result sets. If you are not invoking a stored procedure,
	 * or the stored procedure has no OUT parameters, consider using
	 * {@link #templateUpdate(java.lang.String...) }. If the stored procedure
	 * returns result sets, use
	 * {@link #templateExecute(org.apache.commons.dbutils.ResultSetHandler, java.lang.String...) }.
	 * <p>
	 * The <code>Connection</code> is retrieved from the <code>DataSource</code> set
	 * in the constructor. This <code>Connection</code> must be in auto-commit mode
	 * or the update will not be saved.
	 *
	 * @param templateSQL the SQL template.
	 * @throws SQLException if a database access error occurs
	 * @return The number of rows updated.
	 */
	public int templateExecute(String... templateSQL) throws SQLException {
		SqlAndParams sp = templateToSqlAndParams(templateSQL);
		return this.execute(sp.getSql(), sp.getParams());
	}

	/**
	 * Execute an SQL template statement, including a stored procedure call, which
	 * returns one or more result sets. Any parameters which are instances of
	 * {@link OutParameter} will be registered as OUT parameters.
	 * <p>
	 * Use this method when: a) running SQL statements that return multiple result
	 * sets; b) invoking a stored procedure that return result sets and OUT
	 * parameters. Otherwise you may wish to use
	 * {@link #templateQuery(java.sql.Connection, org.apache.commons.dbutils.ResultSetHandler, java.lang.String...) }
	 * (if there are no OUT parameters) or
	 * {@link #templateExecute(java.sql.Connection, java.lang.String...) } (if there
	 * are no result sets).
	 *
	 * @param <T> The type of object that the handler returns
	 * @param conn The connection to use to run the query.
	 * @param rsh The result set handler
	 * @param templateSQL the SQL template.
	 * @return A list of objects generated by the handler
	 * @throws SQLException if a database access error occurs
	 */
	public <T> List<T> templateExecute(Connection conn, ResultSetHandler<T> rsh, String... templateSQL)
			throws SQLException {
		SqlAndParams sp = templateToSqlAndParams(templateSQL);
		return this.execute(conn, sp.getSql(), rsh, sp.getParams());
	}

	/**
	 * Execute an SQL template statement, including a stored procedure call, which
	 * returns one or more result sets. Any parameters which are instances of
	 * {@link OutParameter} will be registered as OUT parameters.
	 * <p>
	 * Use this method when: a) running SQL statements that return multiple result
	 * sets; b) invoking a stored procedure that return result sets and OUT
	 * parameters. Otherwise you may wish to use
	 * {@link #templateQuery(org.apache.commons.dbutils.ResultSetHandler, java.lang.String...) }
	 * (if there are no OUT parameters) or
	 * {@link #templateExecute(java.lang.String...) } (if there are no result sets).
	 *
	 * @param <T> The type of object that the handler returns
	 * @param rsh The result set handler
	 * @param templateSQL the SQL template
	 * @return A list of objects generated by the handler
	 * @throws SQLException if a database access error occurs
	 */
	public <T> List<T> templateExecute(ResultSetHandler<T> rsh, String... templateSQL) throws SQLException {
		SqlAndParams sp = templateToSqlAndParams(templateSQL);
		return this.execute(sp.getSql(), rsh, sp.getParams());
	}

}
