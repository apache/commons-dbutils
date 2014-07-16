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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.apache.commons.dbutils.handlers.ArrayHandler;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@SuppressWarnings("boxing") // test code
public class AsyncQueryRunnerTest {
    AsyncQueryRunner runner;
    ArrayHandler handler;

    @Mock DataSource dataSource;
    @Mock Connection conn;
    @Mock PreparedStatement stmt;
    @Mock ParameterMetaData meta;
    @Mock ResultSet results;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(dataSource.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(any(String.class))).thenReturn(stmt);
        when(stmt.getParameterMetaData()).thenReturn(meta);
        when(stmt.getResultSet()).thenReturn(results);
        when(stmt.executeQuery()).thenReturn(results);
        when(results.next()).thenReturn(false);

         handler = new ArrayHandler();
         runner = new AsyncQueryRunner(Executors.newFixedThreadPool(1), new QueryRunner(dataSource));
    }

    //
    // Batch test cases
    //
    private void callGoodBatch(Connection conn, Object[][] params) throws Exception {
        when(meta.getParameterCount()).thenReturn(2);
        Future<int[]> future = runner.batch(conn, "select * from blah where ? = ?", params);

        future.get();

        verify(stmt, times(2)).addBatch();
        verify(stmt, times(1)).executeBatch();
        verify(stmt, times(1)).close();    // make sure we closed the statement
        verify(conn, times(0)).close();    // make sure we closed the connection
    }

    private void callGoodBatch(Object[][] params) throws Exception {
        when(meta.getParameterCount()).thenReturn(2);
        Future<int[]> future = runner.batch("select * from blah where ? = ?", params);

        future.get();
        
        verify(stmt, times(2)).addBatch();
        verify(stmt, times(1)).executeBatch();
        verify(stmt, times(1)).close();    // make sure we closed the statement
        verify(conn, times(1)).close();    // make sure we closed the connection
    }

    @Test
    public void testGoodBatch() throws Exception {
        String[][] params = new String[][] { { "unit", "unit" }, { "test", "test" } };

        callGoodBatch(params);
    }

    @SuppressWarnings("deprecation") // deliberate test of deprecated code
    @Test
    public void testGoodBatchPmdTrue() throws Exception {
        runner = new AsyncQueryRunner(dataSource, true, Executors.newFixedThreadPool(1));
        String[][] params = new String[][] { { "unit", "unit" }, { "test", "test" } };

        callGoodBatch(params);
    }

    @Test
    public void testGoodBatchDefaultConstructor() throws Exception {
        runner = new AsyncQueryRunner(Executors.newFixedThreadPool(1));
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
            
            verify(stmt, times(2)).addBatch();
            verify(stmt, times(1)).executeBatch();
            verify(stmt, times(1)).close();    // make sure the statement is closed
            verify(conn, times(1)).close();    // make sure the connection is closed
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
    public void testNullConnectionBatch() throws Exception {
        String[][] params = new String[][] { { "unit", "unit" }, { "test", "test" } };

        when(meta.getParameterCount()).thenReturn(2);
        when(dataSource.getConnection()).thenReturn(null);

        runner.batch("select * from blah where ? = ?", params).get();
    }

    @Test(expected=ExecutionException.class)
    public void testNullSqlBatch() throws Exception {
        String[][] params = new String[][] { { "unit", "unit" }, { "test", "test" } };

        when(meta.getParameterCount()).thenReturn(2);

        runner.batch(null, params).get();
    }

    @Test(expected=ExecutionException.class)
    public void testNullParamsArgBatch() throws Exception {
        when(meta.getParameterCount()).thenReturn(2);

        runner.batch("select * from blah where ? = ?", null).get();
    }

    @Test
    public void testAddBatchException() throws Exception {
        String[][] params = new String[][] { { "unit", "unit" }, { "test", "test" } };

        doThrow(new SQLException()).when(stmt).addBatch();

        callBatchWithException("select * from blah where ? = ?", params);
    }

    @Test
    public void testExecuteBatchException() throws Exception {
        String[][] params = new String[][] { { "unit", "unit" }, { "test", "test" } };

        doThrow(new SQLException()).when(stmt).executeBatch();

        callBatchWithException("select * from blah where ? = ?", params);
    }


    //
    // Query test cases
    //
    private void callGoodQuery(Connection conn) throws Exception {
        when(meta.getParameterCount()).thenReturn(2);
        runner.query(conn, "select * from blah where ? = ?", handler, "unit", "test").get();

        verify(stmt, times(1)).executeQuery();
        verify(results, times(1)).close();
        verify(stmt, times(1)).close();    // make sure we closed the statement
        verify(conn, times(0)).close();    // make sure we closed the connection

        // call the other variation of query
        when(meta.getParameterCount()).thenReturn(0);
        runner.query(conn, "select * from blah", handler).get();

        verify(stmt, times(2)).executeQuery();
        verify(results, times(2)).close();
        verify(stmt, times(2)).close();    // make sure we closed the statement
        verify(conn, times(0)).close();    // make sure we closed the connection
    }

    private void callGoodQuery() throws Exception {
        when(meta.getParameterCount()).thenReturn(2);
        runner.query("select * from blah where ? = ?", handler, "unit", "test").get();

        verify(stmt, times(1)).executeQuery();
        verify(results, times(1)).close();
        verify(stmt, times(1)).close();    // make sure we closed the statement
        verify(conn, times(1)).close();    // make sure we closed the connection

        // call the other variation of query
        when(meta.getParameterCount()).thenReturn(0);
        runner.query("select * from blah", handler).get();

        verify(stmt, times(2)).executeQuery();
        verify(results, times(2)).close();
        verify(stmt, times(2)).close();    // make sure we closed the statement
        verify(conn, times(2)).close();    // make sure we closed the connection
    }

    @Test
    public void testGoodQuery() throws Exception {
        callGoodQuery();
    }

    @SuppressWarnings("deprecation") // deliberate test of deprecated code
    @Test
    public void testGoodQueryPmdTrue() throws Exception {
        runner = new AsyncQueryRunner(true, Executors.newFixedThreadPool(1));
        callGoodQuery(conn);
    }

    @Test
    public void testGoodQueryDefaultConstructor() throws Exception {
        runner = new AsyncQueryRunner(Executors.newFixedThreadPool(1));
        callGoodQuery(conn);
    }


    // helper method for calling batch when an exception is expected
    private void callQueryWithException(Object... params) throws Exception {
        boolean caught = false;

        try {
            when(meta.getParameterCount()).thenReturn(2);
            runner.query("select * from blah where ? = ?", handler, params).get();

            verify(stmt, times(1)).executeQuery();
            verify(results, times(1)).close();
            verify(stmt, times(1)).close();    // make sure we closed the statement
            verify(conn, times(1)).close();    // make sure we closed the connection
        } catch(Exception e) {
            caught = true;
        }

        if(!caught)
            fail("Exception never thrown, but expected");
    }

    @Test
    public void testNoParamsQuery() throws Exception {
        callQueryWithException();
    }

    @Test
    public void testTooFewParamsQuery() throws Exception {
        callQueryWithException("unit");
    }

    @Test
    public void testTooManyParamsQuery() throws Exception {
        callQueryWithException("unit", "test", "fail");
    }

    @Test(expected=ExecutionException.class)
    public void testNullConnectionQuery() throws Exception {
        when(meta.getParameterCount()).thenReturn(2);
        when(dataSource.getConnection()).thenReturn(null);

        runner.query("select * from blah where ? = ?", handler, "unit", "test").get();
    }

    @Test(expected=ExecutionException.class)
    public void testNullSqlQuery() throws Exception {
        when(meta.getParameterCount()).thenReturn(2);

        runner.query(null, handler).get();
    }

    @Test(expected=ExecutionException.class)
    public void testNullHandlerQuery() throws Exception {
        when(meta.getParameterCount()).thenReturn(2);

        runner.query("select * from blah where ? = ?", null).get();
    }

    @Test
    public void testExecuteQueryException() throws Exception {
        doThrow(new SQLException()).when(stmt).executeQuery();

        callQueryWithException(handler, "unit", "test");
    }


    //
    // Update test cases
    //
    private void callGoodUpdate(Connection conn) throws Exception {
        when(meta.getParameterCount()).thenReturn(2);
        runner.update(conn, "update blah set ? = ?", "unit", "test").get();

        verify(stmt, times(1)).executeUpdate();
        verify(stmt, times(1)).close();    // make sure we closed the statement
        verify(conn, times(0)).close();    // make sure we closed the connection

        // call the other variation
        when(meta.getParameterCount()).thenReturn(0);
        runner.update(conn, "update blah set unit = test").get();

        verify(stmt, times(2)).executeUpdate();
        verify(stmt, times(2)).close();    // make sure we closed the statement
        verify(conn, times(0)).close();    // make sure we closed the connection

        // call the other variation
        when(meta.getParameterCount()).thenReturn(1);
        runner.update(conn, "update blah set unit = ?", "test").get();

        verify(stmt, times(3)).executeUpdate();
        verify(stmt, times(3)).close();    // make sure we closed the statement
        verify(conn, times(0)).close();    // make sure we closed the connection
    }

    private void callGoodUpdate() throws Exception {
        when(meta.getParameterCount()).thenReturn(2);
        runner.update("update blah set ? = ?", "unit", "test").get();

        verify(stmt, times(1)).executeUpdate();
        verify(stmt, times(1)).close();    // make sure we closed the statement
        verify(conn, times(1)).close();    // make sure we closed the connection

        // call the other variation
        when(meta.getParameterCount()).thenReturn(0);
        runner.update("update blah set unit = test").get();

        verify(stmt, times(2)).executeUpdate();
        verify(stmt, times(2)).close();    // make sure we closed the statement
        verify(conn, times(2)).close();    // make sure we closed the connection

        // call the other variation
        when(meta.getParameterCount()).thenReturn(1);
        runner.update("update blah set unit = ?", "test").get();

        verify(stmt, times(3)).executeUpdate();
        verify(stmt, times(3)).close();    // make sure we closed the statement
        verify(conn, times(3)).close();    // make sure we closed the connection
    }

    @Test
    public void testGoodUpdate() throws Exception {
        callGoodUpdate();
    }

    @SuppressWarnings("deprecation") // deliberate test of deprecated code
    @Test
    public void testGoodUpdatePmdTrue() throws Exception {
        runner = new AsyncQueryRunner(true, Executors.newFixedThreadPool(1));
        callGoodUpdate(conn);
    }

    @Test
    public void testGoodUpdateDefaultConstructor() throws Exception {
        runner = new AsyncQueryRunner(Executors.newFixedThreadPool(1));
        callGoodUpdate(conn);
    }

    // helper method for calling batch when an exception is expected
    private void callUpdateWithException(Object... params) throws Exception {
        boolean caught = false;

        try {
            when(meta.getParameterCount()).thenReturn(2);
            runner.update("select * from blah where ? = ?", params).get();

            verify(stmt, times(1)).executeUpdate();
            verify(stmt, times(1)).close();    // make sure we closed the statement
            verify(conn, times(1)).close();    // make sure we closed the connection
        } catch(Exception e) {
            caught = true;
        }

        if(!caught)
            fail("Exception never thrown, but expected");
    }

    @Test
    public void testNoParamsUpdate() throws Exception {
        callUpdateWithException();
    }

    @Test
    public void testTooFewParamsUpdate() throws Exception {
        callUpdateWithException("unit");
    }

    @Test
    public void testTooManyParamsUpdate() throws Exception {
        callUpdateWithException("unit", "test", "fail");
    }
    
    @Test
    public void testInsertUsesGivenQueryRunner() throws Exception {
        QueryRunner mockQueryRunner = mock(QueryRunner.class
                , org.mockito.Mockito.withSettings().verboseLogging() // debug for Continuum
                );
        runner = new AsyncQueryRunner(Executors.newSingleThreadExecutor(), mockQueryRunner);

        runner.insert("1", handler);
        runner.insert("2", handler, "param1");
        runner.insert(conn, "3", handler);
        runner.insert(conn, "4", handler, "param1");

        // give the Executor time to submit all insert statements. Otherwise the following verify statements will fail from time to time.
        TimeUnit.MILLISECONDS.sleep(50);

        verify(mockQueryRunner).insert("1", handler);
        verify(mockQueryRunner).insert("2", handler, "param1");
        verify(mockQueryRunner).insert(conn, "3", handler);
        verify(mockQueryRunner).insert(conn, "4", handler, "param1");
    }

    @Test(expected=ExecutionException.class)
    public void testNullConnectionUpdate() throws Exception {
        when(meta.getParameterCount()).thenReturn(2);
        when(dataSource.getConnection()).thenReturn(null);

        runner.update("select * from blah where ? = ?", "unit", "test").get();
    }

    @Test(expected=ExecutionException.class)
    public void testNullSqlUpdate() throws Exception {
        when(meta.getParameterCount()).thenReturn(2);

        runner.update(null).get();
    }

    @Test
    public void testExecuteUpdateException() throws Exception {
        doThrow(new SQLException()).when(stmt).executeUpdate();

        callUpdateWithException("unit", "test");
    }

    //
    // Random tests
    //
    @Test(expected=ExecutionException.class)
    public void testBadPrepareConnection() throws Exception {
        runner = new AsyncQueryRunner(Executors.newFixedThreadPool(1));
        runner.update("update blah set unit = test").get();
    }
}
