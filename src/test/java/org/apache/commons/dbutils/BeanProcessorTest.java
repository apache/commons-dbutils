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

import static org.junit.Assert.assertArrayEquals;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeanProcessorTest extends BaseTestCase {

    private static final class IndexedPropertyTestClass {
        private String name;
        // Indexed variable with indexed getter and setter
        private List<String> things;
        // Indexed variable without indexed getter or setter
        private List<String> stuff;

        public String getName() {
            return name;
        }

        public List<String> getStuff() {
            return stuff;
        }

        public String getThing(final int idx) {
            return things.get(idx);
        }

        public List<String> getThings() {
            return things;
        }

        public void setName(final String name) {
            this.name = name;
        }

        public void setStuff(final List<String> stuff) {
            this.stuff = stuff;
        }

        public void setThing(final int idx, final String thing) {
            this.things.set(idx, thing);
        }

        public void setThings(final List<String> things) {
            this.things = things;
        }
    }

    public static class MapColumnToAnnotationFieldBean {
        private String one;

        private String two;

        private String three;

        private String four;

        public String getFour() {
            return four;
        }

        public String getOne() {
            return one;
        }

        @Column(name = "three_")
        public String getThree() {
            return three;
        }

        public String getTwo() {
            return two;
        }

        public void setFour(final String four) {
            this.four = four;
        }

        public void setOne(final String one) {
            this.one = one;
        }

        public void setThree(final String three) {
            this.three = three;
        }

        public void setTwo(final String two) {
            this.two = two;
        }
    }

    public static class MapColumnToPropertiesBean {
        private String one;

        private String two;

        private String three;

        private String four;

        public String getFour() {
            return four;
        }

        public String getOne() {
            return one;
        }

        public String getThree() {
            return three;
        }

        public String getTwo() {
            return two;
        }

        public void setFour(final String four) {
            this.four = four;
        }

        public void setOne(final String one) {
            this.one = one;
        }

        public void setThree(final String three) {
            this.three = three;
        }

        public void setTwo(final String two) {
            this.two = two;
        }
    }

    private static final class TestNoGetter {
        private String testField;

        /**
         * Add setter to trigger JavaBeans to populate a PropertyDescriptor
         *
         * @param testField The new testField value
         */
        public void setTestField(final String testField) {
            this.testField = testField;
        }
    }

    private static final class TestWrongSetter {
        private Integer testField;

        public Integer getTestField() {
            return testField;
        }

        /**
         * dbutils checks for a setter with exactly 1 param. This tests resilience to a found setter that doesn't match expectations.
         *
         * @param idx
         * @param testField
         */
        public void setTestField(final int idx, final Integer testField) {
            this.testField = testField;
        }
    }

    private static final BeanProcessor beanProc = new BeanProcessor();

    public void testCheckAnnotationOnMissingReadMethod() throws Exception {
        final String[] colNames = { "testField" };
        final ResultSetMetaData metaData = MockResultSetMetaData.create(colNames);

        final String testField = "first";
        final Object[][] rows = { new Object[] { testField } };

        final ResultSet rs = MockResultSet.create(metaData, rows);
        assertTrue(rs.next());
        TestNoGetter testCls = new TestNoGetter();
        testCls = beanProc.populateBean(rs, testCls);
        assertEquals(testCls.testField, "first");
    }

    /**
     * Based on the report in DBUTILS-150. This test validates that indexed property descriptors are not used, and indexed getter/setter methods are not
     * inspected.
     *
     * @throws Exception
     * @see <a href="https://issues.apache.org/jira/browse/DBUTILS-150">DBUTILS-150</a>
     */
    public void testIndexedPropertyDescriptor() throws Exception {
        final String[] colNames = { "name", "things", "stuff" };
        final ResultSetMetaData metaData = MockResultSetMetaData.create(colNames);

        final String name = "first";
        final List<String> things = Arrays.asList("1", "2", "3", "4");
        final List<String> stuff = things;
        final Object[][] rows = { new Object[] { name, things, stuff } };

        final ResultSet rs = MockResultSet.create(metaData, rows);
        assertTrue(rs.next());
        IndexedPropertyTestClass testCls = new IndexedPropertyTestClass();
        testCls = beanProc.populateBean(rs, testCls);
        assertEquals(name, testCls.getName());
        assertArrayEquals(things.toArray(), testCls.getThings().toArray());
        assertArrayEquals(stuff.toArray(), testCls.getStuff().toArray());
    }

    public void testMapColumnToAnnotationField() throws Exception {
        final String[] columnNames = { "test", "test", "three_" };
        final String[] columnLabels = { "one", "two", null };
        final ResultSetMetaData rsmd = ProxyFactory.instance().createResultSetMetaData(new MockResultSetMetaData(columnNames, columnLabels));
        final PropertyDescriptor[] props = Introspector.getBeanInfo(MapColumnToAnnotationFieldBean.class).getPropertyDescriptors();

        final int[] columns = beanProc.mapColumnsToProperties(rsmd, props);
        for (int i = 1; i < columns.length; i++) {
            assertTrue(columns[i] != BeanProcessor.PROPERTY_NOT_FOUND);
        }
    }

    public void testMapColumnToProperties() throws Exception {
        final String[] columnNames = { "test", "test", "three" };
        final String[] columnLabels = { "one", "two", null };
        final ResultSetMetaData rsmd = ProxyFactory.instance().createResultSetMetaData(new MockResultSetMetaData(columnNames, columnLabels));
        final PropertyDescriptor[] props = Introspector.getBeanInfo(MapColumnToPropertiesBean.class).getPropertyDescriptors();

        final int[] columns = beanProc.mapColumnsToProperties(rsmd, props);
        for (int i = 1; i < columns.length; i++) {
            assertTrue(columns[i] != BeanProcessor.PROPERTY_NOT_FOUND);
        }
    }

    public void testMapColumnToPropertiesWithOverrides() throws Exception {
        final Map<String, String> columnToPropertyOverrides = new HashMap<>();
        columnToPropertyOverrides.put("five", "four");
        final BeanProcessor beanProc = new BeanProcessor(columnToPropertyOverrides);
        final String[] columnNames = { "test", "test", "three", "five" };
        final String[] columnLabels = { "one", "two", null, null };
        final ResultSetMetaData rsmd = ProxyFactory.instance().createResultSetMetaData(new MockResultSetMetaData(columnNames, columnLabels));
        final PropertyDescriptor[] props = Introspector.getBeanInfo(MapColumnToPropertiesBean.class).getPropertyDescriptors();

        final int[] columns = beanProc.mapColumnsToProperties(rsmd, props);
        for (int i = 1; i < columns.length; i++) {
            assertTrue(columns[i] != BeanProcessor.PROPERTY_NOT_FOUND);
        }
    }

    public void testProcessWithPopulateBean() throws SQLException {
        TestBean b = new TestBean();
        final ResultSet rs = getResultSet();
        assertTrue(rs.next());
        b = beanProc.populateBean(rs, b);
        assertEquals(13.0, b.getColumnProcessorDoubleTest(), 0);
        assertEquals(b.getThree(), TestBean.Ordinal.THREE);

        assertTrue(rs.next());
        b = beanProc.populateBean(rs, b);
        assertEquals(13.0, b.getColumnProcessorDoubleTest(), 0);
        assertEquals(b.getThree(), TestBean.Ordinal.SIX);

        assertFalse(rs.next());
    }

    public void testProcessWithToBean() throws SQLException {
        final ResultSet rs = getResultSet();
        assertTrue(rs.next());
        TestBean b = beanProc.toBean(rs, TestBean.class);
        assertEquals(13.0, b.getColumnProcessorDoubleTest(), 0);
        assertEquals(b.getThree(), TestBean.Ordinal.THREE);

        assertTrue(rs.next());
        b = beanProc.toBean(rs, TestBean.class);
        assertEquals(13.0, b.getColumnProcessorDoubleTest(), 0);
        assertEquals(b.getThree(), TestBean.Ordinal.SIX);

        assertFalse(rs.next());
    }

    public void testWrongSetterParamCount() throws Exception {
        final String[] colNames = { "testField" };
        final ResultSetMetaData metaData = MockResultSetMetaData.create(colNames);

        final Integer testField = 1;
        final Object[][] rows = { new Object[] { testField } };

        final ResultSet rs = MockResultSet.create(metaData, rows);
        assertTrue(rs.next());
        TestWrongSetter testCls = new TestWrongSetter();
        testCls = beanProc.populateBean(rs, testCls);
        assertNull(testCls.testField);
    }
}
