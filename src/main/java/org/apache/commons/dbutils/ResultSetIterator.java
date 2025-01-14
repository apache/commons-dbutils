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
import java.sql.SQLException;
import java.util.Iterator;

/**
 * <p>
 * Wraps a {@code ResultSet} in an {@code Iterator&lt;Object[]&gt;}.  This is useful
 * when you want to present a non-database application layer with domain
 * neutral data.
 * </p>
 *
 * <p>
 * This implementation requires the {@code ResultSet.isLast()} method
 * to be implemented.
 * </p>
 */
public class ResultSetIterator implements Iterator<Object[]> {

    /**
     * Generates an {@code Iterable}, suitable for use in for-each loops.
     *
     * @param resultSet Wrap this {@code ResultSet} in an {@code Iterator}.
     * @return an {@code Iterable}, suitable for use in for-each loops.
     */
    public static Iterable<Object[]> iterable(final ResultSet resultSet) {
        return () -> new ResultSetIterator(resultSet);
    }

    /**
     * The wrapped {@code ResultSet}.
     */
    private final ResultSet resultSet;

    /**
     * The processor to use when converting a row into an Object[].
     */
    private final RowProcessor convert;

    /**
     * Constructor for ResultSetIterator.
     * @param resultSet Wrap this {@code ResultSet} in an {@code Iterator}.
     */
    public ResultSetIterator(final ResultSet resultSet) {
        this(resultSet, new BasicRowProcessor());
    }

    /**
     * Constructor for ResultSetIterator.
     * @param resultSet Wrap this {@code ResultSet} in an {@code Iterator}.
     * @param convert The processor to use when converting a row into an
     * {@code Object[]}.  Defaults to a
     * {@code BasicRowProcessor}.
     */
    public ResultSetIterator(final ResultSet resultSet, final RowProcessor convert) {
        this.resultSet = resultSet;
        this.convert = convert;
    }

    /**
     * Returns true if there are more rows in the ResultSet.
     * @return boolean {@code true} if there are more rows
     * @throws RuntimeException if an SQLException occurs.
     */
    @Override
    public boolean hasNext() {
        try {
            return !resultSet.isLast();
        } catch (final SQLException e) {
            rethrow(e);
            return false;
        }
    }

    /**
     * Returns the next row as an {@code Object[]}.
     * @return An {@code Object[]} with the same number of elements as
     * columns in the {@code ResultSet}.
     * @see java.util.Iterator#next()
     * @throws RuntimeException if an SQLException occurs.
     * @throws NoSuchElementException if there are no more rows in the {@code ResultSet}.
     */
    @Override
    public Object[] next() {
        try {
            if (!resultSet.next()) {
                throw new NoSuchElementException("No more rows in the ResultSet");
            }
            return this.convert.toArray(resultSet);
        } catch (final SQLException e) {
            rethrow(e);
            return null;
        }
    }

    /**
     * Deletes the current row from the {@code ResultSet}.
     * @see java.util.Iterator#remove()
     * @throws RuntimeException if an SQLException occurs.
     */
    @Override
    public void remove() {
        try {
            this.resultSet.deleteRow();
        } catch (final SQLException e) {
            rethrow(e);
        }
    }

    /**
     * Rethrow the SQLException as a RuntimeException.  This implementation
     * creates a new RuntimeException with the SQLException's error message.
     * @param e SQLException to rethrow
     * @since 1.1
     */
    protected void rethrow(final SQLException e) {
        throw new RuntimeException(e.getMessage());
    }

}
