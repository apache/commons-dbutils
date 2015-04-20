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
package org.apache.commons.dbutils.handlers.properties;

import static org.junit.Assert.assertTrue;

import java.util.ServiceLoader;

import org.apache.commons.dbutils.PropertyHandler;
import org.junit.Before;
import org.junit.Test;

public class PropertyHandlerTest {
    private ServiceLoader<PropertyHandler> loader;

    @Before
    public void setUp() {
        loader = ServiceLoader.load(PropertyHandler.class);
    }

    @Test
    public void testServiceLoaderFindsMultipleRegistries() {
        boolean found = false;
        for (PropertyHandler handler : loader) {
            // this class is defined outside of the main classes of dbutils
            if (handler instanceof TestPropertyHandler) {
                found = true;
            }
        }

        assertTrue(found);
    }

    /**
     * Verifying 'more than 1' shows that we found more than we loaded locally which assumes the core handlers
     * were loaded, too.
     */
    @Test
    public void testFoundMoreThanLocal() {
        int count = 0;
        for (PropertyHandler handler : loader) {
            count++;
        }

        assertTrue(count > 1);
    }

}
