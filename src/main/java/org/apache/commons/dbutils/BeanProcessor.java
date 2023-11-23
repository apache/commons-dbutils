/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.dbutils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * <p>
 * {@code BeanProcessor} matches column names to bean property names
 * and converts {@code ResultSet} columns into objects for those bean
 * properties.  Subclasses should override the methods in the processing chain
 * to customize behavior.
 * </p>
 *
 * <p>
 * This class is thread-safe.
 * </p>
 *
 * @see BasicRowProcessor
 *
 * @since 1.1
 */
public class BeanProcessor {

    /**
     * Special array value used by {@code mapColumnsToProperties} that
     * indicates there is no bean property that matches a column from a
     * {@code ResultSet}.
     */
    protected static final int PROPERTY_NOT_FOUND = -1;

    /**
     * Set a bean's primitive properties to these defaults when SQL NULL
     * is returned.  These are the same as the defaults that ResultSet get*
     * methods return in the event of a NULL column.
     */
    private static final Map<Class<?>, Object> PRIMITIVE_DEFAULTS = new HashMap<>();

    private static final List<ColumnHandler<?>> COLUMN_HANDLERS = new ArrayList<>();

    private static final List<PropertyHandler> PROPERTY_HANDLERS = new ArrayList<>();

    static {
        PRIMITIVE_DEFAULTS.put(Integer.TYPE, Integer.valueOf(0));
        PRIMITIVE_DEFAULTS.put(Short.TYPE, Short.valueOf((short) 0));
        PRIMITIVE_DEFAULTS.put(Byte.TYPE, Byte.valueOf((byte) 0));
        PRIMITIVE_DEFAULTS.put(Float.TYPE, Float.valueOf(0f));
        PRIMITIVE_DEFAULTS.put(Double.TYPE, Double.valueOf(0d));
        PRIMITIVE_DEFAULTS.put(Long.TYPE, Long.valueOf(0L));
        PRIMITIVE_DEFAULTS.put(Boolean.TYPE, Boolean.FALSE);
        PRIMITIVE_DEFAULTS.put(Character.TYPE, Character.valueOf((char) 0));

        // Use a ServiceLoader to find implementations
        ServiceLoader.load(ColumnHandler.class).forEach(COLUMN_HANDLERS::add);

        // Use a ServiceLoader to find implementations
        ServiceLoader.load(PropertyHandler.class).forEach(PROPERTY_HANDLERS::add);
    }

    /**
     * ResultSet column to bean property name overrides.
     */
    private final Map<String, String> columnToPropertyOverrides;

    /**
     * Constructor for BeanProcessor.
     */
    public BeanProcessor() {
        this(new HashMap<>());
    }

    /**
     * Constructor for BeanProcessor configured with column to property name overrides.
     *
     * @param columnToPropertyOverrides ResultSet column to bean property name overrides
     * @since 1.5
     */
    public BeanProcessor(final Map<String, String> columnToPropertyOverrides) {
        if (columnToPropertyOverrides == null) {
            throw new IllegalArgumentException("columnToPropertyOverrides map cannot be null");
        }
        this.columnToPropertyOverrides = columnToPropertyOverrides;
    }

    /**
     * Calls the setter method on the target object for the given property.
     * If no setter method exists for the property, this method does nothing.
     * @param target The object to set the property on.
     * @param prop The property to set.
     * @param value The value to pass into the setter.
     * @throws SQLException if an error occurs setting the property.
     */
    private void callSetter(final Object target, final PropertyDescriptor prop, Object value)
            throws SQLException {

        final Method setter = getWriteMethod(target, prop, value);

        if (setter == null || setter.getParameterTypes().length != 1) {
            return;
        }

        try {
            final Class<?> firstParam = setter.getParameterTypes()[0];
            for (final PropertyHandler handler : PROPERTY_HANDLERS) {
                if (handler.match(firstParam, value)) {
                    value = handler.apply(firstParam, value);
                    break;
                }
            }

            // Don't call setter if the value object isn't the right type
            if (!this.isCompatibleType(value, firstParam)) {
                throw new SQLException(
                        "Cannot set " + prop.getName() + ": incompatible types, cannot convert " + value.getClass().getName() + " to " + firstParam.getName());
                // value cannot be null here because isCompatibleType allows null
            }
            setter.invoke(target, value);

        } catch (final IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
            throw new SQLException("Cannot set " + prop.getName() + ": " + e.getMessage());
        }
    }

    /**
     * Creates a new object and initializes its fields from the ResultSet.
     * @param <T> The type of bean to create
     * @param resultSet The result set.
     * @param type The bean type (the return type of the object).
     * @param props The property descriptors.
     * @param columnToProperty The column indices in the result set.
     * @return An initialized object.
     * @throws SQLException if a database error occurs.
     */
    private <T> T createBean(final ResultSet resultSet, final Class<T> type, final PropertyDescriptor[] props, final int[] columnToProperty)
            throws SQLException {
        return populateBean(resultSet, this.newInstance(type), props, columnToProperty);
    }

    /** Does nothing. */
    @Override
    protected final void finalize() {
        // SpotBugs CT_CONSTRUCTOR_THROW
    }

    /**
     * Gets the write method to use when setting {@code value} to the {@code target}.
     *
     * @param target Object where the write method will be called.
     * @param prop   BeanUtils information.
     * @param value  The value that will be passed to the write method.
     * @return The {@link java.lang.reflect.Method} to call on {@code target} to write {@code value} or {@code null} if
     *         there is no suitable write method.
     */
    protected Method getWriteMethod(final Object target, final PropertyDescriptor prop, final Object value) {
        return prop.getWriteMethod();
    }

    /**
     * ResultSet.getObject() returns an Integer object for an INT column.  The
     * setter method for the property might take an Integer or a primitive int.
     * This method returns true if the value can be successfully passed into
     * the setter method.  Remember, Method.invoke() handles the unwrapping
     * of Integer into an int.
     *
     * @param value The value to be passed into the setter method.
     * @param type The setter's parameter type (non-null)
     * @return boolean True if the value is compatible (null => true)
     */
    private boolean isCompatibleType(final Object value, final Class<?> type) {
        // Do object check first, then primitives
        return value == null || type.isInstance(value) || matchesPrimitive(type, value.getClass());
    }

    /**
     * The positions in the returned array represent column numbers.  The
     * values stored at each position represent the index in the
     * {@code PropertyDescriptor[]} for the bean property that matches
     * the column name.  If no bean property was found for a column, the
     * position is set to {@code PROPERTY_NOT_FOUND}.
     *
     * @param rsmd The {@code ResultSetMetaData} containing column
     * information.
     *
     * @param props The bean property descriptors.
     *
     * @throws SQLException if a database access error occurs
     *
     * @return An int[] with column index to property index mappings.  The 0th
     * element is meaningless because JDBC column indexing starts at 1.
     */
    protected int[] mapColumnsToProperties(final ResultSetMetaData rsmd,
            final PropertyDescriptor[] props) throws SQLException {

        final int cols = rsmd.getColumnCount();
        final int[] columnToProperty = new int[cols + 1];
        Arrays.fill(columnToProperty, PROPERTY_NOT_FOUND);

        for (int col = 1; col <= cols; col++) {
            String columnName = rsmd.getColumnLabel(col);
            if (null == columnName || 0 == columnName.length()) {
              columnName = rsmd.getColumnName(col);
            }
            String propertyName = columnToPropertyOverrides.get(columnName);
            if (propertyName == null) {
                propertyName = columnName;
            }
            if (propertyName == null) {
                propertyName = Integer.toString(col);
            }

            for (int i = 0; i < props.length; i++) {
                final PropertyDescriptor prop = props[i];
                final Method reader = prop.getReadMethod();

                // Check for @Column annotations as explicit marks
                final Column column;
                if (reader != null) {
                    column = reader.getAnnotation(Column.class);
                } else {
                    column = null;
                }

                final String propertyColumnName;
                if (column != null) {
                    propertyColumnName = column.name();
                } else {
                    propertyColumnName = prop.getName();
                }
                if (propertyName.equalsIgnoreCase(propertyColumnName)) {
                    columnToProperty[col] = i;
                    break;
                }
            }
        }

        return columnToProperty;
    }

    /**
     * Check whether a value is of the same primitive type as {@code targetType}.
     *
     * @param targetType The primitive type to target.
     * @param valueType The value to match to the primitive type.
     * @return Whether {@code valueType} can be coerced (e.g. autoboxed) into {@code targetType}.
     */
    private boolean matchesPrimitive(final Class<?> targetType, final Class<?> valueType) {
        if (!targetType.isPrimitive()) {
            return false;
        }

        try {
            // see if there is a "TYPE" field.  This is present for primitive wrappers.
            final Field typeField = valueType.getField("TYPE");
            final Object primitiveValueType = typeField.get(valueType);

            if (targetType == primitiveValueType) {
                return true;
            }
        } catch (final NoSuchFieldException | IllegalAccessException ignored) {
            // an inaccessible TYPE field is a good sign that we're not working with a primitive wrapper.
            // nothing to do.  we can't match for compatibility
        }
        return false;
    }

    /**
     * Factory method that returns a new instance of the given Class.  This
     * is called at the start of the bean creation process and may be
     * overridden to provide custom behavior like returning a cached bean
     * instance.
     * @param <T> The type of object to create
     * @param c The Class to create an object from.
     * @return A newly created object of the Class.
     * @throws SQLException if creation failed.
     */
    protected <T> T newInstance(final Class<T> c) throws SQLException {
        try {
            return c.getDeclaredConstructor().newInstance();

        } catch (final IllegalAccessException | InstantiationException | InvocationTargetException |
            NoSuchMethodException e) {
            throw new SQLException("Cannot create " + c.getName() + ": " + e.getMessage());
        }
    }

    /**
     * Initializes the fields of the provided bean from the ResultSet.
     * @param <T> The type of bean
     * @param resultSet The result set.
     * @param bean The bean to be populated.
     * @return An initialized object.
     * @throws SQLException if a database error occurs.
     */
    public <T> T populateBean(final ResultSet resultSet, final T bean) throws SQLException {
        final PropertyDescriptor[] props = this.propertyDescriptors(bean.getClass());
        final ResultSetMetaData rsmd = resultSet.getMetaData();
        final int[] columnToProperty = this.mapColumnsToProperties(rsmd, props);

        return populateBean(resultSet, bean, props, columnToProperty);
    }

    /**
     * This method populates a bean from the ResultSet based upon the underlying meta-data.
     *
     * @param <T> The type of bean
     * @param resultSet The result set.
     * @param bean The bean to be populated.
     * @param props The property descriptors.
     * @param columnToProperty The column indices in the result set.
     * @return An initialized object.
     * @throws SQLException if a database error occurs.
     */
    private <T> T populateBean(final ResultSet resultSet, final T bean,
            final PropertyDescriptor[] props, final int[] columnToProperty)
            throws SQLException {

        for (int i = 1; i < columnToProperty.length; i++) {

            if (columnToProperty[i] == PROPERTY_NOT_FOUND) {
                continue;
            }

            final PropertyDescriptor prop = props[columnToProperty[i]];
            final Class<?> propType = prop.getPropertyType();

            Object value = null;
            if (propType != null) {
                value = this.processColumn(resultSet, i, propType);

                if (value == null && propType.isPrimitive()) {
                    value = PRIMITIVE_DEFAULTS.get(propType);
                }
            }

            this.callSetter(bean, prop, value);
        }

        return bean;
    }

    /**
     * Convert a {@code ResultSet} column into an object.  Simple
     * implementations could just call {@code rs.getObject(index)} while
     * more complex implementations could perform type manipulation to match
     * the column's type to the bean property type.
     *
     * <p>
     * This implementation calls the appropriate {@code ResultSet} getter
     * method for the given property type to perform the type conversion.  If
     * the property type doesn't match one of the supported
     * {@code ResultSet} types, {@code getObject} is called.
     * </p>
     *
     * @param resultSet The {@code ResultSet} currently being processed.  It is
     * positioned on a valid row before being passed into this method.
     *
     * @param index The current column index being processed.
     *
     * @param propType The bean property type that this column needs to be
     * converted into.
     *
     * @throws SQLException if a database access error occurs
     *
     * @return The object from the {@code ResultSet} at the given column
     * index after optional type processing or {@code null} if the column
     * value was SQL NULL.
     */
    protected Object processColumn(final ResultSet resultSet, final int index, final Class<?> propType)
        throws SQLException {

        Object retval = resultSet.getObject(index);

        if ( !propType.isPrimitive() && retval == null ) {
            return null;
        }

        for (final ColumnHandler<?> handler : COLUMN_HANDLERS) {
            if (handler.match(propType)) {
                retval = handler.apply(resultSet, index);
                break;
            }
        }

        return retval;

    }

    /**
     * Returns a PropertyDescriptor[] for the given Class.
     *
     * @param c The Class to retrieve PropertyDescriptors for.
     * @return A PropertyDescriptor[] describing the Class.
     * @throws SQLException if introspection failed.
     */
    private PropertyDescriptor[] propertyDescriptors(final Class<?> c)
        throws SQLException {
        // Introspector caches BeanInfo classes for better performance
        BeanInfo beanInfo = null;
        try {
            beanInfo = Introspector.getBeanInfo(c);

        } catch (final IntrospectionException e) {
            throw new SQLException(
                "Bean introspection failed: " + e.getMessage());
        }

        return beanInfo.getPropertyDescriptors();
    }

    /**
     * Convert a {@code ResultSet} row into a JavaBean.  This
     * implementation uses reflection and {@code BeanInfo} classes to
     * match column names to bean property names.  Properties are matched to
     * columns based on several factors:
     * &lt;br/&gt;
     * &lt;ol&gt;
     *     &lt;li&gt;
     *     The class has a writable property with the same name as a column.
     *     The name comparison is case insensitive.
     *     &lt;/li&gt;
     *
     *     &lt;li&gt;
     *     The column type can be converted to the property's set method
     *     parameter type with a ResultSet.get* method.  If the conversion fails
     *     (ie. the property was an int and the column was a Timestamp) an
     *     SQLException is thrown.
     *     &lt;/li&gt;
     * &lt;/ol&gt;
     *
     * &lt;p&gt;
     * Primitive bean properties are set to their defaults when SQL NULL is
     * returned from the {@code ResultSet}.  Numeric fields are set to 0
     * and booleans are set to false.  Object bean properties are set to
     * {@code null} when SQL NULL is returned.  This is the same behavior
     * as the {@code ResultSet} get* methods.
     * &lt;/p&gt;
     * @param <T> The type of bean to create
     * @param rs ResultSet that supplies the bean data
     * @param type Class from which to create the bean instance
     * @throws SQLException if a database access error occurs
     * @return the newly created bean
     */
    public <T> T toBean(final ResultSet rs, final Class<? extends T> type) throws SQLException {
        final T bean = this.newInstance(type);
        return this.populateBean(rs, bean);
    }

    /**
     * Convert a {@code ResultSet} into a {@code List} of JavaBeans.
     * This implementation uses reflection and {@code BeanInfo} classes to
     * match column names to bean property names. Properties are matched to
     * columns based on several factors:
     * &lt;br/&gt;
     * &lt;ol&gt;
     *     &lt;li&gt;
     *     The class has a writable property with the same name as a column.
     *     The name comparison is case insensitive.
     *     &lt;/li&gt;
     *
     *     &lt;li&gt;
     *     The column type can be converted to the property's set method
     *     parameter type with a ResultSet.get* method.  If the conversion fails
     *     (ie. the property was an int and the column was a Timestamp) an
     *     SQLException is thrown.
     *     &lt;/li&gt;
     * &lt;/ol&gt;
     *
     * <p>
     * Primitive bean properties are set to their defaults when SQL NULL is
     * returned from the {@code ResultSet}.  Numeric fields are set to 0
     * and booleans are set to false.  Object bean properties are set to
     * {@code null} when SQL NULL is returned.  This is the same behavior
     * as the {@code ResultSet} get* methods.
     * &lt;/p&gt;
     * @param <T> The type of bean to create
     * @param resultSet ResultSet that supplies the bean data
     * @param type Class from which to create the bean instance
     * @throws SQLException if a database access error occurs
     * @return the newly created List of beans
     */
    public <T> List<T> toBeanList(final ResultSet resultSet, final Class<? extends T> type) throws SQLException {
        final List<T> results = new ArrayList<>();

        if (!resultSet.next()) {
            return results;
        }

        final PropertyDescriptor[] props = this.propertyDescriptors(type);
        final ResultSetMetaData rsmd = resultSet.getMetaData();
        final int[] columnToProperty = this.mapColumnsToProperties(rsmd, props);

        do {
            results.add(this.createBean(resultSet, type, props, columnToProperty));
        } while (resultSet.next());

        return results;
    }

}
