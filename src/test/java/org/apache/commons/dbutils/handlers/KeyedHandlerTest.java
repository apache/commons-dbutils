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
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.dbutils.BaseTestCase;
import org.apache.commons.dbutils.ResultSetHandler;

public class KeyedHandlerTest extends BaseTestCase {

    public void testHandle() throws SQLException {
        ResultSetHandler<Map<String,Map<String,Object>>> h = new KeyedHandler<String>();

        Map<String,Map<String,Object>> results = h.handle(this.rs);

        assertNotNull(results);
        assertEquals(ROWS, results.size());

        Map<String,Object> row = null;
        for(Entry<String, Map<String, Object>> entry : results.entrySet())
        {
            Object key = entry.getKey();
            assertNotNull(key);
            row = entry.getValue();
            assertNotNull(row);
            assertEquals(COLS, row.keySet().size());
        }
        row = results.get("1");
        assertEquals("1", row.get("one"));
        assertEquals("2", row.get("TWO"));
        assertEquals("THREE", row.get("Three"));
    }

    public void testColumnIndexHandle() throws SQLException {
        ResultSetHandler<Map<String,Map<String,Object>>> h = new KeyedHandler<String>(2);
        Map<String,Map<String,Object>> results = h.handle(this.rs);

        assertNotNull(results);
        assertEquals(ROWS, results.size());

        Map<String,Object> row = null;
        for(Entry<String, Map<String, Object>> entry : results.entrySet())
        {
            Object key = entry.getKey();
            assertNotNull(key);
            row = entry.getValue();
            assertNotNull(row);
            assertEquals(COLS, row.keySet().size());
        }
        row = results.get("5");
        assertEquals("4", row.get("one"));
        assertEquals("5", row.get("TWO"));
        assertEquals("SIX", row.get("Three"));
    }

    public void testColumnNameHandle() throws SQLException {
        ResultSetHandler<Map<Integer,Map<String,Object>>> h = new KeyedHandler<Integer>("intTest");
        Map<Integer,Map<String,Object>> results = h.handle(this.rs);

        assertNotNull(results);
        assertEquals(ROWS, results.size());

        Map<String,Object> row = null;
        for(Entry<Integer, Map<String, Object>> entry : results.entrySet())
        {
            Object key = entry.getKey();
            assertNotNull(key);
            row = entry.getValue();
            assertNotNull(row);
            assertEquals(COLS, row.keySet().size());
        }
        row = results.get(Integer.valueOf(3));
        assertEquals("4", row.get("one"));
        assertEquals("5", row.get("TWO"));
        assertEquals("SIX", row.get("Three"));
    }

    public void testEmptyResultSetHandle() throws SQLException {
        ResultSetHandler<Map<String,Map<String,Object>>> h = new KeyedHandler<String>();
        Map<String,Map<String,Object>> results = h.handle(this.emptyResultSet);
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }
}
