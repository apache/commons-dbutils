/*
 * Copyright 2003-2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
