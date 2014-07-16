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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.dbutils.handlers.ArrayHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@SuppressWarnings("boxing") // test code
public class QueryRunnerTest {
    QueryRunner runner;
    ArrayHandler handler;

    @Mock DataSource dataSource;
    @Mock Connection conn;
    @Mock PreparedStatement stmt;
    @Mock ParameterMetaData meta;
    @Mock ResultSet results;
    @Mock ResultSetMetaData resultsMeta;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);    // init the mocks

        when(dataSource.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(any(String.class))).thenReturn(stmt);
        when(stmt.getParameterMetaData()).thenReturn(meta);
        when(stmt.getResultSet()).thenReturn(results);
        when(stmt.executeQuery()).thenReturn(results);
        when(results.next()).thenReturn(false);

         handler = new ArrayHandler();
         runner = new QueryRunner(dataSource);
    }

    //
    // Batch test cases
    //

    private void callGoodBatch(Connection conn, Object[][] params) throws Exception {
        when(meta.getParameterCount()).thenReturn(2);
        runner.batch(conn, "select * from blah where ? = ?", params);

        verify(stmt, times(2)).addBatch();
        verify(stmt, times(1)).executeBatch();
        verify(stmt, times(1)).close();    // make sure we closed the statement
        verify(conn, times(0)).close();    // make sure we do not close the connection, since QueryRunner.batch(Connection, String, Object[][]) does not close connections
    }

    private void callGoodBatch(Object[][] params) throws Exception {
        when(meta.getParameterCount()).thenReturn(2);
        runner.batch("select * from blah where ? = ?", params);

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

    @Test
    public void testGoodBatchPmdTrue() throws Exception {
        runner = new QueryRunner(dataSource, true);
        String[][] params = new String[][] { { "unit", "unit" }, { "test", "test" } };

        callGoodBatch(params);
    }

    @Test
    public void testGoodBatchDefaultConstructor() throws Exception {
        runner = new QueryRunner();
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
        boolean caught = false;

        try {
            runner.batch(sql, params);

            verify(stmt, times(2)).addBatch();
            verify(stmt, times(1)).executeBatch();
            verify(stmt, times(1)).close();    // make sure the statement is closed
            verify(conn, times(1)).close();    // make sure the connection is closed
        } catch(SQLException e) {
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

    @Test(expected=SQLException.class)
    public void testNullConnectionBatch() throws Exception {
        String[][] params = new String[][] { { "unit", "unit" }, { "test", "test" } };

        when(meta.getParameterCount()).thenReturn(2);
        when(dataSource.getConnection()).thenReturn(null);

        runner.batch("select * from blah where ? = ?", params);
    }

    @Test(expected=SQLException.class)
    public void testNullSqlBatch() throws Exception {
        String[][] params = new String[][] { { "unit", "unit" }, { "test", "test" } };

        when(meta.getParameterCount()).thenReturn(2);

        runner.batch(null, params);
    }

    @Test(expected=SQLException.class)
    public void testNullParamsArgBatch() throws Exception {
        when(meta.getParameterCount()).thenReturn(2);

        runner.batch("select * from blah where ? = ?", null);
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
        runner.query(conn, "select * from blah where ? = ?", handler, "unit", "test");

        verify(stmt, times(1)).executeQuery();
        verify(results, times(1)).close();
        verify(stmt, times(1)).close();    // make sure we closed the statement
        verify(conn, times(0)).close();    // make sure we do not close the connection, since QueryRunner.query(Connection, String, ResultSetHandler<T>, Object...) does not close connections

        // call the other variation of query
        when(meta.getParameterCount()).thenReturn(0);
        runner.query(conn, "select * from blah", handler);

        verify(stmt, times(2)).executeQuery();
        verify(results, times(2)).close();
        verify(stmt, times(2)).close();    // make sure we closed the statement
        verify(conn, times(0)).close();    // make sure we do not close the connection, see above
    }

    private void callGoodQuery() throws Exception {
        when(meta.getParameterCount()).thenReturn(2);
        runner.query("select * from blah where ? = ?", handler, "unit", "test");

        verify(stmt, times(1)).executeQuery();
        verify(results, times(1)).close();
        verify(stmt, times(1)).close();    // make sure we closed the statement
        verify(conn, times(1)).close();    // make sure we closed the connection

        // call the other variation of query
        when(meta.getParameterCount()).thenReturn(0);
        runner.query("select * from blah", handler);

        verify(stmt, times(2)).executeQuery();
        verify(results, times(2)).close();
        verify(stmt, times(2)).close();    // make sure we closed the statement
        verify(conn, times(2)).close();    // make sure we closed the connection
    }

    @Test
    public void testGoodQuery() throws Exception {
        callGoodQuery();
    }

    @Test
    public void testGoodQueryPmdTrue() throws Exception {
        runner = new QueryRunner(true);
        callGoodQuery(conn);
    }

    @Test
    public void testGoodQueryDefaultConstructor() throws Exception {
        runner = new QueryRunner();
        callGoodQuery(conn);
    }


    // helper method for calling batch when an exception is expected
    private void callQueryWithException(Object... params) throws Exception {
        boolean caught = false;

        try {
            when(meta.getParameterCount()).thenReturn(2);
            runner.query("select * from blah where ? = ?", handler, params);

            verify(stmt, never()).close();    // make sure the statement is still open
            verify(stmt, times(1)).executeQuery();
            verify(results, times(1)).close();
            verify(stmt, times(1)).close();    // make sure we closed the statement
            verify(conn, times(1)).close();    // make sure we closed the connection
        } catch(SQLException e) {
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

    @Test(expected=SQLException.class)
    public void testNullConnectionQuery() throws Exception {
        when(meta.getParameterCount()).thenReturn(2);
        when(dataSource.getConnection()).thenReturn(null);

        runner.query("select * from blah where ? = ?", handler, "unit", "test");
    }

    @Test(expected=SQLException.class)
    public void testNullSqlQuery() throws Exception {
        when(meta.getParameterCount()).thenReturn(2);

        runner.query(null, handler);
    }

    @Test(expected=SQLException.class)
    public void testNullHandlerQuery() throws Exception {
        when(meta.getParameterCount()).thenReturn(2);

        runner.query("select * from blah where ? = ?", null);
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
        runner.update(conn, "update blah set ? = ?", "unit", "test");

        verify(stmt, times(1)).executeUpdate();
        verify(stmt, times(1)).close();    // make sure we closed the statement
        verify(conn, times(0)).close();    // make sure we do not close the connection, since QueryRunner.update(Connection, String, Object...) does not close connections

        // call the other variation
        when(meta.getParameterCount()).thenReturn(0);
        runner.update(conn, "update blah set unit = test");

        verify(stmt, times(2)).executeUpdate();
        verify(stmt, times(2)).close();    // make sure we closed the statement
        verify(conn, times(0)).close();    // make sure we do not close the connection, see above

        // call the other variation
        when(meta.getParameterCount()).thenReturn(1);
        runner.update(conn, "update blah set unit = ?", "test");

        verify(stmt, times(3)).executeUpdate();
        verify(stmt, times(3)).close();    // make sure we closed the statement
        verify(conn, times(0)).close();    // make sure we do not close the connection, see above
    }

    private void callGoodUpdate() throws Exception {
        when(meta.getParameterCount()).thenReturn(2);
        runner.update("update blah set ? = ?", "unit", "test");

        verify(stmt, times(1)).executeUpdate();
        verify(stmt, times(1)).close();    // make sure we closed the statement
        verify(conn, times(1)).close();    // make sure we closed the connection

        // call the other variation
        when(meta.getParameterCount()).thenReturn(0);
        runner.update("update blah set unit = test");

        verify(stmt, times(2)).executeUpdate();
        verify(stmt, times(2)).close();    // make sure we closed the statement
        verify(conn, times(2)).close();    // make sure we closed the connection

        // call the other variation
        when(meta.getParameterCount()).thenReturn(1);
        runner.update("update blah set unit = ?", "test");

        verify(stmt, times(3)).executeUpdate();
        verify(stmt, times(3)).close();    // make sure we closed the statement
        verify(conn, times(3)).close();    // make sure we closed the connection
    }

    @Test
    public void testGoodUpdate() throws Exception {
        callGoodUpdate();
    }

    @Test
    public void testGoodUpdatePmdTrue() throws Exception {
        runner = new QueryRunner(true);
        callGoodUpdate(conn);
    }

    @Test
    public void testGoodUpdateDefaultConstructor() throws Exception {
        runner = new QueryRunner();
        callGoodUpdate(conn);
    }
    
    @Test
    public void testGoodInsert() throws Exception {
        results = mock(ResultSet.class);
        
        when(meta.getParameterCount()).thenReturn(2);
        when(conn.prepareStatement(any(String.class), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(stmt);
        when(stmt.getGeneratedKeys()).thenReturn(results);
        when(results.next()).thenReturn(true).thenReturn(false);
        when(results.getObject(1)).thenReturn(1L);
	
        Long generatedKey = runner.insert("INSERT INTO blah(col1, col2) VALUES(?,?)", new ScalarHandler<Long>(), "unit", "test");

        verify(stmt, times(1)).executeUpdate();
        verify(stmt, times(1)).close();    // make sure we closed the statement
        verify(conn, times(1)).close();    // make sure we closed the connection
        
        Assert.assertEquals(1L, generatedKey.longValue());
    }
    
    @Test
    public void testGoodBatchInsert() throws Exception {
        results = mock(ResultSet.class);
        resultsMeta = mock(ResultSetMetaData.class);
        
        when(meta.getParameterCount()).thenReturn(2);
        when(conn.prepareStatement(any(String.class), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(stmt);
        when(stmt.getGeneratedKeys()).thenReturn(results);
        when(results.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(results.getMetaData()).thenReturn(resultsMeta);
        when(resultsMeta.getColumnCount()).thenReturn(1);
        
        ResultSetHandler<List<Object>> handler = new ResultSetHandler<List<Object>>()
        {
            @Override
            public List<Object> handle(ResultSet rs) throws SQLException
            {
                List<Object> objects = new ArrayList<Object>();
                while (rs.next())
                {
                    objects.add(new Object());
                }
                return objects;
            }
        };
        
        Object[][] params = new Object[2][2];
        params[0][0] = "Test";
        params[0][1] = "Blah";
        params[1][0] = "Test2";
        params[1][1] = "Blah2";
        
        List<Object> generatedKeys = runner.insertBatch("INSERT INTO blah(col1, col2) VALUES(?,?)", handler, params);

        verify(stmt, times(2)).addBatch();
        verify(stmt, times(1)).executeBatch();
        verify(stmt, times(1)).close();    // make sure we closed the statement
        verify(conn, times(1)).close();    // make sure we closed the connection
        
        Assert.assertEquals(2, generatedKeys.size());
    }

    // helper method for calling batch when an exception is expected
    private void callUpdateWithException(Object... params) throws Exception {
        boolean caught = false;

        try {
            when(meta.getParameterCount()).thenReturn(2);
            runner.update("select * from blah where ? = ?", params);

            verify(stmt, times(1)).executeUpdate();
            verify(stmt, times(1)).close();    // make sure we closed the statement
            verify(conn, times(1)).close();    // make sure we closed the connection
        } catch(SQLException e) {
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

    @Test(expected=SQLException.class)
    public void testNullConnectionUpdate() throws Exception {
        when(meta.getParameterCount()).thenReturn(2);
        when(dataSource.getConnection()).thenReturn(null);

        runner.update("select * from blah where ? = ?", "unit", "test");
    }

    @Test(expected=SQLException.class)
    public void testNullSqlUpdate() throws Exception {
        when(meta.getParameterCount()).thenReturn(2);

        runner.update(null);
    }

    @Test
    public void testExecuteUpdateException() throws Exception {
        doThrow(new SQLException()).when(stmt).executeUpdate();

        callUpdateWithException("unit", "test");
    }

    //
    // Random tests
    //
    class MyBean {
        private int a;
        private double b;
        private String c;

        public int getA() {    return a; }
        public void setA(int a) { this.a = a; }
        public double getB() { return b; }
        public void setB(double b) { this.b = b; }
        public String getC() { return c; }
        public void setC(String c) { this.c = c; }
    }

    @Test
    public void testFillStatementWithBean() throws Exception {
        MyBean bean = new MyBean();
        when(meta.getParameterCount()).thenReturn(3);
        runner.fillStatementWithBean(stmt, bean, new String[] { "a", "b", "c" });
    }

    @Test(expected=NullPointerException.class)
    public void testFillStatementWithBeanNullNames() throws Exception {
        MyBean bean = new MyBean();
        when(meta.getParameterCount()).thenReturn(3);
        runner.fillStatementWithBean(stmt, bean, new String[] { "a", "b", null });
    }

    @Test(expected=SQLException.class)
    public void testBadPrepareConnection() throws Exception {
        runner = new QueryRunner();
        runner.update("update blah set unit = test");
    }
}
