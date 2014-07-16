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

import java.sql.Timestamp;

/**
 * A bean to use in testing toBean() and toBeanList().
 */
public class TestBean {

    public enum Ordinal {

        THREE, SIX;

    };

    private String one = null;

    private String two = null;

    private Ordinal three = null;

    private int intTest = 0;

    private Integer integerTest = Integer.valueOf(0);

    private Timestamp timestamp = null;

    private String doNotSet = "not set";

    /**
     * toBean() should set primitive fields to their defaults (ie. 0) when
     * null is returned from the ResultSet.
     */
    private int nullPrimitiveTest = 7;

    /**
     * toBean() should set Object fields to null when null is returned from the
     * ResultSet
     */
    private Object nullObjectTest = "overwrite";

    /**
     * A Date will be returned from the ResultSet but the property is a String.
     * BeanProcessor should create a String from the Date and set this property.
     */
    private String notDate = "not a date";

    /**
     * The ResultSet will have a BigDecimal in this column and the
     * BasicColumnProcessor should convert that to a double and store the value
     * in this property.
     */
    private double columnProcessorDoubleTest = -1;

    /**
     * Constructor for TestBean.
     */
    public TestBean() {
        super();
    }

    public String getOne() {
        return one;
    }

    public Ordinal getThree() {
        return three;
    }

    public String getTwo() {
        return two;
    }

    public void setOne(String string) {
        one = string;
    }

    public void setThree(Ordinal ordinal) {
        three = ordinal;
    }

    public void setTwo(String string) {
        two = string;
    }

    public String getDoNotSet() {
        return doNotSet;
    }

    public void setDoNotSet(String string) {
        doNotSet = string;
    }

    public Integer getIntegerTest() {
        return integerTest;
    }

    public int getIntTest() {
        return intTest;
    }

    public void setIntegerTest(Integer integer) {
        integerTest = integer;
    }

    public void setIntTest(int i) {
        intTest = i;
    }

    public Object getNullObjectTest() {
        return nullObjectTest;
    }

    public int getNullPrimitiveTest() {
        return nullPrimitiveTest;
    }

    public void setNullObjectTest(Object object) {
        nullObjectTest = object;
    }

    public void setNullPrimitiveTest(int i) {
        nullPrimitiveTest = i;
    }

    public String getNotDate() {
        return notDate;
    }

    public void setNotDate(String string) {
        notDate = string;
    }

    public double getColumnProcessorDoubleTest() {
        return columnProcessorDoubleTest;
    }

    public void setColumnProcessorDoubleTest(double d) {
        columnProcessorDoubleTest = d;
    }

}
