/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//dbutils/src/test/org/apache/commons/dbutils/handlers/BeanListHandlerTest.java,v 1.2 2004/01/11 22:30:38 dgraham Exp $
 * $Revision: 1.2 $
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

package org.apache.commons.dbutils.handlers;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.dbutils.BaseTestCase;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.TestBean;

/**
 * BeanListHandlerTest
 */
public class BeanListHandlerTest extends BaseTestCase {

	/**
	 * Constructor for BeanListHandlerTest.
	 */
	public BeanListHandlerTest(String name) {
		super(name);
	}

	public void testHandle() throws SQLException {
		ResultSetHandler h = new BeanListHandler(TestBean.class);
		List results = (List) h.handle(this.rs);

		assertNotNull(results);
		assertEquals(ROWS, results.size());

		Iterator iter = results.iterator();
		TestBean row = null;
		while (iter.hasNext()) {
			row = (TestBean) iter.next();
			assertNotNull(row);
		}

		assertEquals("4", row.getOne());
		assertEquals("5", row.getTwo());
		assertEquals("6", row.getThree());
		assertEquals("not set", row.getDoNotSet());
	}

	public void testEmptyResultSetHandle() throws SQLException {
		ResultSetHandler h = new BeanListHandler(TestBean.class);
		List results = (List) h.handle(this.emptyResultSet);

		assertNotNull(results);
		assertTrue(results.isEmpty());
	}

}
