/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//dbutils/src/test/org/apache/commons/dbutils/MockResultSet.java,v 1.1 2003/11/02 19:15:23 dgraham Exp $
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

	private ResultSetMetaData metaData = null;

	private Iterator iter = null;

	private Object[] currentRow = null;

	private Boolean wasNull = Boolean.FALSE;

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

	public Object invoke(Object proxy, Method method, Object[] args)
		throws Throwable {

		String methodName = method.getName();

		if (methodName.equals("getMetaData")) {
			return this.getMetaData();

		} else if (methodName.equals("next")) {
			return this.next();

		} else if (methodName.equals("previous")) {

		} else if (methodName.equals("close")) {

		} else if (methodName.equals("getObject")) {

			if (args[0] instanceof Integer) {
				int col = ((Integer) args[0]).intValue();
				return this.getObject(col);

			} else if (args[0] instanceof String) {
				return this.getObject((String) args[0]);
			}

		} else if (methodName.equals("getString")) {

			if (args[0] instanceof Integer) {
				int col = ((Integer) args[0]).intValue();
				return this.getString(col);

			} else if (args[0] instanceof String) {
				return this.getString((String) args[0]);
			}

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

	/**
	 * Gets the object at the given column index.
	 * @param columnIndex A 1 based index.
	 * @throws SQLException
	 */
	protected Object getObject(int columnIndex) throws SQLException {
		Object obj = this.currentRow[columnIndex - 1];
		if (obj == null) {
			this.wasNull = (obj == null) ? Boolean.TRUE : Boolean.FALSE;
		}

		return obj;
	}

	protected Object getObject(String columnName) throws SQLException {
		return this.getObject(this.findColumnIndex(columnName));
	}

	/**
	 * Returns the column index for the given column name.
	 * @return A 1 based index
	 * @throws SQLException if the column name is invalid
	 */
	private int findColumnIndex(String columnName) throws SQLException {
		for (int i = 0; i < this.currentRow.length; i++) {
			int c = i + 1;
			if (this.metaData.getColumnName(c).equalsIgnoreCase(columnName)) {
				return c;
			}
		}

		throw new SQLException(columnName + " is not a valid column name.");
	}

	/**
	 * Gets the String at the given column index.
	 * @param columnIndex A 1 based index.
	 * @throws SQLException
	 */
	protected String getString(int columnIndex) throws SQLException {
		Object obj = this.getObject(columnIndex);
		return (obj == null) ? null : obj.toString();
	}

	protected String getString(String columnName) throws SQLException {
		Object obj = this.getObject(this.findColumnIndex(columnName));
		return (obj == null) ? null : obj.toString();
	}

	protected Boolean next() throws SQLException {
		if (!this.iter.hasNext()) {
			return Boolean.FALSE;
		} else {
			this.currentRow = (Object[]) iter.next();
			return Boolean.TRUE;
		}
	}

	protected ResultSetMetaData getMetaData() throws SQLException {
		return this.metaData;
	}

	protected Boolean wasNull() throws SQLException {
		return this.wasNull;
	}
}