/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//dbutils/src/java/org/apache/commons/dbutils/BasicRowProcessor.java,v 1.7 2003/12/07 17:25:01 dgraham Exp $
 * $Revision: 1.7 $
 * $Date: 2003/12/07 17:25:01 $
 * 
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002-2003 The Apache Software Foundation.  All rights
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

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Basic implementation of the <code>RowProcessor</code> interface.
 * 
 * <p>
 * This class is thread-safe.
 * </p>
 * 
 * @see RowProcessor
 * @see ColumnProcessor
 * 
 * @author Henri Yandell
 * @author Juozas Baliuka
 * @author David Graham
 * @author Yoav Shapira
 */
public class BasicRowProcessor implements RowProcessor {

    /**
     * Set a bean's primitive properties to these defaults when SQL NULL 
     * is returned.  These are the same as the defaults that ResultSet get* 
     * methods return in the event of a NULL column.
     */
    private static final Map primitiveDefaults = new HashMap();

    static {
        primitiveDefaults.put(Integer.TYPE, new Integer(0));
        primitiveDefaults.put(Short.TYPE, new Short((short) 0));
        primitiveDefaults.put(Byte.TYPE, new Byte((byte) 0));
        primitiveDefaults.put(Float.TYPE, new Float(0));
        primitiveDefaults.put(Double.TYPE, new Double(0));
        primitiveDefaults.put(Long.TYPE, new Long(0));
        primitiveDefaults.put(Boolean.TYPE, Boolean.FALSE);
        primitiveDefaults.put(Character.TYPE, new Character('\u0000'));
    }

    /**
     * The Singleton instance of this class.
     */
    private static final BasicRowProcessor instance = new BasicRowProcessor();

    /**
     * Returns the Singleton instance of this class.
     *
     * @return The single instance of this class.
     * @deprecated Create instances with the constructors instead.  This will 
     * be removed after DbUtils 1.1.
     */
    public static BasicRowProcessor instance() {
        return instance;
    }
    
    /**
     * A basic ColumnProcessor implementation to use when client doesn't pass
     * one into the constructor.
     */
    private static final ColumnProcessor defaultProcessor =
        new ColumnProcessor() {

        private final ColumnProcessor p = new BasicColumnProcessor();

        public Object process(ResultSet rs, int index, Class propType)
            throws SQLException {
            return rs.getObject(index);
        }

        public int[] mapColumnsToProperties(
            ResultSetMetaData rsmd,
            PropertyDescriptor[] props)
            throws SQLException {

            // TODO Compose the default behavior from BasicColumnProcessor.  
            // Maybe the default should just be a BasicColumnProcessor?                
            return p.mapColumnsToProperties(rsmd, props);
        }
    };

    /**
     * Use this processor to convert columns to bean properties.
     */
    private ColumnProcessor convert = defaultProcessor; 

    /**
     * BasicRowProcessor constructor.
     */
    public BasicRowProcessor() {
        super();
    }
    
    /**
     * BasicRowProcessor constructor.
     * @param convert The ColumnProcessor to use when converting columns to 
     * bean properties.
     * @since DbUtils 1.1
     */
    public BasicRowProcessor(ColumnProcessor convert) {
        super();
        this.convert = convert;
    }

    /**
     * Convert a <code>ResultSet</code> row into an <code>Object[]</code>.
     * This implementation copies column values into the array in the same 
     * order they're returned from the <code>ResultSet</code>.  Array elements
     * will be set to <code>null</code> if the column was SQL NULL.
     *
     * @see org.apache.commons.dbutils.RowProcessor#toArray(java.sql.ResultSet)
     */
    public Object[] toArray(ResultSet rs) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        int cols = meta.getColumnCount();
        Object[] result = new Object[cols];

        for (int i = 0; i < cols; i++) {
            result[i] = rs.getObject(i + 1);
        }

        return result;
    }

    /**
     * Convert a <code>ResultSet</code> row into a JavaBean.  This 
     * implementation uses reflection and <code>BeanInfo</code> classes to 
     * match column names to bean property names.  Properties are matched to 
     * columns based on several factors:
     * <br/>
     * <ol>
     *     <li>
     *     The class has a writable property with the same name as a column.
     *     The name comparison is case insensitive.
     *     </li>
     * 
     *     <li>
     *     The property's set method parameter type matches the column 
     *     type. If the data types do not match, the setter will not be called.
     *     </li>
     * </ol>
     * 
     * <p>
     * Primitive bean properties are set to their defaults when SQL NULL is
     * returned from the <code>ResultSet</code>.  Numeric fields are set to 0
     * and booleans are set to false.  Object bean properties are set to 
     * <code>null</code> when SQL NULL is returned.  This is the same behavior
     * as the <code>ResultSet</code> get* methods.
     * </p>
     * 
     * @see org.apache.commons.dbutils.RowProcessor#toBean(java.sql.ResultSet, java.lang.Class)
     */
    public Object toBean(ResultSet rs, Class type) throws SQLException {

        PropertyDescriptor[] props = this.propertyDescriptors(type);

        ResultSetMetaData rsmd = rs.getMetaData();

        int[] columnToProperty =
            this.convert.mapColumnsToProperties(rsmd, props);

        int cols = rsmd.getColumnCount();

        return this.createBean(rs, type, props, columnToProperty, cols);
    }

    /**
     * Convert a <code>ResultSet</code> into a <code>List</code> of JavaBeans.  
     * This implementation uses reflection and <code>BeanInfo</code> classes to 
     * match column names to bean property names. Properties are matched to 
     * columns based on several factors:
     * <br/>
     * <ol>
     *     <li>
     *     The class has a writable property with the same name as a column.
     *     The name comparison is case insensitive.
     *     </li>
     * 
     *     <li>
     *     The property's set method parameter type matches the column 
     *     type. If the data types do not match, the setter will not be called.
     *     </li>
     * </ol>
     * 
     * <p>
     * Primitive bean properties are set to their defaults when SQL NULL is
     * returned from the <code>ResultSet</code>.  Numeric fields are set to 0
     * and booleans are set to false.  Object bean properties are set to 
     * <code>null</code> when SQL NULL is returned.  This is the same behavior
     * as the <code>ResultSet</code> get* methods.
     * </p>
     * 
     * @see org.apache.commons.dbutils.RowProcessor#toBeanList(java.sql.ResultSet, java.lang.Class)
     */
    public List toBeanList(ResultSet rs, Class type) throws SQLException {
        List results = new ArrayList();

        if (!rs.next()) {
            return results;
        }

        PropertyDescriptor[] props = this.propertyDescriptors(type);
        ResultSetMetaData rsmd = rs.getMetaData();
        
        int[] columnToProperty =
            this.convert.mapColumnsToProperties(rsmd, props);
            
        int cols = rsmd.getColumnCount();

        do {
            results.add(this.createBean(rs, type, props, columnToProperty, cols));

        } while (rs.next());

        return results;
    }

    /**
     * Creates a new object and initializes its fields from the ResultSet.
     *
     * @param rs The result set
     * @param type The bean type (the return type of the object)
     * @param props The property descriptors
     * @param columnToProperty The column indices in the result set
     * @param cols The number of columns
     * @return An initialized object.
     * @throws SQLException If a database error occurs
     */
    private Object createBean(
        ResultSet rs,
        Class type,
        PropertyDescriptor[] props,
        int[] columnToProperty,
        int cols)
        throws SQLException {

        Object bean = this.newInstance(type);

        for (int i = 1; i <= cols; i++) {

            if (columnToProperty[i] == ColumnProcessor.PROPERTY_NOT_FOUND) {
                continue;
            }
            
            PropertyDescriptor prop = props[columnToProperty[i]];
            Class propType = prop.getPropertyType();

            Object value = this.convert.process(rs, i, propType);

            if (propType != null && value == null && propType.isPrimitive()) {
                value = primitiveDefaults.get(propType);
            }

            this.callSetter(bean, prop, value);
        }

        return bean;
    }

    /**
     * Convert a <code>ResultSet</code> row into a <code>Map</code>.  This 
     * implementation returns a <code>Map</code> with case insensitive column
     * names as keys.  Calls to <code>map.get("COL")</code> and 
     * <code>map.get("col")</code> return the same value.
     * @see org.apache.commons.dbutils.RowProcessor#toMap(java.sql.ResultSet)
     */
    public Map toMap(ResultSet rs) throws SQLException {
        Map result = new CaseInsensitiveHashMap();
        ResultSetMetaData rsmd = rs.getMetaData();
        int cols = rsmd.getColumnCount();

        for (int i = 1; i <= cols; i++) {
            result.put(rsmd.getColumnName(i), rs.getObject(i));
        }

        return result;
    }

    /**
     * Calls the setter method on the target object for the given property.
     * If no setter method exists for the property, this method does nothing.
     * @param target The object to set the property on.
     * @param prop The property to set.
     * @param value The value to pass into the setter.
     * @throws SQLException if an error occurs setting the property.
     */
    private void callSetter(
        Object target,
        PropertyDescriptor prop,
        Object value)
        throws SQLException {

        Method setter = prop.getWriteMethod();

        if (setter == null) {
            return;
        }

        Class[] params = setter.getParameterTypes();
        try {
            // Don't call setter if the value object isn't the right type 
            if (this.isCompatibleType(value, params[0])) {
                setter.invoke(target, new Object[] { value });
            }

        } catch (IllegalArgumentException e) {
            throw new SQLException(
                "Cannot set " + prop.getName() + ": " + e.getMessage());

        } catch (IllegalAccessException e) {
            throw new SQLException(
                "Cannot set " + prop.getName() + ": " + e.getMessage());

        } catch (InvocationTargetException e) {
            throw new SQLException(
                "Cannot set " + prop.getName() + ": " + e.getMessage());
        }
    }

    /**
     * ResultSet.getObject() returns an Integer object for an INT column.  The
     * setter method for the property might take an Integer or a primitive int.
     * This method returns true if the value can be successfully passed into
     * the setter method.  Remember, Method.invoke() handles the unwrapping
     * of Integer into an int.
     * 
     * @param value The value to be passed into the setter method.
     * @param type The setter's parameter type.
     * @return boolean True if the value is compatible.
     */
    private boolean isCompatibleType(Object value, Class type) {
        // Do object check first, then primitives
        if (value == null || type.isInstance(value)) {
            return true;

        } else if (
            type.equals(Integer.TYPE) && Integer.class.isInstance(value)) {
            return true;

        } else if (type.equals(Long.TYPE) && Long.class.isInstance(value)) {
            return true;

        } else if (
            type.equals(Double.TYPE) && Double.class.isInstance(value)) {
            return true;

        } else if (type.equals(Float.TYPE) && Float.class.isInstance(value)) {
            return true;

        } else if (type.equals(Short.TYPE) && Short.class.isInstance(value)) {
            return true;

        } else if (type.equals(Byte.TYPE) && Byte.class.isInstance(value)) {
            return true;

        } else if (
            type.equals(Character.TYPE) && Character.class.isInstance(value)) {
            return true;

        } else if (
            type.equals(Boolean.TYPE) && Boolean.class.isInstance(value)) {
            return true;

        } else {
            return false;
        }

    }

    /**
     * Returns a new instance of the given Class.
     *
     * @param c The Class to create an object from.
     * @return A newly created object of the Class.
     * @throws SQLException if creation failed.
     */
    private Object newInstance(Class c) throws SQLException {
        try {
            return c.newInstance();

        } catch (InstantiationException e) {
            throw new SQLException(
                "Cannot create " + c.getName() + ": " + e.getMessage());

        } catch (IllegalAccessException e) {
            throw new SQLException(
                "Cannot create " + c.getName() + ": " + e.getMessage());
        }
    }

    /**
     * Returns a PropertyDescriptor[] for the given Class.
     *
     * @param c The Class to retrieve PropertyDescriptors for.
     * @return A PropertyDescriptor[] describing the Class.
     * @throws SQLException if introspection failed.
     */
    private PropertyDescriptor[] propertyDescriptors(Class c)
        throws SQLException {
        // Introspector caches BeanInfo classes for better performance
        BeanInfo beanInfo = null;
        try {
            beanInfo = Introspector.getBeanInfo(c);

        } catch (IntrospectionException e) {
            throw new SQLException(
                "Bean introspection failed: " + e.getMessage());
        }

        return beanInfo.getPropertyDescriptors();
    }

    /**
     * A Map that converts all keys to lowercase Strings for case insensitive
     * lookups.  This is needed for the toMap() implementation because 
     * databases don't consistenly handle the casing of column names. 
     */
    private static class CaseInsensitiveHashMap extends HashMap {

        /**
         * @see java.util.Map#containsKey(java.lang.Object)
         */
        public boolean containsKey(Object key) {
            return super.containsKey(key.toString().toLowerCase());
        }

        /**
         * @see java.util.Map#get(java.lang.Object)
         */
        public Object get(Object key) {
            return super.get(key.toString().toLowerCase());
        }

        /**
         * @see java.util.Map#put(java.lang.Object, java.lang.Object)
         */
        public Object put(Object key, Object value) {
            return super.put(key.toString().toLowerCase(), value);
        }

        /**
         * @see java.util.Map#putAll(java.util.Map)
         */
        public void putAll(Map m) {
            Iterator iter = m.keySet().iterator();
            while (iter.hasNext()) {
                Object key = iter.next();
                Object value = m.get(key);
                this.put(key, value);
            }
        }

        /**
         * @see java.util.Map#remove(java.lang.Object)
         */
        public Object remove(Object key) {
            return super.remove(key.toString().toLowerCase());
        }
    }
    
}
