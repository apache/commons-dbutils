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
package org.apache.commons.dbutils2;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.commons.dbutils2.UpdateExecutor;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


public class UpdateExecutorTest {

    private UpdateExecutor executor;
    
    @Mock private Connection conn;
    @Mock private PreparedStatement stmt;
    
    @SuppressWarnings("boxing") // test code
    @Before
    public void setup() throws SQLException {
        MockitoAnnotations.initMocks(this);
        
        when(conn.prepareStatement(any(String.class))).thenReturn(stmt);
        when(stmt.executeUpdate()).thenReturn(20);
    }
    
    protected void createExecutor(String sql) throws Exception {
        executor = new UpdateExecutor(conn, sql, true);
    }
    
    @Test
    public void testGoodSQL() throws Exception {
        createExecutor("insert into blah");
        
        int ret = executor.execute();
        
        assertEquals(20, ret);
        verify(conn, times(1)).close();
        verify(stmt, times(1)).close();
    }
    
    @Test(expected=SQLException.class)
    public void testUnmappedParams() throws Exception {
        createExecutor("insert into blah (:something)");
        
        int ret = executor.execute();
        
        assertEquals(20, ret);
        verify(conn, times(1)).close();
        verify(stmt, times(1)).close();
    }
    
}
