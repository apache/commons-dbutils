/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//dbutils/src/java/org/apache/commons/dbutils/RowProcessor.java,v 1.2 2004/01/11 22:30:38 dgraham Exp $
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

package org.apache.commons.dbutils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * <code>RowProcessor</code> implementations convert 
 * <code>ResultSet</code> rows into various other objects.  Implementations
 * can extend <code>BasicRowProcessor</code> to protect themselves
 * from changes to this interface. 
 * 
 * @see BasicRowProcessor
 */
public interface RowProcessor {

    /**
     * Create an <code>Object[]</code> from the column values in one 
     * <code>ResultSet</code> row.  The <code>ResultSet</code> should be 
     * positioned on a valid row before passing it to this method.  
     * Implementations of this method must not alter the row position of 
     * the <code>ResultSet</code>. 
     */
    public Object[] toArray(ResultSet rs) throws SQLException;

    /**
     * Create a JavaBean from the column values in one <code>ResultSet</code> 
     * row.  The <code>ResultSet</code> should be positioned on a valid row before
     * passing it to this method.  Implementations of this method must not
     * alter the row position of the <code>ResultSet</code>.
     */
    public Object toBean(ResultSet rs, Class type) throws SQLException;

    /**
     * Create a <code>List</code> of JavaBeans from the column values in all 
     * <code>ResultSet</code> rows.  <code>ResultSet.next()</code> should 
     * <strong>not</strong> be called before passing it to this method.
     * 
     * @return A <code>List</code> of beans with the given type in the order 
     * they were returned by the <code>ResultSet</code>.
     */
    public List toBeanList(ResultSet rs, Class type) throws SQLException;

    /**
     * Create a <code>Map</code> from the column values in one 
     * <code>ResultSet</code> row.  The <code>ResultSet</code> should be 
     * positioned on a valid row before
     * passing it to this method.  Implementations of this method must not
     * alter the row position of the <code>ResultSet</code>.
     */
    public Map toMap(ResultSet rs) throws SQLException;

}
