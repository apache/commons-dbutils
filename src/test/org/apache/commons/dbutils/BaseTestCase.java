/*
 * Copyright 2003-2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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
import java.util.Date;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.dbutils.handlers.ArrayHandlerTest;
import org.apache.commons.dbutils.handlers.ArrayListHandlerTest;
import org.apache.commons.dbutils.handlers.BeanHandlerTest;
import org.apache.commons.dbutils.handlers.BeanListHandlerTest;
import org.apache.commons.dbutils.handlers.MapHandlerTest;
import org.apache.commons.dbutils.handlers.MapListHandlerTest;
import org.apache.commons.dbutils.handlers.ScalarHandlerTest;
import org.apache.commons.dbutils.wrappers.SqlNullCheckedResultSetTest;
import org.apache.commons.dbutils.wrappers.StringTrimmedResultSetTest;

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

    private static final Object[] row1 =
        new Object[] {
            "1",
            "2",
            "3",
            "  notInBean  ",
            new Integer(1),
            new Integer(2),
            null,
            null,
            new Date(),
            BigInteger.valueOf(13)};

    private static final Object[] row2 =
        new Object[] {
            "4",
            "5",
            "6",
            "  notInBean  ",
            new Integer(3),
            new Integer(4),
            null,
            null,
            new Date(),
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
     * Constructor for BaseTestCase.
     */
    public BaseTestCase(String name) {
        super(name);
    }

    /**
     * This is called before each test method so ResultSet will be fresh each
     * time.
     * @see junit.framework.TestCase#setUp()
     */
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

    /**
     * Return a TestSuite containing all of our test cases.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("All DbUtils Tests");

        suite.addTestSuite(BasicRowProcessorTest.class);
        suite.addTestSuite(BasicColumnProcessorTest.class);
        suite.addTestSuite(ProxyFactoryTest.class);
        suite.addTestSuite(ResultSetIteratorTest.class);
        suite.addTestSuite(QueryLoaderTest.class);

        // test handler implementations
        suite.addTestSuite(ArrayHandlerTest.class);
        suite.addTestSuite(ArrayListHandlerTest.class);
        suite.addTestSuite(BeanHandlerTest.class);
        suite.addTestSuite(BeanListHandlerTest.class);
        suite.addTestSuite(MapHandlerTest.class);
        suite.addTestSuite(MapListHandlerTest.class);
        suite.addTestSuite(ScalarHandlerTest.class);

        suite.addTestSuite(StringTrimmedResultSetTest.class);
        suite.addTestSuite(SqlNullCheckedResultSetTest.class);

        return suite;
    }

}
