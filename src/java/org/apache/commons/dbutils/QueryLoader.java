/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//dbutils/src/java/org/apache/commons/dbutils/QueryLoader.java,v 1.2 2004/01/11 22:30:38 dgraham Exp $
 * $Revision: 1.2 $
 * $Date: 2004/01/11 22:30:38 $
 * 
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2004 The Apache Software Foundation.  All rights
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

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * <code>QueryLoader</code> loads properties files filled with query name to 
 * SQL mappings.  It acts as a registry for sets of queries so that multiple 
 * copies of the same queries aren't loaded into memory.  This class is thread
 * safe.
 */
public class QueryLoader {

    /**
     * The Singleton instance of this class.
     */
    private static final QueryLoader instance = new QueryLoader();

    /**
     * Return an instance of this class.
     * @return The Singleton instance.
     */
    public static QueryLoader instance() {
        return instance;
    }

    /**
     * Maps query set names to Maps of their queries.
     */
    private Map queries = new HashMap();

    /**
     * QueryLoader constructor.
     */
    protected QueryLoader() {
        super();
    }

    /**
     * Loads a Map of query names to SQL values.  The Maps are cached so a 
     * subsequent request to load queries from the same path will return
     * the cached Map.
     * 
     * @param path The path that the ClassLoader will use to find the file. 
     * This is <strong>not</strong> a file system path.  If you had a jarred
     * Queries.properties file in the com.yourcorp.app.jdbc package you would 
     * pass "/com/yourcorp/app/jdbc/Queries.properties" to this method.
     * @throws IOException
     * @throws IllegalArgumentException if the ClassLoader can't find a file at
     * the given path.
     */
    public synchronized Map load(String path) throws IOException {

        Map queryMap = (Map) this.queries.get(path);

        if (queryMap == null) {
            queryMap = this.loadQueries(path);
            this.queries.put(path, queryMap);
        }

        return queryMap;
    }

    /**
     * Loads a properties file into a Map object.
     * @param path The path that the ClassLoader will use to find the file.
     * @throws IOException
     */
    private Map loadQueries(String path) throws IOException {
        InputStream in = QueryLoader.class.getResourceAsStream(path);

        if (in == null) {
            throw new IllegalArgumentException(path + " not found.");
        }

        Properties props = new Properties();
        props.load(in);

        // Copy to HashMap for better performance
        return new HashMap(props);
    }
    
    /**
     * Removes the queries for the given path from the cache.
     * @param path The path that the queries were loaded from.
     */
    public synchronized void unload(String path){
        this.queries.remove(path);
    }

}
