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
package org.apache.commons.dbutils.handlers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.RowProcessor;

/**
 * <code>ResultSetHandler</code> implementation that converts a
 * <code>ResultSet</code> into a <code>List</code> of beans. This class is 
 * thread safe.
 * 
 * @see ResultSetHandler
 */
public class BeanListHandler implements ResultSetHandler {

    /**
     * The Class of beans produced by this handler.
     */
    private Class type = null;

    /**
     * The RowProcessor implementation to use when converting rows 
     * into beans.
     */
    private RowProcessor convert = ArrayHandler.ROW_PROCESSOR;

    /** 
     * Creates a new instance of BeanListHandler.
     * 
     * @param type The Class that objects returned from <code>handle()</code>
     * are created from.
     */
    public BeanListHandler(Class type) {
        this.type = type;
    }

    /** 
     * Creates a new instance of BeanListHandler.
     * 
     * @param type The Class that objects returned from <code>handle()</code>
     * are created from.
     * @param convert The <code>RowProcessor</code> implementation 
     * to use when converting rows into beans.
     */
    public BeanListHandler(Class type, RowProcessor convert) {
        this.type = type;
        this.convert = convert;
    }

    /**
     * Convert the <code>ResultSet</code> rows into a <code>List</code> of 
     * beans with the <code>Class</code> given in the constructor.
     * 
     * @return A <code>List</code> of beans (one for each row), never 
     * <code>null</code>.
     * 
     * @throws SQLException
     * @see org.apache.commons.dbutils.ResultSetHandler#handle(java.sql.ResultSet)
     */
    public Object handle(ResultSet rs) throws SQLException {
        return this.convert.toBeanList(rs, type);
    }

}
