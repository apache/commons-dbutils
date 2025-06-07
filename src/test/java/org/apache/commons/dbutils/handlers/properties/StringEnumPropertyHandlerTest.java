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
package org.apache.commons.dbutils.handlers.properties;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class StringEnumPropertyHandlerTest {
    private StringEnumPropertyHandler handler;

    @BeforeEach
    public void setUp() {
        this.handler = new StringEnumPropertyHandler();
    }

    @Test
    void testMatch() {
        assertTrue(handler.match(TestEnum.class, "test"));
    }

    @Test
    void testMatchNegative() {
        assertFalse(handler.match(TestEnum.class, Double.valueOf(1)));

        assertFalse(handler.match(Integer.class, ""));
    }
}
