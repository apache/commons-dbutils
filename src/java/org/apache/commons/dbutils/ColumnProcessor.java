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
 * <code>ColumnProcessor</code> implementations match 
 * column names to bean property names and convert 
 * <code>ResultSet</code> columns into objects for those bean properties.  
 * The processor is invoked when creating a JavaBean from a 
 * <code>ResultSet</code>.   
 * 
 * @see BasicColumnProcessor
 * @see BasicRowProcessor
 * 
 * @since DbUtils 1.1
 */
public interface ColumnProcessor {

    /**
     * Special array value used by <code>mapColumnsToProperties</code> that 
     * indicates there is no bean property that matches a column from a 
     * <code>ResultSet</code>.
     */
    public static final int PROPERTY_NOT_FOUND = -1;

    /**
     * The positions in the returned array represent column numbers.  The 
     * values stored at each position represent the index in the 
     * <code>PropertyDescriptor[]</code> for the bean property that matches 
     * the column name.  If no bean property was found for a column, the 
     * position is set to <code>PROPERTY_NOT_FOUND</code>.
     * 
     * @param rsmd The <code>ResultSetMetaData</code> containing column 
     * information.
     * 
     * @param props The bean property descriptors.
     * 
     * @return An int[] with column index to property index mappings.  The 0th 
     * element is meaningless because JDBC column indexing starts at 1.
     * 
     * @throws SQLException
     */
    public int[] mapColumnsToProperties(
        ResultSetMetaData rsmd,
        PropertyDescriptor[] props)
        throws SQLException;

    /**
     * Convert a <code>ResultSet</code> column into an object.  Simple 
     * implementations could just call <code>rs.getObject(index)</code> while
     * more complex implementations could perform type manipulation to match 
     * the column's type to the bean property type.
     * 
     * @param rs The <code>ResultSet</code> currently being processed.  It is
     * positioned on a valid row before being passed into this method.
     * 
     * @param index The current column index being processed.
     * 
     * @param propertyType The bean property type that this column needs to be
     * converted into.
     * 
     * @return The object from the <code>ResultSet</code> at the given column
     * index after optional type processing or <code>null</code> if the column
     * value was SQL NULL.
     * 
     * @throws SQLException
     */
    public Object process(ResultSet rs, int index, Class propertyType)
        throws SQLException;

}
