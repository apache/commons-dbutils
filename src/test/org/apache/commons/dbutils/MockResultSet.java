/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//dbutils/src/test/org/apache/commons/dbutils/MockResultSet.java,v 1.2 2003/11/28 21:07:38 dgraham Exp $
 * $Revision: 1.2 $
 * $Date: 2003/11/28 21:07:38 $
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
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

/**
 * MockResultSet dynamically implements the ResultSet interface.
 *  
 * @author David Graham
 */
public class MockResultSet implements InvocationHandler {

    /**
     * Create a <code>MockResultSet</code> proxy object.  This is equivalent to:
     * <pre>
     * ProxyFactory.instance().createResultSet(new MockResultSet(metaData, rows));
     * </pre>
     * 
     * @param metaData
     * @param rows A null value indicates an empty <code>ResultSet</code>.
     * @return
     */
    public static ResultSet create(
        ResultSetMetaData metaData,
        Object[][] rows) {

        return ProxyFactory.instance().createResultSet(
            new MockResultSet(metaData, rows));
    }

    private Object[] currentRow = null;

    private Iterator iter = null;

    private ResultSetMetaData metaData = null;

    private Boolean wasNull = Boolean.FALSE;

    /**
     * MockResultSet constructor.
     * @param metaData
     * @param rows A null value indicates an empty <code>ResultSet</code>.
     */
    public MockResultSet(ResultSetMetaData metaData, Object[][] rows) {
        super();
        this.metaData = metaData;
        this.iter =
            (rows == null)
                ? Collections.EMPTY_LIST.iterator()
                : Arrays.asList(rows).iterator();
    }

    /**
     * The get* methods can have an int column index or a String column name as
     * the parameter.  This method handles both cases and returns the column
     * index that the client is trying to get at.
     * @param args
     * @return A column index.
     * @throws SQLException
     */
    private int columnIndex(Object[] args) throws SQLException {

        if (args[0] instanceof Integer) {
            return ((Integer) args[0]).intValue();

        } else if (args[0] instanceof String) {
            return this.columnNameToIndex((String) args[0]);

        } else {
            throw new SQLException(args[0] + " must be Integer or String");
        }
    }

    /**
     * Returns the column index for the given column name.
     * @return A 1 based index
     * @throws SQLException if the column name is invalid
     */
    private int columnNameToIndex(String columnName) throws SQLException {
        for (int i = 0; i < this.currentRow.length; i++) {
            int c = i + 1;
            if (this.metaData.getColumnName(c).equalsIgnoreCase(columnName)) {
                return c;
            }
        }

        throw new SQLException(columnName + " is not a valid column name.");
    }

    /**
     * Gets the boolean value at the given column index.
     * @param columnIndex A 1 based index.
     * @throws SQLException
     */
    protected Object getBoolean(int columnIndex) throws SQLException {
        Object obj = this.currentRow[columnIndex - 1];
        this.setWasNull(obj);

        try {
            return (obj == null)
                ? Boolean.FALSE
                : Boolean.valueOf(obj.toString());

        } catch (NumberFormatException e) {
            throw new SQLException(e.getMessage());
        }
    }

    /**
     * Gets the byte value at the given column index.
     * @param columnIndex A 1 based index.
     * @throws SQLException
     */
    protected Object getByte(int columnIndex) throws SQLException {
        Object obj = this.currentRow[columnIndex - 1];
        this.setWasNull(obj);

        try {
            return (obj == null)
                ? new Byte((byte) 0)
                : Byte.valueOf(obj.toString());

        } catch (NumberFormatException e) {
            throw new SQLException(e.getMessage());
        }
    }

    /**
     * Gets the double value at the given column index.
     * @param columnIndex A 1 based index.
     * @throws SQLException
     */
    protected Object getDouble(int columnIndex) throws SQLException {
        Object obj = this.currentRow[columnIndex - 1];
        this.setWasNull(obj);

        try {
            return (obj == null)
                ? new Double(0)
                : Double.valueOf(obj.toString());

        } catch (NumberFormatException e) {
            throw new SQLException(e.getMessage());
        }
    }

    /**
     * Gets the float value at the given column index.
     * @param columnIndex A 1 based index.
     * @throws SQLException
     */
    protected Object getFloat(int columnIndex) throws SQLException {
        Object obj = this.currentRow[columnIndex - 1];
        this.setWasNull(obj);

        try {
            return (obj == null) ? new Float(0) : Float.valueOf(obj.toString());

        } catch (NumberFormatException e) {
            throw new SQLException(e.getMessage());
        }
    }

    /**
     * Gets the int value at the given column index.
     * @param columnIndex A 1 based index.
     * @throws SQLException
     */
    protected Object getInt(int columnIndex) throws SQLException {
        Object obj = this.currentRow[columnIndex - 1];
        this.setWasNull(obj);

        try {
            return (obj == null)
                ? new Integer(0)
                : Integer.valueOf(obj.toString());

        } catch (NumberFormatException e) {
            throw new SQLException(e.getMessage());
        }
    }

    /**
     * Gets the long value at the given column index.
     * @param columnIndex A 1 based index.
     * @throws SQLException
     */
    protected Object getLong(int columnIndex) throws SQLException {
        Object obj = this.currentRow[columnIndex - 1];
        this.setWasNull(obj);

        try {
            return (obj == null) ? new Long(0) : Long.valueOf(obj.toString());

        } catch (NumberFormatException e) {
            throw new SQLException(e.getMessage());
        }
    }

    protected ResultSetMetaData getMetaData() throws SQLException {
        return this.metaData;
    }

    /**
     * Gets the object at the given column index.
     * @param columnIndex A 1 based index.
     * @throws SQLException
     */
    protected Object getObject(int columnIndex) throws SQLException {
        Object obj = this.currentRow[columnIndex - 1];
        this.setWasNull(obj);
        return obj;
    }

    /**
     * Gets the short value at the given column index.
     * @param columnIndex A 1 based index.
     * @throws SQLException
     */
    protected Object getShort(int columnIndex) throws SQLException {
        Object obj = this.currentRow[columnIndex - 1];
        this.setWasNull(obj);

        try {
            return (obj == null)
                ? new Short((short) 0)
                : Short.valueOf(obj.toString());

        } catch (NumberFormatException e) {
            throw new SQLException(e.getMessage());
        }
    }

    /**
     * Gets the String at the given column index.
     * @param columnIndex A 1 based index.
     * @throws SQLException
     */
    protected String getString(int columnIndex) throws SQLException {
        Object obj = this.getObject(columnIndex);
        this.setWasNull(obj);
        return (obj == null) ? null : obj.toString();
    }

    public Object invoke(Object proxy, Method method, Object[] args)
        throws Throwable {

        String methodName = method.getName();

        if (methodName.equals("getMetaData")) {
            return this.getMetaData();

        } else if (methodName.equals("next")) {
            return this.next();

        } else if (methodName.equals("previous")) {

        } else if (methodName.equals("close")) {

        } else if (methodName.equals("getBoolean")) {
            return this.getBoolean(columnIndex(args));

        } else if (methodName.equals("getByte")) {
            return this.getByte(columnIndex(args));

        } else if (methodName.equals("getDouble")) {
            return this.getDouble(columnIndex(args));

        } else if (methodName.equals("getFloat")) {
            return this.getFloat(columnIndex(args));

        } else if (methodName.equals("getInt")) {
            return this.getInt(columnIndex(args));

        } else if (methodName.equals("getLong")) {
            return this.getLong(columnIndex(args));

        } else if (methodName.equals("getObject")) {
            return this.getObject(columnIndex(args));

        } else if (methodName.equals("getShort")) {
            return this.getShort(columnIndex(args));

        } else if (methodName.equals("getString")) {
            return this.getString(columnIndex(args));

        } else if (methodName.equals("wasNull")) {
            return this.wasNull();

        } else if (methodName.equals("isLast")) {
            return this.isLast();
        }

        return null;
    }

    protected Boolean isLast() throws SQLException {
        return this.iter.hasNext() ? Boolean.FALSE : Boolean.TRUE;
    }

    protected Boolean next() throws SQLException {
        if (!this.iter.hasNext()) {
            return Boolean.FALSE;
        } else {
            this.currentRow = (Object[]) iter.next();
            return Boolean.TRUE;
        }
    }

    /**
     * Assigns this.wasNull a Boolean value based on the object passed in.
     * @param isNull
     */
    private void setWasNull(Object isNull) {
        this.wasNull = (isNull == null) ? Boolean.TRUE : Boolean.FALSE;
    }

    protected Boolean wasNull() throws SQLException {
        return this.wasNull;
    }
}