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

import java.beans.IndexedPropertyDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;

import junit.framework.TestCase;

public class QueryRunnerTest extends TestCase {
    QueryRunner runner;
    PreparedStatement stmt;
    
    static final Method getParameterCount, getParameterType, getParameterMetaData;
    static {
        try {
            getParameterCount = ParameterMetaData.class.getMethod("getParameterCount", new Class[0]);
            getParameterType = ParameterMetaData.class.getMethod("getParameterType", new Class[]{int.class});
            getParameterMetaData = PreparedStatement.class.getMethod("getParameterMetaData", new Class[0]);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public void setUp() {
        runner = new QueryRunner();
        stmt = fakePreparedStatement();
    }
    
    public void testFillStatementWithNull() throws Exception {
        stmt = fakeFillablePreparedStatement(false, new int[] {Types.VARCHAR, Types.BIGINT});
        runner.fillStatement(stmt, new Object[] { null, null }); 
    }
    
    public void testFillStatementWithNullOracle() throws Exception {
        stmt = fakeFillablePreparedStatement(true, new int[] {Types.VARCHAR, Types.BIGINT});
        runner.fillStatement(stmt, new Object[] { null, null });
    }

    private PreparedStatement fakeFillablePreparedStatement(final boolean simulateOracle, final int[] types) throws NoSuchMethodException {
        // prepare a mock ParameterMetaData and a mock PreparedStatement to return the PMD
        final ParameterMetaData pmd = mockParameterMetaData(simulateOracle,types);
        InvocationHandler stmtHandler = new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args)
                    throws Throwable {
                if (getParameterMetaData.equals(method)) {
                    return pmd;
                }
                return null;
            }
        };
        return ProxyFactory.instance().createPreparedStatement(stmtHandler);
    }

    private ParameterMetaData mockParameterMetaData(final boolean simulateOracle, final int[] types) {
        InvocationHandler pmdHandler = new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args)
                    throws Throwable {
                if (getParameterCount.equals(method)) {
                    return new Integer(types.length);
                }
                if (getParameterType.equals(method)) {
                    if (simulateOracle) throw new SQLException("Oracle fails when you call getParameterType");
                    int arg = ((Integer)args[0]).intValue();
                    return new Integer(types[arg-1]);
                }
                return null;
            }
        };
        
        return (ParameterMetaData) Proxy.newProxyInstance(
                pmdHandler.getClass().getClassLoader(),
                new Class[] {ParameterMetaData.class},
                pmdHandler);
    }
    
    public void testFillStatementWithBean() throws SQLException {
        TestBean tb = new TestBean();
        tb.setOne("uno");
        tb.setTwo("dos");
        tb.setThree("tres");
        NoOpFillStatement fakeQueryRunner = new NoOpFillStatement();
        fakeQueryRunner.fillStatementWithBean(stmt, tb, new String[] {"three", "two", "one"});
        String[] expected = new String[] {"tres", "dos", "uno"};
        assertArrayEquals("Statement filled with incorrect parameters", expected, fakeQueryRunner.params);
    }

    private PreparedStatement fakePreparedStatement() {
        InvocationHandler noOpHandler = new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args)
                    throws Throwable {
                return null;
            }
        };
        PreparedStatement stmt = ProxyFactory.instance().createPreparedStatement(noOpHandler);
        return stmt;
    }
    
    public void testFillStatementWithBeanErrorNoReadMethod() throws Exception {
        TestBean tb = new TestBean();
        PropertyDescriptor noReadMethod = new PropertyDescriptor("one", TestBean.class, null, "setOne");
        
        PropertyDescriptor properties[] = new PropertyDescriptor[] { noReadMethod };
        try {
            runner.fillStatementWithBean(stmt, tb, properties);
            fail("Expected RuntimeException: tried to use a property with no read method");
        } catch (RuntimeException expected) {}
    }
    
    public void testFillStatementWithBeanErrorBadReadMethod() throws Exception {
        PropertyDescriptor badReadMethod = new IndexedPropertyDescriptor("indexed", getClass(), null, null, "getIndexed", null) {
            public synchronized Method getReadMethod() {
                return super.getIndexedReadMethod();
            }
        };
        PropertyDescriptor properties[] = new PropertyDescriptor[] { badReadMethod };
        try {
            runner.fillStatementWithBean(stmt, this, properties);
            fail("Expected RuntimeException: tried to use a property with no no-arg read method");
        } catch (RuntimeException expected) {}
    }
    
    public void testFillStatementWithBeanErrorReadMethodThrows() throws Exception {
        PropertyDescriptor badReadMethod = new PropertyDescriptor("throwsException", getClass(), "getThrowsException", null);
        PropertyDescriptor properties[] = new PropertyDescriptor[] { badReadMethod };
        try {
            runner.fillStatementWithBean(stmt, this, properties);
            fail("Expected RuntimeException: tried to call a method that throws");
        } catch (RuntimeException expected) {}
    }
    
    public void testFillStatementWithBeanErrorReadMethodPrivate() throws Exception {
        getPrivate();
        PropertyDescriptor badReadMethod = new BadPrivatePropertyDescriptor();
        PropertyDescriptor properties[] = new PropertyDescriptor[] { badReadMethod };
        try {
            runner.fillStatementWithBean(stmt, this, properties);
            fail("Expected RuntimeException: tried to call a private method");
        } catch (RuntimeException expected) {}
    }
    
    class BadPrivatePropertyDescriptor extends PropertyDescriptor {
        Method getPrivate;
        BadPrivatePropertyDescriptor() throws Exception {
            super("throwsException", QueryRunnerTest.class, "getThrowsException", null);
            getPrivate = QueryRunnerTest.class.getDeclaredMethod("getPrivate", new Class[0]);
        }
        
        public synchronized Method getReadMethod() {
            if (getPrivate == null) return super.getReadMethod();
            return getPrivate;
        }
    }
    
    public void testRethrowNullMessage() {
        // DBUTILS-40
        SQLException sqe = new SQLException((String)null);
        QueryRunner qr = new QueryRunner();
        try {
            qr.rethrow(sqe, "foo", new Object[] {"bar"});
            fail("rethrow didn't throw");
        } catch (SQLException expected) {}
    }
    
    // indexed bean property
    public String getIndexed(int index) {
        return null;
    }
    
    public String getThrowsException() {
        throw new RuntimeException("this getter always throws an exception");
    }
    
    private String getPrivate() {
        return null;
    }
    
    private void assertArrayEquals(String message, Object[] expected, Object[] actual) {
        assertEquals(message, Arrays.asList(expected).toString(), Arrays.asList(actual).toString());
        assertEquals(message, expected.length, actual.length);
    }
    
    
    private class NoOpFillStatement extends QueryRunner {
        Object[] params;
        public void fillStatement(PreparedStatement stmt, Object[] params)
                throws SQLException {
            this.params = params;
        }
    }

}
