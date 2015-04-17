/*
 * Copyright 2014 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.CallableStatement;
import java.sql.Types;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class OutParameterTest {
    private static final int INDEX = 2;
    private static final int VALUE = 42;

    @Mock CallableStatement stmt;

    OutParameter<Number> parameter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);    // init the mocks

        parameter = new OutParameter<Number>(Types.INTEGER, Number.class);
    }

    @Test
    public void testSetValue() throws Exception {
        when(stmt.getObject(INDEX)).thenReturn(VALUE);

        parameter.setValue(stmt, INDEX);

        assertEquals(VALUE, parameter.getValue());
    }

    @Test
    public void testRegister() throws Exception {
        parameter.register(stmt, INDEX);
        verify(stmt, times(1)).registerOutParameter(INDEX, Types.INTEGER);
        verify(stmt, times(0)).setObject(eq(INDEX), any(Number.class));

        parameter.setValue(VALUE);
        parameter.register(stmt, INDEX);
        verify(stmt, times(2)).registerOutParameter(INDEX, Types.INTEGER);
        verify(stmt, times(1)).setObject(INDEX, VALUE);
    }

    @Test
    public void testRegisterAlternateConstructor() throws Exception {
        parameter = new OutParameter<Number>(Types.INTEGER, Number.class, VALUE);
        parameter.register(stmt, INDEX);
        verify(stmt, times(1)).registerOutParameter(INDEX, Types.INTEGER);
        verify(stmt, times(1)).setObject(INDEX, VALUE);
    }

}
