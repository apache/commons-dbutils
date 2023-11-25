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
package org.apache.commons.dbutils.handlers.columns;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class BooleanColumnHandlerTest extends AbstractTestColumnHandler<Boolean> {

    public BooleanColumnHandlerTest() {
        super(new BooleanColumnHandler(), Boolean.class);
    }

    @Override
    @Test
    public void testApplyType() throws Exception {
        ResultSet rs = getResultSet();
        when(rs.getBoolean(1)).thenReturn(Boolean.TRUE);
        assertEquals(Boolean.class, getColumnHandler().apply(rs, 1).getClass());
    }
}
