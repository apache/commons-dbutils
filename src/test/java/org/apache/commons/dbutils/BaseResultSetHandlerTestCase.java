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

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.junit.Test;

public final class BaseResultSetHandlerTestCase extends BaseTestCase {

    @Test
    public void handleWithoutExplicitResultSetInvocation() throws Exception {
        Collection<Map<String, Object>> result = new ToMapCollectionHandler().handle(createMockResultSet());

        assertFalse(result.isEmpty());

        for (Map<String, Object> current : result) {
            assertTrue(current.containsKey("one"));
            assertTrue(current.containsKey("two"));
            assertTrue(current.containsKey("three"));
            assertTrue(current.containsKey("notInBean"));
            assertTrue(current.containsKey("intTest"));
            assertTrue(current.containsKey("integerTest"));
            assertTrue(current.containsKey("nullObjectTest"));
            assertTrue(current.containsKey("nullPrimitiveTest"));
            assertTrue(current.containsKey("notDate"));
            assertTrue(current.containsKey("columnProcessorDoubleTest"));
        }
    }

    private static final class ToMapCollectionHandler
        extends BaseResultSetHandler<Collection<Map<String, Object>>> {

        @Override
        protected Collection<Map<String, Object>> handle() throws SQLException {
            Collection<Map<String, Object>> result = new LinkedList<Map<String, Object>>();

            while (next()) {
                Map<String, Object> current = new HashMap<String, Object>();

                for (int i = 1; i <= getMetaData().getColumnCount(); i++) {
                    current.put(getMetaData().getColumnName(i), getObject(i));
                }

                result.add(current);
            }

            return result;
        }

    }

}
