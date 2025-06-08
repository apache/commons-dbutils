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
package org.apache.commons.dbutils.handlers.columns;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class IntegerColumnHandlerTest extends AbstractTestColumnHandler<Integer> {

    public IntegerColumnHandlerTest() {
        super(new IntegerColumnHandler(), Integer.class);
    }

    @Override
    @Test
    public void testApplyType() throws Exception {
        when(getResultSet().getInt(1)).thenReturn(Integer.MIN_VALUE);
        assertEquals(Integer.class, getColumnHandler().apply(getResultSet(), 1).getClass());
    }

    @Override
    @Test
    public void testMatchNegative() {
        assertFalse(getColumnHandler().match(Float.class));
    }
}
