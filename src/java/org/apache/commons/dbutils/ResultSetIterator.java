/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//dbutils/src/java/org/apache/commons/dbutils/ResultSetIterator.java,v 1.3 2004/01/11 22:30:38 dgraham Exp $
 * $Revision: 1.3 $
 * $Date: 2004/01/11 22:30:38 $
 * 
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002-2004 The Apache Software Foundation.  All rights
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
import java.util.Iterator;

/**
 * <p>
 * Wraps a <code>ResultSet</code> in an <code>Iterator</code>.  This is useful
 * when you want to present a non-database application layer with domain
 * neutral data.
 * </p>
 * 
 * <p>
 * This implementation requires the <code>ResultSet.isLast()</code> method
 * to be implemented.
 * </p>
 */
public class ResultSetIterator implements Iterator {

    /**
     * The wrapped <code>ResultSet</code>.
     */
    private ResultSet rs = null;
    
    /**
     * The processor to use when converting a row into an Object[].
     */
    private RowProcessor convert = new BasicRowProcessor();

    /**
     * Constructor for ResultSetIterator.
     * @param rs Wrap this <code>ResultSet</code> in an <code>Iterator</code>.
     */
    public ResultSetIterator(ResultSet rs) {
        this.rs = rs;
    }
    
    /**
     * Constructor for ResultSetIterator.
     * @param rs Wrap this <code>ResultSet</code> in an <code>Iterator</code>.
     * @param convert The processor to use when converting a row into an 
     * <code>Object[]</code>.  Defaults to a 
     * <code>BasicRowProcessor</code>.
     */
    public ResultSetIterator(ResultSet rs, RowProcessor convert) {
        this.rs = rs;
        this.convert = convert;
    }

    public boolean hasNext() {
        try {
            return !rs.isLast();

        } catch (SQLException e) {
            // TODO Logging?
            //e.printStackTrace();
            return false;
        }
    }

    /**
     * Returns the next row as an <code>Object[]</code>.
     * @return An <code>Object[]</code> with the same number of elements as
     * columns in the <code>ResultSet</code>. 
     * @see java.util.Iterator#next()
     */
    public Object next() {
        try {
            rs.next();
            return this.convert.toArray(rs);

        } catch (SQLException e) {
            // TODO Logging?
            //e.printStackTrace();
            return null;
        }
    }

    /**
     * Deletes the current row from the <code>ResultSet</code>.
     * @see java.util.Iterator#remove()
     */
    public void remove() {
        try {
            this.rs.deleteRow();

        } catch (SQLException e) {
            // TODO Logging?
            //e.printStackTrace();
        }
    }

}
