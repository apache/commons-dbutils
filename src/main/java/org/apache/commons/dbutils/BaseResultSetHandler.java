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
import java.sql.SQLType;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

/**
 * Extensions of this class convert ResultSets into other objects.
 * <p>
 * According to the <em>DRY</em> principle (Don't Repeat Yourself), repeating {@code resultSet} variable inside the {@link ResultSetHandler#handle(ResultSet)}
 * over and over for each iteration can get a little tedious, {@code AbstractResultSetHandler} implicitly gives users access to {@code ResultSet}'s methods.
 * </p>
 * <p>
 * <strong>NOTE</strong> This class is <em>NOT</em> thread safe!
 * </p>
 *
 * @param <T> the target type the input ResultSet will be converted to.
 * @since 1.6
 * @since 1.9.0 implements {@link ResultSet}.
 */
public abstract class BaseResultSetHandler<T> implements ResultSetHandler<T>, ResultSet {

    /**
     * The adapted ResultSet.
     */
    private ResultSet resultSet;

    /**
     * Constructs a new instance.
     */
    public BaseResultSetHandler() {
        // empty
    }

    @Override
    public final boolean absolute(final int row) throws SQLException {
        return resultSet.absolute(row);
    }

    @Override
    public final void afterLast() throws SQLException {
        resultSet.afterLast();
    }

    @Override
    public final void beforeFirst() throws SQLException {
        resultSet.beforeFirst();
    }

    @Override
    public final void cancelRowUpdates() throws SQLException {
        resultSet.cancelRowUpdates();
    }

    @Override
    public final void clearWarnings() throws SQLException {
        resultSet.clearWarnings();
    }

    @Override
    public final void close() throws SQLException {
        resultSet.close();
    }

    @Override
    public final void deleteRow() throws SQLException {
        resultSet.deleteRow();
    }

    @Override
    public final int findColumn(final String columnLabel) throws SQLException {
        return resultSet.findColumn(columnLabel);
    }

    @Override
    public final boolean first() throws SQLException {
        return resultSet.first();
    }

    /**
     * Gets the underlying result set.
     *
     * @return the underlying result set.
     */
    protected final ResultSet getAdaptedResultSet() {
        return resultSet;
    }

    @Override
    public final Array getArray(final int columnIndex) throws SQLException {
        return resultSet.getArray(columnIndex);
    }

    @Override
    public final Array getArray(final String columnLabel) throws SQLException {
        return resultSet.getArray(columnLabel);
    }

    @Override
    public final InputStream getAsciiStream(final int columnIndex) throws SQLException {
        return resultSet.getAsciiStream(columnIndex);
    }

    @Override
    public final InputStream getAsciiStream(final String columnLabel) throws SQLException {
        return resultSet.getAsciiStream(columnLabel);
    }

    @Override
    public final BigDecimal getBigDecimal(final int columnIndex) throws SQLException {
        return resultSet.getBigDecimal(columnIndex);
    }

    @Override
    @Deprecated
    public final BigDecimal getBigDecimal(final int columnIndex, final int scale) throws SQLException {
        return resultSet.getBigDecimal(columnIndex, scale);
    }

    @Override
    public final BigDecimal getBigDecimal(final String columnLabel) throws SQLException {
        return resultSet.getBigDecimal(columnLabel);
    }

    @Override
    @Deprecated
    public final BigDecimal getBigDecimal(final String columnLabel, final int scale) throws SQLException {
        return resultSet.getBigDecimal(columnLabel, scale);
    }

    @Override
    public final InputStream getBinaryStream(final int columnIndex) throws SQLException {
        return resultSet.getBinaryStream(columnIndex);
    }

    @Override
    public final InputStream getBinaryStream(final String columnLabel) throws SQLException {
        return resultSet.getBinaryStream(columnLabel);
    }

    @Override
    public final Blob getBlob(final int columnIndex) throws SQLException {
        return resultSet.getBlob(columnIndex);
    }

    @Override
    public final Blob getBlob(final String columnLabel) throws SQLException {
        return resultSet.getBlob(columnLabel);
    }

    @Override
    public final boolean getBoolean(final int columnIndex) throws SQLException {
        return resultSet.getBoolean(columnIndex);
    }

    @Override
    public final boolean getBoolean(final String columnLabel) throws SQLException {
        return resultSet.getBoolean(columnLabel);
    }

    @Override
    public final byte getByte(final int columnIndex) throws SQLException {
        return resultSet.getByte(columnIndex);
    }

    @Override
    public final byte getByte(final String columnLabel) throws SQLException {
        return resultSet.getByte(columnLabel);
    }

    @Override
    public final byte[] getBytes(final int columnIndex) throws SQLException {
        return resultSet.getBytes(columnIndex);
    }

    @Override
    public final byte[] getBytes(final String columnLabel) throws SQLException {
        return resultSet.getBytes(columnLabel);
    }

    @Override
    public final Reader getCharacterStream(final int columnIndex) throws SQLException {
        return resultSet.getCharacterStream(columnIndex);
    }

    @Override
    public final Reader getCharacterStream(final String columnLabel) throws SQLException {
        return resultSet.getCharacterStream(columnLabel);
    }

    @Override
    public final Clob getClob(final int columnIndex) throws SQLException {
        return resultSet.getClob(columnIndex);
    }

    @Override
    public final Clob getClob(final String columnLabel) throws SQLException {
        return resultSet.getClob(columnLabel);
    }

    @Override
    public final int getConcurrency() throws SQLException {
        return resultSet.getConcurrency();
    }

    @Override
    public final String getCursorName() throws SQLException {
        return resultSet.getCursorName();
    }

    @Override
    public final Date getDate(final int columnIndex) throws SQLException {
        return resultSet.getDate(columnIndex);
    }

    @Override
    public final Date getDate(final int columnIndex, final Calendar cal) throws SQLException {
        return resultSet.getDate(columnIndex, cal);
    }

    @Override
    public final Date getDate(final String columnLabel) throws SQLException {
        return resultSet.getDate(columnLabel);
    }

    @Override
    public final Date getDate(final String columnLabel, final Calendar cal) throws SQLException {
        return resultSet.getDate(columnLabel, cal);
    }

    @Override
    public final double getDouble(final int columnIndex) throws SQLException {
        return resultSet.getDouble(columnIndex);
    }

    @Override
    public final double getDouble(final String columnLabel) throws SQLException {
        return resultSet.getDouble(columnLabel);
    }

    @Override
    public final int getFetchDirection() throws SQLException {
        return resultSet.getFetchDirection();
    }

    @Override
    public final int getFetchSize() throws SQLException {
        return resultSet.getFetchSize();
    }

    @Override
    public final float getFloat(final int columnIndex) throws SQLException {
        return resultSet.getFloat(columnIndex);
    }

    @Override
    public final float getFloat(final String columnLabel) throws SQLException {
        return resultSet.getFloat(columnLabel);
    }

    @Override
    public final int getHoldability() throws SQLException {
        return resultSet.getHoldability();
    }

    @Override
    public final int getInt(final int columnIndex) throws SQLException {
        return resultSet.getInt(columnIndex);
    }

    @Override
    public final int getInt(final String columnLabel) throws SQLException {
        return resultSet.getInt(columnLabel);
    }

    @Override
    public final long getLong(final int columnIndex) throws SQLException {
        return resultSet.getLong(columnIndex);
    }

    @Override
    public final long getLong(final String columnLabel) throws SQLException {
        return resultSet.getLong(columnLabel);
    }

    @Override
    public final ResultSetMetaData getMetaData() throws SQLException {
        return resultSet.getMetaData();
    }

    @Override
    public final Reader getNCharacterStream(final int columnIndex) throws SQLException {
        return resultSet.getNCharacterStream(columnIndex);
    }

    @Override
    public final Reader getNCharacterStream(final String columnLabel) throws SQLException {
        return resultSet.getNCharacterStream(columnLabel);
    }

    @Override
    public final NClob getNClob(final int columnIndex) throws SQLException {
        return resultSet.getNClob(columnIndex);
    }

    @Override
    public final NClob getNClob(final String columnLabel) throws SQLException {
        return resultSet.getNClob(columnLabel);
    }

    @Override
    public final String getNString(final int columnIndex) throws SQLException {
        return resultSet.getNString(columnIndex);
    }

    @Override
    public final String getNString(final String columnLabel) throws SQLException {
        return resultSet.getNString(columnLabel);
    }

    @Override
    public final Object getObject(final int columnIndex) throws SQLException {
        return resultSet.getObject(columnIndex);
    }

    @Override
    public <R> R getObject(int columnIndex, Class<R> type) throws SQLException {
        return resultSet.getObject(columnIndex, type);
    }

    @Override
    public final Object getObject(final int columnIndex, final Map<String, Class<?>> map) throws SQLException {
        return resultSet.getObject(columnIndex, map);
    }

    @Override
    public final Object getObject(final String columnLabel) throws SQLException {
        return resultSet.getObject(columnLabel);
    }

    @Override
    public <R> R getObject(String columnLabel, Class<R> type) throws SQLException {
        return resultSet.getObject(columnLabel, type);
    }

    @Override
    public final Object getObject(final String columnLabel, final Map<String, Class<?>> map) throws SQLException {
        return resultSet.getObject(columnLabel, map);
    }

    @Override
    public final Ref getRef(final int columnIndex) throws SQLException {
        return resultSet.getRef(columnIndex);
    }

    @Override
    public final Ref getRef(final String columnLabel) throws SQLException {
        return resultSet.getRef(columnLabel);
    }

    @Override
    public final int getRow() throws SQLException {
        return resultSet.getRow();
    }

    @Override
    public final RowId getRowId(final int columnIndex) throws SQLException {
        return resultSet.getRowId(columnIndex);
    }

    @Override
    public final RowId getRowId(final String columnLabel) throws SQLException {
        return resultSet.getRowId(columnLabel);
    }

    @Override
    public final short getShort(final int columnIndex) throws SQLException {
        return resultSet.getShort(columnIndex);
    }

    @Override
    public final short getShort(final String columnLabel) throws SQLException {
        return resultSet.getShort(columnLabel);
    }

    @Override
    public final SQLXML getSQLXML(final int columnIndex) throws SQLException {
        return resultSet.getSQLXML(columnIndex);
    }

    @Override
    public final SQLXML getSQLXML(final String columnLabel) throws SQLException {
        return resultSet.getSQLXML(columnLabel);
    }

    @Override
    public final Statement getStatement() throws SQLException {
        return resultSet.getStatement();
    }

    @Override
    public final String getString(final int columnIndex) throws SQLException {
        return resultSet.getString(columnIndex);
    }

    @Override
    public final String getString(final String columnLabel) throws SQLException {
        return resultSet.getString(columnLabel);
    }

    @Override
    public final Time getTime(final int columnIndex) throws SQLException {
        return resultSet.getTime(columnIndex);
    }

    @Override
    public final Time getTime(final int columnIndex, final Calendar cal) throws SQLException {
        return resultSet.getTime(columnIndex, cal);
    }

    @Override
    public final Time getTime(final String columnLabel) throws SQLException {
        return resultSet.getTime(columnLabel);
    }

    @Override
    public final Time getTime(final String columnLabel, final Calendar cal) throws SQLException {
        return resultSet.getTime(columnLabel, cal);
    }

    @Override
    public final Timestamp getTimestamp(final int columnIndex) throws SQLException {
        return resultSet.getTimestamp(columnIndex);
    }

    @Override
    public final Timestamp getTimestamp(final int columnIndex, final Calendar cal) throws SQLException {
        return resultSet.getTimestamp(columnIndex, cal);
    }

    @Override
    public final Timestamp getTimestamp(final String columnLabel) throws SQLException {
        return resultSet.getTimestamp(columnLabel);
    }

    @Override
    public final Timestamp getTimestamp(final String columnLabel, final Calendar cal) throws SQLException {
        return resultSet.getTimestamp(columnLabel, cal);
    }

    @Override
    public final int getType() throws SQLException {
        return resultSet.getType();
    }

    @Override
    @Deprecated
    public final InputStream getUnicodeStream(final int columnIndex) throws SQLException {
        return resultSet.getUnicodeStream(columnIndex);
    }

    @Override
    @Deprecated
    public final InputStream getUnicodeStream(final String columnLabel) throws SQLException {
        return resultSet.getUnicodeStream(columnLabel);
    }

    @Override
    public final URL getURL(final int columnIndex) throws SQLException {
        return resultSet.getURL(columnIndex);
    }

    @Override
    public final URL getURL(final String columnLabel) throws SQLException {
        return resultSet.getURL(columnLabel);
    }

    @Override
    public final SQLWarning getWarnings() throws SQLException {
        return resultSet.getWarnings();
    }

    /**
     * Turn the {@code ResultSet} into an Object.
     *
     * @return An Object initialized with {@code ResultSet} data
     * @throws SQLException if a database access error occurs
     * @see ResultSetHandler#handle(ResultSet)
     */
    protected abstract T handle() throws SQLException;

    @Override
    public final T handle(final ResultSet resultSet) throws SQLException {
        if (this.resultSet != null) {
            throw new IllegalStateException("Re-entry not allowed!");
        }
        this.resultSet = resultSet;
        try {
            return handle();
        } finally {
            this.resultSet = null;
        }
    }

    @Override
    public final void insertRow() throws SQLException {
        resultSet.insertRow();
    }

    @Override
    public final boolean isAfterLast() throws SQLException {
        return resultSet.isAfterLast();
    }

    @Override
    public final boolean isBeforeFirst() throws SQLException {
        return resultSet.isBeforeFirst();
    }

    @Override
    public final boolean isClosed() throws SQLException {
        return resultSet.isClosed();
    }

    @Override
    public final boolean isFirst() throws SQLException {
        return resultSet.isFirst();
    }

    @Override
    public final boolean isLast() throws SQLException {
        return resultSet.isLast();
    }

    @Override
    public final boolean isWrapperFor(final Class<?> iface) throws SQLException {
        return resultSet.isWrapperFor(iface);
    }

    @Override
    public final boolean last() throws SQLException {
        return resultSet.last();
    }

    @Override
    public final void moveToCurrentRow() throws SQLException {
        resultSet.moveToCurrentRow();
    }

    @Override
    public final void moveToInsertRow() throws SQLException {
        resultSet.moveToInsertRow();
    }

    @Override
    public final boolean next() throws SQLException {
        return resultSet.next();
    }

    @Override
    public final boolean previous() throws SQLException {
        return resultSet.previous();
    }

    @Override
    public final void refreshRow() throws SQLException {
        resultSet.refreshRow();
    }

    @Override
    public final boolean relative(final int rows) throws SQLException {
        return resultSet.relative(rows);
    }

    @Override
    public final boolean rowDeleted() throws SQLException {
        return resultSet.rowDeleted();
    }

    @Override
    public final boolean rowInserted() throws SQLException {
        return resultSet.rowInserted();
    }

    @Override
    public final boolean rowUpdated() throws SQLException {
        return resultSet.rowUpdated();
    }

    @Override
    public final void setFetchDirection(final int direction) throws SQLException {
        resultSet.setFetchDirection(direction);
    }

    @Override
    public final void setFetchSize(final int rows) throws SQLException {
        resultSet.setFetchSize(rows);
    }

    @Override
    public final <E> E unwrap(final Class<E> iface) throws SQLException {
        return resultSet.unwrap(iface);
    }

    @Override
    public final void updateArray(final int columnIndex, final Array x) throws SQLException {
        resultSet.updateArray(columnIndex, x);
    }

    @Override
    public final void updateArray(final String columnLabel, final Array x) throws SQLException {
        resultSet.updateArray(columnLabel, x);
    }

    @Override
    public final void updateAsciiStream(final int columnIndex, final InputStream x) throws SQLException {
        resultSet.updateAsciiStream(columnIndex, x);
    }

    @Override
    public final void updateAsciiStream(final int columnIndex, final InputStream x, final int length) throws SQLException {
        resultSet.updateAsciiStream(columnIndex, x, length);
    }

    @Override
    public final void updateAsciiStream(final int columnIndex, final InputStream x, final long length) throws SQLException {
        resultSet.updateAsciiStream(columnIndex, x, length);
    }

    @Override
    public final void updateAsciiStream(final String columnLabel, final InputStream x) throws SQLException {
        resultSet.updateAsciiStream(columnLabel, x);
    }

    @Override
    public final void updateAsciiStream(final String columnLabel, final InputStream x, final int length) throws SQLException {
        resultSet.updateAsciiStream(columnLabel, x, length);
    }

    @Override
    public final void updateAsciiStream(final String columnLabel, final InputStream x, final long length) throws SQLException {
        resultSet.updateAsciiStream(columnLabel, x, length);
    }

    @Override
    public final void updateBigDecimal(final int columnIndex, final BigDecimal x) throws SQLException {
        resultSet.updateBigDecimal(columnIndex, x);
    }

    @Override
    public final void updateBigDecimal(final String columnLabel, final BigDecimal x) throws SQLException {
        resultSet.updateBigDecimal(columnLabel, x);
    }

    @Override
    public final void updateBinaryStream(final int columnIndex, final InputStream x) throws SQLException {
        resultSet.updateBinaryStream(columnIndex, x);
    }

    @Override
    public final void updateBinaryStream(final int columnIndex, final InputStream x, final int length) throws SQLException {
        resultSet.updateBinaryStream(columnIndex, x, length);
    }

    @Override
    public final void updateBinaryStream(final int columnIndex, final InputStream x, final long length) throws SQLException {
        resultSet.updateBinaryStream(columnIndex, x, length);
    }

    @Override
    public final void updateBinaryStream(final String columnLabel, final InputStream x) throws SQLException {
        resultSet.updateBinaryStream(columnLabel, x);
    }

    @Override
    public final void updateBinaryStream(final String columnLabel, final InputStream x, final int length) throws SQLException {
        resultSet.updateBinaryStream(columnLabel, x, length);
    }

    @Override
    public final void updateBinaryStream(final String columnLabel, final InputStream x, final long length) throws SQLException {
        resultSet.updateBinaryStream(columnLabel, x, length);
    }

    @Override
    public final void updateBlob(final int columnIndex, final Blob x) throws SQLException {
        resultSet.updateBlob(columnIndex, x);
    }

    @Override
    public final void updateBlob(final int columnIndex, final InputStream inputStream) throws SQLException {
        resultSet.updateBlob(columnIndex, inputStream);
    }

    @Override
    public final void updateBlob(final int columnIndex, final InputStream inputStream, final long length) throws SQLException {
        resultSet.updateBlob(columnIndex, inputStream, length);
    }

    @Override
    public final void updateBlob(final String columnLabel, final Blob x) throws SQLException {
        resultSet.updateBlob(columnLabel, x);
    }

    @Override
    public final void updateBlob(final String columnLabel, final InputStream inputStream) throws SQLException {
        resultSet.updateBlob(columnLabel, inputStream);
    }

    @Override
    public final void updateBlob(final String columnLabel, final InputStream inputStream, final long length) throws SQLException {
        resultSet.updateBlob(columnLabel, inputStream, length);
    }

    @Override
    public final void updateBoolean(final int columnIndex, final boolean x) throws SQLException {
        resultSet.updateBoolean(columnIndex, x);
    }

    @Override
    public final void updateBoolean(final String columnLabel, final boolean x) throws SQLException {
        resultSet.updateBoolean(columnLabel, x);
    }

    @Override
    public final void updateByte(final int columnIndex, final byte x) throws SQLException {
        resultSet.updateByte(columnIndex, x);
    }

    @Override
    public final void updateByte(final String columnLabel, final byte x) throws SQLException {
        resultSet.updateByte(columnLabel, x);
    }

    @Override
    public final void updateBytes(final int columnIndex, final byte[] x) throws SQLException {
        resultSet.updateBytes(columnIndex, x);
    }

    @Override
    public final void updateBytes(final String columnLabel, final byte[] x) throws SQLException {
        resultSet.updateBytes(columnLabel, x);
    }

    @Override
    public final void updateCharacterStream(final int columnIndex, final Reader x) throws SQLException {
        resultSet.updateCharacterStream(columnIndex, x);
    }

    @Override
    public final void updateCharacterStream(final int columnIndex, final Reader x, final int length) throws SQLException {
        resultSet.updateCharacterStream(columnIndex, x, length);
    }

    @Override
    public final void updateCharacterStream(final int columnIndex, final Reader x, final long length) throws SQLException {
        resultSet.updateCharacterStream(columnIndex, x, length);
    }

    @Override
    public final void updateCharacterStream(final String columnLabel, final Reader reader) throws SQLException {
        resultSet.updateCharacterStream(columnLabel, reader);
    }

    @Override
    public final void updateCharacterStream(final String columnLabel, final Reader reader, final int length) throws SQLException {
        resultSet.updateCharacterStream(columnLabel, reader, length);
    }

    @Override
    public final void updateCharacterStream(final String columnLabel, final Reader reader, final long length) throws SQLException {
        resultSet.updateCharacterStream(columnLabel, reader, length);
    }

    @Override
    public final void updateClob(final int columnIndex, final Clob x) throws SQLException {
        resultSet.updateClob(columnIndex, x);
    }

    @Override
    public final void updateClob(final int columnIndex, final Reader reader) throws SQLException {
        resultSet.updateClob(columnIndex, reader);
    }

    @Override
    public final void updateClob(final int columnIndex, final Reader reader, final long length) throws SQLException {
        resultSet.updateClob(columnIndex, reader, length);
    }

    @Override
    public final void updateClob(final String columnLabel, final Clob x) throws SQLException {
        resultSet.updateClob(columnLabel, x);
    }

    @Override
    public final void updateClob(final String columnLabel, final Reader reader) throws SQLException {
        resultSet.updateClob(columnLabel, reader);
    }

    @Override
    public final void updateClob(final String columnLabel, final Reader reader, final long length) throws SQLException {
        resultSet.updateClob(columnLabel, reader, length);
    }

    @Override
    public final void updateDate(final int columnIndex, final Date x) throws SQLException {
        resultSet.updateDate(columnIndex, x);
    }

    @Override
    public final void updateDate(final String columnLabel, final Date x) throws SQLException {
        resultSet.updateDate(columnLabel, x);
    }

    @Override
    public final void updateDouble(final int columnIndex, final double x) throws SQLException {
        resultSet.updateDouble(columnIndex, x);
    }

    @Override
    public final void updateDouble(final String columnLabel, final double x) throws SQLException {
        resultSet.updateDouble(columnLabel, x);
    }

    @Override
    public final void updateFloat(final int columnIndex, final float x) throws SQLException {
        resultSet.updateFloat(columnIndex, x);
    }

    @Override
    public final void updateFloat(final String columnLabel, final float x) throws SQLException {
        resultSet.updateFloat(columnLabel, x);
    }

    @Override
    public final void updateInt(final int columnIndex, final int x) throws SQLException {
        resultSet.updateInt(columnIndex, x);
    }

    @Override
    public final void updateInt(final String columnLabel, final int x) throws SQLException {
        resultSet.updateInt(columnLabel, x);
    }

    @Override
    public final void updateLong(final int columnIndex, final long x) throws SQLException {
        resultSet.updateLong(columnIndex, x);
    }

    @Override
    public final void updateLong(final String columnLabel, final long x) throws SQLException {
        resultSet.updateLong(columnLabel, x);
    }

    @Override
    public final void updateNCharacterStream(final int columnIndex, final Reader x) throws SQLException {
        resultSet.updateNCharacterStream(columnIndex, x);
    }

    @Override
    public final void updateNCharacterStream(final int columnIndex, final Reader x, final long length) throws SQLException {
        resultSet.updateNCharacterStream(columnIndex, x, length);
    }

    @Override
    public final void updateNCharacterStream(final String columnLabel, final Reader reader) throws SQLException {
        resultSet.updateNCharacterStream(columnLabel, reader);
    }

    @Override
    public final void updateNCharacterStream(final String columnLabel, final Reader reader, final long length) throws SQLException {
        resultSet.updateNCharacterStream(columnLabel, reader, length);
    }

    @Override
    public final void updateNClob(final int columnIndex, final NClob nClob) throws SQLException {
        resultSet.updateNClob(columnIndex, nClob);
    }

    @Override
    public final void updateNClob(final int columnIndex, final Reader reader) throws SQLException {
        resultSet.updateNClob(columnIndex, reader);
    }

    @Override
    public final void updateNClob(final int columnIndex, final Reader reader, final long length) throws SQLException {
        resultSet.updateNClob(columnIndex, reader, length);
    }

    @Override
    public final void updateNClob(final String columnLabel, final NClob nClob) throws SQLException {
        resultSet.updateNClob(columnLabel, nClob);
    }

    @Override
    public final void updateNClob(final String columnLabel, final Reader reader) throws SQLException {
        resultSet.updateNClob(columnLabel, reader);
    }

    @Override
    public final void updateNClob(final String columnLabel, final Reader reader, final long length) throws SQLException {
        resultSet.updateNClob(columnLabel, reader, length);
    }

    @Override
    public final void updateNString(final int columnIndex, final String nString) throws SQLException {
        resultSet.updateNString(columnIndex, nString);
    }

    @Override
    public final void updateNString(final String columnLabel, final String nString) throws SQLException {
        resultSet.updateNString(columnLabel, nString);
    }

    @Override
    public final void updateNull(final int columnIndex) throws SQLException {
        resultSet.updateNull(columnIndex);
    }

    @Override
    public final void updateNull(final String columnLabel) throws SQLException {
        resultSet.updateNull(columnLabel);
    }

    @Override
    public final void updateObject(final int columnIndex, final Object x) throws SQLException {
        resultSet.updateObject(columnIndex, x);
    }

    @Override
    public final void updateObject(final int columnIndex, final Object x, final int scaleOrLength) throws SQLException {
        resultSet.updateObject(columnIndex, x, scaleOrLength);
    }

    public final void updateObject(int columnIndex, Object x, SQLType targetSqlType) throws SQLException {
        resultSet.updateObject(columnIndex, x, targetSqlType);
    }

    public final void updateObject(int columnIndex, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException {
        resultSet.updateObject(columnIndex, x, targetSqlType, scaleOrLength);
    }

    @Override
    public final void updateObject(final String columnLabel, final Object x) throws SQLException {
        resultSet.updateObject(columnLabel, x);
    }

    @Override
    public final void updateObject(final String columnLabel, final Object x, final int scaleOrLength) throws SQLException {
        resultSet.updateObject(columnLabel, x, scaleOrLength);
    }

    public final void updateObject(String columnLabel, Object x, SQLType targetSqlType) throws SQLException {
        resultSet.updateObject(columnLabel, x, targetSqlType);
    }

    public final void updateObject(String columnLabel, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException {
        resultSet.updateObject(columnLabel, x, targetSqlType, scaleOrLength);
    }

    @Override
    public final void updateRef(final int columnIndex, final Ref x) throws SQLException {
        resultSet.updateRef(columnIndex, x);
    }

    @Override
    public final void updateRef(final String columnLabel, final Ref x) throws SQLException {
        resultSet.updateRef(columnLabel, x);
    }

    @Override
    public final void updateRow() throws SQLException {
        resultSet.updateRow();
    }

    @Override
    public final void updateRowId(final int columnIndex, final RowId x) throws SQLException {
        resultSet.updateRowId(columnIndex, x);
    }

    @Override
    public final void updateRowId(final String columnLabel, final RowId x) throws SQLException {
        resultSet.updateRowId(columnLabel, x);
    }

    @Override
    public final void updateShort(final int columnIndex, final short x) throws SQLException {
        resultSet.updateShort(columnIndex, x);
    }

    @Override
    public final void updateShort(final String columnLabel, final short x) throws SQLException {
        resultSet.updateShort(columnLabel, x);
    }

    @Override
    public final void updateSQLXML(final int columnIndex, final SQLXML xmlObject) throws SQLException {
        resultSet.updateSQLXML(columnIndex, xmlObject);
    }

    @Override
    public final void updateSQLXML(final String columnLabel, final SQLXML xmlObject) throws SQLException {
        resultSet.updateSQLXML(columnLabel, xmlObject);
    }

    @Override
    public final void updateString(final int columnIndex, final String x) throws SQLException {
        resultSet.updateString(columnIndex, x);
    }

    @Override
    public final void updateString(final String columnLabel, final String x) throws SQLException {
        resultSet.updateString(columnLabel, x);
    }

    @Override
    public final void updateTime(final int columnIndex, final Time x) throws SQLException {
        resultSet.updateTime(columnIndex, x);
    }

    @Override
    public final void updateTime(final String columnLabel, final Time x) throws SQLException {
        resultSet.updateTime(columnLabel, x);
    }

    @Override
    public final void updateTimestamp(final int columnIndex, final Timestamp x) throws SQLException {
        resultSet.updateTimestamp(columnIndex, x);
    }

    @Override
    public final void updateTimestamp(final String columnLabel, final Timestamp x) throws SQLException {
        resultSet.updateTimestamp(columnLabel, x);
    }

    @Override
    public final boolean wasNull() throws SQLException {
        return resultSet.wasNull();
    }

}
