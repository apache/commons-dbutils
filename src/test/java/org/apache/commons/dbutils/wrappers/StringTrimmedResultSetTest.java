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

    @Override
    public void setUp() throws Exception {
        super.setUp();
        this.rs = StringTrimmedResultSet.wrap(this.rs);
    }

    public void testGetString() throws SQLException {
        this.rs.next();
        assertEquals("notInBean", rs.getString(4));
    }

    public void testGetObject() throws SQLException {
        this.rs.next();
        assertEquals("notInBean", rs.getObject(4));
    }

    /**
     * Make sure 2 wrappers work together.
     * @throws SQLException if a database access error occurs
     */
    public void testMultipleWrappers() throws Exception {
        // Create a ResultSet with data
        Object[][] rows = new Object[][] { { null }
        };
        ResultSet rs = MockResultSet.create(metaData, rows);

        // Wrap the ResultSet with a null checked version
        SqlNullCheckedResultSet ncrs = new SqlNullCheckedResultSet(rs);
        ncrs.setNullString("   trim this   ");
        rs = ProxyFactory.instance().createResultSet(ncrs);

        // Wrap the wrapper with a string trimmed version
        rs = StringTrimmedResultSet.wrap(rs);

        rs.next();
        assertEquals("trim this", rs.getString(1));
    }

}
