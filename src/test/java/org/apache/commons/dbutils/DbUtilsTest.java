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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.Test;

public class DbUtilsTest {

    @Test
    public void closeNullConnection() throws Exception {
        DbUtils.close((Connection) null);
    }

    @Test
    public void closeConnection() throws Exception {
        Connection mockCon = mock(Connection.class);
        DbUtils.close(mockCon);
        verify(mockCon).close();
    }

    @Test
    public void closeNullResultSet() throws Exception {
        DbUtils.close((ResultSet) null);
    }

    @Test
    public void closeResultSet() throws Exception {
        ResultSet mockResultSet = mock(ResultSet.class);
        DbUtils.close(mockResultSet);
        verify(mockResultSet).close();
    }

    @Test
    public void closeNullStatement() throws Exception {
        DbUtils.close((Statement) null);
    }

    @Test
    public void closeStatement() throws Exception {
        Statement mockStatement = mock(Statement.class);
        DbUtils.close(mockStatement);
        verify(mockStatement).close();
    }

    @Test
    public void closeQuietlyNullConnection() throws Exception {
        DbUtils.closeQuietly((Connection) null);
    }

    @Test
    public void closeQuietlyConnection() throws Exception {
        Connection mockConnection = mock(Connection.class);
        DbUtils.closeQuietly(mockConnection);
        verify(mockConnection).close();
    }

    @Test
    public void closeQuietlyConnectionThrowingException() throws Exception {
        Connection mockConnection = mock(Connection.class);
        doThrow(SQLException.class).when(mockConnection).close();
        DbUtils.closeQuietly(mockConnection);
    }

    @Test
    public void closeQuietlyNullResultSet() throws Exception {
        DbUtils.closeQuietly((ResultSet) null);
    }

    @Test
    public void closeQuietlyResultSet() throws Exception {
        ResultSet mockResultSet = mock(ResultSet.class);
        DbUtils.closeQuietly(mockResultSet);
        verify(mockResultSet).close();
    }

    @Test
    public void closeQuietlyResultSetThrowingException() throws Exception {
        ResultSet mockResultSet = mock(ResultSet.class);
        doThrow(SQLException.class).when(mockResultSet).close();
        DbUtils.closeQuietly(mockResultSet);
    }

    @Test
    public void closeQuietlyNullStatement() throws Exception {
        DbUtils.closeQuietly((Statement) null);
    }

    @Test
    public void closeQuietlyStatement() throws Exception {
        Statement mockStatement = mock(Statement.class);
        DbUtils.closeQuietly(mockStatement);
        verify(mockStatement).close();
    }

    @Test
    public void closeQuietlyStatementThrowingException() throws Exception {
        Statement mockStatement = mock(Statement.class);
        doThrow(SQLException.class).when(mockStatement).close();
        DbUtils.closeQuietly(mockStatement);
    }

    @Test
    public void closeQuietlyConnectionResultSetStatement() throws Exception {
        Connection mockConnection = mock(Connection.class);
        ResultSet mockResultSet = mock(ResultSet.class);
        Statement mockStatement = mock(Statement.class);
        DbUtils.closeQuietly(mockConnection, mockStatement, mockResultSet);
        verify(mockConnection).close();
        verify(mockResultSet).close();
        verify(mockStatement).close();
    }

    @Test
    public void closeQuietlyConnectionThrowingExceptionResultSetStatement() throws Exception {
        Connection mockConnection = mock(Connection.class);
        doThrow(SQLException.class).when(mockConnection).close();
        ResultSet mockResultSet = mock(ResultSet.class);
        Statement mockStatement = mock(Statement.class);
        DbUtils.closeQuietly(mockConnection, mockStatement, mockResultSet);
        verify(mockConnection).close();
        verify(mockResultSet).close();
        verify(mockStatement).close();
    }

    @Test
    public void closeQuietlyConnectionResultSetThrowingExceptionStatement() throws Exception {
        Connection mockConnection = mock(Connection.class);
        ResultSet mockResultSet = mock(ResultSet.class);
        doThrow(SQLException.class).when(mockResultSet).close();
        Statement mockStatement = mock(Statement.class);
        DbUtils.closeQuietly(mockConnection, mockStatement, mockResultSet);
        verify(mockConnection).close();
        verify(mockResultSet).close();
        verify(mockStatement).close();
    }

    @Test
    public void closeQuietlyConnectionResultSetStatementThrowingException() throws Exception {
        Connection mockConnection = mock(Connection.class);
        ResultSet mockResultSet = mock(ResultSet.class);
        Statement mockStatement = mock(Statement.class);
        doThrow(SQLException.class).when(mockStatement).close();
        DbUtils.closeQuietly(mockConnection, mockStatement, mockResultSet);
        verify(mockConnection).close();
        verify(mockResultSet).close();
        verify(mockStatement).close();
    }

    @Test
    public void commitAndClose() throws Exception {
        Connection mockConnection = mock(Connection.class);
        DbUtils.commitAndClose(mockConnection);
        verify(mockConnection).commit();
        verify(mockConnection).close();
    }

    @Test
    public void commitAndCloseWithException() throws Exception {
        Connection mockConnection = mock(Connection.class);
        doThrow(SQLException.class).when(mockConnection).commit();
        try {
            DbUtils.commitAndClose(mockConnection);
            fail("DbUtils.commitAndClose() swallowed SQLEception!");
        } catch (SQLException e) {
            // we expect this exception
        }
        verify(mockConnection).close();
    }

    @Test
    public void commitAndCloseQuietly() throws Exception {
        Connection mockConnection = mock(Connection.class);
        DbUtils.commitAndClose(mockConnection);
        verify(mockConnection).commit();
        verify(mockConnection).close();
    }

    @Test
    public void commitAndCloseQuietlyWithException() throws Exception {
        Connection mockConnection = mock(Connection.class);
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
        Connection mockConnection = mock(Connection.class);
        DbUtils.rollback(mockConnection);
        verify(mockConnection).rollback();
    }

    @Test
    public void rollbackAndCloseNull() throws Exception {
        DbUtils.rollbackAndClose(null);
    }

    @Test
    public void rollbackAndClose() throws Exception {
        Connection mockConnection = mock(Connection.class);
        DbUtils.rollbackAndClose(mockConnection);
        verify(mockConnection).rollback();
        verify(mockConnection).close();
    }

    @Test
    public void rollbackAndCloseWithException() throws Exception {
        Connection mockConnection = mock(Connection.class);
        doThrow(SQLException.class).when(mockConnection).rollback();
        try {
            DbUtils.rollbackAndClose(mockConnection);
            fail("DbUtils.rollbackAndClose() swallowed SQLException!");
        } catch (SQLException e) {
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
        Connection mockConnection = mock(Connection.class);
        DbUtils.rollbackAndCloseQuietly(mockConnection);
        verify(mockConnection).rollback();
        verify(mockConnection).close();
    }

    @Test
    public void rollbackAndCloseQuietlyWithException() throws Exception {
        Connection mockConnection = mock(Connection.class);
        doThrow(SQLException.class).when(mockConnection).rollback();
        DbUtils.rollbackAndCloseQuietly(mockConnection);
        verify(mockConnection).rollback();
        verify(mockConnection).close();
    }

}
