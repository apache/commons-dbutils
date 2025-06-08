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

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link BaseResultSetHandler} delegation.
 */
class BaseResultSetHandlerDelegationTest extends BaseTestCase {

    private Array array;
    private final BigDecimal bigDecimal = BigDecimal.ONE;
    private Blob blob;
    private boolean booleanValue;
    private final byte[] byteArray = {};
    private byte byteValue;
    private final Calendar cal = Calendar.getInstance();
    private Clob clob;
    private int columnIndex;
    private final String columnLabel = COLUMN_NAMES[0];
    private final Date date = new Date(1);
    private int direction;
    private double doubleValue;
    private float floatValue;
    private final Class<?> iface = String.class;
    private InputStream inputStream;
    private int intValue;
    private int lengthInt;
    private long lengthLong;
    private long longValue;
    private final Map<String, Class<?>> map = new HashMap<>();
    private NClob nClob;
    private final String nString = "";
    private final Object object = new Object();
    private Reader reader;
    private Ref ref;
    private int row;
    private RowId rowId;
    private int rows;
    private int scale;
    private int scaleOrLength;
    private short shortValue;
    private final String string = "";
    private SQLType targetSqlType;
    private final Time time = new Time(1);
    private final Timestamp timestamp = new Timestamp(1);
    private final Class<?> type = String.class;
    private SQLXML xmlObject;

    private void assertNextRow() throws SQLException {
        assertTrue(getResultSet().next());
    }

    @Override
    protected ResultSet createEmptyMockResultSet() {
        return MockResultSet.create(META_DATA, null, true);
    }

    /**
     * Creates a freshly initialized ResultSet.
     *
     * @return a freshly initialized ResultSet.
     */
    @Override
    protected ResultSet createMockResultSet() {
        return MockResultSet.create(META_DATA, ROW_ARRAY, true);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testAbsolute() throws SQLException {
        getResultSet().absolute(row);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testAfterLast() throws SQLException {
        getResultSet().afterLast();
    }

    /**
     * Tests delegation.
     */
    @Test
    void testBeforeFirst() throws SQLException {
        getResultSet().beforeFirst();
    }

    /**
     * Tests delegation.
     */
    @Test
    void testCancelRowUpdates() throws SQLException {
        getResultSet().cancelRowUpdates();
    }

    /**
     * Tests delegation.
     */
    @Test
    void testClearWarnings() throws SQLException {
        getResultSet().clearWarnings();
    }

    /**
     * Tests delegation.
     */
    @Test
    void testClose() throws SQLException {
        getResultSet().close();
    }

    /**
     * Tests delegation.
     */
    @Test
    void testDeleteRow() throws SQLException {
        getResultSet().deleteRow();
    }

    /**
     * Tests delegation.
     */
    @Test
    void testFindColumn() throws SQLException {
        getResultSet().findColumn(columnLabel);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testFirst() throws SQLException {
        getResultSet().first();
    }

    /**
     * Tests delegation.
     */
    @Test
    void testGetArrayInt() throws SQLException {
        getResultSet().getArray(columnIndex);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testGetArrayString() throws SQLException {
        getResultSet().getArray(columnLabel);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testGetAsciiStream() throws SQLException {
        getResultSet().getAsciiStream(columnLabel);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testGetAsciiStreamInt() throws SQLException {
        getResultSet().getAsciiStream(columnIndex);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testGetBigDecimalInt() throws SQLException {
        getResultSet().getBigDecimal(columnIndex);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testGetBigDecimalIntInt() throws SQLException {
        getResultSet().getBigDecimal(columnIndex, scale);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testGetBigDecimalString() throws SQLException {
        getResultSet().getBigDecimal(columnLabel);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testGetBigDecimalStringInt() throws SQLException {
        getResultSet().getBigDecimal(columnLabel, scale);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testGetBinaryStreamInt() throws SQLException {
        getResultSet().getBinaryStream(columnIndex);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testGetBinaryStreamString() throws SQLException {
        getResultSet().getBinaryStream(columnLabel);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testGetBlobInt() throws SQLException {
        getResultSet().getBlob(columnIndex);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testGetBlobString() throws SQLException {
        getResultSet().getBlob(columnLabel);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testGetBoolean() throws SQLException {
        assertNextRow();
        getResultSet().getBoolean(columnLabel);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testGetBooleanInt() throws SQLException {
        getResultSet().getBoolean(columnIndex);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testGetByteInt() throws SQLException {
        getResultSet().getByte(columnIndex);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testGetBytesInt() throws SQLException {
        getResultSet().getBytes(columnIndex);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testGetBytesString() throws SQLException {
        getResultSet().getBytes(columnLabel);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testGetByteString() throws SQLException {
        assertNextRow();
        getResultSet().getByte(columnLabel);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testGetCharacterStream() throws SQLException {
        getResultSet().getCharacterStream(columnLabel);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testGetCharacterStreamInt() throws SQLException {
        getResultSet().getCharacterStream(columnIndex);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testGetClob() throws SQLException {
        getResultSet().getClob(columnLabel);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testGetClobInt() throws SQLException {
        getResultSet().getClob(columnIndex);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testGetConcurrency() throws SQLException {
        getResultSet().getConcurrency();
    }

    /**
     * Tests delegation.
     */
    @Test
    void testGetCursorName() throws SQLException {
        getResultSet().getCursorName();
    }

    /**
     * Tests delegation.
     */
    @Test
    void testGetDate() throws SQLException {
        getResultSet().getDate(columnLabel);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testGetDateInt() throws SQLException {
        getResultSet().getDate(columnIndex);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testGetDateIntCalendar() throws SQLException {
        getResultSet().getDate(columnIndex, cal);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testGetDateStringCalendar() throws SQLException {
        getResultSet().getDate(columnLabel, cal);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testGetDouble() throws SQLException {
        assertNextRow();
        getResultSet().getDouble(columnLabel);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testGetDoubleInt() throws SQLException {
        getResultSet().getDouble(columnIndex);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testGetFetchDirection() throws SQLException {
        getResultSet().getFetchDirection();
    }

    /**
     * Tests delegation.
     */
    @Test
    void testGetFetchSize() throws SQLException {
        getResultSet().getFetchSize();
    }

    /**
     * Tests delegation.
     */
    @Test
    void testGetFloat() throws SQLException {
        assertNextRow();
        getResultSet().getFloat(columnLabel);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testGetFloatInt() throws SQLException {
        getResultSet().getFloat(columnIndex);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testGetHoldability() throws SQLException {
        getResultSet().getHoldability();
    }

    /**
     * Tests delegation.
     */
    @Test
    void testGetInt() throws SQLException {
        assertNextRow();
        getResultSet().getInt(columnLabel);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testGetIntInt() throws SQLException {
        getResultSet().getInt(columnIndex);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testGetLong() throws SQLException {
        assertNextRow();
        getResultSet().getLong(columnLabel);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testGetLongInt() throws SQLException {
        getResultSet().getLong(columnIndex);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testGetMetaData() throws SQLException {
        getResultSet().getMetaData();
    }

    /**
     * Tests delegation.
     */
    @Test
    void testGetNCharacterStream() throws SQLException {
        getResultSet().getNCharacterStream(columnLabel);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testGetNCharacterStreamInt() throws SQLException {
        getResultSet().getNCharacterStream(columnIndex);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testGetNClob() throws SQLException {
        getResultSet().getNClob(columnLabel);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testGetNClobInt() throws SQLException {
        getResultSet().getNClob(columnIndex);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testGetNString() throws SQLException {
        getResultSet().getNString(columnLabel);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testGetNStringInt() throws SQLException {
        getResultSet().getNString(columnIndex);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testGetObject() throws SQLException {
        assertNextRow();
        getResultSet().getObject(columnLabel);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testGetObjectInt() throws SQLException {
        getResultSet().getObject(columnIndex);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testGetObjectIntClass() throws SQLException {
        getResultSet().getObject(columnIndex, type);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testGetObjectIntMap() throws SQLException {
        getResultSet().getObject(columnIndex, map);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testGetObjectStringClass() throws SQLException {
        assertNextRow();
        getResultSet().getObject(columnLabel, type);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testGetObjectStringMap() throws SQLException {
        assertNextRow();
        getResultSet().getObject(columnLabel, map);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testGetRef() throws SQLException {
        getResultSet().getRef(columnLabel);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testGetRefInt() throws SQLException {
        getResultSet().getRef(columnIndex);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testGetRow() throws SQLException {
        getResultSet().getRow();
    }

    /**
     * Tests delegation.
     */
    @Test
    void testGetRowId() throws SQLException {
        getResultSet().getRowId(columnLabel);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testGetRowIdInt() throws SQLException {
        getResultSet().getRowId(columnIndex);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testGetShort() throws SQLException {
        assertNextRow();
        getResultSet().getShort(columnLabel);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testGetShortInt() throws SQLException {
        getResultSet().getShort(columnIndex);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testGetSQLXML() throws SQLException {
        getResultSet().getSQLXML(columnLabel);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testGetSQLXMLInt() throws SQLException {
        getResultSet().getSQLXML(columnIndex);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testGetStatement() throws SQLException {
        getResultSet().getStatement();
    }

    /**
     * Tests delegation.
     */
    @Test
    void testGetString() throws SQLException {
        assertNextRow();
        getResultSet().getString(columnLabel);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testGetStringInt() throws SQLException {
        getResultSet().getString(columnIndex);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testGetTime() throws SQLException {
        getResultSet().getTime(columnLabel);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testGetTimeInt() throws SQLException {
        getResultSet().getTime(columnIndex);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testGetTimeIntCalendar() throws SQLException {
        getResultSet().getTime(columnIndex, cal);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testGetTimestamp() throws SQLException {
        getResultSet().getTimestamp(columnLabel);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testGetTimestampInt() throws SQLException {
        getResultSet().getTimestamp(columnIndex);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testGetTimestampIntCalendar() throws SQLException {
        getResultSet().getTimestamp(columnIndex, cal);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testGetTimestampStringCalendar() throws SQLException {
        getResultSet().getTimestamp(columnLabel, cal);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testGetTimeStringCalendar() throws SQLException {
        getResultSet().getTime(columnLabel, cal);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testGetType() throws SQLException {
        getResultSet().getType();
    }

    /**
     * Tests delegation.
     */
    @Test
    void testGetUnicodeStream() throws SQLException {
        getResultSet().getUnicodeStream(columnLabel);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testGetUnicodeStreamInt() throws SQLException {
        getResultSet().getUnicodeStream(columnIndex);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testGetURL() throws SQLException {
        getResultSet().getURL(columnLabel);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testGetURLInt() throws SQLException {
        getResultSet().getURL(columnIndex);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testGetWarnings() throws SQLException {
        getResultSet().getWarnings();
    }

    /**
     * Tests delegation.
     */
    @Test
    void testInsertRow() throws SQLException {
        getResultSet().insertRow();
    }

    /**
     * Tests delegation.
     */
    @Test
    void testIsAfterLast() throws SQLException {
        getResultSet().isAfterLast();
    }

    /**
     * Tests delegation.
     */
    @Test
    void testIsBeforeFirst() throws SQLException {
        getResultSet().isBeforeFirst();
    }

    /**
     * Tests delegation.
     */
    @Test
    void testIsClosed() throws SQLException {
        getResultSet().isClosed();
    }

    /**
     * Tests delegation.
     */
    @Test
    void testIsFirst() throws SQLException {
        getResultSet().isFirst();
    }

    /**
     * Tests delegation.
     */
    @Test
    void testIsLast() throws SQLException {
        getResultSet().isLast();
    }

    /**
     * Tests delegation.
     */
    @Test
    void testIsWrapperFor() throws SQLException {
        getResultSet().isWrapperFor(iface);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testLast() throws SQLException {
        getResultSet().last();
    }

    /**
     * Tests delegation.
     */
    @Test
    void testMoveToInsertRow() throws SQLException {
        getResultSet().moveToInsertRow();
    }

    /**
     * Tests delegation.
     */
    @Test
    void testNext() throws SQLException {
        assertNextRow();
    }

    /**
     * Tests delegation.
     */
    @Test
    void testPrevious() throws SQLException {
        getResultSet().previous();
    }

    /**
     * Tests delegation.
     */
    @Test
    void testRefreshRow() throws SQLException {
        getResultSet().refreshRow();
    }

    /**
     * Tests delegation.
     */
    @Test
    void testRelativeInt() throws SQLException {
        getResultSet().relative(rows);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testRowDeleted() throws SQLException {
        getResultSet().rowDeleted();
    }

    /**
     * Tests delegation.
     */
    @Test
    void testRowInserted() throws SQLException {
        getResultSet().rowInserted();
    }

    /**
     * Tests delegation.
     */
    @Test
    void testRowUpdated() throws SQLException {
        getResultSet().rowUpdated();
    }

    /**
     * Tests delegation.
     */
    @Test
    void testSetFetchDirectionInt() throws SQLException {
        getResultSet().setFetchDirection(direction);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testSetFetchSizeInt() throws SQLException {
        getResultSet().setFetchSize(rows);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testTest_moveToCurrentRow() throws SQLException {
        getResultSet().moveToCurrentRow();
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUnwrapClass() throws SQLException {
        getResultSet().unwrap(iface);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateArrayIntArray() throws SQLException {
        getResultSet().updateArray(columnIndex, array);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateArrayStringArray() throws SQLException {
        getResultSet().updateArray(columnLabel, array);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateAsciiStreamIntInputStream() throws SQLException {
        getResultSet().updateAsciiStream(columnIndex, inputStream);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateAsciiStreamIntInputStreamInt() throws SQLException {
        getResultSet().updateAsciiStream(columnIndex, inputStream, lengthInt);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateAsciiStreamIntInputStreamLong() throws SQLException {
        getResultSet().updateAsciiStream(columnIndex, inputStream, lengthLong);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateAsciiStreamStringInputStream() throws SQLException {
        getResultSet().updateAsciiStream(columnLabel, inputStream);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateAsciiStreamStringInputStreamInt() throws SQLException {
        getResultSet().updateAsciiStream(columnLabel, inputStream, lengthInt);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateAsciiStreamStringInputStreamLong() throws SQLException {
        getResultSet().updateAsciiStream(columnLabel, inputStream, lengthLong);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateBigDecimalIntBigDecimal() throws SQLException {
        getResultSet().updateBigDecimal(columnIndex, bigDecimal);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateBigDecimalStringBigDecimal() throws SQLException {
        getResultSet().updateBigDecimal(columnLabel, bigDecimal);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateBinaryStreamIntInputStream() throws SQLException {
        getResultSet().updateBinaryStream(columnIndex, inputStream);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateBinaryStreamIntInputStreamInt() throws SQLException {
        getResultSet().updateBinaryStream(columnIndex, inputStream, lengthInt);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateBinaryStreamIntInputStreamLong() throws SQLException {
        getResultSet().updateBinaryStream(columnIndex, inputStream, lengthLong);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateBinaryStreamStringInputStream() throws SQLException {
        getResultSet().updateBinaryStream(columnLabel, inputStream);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateBinaryStreamStringInputStreamInt() throws SQLException {
        getResultSet().updateBinaryStream(columnLabel, inputStream, lengthInt);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateBinaryStreamStringInputStreamLong() throws SQLException {
        getResultSet().updateBinaryStream(columnLabel, inputStream, lengthLong);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateBlobIntBlob() throws SQLException {
        getResultSet().updateBlob(columnIndex, blob);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateBlobIntInputStream() throws SQLException {
        getResultSet().updateBlob(columnIndex, inputStream);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateBlobIntInputStreamLong() throws SQLException {
        getResultSet().updateBlob(columnIndex, inputStream, lengthLong);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateBlobStringBlob() throws SQLException {
        getResultSet().updateBlob(columnLabel, blob);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateBlobStringInputStream() throws SQLException {
        getResultSet().updateBlob(columnLabel, inputStream);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateBlobStringInputStreamLong() throws SQLException {
        getResultSet().updateBlob(columnLabel, inputStream, lengthLong);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateBooleanIntBoolean() throws SQLException {
        getResultSet().updateBoolean(columnIndex, booleanValue);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateBooleanStringBoolean() throws SQLException {
        getResultSet().updateBoolean(columnLabel, booleanValue);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateByteIntByte() throws SQLException {
        getResultSet().updateByte(columnIndex, byteValue);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateBytesIntByteArray() throws SQLException {
        getResultSet().updateBytes(columnIndex, byteArray);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateBytesStringByteArray() throws SQLException {
        getResultSet().updateBytes(columnLabel, byteArray);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateByteStringByte() throws SQLException {
        getResultSet().updateByte(columnLabel, byteValue);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateCharacterStreamIntReader() throws SQLException {
        getResultSet().updateCharacterStream(columnIndex, reader);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateCharacterStreamIntReaderInt() throws SQLException {
        getResultSet().updateCharacterStream(columnIndex, reader, lengthInt);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateCharacterStreamIntReaderLong() throws SQLException {
        getResultSet().updateCharacterStream(columnIndex, reader, lengthLong);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateCharacterStreamStringReader() throws SQLException {
        getResultSet().updateCharacterStream(columnLabel, reader);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateCharacterStreamStringReaderInt() throws SQLException {
        getResultSet().updateCharacterStream(columnLabel, reader, lengthInt);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateCharacterStreamStringReaderLong() throws SQLException {
        getResultSet().updateCharacterStream(columnLabel, reader, lengthLong);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateClobIntClob() throws SQLException {
        getResultSet().updateClob(columnIndex, clob);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateClobIntReader() throws SQLException {
        getResultSet().updateClob(columnIndex, reader);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateClobIntReaderLong() throws SQLException {
        getResultSet().updateClob(columnIndex, reader, lengthLong);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateClobStringClob() throws SQLException {
        getResultSet().updateClob(columnLabel, clob);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateClobStringReader() throws SQLException {
        getResultSet().updateClob(columnLabel, reader);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateClobStringReaderLong() throws SQLException {
        getResultSet().updateClob(columnLabel, reader, lengthLong);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateDateIntDate() throws SQLException {
        getResultSet().updateDate(columnIndex, date);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateDateStringDate() throws SQLException {
        getResultSet().updateDate(columnLabel, date);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateDoubleIntDouble() throws SQLException {
        getResultSet().updateDouble(columnIndex, doubleValue);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateDoubleStringDouble() throws SQLException {
        getResultSet().updateDouble(columnLabel, doubleValue);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateFloatIntFloat() throws SQLException {
        getResultSet().updateFloat(columnIndex, floatValue);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateFloatStringFloat() throws SQLException {
        getResultSet().updateFloat(columnLabel, floatValue);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateIntIntInt() throws SQLException {
        getResultSet().updateInt(columnIndex, intValue);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateIntStringInt() throws SQLException {
        getResultSet().updateInt(columnLabel, intValue);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateLongIntLong() throws SQLException {
        getResultSet().updateLong(columnIndex, longValue);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateLongStringLong() throws SQLException {
        getResultSet().updateLong(columnLabel, longValue);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateNCharacterStreamIntReader() throws SQLException {
        getResultSet().updateNCharacterStream(columnIndex, reader);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateNCharacterStreamIntReaderLong() throws SQLException {
        getResultSet().updateNCharacterStream(columnIndex, reader, lengthLong);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateNCharacterStreamStringReader() throws SQLException {
        getResultSet().updateNCharacterStream(columnLabel, reader);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateNCharacterStreamStringReaderLong() throws SQLException {
        getResultSet().updateNCharacterStream(columnLabel, reader, lengthLong);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateNClobIntNClob() throws SQLException {
        getResultSet().updateNClob(columnIndex, nClob);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateNClobIntReader() throws SQLException {
        getResultSet().updateNClob(columnIndex, reader);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateNClobIntReaderLong() throws SQLException {
        getResultSet().updateNClob(columnIndex, reader, lengthLong);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateNClobStringNClob() throws SQLException {
        getResultSet().updateNClob(columnLabel, nClob);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateNClobStringReader() throws SQLException {
        getResultSet().updateNClob(columnLabel, reader);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateNClobStringReaderLong() throws SQLException {
        getResultSet().updateNClob(columnLabel, reader, lengthLong);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateNStringIntString() throws SQLException {
        getResultSet().updateNString(columnIndex, nString);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateNStringStringString() throws SQLException {
        getResultSet().updateNString(columnLabel, nString);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateNull() throws SQLException {
        getResultSet().updateNull(columnLabel);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateNullInt() throws SQLException {
        getResultSet().updateNull(columnIndex);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateObjectInObjectInt() throws SQLException {
        getResultSet().updateObject(columnIndex, object, scaleOrLength);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateObjectIntObject() throws SQLException {
        getResultSet().updateObject(columnIndex, object);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateObjectIntObjectSQLType() throws SQLException {
        getResultSet().updateObject(columnIndex, object, targetSqlType);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateObjectIntObjectSQLTypeInt() throws SQLException {
        getResultSet().updateObject(columnIndex, object, targetSqlType, scaleOrLength);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateObjectStringObject() throws SQLException {
        getResultSet().updateObject(columnLabel, object);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateObjectStringObjectInt() throws SQLException {
        getResultSet().updateObject(columnLabel, object, scaleOrLength);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateObjectStringObjectSQLType() throws SQLException {
        getResultSet().updateObject(columnLabel, object, targetSqlType);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateObjectStringObjectSQLTypeInt() throws SQLException {
        getResultSet().updateObject(columnLabel, object, targetSqlType, scaleOrLength);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateRefIntRef() throws SQLException {
        getResultSet().updateRef(columnIndex, ref);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateRefStringRef() throws SQLException {
        getResultSet().updateRef(columnLabel, ref);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateRow() throws SQLException {
        getResultSet().updateRow();
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateRowIdIntRowId() throws SQLException {
        getResultSet().updateRowId(columnIndex, rowId);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateRowIdStringRowId() throws SQLException {
        getResultSet().updateRowId(columnLabel, rowId);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateShortIntShort() throws SQLException {
        getResultSet().updateShort(columnIndex, shortValue);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateShortStringShort() throws SQLException {
        getResultSet().updateShort(columnLabel, shortValue);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateSQLXMLIntSQLXML() throws SQLException {
        getResultSet().updateSQLXML(columnIndex, xmlObject);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateSQLXMLStringSQLXML() throws SQLException {
        getResultSet().updateSQLXML(columnLabel, xmlObject);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateStringIntString() throws SQLException {
        getResultSet().updateString(columnIndex, string);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateStringStringString() throws SQLException {
        getResultSet().updateString(columnLabel, string);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateTimeIntTime() throws SQLException {
        getResultSet().updateTime(columnIndex, time);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateTimestampIntTimestamp() throws SQLException {
        getResultSet().updateTimestamp(columnIndex, timestamp);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateTimestampStringTimestamp() throws SQLException {
        getResultSet().updateTimestamp(columnLabel, timestamp);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testUpdateTimeStringTime() throws SQLException {
        getResultSet().updateTime(columnLabel, time);
    }

    /**
     * Tests delegation.
     */
    @Test
    void testWasNull() throws SQLException {
        getResultSet().wasNull();
    }
}
