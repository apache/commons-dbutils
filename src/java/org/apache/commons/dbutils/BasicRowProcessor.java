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
    public BasicRowProcessor(BeanProcessor convert) {
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
     * implementation delegates to a BeanProcessor instance.
     * @see org.apache.commons.dbutils.RowProcessor#toBean(java.sql.ResultSet, java.lang.Class)
     * @see org.apache.commons.dbutils.BeanProcessor#toBean(java.sql.ResultSet, java.lang.Class) 
     */
    public Object toBean(ResultSet rs, Class type) throws SQLException {
        return this.convert.toBean(rs, type);
    }

    /**
     * Convert a <code>ResultSet</code> into a <code>List</code> of JavaBeans.  
     * This implementation delegates to a BeanProcessor instance. 
     * @see org.apache.commons.dbutils.RowProcessor#toBeanList(java.sql.ResultSet, java.lang.Class)
     * @see org.apache.commons.dbutils.BeanProcessor#toBeanList(java.sql.ResultSet, java.lang.Class)
     */
    public List toBeanList(ResultSet rs, Class type) throws SQLException {
        return this.convert.toBeanList(rs, type);
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
     * A Map that converts all keys to lowercase Strings for case insensitive
     * lookups.  This is needed for the toMap() implementation because 
     * databases don't consistenly handle the casing of column names. 
     * 
     * <p>The keys are stored as they are given [BUG #DBUTILS-34], so we maintain
     * an internal mapping from lowercase keys to the real keys in order to 
     * achieve the case insensitive lookup.
     * 
     * <p>Note: This implementation does not allow <tt>null</tt>
     * for key, whereas {@link HashMap} does, because of the code:
     * <pre>
     * key.toString().toLowerCase()
     * </pre>
     */
    private static class CaseInsensitiveHashMap extends HashMap {

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
        private final Map lowerCaseMap = new HashMap();

        /**
         * Required for serialization support.
         * 
         * @see java.io.Serializable
         */ 
        private static final long serialVersionUID = 1841673097701957808L;

        /**
         * @see java.util.Map#containsKey(java.lang.Object)
         */
        public boolean containsKey(Object key) {
            Object realKey = lowerCaseMap.get(key.toString().toLowerCase());
            return super.containsKey(realKey);
            // Possible optimisation here:
            // Since the lowerCaseMap contains a mapping for all the keys,
            // we could just do this:
            // return lowerCaseMap.containsKey(key.toString().toLowerCase());
        }

        /**
         * @see java.util.Map#get(java.lang.Object)
         */
        public Object get(Object key) {
            Object realKey = lowerCaseMap.get(key.toString().toLowerCase());
            return super.get(realKey);
        }

        /**
         * @see java.util.Map#put(java.lang.Object, java.lang.Object)
         */
        public Object put(Object key, Object value) {
            /*
             * In order to keep the map and lowerCaseMap synchronized,
             * we have to remove the old mapping before putting the 
             * new one. Indeed, oldKey and key are not necessaliry equals.
             * (That's why we call super.remove(oldKey) and not just
             * super.put(key, value))
             */
            Object oldKey = lowerCaseMap.put(key.toString().toLowerCase(), key);
            Object oldValue = super.remove(oldKey);
            super.put(key, value);
            return oldValue;
        }

        /**
         * @see java.util.Map#putAll(java.util.Map)
         */
        public void putAll(Map m) {
            Iterator iter = m.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                Object key = entry.getKey();
                Object value = entry.getValue();
                this.put(key, value);
            }
        }

        /**
         * @see java.util.Map#remove(java.lang.Object)
         */
        public Object remove(Object key) {
            Object realKey = lowerCaseMap.remove(key.toString().toLowerCase());
            return super.remove(realKey);
        }
    }
    
}
