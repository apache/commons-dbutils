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
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.BaseTestCase;
import org.apache.commons.dbutils.ResultSetHandler;

/**
 * MapListHandlerTest
 */
public class MapListHandlerTest extends BaseTestCase {

    public void testEmptyResultSetHandle() throws SQLException {
        final ResultSetHandler<List<Map<String, Object>>> h = new MapListHandler();
        final List<Map<String, Object>> results = h.handle(this.getEmptyResultSet());

        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    public void testHandle() throws SQLException {
        final ResultSetHandler<List<Map<String, Object>>> h = new MapListHandler();
        final List<Map<String, Object>> results = h.handle(this.getResultSet());

        assertNotNull(results);
        assertEquals(ROWS, results.size());

        final Iterator<Map<String, Object>> iter = results.iterator();
        assertTrue(iter.hasNext());
        Map<String, Object> row = iter.next();
        assertEquals(COLS, row.size());
        assertEquals("1", row.get("one"));
        assertEquals("2", row.get("TWO"));
        assertEquals("THREE", row.get("Three"));

        assertTrue(iter.hasNext());
        row = iter.next();
        assertEquals(COLS, row.size());

        assertEquals("4", row.get("one"));
        assertEquals("5", row.get("TWO"));
        assertEquals("SIX", row.get("Three"));

        assertFalse(iter.hasNext());
    }

}
