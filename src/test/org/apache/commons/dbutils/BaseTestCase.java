/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//dbutils/src/test/org/apache/commons/dbutils/BaseTestCase.java,v 1.4 2003/11/28 21:11:33 dgraham Exp $
 * $Revision: 1.4 $
 * $Date: 2003/11/28 21:11:33 $
 * 
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
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
 * 
 * @author David Graham
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
