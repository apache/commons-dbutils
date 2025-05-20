/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.dbutils;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.lang.reflect.InvocationHandler;

import org.junit.jupiter.api.Test;

/**
 * ProxyFactoryTest performs simple type checking on proxy objects returned from a ProxyFactory.
 */
public class ProxyFactoryTest extends BaseTestCase {

    private static final InvocationHandler stub = (proxy, method, args) -> null;

    @Test
    public void testCreateCallableStatement() {
        assertNotNull(ProxyFactory.instance().createCallableStatement(stub));
    }

    @Test
    public void testCreateConnection() {
        assertNotNull(ProxyFactory.instance().createConnection(stub));
    }

    @Test
    public void testCreateDriver() {
        assertNotNull(ProxyFactory.instance().createDriver(stub));
    }

    @Test
    public void testCreatePreparedStatement() {
        assertNotNull(ProxyFactory.instance().createPreparedStatement(stub));
    }

    @Test
    public void testCreateResultSet() {
        assertNotNull(ProxyFactory.instance().createResultSet(stub));
    }

    @Test
    public void testCreateResultSetMetaData() {
        assertNotNull(ProxyFactory.instance().createResultSetMetaData(stub));
    }

    @Test
    public void testCreateStatement() {
        assertNotNull(ProxyFactory.instance().createStatement(stub));
    }

}
