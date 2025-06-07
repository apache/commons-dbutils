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
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Timestamp;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * BaseTestCase is the base class for all test cases as well as the "all tests" runner.
 */
public class BaseTestCase {

    protected static final String[] COLUMN_NAMES = { "one", "two", "three", "notInBean", "intTest", "integerTest", "nullObjectTest", "nullPrimitiveTest",
            "notDate", "columnProcessorDoubleTest", null };

    /**
     * The number of columns in the MockResultSet.
     */
    protected static final int COLUMN_COUNT = COLUMN_NAMES.length;

    protected static final ResultSetMetaData META_DATA = MockResultSetMetaData.create(COLUMN_NAMES);

    /**
     * A Timestamp for test purposes having 9 decimals
     */
    static final Timestamp TS_789456123;

    static {
        TS_789456123 = new Timestamp(new Date().getTime());
        TS_789456123.setNanos(789456123);
    }

    private static final Object[] row1 = { "1", "2", "THREE", "  notInBean  ", Integer.valueOf(1), Integer.valueOf(2), null, null, new Date(),
            BigInteger.valueOf(13), null };

    private static final Object[] row2 = { "4", "5", "SIX", "  notInBean  ", Integer.valueOf(3), Integer.valueOf(4), null, null, TS_789456123,
            BigInteger.valueOf(13), null };

    protected static final Object[][] ROW_ARRAY = { row1, row2 };

    /**
     * The number of rows in the MockResultSet.
     */
    protected static final int ROW_COUNT = ROW_ARRAY.length;

    /**
     * The ResultSet all test methods will use.
     */
    private ResultSet resultSet;

    /**
     * A ResultSet with 0 rows.
     */
    private ResultSet emptyResultSet;

    protected ResultSet createEmptyMockResultSet() {
        return MockResultSet.create(META_DATA, null, false);
    }

    /**
     * Creates a freshly initialized ResultSet.
     *
     * @return a freshly initialized ResultSet.
     */
    protected ResultSet createMockResultSet() {
        return MockResultSet.create(META_DATA, ROW_ARRAY, false);
    }

    public ResultSet getEmptyResultSet() {
        return emptyResultSet;
    }

    public ResultSet getResultSet() {
        return resultSet;
    }

    public void setResultSet(final ResultSet resultSet) {
        this.resultSet = resultSet;
    }

    /**
     * This is called before each test method so ResultSet will be fresh each time.
     *
     * @see org.junit.jupiter.api.BeforeEach
     */
    @BeforeEach
    protected void setUp() throws Exception {
        resultSet = createMockResultSet();
        emptyResultSet = createEmptyMockResultSet();
    }

    // Test which allows Eclipse to be run on full project (avoids no tests found)
    // check that the rows are valid for the column definition
    @Test
    void testCheckDataSizes() {
        assertEquals(COLUMN_NAMES.length, row1.length, "Row 1 must contain correct number of columns");
        assertEquals(COLUMN_NAMES.length, row2.length, "Row 1 must contain correct number of columns");
    }

    @Test
    void testResultSets() throws Exception {
        assertFalse(emptyResultSet.next(), "emptyResultSet should be empty");
        // fails in SqlNullCheckedResultSetTest assertTrue("rs should not be empty", rs.next());
    }
}
