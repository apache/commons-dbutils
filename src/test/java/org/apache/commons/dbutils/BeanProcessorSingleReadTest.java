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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

/**
 * Tests single column access and SQL NULL conversions.
 */
class BeanProcessorSingleReadTest {

    private static final BeanProcessor BEAN_PROCESSOR = new BeanProcessor();

    @Test
    void testStringColumnReadsOnce() throws Exception {
        final ResultSet mockRs = Mockito.mock(ResultSet.class);
        final AtomicInteger reads = new AtomicInteger();
        // Derby rejects a second getter on a LOB column with SQLState XCL18.
        final Answer<String> readColumn = invocation -> {
            if (reads.incrementAndGet() > 1) {
                throw new SQLException("LOB column already read", "XCL18");
            }
            return "hello";
        };
        Mockito.when(mockRs.getObject(1)).thenAnswer(readColumn);
        Mockito.when(mockRs.getString(1)).thenAnswer(readColumn);

        assertEquals("hello", BEAN_PROCESSOR.processColumn(mockRs, 1, String.class));
        assertEquals(1, reads.get());
    }

    @Test
    void testIntegerNullBoxedAndPrimitive() throws Exception {
        final ResultSet mockRs = Mockito.mock(ResultSet.class);
        Mockito.when(mockRs.getInt(1)).thenReturn(0);
        Mockito.when(mockRs.wasNull()).thenReturn(true);

        final Object boxedResult = BEAN_PROCESSOR.processColumn(mockRs, 1, Integer.class);
        assertNull(boxedResult);

        final Object primitiveResult = BEAN_PROCESSOR.processColumn(mockRs, 1, Integer.TYPE);
        assertEquals(0, primitiveResult);

    }

    @Test
    void testBooleanNullBoxedAndPrimitive() throws Exception {
        final ResultSet mockRs = Mockito.mock(ResultSet.class);
        Mockito.when(mockRs.getBoolean(1)).thenReturn(false);
        Mockito.when(mockRs.wasNull()).thenReturn(true);

        final Object boxedResult = BEAN_PROCESSOR.processColumn(mockRs, 1, Boolean.class);
        assertNull(boxedResult);

        final Object primitiveResult = BEAN_PROCESSOR.processColumn(mockRs, 1, Boolean.TYPE);
        assertEquals(false, primitiveResult);

    }

    @Test
    void testNonNullDefaultValues() throws Exception {
        final ResultSet mockRs = Mockito.mock(ResultSet.class);
        Mockito.when(mockRs.getObject(1)).thenReturn(0);
        Mockito.when(mockRs.getInt(1)).thenReturn(0);
        Mockito.when(mockRs.getObject(2)).thenReturn(false);
        Mockito.when(mockRs.getBoolean(2)).thenReturn(false);
        Mockito.when(mockRs.wasNull()).thenReturn(false);

        assertEquals(0, BEAN_PROCESSOR.processColumn(mockRs, 1, Integer.class));
        assertEquals(0, BEAN_PROCESSOR.processColumn(mockRs, 1, Integer.TYPE));
        assertEquals(false, BEAN_PROCESSOR.processColumn(mockRs, 2, Boolean.class));
        assertEquals(false, BEAN_PROCESSOR.processColumn(mockRs, 2, Boolean.TYPE));
    }

    @Test
    void testNullableStringColumn() throws Exception {
        final ResultSet mockRs = Mockito.mock(ResultSet.class);
        Mockito.when(mockRs.getString(1)).thenReturn(null);
        Mockito.when(mockRs.wasNull()).thenReturn(true);

        final Object result = BEAN_PROCESSOR.processColumn(mockRs, 1, String.class);
        assertNull(result);

    }

    @Test
    void testUnhandledTypeFallsBackToGetObject() throws Exception {
        final ResultSet mockRs = Mockito.mock(ResultSet.class);
        final Object expected = new Object();
        Mockito.when(mockRs.getObject(1)).thenReturn(expected);

        final Object result = BEAN_PROCESSOR.processColumn(mockRs, 1, Object.class);
        assertSame(expected, result);
    }

    @Test
    void testSQLExceptionPropagationFromHandler() throws Exception {
        final ResultSet mockRs = Mockito.mock(ResultSet.class);
        final SQLException exception = new SQLException("DB error", "S1000");
        Mockito.when(mockRs.getObject(1)).thenReturn("value");
        Mockito.when(mockRs.getString(1)).thenThrow(exception);

        final SQLException thrown = assertThrows(SQLException.class,
            () -> BEAN_PROCESSOR.processColumn(mockRs, 1, String.class));
        assertSame(exception, thrown);
    }
}
