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
import java.util.List;

import org.apache.commons.dbutils.BaseTestCase;
import org.apache.commons.dbutils.ResultSetHandler;

/**
 * ColumnListHandlerTest
 */
public class ColumnListHandlerTest extends BaseTestCase {


    public void testColumnIndexHandle() throws SQLException {
        final ResultSetHandler<List<String>> h = new ColumnListHandler<>(2);
        final List<String> results = h.handle(this.getResultSet());

        assertNotNull(results);
        assertEquals(ROWS, results.size());

        assertEquals("2", results.get(0));
        assertEquals("5", results.get(1));
    }

    public void testColumnNameHandle() throws SQLException {
        final ResultSetHandler<List<Integer>> h = new ColumnListHandler<>("intTest");
        final List<Integer> results = h.handle(this.getResultSet());

        assertNotNull(results);
        assertEquals(ROWS, results.size());

        assertEquals(Integer.valueOf(1), results.get(0));
        assertEquals(Integer.valueOf(3), results.get(1));
    }

    public void testEmptyResultSetHandle() throws SQLException {
        final ResultSetHandler<List<String>> h = new ColumnListHandler<>();
        final List<String> results = h.handle(this.getEmptyResultSet());

        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    public void testHandle() throws SQLException {
        final ResultSetHandler<List<String>> h = new ColumnListHandler<>();
        final List<String> results = h.handle(this.getResultSet());

        assertNotNull(results);
        assertEquals(ROWS, results.size());

        assertEquals("1", results.get(0));
        assertEquals("4", results.get(1));
    }

}
