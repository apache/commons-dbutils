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

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


public class BatchExecutorTest {

    private BatchExecutor executor;
    
    @Mock private Connection conn;
    @Mock private PreparedStatement stmt;
    
    @Before
    public void setup() throws SQLException {
        MockitoAnnotations.initMocks(this);
        
        when(conn.prepareStatement(any(String.class))).thenReturn(stmt);
        when(stmt.executeBatch()).thenReturn(new int[] { 2, 3, 4 });
    }
    
    protected void createExecutor(String sql) throws Exception {
        executor = new BatchExecutor(conn, sql, true);
    }
    
    @Test
    public void testGoodSQL() throws Exception {
        createExecutor("insert into blah");
        
        executor.addBatch();
        int[] ret = executor.execute();
        
        assertEquals(3, ret.length);
        assertEquals(2, ret[0]);
        assertEquals(3, ret[1]);
        assertEquals(4, ret[2]);
        verify(conn, times(1)).close();
        verify(stmt, times(1)).close();
    }
    
}
