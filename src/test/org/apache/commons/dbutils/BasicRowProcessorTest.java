/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//dbutils/src/test/org/apache/commons/dbutils/BasicRowProcessorTest.java,v 1.5 2004/01/11 22:30:38 dgraham Exp $
 * $Revision: 1.5 $
 * $Date: 2004/01/11 22:30:38 $
 * 
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2004 The Apache Software Foundation.  All rights
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

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Test the BasicRowProcessor class.
 */
public class BasicRowProcessorTest extends BaseTestCase {

    private static final RowProcessor processor = new BasicRowProcessor();

    /**
     * Constructor for BasicRowProcessorTest.
     * @param name
     */
    public BasicRowProcessorTest(String name) {
        super(name);
    }

    public void testToArray() throws SQLException {

        int rowCount = 0;
        Object[] a = null;
        while (this.rs.next()) {
            a = processor.toArray(this.rs);
            assertEquals(COLS, a.length);
            rowCount++;
        }

        assertEquals(ROWS, rowCount);
        assertEquals("4", a[0]);
        assertEquals("5", a[1]);
        assertEquals("6", a[2]);
    }

    public void testToBean() throws SQLException {

        int rowCount = 0;
        TestBean b = null;
        while (this.rs.next()) {
            b = (TestBean) processor.toBean(this.rs, TestBean.class);
            assertNotNull(b);
            rowCount++;
        }

        assertEquals(ROWS, rowCount);
        assertEquals("4", b.getOne());
        assertEquals("5", b.getTwo());
        assertEquals("6", b.getThree());
        assertEquals("not set", b.getDoNotSet());
        assertEquals(3, b.getIntTest());
        assertEquals(new Integer(4), b.getIntegerTest());
        assertEquals(null, b.getNullObjectTest());
        assertEquals(0, b.getNullPrimitiveTest());
        assertEquals("not a date", b.getNotDate());
    }

    public void testToBeanList() throws SQLException {

        List list = processor.toBeanList(this.rs, TestBean.class);
        assertNotNull(list);
        assertEquals(ROWS, list.size());

        TestBean b = (TestBean) list.get(1);

        assertEquals("4", b.getOne());
        assertEquals("5", b.getTwo());
        assertEquals("6", b.getThree());
        assertEquals("not set", b.getDoNotSet());
        assertEquals(3, b.getIntTest());
        assertEquals(new Integer(4), b.getIntegerTest());
        assertEquals(null, b.getNullObjectTest());
        assertEquals(0, b.getNullPrimitiveTest());
        assertEquals("not a date", b.getNotDate());
    }

    public void testToMap() throws SQLException {

        int rowCount = 0;
        Map m = null;
        while (this.rs.next()) {
            m = processor.toMap(this.rs);
            assertNotNull(m);
            assertEquals(COLS, m.keySet().size());
            rowCount++;
        }

        assertEquals(ROWS, rowCount);
        assertEquals("4", m.get("One")); // case shouldn't matter
        assertEquals("5", m.get("two"));
        assertEquals("6", m.get("THREE"));
    }

}
