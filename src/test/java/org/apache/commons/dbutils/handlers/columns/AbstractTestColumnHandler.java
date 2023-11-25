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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.ResultSet;

import org.apache.commons.dbutils.ColumnHandler;
import org.junit.Test;
import org.mockito.Mock;

public abstract class AbstractTestColumnHandler<T> {

    @Mock
    private ResultSet rs;
    private final ColumnHandler<T> handler;
    private final Class<?> matchingType;

    public AbstractTestColumnHandler(final ColumnHandler<T> handler, final Class<?> matchingType) {
        this.handler = handler;
        this.matchingType = matchingType;
    }

    public ColumnHandler<T> getColumnHandler() {
        return this.handler;
    }

    public Class<?> getMatchingType() {
        return this.matchingType;
    }

    public ResultSet getResultSet() {
        return this.rs;
    }

    @Test
    public abstract void testApplyType() throws Exception;

    @Test
    public void testMatch() {
        assertTrue(handler.match(matchingType));
    }

    @Test
    public void testMatchNegative() {
        assertFalse(handler.match(Integer.class));
    }
}
