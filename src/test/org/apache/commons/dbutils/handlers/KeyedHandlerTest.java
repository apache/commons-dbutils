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
package org.apache.commons.dbutils.handlers;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.dbutils.BaseTestCase;
import org.apache.commons.dbutils.ResultSetHandler;

public class KeyedHandlerTest extends BaseTestCase {

    public void testHandle() throws SQLException {
        ResultSetHandler h = new KeyedHandler();

        Map results = (Map) h.handle(this.rs);

        assertNotNull(results);
        assertEquals(ROWS, results.size());

        Iterator iter = results.keySet().iterator();
        Map row = null;
        while (iter.hasNext()) {
            Object key = iter.next();
            assertNotNull(key);
            row = (Map) results.get(key);
            assertNotNull(row);
            assertEquals(COLS, row.keySet().size());
        }

        row = (Map) results.get("1");
        assertEquals("1", row.get("one"));
        assertEquals("2", row.get("TWO"));
        assertEquals("3", row.get("Three"));
    }

    public void testColumnIndexHandle() throws SQLException {
        ResultSetHandler h = new KeyedHandler(2);
        Map results = (Map) h.handle(this.rs);

        assertNotNull(results);
        assertEquals(ROWS, results.size());

        Iterator iter = results.keySet().iterator();
        Map row = null;
        while (iter.hasNext()) {
            Object key = iter.next();
            assertNotNull(key);
            row = (Map) results.get(key);
            assertNotNull(row);
            assertEquals(COLS, row.keySet().size());
        }

        row = (Map) results.get("5");
        assertEquals("4", row.get("one"));
        assertEquals("5", row.get("TWO"));
        assertEquals("6", row.get("Three"));
    }

    public void testColumnNameHandle() throws SQLException {
        ResultSetHandler h = new KeyedHandler("three");
        Map results = (Map) h.handle(this.rs);

        assertNotNull(results);
        assertEquals(ROWS, results.size());

        Iterator iter = results.keySet().iterator();
        Map row = null;
        while (iter.hasNext()) {
            Object key = iter.next();
            assertNotNull(key);
            row = (Map) results.get(key);
            assertNotNull(row);
            assertEquals(COLS, row.keySet().size());
        }

        row = (Map) results.get("6");
        assertEquals("4", row.get("one"));
        assertEquals("5", row.get("TWO"));
        assertEquals("6", row.get("Three"));
    }

    public void testEmptyResultSetHandle() throws SQLException {
        ResultSetHandler h = new KeyedHandler();
        Map results = (Map) h.handle(this.emptyResultSet);
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }
}
