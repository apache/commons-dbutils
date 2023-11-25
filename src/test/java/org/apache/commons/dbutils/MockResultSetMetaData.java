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
import java.sql.ResultSetMetaData;

/**
 * MockResultSetMetaData dynamically implements the ResultSetMetaData interface.
 */
public class MockResultSetMetaData implements InvocationHandler {

    /**
     * Create a {@code MockResultSetMetaData} proxy object. This is equivalent to:
     *
     * <pre>
     * ProxyFactory.instance().createResultSetMetaData(new MockResultSetMetaData(columnNames));
     * </pre>
     *
     * @param columnNames
     * @return the proxy object
     */
    public static ResultSetMetaData create(final String[] columnNames) {
        return ProxyFactory.instance().createResultSetMetaData(new MockResultSetMetaData(columnNames));
    }

    private String[] columnNames;

    private String[] columnLabels;

    public MockResultSetMetaData(final String[] columnNames) {
        this.columnNames = columnNames;
        this.columnLabels = new String[columnNames.length];

    }

    public MockResultSetMetaData(final String[] columnNames, final String[] columnLabels) {
        this.columnNames = columnNames;
        this.columnLabels = columnLabels;
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {

        final String methodName = method.getName();

        if (methodName.equals("getColumnCount")) {
            return Integer.valueOf(this.columnNames.length);

        }
        if (methodName.equals("getColumnName")) {

            final int col = ((Integer) args[0]).intValue() - 1;
            return this.columnNames[col];

        }
        if (methodName.equals("getColumnLabel")) {

            final int col = ((Integer) args[0]).intValue() - 1;
            return this.columnLabels[col];

        }
        if (methodName.equals("hashCode")) {
            return Integer.valueOf(System.identityHashCode(proxy));

        }
        if (methodName.equals("toString")) {
            return "MockResultSetMetaData " + System.identityHashCode(proxy);

        }
        if (methodName.equals("equals")) {
            return Boolean.valueOf(proxy == args[0]);

        }
        throw new UnsupportedOperationException("Unsupported method: " + methodName);
    }
}
