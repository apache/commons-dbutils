/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//dbutils/src/java/org/apache/commons/dbutils/Attic/BasicColumnProcessor.java,v 1.3 2003/12/13 20:51:09 dgraham Exp $
 * $Revision: 1.3 $
 * $Date: 2003/12/13 20:51:09 $
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

import java.beans.PropertyDescriptor;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * <code>ColumnProcessor</code> implementation that delegates to the JDBC
 * driver to perform type conversions.  This calls the type specific methods on 
 * the <code>ResultSet</code> such as getDouble(), getInt(), etc. depending on 
 * the supplied JavaBean property type.
 * 
 * <p>
 * This class is thread safe.
 * </p>
 * 
 * @author Corby Page
 * @author David Graham
 * 
 * @see ColumnProcessor
 * @see BasicRowProcessor
 * 
 * @since DbUtils 1.1
 */
public class BasicColumnProcessor implements ColumnProcessor {

    /**
     * BasicColumnProcessor constructor.
     */
    public BasicColumnProcessor() {
        super();
    }
    
    /**
     * Column names are matched to bean property names with a case insensitive
     * comparison. 
     * 
     * @see org.apache.commons.dbutils.ColumnProcessor#mapColumnsToProperties(java.sql.ResultSetMetaData, java.beans.PropertyDescriptor[])
     */
    public int[] mapColumnsToProperties(
        ResultSetMetaData rsmd,
        PropertyDescriptor[] props)
        throws SQLException {

        int cols = rsmd.getColumnCount();
        int columnToProperty[] = new int[cols + 1];

        for (int col = 1; col <= cols; col++) {
            String columnName = rsmd.getColumnName(col);
            for (int i = 0; i < props.length; i++) {

                if (columnName.equalsIgnoreCase(props[i].getName())) {
                    columnToProperty[col] = i;
                    break;

                } else {
                    columnToProperty[col] = PROPERTY_NOT_FOUND;
                }
            }
        }

        return columnToProperty;
    }

    /**
     * Call the appropriate <code>ResultSet</code> getter method for the given
     * property type to perform the type conversion.  If the property type
     * doesn't match one of the supported <code>ResultSet</code> types, 
     * <code>getObject</code> is called.
     * 
     * @see org.apache.commons.dbutils.ColumnProcessor#process(java.sql.ResultSet, int, java.lang.Class)
     */
    public Object process(ResultSet rs, int index, Class propType)
        throws SQLException {

        if (propType.equals(Integer.TYPE) || propType.equals(Integer.class)) {
            return new Integer(rs.getInt(index));

        } else if (
            propType.equals(Boolean.TYPE) || propType.equals(Boolean.class)) {
            return new Boolean(rs.getBoolean(index));

        } else if (propType.equals(Long.TYPE) || propType.equals(Long.class)) {
            return new Long(rs.getLong(index));

        } else if (
            propType.equals(Double.TYPE) || propType.equals(Double.class)) {
            return new Double(rs.getDouble(index));

        } else if (
            propType.equals(Float.TYPE) || propType.equals(Float.class)) {
            return new Float(rs.getFloat(index));

        } else if (
            propType.equals(Short.TYPE) || propType.equals(Short.class)) {
            return new Short(rs.getShort(index));

        } else if (propType.equals(Byte.TYPE) || propType.equals(Byte.class)) {
            return new Byte(rs.getByte(index));

        } else {
            return rs.getObject(index);
        }

    }

}
