/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//dbutils/src/java/org/apache/commons/dbutils/ProxyFactory.java,v 1.1 2003/11/02 19:15:23 dgraham Exp $
 * $Revision: 1.1 $
 * $Date: 2003/11/02 19:15:23 $
 * 
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
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
 * Creates proxy implementations of JDBC classes.  This avoids 
 * incompatibilities between the JDBC 2 and JDBC 3 interfaces.  This class is 
 * thread safe.
 * 
 * @see java.lang.reflect.Proxy
 * @see java.lang.reflect.InvocationHandler
 * 
 * @author David Graham
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
     */
    public PreparedStatement createCallableStatement(InvocationHandler handler) {
        return (CallableStatement) Proxy.newProxyInstance(
            handler.getClass().getClassLoader(),
            callableStatementClass,
            handler);
    }

    /**
     * Creates a new proxy <code>Connection</code> object.
     * @param handler The handler that intercepts/overrides method calls.
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
     */
    public Statement createStatement(InvocationHandler handler) {
        return (Statement) Proxy.newProxyInstance(
            handler.getClass().getClassLoader(),
            statementClass,
            handler);
    }

}
