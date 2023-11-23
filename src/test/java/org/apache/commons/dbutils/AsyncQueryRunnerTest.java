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
import static org.mockito.ArgumentMatchers.any;
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
import java.sql.Statement;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.apache.commons.dbutils.handlers.ArrayHandler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@SuppressWarnings("boxing") // test code
@RunWith(MockitoJUnitRunner.class)
public class AsyncQueryRunnerTest {
    private AsyncQueryRunner runner;
    private ArrayHandler handler;

    @Mock
    private DataSource dataSource;
    @Mock
    private Connection conn;
    @Mock
    private PreparedStatement prepStmt;
    @Mock
    private Statement stmt;
    @Mock
    private ParameterMetaData meta;
    @Mock
    private ResultSet results;

    // helper method for calling batch when an exception is expected
    private void callBatchWithException(final String sql, final Object[][] params) throws Exception {
        Future<int[]> future = null;
        boolean caught = false;

        try {
            future = runner.batch(sql, params);

            future.get();

            verify(prepStmt, times(2)).addBatch();
            verify(prepStmt, times(1)).executeBatch();
            verify(prepStmt, times(1)).close(); // make sure the statement is closed
            verify(conn, times(1)).close(); // make sure the connection is closed
        } catch (final Exception e) {
            caught = true;
        }

        if (!caught) {
            fail("Exception never thrown, but expected");
        }
    }

    //
    // Batch test cases
    //
    private void callGoodBatch(final Connection conn, final Object[][] params) throws Exception {
        when(meta.getParameterCount()).thenReturn(2);
        final Future<int[]> future = runner.batch(conn, "select * from blah where ? = ?", params);

        future.get();

        verify(prepStmt, times(2)).addBatch();
        verify(prepStmt, times(1)).executeBatch();
        verify(prepStmt, times(1)).close();    // make sure we closed the statement
        verify(conn, times(0)).close();    // make sure we closed the connection
    }

    private void callGoodBatch(final Object[][] params) throws Exception {
        when(meta.getParameterCount()).thenReturn(2);
        final Future<int[]> future = runner.batch("select * from blah where ? = ?", params);

        future.get();

        verify(prepStmt, times(2)).addBatch();
        verify(prepStmt, times(1)).executeBatch();
        verify(prepStmt, times(1)).close();    // make sure we closed the statement
        verify(conn, times(1)).close();    // make sure we closed the connection
    }

    private void callGoodQuery() throws Exception {
        when(meta.getParameterCount()).thenReturn(2);
        String sql = "select * from blah where ? = ?";
        runner.query(sql, handler, "unit", "test").get();

        verify(prepStmt, times(1)).executeQuery();
        verify(results, times(1)).close();
        verify(prepStmt, times(1)).close();    // make sure we closed the statement
        verify(conn, times(1)).close();    // make sure we closed the connection

        // call the other variation of query
        sql = "select * from blah";
        runner.query(sql, handler).get();

        verify(stmt, times(1)).executeQuery(sql);
        verify(results, times(2)).close();
        verify(stmt, times(1)).close();    // make sure we closed the statement
        verify(conn, times(2)).close();    // make sure we closed the connection
    }

    //
    // Query test cases
    //
    private void callGoodQuery(final Connection conn) throws Exception {
        when(meta.getParameterCount()).thenReturn(2);
        String sql = "select * from blah where ? = ?";
        runner.query(conn, sql, handler, "unit", "test").get();

        verify(prepStmt, times(1)).executeQuery();
        verify(results, times(1)).close();
        verify(prepStmt, times(1)).close();    // make sure we closed the statement
        verify(conn, times(0)).close();    // make sure we closed the connection

        // call the other variation of query
        sql = "select * from blah";
        runner.query(conn, sql, handler).get();

        verify(stmt, times(1)).executeQuery(sql);
        verify(results, times(2)).close();
        verify(stmt, times(1)).close();    // make sure we closed the statement
        verify(conn, times(0)).close();    // make sure we closed the connection
    }

    private void callGoodUpdate() throws Exception {
        when(meta.getParameterCount()).thenReturn(2);
        String sql = "update blah set ? = ?";
        runner.update(sql, "unit", "test").get();

        verify(prepStmt, times(1)).executeUpdate();
        verify(prepStmt, times(1)).close();    // make sure we closed the statement
        verify(conn, times(1)).close();    // make sure we closed the connection

        // call the other variation
        when(meta.getParameterCount()).thenReturn(0);
        sql = "update blah set unit = test";
        runner.update(sql).get();

        verify(stmt, times(1)).executeUpdate(sql);
        verify(stmt, times(1)).close();    // make sure we closed the statement
        verify(conn, times(2)).close();    // make sure we closed the connection

        // call the other variation
        when(meta.getParameterCount()).thenReturn(1);
        runner.update("update blah set unit = ?", "test").get();

        verify(prepStmt, times(2)).executeUpdate();
        verify(prepStmt, times(2)).close();    // make sure we closed the statement
        verify(conn, times(3)).close();    // make sure we closed the connection
    }

    //
    // Update test cases
    //
    private void callGoodUpdate(final Connection conn) throws Exception {
        when(meta.getParameterCount()).thenReturn(2);
        String sql = "update blah set ? = ?";
        runner.update(conn, sql, "unit", "test").get();

        verify(prepStmt, times(1)).executeUpdate();
        verify(prepStmt, times(1)).close();    // make sure we closed the statement
        verify(conn, times(0)).close();    // make sure we closed the connection

        // call the other variation
        when(meta.getParameterCount()).thenReturn(0);
        sql = "update blah set unit = test";
        runner.update(conn, sql).get();

        verify(stmt, times(1)).executeUpdate(sql);
        verify(stmt, times(1)).close();    // make sure we closed the statement
        verify(conn, times(0)).close();    // make sure we closed the connection

        // call the other variation
        when(meta.getParameterCount()).thenReturn(1);
        sql = "update blah set unit = ?";
        runner.update(conn, sql, "test").get();

        verify(prepStmt, times(2)).executeUpdate();
        verify(prepStmt, times(2)).close();    // make sure we closed the statement
        verify(conn, times(0)).close();    // make sure we closed the connection
    }

    // helper method for calling batch when an exception is expected
    private void callQueryWithException(final Object... params) throws Exception {
        boolean caught = false;

        try {
            when(meta.getParameterCount()).thenReturn(2);
            runner.query("select * from blah where ? = ?", handler, params).get();

            verify(prepStmt, times(1)).executeQuery();
            verify(results, times(1)).close();
            verify(prepStmt, times(1)).close(); // make sure we closed the statement
            verify(conn, times(1)).close(); // make sure we closed the connection
        } catch (final Exception e) {
            caught = true;
        }

        if (!caught) {
            fail("Exception never thrown, but expected");
        }
    }

    // helper method for calling batch when an exception is expected
    private void callUpdateWithException(final Object... params) throws Exception {
        boolean caught = false;

        try {
            when(meta.getParameterCount()).thenReturn(2);
            runner.update("select * from blah where ? = ?", params).get();

            verify(prepStmt, times(1)).executeUpdate();
            verify(prepStmt, times(1)).close(); // make sure we closed the statement
            verify(conn, times(1)).close(); // make sure we closed the connection
        } catch (final Exception e) {
            caught = true;
        }

        if (!caught) {
            fail("Exception never thrown, but expected");
        }
    }

    @Before
    public void setUp() throws Exception {
        when(dataSource.getConnection()).thenReturn(conn);

        when(conn.prepareStatement(any(String.class))).thenReturn(prepStmt);
        when(prepStmt.getParameterMetaData()).thenReturn(meta);
        when(prepStmt.executeQuery()).thenReturn(results);

        when(conn.createStatement()).thenReturn(stmt);
        when(stmt.executeQuery(any(String.class))).thenReturn(results);

        when(results.next()).thenReturn(false);

         handler = new ArrayHandler();
         runner = new AsyncQueryRunner(Executors.newFixedThreadPool(1), new QueryRunner(dataSource));
    }

    @Test
    public void testAddBatchException() throws Exception {
        final String[][] params = { { "unit", "unit" }, { "test", "test" } };

        callBatchWithException("select * from blah where ? = ?", params);
    }

    //
    // Random tests
    //
    @Test(expected = ExecutionException.class)
    public void testBadPrepareConnection() throws Exception {
        runner = new AsyncQueryRunner(Executors.newFixedThreadPool(1));
        runner.update("update blah set unit = test").get();
    }

    @Test
    public void testExecuteBatchException() throws Exception {
        final String[][] params = { { "unit", "unit" }, { "test", "test" } };

        callBatchWithException("select * from blah where ? = ?", params);
    }

    @Test
    public void testExecuteQueryException() throws Exception {
        callQueryWithException(handler, "unit", "test");
    }

    @Test
    public void testExecuteUpdateException() throws Exception {
        doThrow(new SQLException()).when(prepStmt).executeUpdate();

        callUpdateWithException("unit", "test");
    }

    @Test
    public void testGoodBatch() throws Exception {
        final String[][] params = { { "unit", "unit" }, { "test", "test" } };

        callGoodBatch(params);
    }

    @Test
    public void testGoodBatchDefaultConstructor() throws Exception {
        runner = new AsyncQueryRunner(Executors.newFixedThreadPool(1));
        final String[][] params = { { "unit", "unit" }, { "test", "test" } };

        callGoodBatch(conn, params);
    }

    @SuppressWarnings("deprecation") // deliberate test of deprecated code
    @Test
    public void testGoodBatchPmdTrue() throws Exception {
        runner = new AsyncQueryRunner(dataSource, true, Executors.newFixedThreadPool(1));
        final String[][] params = { { "unit", "unit" }, { "test", "test" } };

        callGoodBatch(params);
    }

    @Test
    public void testGoodQuery() throws Exception {
        callGoodQuery();
    }

    @Test
    public void testGoodQueryDefaultConstructor() throws Exception {
        runner = new AsyncQueryRunner(Executors.newFixedThreadPool(1));
        callGoodQuery(conn);
    }

    @SuppressWarnings("deprecation") // deliberate test of deprecated code
    @Test
    public void testGoodQueryPmdTrue() throws Exception {
        runner = new AsyncQueryRunner(true, Executors.newFixedThreadPool(1));
        callGoodQuery(conn);
    }

    @Test
    public void testGoodUpdate() throws Exception {
        callGoodUpdate();
    }

    @Test
    public void testGoodUpdateDefaultConstructor() throws Exception {
        runner = new AsyncQueryRunner(Executors.newFixedThreadPool(1));
        callGoodUpdate(conn);
    }

    @SuppressWarnings("deprecation") // deliberate test of deprecated code
    @Test
    public void testGoodUpdatePmdTrue() throws Exception {
        runner = new AsyncQueryRunner(true, Executors.newFixedThreadPool(1));
        callGoodUpdate(conn);
    }

    @Test
    public void testInsertUsesGivenQueryRunner() throws Exception {
        final QueryRunner mockQueryRunner = mock(QueryRunner.class, org.mockito.Mockito.withSettings().verboseLogging() // debug for Continuum
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

    @Test
    public void testNoParamsQuery() throws Exception {
        callGoodQuery();
    }

    @Test
    public void testNoParamsUpdate() throws Exception {
        callGoodUpdate();
    }

    @Test(expected = ExecutionException.class)
    public void testNullConnectionBatch() throws Exception {
        final String[][] params = { { "unit", "unit" }, { "test", "test" } };

        when(dataSource.getConnection()).thenReturn(null);

        runner.batch("select * from blah where ? = ?", params).get();
    }

    @Test(expected = ExecutionException.class)
    public void testNullConnectionQuery() throws Exception {
        when(dataSource.getConnection()).thenReturn(null);

        runner.query("select * from blah where ? = ?", handler, "unit", "test").get();
    }

    @Test(expected = ExecutionException.class)
    public void testNullConnectionUpdate() throws Exception {
        when(dataSource.getConnection()).thenReturn(null);

        runner.update("select * from blah where ? = ?", "unit", "test").get();
    }

    @Test(expected = ExecutionException.class)
    public void testNullHandlerQuery() throws Exception {
        runner.query("select * from blah where ? = ?", null).get();
    }

    @Test(expected = ExecutionException.class)
    public void testNullParamsArgBatch() throws Exception {
        runner.batch("select * from blah where ? = ?", null).get();
    }

    @Test
    public void testNullParamsBatch() throws Exception {
        final String[][] params = { { null, "unit" }, { "test", null } };

        callGoodBatch(params);
    }

    @Test(expected = ExecutionException.class)
    public void testNullSqlBatch() throws Exception {
        final String[][] params = { { "unit", "unit" }, { "test", "test" } };

        runner.batch(null, params).get();
    }

    @Test(expected = ExecutionException.class)
    public void testNullSqlQuery() throws Exception {
        runner.query(null, handler).get();
    }

    @Test(expected = ExecutionException.class)
    public void testNullSqlUpdate() throws Exception {
        runner.update(null).get();
    }

    @Test
    public void testTooFewParamsBatch() throws Exception {
        final String[][] params = { { "unit" }, { "test" } };

        callBatchWithException("select * from blah where ? = ?", params);
    }

    @Test
    public void testTooFewParamsQuery() throws Exception {
        callQueryWithException("unit");
    }

    @Test
    public void testTooFewParamsUpdate() throws Exception {
        callUpdateWithException("unit");
    }

    @Test
    public void testTooManyParamsBatch() throws Exception {
        final String[][] params = { { "unit", "unit", "unit" }, { "test", "test", "test" } };

        callBatchWithException("select * from blah where ? = ?", params);
    }

    @Test
    public void testTooManyParamsQuery() throws Exception {
        callQueryWithException("unit", "test", "fail");
    }

    @Test
    public void testTooManyParamsUpdate() throws Exception {
        callUpdateWithException("unit", "test", "fail");
    }
}
