/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//dbutils/src/test/org/apache/commons/dbutils/TestBean.java,v 1.4 2003/11/28 21:11:33 dgraham Exp $
 * $Revision: 1.4 $
 * $Date: 2003/11/28 21:11:33 $
 * 
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package org.apache.commons.dbutils;

/**
 * A bean to use in testing toBean() and toBeanList().
 * 
 * @author David Graham
 */
public class TestBean {

    private String one = null;

    private String two = null;

    private String three = null;

    private int intTest = 0;

    private Integer integerTest = new Integer(0);

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
     * The property should not be set when the object returned from the 
     * ResultSet does not match the type of the bean property.  In this case, 
     * a Date will be returned but the property is a String.
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

    public String getThree() {
        return three;
    }

    public String getTwo() {
        return two;
    }

    public void setOne(String string) {
        one = string;
    }

    public void setThree(String string) {
        three = string;
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
