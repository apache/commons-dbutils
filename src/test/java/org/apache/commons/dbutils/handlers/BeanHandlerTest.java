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
package org.apache.commons.dbutils.handlers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.sql.SQLException;

import org.apache.commons.dbutils.BaseTestCase;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.TestBean;
import org.junit.jupiter.api.Test;

/**
 * BeanHandlerTest
 */
public class BeanHandlerTest extends BaseTestCase {

    public static class SubTestBean extends TestBean implements SubTestBeanInterface {
    }

    public interface SubTestBeanInterface {
        String getDoNotSet();

        String getOne();

        TestBean.Ordinal getThree();

        String getTwo();
    }

    @Test
    public void testEmptyResultSetHandle() throws SQLException {
        final ResultSetHandler<TestBean> h = new BeanHandler<>(TestBean.class);
        final TestBean results = h.handle(getEmptyResultSet());

        assertNull(results);
    }

    @Test
    public void testHandle() throws SQLException {
        final ResultSetHandler<TestBean> h = new BeanHandler<>(TestBean.class);
        final TestBean results = h.handle(getResultSet());

        assertNotNull(results);
        assertEquals("1", results.getOne());
        assertEquals("2", results.getTwo());
        assertEquals(TestBean.Ordinal.THREE, results.getThree());
        assertEquals("not set", results.getDoNotSet());
    }

    @Test
    public void testHandleToInterface() throws SQLException {
        final ResultSetHandler<SubTestBeanInterface> h = new BeanHandler<>(SubTestBean.class);
        final SubTestBeanInterface results = h.handle(getResultSet());

        assertNotNull(results);
        assertEquals("1", results.getOne());
        assertEquals("2", results.getTwo());
        assertEquals(TestBean.Ordinal.THREE, results.getThree());
        assertEquals("not set", results.getDoNotSet());
    }

    @Test
    public void testHandleToSuperClass() throws SQLException {
        final ResultSetHandler<TestBean> h = new BeanHandler<>(SubTestBean.class);
        final TestBean results = h.handle(getResultSet());

        assertNotNull(results);
        assertEquals("1", results.getOne());
        assertEquals("2", results.getTwo());
        assertEquals(TestBean.Ordinal.THREE, results.getThree());
        assertEquals("not set", results.getDoNotSet());
    }
}
