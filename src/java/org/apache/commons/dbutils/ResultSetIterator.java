/*
 * Copyright 2002-2004 The Apache Software Foundation
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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

/**
 * <p>
 * Wraps a <code>ResultSet</code> in an <code>Iterator</code>.  This is useful
 * when you want to present a non-database application layer with domain
 * neutral data.
 * </p>
 * 
 * <p>
 * This implementation requires the <code>ResultSet.isLast()</code> method
 * to be implemented.
 * </p>
 */
public class ResultSetIterator implements Iterator {

    /**
     * The wrapped <code>ResultSet</code>.
     */
    private ResultSet rs = null;
    
    /**
     * The processor to use when converting a row into an Object[].
     */
    private RowProcessor convert = new BasicRowProcessor();

    /**
     * Constructor for ResultSetIterator.
     * @param rs Wrap this <code>ResultSet</code> in an <code>Iterator</code>.
     */
    public ResultSetIterator(ResultSet rs) {
        this.rs = rs;
    }
    
    /**
     * Constructor for ResultSetIterator.
     * @param rs Wrap this <code>ResultSet</code> in an <code>Iterator</code>.
     * @param convert The processor to use when converting a row into an 
     * <code>Object[]</code>.  Defaults to a 
     * <code>BasicRowProcessor</code>.
     */
    public ResultSetIterator(ResultSet rs, RowProcessor convert) {
        this.rs = rs;
        this.convert = convert;
    }

    public boolean hasNext() {
        try {
            return !rs.isLast();
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Returns the next row as an <code>Object[]</code>.
     * @return An <code>Object[]</code> with the same number of elements as
     * columns in the <code>ResultSet</code>. 
     * @see java.util.Iterator#next()
     */
    public Object next() {
        try {
            rs.next();
            return this.convert.toArray(rs);
        } catch (SQLException e) {
            return null;
        }
    }

    /**
     * Deletes the current row from the <code>ResultSet</code>.
     * @see java.util.Iterator#remove()
     */
    public void remove() {
        try {
            this.rs.deleteRow();
        } catch (SQLException e) {
        }
    }

}
