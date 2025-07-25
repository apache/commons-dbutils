/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.dbutils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * {@code QueryLoader} is a registry for sets of queries so
 * that multiple copies of the same queries aren't loaded into memory.
 * This implementation loads properties files filled with query name to
 * SQL mappings.  This class is thread safe.
 */
public class QueryLoader {

    /**
     * The Singleton INSTANCE of this class.
     */
    private static final QueryLoader INSTANCE = new QueryLoader();

    /**
     * Matches .xml file extensions.
     */
    private static final Pattern dotXml = Pattern.compile(".+\\.[xX][mM][lL]");

    /**
     * Return an INSTANCE of this class.
     * @return The Singleton INSTANCE.
     */
    public static QueryLoader instance() {
        return INSTANCE;
    }

    /**
     * Maps query set names to Maps of their queries.
     */
    private final Map<String, Map<String, String>> queries = new HashMap<>();

    /**
     * QueryLoader constructor.
     */
    protected QueryLoader() {
    }

    /**
     * Loads a Map of query names to SQL values.  The Maps are cached so a
     * subsequent request to load queries from the same path will return
     * the cached Map.  The properties file to load can be in either
     * line-oriented or XML format.  XML formatted properties files must use a
     * {@code .xml} file extension.
     *
     * @param path The path that the ClassLoader will use to find the file.
     * This is <strong>not</strong> a file system path.  If you had a jarred
     * Queries.properties file in the com.yourcorp.app.jdbc package you would
     * pass "/com/yourcorp/app/jdbc/Queries.properties" to this method.
     * @throws IOException if a file access error occurs
     * @throws IllegalArgumentException if the ClassLoader can't find a file at
     * the given path.
     * @throws java.util.InvalidPropertiesFormatException if the XML properties file is
     * invalid
     * @return Map of query names to SQL values
     * @see java.util.Properties
     */
    public synchronized Map<String, String> load(final String path) throws IOException {

        Map<String, String> queryMap = this.queries.get(path);

        if (queryMap == null) {
            queryMap = loadQueries(path);
            this.queries.put(path, queryMap);
        }

        return queryMap;
    }

    /**
     * Loads a set of named queries into a Map object.  This implementation
     * reads a properties file at the given path.  The properties file can be
     * in either line-oriented or XML format.  XML formatted properties files
     * must use a {@code .xml} file extension.
     *
     * @param path The path that the ClassLoader will use to find the file.
     * @throws IOException if a file access error occurs
     * @throws IllegalArgumentException if the ClassLoader can't find a file at
     * @return Map of query names to SQL values
     * the given path.
     * @throws java.util.InvalidPropertiesFormatException if the XML properties file is
     * invalid
     * @see java.util.Properties
     * @since 1.1
     */
    protected Map<String, String> loadQueries(final String path) throws IOException {
        // Findbugs flags getClass().getResource as a bad practice; maybe we should change the API?
        final Properties props;
        try (InputStream in = getClass().getResourceAsStream(path)) {

            if (in == null) {
                throw new IllegalArgumentException(path + " not found.");
            }
            props = new Properties();
            if (dotXml.matcher(path).matches()) {
                props.loadFromXML(in);
            } else {
                props.load(in);
            }
        }

        // Copy to HashMap for better performance

        @SuppressWarnings({"rawtypes", "unchecked" }) // load() always creates <String,String> entries
        final HashMap<String, String> hashMap = new HashMap(props);
        return hashMap;
    }

    /**
     * Removes the queries for the given path from the cache.
     * @param path The path that the queries were loaded from.
     */
    public synchronized void unload(final String path) {
        this.queries.remove(path);
    }

}
