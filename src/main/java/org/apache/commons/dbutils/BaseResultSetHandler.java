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

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

/**
 * Extensions of this class convert ResultSets into other objects.
 *
 * According to the <i>DRY</i> principle (Don't Repeat Yourself), repeating <code>resultSet</code>
 * variable inside the {@link ResultSetHandler#handle(ResultSet)} over and over for each iteration
 * can get a little tedious, <code>AbstractResultSetHandler</code> implicitly gives users access to
 * <code>ResultSet</code>'s methods.
 *
 * <b>NOTE</b> This class is <i>NOT</i> thread safe!
 *
 * @param <T> the target type the input ResultSet will be converted to.
 * @since 1.6
 */
public abstract class BaseResultSetHandler<T> implements ResultSetHandler<T> {

    /**
     * The adapted ResultSet.
     */
    private ResultSet rs;

    /**
     * {@inheritDoc}
     */
    @Override
    public final T handle(ResultSet rs) throws SQLException {
        if (this.rs != null) {
            throw new IllegalStateException("Re-entry not allowed!");
        }

        this.rs = rs;

        try {
            return handle();
        } finally {
            this.rs = null;
        }
    }

    /**
     * Turn the <code>ResultSet</code> into an Object.
     *
     * @return An Object initialized with <code>ResultSet</code> data
     * @throws SQLException if a database access error occurs
     * @see {@link ResultSetHandler#handle(ResultSet)}
     */
    protected abstract T handle() throws SQLException;

    /**
     * @param row
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#absolute(int)
     */
    protected final boolean absolute(int row) throws SQLException {
        return rs.absolute(row);
    }

    /**
     * @throws SQLException
     * @see java.sql.ResultSet#afterLast()
     */
    protected final void afterLast() throws SQLException {
        rs.afterLast();
    }

    /**
     * @throws SQLException
     * @see java.sql.ResultSet#beforeFirst()
     */
    protected final void beforeFirst() throws SQLException {
        rs.beforeFirst();
    }

    /**
     * @throws SQLException
     * @see java.sql.ResultSet#cancelRowUpdates()
     */
    protected final void cancelRowUpdates() throws SQLException {
        rs.cancelRowUpdates();
    }

    /**
     * @throws SQLException
     * @see java.sql.ResultSet#clearWarnings()
     */
    protected final void clearWarnings() throws SQLException {
        rs.clearWarnings();
    }

    /**
     * @throws SQLException
     * @see java.sql.ResultSet#close()
     */
    protected final void close() throws SQLException {
        rs.close();
    }

    /**
     * @throws SQLException
     * @see java.sql.ResultSet#deleteRow()
     */
    protected final void deleteRow() throws SQLException {
        rs.deleteRow();
    }

    /**
     * @param columnLabel
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#findColumn(java.lang.String)
     */
    protected final int findColumn(String columnLabel) throws SQLException {
        return rs.findColumn(columnLabel);
    }

    /**
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#first()
     */
    protected final boolean first() throws SQLException {
        return rs.first();
    }

    /**
     * @param columnIndex
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#getArray(int)
     */
    protected final Array getArray(int columnIndex) throws SQLException {
        return rs.getArray(columnIndex);
    }

    /**
     * @param columnLabel
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#getArray(java.lang.String)
     */
    protected final Array getArray(String columnLabel) throws SQLException {
        return rs.getArray(columnLabel);
    }

    /**
     * @param columnIndex
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#getAsciiStream(int)
     */
    protected final InputStream getAsciiStream(int columnIndex) throws SQLException {
        return rs.getAsciiStream(columnIndex);
    }

    /**
     * @param columnLabel
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#getAsciiStream(java.lang.String)
     */
    protected final InputStream getAsciiStream(String columnLabel) throws SQLException {
        return rs.getAsciiStream(columnLabel);
    }

    /**
     * @param columnIndex
     * @param scale
     * @return
     * @throws SQLException
     * @deprecated
     * @see java.sql.ResultSet#getBigDecimal(int, int)
     */
    @Deprecated
    protected final BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
        return rs.getBigDecimal(columnIndex, scale);
    }

    /**
     * @param columnIndex
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#getBigDecimal(int)
     */
    protected final BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        return rs.getBigDecimal(columnIndex);
    }

    /**
     * @param columnLabel
     * @param scale
     * @return
     * @throws SQLException
     * @deprecated
     * @see java.sql.ResultSet#getBigDecimal(java.lang.String, int)
     */
    @Deprecated
    protected final BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
        return rs.getBigDecimal(columnLabel, scale);
    }

    /**
     * @param columnLabel
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#getBigDecimal(java.lang.String)
     */
    protected final BigDecimal getBigDecimal(String columnLabel) throws SQLException {
        return rs.getBigDecimal(columnLabel);
    }

    /**
     * @param columnIndex
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#getBinaryStream(int)
     */
    protected final InputStream getBinaryStream(int columnIndex) throws SQLException {
        return rs.getBinaryStream(columnIndex);
    }

    /**
     * @param columnLabel
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#getBinaryStream(java.lang.String)
     */
    protected final InputStream getBinaryStream(String columnLabel) throws SQLException {
        return rs.getBinaryStream(columnLabel);
    }

    /**
     * @param columnIndex
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#getBlob(int)
     */
    protected final Blob getBlob(int columnIndex) throws SQLException {
        return rs.getBlob(columnIndex);
    }

    /**
     * @param columnLabel
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#getBlob(java.lang.String)
     */
    protected final Blob getBlob(String columnLabel) throws SQLException {
        return rs.getBlob(columnLabel);
    }

    /**
     * @param columnIndex
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#getBoolean(int)
     */
    protected final boolean getBoolean(int columnIndex) throws SQLException {
        return rs.getBoolean(columnIndex);
    }

    /**
     * @param columnLabel
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#getBoolean(java.lang.String)
     */
    protected final boolean getBoolean(String columnLabel) throws SQLException {
        return rs.getBoolean(columnLabel);
    }

    /**
     * @param columnIndex
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#getByte(int)
     */
    protected final byte getByte(int columnIndex) throws SQLException {
        return rs.getByte(columnIndex);
    }

    /**
     * @param columnLabel
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#getByte(java.lang.String)
     */
    protected final byte getByte(String columnLabel) throws SQLException {
        return rs.getByte(columnLabel);
    }

    /**
     * @param columnIndex
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#getBytes(int)
     */
    protected final byte[] getBytes(int columnIndex) throws SQLException {
        return rs.getBytes(columnIndex);
    }

    /**
     * @param columnLabel
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#getBytes(java.lang.String)
     */
    protected final byte[] getBytes(String columnLabel) throws SQLException {
        return rs.getBytes(columnLabel);
    }

    /**
     * @param columnIndex
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#getCharacterStream(int)
     */
    protected final Reader getCharacterStream(int columnIndex) throws SQLException {
        return rs.getCharacterStream(columnIndex);
    }

    /**
     * @param columnLabel
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#getCharacterStream(java.lang.String)
     */
    protected final Reader getCharacterStream(String columnLabel) throws SQLException {
        return rs.getCharacterStream(columnLabel);
    }

    /**
     * @param columnIndex
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#getClob(int)
     */
    protected final Clob getClob(int columnIndex) throws SQLException {
        return rs.getClob(columnIndex);
    }

    /**
     * @param columnLabel
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#getClob(java.lang.String)
     */
    protected final Clob getClob(String columnLabel) throws SQLException {
        return rs.getClob(columnLabel);
    }

    /**
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#getConcurrency()
     */
    protected final int getConcurrency() throws SQLException {
        return rs.getConcurrency();
    }

    /**
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#getCursorName()
     */
    protected final String getCursorName() throws SQLException {
        return rs.getCursorName();
    }

    /**
     * @param columnIndex
     * @param cal
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#getDate(int, java.util.Calendar)
     */
    protected final Date getDate(int columnIndex, Calendar cal) throws SQLException {
        return rs.getDate(columnIndex, cal);
    }

    /**
     * @param columnIndex
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#getDate(int)
     */
    protected final Date getDate(int columnIndex) throws SQLException {
        return rs.getDate(columnIndex);
    }

    /**
     * @param columnLabel
     * @param cal
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#getDate(java.lang.String, java.util.Calendar)
     */
    protected final Date getDate(String columnLabel, Calendar cal) throws SQLException {
        return rs.getDate(columnLabel, cal);
    }

    /**
     * @param columnLabel
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#getDate(java.lang.String)
     */
    protected final Date getDate(String columnLabel) throws SQLException {
        return rs.getDate(columnLabel);
    }

    /**
     * @param columnIndex
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#getDouble(int)
     */
    protected final double getDouble(int columnIndex) throws SQLException {
        return rs.getDouble(columnIndex);
    }

    /**
     * @param columnLabel
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#getDouble(java.lang.String)
     */
    protected final double getDouble(String columnLabel) throws SQLException {
        return rs.getDouble(columnLabel);
    }

    /**
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#getFetchDirection()
     */
    protected final int getFetchDirection() throws SQLException {
        return rs.getFetchDirection();
    }

    /**
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#getFetchSize()
     */
    protected final int getFetchSize() throws SQLException {
        return rs.getFetchSize();
    }

    /**
     * @param columnIndex
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#getFloat(int)
     */
    protected final float getFloat(int columnIndex) throws SQLException {
        return rs.getFloat(columnIndex);
    }

    /**
     * @param columnLabel
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#getFloat(java.lang.String)
     */
    protected final float getFloat(String columnLabel) throws SQLException {
        return rs.getFloat(columnLabel);
    }

    /**
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#getHoldability()
     */
    protected final int getHoldability() throws SQLException {
        return rs.getHoldability();
    }

    /**
     * @param columnIndex
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#getInt(int)
     */
    protected final int getInt(int columnIndex) throws SQLException {
        return rs.getInt(columnIndex);
    }

    /**
     * @param columnLabel
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#getInt(java.lang.String)
     */
    protected final int getInt(String columnLabel) throws SQLException {
        return rs.getInt(columnLabel);
    }

    /**
     * @param columnIndex
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#getLong(int)
     */
    protected final long getLong(int columnIndex) throws SQLException {
        return rs.getLong(columnIndex);
    }

    /**
     * @param columnLabel
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#getLong(java.lang.String)
     */
    protected final long getLong(String columnLabel) throws SQLException {
        return rs.getLong(columnLabel);
    }

    /**
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#getMetaData()
     */
    protected final ResultSetMetaData getMetaData() throws SQLException {
        return rs.getMetaData();
    }

    /**
     * @param columnIndex
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#getNCharacterStream(int)
     */
    protected final Reader getNCharacterStream(int columnIndex) throws SQLException {
        return rs.getNCharacterStream(columnIndex);
    }

    /**
     * @param columnLabel
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#getNCharacterStream(java.lang.String)
     */
    protected final Reader getNCharacterStream(String columnLabel) throws SQLException {
        return rs.getNCharacterStream(columnLabel);
    }

    /**
     * @param columnIndex
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#getNClob(int)
     */
    protected final NClob getNClob(int columnIndex) throws SQLException {
        return rs.getNClob(columnIndex);
    }

    /**
     * @param columnLabel
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#getNClob(java.lang.String)
     */
    protected final NClob getNClob(String columnLabel) throws SQLException {
        return rs.getNClob(columnLabel);
    }

    /**
     * @param columnIndex
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#getNString(int)
     */
    protected final String getNString(int columnIndex) throws SQLException {
        return rs.getNString(columnIndex);
    }

    /**
     * @param columnLabel
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#getNString(java.lang.String)
     */
    protected final String getNString(String columnLabel) throws SQLException {
        return rs.getNString(columnLabel);
    }

    /**
     * @param columnIndex
     * @param map
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#getObject(int, java.util.Map)
     */
    protected final Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException {
        return rs.getObject(columnIndex, map);
    }

    /**
     * @param columnIndex
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#getObject(int)
     */
    protected final Object getObject(int columnIndex) throws SQLException {
        return rs.getObject(columnIndex);
    }

    /**
     * @param columnLabel
     * @param map
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#getObject(java.lang.String, java.util.Map)
     */
    protected final Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException {
        return rs.getObject(columnLabel, map);
    }

    /**
     * @param columnLabel
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#getObject(java.lang.String)
     */
    protected final Object getObject(String columnLabel) throws SQLException {
        return rs.getObject(columnLabel);
    }

    /**
     * @param columnIndex
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#getRef(int)
     */
    protected final Ref getRef(int columnIndex) throws SQLException {
        return rs.getRef(columnIndex);
    }

    /**
     * @param columnLabel
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#getRef(java.lang.String)
     */
    protected final Ref getRef(String columnLabel) throws SQLException {
        return rs.getRef(columnLabel);
    }

    /**
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#getRow()
     */
    protected final int getRow() throws SQLException {
        return rs.getRow();
    }

    /**
     * @param columnIndex
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#getRowId(int)
     */
    protected final RowId getRowId(int columnIndex) throws SQLException {
        return rs.getRowId(columnIndex);
    }

    /**
     * @param columnLabel
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#getRowId(java.lang.String)
     */
    protected final RowId getRowId(String columnLabel) throws SQLException {
        return rs.getRowId(columnLabel);
    }

    /**
     * @param columnIndex
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#getSQLXML(int)
     */
    protected final SQLXML getSQLXML(int columnIndex) throws SQLException {
        return rs.getSQLXML(columnIndex);
    }

    /**
     * @param columnLabel
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#getSQLXML(java.lang.String)
     */
    protected final SQLXML getSQLXML(String columnLabel) throws SQLException {
        return rs.getSQLXML(columnLabel);
    }

    /**
     * @param columnIndex
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#getShort(int)
     */
    protected final short getShort(int columnIndex) throws SQLException {
        return rs.getShort(columnIndex);
    }

    /**
     * @param columnLabel
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#getShort(java.lang.String)
     */
    protected final short getShort(String columnLabel) throws SQLException {
        return rs.getShort(columnLabel);
    }

    /**
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#getStatement()
     */
    protected final Statement getStatement() throws SQLException {
        return rs.getStatement();
    }

    /**
     * @param columnIndex
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#getString(int)
     */
    protected final String getString(int columnIndex) throws SQLException {
        return rs.getString(columnIndex);
    }

    /**
     * @param columnLabel
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#getString(java.lang.String)
     */
    protected final String getString(String columnLabel) throws SQLException {
        return rs.getString(columnLabel);
    }

    /**
     * @param columnIndex
     * @param cal
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#getTime(int, java.util.Calendar)
     */
    protected final Time getTime(int columnIndex, Calendar cal) throws SQLException {
        return rs.getTime(columnIndex, cal);
    }

    /**
     * @param columnIndex
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#getTime(int)
     */
    protected final Time getTime(int columnIndex) throws SQLException {
        return rs.getTime(columnIndex);
    }

    /**
     * @param columnLabel
     * @param cal
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#getTime(java.lang.String, java.util.Calendar)
     */
    protected final Time getTime(String columnLabel, Calendar cal) throws SQLException {
        return rs.getTime(columnLabel, cal);
    }

    /**
     * @param columnLabel
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#getTime(java.lang.String)
     */
    protected final Time getTime(String columnLabel) throws SQLException {
        return rs.getTime(columnLabel);
    }

    /**
     * @param columnIndex
     * @param cal
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#getTimestamp(int, java.util.Calendar)
     */
    protected final Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
        return rs.getTimestamp(columnIndex, cal);
    }

    /**
     * @param columnIndex
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#getTimestamp(int)
     */
    protected final Timestamp getTimestamp(int columnIndex) throws SQLException {
        return rs.getTimestamp(columnIndex);
    }

    /**
     * @param columnLabel
     * @param cal
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#getTimestamp(java.lang.String, java.util.Calendar)
     */
    protected final Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
        return rs.getTimestamp(columnLabel, cal);
    }

    /**
     * @param columnLabel
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#getTimestamp(java.lang.String)
     */
    protected final Timestamp getTimestamp(String columnLabel) throws SQLException {
        return rs.getTimestamp(columnLabel);
    }

    /**
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#getType()
     */
    protected final int getType() throws SQLException {
        return rs.getType();
    }

    /**
     * @param columnIndex
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#getURL(int)
     */
    protected final URL getURL(int columnIndex) throws SQLException {
        return rs.getURL(columnIndex);
    }

    /**
     * @param columnLabel
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#getURL(java.lang.String)
     */
    protected final URL getURL(String columnLabel) throws SQLException {
        return rs.getURL(columnLabel);
    }

    /**
     * @param columnIndex
     * @return
     * @throws SQLException
     * @deprecated
     * @see java.sql.ResultSet#getUnicodeStream(int)
     */
    @Deprecated
    protected final InputStream getUnicodeStream(int columnIndex) throws SQLException {
        return rs.getUnicodeStream(columnIndex);
    }

    /**
     * @param columnLabel
     * @return
     * @throws SQLException
     * @deprecated
     * @see java.sql.ResultSet#getUnicodeStream(java.lang.String)
     */
    @Deprecated
    protected final InputStream getUnicodeStream(String columnLabel) throws SQLException {
        return rs.getUnicodeStream(columnLabel);
    }

    /**
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#getWarnings()
     */
    protected final SQLWarning getWarnings() throws SQLException {
        return rs.getWarnings();
    }

    /**
     * @throws SQLException
     * @see java.sql.ResultSet#insertRow()
     */
    protected final void insertRow() throws SQLException {
        rs.insertRow();
    }

    /**
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#isAfterLast()
     */
    protected final boolean isAfterLast() throws SQLException {
        return rs.isAfterLast();
    }

    /**
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#isBeforeFirst()
     */
    protected final boolean isBeforeFirst() throws SQLException {
        return rs.isBeforeFirst();
    }

    /**
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#isClosed()
     */
    protected final boolean isClosed() throws SQLException {
        return rs.isClosed();
    }

    /**
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#isFirst()
     */
    protected final boolean isFirst() throws SQLException {
        return rs.isFirst();
    }

    /**
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#isLast()
     */
    protected final boolean isLast() throws SQLException {
        return rs.isLast();
    }

    /**
     * @param iface
     * @return
     * @throws SQLException
     * @see java.sql.Wrapper#isWrapperFor(java.lang.Class)
     */
    protected final boolean isWrapperFor(Class<?> iface) throws SQLException {
        return rs.isWrapperFor(iface);
    }

    /**
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#last()
     */
    protected final boolean last() throws SQLException {
        return rs.last();
    }

    /**
     * @throws SQLException
     * @see java.sql.ResultSet#moveToCurrentRow()
     */
    protected final void moveToCurrentRow() throws SQLException {
        rs.moveToCurrentRow();
    }

    /**
     * @throws SQLException
     * @see java.sql.ResultSet#moveToInsertRow()
     */
    protected final void moveToInsertRow() throws SQLException {
        rs.moveToInsertRow();
    }

    /**
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#next()
     */
    protected final boolean next() throws SQLException {
        return rs.next();
    }

    /**
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#previous()
     */
    protected final boolean previous() throws SQLException {
        return rs.previous();
    }

    /**
     * @throws SQLException
     * @see java.sql.ResultSet#refreshRow()
     */
    protected final void refreshRow() throws SQLException {
        rs.refreshRow();
    }

    /**
     * @param rows
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#relative(int)
     */
    protected final boolean relative(int rows) throws SQLException {
        return rs.relative(rows);
    }

    /**
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#rowDeleted()
     */
    protected final boolean rowDeleted() throws SQLException {
        return rs.rowDeleted();
    }

    /**
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#rowInserted()
     */
    protected final boolean rowInserted() throws SQLException {
        return rs.rowInserted();
    }

    /**
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#rowUpdated()
     */
    protected final boolean rowUpdated() throws SQLException {
        return rs.rowUpdated();
    }

    /**
     * @param direction
     * @throws SQLException
     * @see java.sql.ResultSet#setFetchDirection(int)
     */
    protected final void setFetchDirection(int direction) throws SQLException {
        rs.setFetchDirection(direction);
    }

    /**
     * @param rows
     * @throws SQLException
     * @see java.sql.ResultSet#setFetchSize(int)
     */
    protected final void setFetchSize(int rows) throws SQLException {
        rs.setFetchSize(rows);
    }

    /**
     * @param iface
     * @return
     * @throws SQLException
     * @see java.sql.Wrapper#unwrap(java.lang.Class)
     */
    protected final <E> E unwrap(Class<E> iface) throws SQLException {
        return rs.unwrap(iface);
    }

    /**
     * @param columnIndex
     * @param x
     * @throws SQLException
     * @see java.sql.ResultSet#updateArray(int, java.sql.Array)
     */
    protected final void updateArray(int columnIndex, Array x) throws SQLException {
        rs.updateArray(columnIndex, x);
    }

    /**
     * @param columnLabel
     * @param x
     * @throws SQLException
     * @see java.sql.ResultSet#updateArray(java.lang.String, java.sql.Array)
     */
    protected final void updateArray(String columnLabel, Array x) throws SQLException {
        rs.updateArray(columnLabel, x);
    }

    /**
     * @param columnIndex
     * @param x
     * @param length
     * @throws SQLException
     * @see java.sql.ResultSet#updateAsciiStream(int, java.io.InputStream, int)
     */
    protected final void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
        rs.updateAsciiStream(columnIndex, x, length);
    }

    /**
     * @param columnIndex
     * @param x
     * @param length
     * @throws SQLException
     * @see java.sql.ResultSet#updateAsciiStream(int, java.io.InputStream, long)
     */
    protected final void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
        rs.updateAsciiStream(columnIndex, x, length);
    }

    /**
     * @param columnIndex
     * @param x
     * @throws SQLException
     * @see java.sql.ResultSet#updateAsciiStream(int, java.io.InputStream)
     */
    protected final void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
        rs.updateAsciiStream(columnIndex, x);
    }

    /**
     * @param columnLabel
     * @param x
     * @param length
     * @throws SQLException
     * @see java.sql.ResultSet#updateAsciiStream(java.lang.String, java.io.InputStream, int)
     */
    protected final void updateAsciiStream(String columnLabel, InputStream x, int length) throws SQLException {
        rs.updateAsciiStream(columnLabel, x, length);
    }

    /**
     * @param columnLabel
     * @param x
     * @param length
     * @throws SQLException
     * @see java.sql.ResultSet#updateAsciiStream(java.lang.String, java.io.InputStream, long)
     */
    protected final void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {
        rs.updateAsciiStream(columnLabel, x, length);
    }

    /**
     * @param columnLabel
     * @param x
     * @throws SQLException
     * @see java.sql.ResultSet#updateAsciiStream(java.lang.String, java.io.InputStream)
     */
    protected final void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
        rs.updateAsciiStream(columnLabel, x);
    }

    /**
     * @param columnIndex
     * @param x
     * @throws SQLException
     * @see java.sql.ResultSet#updateBigDecimal(int, java.math.BigDecimal)
     */
    protected final void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
        rs.updateBigDecimal(columnIndex, x);
    }

    /**
     * @param columnLabel
     * @param x
     * @throws SQLException
     * @see java.sql.ResultSet#updateBigDecimal(java.lang.String, java.math.BigDecimal)
     */
    protected final void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException {
        rs.updateBigDecimal(columnLabel, x);
    }

    /**
     * @param columnIndex
     * @param x
     * @param length
     * @throws SQLException
     * @see java.sql.ResultSet#updateBinaryStream(int, java.io.InputStream, int)
     */
    protected final void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
        rs.updateBinaryStream(columnIndex, x, length);
    }

    /**
     * @param columnIndex
     * @param x
     * @param length
     * @throws SQLException
     * @see java.sql.ResultSet#updateBinaryStream(int, java.io.InputStream, long)
     */
    protected final void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
        rs.updateBinaryStream(columnIndex, x, length);
    }

    /**
     * @param columnIndex
     * @param x
     * @throws SQLException
     * @see java.sql.ResultSet#updateBinaryStream(int, java.io.InputStream)
     */
    protected final void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
        rs.updateBinaryStream(columnIndex, x);
    }

    /**
     * @param columnLabel
     * @param x
     * @param length
     * @throws SQLException
     * @see java.sql.ResultSet#updateBinaryStream(java.lang.String, java.io.InputStream, int)
     */
    protected final void updateBinaryStream(String columnLabel, InputStream x, int length) throws SQLException {
        rs.updateBinaryStream(columnLabel, x, length);
    }

    /**
     * @param columnLabel
     * @param x
     * @param length
     * @throws SQLException
     * @see java.sql.ResultSet#updateBinaryStream(java.lang.String, java.io.InputStream, long)
     */
    protected final void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {
        rs.updateBinaryStream(columnLabel, x, length);
    }

    /**
     * @param columnLabel
     * @param x
     * @throws SQLException
     * @see java.sql.ResultSet#updateBinaryStream(java.lang.String, java.io.InputStream)
     */
    protected final void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
        rs.updateBinaryStream(columnLabel, x);
    }

    /**
     * @param columnIndex
     * @param x
     * @throws SQLException
     * @see java.sql.ResultSet#updateBlob(int, java.sql.Blob)
     */
    protected final void updateBlob(int columnIndex, Blob x) throws SQLException {
        rs.updateBlob(columnIndex, x);
    }

    /**
     * @param columnIndex
     * @param inputStream
     * @param length
     * @throws SQLException
     * @see java.sql.ResultSet#updateBlob(int, java.io.InputStream, long)
     */
    protected final void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
        rs.updateBlob(columnIndex, inputStream, length);
    }

    /**
     * @param columnIndex
     * @param inputStream
     * @throws SQLException
     * @see java.sql.ResultSet#updateBlob(int, java.io.InputStream)
     */
    protected final void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
        rs.updateBlob(columnIndex, inputStream);
    }

    /**
     * @param columnLabel
     * @param x
     * @throws SQLException
     * @see java.sql.ResultSet#updateBlob(java.lang.String, java.sql.Blob)
     */
    protected final void updateBlob(String columnLabel, Blob x) throws SQLException {
        rs.updateBlob(columnLabel, x);
    }

    /**
     * @param columnLabel
     * @param inputStream
     * @param length
     * @throws SQLException
     * @see java.sql.ResultSet#updateBlob(java.lang.String, java.io.InputStream, long)
     */
    protected final void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
        rs.updateBlob(columnLabel, inputStream, length);
    }

    /**
     * @param columnLabel
     * @param inputStream
     * @throws SQLException
     * @see java.sql.ResultSet#updateBlob(java.lang.String, java.io.InputStream)
     */
    protected final void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
        rs.updateBlob(columnLabel, inputStream);
    }

    /**
     * @param columnIndex
     * @param x
     * @throws SQLException
     * @see java.sql.ResultSet#updateBoolean(int, boolean)
     */
    protected final void updateBoolean(int columnIndex, boolean x) throws SQLException {
        rs.updateBoolean(columnIndex, x);
    }

    /**
     * @param columnLabel
     * @param x
     * @throws SQLException
     * @see java.sql.ResultSet#updateBoolean(java.lang.String, boolean)
     */
    protected final void updateBoolean(String columnLabel, boolean x) throws SQLException {
        rs.updateBoolean(columnLabel, x);
    }

    /**
     * @param columnIndex
     * @param x
     * @throws SQLException
     * @see java.sql.ResultSet#updateByte(int, byte)
     */
    protected final void updateByte(int columnIndex, byte x) throws SQLException {
        rs.updateByte(columnIndex, x);
    }

    /**
     * @param columnLabel
     * @param x
     * @throws SQLException
     * @see java.sql.ResultSet#updateByte(java.lang.String, byte)
     */
    protected final void updateByte(String columnLabel, byte x) throws SQLException {
        rs.updateByte(columnLabel, x);
    }

    /**
     * @param columnIndex
     * @param x
     * @throws SQLException
     * @see java.sql.ResultSet#updateBytes(int, byte[])
     */
    protected final void updateBytes(int columnIndex, byte[] x) throws SQLException {
        rs.updateBytes(columnIndex, x);
    }

    /**
     * @param columnLabel
     * @param x
     * @throws SQLException
     * @see java.sql.ResultSet#updateBytes(java.lang.String, byte[])
     */
    protected final void updateBytes(String columnLabel, byte[] x) throws SQLException {
        rs.updateBytes(columnLabel, x);
    }

    /**
     * @param columnIndex
     * @param x
     * @param length
     * @throws SQLException
     * @see java.sql.ResultSet#updateCharacterStream(int, java.io.Reader, int)
     */
    protected final void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
        rs.updateCharacterStream(columnIndex, x, length);
    }

    /**
     * @param columnIndex
     * @param x
     * @param length
     * @throws SQLException
     * @see java.sql.ResultSet#updateCharacterStream(int, java.io.Reader, long)
     */
    protected final void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
        rs.updateCharacterStream(columnIndex, x, length);
    }

    /**
     * @param columnIndex
     * @param x
     * @throws SQLException
     * @see java.sql.ResultSet#updateCharacterStream(int, java.io.Reader)
     */
    protected final void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
        rs.updateCharacterStream(columnIndex, x);
    }

    /**
     * @param columnLabel
     * @param reader
     * @param length
     * @throws SQLException
     * @see java.sql.ResultSet#updateCharacterStream(java.lang.String, java.io.Reader, int)
     */
    protected final void updateCharacterStream(String columnLabel, Reader reader, int length) throws SQLException {
        rs.updateCharacterStream(columnLabel, reader, length);
    }

    /**
     * @param columnLabel
     * @param reader
     * @param length
     * @throws SQLException
     * @see java.sql.ResultSet#updateCharacterStream(java.lang.String, java.io.Reader, long)
     */
    protected final void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
        rs.updateCharacterStream(columnLabel, reader, length);
    }

    /**
     * @param columnLabel
     * @param reader
     * @throws SQLException
     * @see java.sql.ResultSet#updateCharacterStream(java.lang.String, java.io.Reader)
     */
    protected final void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
        rs.updateCharacterStream(columnLabel, reader);
    }

    /**
     * @param columnIndex
     * @param x
     * @throws SQLException
     * @see java.sql.ResultSet#updateClob(int, java.sql.Clob)
     */
    protected final void updateClob(int columnIndex, Clob x) throws SQLException {
        rs.updateClob(columnIndex, x);
    }

    /**
     * @param columnIndex
     * @param reader
     * @param length
     * @throws SQLException
     * @see java.sql.ResultSet#updateClob(int, java.io.Reader, long)
     */
    protected final void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
        rs.updateClob(columnIndex, reader, length);
    }

    /**
     * @param columnIndex
     * @param reader
     * @throws SQLException
     * @see java.sql.ResultSet#updateClob(int, java.io.Reader)
     */
    protected final void updateClob(int columnIndex, Reader reader) throws SQLException {
        rs.updateClob(columnIndex, reader);
    }

    /**
     * @param columnLabel
     * @param x
     * @throws SQLException
     * @see java.sql.ResultSet#updateClob(java.lang.String, java.sql.Clob)
     */
    protected final void updateClob(String columnLabel, Clob x) throws SQLException {
        rs.updateClob(columnLabel, x);
    }

    /**
     * @param columnLabel
     * @param reader
     * @param length
     * @throws SQLException
     * @see java.sql.ResultSet#updateClob(java.lang.String, java.io.Reader, long)
     */
    protected final void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
        rs.updateClob(columnLabel, reader, length);
    }

    /**
     * @param columnLabel
     * @param reader
     * @throws SQLException
     * @see java.sql.ResultSet#updateClob(java.lang.String, java.io.Reader)
     */
    protected final void updateClob(String columnLabel, Reader reader) throws SQLException {
        rs.updateClob(columnLabel, reader);
    }

    /**
     * @param columnIndex
     * @param x
     * @throws SQLException
     * @see java.sql.ResultSet#updateDate(int, java.sql.Date)
     */
    protected final void updateDate(int columnIndex, Date x) throws SQLException {
        rs.updateDate(columnIndex, x);
    }

    /**
     * @param columnLabel
     * @param x
     * @throws SQLException
     * @see java.sql.ResultSet#updateDate(java.lang.String, java.sql.Date)
     */
    protected final void updateDate(String columnLabel, Date x) throws SQLException {
        rs.updateDate(columnLabel, x);
    }

    /**
     * @param columnIndex
     * @param x
     * @throws SQLException
     * @see java.sql.ResultSet#updateDouble(int, double)
     */
    protected final void updateDouble(int columnIndex, double x) throws SQLException {
        rs.updateDouble(columnIndex, x);
    }

    /**
     * @param columnLabel
     * @param x
     * @throws SQLException
     * @see java.sql.ResultSet#updateDouble(java.lang.String, double)
     */
    protected final void updateDouble(String columnLabel, double x) throws SQLException {
        rs.updateDouble(columnLabel, x);
    }

    /**
     * @param columnIndex
     * @param x
     * @throws SQLException
     * @see java.sql.ResultSet#updateFloat(int, float)
     */
    protected final void updateFloat(int columnIndex, float x) throws SQLException {
        rs.updateFloat(columnIndex, x);
    }

    /**
     * @param columnLabel
     * @param x
     * @throws SQLException
     * @see java.sql.ResultSet#updateFloat(java.lang.String, float)
     */
    protected final void updateFloat(String columnLabel, float x) throws SQLException {
        rs.updateFloat(columnLabel, x);
    }

    /**
     * @param columnIndex
     * @param x
     * @throws SQLException
     * @see java.sql.ResultSet#updateInt(int, int)
     */
    protected final void updateInt(int columnIndex, int x) throws SQLException {
        rs.updateInt(columnIndex, x);
    }

    /**
     * @param columnLabel
     * @param x
     * @throws SQLException
     * @see java.sql.ResultSet#updateInt(java.lang.String, int)
     */
    protected final void updateInt(String columnLabel, int x) throws SQLException {
        rs.updateInt(columnLabel, x);
    }

    /**
     * @param columnIndex
     * @param x
     * @throws SQLException
     * @see java.sql.ResultSet#updateLong(int, long)
     */
    protected final void updateLong(int columnIndex, long x) throws SQLException {
        rs.updateLong(columnIndex, x);
    }

    /**
     * @param columnLabel
     * @param x
     * @throws SQLException
     * @see java.sql.ResultSet#updateLong(java.lang.String, long)
     */
    protected final void updateLong(String columnLabel, long x) throws SQLException {
        rs.updateLong(columnLabel, x);
    }

    /**
     * @param columnIndex
     * @param x
     * @param length
     * @throws SQLException
     * @see java.sql.ResultSet#updateNCharacterStream(int, java.io.Reader, long)
     */
    protected final void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
        rs.updateNCharacterStream(columnIndex, x, length);
    }

    /**
     * @param columnIndex
     * @param x
     * @throws SQLException
     * @see java.sql.ResultSet#updateNCharacterStream(int, java.io.Reader)
     */
    protected final void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
        rs.updateNCharacterStream(columnIndex, x);
    }

    /**
     * @param columnLabel
     * @param reader
     * @param length
     * @throws SQLException
     * @see java.sql.ResultSet#updateNCharacterStream(java.lang.String, java.io.Reader, long)
     */
    protected final void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
        rs.updateNCharacterStream(columnLabel, reader, length);
    }

    /**
     * @param columnLabel
     * @param reader
     * @throws SQLException
     * @see java.sql.ResultSet#updateNCharacterStream(java.lang.String, java.io.Reader)
     */
    protected final void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {
        rs.updateNCharacterStream(columnLabel, reader);
    }

    /**
     * @param columnIndex
     * @param nClob
     * @throws SQLException
     * @see java.sql.ResultSet#updateNClob(int, java.sql.NClob)
     */
    protected final void updateNClob(int columnIndex, NClob nClob) throws SQLException {
        rs.updateNClob(columnIndex, nClob);
    }

    /**
     * @param columnIndex
     * @param reader
     * @param length
     * @throws SQLException
     * @see java.sql.ResultSet#updateNClob(int, java.io.Reader, long)
     */
    protected final void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
        rs.updateNClob(columnIndex, reader, length);
    }

    /**
     * @param columnIndex
     * @param reader
     * @throws SQLException
     * @see java.sql.ResultSet#updateNClob(int, java.io.Reader)
     */
    protected final void updateNClob(int columnIndex, Reader reader) throws SQLException {
        rs.updateNClob(columnIndex, reader);
    }

    /**
     * @param columnLabel
     * @param nClob
     * @throws SQLException
     * @see java.sql.ResultSet#updateNClob(java.lang.String, java.sql.NClob)
     */
    protected final void updateNClob(String columnLabel, NClob nClob) throws SQLException {
        rs.updateNClob(columnLabel, nClob);
    }

    /**
     * @param columnLabel
     * @param reader
     * @param length
     * @throws SQLException
     * @see java.sql.ResultSet#updateNClob(java.lang.String, java.io.Reader, long)
     */
    protected final void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
        rs.updateNClob(columnLabel, reader, length);
    }

    /**
     * @param columnLabel
     * @param reader
     * @throws SQLException
     * @see java.sql.ResultSet#updateNClob(java.lang.String, java.io.Reader)
     */
    protected final void updateNClob(String columnLabel, Reader reader) throws SQLException {
        rs.updateNClob(columnLabel, reader);
    }

    /**
     * @param columnIndex
     * @param nString
     * @throws SQLException
     * @see java.sql.ResultSet#updateNString(int, java.lang.String)
     */
    protected final void updateNString(int columnIndex, String nString) throws SQLException {
        rs.updateNString(columnIndex, nString);
    }

    /**
     * @param columnLabel
     * @param nString
     * @throws SQLException
     * @see java.sql.ResultSet#updateNString(java.lang.String, java.lang.String)
     */
    protected final void updateNString(String columnLabel, String nString) throws SQLException {
        rs.updateNString(columnLabel, nString);
    }

    /**
     * @param columnIndex
     * @throws SQLException
     * @see java.sql.ResultSet#updateNull(int)
     */
    protected final void updateNull(int columnIndex) throws SQLException {
        rs.updateNull(columnIndex);
    }

    /**
     * @param columnLabel
     * @throws SQLException
     * @see java.sql.ResultSet#updateNull(java.lang.String)
     */
    protected final void updateNull(String columnLabel) throws SQLException {
        rs.updateNull(columnLabel);
    }

    /**
     * @param columnIndex
     * @param x
     * @param scaleOrLength
     * @throws SQLException
     * @see java.sql.ResultSet#updateObject(int, java.lang.Object, int)
     */
    protected final void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException {
        rs.updateObject(columnIndex, x, scaleOrLength);
    }

    /**
     * @param columnIndex
     * @param x
     * @throws SQLException
     * @see java.sql.ResultSet#updateObject(int, java.lang.Object)
     */
    protected final void updateObject(int columnIndex, Object x) throws SQLException {
        rs.updateObject(columnIndex, x);
    }

    /**
     * @param columnLabel
     * @param x
     * @param scaleOrLength
     * @throws SQLException
     * @see java.sql.ResultSet#updateObject(java.lang.String, java.lang.Object, int)
     */
    protected final void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException {
        rs.updateObject(columnLabel, x, scaleOrLength);
    }

    /**
     * @param columnLabel
     * @param x
     * @throws SQLException
     * @see java.sql.ResultSet#updateObject(java.lang.String, java.lang.Object)
     */
    protected final void updateObject(String columnLabel, Object x) throws SQLException {
        rs.updateObject(columnLabel, x);
    }

    /**
     * @param columnIndex
     * @param x
     * @throws SQLException
     * @see java.sql.ResultSet#updateRef(int, java.sql.Ref)
     */
    protected final void updateRef(int columnIndex, Ref x) throws SQLException {
        rs.updateRef(columnIndex, x);
    }

    /**
     * @param columnLabel
     * @param x
     * @throws SQLException
     * @see java.sql.ResultSet#updateRef(java.lang.String, java.sql.Ref)
     */
    protected final void updateRef(String columnLabel, Ref x) throws SQLException {
        rs.updateRef(columnLabel, x);
    }

    /**
     * @throws SQLException
     * @see java.sql.ResultSet#updateRow()
     */
    protected final void updateRow() throws SQLException {
        rs.updateRow();
    }

    /**
     * @param columnIndex
     * @param x
     * @throws SQLException
     * @see java.sql.ResultSet#updateRowId(int, java.sql.RowId)
     */
    protected final void updateRowId(int columnIndex, RowId x) throws SQLException {
        rs.updateRowId(columnIndex, x);
    }

    /**
     * @param columnLabel
     * @param x
     * @throws SQLException
     * @see java.sql.ResultSet#updateRowId(java.lang.String, java.sql.RowId)
     */
    protected final void updateRowId(String columnLabel, RowId x) throws SQLException {
        rs.updateRowId(columnLabel, x);
    }

    /**
     * @param columnIndex
     * @param xmlObject
     * @throws SQLException
     * @see java.sql.ResultSet#updateSQLXML(int, java.sql.SQLXML)
     */
    protected final void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
        rs.updateSQLXML(columnIndex, xmlObject);
    }

    /**
     * @param columnLabel
     * @param xmlObject
     * @throws SQLException
     * @see java.sql.ResultSet#updateSQLXML(java.lang.String, java.sql.SQLXML)
     */
    protected final void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
        rs.updateSQLXML(columnLabel, xmlObject);
    }

    /**
     * @param columnIndex
     * @param x
     * @throws SQLException
     * @see java.sql.ResultSet#updateShort(int, short)
     */
    protected final void updateShort(int columnIndex, short x) throws SQLException {
        rs.updateShort(columnIndex, x);
    }

    /**
     * @param columnLabel
     * @param x
     * @throws SQLException
     * @see java.sql.ResultSet#updateShort(java.lang.String, short)
     */
    protected final void updateShort(String columnLabel, short x) throws SQLException {
        rs.updateShort(columnLabel, x);
    }

    /**
     * @param columnIndex
     * @param x
     * @throws SQLException
     * @see java.sql.ResultSet#updateString(int, java.lang.String)
     */
    protected final void updateString(int columnIndex, String x) throws SQLException {
        rs.updateString(columnIndex, x);
    }

    /**
     * @param columnLabel
     * @param x
     * @throws SQLException
     * @see java.sql.ResultSet#updateString(java.lang.String, java.lang.String)
     */
    protected final void updateString(String columnLabel, String x) throws SQLException {
        rs.updateString(columnLabel, x);
    }

    /**
     * @param columnIndex
     * @param x
     * @throws SQLException
     * @see java.sql.ResultSet#updateTime(int, java.sql.Time)
     */
    protected final void updateTime(int columnIndex, Time x) throws SQLException {
        rs.updateTime(columnIndex, x);
    }

    /**
     * @param columnLabel
     * @param x
     * @throws SQLException
     * @see java.sql.ResultSet#updateTime(java.lang.String, java.sql.Time)
     */
    protected final void updateTime(String columnLabel, Time x) throws SQLException {
        rs.updateTime(columnLabel, x);
    }

    /**
     * @param columnIndex
     * @param x
     * @throws SQLException
     * @see java.sql.ResultSet#updateTimestamp(int, java.sql.Timestamp)
     */
    protected final void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
        rs.updateTimestamp(columnIndex, x);
    }

    /**
     * @param columnLabel
     * @param x
     * @throws SQLException
     * @see java.sql.ResultSet#updateTimestamp(java.lang.String, java.sql.Timestamp)
     */
    protected final void updateTimestamp(String columnLabel, Timestamp x) throws SQLException {
        rs.updateTimestamp(columnLabel, x);
    }

    /**
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#wasNull()
     */
    protected final boolean wasNull() throws SQLException {
        return rs.wasNull();
    }

    protected final ResultSet getAdaptedResultSet() {
        return rs;
    }

}