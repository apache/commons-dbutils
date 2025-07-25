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
package org.apache.commons.dbutils.handlers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.sql.SQLException;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.dbutils.BaseTestCase;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.RowProcessor;
import org.junit.jupiter.api.Test;

class KeyedHandlerTest extends BaseTestCase {

    @Test
    void testColumnIndexHandle() throws SQLException {
        final ResultSetHandler<Map<String, Map<String, Object>>> h = new KeyedHandler<>(2);
        final Map<String, Map<String, Object>> results = h.handle(getResultSet());

        assertNotNull(results);
        assertEquals(ROW_COUNT, results.size());

        Map<String, Object> row = null;
        for (final Entry<String, Map<String, Object>> entry : results.entrySet()) {
            final Object key = entry.getKey();
            assertNotNull(key);
            row = entry.getValue();
            assertNotNull(row);
            assertEquals(COLUMN_COUNT, row.size());
        }
        row = results.get("5");
        assertEquals("4", row.get("one"));
        assertEquals("5", row.get("TWO"));
        assertEquals("SIX", row.get("Three"));
    }

    @Test
    void testColumnNameHandle() throws SQLException {
        final ResultSetHandler<Map<Integer, Map<String, Object>>> h = new KeyedHandler<>("intTest");
        final Map<Integer, Map<String, Object>> results = h.handle(getResultSet());

        assertNotNull(results);
        assertEquals(ROW_COUNT, results.size());

        Map<String, Object> row = null;
        for (final Entry<Integer, Map<String, Object>> entry : results.entrySet()) {
            final Object key = entry.getKey();
            assertNotNull(key);
            row = entry.getValue();
            assertNotNull(row);
            assertEquals(COLUMN_COUNT, row.size());
        }
        row = results.get(Integer.valueOf(3));
        assertEquals("4", row.get("one"));
        assertEquals("5", row.get("TWO"));
        assertEquals("SIX", row.get("Three"));
    }

    @Test
    void testEmptyResultSetHandle() throws SQLException {
        final ResultSetHandler<Map<String, Map<String, Object>>> h = new KeyedHandler<>();
        final Map<String, Map<String, Object>> results = h.handle(getEmptyResultSet());
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    void testHandle() throws SQLException {
        final ResultSetHandler<Map<String, Map<String, Object>>> h = new KeyedHandler<>();

        final Map<String, Map<String, Object>> results = h.handle(getResultSet());

        assertNotNull(results);
        assertEquals(ROW_COUNT, results.size());

        Map<String, Object> row = null;
        for (final Entry<String, Map<String, Object>> entry : results.entrySet()) {
            final Object key = entry.getKey();
            assertNotNull(key);
            row = entry.getValue();
            assertNotNull(row);
            assertEquals(COLUMN_COUNT, row.size());
        }
        row = results.get("1");
        assertEquals("1", row.get("one"));
        assertEquals("2", row.get("TWO"));
        assertEquals("THREE", row.get("Three"));
    }

    @Test
    void testInjectedRowProcess() throws Exception {
        final RowProcessor mockProc = mock(RowProcessor.class);
        final ResultSetHandler<Map<String, Map<String, Object>>> h = new KeyedHandler<>(mockProc);
        final Map<String, Map<String, Object>> results = h.handle(getResultSet());

        assertNotNull(results);
        assertEquals(ROW_COUNT, results.size());

        Map<String, Object> row = null;
        for (final Entry<String, Map<String, Object>> entry : results.entrySet()) {
            row = entry.getValue();
            assertNotNull(row);
            assertTrue(row.isEmpty());
            assertEquals(0, row.size());
        }
    }
}
