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
import java.lang.reflect.Proxy;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

/**
 * Creates proxy implementations of JDBC interfaces.  This avoids 
 * incompatibilities between the JDBC 2 and JDBC 3 interfaces.  This class is 
 * thread safe.
 * 
 * @see java.lang.reflect.Proxy
 * @see java.lang.reflect.InvocationHandler
 */
public class ProxyFactory {

    /**
     * Class[] for CallableStatement interface.
     */
    private static final Class[] callableStatementClass =
        new Class[] { CallableStatement.class };

    /**
     * Class[] for Connection interface.
     */
    private static final Class[] connectionClass =
        new Class[] { Connection.class };

    /**
     * Class[] for Driver interface.
     */
    private static final Class[] driverClass = new Class[] { Driver.class };

    /**
     * The Singleton instance of this class.
     */
    private static final ProxyFactory instance = new ProxyFactory();

    /**
     * Class[] for ResultSetMetaData interface.
     */
    private static final Class[] metaClass =
        new Class[] { ResultSetMetaData.class };

    /**
     * Class[] for PreparedStatement interface.
     */
    private static final Class[] preparedStatementClass =
        new Class[] { PreparedStatement.class };

    /**
     * Class[] for ResultSet interface.
     */
    private static final Class[] resultSetClass =
        new Class[] { ResultSet.class };

    /**
     * Class[] for Statement interface.
     */
    private static final Class[] statementClass =
        new Class[] { Statement.class };

    /**
     * Returns the Singleton instance of this class.
     *
     * @return singleton instance
     */
    public static ProxyFactory instance() {
        return instance;
    }

    /**
     * Protected constructor for ProxyFactory subclasses to use.
     */
    protected ProxyFactory() {
        super();
    }

    /**
     * Creates a new proxy <code>CallableStatement</code> object.
     * @param handler The handler that intercepts/overrides method calls.
     * @return proxied CallableStatement
     */
    public CallableStatement createCallableStatement(InvocationHandler handler) {
        return (CallableStatement) Proxy.newProxyInstance(
            handler.getClass().getClassLoader(),
            callableStatementClass,
            handler);
    }

    /**
     * Creates a new proxy <code>Connection</code> object.
     * @param handler The handler that intercepts/overrides method calls.
     * @return proxied Connection
     */
    public Connection createConnection(InvocationHandler handler) {
        return (Connection) Proxy.newProxyInstance(
            handler.getClass().getClassLoader(),
            connectionClass,
            handler);
    }

    /**
     * Creates a new proxy <code>Driver</code> object.
     * @param handler The handler that intercepts/overrides method calls.
     * @return proxied Driver
     */
    public Driver createDriver(InvocationHandler handler) {
        return (Driver) Proxy.newProxyInstance(
            handler.getClass().getClassLoader(),
            driverClass,
            handler);
    }

    /**
     * Creates a new proxy <code>PreparedStatement</code> object.
     * @param handler The handler that intercepts/overrides method calls.
     * @return proxied PreparedStatement
     */
    public PreparedStatement createPreparedStatement(InvocationHandler handler) {
        return (PreparedStatement) Proxy.newProxyInstance(
            handler.getClass().getClassLoader(),
            preparedStatementClass,
            handler);
    }

    /**
     * Creates a new proxy <code>ResultSet</code> object.
     * @param handler The handler that intercepts/overrides method calls.
     * @return proxied ResultSet
     */
    public ResultSet createResultSet(InvocationHandler handler) {
        return (ResultSet) Proxy.newProxyInstance(
            handler.getClass().getClassLoader(),
            resultSetClass,
            handler);
    }

    /**
     * Creates a new proxy <code>ResultSetMetaData</code> object.
     * @param handler The handler that intercepts/overrides method calls.
     * @return proxied ResultSetMetaData
     */
    public ResultSetMetaData createResultSetMetaData(InvocationHandler handler) {
        return (ResultSetMetaData) Proxy.newProxyInstance(
            handler.getClass().getClassLoader(),
            metaClass,
            handler);
    }

    /**
     * Creates a new proxy <code>Statement</code> object.
     * @param handler The handler that intercepts/overrides method calls.
     * @return proxied Statement
     */
    public Statement createStatement(InvocationHandler handler) {
        return (Statement) Proxy.newProxyInstance(
            handler.getClass().getClassLoader(),
            statementClass,
            handler);
    }

}
