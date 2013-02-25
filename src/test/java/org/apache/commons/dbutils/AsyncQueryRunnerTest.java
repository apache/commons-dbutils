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

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.apache.commons.dbutils.handlers.ArrayHandler;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@SuppressWarnings("boxing") // test code
public class AsyncQueryRunnerTest {
    AsyncQueryRunner runner;
    ArrayHandler handler;

    @Mock QueryRunner qRunner;
    @Mock Connection conn;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);    // init the mocks

         handler = new ArrayHandler();
         runner = new AsyncQueryRunner(Executors.newFixedThreadPool(1), qRunner);
    }

    //
    // Batch test cases
    //
    private void callGoodBatch(Connection conn, Object[][] params) throws Exception {
        Future<int[]> future = runner.batch(conn, "select * from blah where ? = ?", params);

        future.get();

        verify(qRunner, times(1)).batch(eq(conn), any(String.class), eq(params));
    }

    private void callGoodBatch(Object[][] params) throws Exception {
        Future<int[]> future = runner.batch("select * from blah where ? = ?", params);

        future.get();
        
        verify(qRunner, times(1)).batch(any(String.class), eq(params));
    }

    @Test
    public void testGoodBatch() throws Exception {
        String[][] params = new String[][] { { "unit", "unit" }, { "test", "test" } };

        callGoodBatch(params);
    }

    @Test
    public void testGoodBatchPmdTrue() throws Exception {
        String[][] params = new String[][] { { "unit", "unit" }, { "test", "test" } };

        callGoodBatch(params);
    }

    @Test
    public void testGoodBatchDefaultConstructor() throws Exception {
        String[][] params = new String[][] { { "unit", "unit" }, { "test", "test" } };

        callGoodBatch(conn, params);
    }

    @Test
    public void testNullParamsBatch() throws Exception {
        String[][] params = new String[][] { { null, "unit" }, { "test", null } };

        callGoodBatch(params);
    }



    // helper method for calling batch when an exception is expected
    private void callBatchWithException(String sql, Object[][] params) throws Exception {
        Future<int[]> future = null;
        boolean caught = false;

        try {
            future = runner.batch(sql, params);
            
            future.get();

            verify(qRunner, times(1)).batch(any(String.class), params);
        } catch(Exception e) {
            caught = true;
        }

        if(!caught)
            fail("Exception never thrown, but expected");
    }

    @Test
    public void testTooFewParamsBatch() throws Exception {
        String[][] params = new String[][] { { "unit" }, { "test" } };

        callBatchWithException("select * from blah where ? = ?", params);
    }

    @Test
    public void testTooManyParamsBatch() throws Exception {
        String[][] params = new String[][] { { "unit", "unit", "unit" }, { "test", "test", "test" } };

        callBatchWithException("select * from blah where ? = ?", params);
    }

    @Test(expected=ExecutionException.class)
    public void testExceptionBatch() throws Exception {
        doThrow(ExecutionException.class).when(qRunner).batch(any(String.class), any(Object[][].class));
        runner.batch("select * from blah where ? = ?", null).get();
    }

    @Test
    public void testBatchException() throws Exception {
        String[][] params = new String[][] { { "unit", "unit" }, { "test", "test" } };

        doThrow(new SQLException()).when(qRunner).batch(any(String.class), any(Object[][].class));

        callBatchWithException("select * from blah where ? = ?", params);
    }


    //
    // Query test cases
    //
    private void callGoodQuery(Connection conn) throws Exception {
        runner.query(conn, "select * from blah where ? = ?", handler, "unit", "test").get();
        
        verify(qRunner, times(1)).query(eq(conn), any(String.class), eq(handler), any(String.class), any(String.class));

        // call the other variation of query
        runner.query(conn, "select * from blah", handler).get();

        verify(qRunner, times(1)).query(eq(conn), any(String.class), eq(handler));
    }

    private void callGoodQuery() throws Exception {
        runner.query("select * from blah where ? = ?", handler, "unit", "test").get();

        verify(qRunner, times(1)).query(any(String.class), eq(handler), any(String.class), any(String.class));

        // call the other variation of query
        runner.query("select * from blah", handler).get();

        verify(qRunner, times(1)).query(any(String.class), eq(handler));
    }

    @Test
    public void testGoodQuery() throws Exception {
        callGoodQuery();
    }

    @Test
    public void testGoodQueryWithConn() throws Exception {
        callGoodQuery(conn);
    }

    @Test(expected=ExecutionException.class)
    public void testExecuteQueryException() throws Exception {
        doThrow(new SQLException()).when(qRunner).query(any(String.class), eq(handler), any(String.class), any(String.class));

        runner.query("select * from blah where ? = ?", handler, "unit", "test").get();
    }


    //
    // Update test cases
    //
    private void callGoodUpdate(Connection conn) throws Exception {
        runner.update(conn, "update blah set ? = ?", "unit", "test").get();

        verify(qRunner, times(1)).update(eq(conn), any(String.class), any(String.class), any(String.class));

        // call the other variation
        runner.update(conn, "update blah set unit = test").get();

        verify(qRunner, times(1)).update(eq(conn), any(String.class));

        // call the other variation
        runner.update(conn, "update blah set unit = ?", "test").get();

        verify(qRunner, times(1)).update(eq(conn), any(String.class), any(String.class));
    }

    private void callGoodUpdate() throws Exception {
        runner.update("update blah set ? = ?", "unit", "test").get();

        verify(qRunner, times(1)).update(any(String.class), any(String.class), any(String.class));

        // call the other variation
        runner.update("update blah set unit = test").get();

        verify(qRunner, times(1)).update(any(String.class));

        // call the other variation
        runner.update("update blah set unit = ?", "test").get();

        verify(qRunner, times(1)).update(any(String.class), any(String.class));
    }

    @Test
    public void testGoodUpdate() throws Exception {
        callGoodUpdate();
    }

    @Test
    public void testGoodUpdateConnection() throws Exception {
        callGoodUpdate(conn);
    }

    
    @Test
    public void testInsertUsesGivenQueryRunner() throws Exception {
    	runner.insert("1", handler);
    	runner.insert("2", handler, "param1");
    	runner.insert(conn, "3", handler);
    	runner.insert(conn, "4", handler, "param1");
    	
    	verify(qRunner).insert("1", handler);
    	verify(qRunner).insert("2", handler, "param1");
    	verify(qRunner).insert(conn, "3", handler);
    	verify(qRunner).insert(conn, "4", handler, "param1");
    }

    @Test(expected=ExecutionException.class)
    public void testExceptionUpdate() throws Exception {
        doThrow(new SQLException()).when(qRunner).update(any(String.class), any(String.class), any(String.class));
        
        runner.update("select * from blah where ? = ?", "unit", "test").get();
        
        verify(qRunner, times(1)).update(any(String.class), any(String.class), any(String.class));
    }

}
