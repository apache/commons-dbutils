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
package org.apache.commons.dbutils.wrappers;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.ByteArrayInputStream;
import java.io.CharArrayReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Map;

import org.apache.commons.dbutils.BaseTestCase;
import org.apache.commons.dbutils.ProxyFactory;

final class SqlNullCheckedResultSetMockBlob implements Blob {

    /**
     * @throws SQLException
     */
    @Override
    public void free() throws SQLException {

    }

    @Override
    public InputStream getBinaryStream() throws SQLException {
        return new ByteArrayInputStream(new byte[0]);
    }

    /**
     * @throws SQLException
     */
    @Override
    public InputStream getBinaryStream(final long pos, final long length) throws SQLException {
      return null;
    }

    @Override
    public byte[] getBytes(final long param, final int param1) throws SQLException {
        return new byte[0];
    }

    @Override
    public long length() throws SQLException {
        return 0;
    }

    @Override
    public long position(final Blob blob, final long param) throws SQLException {
        return 0;
    }

    @Override
    public long position(final byte[] values, final long param) throws SQLException {
        return 0;
    }

    @Override
    public OutputStream setBinaryStream(final long pos) throws SQLException {
        return null;
    }

    @Override
    public int setBytes(final long pos, final byte[] bytes) throws SQLException {
        return 0;
    }

    @Override
    public int setBytes(final long pos, final byte[] bytes, final int offset, final int len)
        throws SQLException {
        return 0;
    }

    @Override
    public void truncate(final long len) throws SQLException {

    }

}

final class SqlNullCheckedResultSetMockClob implements Clob {

    /**
     * @throws SQLException
     */
    @Override
    public void free() throws SQLException {

    }

    @Override
    public InputStream getAsciiStream() throws SQLException {
        return null;
    }

    @Override
    public Reader getCharacterStream() throws SQLException {
        return null;
    }

    /**
     * @throws SQLException
     */
    @Override
    public Reader getCharacterStream(final long pos, final long length) throws SQLException {
      return null;
    }

    @Override
    public String getSubString(final long param, final int param1) throws SQLException {
        return "";
    }

    @Override
    public long length() throws SQLException {
        return 0;
    }

    @Override
    public long position(final Clob clob, final long param) throws SQLException {
        return 0;
    }

    @Override
    public long position(final String str, final long param) throws SQLException {
        return 0;
    }

    @Override
    public OutputStream setAsciiStream(final long pos) throws SQLException {
        return null;
    }

    @Override
    public Writer setCharacterStream(final long pos) throws SQLException {
        return null;
    }

    @Override
    public int setString(final long pos, final String str) throws SQLException {
        return 0;
    }

    @Override
    public int setString(final long pos, final String str, final int offset, final int len)
        throws SQLException {
        return 0;
    }

    @Override
    public void truncate(final long len) throws SQLException {

    }

}

final class SqlNullCheckedResultSetMockRef implements Ref {

    @Override
    public String getBaseTypeName() throws SQLException {
        return "";
    }

    @Override
    public Object getObject() throws SQLException {
        return null;
    }

    @Override
    public Object getObject(final Map<String,Class<?>> map) throws SQLException {
        return null;
    }

    @Override
    public void setObject(final Object value) throws SQLException {

    }

}

/**
 * Test cases for {@code SqlNullCheckedResultSet} class.
 */
public class SqlNullCheckedResultSetTest extends BaseTestCase {

    private static void assertArrayEquals(final byte[] expected, final byte[] actual) {
        if (expected == actual) {
            return;
        }
        if (expected.length != actual.length) {
            failNotEquals(null, Arrays.toString(expected), Arrays.toString(actual));
        }
        for (int i = 0; i < expected.length; i++) {
            final byte expectedItem = expected[i];
            final byte actualItem = actual[i];
            assertEquals("Array not equal at index " + i, expectedItem, actualItem);
        }
    }

    private SqlNullCheckedResultSet rs2;

    /**
     * Sets up instance variables required by this test case.
     */
    @Override
    public void setUp() throws Exception {
        super.setUp();

        rs2 =
            new SqlNullCheckedResultSet(
                ProxyFactory.instance().createResultSet(
                    new SqlNullUncheckedMockResultSet()));

        rs = ProxyFactory.instance().createResultSet(rs2); // Override superclass field
    }

    /**
     * Tests the getAsciiStream implementation.
     */
    public void testGetAsciiStream() throws SQLException {

        assertNull(rs.getAsciiStream(1));
        assertTrue(rs.wasNull());
        assertNull(rs.getAsciiStream("column"));
        assertTrue(rs.wasNull());
        // Set what gets returned to something other than the default
        final InputStream stream = new ByteArrayInputStream(new byte[0]);
        rs2.setNullAsciiStream(stream);
        assertNotNull(rs.getAsciiStream(1));
        assertEquals(stream, rs.getAsciiStream(1));
        assertNotNull(rs.getAsciiStream("column"));
        assertEquals(stream, rs.getAsciiStream("column"));

    }

    /**
     * Tests the getBigDecimal implementation.
     */
    public void testGetBigDecimal() throws SQLException {

        assertNull(rs.getBigDecimal(1));
        assertTrue(rs.wasNull());
        assertNull(rs.getBigDecimal("column"));
        assertTrue(rs.wasNull());
        // Set what gets returned to something other than the default
        final BigDecimal bd = new BigDecimal(5.0);
        rs2.setNullBigDecimal(bd);
        assertNotNull(rs.getBigDecimal(1));
        assertEquals(bd, rs.getBigDecimal(1));
        assertNotNull(rs.getBigDecimal("column"));
        assertEquals(bd, rs.getBigDecimal("column"));

    }

    /**
     * Tests the getBinaryStream implementation.
     */
    public void testGetBinaryStream() throws SQLException {

        assertNull(rs.getBinaryStream(1));
        assertTrue(rs.wasNull());
        assertNull(rs.getBinaryStream("column"));
        assertTrue(rs.wasNull());
        // Set what gets returned to something other than the default
        final InputStream stream = new ByteArrayInputStream(new byte[0]);
        rs2.setNullBinaryStream(stream);
        assertNotNull(rs.getBinaryStream(1));
        assertEquals(stream, rs.getBinaryStream(1));
        assertNotNull(rs.getBinaryStream("column"));
        assertEquals(stream, rs.getBinaryStream("column"));

    }

    /**
     * Tests the getBlob implementation.
     */
    public void testGetBlob() throws SQLException {

        assertNull(rs.getBlob(1));
        assertTrue(rs.wasNull());
        assertNull(rs.getBlob("column"));
        assertTrue(rs.wasNull());
        // Set what gets returned to something other than the default
        final Blob blob = new SqlNullCheckedResultSetMockBlob();
        rs2.setNullBlob(blob);
        assertNotNull(rs.getBlob(1));
        assertEquals(blob, rs.getBlob(1));
        assertNotNull(rs.getBlob("column"));
        assertEquals(blob, rs.getBlob("column"));

    }

    /**
     * Tests the getBoolean implementation.
     */
    public void testGetBoolean() throws SQLException {

        assertEquals(false, rs.getBoolean(1));
        assertTrue(rs.wasNull());
        assertEquals(false, rs.getBoolean("column"));
        assertTrue(rs.wasNull());
        // Set what gets returned to something other than the default
        rs2.setNullBoolean(true);
        assertEquals(true, rs.getBoolean(1));
        assertEquals(true, rs.getBoolean("column"));

    }

    /**
     * Tests the getByte implementation.
     */
    public void testGetByte() throws SQLException {

        assertEquals((byte) 0, rs.getByte(1));
        assertTrue(rs.wasNull());
        assertEquals((byte) 0, rs.getByte("column"));
        assertTrue(rs.wasNull());
        // Set what gets returned to something other than the default
        final byte b = (byte) 10;
        rs2.setNullByte(b);
        assertEquals(b, rs.getByte(1));
        assertEquals(b, rs.getByte("column"));

    }

    /**
     * Tests the getByte implementation.
     */
    public void testGetBytes() throws SQLException {

        assertNull(rs.getBytes(1));
        assertTrue(rs.wasNull());
        assertNull(rs.getBytes("column"));
        assertTrue(rs.wasNull());
        // Set what gets returned to something other than the default
        final byte[] b = new byte[5];
        for (int i = 0; i < 5; i++) {
            b[0] = (byte) i;
        }
        rs2.setNullBytes(b);
        assertNotNull(rs.getBytes(1));
        assertArrayEquals(b, rs.getBytes(1));
        assertNotNull(rs.getBytes("column"));
        assertArrayEquals(b, rs.getBytes("column"));

    }

    /**
     * Tests the getCharacterStream implementation.
     */
    public void testGetCharacterStream() throws SQLException {

        assertNull(rs.getCharacterStream(1));
        assertTrue(rs.wasNull());
        assertNull(rs.getCharacterStream("column"));
        assertTrue(rs.wasNull());
        // Set what gets returned to something other than the default
        final Reader reader = new CharArrayReader("this is a string".toCharArray());
        rs2.setNullCharacterStream(reader);
        assertNotNull(rs.getCharacterStream(1));
        assertEquals(reader, rs.getCharacterStream(1));
        assertNotNull(rs.getCharacterStream("column"));
        assertEquals(reader, rs.getCharacterStream("column"));

    }

    /**
     * Tests the getClob implementation.
     */
    public void testGetClob() throws SQLException {

        assertNull(rs.getClob(1));
        assertTrue(rs.wasNull());
        assertNull(rs.getClob("column"));
        assertTrue(rs.wasNull());
        // Set what gets returned to something other than the default
        final Clob clob = new SqlNullCheckedResultSetMockClob();
        rs2.setNullClob(clob);
        assertNotNull(rs.getClob(1));
        assertEquals(clob, rs.getClob(1));
        assertNotNull(rs.getClob("column"));
        assertEquals(clob, rs.getClob("column"));

    }

    /**
     * Tests the getDate implementation.
     */
    public void testGetDate() throws SQLException {

        assertNull(rs.getDate(1));
        assertTrue(rs.wasNull());
        assertNull(rs.getDate("column"));
        assertTrue(rs.wasNull());
        assertNull(rs.getDate(1, Calendar.getInstance()));
        assertTrue(rs.wasNull());
        assertNull(rs.getDate("column", Calendar.getInstance()));
        assertTrue(rs.wasNull());
        // Set what gets returned to something other than the default
        final java.sql.Date date = new java.sql.Date(new java.util.Date().getTime());
        rs2.setNullDate(date);
        assertNotNull(rs.getDate(1));
        assertEquals(date, rs.getDate(1));
        assertNotNull(rs.getDate("column"));
        assertEquals(date, rs.getDate("column"));
        assertNotNull(rs.getDate(1, Calendar.getInstance()));
        assertEquals(date, rs.getDate(1, Calendar.getInstance()));
        assertNotNull(rs.getDate("column", Calendar.getInstance()));
        assertEquals(date, rs.getDate("column", Calendar.getInstance()));

    }

    /**
     * Tests the getDouble implementation.
     */
    public void testGetDouble() throws SQLException {

        assertEquals(0.0, rs.getDouble(1), 0.0);
        assertTrue(rs.wasNull());
        assertEquals(0.0, rs.getDouble("column"), 0.0);
        assertTrue(rs.wasNull());
        // Set what gets returned to something other than the default
        final double d = 10.0;
        rs2.setNullDouble(d);
        assertEquals(d, rs.getDouble(1), 0.0);
        assertEquals(d, rs.getDouble("column"), 0.0);

    }

    /**
     * Tests the getFloat implementation.
     */
    public void testGetFloat() throws SQLException {
        assertEquals(0, rs.getFloat(1), 0.0);
        assertTrue(rs.wasNull());
        assertEquals(0, rs.getFloat("column"), 0.0);
        assertTrue(rs.wasNull());
        // Set what gets returned to something other than the default
        final float f = 10;
        rs2.setNullFloat(f);
        assertEquals(f, rs.getFloat(1), 0.0);
        assertEquals(f, rs.getFloat("column"), 0.0);
    }

    /**
     * Tests the getInt implementation.
     */
    public void testGetInt() throws SQLException {
        assertEquals(0, rs.getInt(1));
        assertTrue(rs.wasNull());
        assertEquals(0, rs.getInt("column"));
        assertTrue(rs.wasNull());
        // Set what gets returned to something other than the default
        final int i = 10;
        rs2.setNullInt(i);
        assertEquals(i, rs.getInt(1));
        assertEquals(i, rs.getInt("column"));
    }

    /**
     * Tests the getLong implementation.
     */
    public void testGetLong() throws SQLException {
        assertEquals(0, rs.getLong(1));
        assertTrue(rs.wasNull());
        assertEquals(0, rs.getLong("column"));
        assertTrue(rs.wasNull());
        // Set what gets returned to something other than the default
        final long l = 10;
        rs2.setNullLong(l);
        assertEquals(l, rs.getLong(1));
        assertEquals(l, rs.getLong("column"));
    }

    /**
     * Tests the getObject implementation.
     */
    public void testGetObject() throws SQLException {

        assertNull(rs.getObject(1));
        assertTrue(rs.wasNull());
        assertNull(rs.getObject("column"));
        assertTrue(rs.wasNull());
        assertNull(rs.getObject(1, (Map<String, Class<?>>) null));
        assertTrue(rs.wasNull());
        assertNull(rs.getObject("column", (Map<String, Class<?>>) null));
        assertTrue(rs.wasNull());
        // Set what gets returned to something other than the default
        final Object o = new Object();
        rs2.setNullObject(o);
        assertNotNull(rs.getObject(1));
        assertEquals(o, rs.getObject(1));
        assertNotNull(rs.getObject("column"));
        assertEquals(o, rs.getObject("column"));
        assertNotNull(rs.getObject(1, (Map<String, Class<?>>) null));
        assertEquals(o, rs.getObject(1, (Map<String, Class<?>>) null));
        assertNotNull(rs.getObject("column", (Map<String, Class<?>>) null));
        assertEquals(o, rs.getObject("column", (Map<String, Class<?>>) null));

    }

    /**
     * Tests the getRef implementation.
     */
    public void testGetRef() throws SQLException {

        assertNull(rs.getRef(1));
        assertTrue(rs.wasNull());
        assertNull(rs.getRef("column"));
        assertTrue(rs.wasNull());
        // Set what gets returned to something other than the default
        final Ref ref = new SqlNullCheckedResultSetMockRef();
        rs2.setNullRef(ref);
        assertNotNull(rs.getRef(1));
        assertEquals(ref, rs.getRef(1));
        assertNotNull(rs.getRef("column"));
        assertEquals(ref, rs.getRef("column"));

    }

    /**
     * Tests the getShort implementation.
     */
    public void testGetShort() throws SQLException {

        assertEquals((short) 0, rs.getShort(1));
        assertTrue(rs.wasNull());
        assertEquals((short) 0, rs.getShort("column"));
        assertTrue(rs.wasNull());
        // Set what gets returned to something other than the default
        final short s = (short) 10;
        rs2.setNullShort(s);
        assertEquals(s, rs.getShort(1));
        assertEquals(s, rs.getShort("column"));
    }

    /**
     * Tests the getString implementation.
     */
    public void testGetString() throws SQLException {
        assertEquals(null, rs.getString(1));
        assertTrue(rs.wasNull());
        assertEquals(null, rs.getString("column"));
        assertTrue(rs.wasNull());
        // Set what gets returned to something other than the default
        final String s = "hello, world";
        rs2.setNullString(s);
        assertEquals(s, rs.getString(1));
        assertEquals(s, rs.getString("column"));
    }

    /**
     * Tests the getTime implementation.
     */
    public void testGetTime() throws SQLException {

        assertNull(rs.getTime(1));
        assertTrue(rs.wasNull());
        assertNull(rs.getTime("column"));
        assertTrue(rs.wasNull());
        assertNull(rs.getTime(1, Calendar.getInstance()));
        assertTrue(rs.wasNull());
        assertNull(rs.getTime("column", Calendar.getInstance()));
        assertTrue(rs.wasNull());
        // Set what gets returned to something other than the default
        final Time time = new Time(new java.util.Date().getTime());
        rs2.setNullTime(time);
        assertNotNull(rs.getTime(1));
        assertEquals(time, rs.getTime(1));
        assertNotNull(rs.getTime("column"));
        assertEquals(time, rs.getTime("column"));
        assertNotNull(rs.getTime(1, Calendar.getInstance()));
        assertEquals(time, rs.getTime(1, Calendar.getInstance()));
        assertNotNull(rs.getTime("column", Calendar.getInstance()));
        assertEquals(time, rs.getTime("column", Calendar.getInstance()));

    }

    /**
     * Tests the getTimestamp implementation.
     */
    public void testGetTimestamp() throws SQLException {

        assertNull(rs.getTimestamp(1));
        assertTrue(rs.wasNull());
        assertNull(rs.getTimestamp("column"));
        assertTrue(rs.wasNull());
        assertNull(rs.getTimestamp(1, Calendar.getInstance()));
        assertTrue(rs.wasNull());
        assertNull(rs.getTimestamp("column", Calendar.getInstance()));
        assertTrue(rs.wasNull());
        // Set what gets returned to something other than the default
        final Timestamp ts = new Timestamp(new java.util.Date().getTime());
        rs2.setNullTimestamp(ts);
        assertNotNull(rs.getTimestamp(1));
        assertEquals(ts, rs.getTimestamp(1));
        assertNotNull(rs.getTimestamp("column"));
        assertEquals(ts, rs.getTimestamp("column"));
        assertNotNull(rs.getTimestamp(1, Calendar.getInstance()));
        assertEquals(ts, rs.getTimestamp(1, Calendar.getInstance()));
        assertNotNull(rs.getTimestamp("column", Calendar.getInstance()));
        assertEquals(ts, rs.getTimestamp("column", Calendar.getInstance()));
    }

    /**
     * Tests the setNullAsciiStream implementation.
     */
    public void testSetNullAsciiStream() throws SQLException {

        assertNull(rs2.getNullAsciiStream());
        // Set what gets returned to something other than the default
        final InputStream stream = new ByteArrayInputStream(new byte[0]);
        rs2.setNullAsciiStream(stream);
        assertNotNull(rs.getAsciiStream(1));
        assertEquals(stream, rs.getAsciiStream(1));
        assertNotNull(rs.getAsciiStream("column"));
        assertEquals(stream, rs.getAsciiStream("column"));

    }

    /**
     * Tests the setNullBigDecimal implementation.
     */
    public void testSetNullBigDecimal() throws SQLException {

        assertNull(rs2.getNullBigDecimal());
        // Set what gets returned to something other than the default
        final BigDecimal bd = new BigDecimal(5.0);
        rs2.setNullBigDecimal(bd);
        assertNotNull(rs.getBigDecimal(1));
        assertEquals(bd, rs.getBigDecimal(1));
        assertNotNull(rs.getBigDecimal("column"));
        assertEquals(bd, rs.getBigDecimal("column"));

    }

    /**
     * Tests the setNullBinaryStream implementation.
     */
    public void testSetNullBinaryStream() throws SQLException {

        assertNull(rs2.getNullBinaryStream());
        // Set what gets returned to something other than the default
        final InputStream stream = new ByteArrayInputStream(new byte[0]);
        rs2.setNullBinaryStream(stream);
        assertNotNull(rs.getBinaryStream(1));
        assertEquals(stream, rs.getBinaryStream(1));
        assertNotNull(rs.getBinaryStream("column"));
        assertEquals(stream, rs.getBinaryStream("column"));

    }

    /**
     * Tests the setNullBlob implementation.
     */
    public void testSetNullBlob() throws SQLException {

        assertNull(rs2.getNullBlob());
        // Set what gets returned to something other than the default
        final Blob blob = new SqlNullCheckedResultSetMockBlob();
        rs2.setNullBlob(blob);
        assertNotNull(rs.getBlob(1));
        assertEquals(blob, rs.getBlob(1));
        assertNotNull(rs.getBlob("column"));
        assertEquals(blob, rs.getBlob("column"));

    }

    /**
     * Tests the setNullBoolean implementation.
     */
    public void testSetNullBoolean() throws SQLException {

        assertEquals(false, rs2.getNullBoolean());
        // Set what gets returned to something other than the default
        rs2.setNullBoolean(true);
        assertEquals(true, rs.getBoolean(1));
        assertEquals(true, rs.getBoolean("column"));

    }

    /**
     * Tests the setNullByte implementation.
     */
    public void testSetNullByte() throws SQLException {

        assertEquals((byte) 0, rs2.getNullByte());
        // Set what gets returned to something other than the default
        final byte b = (byte) 10;
        rs2.setNullByte(b);
        assertEquals(b, rs.getByte(1));
        assertEquals(b, rs.getByte("column"));

    }

    /**
     * Tests the setNullByte implementation.
     */
    public void testSetNullBytes() throws SQLException {
        // test the default, unset value
        assertEquals(null, rs2.getNullBytes());

        // test that setting null is safe
        rs2.setNullBytes(null);
        assertEquals(null, rs2.getNullBytes());

        // Set what gets returned to something other than the default
        final byte[] b = new byte[5];
        for (int i = 0; i < 5; i++) {
            b[0] = (byte) i;
        }
        rs2.setNullBytes(b);
        assertNotNull(rs.getBytes(1));
        assertArrayEquals(b, rs.getBytes(1));
        assertNotNull(rs.getBytes("column"));
        assertArrayEquals(b, rs.getBytes("column"));

    }

    /**
     * Tests the setNullCharacterStream implementation.
     */
    public void testSetNullCharacterStream() throws SQLException {

        assertNull(rs2.getNullCharacterStream());
        // Set what gets returned to something other than the default
        final Reader reader = new CharArrayReader("this is a string".toCharArray());
        rs2.setNullCharacterStream(reader);
        assertNotNull(rs.getCharacterStream(1));
        assertEquals(reader, rs.getCharacterStream(1));
        assertNotNull(rs.getCharacterStream("column"));
        assertEquals(reader, rs.getCharacterStream("column"));

    }

    /**
     * Tests the setNullClob implementation.
     */
    public void testSetNullClob() throws SQLException {

        assertNull(rs2.getNullClob());
        // Set what gets returned to something other than the default
        final Clob clob = new SqlNullCheckedResultSetMockClob();
        rs2.setNullClob(clob);
        assertNotNull(rs.getClob(1));
        assertEquals(clob, rs.getClob(1));
        assertNotNull(rs.getClob("column"));
        assertEquals(clob, rs.getClob("column"));

    }

    /**
     * Tests the setNullDate implementation.
     */
    public void testSetNullDate() throws SQLException {
        // test the default, unset value
        assertEquals(null, rs2.getNullDate());

        // test that setting null is safe
        rs2.setNullDate(null);
        assertEquals(null, rs2.getNullDate());

        // Set what gets returned to something other than the default
        final java.sql.Date date = new java.sql.Date(new java.util.Date().getTime());
        rs2.setNullDate(date);
        assertNotNull(rs.getDate(1));
        assertEquals(date, rs.getDate(1));
        assertNotNull(rs.getDate("column"));
        assertEquals(date, rs.getDate("column"));
        assertNotNull(rs.getDate(1, Calendar.getInstance()));
        assertEquals(date, rs.getDate(1, Calendar.getInstance()));
        assertNotNull(rs.getDate("column", Calendar.getInstance()));
        assertEquals(date, rs.getDate("column", Calendar.getInstance()));

    }

    /**
     * Tests the setNullDouble implementation.
     */
    public void testSetNullDouble() throws SQLException {
        assertEquals(0.0, rs2.getNullDouble(), 0.0);
        // Set what gets returned to something other than the default
        final double d = 10.0;
        rs2.setNullDouble(d);
        assertEquals(d, rs.getDouble(1), 0.0);
        assertEquals(d, rs.getDouble("column"), 0.0);
    }

    /**
     * Tests the setNullFloat implementation.
     */
    public void testSetNullFloat() throws SQLException {
        assertEquals((float) 0.0, rs2.getNullFloat(), 0.0);
        // Set what gets returned to something other than the default
        final float f = (float) 10.0;
        rs2.setNullFloat(f);
        assertEquals(f, rs.getFloat(1), 0.0);
        assertEquals(f, rs.getFloat("column"), 0.0);
    }

    /**
     * Tests the setNullInt implementation.
     */
    public void testSetNullInt() throws SQLException {
        assertEquals(0, rs2.getNullInt());
        assertEquals(0, rs.getInt(1));
        assertTrue(rs.wasNull());
        assertEquals(0, rs.getInt("column"));
        assertTrue(rs.wasNull());
        // Set what gets returned to something other than the default
        final int i = 10;
        rs2.setNullInt(i);
        assertEquals(i, rs.getInt(1));
        assertEquals(i, rs.getInt("column"));
    }

    /**
     * Tests the setNullLong implementation.
     */
    public void testSetNullLong() throws SQLException {
        assertEquals(0, rs2.getNullLong());
        // Set what gets returned to something other than the default
        final long l = 10;
        rs2.setNullLong(l);
        assertEquals(l, rs.getLong(1));
        assertEquals(l, rs.getLong("column"));
    }

    /**
     * Tests the setNullObject implementation.
     */
    public void testSetNullObject() throws SQLException {
        assertNull(rs2.getNullObject());
        // Set what gets returned to something other than the default
        final Object o = new Object();
        rs2.setNullObject(o);
        assertNotNull(rs.getObject(1));
        assertEquals(o, rs.getObject(1));
        assertNotNull(rs.getObject("column"));
        assertEquals(o, rs.getObject("column"));
        assertNotNull(rs.getObject(1, (Map<String, Class<?>>) null));
        assertEquals(o, rs.getObject(1, (Map<String, Class<?>>) null));
        assertNotNull(rs.getObject("column", (Map<String, Class<?>>) null));
        assertEquals(o, rs.getObject("column", (Map<String, Class<?>>) null));
    }

    /**
     * Tests the setNullRef implementation.
     */
    public void testSetNullRef() throws SQLException {
        assertNull(rs2.getNullRef());
        // Set what gets returned to something other than the default
        final Ref ref = new SqlNullCheckedResultSetMockRef();
        rs2.setNullRef(ref);
        assertNotNull(rs.getRef(1));
        assertEquals(ref, rs.getRef(1));
        assertNotNull(rs.getRef("column"));
        assertEquals(ref, rs.getRef("column"));
    }

    /**
     * Tests the setNullShort implementation.
     */
    public void testSetNullShort() throws SQLException {

        assertEquals((short) 0, rs2.getNullShort());
        // Set what gets returned to something other than the default
        final short s = (short) 10;
        rs2.setNullShort(s);
        assertEquals(s, rs.getShort(1));
        assertEquals(s, rs.getShort("column"));

    }

    /**
     * Tests the setNullString implementation.
     */
    public void testSetNullString() throws SQLException {
        assertEquals(null, rs2.getNullString());
        // Set what gets returned to something other than the default
        final String s = "hello, world";
        rs2.setNullString(s);
        assertEquals(s, rs.getString(1));
        assertEquals(s, rs.getString("column"));
    }

    /**
     * Tests the setNullTime implementation.
     */
    public void testSetNullTime() throws SQLException {
        // test the default, unset value
        assertEquals(null, rs2.getNullTime());

        // test that setting null is safe
        rs2.setNullTime(null);
        assertEquals(null, rs2.getNullTime());

        // Set what gets returned to something other than the default
        final Time time = new Time(new java.util.Date().getTime());
        rs2.setNullTime(time);
        assertNotNull(rs.getTime(1));
        assertEquals(time, rs.getTime(1));
        assertNotNull(rs.getTime("column"));
        assertEquals(time, rs.getTime("column"));
        assertNotNull(rs.getTime(1, Calendar.getInstance()));
        assertEquals(time, rs.getTime(1, Calendar.getInstance()));
        assertNotNull(rs.getTime("column", Calendar.getInstance()));
        assertEquals(time, rs.getTime("column", Calendar.getInstance()));
    }

    /**
     * Tests the setNullTimestamp implementation.
     */
    public void testSetNullTimestamp() throws SQLException {
        // test the default, unset value
        assertEquals(null, rs2.getNullTimestamp());

        // test that setting null is safe
        rs2.setNullTimestamp(null);
        assertEquals(null, rs2.getNullTimestamp());

        // Set what gets returned to something other than the default
        final Timestamp ts = new Timestamp(new java.util.Date().getTime());
        rs2.setNullTimestamp(ts);
        assertNotNull(rs.getTimestamp(1));
        assertEquals(ts, rs.getTimestamp(1));
        assertNotNull(rs.getTimestamp("column"));
        assertEquals(ts, rs.getTimestamp("column"));
        assertNotNull(rs.getTimestamp(1, Calendar.getInstance()));
        assertEquals(ts, rs.getTimestamp(1, Calendar.getInstance()));
        assertNotNull(rs.getTimestamp("column", Calendar.getInstance()));
        assertEquals(ts, rs.getTimestamp("column", Calendar.getInstance()));
    }

    /**
     * Tests the getURL and setNullURL implementations.
     */
    public void testURL() throws SQLException, MalformedURLException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        assertEquals(null, rs.getURL(1));
        assertTrue(rs.wasNull());
        assertEquals(null, rs.getURL("column"));
        assertTrue(rs.wasNull());
        // Set what gets returned to something other than the default
        final URL u = new URL("http://www.apache.org");
        rs2.setNullURL(u);
        assertEquals(u, rs.getURL(1));
        assertEquals(u, rs.getURL("column"));
    }

    public void testWrapResultSet() throws SQLException {
        final ResultSet wrappedRs = mock(ResultSet.class);
        final ResultSet rs = SqlNullCheckedResultSet.wrap(wrappedRs);
        rs.beforeFirst();
        verify(wrappedRs).beforeFirst();
        rs.next();
        verify(wrappedRs).next();
    }
}

final class SqlNullUncheckedMockResultSet implements InvocationHandler {

    /**
     * Always return false for booleans, 0 for numerics, and null for Objects.
     * @see java.lang.reflect.InvocationHandler#invoke(Object, java.lang.reflect.Method, Object[])
     */
    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args)
        throws Throwable {

        final Class<?> returnType = method.getReturnType();

        if (method.getName().equals("wasNull")) {
            return Boolean.TRUE;

        }
        if (returnType.equals(Boolean.TYPE)) {
            return Boolean.FALSE;

        }
        if (returnType.equals(Integer.TYPE)) {
            return Integer.valueOf(0);

        }
        if (returnType.equals(Short.TYPE)) {
            return Short.valueOf((short) 0);

        }
        if (returnType.equals(Double.TYPE)) {
            return Double.valueOf(0);

        }
        if (returnType.equals(Long.TYPE)) {
            return Long.valueOf(0);

        }
        if (returnType.equals(Byte.TYPE)) {
            return Byte.valueOf((byte) 0);

        }
        if (returnType.equals(Float.TYPE)) {
            return Float.valueOf(0);

        }
        return null;
    }
}
