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

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Test the BasicRowProcessor class.
 */
public class BasicRowProcessorTest extends BaseTestCase {

    private static final RowProcessor processor = new BasicRowProcessor();

    /**
     * Format that matches Date.toString(). Sun Mar 14 15:19:15 MST 2004
     */
    private static final DateFormat datef = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);

    public void testPutAllContainsKeyAndRemove() throws Exception {
        final Map<String, Object> test = new HashMap<>(3);
        test.put("fiRst", "thing");
        test.put("seCond", "another");
        test.put("thIrd", "more");
        final Map<String, Object> brpMap = BasicRowProcessor.createCaseInsensitiveHashMap(3);
        brpMap.putAll(test);

        assertEquals(test, brpMap);
        assertTrue(brpMap.containsKey("fiRst"));
        assertTrue(brpMap.containsKey("first"));

        brpMap.remove("first");
        assertFalse(brpMap.containsKey("first"));
    }

    public void testToArray() throws SQLException {

        Object[] a;
        assertTrue(this.getResultSet().next());
        a = processor.toArray(this.getResultSet());
        assertEquals(COLS, a.length);
        assertEquals("1", a[0]);
        assertEquals("2", a[1]);
        assertEquals("THREE", a[2]);

        assertTrue(this.getResultSet().next());
        a = processor.toArray(this.getResultSet());
        assertEquals(COLS, a.length);

        assertEquals("4", a[0]);
        assertEquals("5", a[1]);
        assertEquals("SIX", a[2]);

        assertFalse(this.getResultSet().next());
    }

    public void testToBean() throws SQLException, ParseException {

        assertTrue(this.getResultSet().next());
        TestBean row = processor.toBean(this.getResultSet(), TestBean.class);
        assertEquals("1", row.getOne());
        assertEquals("2", row.getTwo());
        assertEquals(TestBean.Ordinal.THREE, row.getThree());
        assertEquals("not set", row.getDoNotSet());

        assertTrue(this.getResultSet().next());
        row = processor.toBean(this.getResultSet(), TestBean.class);

        assertEquals("4", row.getOne());
        assertEquals("5", row.getTwo());
        assertEquals(TestBean.Ordinal.SIX, row.getThree());
        assertEquals("not set", row.getDoNotSet());
        assertEquals(3, row.getIntTest());
        assertEquals(Integer.valueOf(4), row.getIntegerTest());
        assertEquals(null, row.getNullObjectTest());
        assertEquals(0, row.getNullPrimitiveTest());
        // test date -> string handling
        assertNotNull(row.getNotDate());
        assertTrue(!"not a date".equals(row.getNotDate()));
        assertTrue(row.getNotDate().endsWith("789456123"));

        assertFalse(this.getResultSet().next());

    }

    public void testToBeanList() throws SQLException, ParseException {

        final List<TestBean> list = processor.toBeanList(this.getResultSet(), TestBean.class);
        assertNotNull(list);
        assertEquals(ROWS, list.size());

        TestBean b = list.get(0);
        assertEquals("1", b.getOne());
        assertEquals("2", b.getTwo());
        assertEquals(TestBean.Ordinal.THREE, b.getThree());
        assertEquals("not set", b.getDoNotSet());
        datef.parse(b.getNotDate());

        b = list.get(1);
        assertEquals("4", b.getOne());
        assertEquals("5", b.getTwo());
        assertEquals(TestBean.Ordinal.SIX, b.getThree());
        assertEquals("not set", b.getDoNotSet());
        assertEquals(3, b.getIntTest());
        assertEquals(Integer.valueOf(4), b.getIntegerTest());
        assertEquals(null, b.getNullObjectTest());
        assertEquals(0, b.getNullPrimitiveTest());
        // test date -> string handling
        assertNotNull(b.getNotDate());
        assertTrue(!"not a date".equals(b.getNotDate()));
        assertTrue(b.getNotDate().endsWith("789456123"));
    }

    public void testToMap() throws SQLException {

        assertTrue(this.getResultSet().next());
        Map<String, Object> m = processor.toMap(this.getResultSet());
        assertEquals(COLS, m.size());
        assertEquals("1", m.get("one"));
        assertEquals("2", m.get("TWO"));
        assertEquals("THREE", m.get("Three"));

        assertTrue(this.getResultSet().next());
        m = processor.toMap(this.getResultSet());
        assertEquals(COLS, m.size());

        assertEquals("4", m.get("One")); // case shouldn't matter
        assertEquals("5", m.get("two"));
        assertEquals("SIX", m.get("THREE"));

        assertFalse(this.getResultSet().next());
    }

    public void testToMapOrdering() throws SQLException {

        assertTrue(this.getResultSet().next());
        final Map<String, Object> m = processor.toMap(this.getResultSet());

        final Iterator<String> itr = m.keySet().iterator();
        assertEquals("one", itr.next());
        assertEquals("two", itr.next());
        assertEquals("three", itr.next());
        assertEquals("notInBean", itr.next());
        assertEquals("intTest", itr.next());
        assertEquals("integerTest", itr.next());
        assertEquals("nullObjectTest", itr.next());
        assertEquals("nullPrimitiveTest", itr.next());
        assertEquals("notDate", itr.next());
        assertEquals("columnProcessorDoubleTest", itr.next());
        assertEquals("11", itr.next());

        assertFalse(itr.hasNext());
    }
}
