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
 * According to the <i>DRY</i> principle (Don't Repeat Yourself), repeating {@code resultSet}
 * variable inside the {@link ResultSetHandler#handle(ResultSet)} over and over for each iteration
 * can get a little tedious, {@code AbstractResultSetHandler} implicitly gives users access to
 * {@code ResultSet}'s methods.
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
    private ResultSet resultSet;

    /**
     * {@inheritDoc}
     */
    protected final boolean absolute(final int row) throws SQLException {
        return resultSet.absolute(row);
    }

    /**
     * {@inheritDoc}
     */
    protected final void afterLast() throws SQLException {
        resultSet.afterLast();
    }

    /**
     * {@inheritDoc}
     */
    protected final void beforeFirst() throws SQLException {
        resultSet.beforeFirst();
    }

    /**
     * {@inheritDoc}
     */
    protected final void cancelRowUpdates() throws SQLException {
        resultSet.cancelRowUpdates();
    }

    /**
     * {@inheritDoc}
     */
    protected final void clearWarnings() throws SQLException {
        resultSet.clearWarnings();
    }

    /**
     * {@inheritDoc}
     */
    protected final void close() throws SQLException {
        resultSet.close();
    }

    /**
     * {@inheritDoc}
     */
    protected final void deleteRow() throws SQLException {
        resultSet.deleteRow();
    }

    /**
     * {@inheritDoc}
     */
    protected final int findColumn(final String columnLabel) throws SQLException {
        return resultSet.findColumn(columnLabel);
    }

    /**
     * {@inheritDoc}
     */
    protected final boolean first() throws SQLException {
        return resultSet.first();
    }

    protected final ResultSet getAdaptedResultSet() {
        return resultSet;
    }

    /**
     * {@inheritDoc}
     */
    protected final Array getArray(final int columnIndex) throws SQLException {
        return resultSet.getArray(columnIndex);
    }

    /**
     * {@inheritDoc}
     */
    protected final Array getArray(final String columnLabel) throws SQLException {
        return resultSet.getArray(columnLabel);
    }

    /**
     * {@inheritDoc}
     */
    protected final InputStream getAsciiStream(final int columnIndex) throws SQLException {
        return resultSet.getAsciiStream(columnIndex);
    }

    /**
     * {@inheritDoc}
     */
    protected final InputStream getAsciiStream(final String columnLabel) throws SQLException {
        return resultSet.getAsciiStream(columnLabel);
    }

    /**
     * {@inheritDoc}
     */
    protected final BigDecimal getBigDecimal(final int columnIndex) throws SQLException {
        return resultSet.getBigDecimal(columnIndex);
    }

    /**
     * {@inheritDoc}
     */
    @Deprecated
    protected final BigDecimal getBigDecimal(final int columnIndex, final int scale) throws SQLException {
        return resultSet.getBigDecimal(columnIndex, scale);
    }

    /**
     * {@inheritDoc}
     */
    protected final BigDecimal getBigDecimal(final String columnLabel) throws SQLException {
        return resultSet.getBigDecimal(columnLabel);
    }

    /**
     * {@inheritDoc}
     */
    @Deprecated
    protected final BigDecimal getBigDecimal(final String columnLabel, final int scale) throws SQLException {
        return resultSet.getBigDecimal(columnLabel, scale);
    }

    /**
     * {@inheritDoc}
     */
    protected final InputStream getBinaryStream(final int columnIndex) throws SQLException {
        return resultSet.getBinaryStream(columnIndex);
    }

    /**
     * {@inheritDoc}
     */
    protected final InputStream getBinaryStream(final String columnLabel) throws SQLException {
        return resultSet.getBinaryStream(columnLabel);
    }

    /**
     * {@inheritDoc}
     */
    protected final Blob getBlob(final int columnIndex) throws SQLException {
        return resultSet.getBlob(columnIndex);
    }

    /**
     * {@inheritDoc}
     */
    protected final Blob getBlob(final String columnLabel) throws SQLException {
        return resultSet.getBlob(columnLabel);
    }

    /**
     * {@inheritDoc}
     */
    protected final boolean getBoolean(final int columnIndex) throws SQLException {
        return resultSet.getBoolean(columnIndex);
    }

    /**
     * {@inheritDoc}
     */
    protected final boolean getBoolean(final String columnLabel) throws SQLException {
        return resultSet.getBoolean(columnLabel);
    }

    /**
     * {@inheritDoc}
     */
    protected final byte getByte(final int columnIndex) throws SQLException {
        return resultSet.getByte(columnIndex);
    }

    /**
     * {@inheritDoc}
     */
    protected final byte getByte(final String columnLabel) throws SQLException {
        return resultSet.getByte(columnLabel);
    }

    /**
     * {@inheritDoc}
     */
    protected final byte[] getBytes(final int columnIndex) throws SQLException {
        return resultSet.getBytes(columnIndex);
    }

    /**
     * {@inheritDoc}
     */
    protected final byte[] getBytes(final String columnLabel) throws SQLException {
        return resultSet.getBytes(columnLabel);
    }

    /**
     * {@inheritDoc}
     */
    protected final Reader getCharacterStream(final int columnIndex) throws SQLException {
        return resultSet.getCharacterStream(columnIndex);
    }

    /**
     * {@inheritDoc}
     */
    protected final Reader getCharacterStream(final String columnLabel) throws SQLException {
        return resultSet.getCharacterStream(columnLabel);
    }

    /**
     * {@inheritDoc}
     */
    protected final Clob getClob(final int columnIndex) throws SQLException {
        return resultSet.getClob(columnIndex);
    }

    /**
     * {@inheritDoc}
     */
    protected final Clob getClob(final String columnLabel) throws SQLException {
        return resultSet.getClob(columnLabel);
    }

    /**
     * {@inheritDoc}
     */
    protected final int getConcurrency() throws SQLException {
        return resultSet.getConcurrency();
    }

    /**
     * {@inheritDoc}
     */
    protected final String getCursorName() throws SQLException {
        return resultSet.getCursorName();
    }

    /**
     * {@inheritDoc}
     */
    protected final Date getDate(final int columnIndex) throws SQLException {
        return resultSet.getDate(columnIndex);
    }

    /**
     * {@inheritDoc}
     */
    protected final Date getDate(final int columnIndex, final Calendar cal) throws SQLException {
        return resultSet.getDate(columnIndex, cal);
    }

    /**
     * {@inheritDoc}
     */
    protected final Date getDate(final String columnLabel) throws SQLException {
        return resultSet.getDate(columnLabel);
    }

    /**
     * {@inheritDoc}
     */
    protected final Date getDate(final String columnLabel, final Calendar cal) throws SQLException {
        return resultSet.getDate(columnLabel, cal);
    }

    /**
     * {@inheritDoc}
     */
    protected final double getDouble(final int columnIndex) throws SQLException {
        return resultSet.getDouble(columnIndex);
    }

    /**
     * {@inheritDoc}
     */
    protected final double getDouble(final String columnLabel) throws SQLException {
        return resultSet.getDouble(columnLabel);
    }

    /**
     * {@inheritDoc}
     */
    protected final int getFetchDirection() throws SQLException {
        return resultSet.getFetchDirection();
    }

    /**
     * {@inheritDoc}
     */
    protected final int getFetchSize() throws SQLException {
        return resultSet.getFetchSize();
    }

    /**
     * {@inheritDoc}
     */
    protected final float getFloat(final int columnIndex) throws SQLException {
        return resultSet.getFloat(columnIndex);
    }

    /**
     * {@inheritDoc}
     */
    protected final float getFloat(final String columnLabel) throws SQLException {
        return resultSet.getFloat(columnLabel);
    }

    /**
     * {@inheritDoc}
     */
    protected final int getHoldability() throws SQLException {
        return resultSet.getHoldability();
    }

    /**
     * {@inheritDoc}
     */
    protected final int getInt(final int columnIndex) throws SQLException {
        return resultSet.getInt(columnIndex);
    }

    /**
     * {@inheritDoc}
     */
    protected final int getInt(final String columnLabel) throws SQLException {
        return resultSet.getInt(columnLabel);
    }

    /**
     * {@inheritDoc}
     */
    protected final long getLong(final int columnIndex) throws SQLException {
        return resultSet.getLong(columnIndex);
    }

    /**
     * {@inheritDoc}
     */
    protected final long getLong(final String columnLabel) throws SQLException {
        return resultSet.getLong(columnLabel);
    }

    /**
     * {@inheritDoc}
     */
    protected final ResultSetMetaData getMetaData() throws SQLException {
        return resultSet.getMetaData();
    }

    /**
     * {@inheritDoc}
     */
    protected final Reader getNCharacterStream(final int columnIndex) throws SQLException {
        return resultSet.getNCharacterStream(columnIndex);
    }

    /**
     * {@inheritDoc}
     */
    protected final Reader getNCharacterStream(final String columnLabel) throws SQLException {
        return resultSet.getNCharacterStream(columnLabel);
    }

    /**
     * {@inheritDoc}
     */
    protected final NClob getNClob(final int columnIndex) throws SQLException {
        return resultSet.getNClob(columnIndex);
    }

    /**
     * {@inheritDoc}
     */
    protected final NClob getNClob(final String columnLabel) throws SQLException {
        return resultSet.getNClob(columnLabel);
    }

    /**
     * {@inheritDoc}
     */
    protected final String getNString(final int columnIndex) throws SQLException {
        return resultSet.getNString(columnIndex);
    }

    /**
     * {@inheritDoc}
     */
    protected final String getNString(final String columnLabel) throws SQLException {
        return resultSet.getNString(columnLabel);
    }

    /**
     * {@inheritDoc}
     */
    protected final Object getObject(final int columnIndex) throws SQLException {
        return resultSet.getObject(columnIndex);
    }

    /**
     * {@inheritDoc}
     */
    protected final Object getObject(final int columnIndex, final Map<String, Class<?>> map) throws SQLException {
        return resultSet.getObject(columnIndex, map);
    }

    /**
     * {@inheritDoc}
     */
    protected final Object getObject(final String columnLabel) throws SQLException {
        return resultSet.getObject(columnLabel);
    }

    /**
     * {@inheritDoc}
     */
    protected final Object getObject(final String columnLabel, final Map<String, Class<?>> map) throws SQLException {
        return resultSet.getObject(columnLabel, map);
    }

    /**
     * {@inheritDoc}
     */
    protected final Ref getRef(final int columnIndex) throws SQLException {
        return resultSet.getRef(columnIndex);
    }

    /**
     * {@inheritDoc}
     */
    protected final Ref getRef(final String columnLabel) throws SQLException {
        return resultSet.getRef(columnLabel);
    }

    /**
     * {@inheritDoc}
     */
    protected final int getRow() throws SQLException {
        return resultSet.getRow();
    }

    /**
     * {@inheritDoc}
     */
    protected final RowId getRowId(final int columnIndex) throws SQLException {
        return resultSet.getRowId(columnIndex);
    }

    /**
     * {@inheritDoc}
     */
    protected final RowId getRowId(final String columnLabel) throws SQLException {
        return resultSet.getRowId(columnLabel);
    }

    /**
     * {@inheritDoc}
     */
    protected final short getShort(final int columnIndex) throws SQLException {
        return resultSet.getShort(columnIndex);
    }

    /**
     * {@inheritDoc}
     */
    protected final short getShort(final String columnLabel) throws SQLException {
        return resultSet.getShort(columnLabel);
    }

    /**
     * {@inheritDoc}
     */
    protected final SQLXML getSQLXML(final int columnIndex) throws SQLException {
        return resultSet.getSQLXML(columnIndex);
    }

    /**
     * {@inheritDoc}
     */
    protected final SQLXML getSQLXML(final String columnLabel) throws SQLException {
        return resultSet.getSQLXML(columnLabel);
    }

    /**
     * {@inheritDoc}
     */
    protected final Statement getStatement() throws SQLException {
        return resultSet.getStatement();
    }

    /**
     * {@inheritDoc}
     */
    protected final String getString(final int columnIndex) throws SQLException {
        return resultSet.getString(columnIndex);
    }

    /**
     * {@inheritDoc}
     */
    protected final String getString(final String columnLabel) throws SQLException {
        return resultSet.getString(columnLabel);
    }

    /**
     * {@inheritDoc}
     */
    protected final Time getTime(final int columnIndex) throws SQLException {
        return resultSet.getTime(columnIndex);
    }

    /**
     * {@inheritDoc}
     */
    protected final Time getTime(final int columnIndex, final Calendar cal) throws SQLException {
        return resultSet.getTime(columnIndex, cal);
    }

    /**
     * {@inheritDoc}
     */
    protected final Time getTime(final String columnLabel) throws SQLException {
        return resultSet.getTime(columnLabel);
    }

    /**
     * {@inheritDoc}
     */
    protected final Time getTime(final String columnLabel, final Calendar cal) throws SQLException {
        return resultSet.getTime(columnLabel, cal);
    }

    /**
     * {@inheritDoc}
     */
    protected final Timestamp getTimestamp(final int columnIndex) throws SQLException {
        return resultSet.getTimestamp(columnIndex);
    }

    /**
     * {@inheritDoc}
     */
    protected final Timestamp getTimestamp(final int columnIndex, final Calendar cal) throws SQLException {
        return resultSet.getTimestamp(columnIndex, cal);
    }

    /**
     * {@inheritDoc}
     */
    protected final Timestamp getTimestamp(final String columnLabel) throws SQLException {
        return resultSet.getTimestamp(columnLabel);
    }

    /**
     * {@inheritDoc}
     */
    protected final Timestamp getTimestamp(final String columnLabel, final Calendar cal) throws SQLException {
        return resultSet.getTimestamp(columnLabel, cal);
    }

    /**
     * {@inheritDoc}
     */
    protected final int getType() throws SQLException {
        return resultSet.getType();
    }

    /**
     * {@inheritDoc}
     */
    @Deprecated
    protected final InputStream getUnicodeStream(final int columnIndex) throws SQLException {
        return resultSet.getUnicodeStream(columnIndex);
    }

    /**
     * {@inheritDoc}
     */
    @Deprecated
    protected final InputStream getUnicodeStream(final String columnLabel) throws SQLException {
        return resultSet.getUnicodeStream(columnLabel);
    }

    /**
     * {@inheritDoc}
     */
    protected final URL getURL(final int columnIndex) throws SQLException {
        return resultSet.getURL(columnIndex);
    }

    /**
     * {@inheritDoc}
     */
    protected final URL getURL(final String columnLabel) throws SQLException {
        return resultSet.getURL(columnLabel);
    }

    /**
     * {@inheritDoc}
     */
    protected final SQLWarning getWarnings() throws SQLException {
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

    /**
     * {@inheritDoc}
     */
    @Override
    public final T handle(final ResultSet rs) throws SQLException {
        if (this.resultSet != null) {
            throw new IllegalStateException("Re-entry not allowed!");
        }

        this.resultSet = rs;

        try {
            return handle();
        } finally {
            this.resultSet = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    protected final void insertRow() throws SQLException {
        resultSet.insertRow();
    }

    /**
     * {@inheritDoc}
     */
    protected final boolean isAfterLast() throws SQLException {
        return resultSet.isAfterLast();
    }

    /**
     * {@inheritDoc}
     */
    protected final boolean isBeforeFirst() throws SQLException {
        return resultSet.isBeforeFirst();
    }

    /**
     * {@inheritDoc}
     */
    protected final boolean isClosed() throws SQLException {
        return resultSet.isClosed();
    }

    /**
     * {@inheritDoc}
     */
    protected final boolean isFirst() throws SQLException {
        return resultSet.isFirst();
    }

    /**
     * {@inheritDoc}
     */
    protected final boolean isLast() throws SQLException {
        return resultSet.isLast();
    }

    /**
     * {@inheritDoc}
     */
    protected final boolean isWrapperFor(final Class<?> iface) throws SQLException {
        return resultSet.isWrapperFor(iface);
    }

    /**
     * {@inheritDoc}
     */
    protected final boolean last() throws SQLException {
        return resultSet.last();
    }

    /**
     * {@inheritDoc}
     */
    protected final void moveToCurrentRow() throws SQLException {
        resultSet.moveToCurrentRow();
    }

    /**
     * {@inheritDoc}
     */
    protected final void moveToInsertRow() throws SQLException {
        resultSet.moveToInsertRow();
    }

    /**
     * {@inheritDoc}
     */
    protected final boolean next() throws SQLException {
        return resultSet.next();
    }

    /**
     * {@inheritDoc}
     */
    protected final boolean previous() throws SQLException {
        return resultSet.previous();
    }

    /**
     * {@inheritDoc}
     */
    protected final void refreshRow() throws SQLException {
        resultSet.refreshRow();
    }

    /**
     * {@inheritDoc}
     */
    protected final boolean relative(final int rows) throws SQLException {
        return resultSet.relative(rows);
    }

    /**
     * {@inheritDoc}
     */
    protected final boolean rowDeleted() throws SQLException {
        return resultSet.rowDeleted();
    }

    /**
     * {@inheritDoc}
     */
    protected final boolean rowInserted() throws SQLException {
        return resultSet.rowInserted();
    }

    /**
     * {@inheritDoc}
     */
    protected final boolean rowUpdated() throws SQLException {
        return resultSet.rowUpdated();
    }

    /**
     * {@inheritDoc}
     */
    protected final void setFetchDirection(final int direction) throws SQLException {
        resultSet.setFetchDirection(direction);
    }

    /**
     * {@inheritDoc}
     */
    protected final void setFetchSize(final int rows) throws SQLException {
        resultSet.setFetchSize(rows);
    }

    /**
     * {@inheritDoc}
     */
    protected final <E> E unwrap(final Class<E> iface) throws SQLException {
        return resultSet.unwrap(iface);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateArray(final int columnIndex, final Array x) throws SQLException {
        resultSet.updateArray(columnIndex, x);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateArray(final String columnLabel, final Array x) throws SQLException {
        resultSet.updateArray(columnLabel, x);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateAsciiStream(final int columnIndex, final InputStream x) throws SQLException {
        resultSet.updateAsciiStream(columnIndex, x);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateAsciiStream(final int columnIndex, final InputStream x, final int length) throws SQLException {
        resultSet.updateAsciiStream(columnIndex, x, length);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateAsciiStream(final int columnIndex, final InputStream x, final long length) throws SQLException {
        resultSet.updateAsciiStream(columnIndex, x, length);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateAsciiStream(final String columnLabel, final InputStream x) throws SQLException {
        resultSet.updateAsciiStream(columnLabel, x);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateAsciiStream(final String columnLabel, final InputStream x, final int length) throws SQLException {
        resultSet.updateAsciiStream(columnLabel, x, length);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateAsciiStream(final String columnLabel, final InputStream x, final long length) throws SQLException {
        resultSet.updateAsciiStream(columnLabel, x, length);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateBigDecimal(final int columnIndex, final BigDecimal x) throws SQLException {
        resultSet.updateBigDecimal(columnIndex, x);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateBigDecimal(final String columnLabel, final BigDecimal x) throws SQLException {
        resultSet.updateBigDecimal(columnLabel, x);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateBinaryStream(final int columnIndex, final InputStream x) throws SQLException {
        resultSet.updateBinaryStream(columnIndex, x);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateBinaryStream(final int columnIndex, final InputStream x, final int length) throws SQLException {
        resultSet.updateBinaryStream(columnIndex, x, length);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateBinaryStream(final int columnIndex, final InputStream x, final long length) throws SQLException {
        resultSet.updateBinaryStream(columnIndex, x, length);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateBinaryStream(final String columnLabel, final InputStream x) throws SQLException {
        resultSet.updateBinaryStream(columnLabel, x);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateBinaryStream(final String columnLabel, final InputStream x, final int length) throws SQLException {
        resultSet.updateBinaryStream(columnLabel, x, length);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateBinaryStream(final String columnLabel, final InputStream x, final long length) throws SQLException {
        resultSet.updateBinaryStream(columnLabel, x, length);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateBlob(final int columnIndex, final Blob x) throws SQLException {
        resultSet.updateBlob(columnIndex, x);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateBlob(final int columnIndex, final InputStream inputStream) throws SQLException {
        resultSet.updateBlob(columnIndex, inputStream);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateBlob(final int columnIndex, final InputStream inputStream, final long length) throws SQLException {
        resultSet.updateBlob(columnIndex, inputStream, length);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateBlob(final String columnLabel, final Blob x) throws SQLException {
        resultSet.updateBlob(columnLabel, x);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateBlob(final String columnLabel, final InputStream inputStream) throws SQLException {
        resultSet.updateBlob(columnLabel, inputStream);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateBlob(final String columnLabel, final InputStream inputStream, final long length) throws SQLException {
        resultSet.updateBlob(columnLabel, inputStream, length);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateBoolean(final int columnIndex, final boolean x) throws SQLException {
        resultSet.updateBoolean(columnIndex, x);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateBoolean(final String columnLabel, final boolean x) throws SQLException {
        resultSet.updateBoolean(columnLabel, x);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateByte(final int columnIndex, final byte x) throws SQLException {
        resultSet.updateByte(columnIndex, x);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateByte(final String columnLabel, final byte x) throws SQLException {
        resultSet.updateByte(columnLabel, x);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateBytes(final int columnIndex, final byte[] x) throws SQLException {
        resultSet.updateBytes(columnIndex, x);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateBytes(final String columnLabel, final byte[] x) throws SQLException {
        resultSet.updateBytes(columnLabel, x);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateCharacterStream(final int columnIndex, final Reader x) throws SQLException {
        resultSet.updateCharacterStream(columnIndex, x);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateCharacterStream(final int columnIndex, final Reader x, final int length) throws SQLException {
        resultSet.updateCharacterStream(columnIndex, x, length);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateCharacterStream(final int columnIndex, final Reader x, final long length) throws SQLException {
        resultSet.updateCharacterStream(columnIndex, x, length);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateCharacterStream(final String columnLabel, final Reader reader) throws SQLException {
        resultSet.updateCharacterStream(columnLabel, reader);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateCharacterStream(final String columnLabel, final Reader reader, final int length) throws SQLException {
        resultSet.updateCharacterStream(columnLabel, reader, length);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateCharacterStream(final String columnLabel, final Reader reader, final long length) throws SQLException {
        resultSet.updateCharacterStream(columnLabel, reader, length);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateClob(final int columnIndex, final Clob x) throws SQLException {
        resultSet.updateClob(columnIndex, x);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateClob(final int columnIndex, final Reader reader) throws SQLException {
        resultSet.updateClob(columnIndex, reader);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateClob(final int columnIndex, final Reader reader, final long length) throws SQLException {
        resultSet.updateClob(columnIndex, reader, length);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateClob(final String columnLabel, final Clob x) throws SQLException {
        resultSet.updateClob(columnLabel, x);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateClob(final String columnLabel, final Reader reader) throws SQLException {
        resultSet.updateClob(columnLabel, reader);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateClob(final String columnLabel, final Reader reader, final long length) throws SQLException {
        resultSet.updateClob(columnLabel, reader, length);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateDate(final int columnIndex, final Date x) throws SQLException {
        resultSet.updateDate(columnIndex, x);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateDate(final String columnLabel, final Date x) throws SQLException {
        resultSet.updateDate(columnLabel, x);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateDouble(final int columnIndex, final double x) throws SQLException {
        resultSet.updateDouble(columnIndex, x);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateDouble(final String columnLabel, final double x) throws SQLException {
        resultSet.updateDouble(columnLabel, x);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateFloat(final int columnIndex, final float x) throws SQLException {
        resultSet.updateFloat(columnIndex, x);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateFloat(final String columnLabel, final float x) throws SQLException {
        resultSet.updateFloat(columnLabel, x);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateInt(final int columnIndex, final int x) throws SQLException {
        resultSet.updateInt(columnIndex, x);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateInt(final String columnLabel, final int x) throws SQLException {
        resultSet.updateInt(columnLabel, x);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateLong(final int columnIndex, final long x) throws SQLException {
        resultSet.updateLong(columnIndex, x);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateLong(final String columnLabel, final long x) throws SQLException {
        resultSet.updateLong(columnLabel, x);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateNCharacterStream(final int columnIndex, final Reader x) throws SQLException {
        resultSet.updateNCharacterStream(columnIndex, x);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateNCharacterStream(final int columnIndex, final Reader x, final long length) throws SQLException {
        resultSet.updateNCharacterStream(columnIndex, x, length);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateNCharacterStream(final String columnLabel, final Reader reader) throws SQLException {
        resultSet.updateNCharacterStream(columnLabel, reader);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateNCharacterStream(final String columnLabel, final Reader reader, final long length) throws SQLException {
        resultSet.updateNCharacterStream(columnLabel, reader, length);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateNClob(final int columnIndex, final NClob nClob) throws SQLException {
        resultSet.updateNClob(columnIndex, nClob);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateNClob(final int columnIndex, final Reader reader) throws SQLException {
        resultSet.updateNClob(columnIndex, reader);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateNClob(final int columnIndex, final Reader reader, final long length) throws SQLException {
        resultSet.updateNClob(columnIndex, reader, length);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateNClob(final String columnLabel, final NClob nClob) throws SQLException {
        resultSet.updateNClob(columnLabel, nClob);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateNClob(final String columnLabel, final Reader reader) throws SQLException {
        resultSet.updateNClob(columnLabel, reader);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateNClob(final String columnLabel, final Reader reader, final long length) throws SQLException {
        resultSet.updateNClob(columnLabel, reader, length);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateNString(final int columnIndex, final String nString) throws SQLException {
        resultSet.updateNString(columnIndex, nString);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateNString(final String columnLabel, final String nString) throws SQLException {
        resultSet.updateNString(columnLabel, nString);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateNull(final int columnIndex) throws SQLException {
        resultSet.updateNull(columnIndex);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateNull(final String columnLabel) throws SQLException {
        resultSet.updateNull(columnLabel);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateObject(final int columnIndex, final Object x) throws SQLException {
        resultSet.updateObject(columnIndex, x);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateObject(final int columnIndex, final Object x, final int scaleOrLength) throws SQLException {
        resultSet.updateObject(columnIndex, x, scaleOrLength);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateObject(final String columnLabel, final Object x) throws SQLException {
        resultSet.updateObject(columnLabel, x);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateObject(final String columnLabel, final Object x, final int scaleOrLength) throws SQLException {
        resultSet.updateObject(columnLabel, x, scaleOrLength);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateRef(final int columnIndex, final Ref x) throws SQLException {
        resultSet.updateRef(columnIndex, x);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateRef(final String columnLabel, final Ref x) throws SQLException {
        resultSet.updateRef(columnLabel, x);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateRow() throws SQLException {
        resultSet.updateRow();
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateRowId(final int columnIndex, final RowId x) throws SQLException {
        resultSet.updateRowId(columnIndex, x);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateRowId(final String columnLabel, final RowId x) throws SQLException {
        resultSet.updateRowId(columnLabel, x);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateShort(final int columnIndex, final short x) throws SQLException {
        resultSet.updateShort(columnIndex, x);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateShort(final String columnLabel, final short x) throws SQLException {
        resultSet.updateShort(columnLabel, x);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateSQLXML(final int columnIndex, final SQLXML xmlObject) throws SQLException {
        resultSet.updateSQLXML(columnIndex, xmlObject);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateSQLXML(final String columnLabel, final SQLXML xmlObject) throws SQLException {
        resultSet.updateSQLXML(columnLabel, xmlObject);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateString(final int columnIndex, final String x) throws SQLException {
        resultSet.updateString(columnIndex, x);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateString(final String columnLabel, final String x) throws SQLException {
        resultSet.updateString(columnLabel, x);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateTime(final int columnIndex, final Time x) throws SQLException {
        resultSet.updateTime(columnIndex, x);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateTime(final String columnLabel, final Time x) throws SQLException {
        resultSet.updateTime(columnLabel, x);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateTimestamp(final int columnIndex, final Timestamp x) throws SQLException {
        resultSet.updateTimestamp(columnIndex, x);
    }

    /**
     * {@inheritDoc}
     */
    protected final void updateTimestamp(final String columnLabel, final Timestamp x) throws SQLException {
        resultSet.updateTimestamp(columnLabel, x);
    }

    /**
     * {@inheritDoc}
     */
    protected final boolean wasNull() throws SQLException {
        return resultSet.wasNull();
    }

}