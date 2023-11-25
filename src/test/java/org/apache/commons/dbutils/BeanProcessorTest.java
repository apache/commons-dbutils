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
import static org.junit.Assert.assertNotEquals;

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

    private static final BeanProcessor BEAN_PROCESSOR = new BeanProcessor();

    public void testCheckAnnotationOnMissingReadMethod() throws Exception {
        final String[] colNames = { "testField" };
        final ResultSetMetaData metaData = MockResultSetMetaData.create(colNames);

        final String testField = "first";
        final Object[][] rows = { new Object[] { testField } };

        final ResultSet resultSet = MockResultSet.create(metaData, rows);
        assertTrue(resultSet.next());
        TestNoGetter testCls = new TestNoGetter();
        testCls = BEAN_PROCESSOR.populateBean(resultSet, testCls);
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
        final String[] columnNames = {"name", "things", "stuff"};
        final ResultSetMetaData metaData = MockResultSetMetaData.create(columnNames);

        final String nameHeader = "first";
        final List<String> thingsRowValues = Arrays.asList("1", "2", "3", "4");
        final List<String> stuffRowValues = thingsRowValues;
        final Object[][] rows = {
                new Object[] {nameHeader, thingsRowValues, stuffRowValues}
        };

        final ResultSet resultSet = MockResultSet.create(metaData, rows);
        assertTrue(resultSet.next());
        IndexedPropertyTestClass indexedPropertyTestClass = new IndexedPropertyTestClass();
        indexedPropertyTestClass = BEAN_PROCESSOR.populateBean(resultSet, indexedPropertyTestClass);
        assertEquals(nameHeader, indexedPropertyTestClass.getName());
        assertArrayEquals(thingsRowValues.toArray(), indexedPropertyTestClass.getThings().toArray());
        assertArrayEquals(stuffRowValues.toArray(), indexedPropertyTestClass.getStuff().toArray());
    }

    public void testMapColumnToAnnotationField() throws Exception {
        final String[] columnNames = { "test", "test", "three_" };
        final String[] columnLabels = { "one", "two", null };
        final ResultSetMetaData resultSetMetaData = buildResultSetMetaData(columnNames, columnLabels);
        final PropertyDescriptor[] props = Introspector.getBeanInfo(MapColumnToAnnotationFieldBean.class).getPropertyDescriptors();

        final int[] columns = BEAN_PROCESSOR.mapColumnsToProperties(resultSetMetaData, props);
        for (int i = 1; i < columns.length; i++) {
            assertNotEquals(columns[i], BeanProcessor.PROPERTY_NOT_FOUND);
        }
    }

    public void testMapColumnToProperties() throws Exception {
        final String[] columnNames = { "test", "test", "three" };
        final String[] columnLabels = { "one", "two", null };
        final ResultSetMetaData resultSetMetaData = buildResultSetMetaData(columnNames, columnLabels);
        final PropertyDescriptor[] props = Introspector.getBeanInfo(MapColumnToPropertiesBean.class).getPropertyDescriptors();

        final int[] columns = BEAN_PROCESSOR.mapColumnsToProperties(resultSetMetaData, props);
        for (int i = 1; i < columns.length; i++) {
            assertNotEquals(columns[i], BeanProcessor.PROPERTY_NOT_FOUND);
        }
    }

    public void testMapColumnToPropertiesWithOverrides() throws Exception {
        final Map<String, String> columnToPropertyOverrides = new HashMap<>();
        columnToPropertyOverrides.put("five", "four");
        final BeanProcessor beanProcessor = new BeanProcessor(columnToPropertyOverrides);
        final String[] columnNames = { "test", "test", "three", "five" };
        final String[] columnLabels = { "one", "two", null, null };
        final ResultSetMetaData resultSetMetaData = buildResultSetMetaData(columnNames, columnLabels);
        final PropertyDescriptor[] props = Introspector.getBeanInfo(MapColumnToPropertiesBean.class).getPropertyDescriptors();

        final int[] columns = beanProcessor.mapColumnsToProperties(resultSetMetaData, props);
        for (int i = 1; i < columns.length; i++) {
            assertNotEquals(BeanProcessor.PROPERTY_NOT_FOUND, columns[i]);
        }
    }

    public void testProcessWithPopulateBean() throws SQLException {
        TestBean testBean = new TestBean();

        assertTrue(this.getResultSet().next());
        testBean = BEAN_PROCESSOR.populateBean(this.getResultSet(), testBean);
        assertEquals(13.0, testBean.getColumnProcessorDoubleTest(), 0);
        assertEquals(testBean.getThree(), TestBean.Ordinal.THREE);

        assertTrue(this.getResultSet().next());
        testBean = BEAN_PROCESSOR.populateBean(this.getResultSet(), testBean);
        assertEquals(13.0, testBean.getColumnProcessorDoubleTest(), 0);
        assertEquals(testBean.getThree(), TestBean.Ordinal.SIX);

        assertFalse(getResultSet().next());
    }

    public void testProcessWithToBean() throws SQLException {
        assertTrue(this.getResultSet().next());
        TestBean testBean = BEAN_PROCESSOR.toBean(this.getResultSet(), TestBean.class);
        assertEquals(13.0, testBean.getColumnProcessorDoubleTest(), 0);
        assertEquals(testBean.getThree(), TestBean.Ordinal.THREE);

        assertTrue(this.getResultSet().next());
        testBean = BEAN_PROCESSOR.toBean(this.getResultSet(), TestBean.class);
        assertEquals(13.0, testBean.getColumnProcessorDoubleTest(), 0);
        assertEquals(testBean.getThree(), TestBean.Ordinal.SIX);

        assertFalse(getResultSet().next());
    }

    public void testWrongSetterParamCount() throws Exception {
        final String[] colNames = { "testField" };
        final ResultSetMetaData metaData = MockResultSetMetaData.create(colNames);

        final Integer testFieldRowValue = 1;
        final Object[][] rows = {
                new Object[] {testFieldRowValue}
        };

        final ResultSet resultSet = MockResultSet.create(metaData, rows);
        assertTrue(resultSet.next());
        TestWrongSetter testWrongSetter = new TestWrongSetter();
        testWrongSetter = BEAN_PROCESSOR.populateBean(resultSet, testWrongSetter);
        assertNull(testWrongSetter.testField);
    }

    private ResultSetMetaData buildResultSetMetaData(String[] columnNames, String[] columnLabels) {
        ProxyFactory proxyFactory = ProxyFactory.instance();
        MockResultSetMetaData mockResultSetMetaData = new MockResultSetMetaData(columnNames, columnLabels);
        return proxyFactory.createResultSetMetaData(mockResultSetMetaData);
    }
}
