/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//dbutils/src/test/org/apache/commons/dbutils/wrappers/StringTrimmedResultSetTest.java,v 1.2 2004/01/11 22:30:39 dgraham Exp $
 * $Revision: 1.2 $
 * $Date: 2004/01/11 22:30:39 $
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

package org.apache.commons.dbutils.wrappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbutils.BaseTestCase;
import org.apache.commons.dbutils.MockResultSet;
import org.apache.commons.dbutils.ProxyFactory;

/**
 * StringTrimmedResultSetTest
 */
public class StringTrimmedResultSetTest extends BaseTestCase {

    public StringTrimmedResultSetTest(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();
        this.rs = StringTrimmedResultSet.wrap(this.rs);
    }

    public void testGetString() throws SQLException {
        this.rs.next();
        assertEquals("notInBean", rs.getString(4));
    }

    public void testGetObject() throws SQLException {
        this.rs.next();
        assertEquals("notInBean", rs.getObject(4));
    }

    /**
     * Make sure 2 wrappers work together.
     * @throws SQLException
     */
    public void testMultipleWrappers() throws Exception {
        // Create a ResultSet with data
        Object[][] rows = new Object[][] { { null }
        };
        ResultSet rs = MockResultSet.create(metaData, rows);

        // Wrap the ResultSet with a null checked version
        SqlNullCheckedResultSet ncrs = new SqlNullCheckedResultSet(rs);
        ncrs.setNullString("   trim this   ");
        rs = ProxyFactory.instance().createResultSet(ncrs);

        // Wrap the wrapper with a string trimmed version
        rs = StringTrimmedResultSet.wrap(rs);

        rs.next();
        assertEquals("trim this", rs.getString(1));
    }

}
