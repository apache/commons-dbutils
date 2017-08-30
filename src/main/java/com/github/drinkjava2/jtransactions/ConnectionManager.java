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
package com.github.drinkjava2.jtransactions;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

/**
 * A ConnectionManager implementation determine how to get or release connection
 * from DataSource or ThreadLocal or from Spring or JTA or some container...
 * 
 * @author Yong Zhu
 * @since 1.0.0
 */
public interface ConnectionManager {
	/**
	 * A ConnectionManager implementation determine how to get connection from
	 * DataSource or ThreadLocal or from Spring or JTA or some container...
	 * 
	 * @param dataSource
	 * @return Connection instance
	 * @throws SQLException
	 */
	public Connection getConnection(DataSource ds) throws SQLException;

	/**
	 * A ConnectionManager implementation determine how to close connection or
	 * return connection to ThreadLocal or return to Spring or JTA or some
	 * container...
	 * 
	 * @param con
	 * @param dataSource
	 * @throws SQLException
	 */
	public void releaseConnection(Connection conn, DataSource ds) throws SQLException;

}
