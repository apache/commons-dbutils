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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.dbutils.BaseTestCase;
import org.apache.commons.dbutils.ResultSetHandler;
import org.junit.jupiter.api.Test;

/**
 * ArrayListHandlerTest
 */
public class ArrayListHandlerTest extends BaseTestCase {

    @Test
    public void testEmptyResultSetHandle() throws SQLException {
        final ResultSetHandler<List<Object[]>> h = new ArrayListHandler();
        final List<Object[]> results = h.handle(getEmptyResultSet());

        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    public void testHandle() throws SQLException {
        final ResultSetHandler<List<Object[]>> h = new ArrayListHandler();
        final List<Object[]> results = h.handle(getResultSet());

        assertNotNull(results);
        assertEquals(ROWS, results.size());

        final Iterator<Object[]> iter = results.iterator();
        assertTrue(iter.hasNext());
        Object[] row = iter.next();
        assertEquals(COLS, row.length);
        assertEquals("1", row[0]);
        assertEquals("2", row[1]);
        assertEquals("THREE", row[2]);

        assertTrue(iter.hasNext());
        row = iter.next();
        assertEquals(COLS, row.length);

        assertEquals("4", row[0]);
        assertEquals("5", row[1]);
        assertEquals("SIX", row[2]);

        assertFalse(iter.hasNext());
    }

}
