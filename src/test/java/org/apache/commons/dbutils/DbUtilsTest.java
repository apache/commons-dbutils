/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.dbutils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class DbUtilsTest {

    @Nested
    class DriverProxyTest {
        private final Driver mockedDriver = mock(Driver.class);
        private DbUtils.DriverProxy proxy;

        @BeforeEach
        public void setUp() {
            proxy = new DbUtils.DriverProxy(mockedDriver);
        }

        @AfterEach
        public void tearDown() {
            reset(mockedDriver);
        }

        @Test
        void testProxiedMethods() throws Exception {
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
    void testCloseConnection() throws Exception {
        final Connection mockCon = mock(Connection.class);
        DbUtils.close(mockCon);
        verify(mockCon).close();
    }

    @Test
    void testCloseNullConnection() throws Exception {
        DbUtils.close((Connection) null);
    }

    @Test
    void testCloseNullResultSet() throws Exception {
        DbUtils.close((ResultSet) null);
    }

    @Test
    void testCloseNullStatement() throws Exception {
        DbUtils.close((Statement) null);
    }

    @Test
    void testCloseQuietlyConnection() throws Exception {
        final Connection mockConnection = mock(Connection.class);
        DbUtils.closeQuietly(mockConnection);
        verify(mockConnection).close();
    }

    @Test
    void testCloseQuietlyConnectionResultSetStatement() throws Exception {
        final Connection mockConnection = mock(Connection.class);
        final ResultSet mockResultSet = mock(ResultSet.class);
        final Statement mockStatement = mock(Statement.class);
        DbUtils.closeQuietly(mockConnection, mockStatement, mockResultSet);
        verify(mockConnection).close();
        verify(mockResultSet).close();
        verify(mockStatement).close();
    }

    @Test
    void testCloseQuietlyConnectionResultSetStatementThrowingException() throws Exception {
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
    void testCloseQuietlyConnectionResultSetThrowingExceptionStatement() throws Exception {
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
    void testCloseQuietlyConnectionThrowingException() throws Exception {
        final Connection mockConnection = mock(Connection.class);
        doThrow(SQLException.class).when(mockConnection).close();
        DbUtils.closeQuietly(mockConnection);
    }

    @Test
    void testCloseQuietlyConnectionThrowingExceptionResultSetStatement() throws Exception {
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
    void testCloseQuietlyNullConnection() throws Exception {
        DbUtils.closeQuietly((Connection) null);
    }

    @Test
    void testCloseQuietlyNullResultSet() throws Exception {
        DbUtils.closeQuietly((ResultSet) null);
    }

    @Test
    void testCloseQuietlyNullStatement() throws Exception {
        DbUtils.closeQuietly((Statement) null);
    }

    @Test
    void testCloseQuietlyResultSet() throws Exception {
        final ResultSet mockResultSet = mock(ResultSet.class);
        DbUtils.closeQuietly(mockResultSet);
        verify(mockResultSet).close();
    }

    @Test
    void testCloseQuietlyResultSetThrowingException() throws Exception {
        final ResultSet mockResultSet = mock(ResultSet.class);
        doThrow(SQLException.class).when(mockResultSet).close();
        DbUtils.closeQuietly(mockResultSet);
    }

    @Test
    void testCloseQuietlyStatement() throws Exception {
        final Statement mockStatement = mock(Statement.class);
        DbUtils.closeQuietly(mockStatement);
        verify(mockStatement).close();
    }

    @Test
    void testCloseQuietlyStatementThrowingException() throws Exception {
        final Statement mockStatement = mock(Statement.class);
        doThrow(SQLException.class).when(mockStatement).close();
        DbUtils.closeQuietly(mockStatement);
    }

    @Test
    void testCloseResultSet() throws Exception {
        final ResultSet mockResultSet = mock(ResultSet.class);
        DbUtils.close(mockResultSet);
        verify(mockResultSet).close();
    }

    @Test
    void testCloseStatement() throws Exception {
        final Statement mockStatement = mock(Statement.class);
        DbUtils.close(mockStatement);
        verify(mockStatement).close();
    }

    @Test
    void testCommitAndClose() throws Exception {
        final Connection mockConnection = mock(Connection.class);
        DbUtils.commitAndClose(mockConnection);
        verify(mockConnection).commit();
        verify(mockConnection).close();
    }

    @Test
    void testCommitAndCloseQuietly() throws Exception {
        final Connection mockConnection = mock(Connection.class);
        DbUtils.commitAndClose(mockConnection);
        verify(mockConnection).commit();
        verify(mockConnection).close();
    }

    @Test
    void testCommitAndCloseQuietlyWithException() throws Exception {
        final Connection mockConnection = mock(Connection.class);
        doThrow(SQLException.class).when(mockConnection).close();
        DbUtils.commitAndCloseQuietly(mockConnection);
        verify(mockConnection).commit();
        verify(mockConnection).close();
    }

    @Test
    void testCommitAndCloseQuietlyWithNullDoesNotThrowAnSQLException() {

        DbUtils.commitAndCloseQuietly(null);

    }

    @Test
    void testCommitAndCloseWithException() throws Exception {
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
    void testConstructor() throws Exception {
        // For compatibility only
        new DbUtils();
    }

    @Test
    void testLoadDriverClassLoaderH2() throws Exception {
        assertTrue(DbUtils.loadDriver(DbUtils.class.getClassLoader(), "org.h2.Driver"));
    }

    @Test
    void testLoadDriverH2() throws Exception {
        assertTrue(DbUtils.loadDriver("org.h2.Driver"));
    }

    @Test
    void testLoadDriverReturnsFalse() {
        assertFalse(DbUtils.loadDriver(""));
    }

    @Test
    void testPrintStackTraceSQLException() {
        DbUtils.printStackTrace(new SQLException());
    }

    @Test
    void testPrintStackTraceSQLExceptionPrintWriter() {
        final PrintWriter pw = new PrintWriter(new StringWriter());
        DbUtils.printStackTrace(new SQLException(), pw);
        assertFalse(pw.toString().isEmpty());
    }

    @Test
    void testRollback() throws Exception {
        final Connection mockConnection = mock(Connection.class);
        DbUtils.rollback(mockConnection);
        verify(mockConnection).rollback();
    }

    @Test
    void testRollbackAndClose() throws Exception {
        final Connection mockConnection = mock(Connection.class);
        DbUtils.rollbackAndClose(mockConnection);
        verify(mockConnection).rollback();
        verify(mockConnection).close();
    }

    @Test
    void testRollbackAndCloseNull() throws Exception {
        DbUtils.rollbackAndClose(null);
    }

    @Test
    void testRollbackAndCloseQuietly() throws Exception {
        final Connection mockConnection = mock(Connection.class);
        DbUtils.rollbackAndCloseQuietly(mockConnection);
        verify(mockConnection).rollback();
        verify(mockConnection).close();
    }

    @Test
    void testRollbackAndCloseQuietlyNull() throws Exception {
        DbUtils.rollbackAndCloseQuietly(null);
    }

    @Test
    void testRollbackAndCloseQuietlyWithException() throws Exception {
        final Connection mockConnection = mock(Connection.class);
        doThrow(SQLException.class).when(mockConnection).rollback();
        DbUtils.rollbackAndCloseQuietly(mockConnection);
        verify(mockConnection).rollback();
        verify(mockConnection).close();
    }

    @Test
    void testRollbackAndCloseWithException() throws Exception {
        final Connection mockConnection = mock(Connection.class);
        doThrow(SQLException.class).when(mockConnection).rollback();
        try {
            DbUtils.rollbackAndClose(mockConnection);
            fail("DbUtils.rollbackAndClose() swallowed SQLException!");
        } catch (final SQLException e) {
            // we expect this exception
        }
        verify(mockConnection).rollback();
        verify(mockConnection).close();
    }

    @Test
    void testRollbackNull() throws Exception {
        DbUtils.rollback(null);
    }

    @Test
    void testRollbackQuietly() throws Exception {
        final Connection mockConnection = mock(Connection.class);
        DbUtils.rollbackQuietly(mockConnection);
        verify(mockConnection).rollback();
    }

    @Test
    void testRollbackQuietlyNull() throws Exception {
        DbUtils.rollbackQuietly(null);
    }

    @Test
    void testRollbackQuietlyWithException() throws Exception {
        final Connection mockConnection = mock(Connection.class);
        doThrow(SQLException.class).when(mockConnection).rollback();
        DbUtils.rollbackQuietly(mockConnection);
        verify(mockConnection).rollback();
    }
}
