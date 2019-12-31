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

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.sql.SQLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class AsyncExecutorTest {
    AsyncExecutor runner;

    @Mock QueryRunner qRunner;
    @Mock ResultSetHandler<Object> handler;
    @Mock QueryExecutor queryExecutor;
    @Mock UpdateExecutor updateExecutor;
    @Mock InsertExecutor insertExecutor;
    
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);    // init the mocks

         runner = new AsyncExecutor(Executors.newFixedThreadPool(1));
    }

    @Test
    public void testQueryExecutor() throws Exception {
        runner.execute(queryExecutor, handler).get();
        
        verify(queryExecutor, times(1)).execute(handler);
    }

    @Test(expected=ExecutionException.class)
    public void testQueryExecutorException() throws Exception {
        doThrow(SQLException.class).when(queryExecutor).execute(handler);
        runner.execute(queryExecutor, handler).get();
        
        verify(queryExecutor, times(1)).execute(handler);
    }

    @Test
    public void testUpdateExecutor() throws Exception {
        runner.execute(updateExecutor).get();
        
        verify(updateExecutor, times(1)).execute();
    }

    @Test(expected=ExecutionException.class)
    public void testUpdateExecutorException() throws Exception {
        doThrow(SQLException.class).when(updateExecutor).execute();
        runner.execute(updateExecutor).get();
        
        verify(updateExecutor, times(1)).execute();
    }

    @Test
    public void testInsertExecutor() throws Exception {
        runner.execute(insertExecutor, handler).get();
        
        verify(insertExecutor, times(1)).execute(handler);
    }

    @Test(expected=ExecutionException.class)
    public void testInsertExecutorException() throws Exception {
        doThrow(SQLException.class).when(insertExecutor).execute(handler);
        runner.execute(insertExecutor, handler).get();
        
        verify(insertExecutor, times(1)).execute(handler);
    }


}
