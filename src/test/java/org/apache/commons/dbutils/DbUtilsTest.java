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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DbUtilsTest {

    public static class DriverProxyTest {
        private static final Driver mockedDriver = mock(Driver.class);
        private DbUtils.DriverProxy proxy;

        @Before
        public void setUp() {
            proxy = new DbUtils.DriverProxy(mockedDriver);
        }

        @After
        public void tearDown() {
            reset(mockedDriver);
        }

        @Test
        public void testProxiedMethods() throws Exception {
            proxy.getMajorVersion();
            verify(mockedDriver).getMajorVersion();
            proxy.getMinorVersion();
            verify(mockedDriver).getMinorVersion();
            proxy.jdbcCompliant();
            verify(mockedDriver).jdbcCompliant();

            final String url = "testUrl";
            proxy.acceptsURL(url);
            verify(mockedDriver).acceptsURL(url);

            final Properties props = new Properties();
            props.setProperty("test", "true");
            proxy.connect(url, props);
            verify(mockedDriver).connect(url, props);
            proxy.getPropertyInfo(url, props);
            verify(mockedDriver).getPropertyInfo(url, props);
        }
    }

    @Test
    public void testCloseConnection() throws Exception {
        final Connection mockCon = mock(Connection.class);
        DbUtils.close(mockCon);
        verify(mockCon).close();
    }

    @Test
    public void testCloseNullConnection() throws Exception {
        DbUtils.close((Connection) null);
    }

    @Test
    public void testCloseNullResultSet() throws Exception {
        DbUtils.close((ResultSet) null);
    }

    @Test
    public void testCloseNullStatement() throws Exception {
        DbUtils.close((Statement) null);
    }

    @Test
    public void testCloseQuietlyConnection() throws Exception {
        final Connection mockConnection = mock(Connection.class);
        DbUtils.closeQuietly(mockConnection);
        verify(mockConnection).close();
    }

    @Test
    public void testCloseQuietlyConnectionResultSetStatement() throws Exception {
        final Connection mockConnection = mock(Connection.class);
        final ResultSet mockResultSet = mock(ResultSet.class);
        final Statement mockStatement = mock(Statement.class);
        DbUtils.closeQuietly(mockConnection, mockStatement, mockResultSet);
        verify(mockConnection).close();
        verify(mockResultSet).close();
        verify(mockStatement).close();
    }

    @Test
    public void testCloseQuietlyConnectionResultSetStatementThrowingException() throws Exception {
        final Connection mockConnection = mock(Connection.class);
        final ResultSet mockResultSet = mock(ResultSet.class);
        final Statement mockStatement = mock(Statement.class);
        doThrow(SQLException.class).when(mockStatement).close();
        DbUtils.closeQuietly(mockConnection, mockStatement, mockResultSet);
        verify(mockConnection).close();
        verify(mockResultSet).close();
        verify(mockStatement).close();
    }

    @Test
    public void testCloseQuietlyConnectionResultSetThrowingExceptionStatement() throws Exception {
        final Connection mockConnection = mock(Connection.class);
        final ResultSet mockResultSet = mock(ResultSet.class);
        doThrow(SQLException.class).when(mockResultSet).close();
        final Statement mockStatement = mock(Statement.class);
        DbUtils.closeQuietly(mockConnection, mockStatement, mockResultSet);
        verify(mockConnection).close();
        verify(mockResultSet).close();
        verify(mockStatement).close();
    }

    @Test
    public void testCloseQuietlyConnectionThrowingException() throws Exception {
        final Connection mockConnection = mock(Connection.class);
        doThrow(SQLException.class).when(mockConnection).close();
        DbUtils.closeQuietly(mockConnection);
    }

    @Test
    public void testCloseQuietlyConnectionThrowingExceptionResultSetStatement() throws Exception {
        final Connection mockConnection = mock(Connection.class);
        doThrow(SQLException.class).when(mockConnection).close();
        final ResultSet mockResultSet = mock(ResultSet.class);
        final Statement mockStatement = mock(Statement.class);
        DbUtils.closeQuietly(mockConnection, mockStatement, mockResultSet);
        verify(mockConnection).close();
        verify(mockResultSet).close();
        verify(mockStatement).close();
    }

    @Test
    public void testCloseQuietlyNullConnection() throws Exception {
        DbUtils.closeQuietly((Connection) null);
    }

    @Test
    public void testCloseQuietlyNullResultSet() throws Exception {
        DbUtils.closeQuietly((ResultSet) null);
    }

    @Test
    public void testCloseQuietlyNullStatement() throws Exception {
        DbUtils.closeQuietly((Statement) null);
    }

    @Test
    public void testCloseQuietlyResultSet() throws Exception {
        final ResultSet mockResultSet = mock(ResultSet.class);
        DbUtils.closeQuietly(mockResultSet);
        verify(mockResultSet).close();
    }

    @Test
    public void testCloseQuietlyResultSetThrowingException() throws Exception {
        final ResultSet mockResultSet = mock(ResultSet.class);
        doThrow(SQLException.class).when(mockResultSet).close();
        DbUtils.closeQuietly(mockResultSet);
    }

    @Test
    public void testCloseQuietlyStatement() throws Exception {
        final Statement mockStatement = mock(Statement.class);
        DbUtils.closeQuietly(mockStatement);
        verify(mockStatement).close();
    }

    @Test
    public void testCloseQuietlyStatementThrowingException() throws Exception {
        final Statement mockStatement = mock(Statement.class);
        doThrow(SQLException.class).when(mockStatement).close();
        DbUtils.closeQuietly(mockStatement);
    }

    @Test
    public void testCloseResultSet() throws Exception {
        final ResultSet mockResultSet = mock(ResultSet.class);
        DbUtils.close(mockResultSet);
        verify(mockResultSet).close();
    }

    @Test
    public void testCloseStatement() throws Exception {
        final Statement mockStatement = mock(Statement.class);
        DbUtils.close(mockStatement);
        verify(mockStatement).close();
    }

    @Test
    public void testCommitAndClose() throws Exception {
        final Connection mockConnection = mock(Connection.class);
        DbUtils.commitAndClose(mockConnection);
        verify(mockConnection).commit();
        verify(mockConnection).close();
    }

    @Test
    public void testCommitAndCloseQuietly() throws Exception {
        final Connection mockConnection = mock(Connection.class);
        DbUtils.commitAndClose(mockConnection);
        verify(mockConnection).commit();
        verify(mockConnection).close();
    }

    @Test
    public void testCommitAndCloseQuietlyWithException() throws Exception {
        final Connection mockConnection = mock(Connection.class);
        doThrow(SQLException.class).when(mockConnection).close();
        DbUtils.commitAndCloseQuietly(mockConnection);
        verify(mockConnection).commit();
        verify(mockConnection).close();
    }

    @Test
    public void testCommitAndCloseQuietlyWithNullDoesNotThrowAnSQLException() {

        DbUtils.commitAndCloseQuietly(null);

    }

    @Test
    public void testCommitAndCloseWithException() throws Exception {
        final Connection mockConnection = mock(Connection.class);
        doThrow(SQLException.class).when(mockConnection).commit();
        assertThrows(SQLException.class, () ->
                DbUtils.commitAndClose(mockConnection));
    }

    @Test
    public void testLoadDriverReturnsFalse() {
        assertFalse(DbUtils.loadDriver(""));
    }

    @Test
    public void testRollback() throws Exception {
        final Connection mockConnection = mock(Connection.class);
        DbUtils.rollback(mockConnection);
        verify(mockConnection).rollback();
    }

    @Test
    public void testRollbackAndClose() throws Exception {
        final Connection mockConnection = mock(Connection.class);
        DbUtils.rollbackAndClose(mockConnection);
        verify(mockConnection).rollback();
        verify(mockConnection).close();
    }

    @Test
    public void testRollbackAndCloseNull() throws Exception {
        DbUtils.rollbackAndClose(null);
    }

    @Test
    public void testRollbackAndCloseQuietly() throws Exception {
        final Connection mockConnection = mock(Connection.class);
        DbUtils.rollbackAndCloseQuietly(mockConnection);
        verify(mockConnection).rollback();
        verify(mockConnection).close();
    }

    @Test
    public void testRollbackAndCloseQuietlyNull() throws Exception {
        DbUtils.rollbackAndCloseQuietly(null);
    }

    @Test
    public void testRollbackAndCloseQuietlyWithException() throws Exception {
        final Connection mockConnection = mock(Connection.class);
        doThrow(SQLException.class).when(mockConnection).rollback();
        DbUtils.rollbackAndCloseQuietly(mockConnection);
        verify(mockConnection).rollback();
        verify(mockConnection).close();
    }

    @Test
    public void testRollbackAndCloseWithException() throws Exception {
        final Connection mockConnection = mock(Connection.class);
        doThrow(SQLException.class).when(mockConnection).rollback();
        assertThrows(SQLException.class, () ->
                DbUtils.rollbackAndClose(mockConnection));
    }

    @Test
    public void testRollbackNull() throws Exception {
        DbUtils.rollback(null);
    }

    @Test
    public void testRollbackQuietly() throws Exception {
        final Connection mockConnection = mock(Connection.class);
        DbUtils.rollbackQuietly(mockConnection);
        verify(mockConnection).rollback();
    }

    @Test
    public void testRollbackQuietlyNull() throws Exception {
        DbUtils.rollbackQuietly(null);
    }

    @Test
    public void testRollbackQuietlyWithException() throws Exception {
        final Connection mockConnection = mock(Connection.class);
        doThrow(SQLException.class).when(mockConnection).rollback();
        DbUtils.rollbackQuietly(mockConnection);
        verify(mockConnection).rollback();
    }
}
