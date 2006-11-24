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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

/**
 * ProxyFactoryTest performs simple type checking on proxy objects returned
 * from a ProxyFactory.
 */
public class ProxyFactoryTest extends BaseTestCase {

	private static final InvocationHandler stub = new InvocationHandler() {

		public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {

			return null;
		}
	};

	/**
	 * Constructor for ProxyFactoryTest.
	 */
	public ProxyFactoryTest(String name) {
		super(name);
	}

	public void testCreateConnection() {
		assertTrue(
			ProxyFactory.instance().createConnection(stub)
				instanceof Connection);
	}

	public void testCreateDriver() {
		assertTrue(
			ProxyFactory.instance().createDriver(stub) instanceof Driver);
	}

	public void testCreatePreparedStatement() {
		assertTrue(
			ProxyFactory.instance().createPreparedStatement(stub)
				instanceof PreparedStatement);
	}

	public void testCreateResultSet() {
		assertTrue(
			ProxyFactory.instance().createResultSet(stub) instanceof ResultSet);
	}

	public void testCreateResultSetMetaData() {
		assertTrue(
			ProxyFactory.instance().createResultSetMetaData(stub)
				instanceof ResultSetMetaData);
	}

	public void testCreateStatement() {
		assertTrue(
			ProxyFactory.instance().createStatement(stub) instanceof Statement);
	}

	public void testCreateCallableStatement() {
		assertTrue(
			ProxyFactory.instance().createCallableStatement(stub)
				instanceof CallableStatement);
	}

}
