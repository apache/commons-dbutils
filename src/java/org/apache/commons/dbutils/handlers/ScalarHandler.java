/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//dbutils/src/java/org/apache/commons/dbutils/handlers/ScalarHandler.java,v 1.1 2003/11/02 19:15:24 dgraham Exp $
 * $Revision: 1.1 $
 * $Date: 2003/11/02 19:15:24 $
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

package org.apache.commons.dbutils.handlers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbutils.ResultSetHandler;

/**
 * <code>ResultSetHandler</code> implementation that converts one
 * <code>ResultSet</code> column into an Object. This class is thread safe.
 * 
 * @see ResultSetHandler
 * @author Juozas Baliuka
 * @author David Graham
 */
public class ScalarHandler implements ResultSetHandler {

    /**
     * The column number to retrieve.
     */
    private int columnIndex = 1;

    /**
     * The column name to retrieve.  Either columnName or columnIndex
     * will be used but never both.
     */
    private String columnName = null;

    /** 
     * Creates a new instance of ScalarHandler.  The first column will
     * be returned from <code>handle()</code>.
     */
    public ScalarHandler() {
        super();
    }

    /** 
     * Creates a new instance of ScalarHandler.
     * 
     * @param columnIndex The index of the column to retrieve from the 
     * <code>ResultSet</code>.
     */
    public ScalarHandler(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    /** 
     * Creates a new instance of ScalarHandler.
     * 
     * @param columnName The name of the column to retrieve from the 
     * <code>ResultSet</code>.
     */
    public ScalarHandler(String columnName) {
        this.columnName = columnName;
    }

    /**
     * Returns one <code>ResultSet</code> column as an object via the
     * <code>ResultSet.getObject()</code> method that performs type 
     * conversions.
     * 
     * @return The column or <code>null</code> if there are no rows in
     * the <code>ResultSet</code>.
     * 
     * @throws SQLException
     * 
     * @see org.apache.commons.dbutils.ResultSetHandler#handle(java.sql.ResultSet)
     */
    public Object handle(ResultSet rs) throws SQLException {

        if (rs.next()) {
            if (this.columnName == null) {
                return rs.getObject(this.columnIndex);
            } else {
                return rs.getObject(this.columnName);
            }

        } else {
            return null;
        }
    }
}
