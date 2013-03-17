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

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import org.apache.commons.dbutils2.AbstractExecutor;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


public class AbstractExecutorTest {

    @SuppressWarnings("rawtypes") // don't care about this in the unit test
    private AbstractExecutor executor;
    
    @Mock private Connection conn;
    @Mock private PreparedStatement stmt;
    
    @Before
    public void setup() throws SQLException {
        MockitoAnnotations.initMocks(this);
        
        when(conn.prepareStatement(any(String.class))).thenReturn(stmt);
    }
    
    @SuppressWarnings("rawtypes")
    public void createExecutor(String sql) throws SQLException {
        executor = new AbstractExecutor(conn, sql) { };
    }
    
    @Test
    public void testGoodSql() throws SQLException {
        createExecutor("select * from blah :first = first and :last=last and phone=:phone");

        verify(conn, times(1)).prepareStatement("select * from blah ? = first and ?=last and phone=?");

        executor.bind("first", "first_name")
                .bind(":last", "last_name")
                .bind("phone", Integer.valueOf(12345));
       
        verify(stmt, times(1)).setObject(1, "first_name");
        verify(stmt, times(1)).setObject(2, "last_name");
        verify(stmt, times(1)).setObject(eq(3), eq(Integer.valueOf(12345)));
        
        executor.throwIfUnmappedParams();
    }

    @SuppressWarnings("boxing") // test code
    @Test
    public void testNoParamsSql() throws SQLException {
        createExecutor("select * from blah");

        verify(conn, times(1)).prepareStatement("select * from blah");
        verify(stmt, times(0)).setObject(any(Integer.class), any(Object.class));
        
        executor.throwIfUnmappedParams();
    }

    @Test(expected=SQLException.class)
    public void testMissingParamSql() throws SQLException {
        createExecutor("select * from blah :first = first and :last=last");

        verify(conn, times(1)).prepareStatement("select * from blah ? = first and ?=last");

        executor.bind("first", "first_name")
                .bind(":last", "last_name")
                .bind("phone", Integer.valueOf(12345)); // should throw
       
        verify(stmt, times(1)).setObject(1, "first_name");
        verify(stmt, times(1)).setObject(2, "last_name");
        verify(stmt, times(1)).setObject(eq(3), eq(Integer.valueOf(12345)));
    }

    @Test(expected=SQLException.class)
    public void testDoubleBind() throws SQLException {
        createExecutor("select * from blah :first = first and :last=last");

        verify(conn, times(1)).prepareStatement("select * from blah ? = first and ?=last");

        executor.bind("first", "first_name")
                .bind(":last", "last_name")
                .bind(":last", "last_name");
        
        verify(stmt, times(1)).setObject(1, "first_name");
        verify(stmt, times(1)).setObject(2, "last_name");
    }
    
    @Test
    public void testNullBind() throws SQLException {
        createExecutor("select * from blah :first = first and :last=last");

        verify(conn, times(1)).prepareStatement("select * from blah ? = first and ?=last");

        executor.bindNull("first")
                .bindNull(":last", Types.NULL);
        
        verify(stmt, times(1)).setNull(1, Types.VARCHAR);
        verify(stmt, times(1)).setNull(2, Types.NULL);
    }
}
