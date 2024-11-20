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
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.sql.SQLException;

import org.apache.commons.dbutils.BaseTestCase;
import org.apache.commons.dbutils.ResultSetHandler;
import org.junit.jupiter.api.Test;

/**
 * ArrayHandlerTest
 */
public class ArrayHandlerTest extends BaseTestCase {

    @Test
    public void testEmptyResultSetHandle() throws SQLException {
        final ResultSetHandler<Object[]> h = new ArrayHandler();
        final Object[] results = h.handle(getEmptyResultSet());

        assertEquals(0, results.length);
    }

    @Test
    public void testHandle() throws SQLException {
        final ResultSetHandler<Object[]> h = new ArrayHandler();
        final Object[] results = h.handle(getResultSet());

        assertNotNull(results);
        assertEquals(COLS, results.length);
        assertEquals("1", results[0]);
        assertEquals("2", results[1]);
        assertEquals("THREE", results[2]);
    }

}
