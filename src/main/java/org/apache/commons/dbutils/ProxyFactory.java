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
     * The Singleton instance of this class.
     */
    private static final ProxyFactory instance = new ProxyFactory();

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
     * Convenience method to generate a single-interface proxy using the handler's classloader
     *
     * @param <T> The type of object to proxy
     * @param type The type of object to proxy
     * @param handler The handler that intercepts/overrides method calls.
     * @return proxied object
     */
    public <T> T newProxyInstance(final Class<T> type, final InvocationHandler handler) {
        return type.cast(Proxy.newProxyInstance(handler.getClass().getClassLoader(), new Class<?>[] {type}, handler));
    }

    /**
     * Creates a new proxy {@code CallableStatement} object.
     * @param handler The handler that intercepts/overrides method calls.
     * @return proxied CallableStatement
     */
    public CallableStatement createCallableStatement(final InvocationHandler handler) {
        return newProxyInstance(CallableStatement.class, handler);
    }

    /**
     * Creates a new proxy {@code Connection} object.
     * @param handler The handler that intercepts/overrides method calls.
     * @return proxied Connection
     */
    public Connection createConnection(final InvocationHandler handler) {
        return newProxyInstance(Connection.class, handler);
    }

    /**
     * Creates a new proxy {@code Driver} object.
     * @param handler The handler that intercepts/overrides method calls.
     * @return proxied Driver
     */
    public Driver createDriver(final InvocationHandler handler) {
        return newProxyInstance(Driver.class, handler);
    }

    /**
     * Creates a new proxy {@code PreparedStatement} object.
     * @param handler The handler that intercepts/overrides method calls.
     * @return proxied PreparedStatement
     */
    public PreparedStatement createPreparedStatement(final InvocationHandler handler) {
        return newProxyInstance(PreparedStatement.class, handler);
    }

    /**
     * Creates a new proxy {@code ResultSet} object.
     * @param handler The handler that intercepts/overrides method calls.
     * @return proxied ResultSet
     */
    public ResultSet createResultSet(final InvocationHandler handler) {
        return newProxyInstance(ResultSet.class, handler);
    }

    /**
     * Creates a new proxy {@code ResultSetMetaData} object.
     * @param handler The handler that intercepts/overrides method calls.
     * @return proxied ResultSetMetaData
     */
    public ResultSetMetaData createResultSetMetaData(final InvocationHandler handler) {
        return newProxyInstance(ResultSetMetaData.class, handler);
    }

    /**
     * Creates a new proxy {@code Statement} object.
     * @param handler The handler that intercepts/overrides method calls.
     * @return proxied Statement
     */
    public Statement createStatement(final InvocationHandler handler) {
        return newProxyInstance(Statement.class, handler);
    }

}
