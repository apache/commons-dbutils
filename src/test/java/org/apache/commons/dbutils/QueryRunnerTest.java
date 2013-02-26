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

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@SuppressWarnings("boxing") // test code
public class QueryRunnerTest {
    QueryRunner runner;

    @Mock DataSource dataSource;
    @Mock Connection conn;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);    // init the mocks

        when(dataSource.getConnection()).thenReturn(conn);
        runner = new QueryRunner(dataSource);
    }
    
    // query tests
    
    @Test
    public void testQuerySQL() throws SQLException {        
        assertNotNull(runner.query("select * from blah where :first=first"));
        verify(dataSource, times(1)).getConnection();
    }
    
    @Test
    public void testQueryConnSQL() throws SQLException {
        assertNotNull(runner.query(conn, "select * from blah where :first=first"));
    }
    
    @Test
    public void testQueryConnSQLBoolean() throws SQLException {
        assertNotNull(runner.query(conn, true, "select * from blah where :first=first"));
    }
    
    // insert tests
    
    @Test
    public void testInsertSQL() throws SQLException {        
        assertNotNull(runner.insert("insert * from blah where :first=first"));
        verify(dataSource, times(1)).getConnection();
    }
    
    @Test
    public void testInsertConnSQL() throws SQLException {
        assertNotNull(runner.insert(conn, "insert * from blah where :first=first"));
    }
    
    @Test
    public void testInsertConnSQLBoolean() throws SQLException {
        assertNotNull(runner.insert(conn, true, "insert * from blah where :first=first"));
    }
    
    // update tests
    
    @Test
    public void testUpdateSQL() throws SQLException {        
        assertNotNull(runner.update("select * from blah where :first=first"));
        verify(dataSource, times(1)).getConnection();
    }
    
    @Test
    public void testUpdateConnSQL() throws SQLException {
        assertNotNull(runner.update(conn, "select * from blah where :first=first"));
    }
    
    @Test
    public void testUpdateConnSQLBoolean() throws SQLException {
        assertNotNull(runner.update(conn, true, "select * from blah where :first=first"));
    }
    
}
