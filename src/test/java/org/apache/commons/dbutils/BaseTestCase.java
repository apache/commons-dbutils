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

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Timestamp;
import java.util.Date;

import junit.framework.TestCase;

/**
 * BaseTestCase is the base class for all test cases as well as the "all tests"
 * runner.
 */
public class BaseTestCase extends TestCase {

    private static final String[] columnNames =
        new String[] {
            "one",
            "two",
            "three",
            "notInBean",
            "intTest",
            "integerTest",
            "nullObjectTest",
            "nullPrimitiveTest",
            "notDate",
            "columnProcessorDoubleTest" };

    /**
     * The number of columns in the MockResultSet.
     */
    protected static final int COLS = columnNames.length;

    protected static final ResultSetMetaData metaData =
        MockResultSetMetaData.create(columnNames);

    /**
     * A Timestamp for test purposes having 9 decimals
     */
    static final Timestamp ts789456123;

    static {
        ts789456123 = new Timestamp(new Date().getTime());
        ts789456123.setNanos(789456123);
    }
    
    private static final Object[] row1 =
        new Object[] {
            "1",
            "2",
            "THREE",
            "  notInBean  ",
            Integer.valueOf(1),
            Integer.valueOf(2),
            null,
            null,
            new Date(),
            BigInteger.valueOf(13)};

    private static final Object[] row2 =
        new Object[] {
            "4",
            "5",
            "SIX",
            "  notInBean  ",
            Integer.valueOf(3),
            Integer.valueOf(4),
            null,
            null,
            ts789456123,
            BigInteger.valueOf(13)};

    private static final Object[][] rows = new Object[][] { row1, row2 };

    /**
     * The number of rows in the MockResultSet.
     */
    protected static final int ROWS = rows.length;

    /**
     * The ResultSet all test methods will use.
     */
    protected ResultSet rs = null;

    /**
     * A ResultSet with 0 rows.
     */
    protected ResultSet emptyResultSet = null;

    /**
     * This is called before each test method so ResultSet will be fresh each
     * time.
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        rs = this.createMockResultSet();
        emptyResultSet = MockResultSet.create(metaData, null);
    }

    /**
     * Creates a freshly initialized ResultSet.
     */
    protected ResultSet createMockResultSet() {
        return MockResultSet.create(metaData, rows);
    }

    // Test which allows Eclipse to be run on full project (avoids no tests found)
    // check that the rows are valid for the column definition
    public void testCheckDataSizes() {
        assertEquals("Row 1 must contain correct number of columns", columnNames.length, row1.length);
        assertEquals("Row 1 must contain correct number of columns", columnNames.length, row2.length);
    }

    public void testResultSets() throws Exception {
        assertFalse("emptyResultSet should be empty", emptyResultSet.next());
        // fails in SqlNullCheckedResultSetTest assertTrue("rs should not be empty", rs.next());
    }
}
