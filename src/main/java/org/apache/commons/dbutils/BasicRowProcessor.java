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

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Basic implementation of the {@code RowProcessor} interface.
 *
 * <p>
 * This class is thread-safe.
 * </p>
 *
 * @see RowProcessor
 */
public class BasicRowProcessor implements RowProcessor {

    /**
     * The default BeanProcessor instance to use if not supplied in the
     * constructor.
     */
    private static final BeanProcessor defaultConvert = new BeanProcessor();

    /**
     * The Singleton instance of this class.
     */
    private static final BasicRowProcessor instance = new BasicRowProcessor();

    protected static Map<String, Object> createCaseInsensitiveHashMap(final int cols) {
        return new CaseInsensitiveHashMap(cols);
    }

    /**
     * Returns the Singleton instance of this class.
     *
     * @return The single instance of this class.
     * @deprecated Create instances with the constructors instead.  This will
     * be removed after DbUtils 1.1.
     */
    @Deprecated
    public static BasicRowProcessor instance() {
        return instance;
    }

    /**
     * Use this to process beans.
     */
    private final BeanProcessor convert;

    /**
     * BasicRowProcessor constructor.  Bean processing defaults to a
     * BeanProcessor instance.
     */
    public BasicRowProcessor() {
        this(defaultConvert);
    }

    /**
     * BasicRowProcessor constructor.
     * @param convert The BeanProcessor to use when converting columns to
     * bean properties.
     * @since DbUtils 1.1
     */
    public BasicRowProcessor(final BeanProcessor convert) {
        super();
        this.convert = convert;
    }

    /**
     * Convert a {@code ResultSet} row into an {@code Object[]}.
     * This implementation copies column values into the array in the same
     * order they're returned from the {@code ResultSet}.  Array elements
     * will be set to {@code null} if the column was SQL NULL.
     *
     * @see org.apache.commons.dbutils.RowProcessor#toArray(java.sql.ResultSet)
     * @param rs ResultSet that supplies the array data
     * @throws SQLException if a database access error occurs
     * @return the newly created array
     */
    @Override
    public Object[] toArray(final ResultSet rs) throws SQLException {
        final ResultSetMetaData meta = rs.getMetaData();
        final int cols = meta.getColumnCount();
        final Object[] result = new Object[cols];

        for (int i = 0; i < cols; i++) {
            result[i] = rs.getObject(i + 1);
        }

        return result;
    }

    /**
     * Convert a {@code ResultSet} row into a JavaBean.  This
     * implementation delegates to a BeanProcessor instance.
     * @see org.apache.commons.dbutils.RowProcessor#toBean(java.sql.ResultSet, java.lang.Class)
     * @see org.apache.commons.dbutils.BeanProcessor#toBean(java.sql.ResultSet, java.lang.Class)
     * @param <T> The type of bean to create
     * @param rs ResultSet that supplies the bean data
     * @param type Class from which to create the bean instance
     * @throws SQLException if a database access error occurs
     * @return the newly created bean
     */
    @Override
    public <T> T toBean(final ResultSet rs, final Class<? extends T> type) throws SQLException {
        return this.convert.toBean(rs, type);
    }

    /**
     * Convert a {@code ResultSet} into a {@code List} of JavaBeans.
     * This implementation delegates to a BeanProcessor instance.
     * @see org.apache.commons.dbutils.RowProcessor#toBeanList(java.sql.ResultSet, java.lang.Class)
     * @see org.apache.commons.dbutils.BeanProcessor#toBeanList(java.sql.ResultSet, java.lang.Class)
     * @param <T> The type of bean to create
     * @param rs ResultSet that supplies the bean data
     * @param type Class from which to create the bean instance
     * @throws SQLException if a database access error occurs
     * @return A {@code List} of beans with the given type in the order
     * they were returned by the {@code ResultSet}.
     */
    @Override
    public <T> List<T> toBeanList(final ResultSet rs, final Class<? extends T> type) throws SQLException {
        return this.convert.toBeanList(rs, type);
    }

    /**
     * Convert a {@code ResultSet} row into a {@code Map}.
     *
     * <p>
     * This implementation returns a {@code Map} with case insensitive column names as keys. Calls to
     * {@code map.get("COL")} and {@code map.get("col")} return the same value. Furthermore this implementation
     * will return an ordered map, that preserves the ordering of the columns in the ResultSet, so that iterating over
     * the entry set of the returned map will return the first column of the ResultSet, then the second and so forth.
     * </p>
     *
     * @param rs ResultSet that supplies the map data
     * @return the newly created Map
     * @throws SQLException if a database access error occurs
     * @see org.apache.commons.dbutils.RowProcessor#toMap(java.sql.ResultSet)
     */
    @Override
    public Map<String, Object> toMap(final ResultSet rs) throws SQLException {
        final ResultSetMetaData rsmd = rs.getMetaData();
        final int cols = rsmd.getColumnCount();
        final Map<String, Object> result = createCaseInsensitiveHashMap(cols);

        for (int i = 1; i <= cols; i++) {
            String columnName = rsmd.getColumnLabel(i);
            if (null == columnName || 0 == columnName.length()) {
              columnName = rsmd.getColumnName(i);
            }
            result.put(columnName, rs.getObject(i));
        }

        return result;
    }


    /**
     * A Map that converts all keys to lowercase Strings for case insensitive
     * lookups.  This is needed for the toMap() implementation because
     * databases don't consistently handle the casing of column names.
     *
     * <p>The keys are stored as they are given [BUG #DBUTILS-34], so we maintain
     * an internal mapping from lowercase keys to the real keys in order to
     * achieve the case insensitive lookup.
     *
     * <p>Note: This implementation does not allow {@code null}
     * for key, whereas {@link LinkedHashMap} does, because of the code:
     * <pre>
     * key.toString().toLowerCase()
     * </pre>
     */
    private static final class CaseInsensitiveHashMap extends LinkedHashMap<String, Object> {

        private CaseInsensitiveHashMap(final int initialCapacity) {
            super(initialCapacity);
        }

        /**
         * The internal mapping from lowercase keys to the real keys.
         *
         * <p>
         * Any query operation using the key
         * ({@link #get(Object)}, {@link #containsKey(Object)})
         * is done in three steps:
         * <ul>
         * <li>convert the parameter key to lower case</li>
         * <li>get the actual key that corresponds to the lower case key</li>
         * <li>query the map with the actual key</li>
         * </ul>
         * </p>
         */
        private final Map<String, String> lowerCaseMap = new HashMap<>();

        /**
         * Required for serialization support.
         *
         * @see java.io.Serializable
         */
        private static final long serialVersionUID = -2848100435296897392L;

        /** {@inheritDoc} */
        @Override
        public boolean containsKey(final Object key) {
            final Object realKey = lowerCaseMap.get(key.toString().toLowerCase(Locale.ENGLISH));
            return super.containsKey(realKey);
            // Possible optimisation here:
            // Since the lowerCaseMap contains a mapping for all the keys,
            // we could just do this:
            // return lowerCaseMap.containsKey(key.toString().toLowerCase());
        }

        /** {@inheritDoc} */
        @Override
        public Object get(final Object key) {
            final Object realKey = lowerCaseMap.get(key.toString().toLowerCase(Locale.ENGLISH));
            return super.get(realKey);
        }

        /** {@inheritDoc} */
        @Override
        public Object put(final String key, final Object value) {
            /*
             * In order to keep the map and lowerCaseMap synchronized,
             * we have to remove the old mapping before putting the
             * new one. Indeed, oldKey and key are not necessaliry equals.
             * (That's why we call super.remove(oldKey) and not just
             * super.put(key, value))
             */
            final Object oldKey = lowerCaseMap.put(key.toLowerCase(Locale.ENGLISH), key);
            final Object oldValue = super.remove(oldKey);
            super.put(key, value);
            return oldValue;
        }

        /** {@inheritDoc} */
        @Override
        public void putAll(final Map<? extends String, ?> m) {
            for (final Map.Entry<? extends String, ?> entry : m.entrySet()) {
                final String key = entry.getKey();
                final Object value = entry.getValue();
                this.put(key, value);
            }
        }

        /** {@inheritDoc} */
        @Override
        public Object remove(final Object key) {
            final Object realKey = lowerCaseMap.remove(key.toString().toLowerCase(Locale.ENGLISH));
            return super.remove(realKey);
        }
    }

}
