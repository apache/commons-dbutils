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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class StatementConfigurationTest {
    /**
     * Test that an empty builder yields null values for all configuration settings.
     */
    @Test
    public void testEmptyBuilder() {
        StatementConfiguration config = new StatementConfiguration.Builder().build();

        assertFalse(config.isFetchDirectionSet());
        assertFalse(config.isFetchSizeSet());
        assertFalse(config.isMaxFieldSizeSet());
        assertFalse(config.isMaxRowsSet());
        assertFalse(config.isQueryTimeoutSet());
    }

    /**
     * Test that a builder with all values set yields like values in the constructed configuration.
     */
    @Test
    public void testBuilder() {
        StatementConfiguration.Builder builder = new StatementConfiguration.Builder()
                .fetchDirection(1)
                .fetchSize(2)
                .maxFieldSize(3)
                .maxRows(4)
                .queryTimeout(5);
        StatementConfiguration config = builder.build();

        assertTrue(config.isFetchDirectionSet());
        assertEquals(Integer.valueOf(1), config.getFetchDirection());

        assertTrue(config.isFetchSizeSet());
        assertEquals(Integer.valueOf(2), config.getFetchSize());

        assertTrue(config.isMaxFieldSizeSet());
        assertEquals(Integer.valueOf(3), config.getMaxFieldSize());

        assertTrue(config.isMaxRowsSet());
        assertEquals(Integer.valueOf(4), config.getMaxRows());

        assertTrue(config.isQueryTimeoutSet());
        assertEquals(Integer.valueOf(5), config.getQueryTimeout());
    }

    /**
     * Test that the constructor of <code>StatementConfiguration</code> correctly sets all values.
     */
    @Test
    public void testConstructor() {
        StatementConfiguration config = new StatementConfiguration(1, 2, 3, 4, 5);

        assertEquals(Integer.valueOf(1), config.getFetchDirection());
        assertEquals(Integer.valueOf(2), config.getFetchSize());
        assertEquals(Integer.valueOf(3), config.getMaxFieldSize());
        assertEquals(Integer.valueOf(4), config.getMaxRows());
        assertEquals(Integer.valueOf(5), config.getQueryTimeout());
    }
}
