/*
 * Copyright 2003-2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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
 * MockResultSetMetaData dynamically implements the ResultSetMetaData 
 * interface.
 */
public class MockResultSetMetaData implements InvocationHandler {

    private String[] columnNames = null;

    /**
     * Create a <code>MockResultSetMetaData</code> proxy object.  This is 
     * equivalent to:
     * <pre>
     * ProxyFactory.instance().createResultSetMetaData(new MockResultSetMetaData(columnNames));
     * </pre>
     * 
     * @param columnNames
     * @return
     */
    public static ResultSetMetaData create(String[] columnNames) {
        return ProxyFactory.instance().createResultSetMetaData(
            new MockResultSetMetaData(columnNames));
    }

    public MockResultSetMetaData(String[] columnNames) {
        super();
        this.columnNames = columnNames;

    }

    public Object invoke(Object proxy, Method method, Object[] args)
        throws Throwable {

        String methodName = method.getName();

        if (methodName.equals("getColumnCount")) {
            return new Integer(this.columnNames.length);

        } else if (
            methodName.equals("getColumnName")
                || methodName.equals("getColumnLabel")) {

            int col = ((Integer) args[0]).intValue() - 1;
            return this.columnNames[col];

            // stub out other methods for now
        } else {
            Class returnType = method.getReturnType();

            if (returnType.equals(String.class)) {
                return "";

            } else if (returnType.equals(Integer.TYPE)) {
                return new Integer(0);

            } else {
                return Boolean.FALSE;
            }
        }
    }
}