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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

public class DbUtilsTest {

    @Test
    public void closeNullConnection() throws Exception {
        DbUtils.close((Connection) null);
    }

    @Test
    public void closeConnection() throws Exception {
        final Connection mockCon = mock(Connection.class);
        DbUtils.close(mockCon);
        verify(mockCon).close();
    }

    @Test
    public void closeNullResultSet() throws Exception {
        DbUtils.close((ResultSet) null);
    }

    @Test
    public void closeResultSet() throws Exception {
        final ResultSet mockResultSet = mock(ResultSet.class);
        DbUtils.close(mockResultSet);
        verify(mockResultSet).close();
    }

    @Test
    public void closeNullStatement() throws Exception {
        DbUtils.close((Statement) null);
    }

    @Test
    public void closeStatement() throws Exception {
        final Statement mockStatement = mock(Statement.class);
        DbUtils.close(mockStatement);
        verify(mockStatement).close();
    }

    @Test
    public void closeQuietlyNullConnection() throws Exception {
        DbUtils.closeQuietly((Connection) null);
    }

    @Test
    public void closeQuietlyConnection() throws Exception {
        final Connection mockConnection = mock(Connection.class);
        DbUtils.closeQuietly(mockConnection);
        verify(mockConnection).close();
    }

    @Test
    public void closeQuietlyConnectionThrowingException() throws Exception {
        final Connection mockConnection = mock(Connection.class);
        doThrow(SQLException.class).when(mockConnection).close();
        DbUtils.closeQuietly(mockConnection);
    }

    @Test
    public void closeQuietlyNullResultSet() throws Exception {
        DbUtils.closeQuietly((ResultSet) null);
    }

    @Test
    public void closeQuietlyResultSet() throws Exception {
        final ResultSet mockResultSet = mock(ResultSet.class);
        DbUtils.closeQuietly(mockResultSet);
        verify(mockResultSet).close();
    }

    @Test
    public void closeQuietlyResultSetThrowingException() throws Exception {
        final ResultSet mockResultSet = mock(ResultSet.class);
        doThrow(SQLException.class).when(mockResultSet).close();
        DbUtils.closeQuietly(mockResultSet);
    }

    @Test
    public void closeQuietlyNullStatement() throws Exception {
        DbUtils.closeQuietly((Statement) null);
    }

    @Test
    public void closeQuietlyStatement() throws Exception {
        final Statement mockStatement = mock(Statement.class);
        DbUtils.closeQuietly(mockStatement);
        verify(mockStatement).close();
    }

    @Test
    public void closeQuietlyStatementThrowingException() throws Exception {
        final Statement mockStatement = mock(Statement.class);
        doThrow(SQLException.class).when(mockStatement).close();
        DbUtils.closeQuietly(mockStatement);
    }

    @Test
    public void closeQuietlyConnectionResultSetStatement() throws Exception {
        final Connection mockConnection = mock(Connection.class);
        final ResultSet mockResultSet = mock(ResultSet.class);
        final Statement mockStatement = mock(Statement.class);
        DbUtils.closeQuietly(mockConnection, mockStatement, mockResultSet);
        verify(mockConnection).close();
        verify(mockResultSet).close();
        verify(mockStatement).close();
    }

    @Test
    public void closeQuietlyConnectionThrowingExceptionResultSetStatement() throws Exception {
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
    public void closeQuietlyConnectionResultSetThrowingExceptionStatement() throws Exception {
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
    public void closeQuietlyConnectionResultSetStatementThrowingException() throws Exception {
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
    public void commitAndClose() throws Exception {
        final Connection mockConnection = mock(Connection.class);
        DbUtils.commitAndClose(mockConnection);
        verify(mockConnection).commit();
        verify(mockConnection).close();
    }

    @Test
    public void commitAndCloseWithException() throws Exception {
        final Connection mockConnection = mock(Connection.class);
        doThrow(SQLException.class).when(mockConnection).commit();
        try {
            DbUtils.commitAndClose(mockConnection);
            fail("DbUtils.commitAndClose() swallowed SQLEception!");
        } catch (final SQLException e) {
            // we expect this exception
        }
        verify(mockConnection).close();
    }

    @Test
    public void commitAndCloseQuietly() throws Exception {
        final Connection mockConnection = mock(Connection.class);
        DbUtils.commitAndClose(mockConnection);
        verify(mockConnection).commit();
        verify(mockConnection).close();
    }

    @Test
    public void commitAndCloseQuietlyWithException() throws Exception {
        final Connection mockConnection = mock(Connection.class);
        doThrow(SQLException.class).when(mockConnection).close();
        DbUtils.commitAndCloseQuietly(mockConnection);
        verify(mockConnection).commit();
        verify(mockConnection).close();
    }

    @Test
    public void rollbackNull() throws Exception {
        DbUtils.rollback(null);
    }

    @Test
    public void rollback() throws Exception {
        final Connection mockConnection = mock(Connection.class);
        DbUtils.rollback(mockConnection);
        verify(mockConnection).rollback();
    }

    @Test
    public void rollbackAndCloseNull() throws Exception {
        DbUtils.rollbackAndClose(null);
    }

    @Test
    public void rollbackAndClose() throws Exception {
        final Connection mockConnection = mock(Connection.class);
        DbUtils.rollbackAndClose(mockConnection);
        verify(mockConnection).rollback();
        verify(mockConnection).close();
    }

    @Test
    public void rollbackAndCloseWithException() throws Exception {
        final Connection mockConnection = mock(Connection.class);
        doThrow(SQLException.class).when(mockConnection).rollback();
        try {
            DbUtils.rollbackAndClose(mockConnection);
            fail("DbUtils.rollbackAndClose() swallowed SQLException!");
        } catch (final SQLException e) {
            // we expect this exeption
        }
        verify(mockConnection).rollback();
        verify(mockConnection).close();
    }

    @Test
    public void rollbackAndCloseQuietlyNull() throws Exception {
        DbUtils.rollbackAndCloseQuietly(null);
    }

    @Test
    public void rollbackAndCloseQuietly() throws Exception {
        final Connection mockConnection = mock(Connection.class);
        DbUtils.rollbackAndCloseQuietly(mockConnection);
        verify(mockConnection).rollback();
        verify(mockConnection).close();
    }

    @Test
    public void rollbackAndCloseQuietlyWithException() throws Exception {
        final Connection mockConnection = mock(Connection.class);
        doThrow(SQLException.class).when(mockConnection).rollback();
        DbUtils.rollbackAndCloseQuietly(mockConnection);
        verify(mockConnection).rollback();
        verify(mockConnection).close();
    }

    @Test
    public void testLoadDriverReturnsFalse() {

        assertFalse(DbUtils.loadDriver(""));

    }

    @Test
    public void testCommitAndCloseQuietlyWithNullDoesNotThrowAnSQLException() {

        DbUtils.commitAndCloseQuietly(null);

    }

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

            String url = "testUrl";
            proxy.acceptsURL(url);
            verify(mockedDriver).acceptsURL(url);

            Properties props = new Properties();
            props.setProperty("test", "true");
            proxy.connect(url, props);
            verify(mockedDriver).connect(url, props);
            proxy.getPropertyInfo(url, props);
            verify(mockedDriver).getPropertyInfo(url, props);
        }
    }
}
