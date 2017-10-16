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
package com.github.drinkjava2.dbutilspro.improve;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;

import javax.sql.DataSource;

import org.apache.commons.dbutils.QueryRunner;

import com.github.drinkjava2.dbutilspro.DbProLogger;
import com.github.drinkjava2.jtransactions.ConnectionManager;

/**
 * ImprovedQueryRunner made below improvements compare DbUtils's QueryRunner:
 * 
 * 
 * 1) Override close() and prepareConnection() method of QueryRunner, use a
 * ConnectionManager to manage connection, ConnectionManager can get connection
 * from DataSource or ThreadLocal or some other 3rd party tools like Spring.
 * <br/>
 * 2) Override some methods to add logger support
 * 
 * @author Yong Zhu
 * @since 1.7.0
 */
public class ImprovedQueryRunner extends QueryRunner {
	/**
	 * The ConnectionManager determine how to get and release connections from
	 * DataSource or ThreadLocal or container
	 */
	protected ConnectionManager cm;
	protected Boolean allowShowSQL = false;
	private static final DbProLogger staticlogger = DbProLogger.getLog(ImprovedQueryRunner.class);
	protected DbProLogger logger = staticlogger;

	public ImprovedQueryRunner() {
		super();
	}

	public ImprovedQueryRunner(DataSource ds) {
		super(ds);
	}

	public ImprovedQueryRunner(DataSource ds, ConnectionManager cm) {
		super(ds);
		this.cm = cm;
	}

	@Override
	public void close(Connection conn) throws SQLException {
		if (cm == null)
			super.close(conn);
		else
			cm.releaseConnection(conn, this.getDataSource());
	}

	@Override
	public Connection prepareConnection() throws SQLException {
		if (cm == null)
			return super.prepareConnection();
		else
			return cm.getConnection(this.getDataSource());
	}

	@Override
	protected CallableStatement prepareCall(Connection conn, String sql) throws SQLException {
		if (this.getAllowShowSQL())
			logger.info("SQL: " + sql);
		return super.prepareCall(conn, sql);
	}

	@Override
	protected PreparedStatement prepareStatement(Connection conn, String sql) throws SQLException {
		if (this.getAllowShowSQL())
			logger.info(formatSql(sql));
		return super.prepareStatement(conn, sql);
	}

	@Override
	public void fillStatement(PreparedStatement stmt, Object... params) throws SQLException {
		if (this.getAllowShowSQL())
			logger.info(formatParameters(params));
		super.fillStatement(stmt, params);
	}

	/**
	 * Format SQL, subClass can override this method to customise SQL format
	 */
	protected String formatSql(String sql) {
		return "SQL: " + sql;
	}

	/**
	 * Format parameters, subClass can override this method to customise parameters
	 * format
	 */
	protected String formatParameters(Object... params) {
		return "Parameters: " + Arrays.deepToString(params);
	}

	// ==========getter & setter==========
	public ConnectionManager getConnectionManager() {
		return cm;
	}

	public void setConnectionManager(ConnectionManager connectionManager) {
		this.cm = connectionManager;
	}

	public Boolean getAllowShowSQL() {
		return allowShowSQL;
	}

	public void setAllowShowSQL(Boolean allowShowSQL) {
		this.allowShowSQL = allowShowSQL;
	}

	public DbProLogger getLogger() {
		return logger;
	}

	public void setLogger(DbProLogger logger) {
		this.logger = logger;
	}

}
