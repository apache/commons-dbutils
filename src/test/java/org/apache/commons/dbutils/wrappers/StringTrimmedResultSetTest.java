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
package org.apache.commons.dbutils.wrappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbutils.BaseTestCase;
import org.apache.commons.dbutils.MockResultSet;
import org.apache.commons.dbutils.ProxyFactory;

/**
 * StringTrimmedResultSetTest
 */
public class StringTrimmedResultSetTest extends BaseTestCase {

    private final static int COLUMN_INDEX = 4;
    private final static String EXPECTED_VALUE = "notInBean";

    @Override
    public void setUp() throws Exception {
        super.setUp();
        this.setResultSet(StringTrimmedResultSet.wrap(this.getResultSet()));
    }

    public void testGetObject() throws SQLException {
        this.getResultSet().next();
        assertEquals(EXPECTED_VALUE, this.getResultSet().getObject(COLUMN_INDEX));
    }

    public void testGetString() throws SQLException {
        this.getResultSet().next();
        assertEquals("notInBean", getResultSet().getObject(COLUMN_INDEX));
    }

    /**
     * Make sure 2 wrappers work together.
     *
     * @throws SQLException if a database access error occurs
     */
    public void testMultipleWrappers() throws Exception {
        // Create a ResultSet with data
        final Object[][] rows = { { null } };
        ResultSet resultSet = MockResultSet.create(metaData, rows);

        // Wrap the ResultSet with a null checked version
        final SqlNullCheckedResultSet ncrs = new SqlNullCheckedResultSet(resultSet);
        ncrs.setNullString("   trim this   ");
        resultSet = ProxyFactory.instance().createResultSet(ncrs);

        // Wrap the wrapper with a string trimmed version
        resultSet = StringTrimmedResultSet.wrap(resultSet);

        resultSet.next();
        assertEquals("trim this", resultSet.getString(1));
    }

}
