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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * {@code RowProcessor} implementations convert
 * {@code ResultSet} rows into various other objects.  Implementations
 * can extend {@code BasicRowProcessor} to protect themselves
 * from changes to this interface.
 *
 * @see BasicRowProcessor
 */
public interface RowProcessor {

    /**
     * Create an {@code Object[]} from the column values in one
     * {@code ResultSet} row.  The {@code ResultSet} should be
     * positioned on a valid row before passing it to this method.
     * Implementations of this method must not alter the row position of
     * the {@code ResultSet}.
     *
     * @param resultSet ResultSet that supplies the array data
     * @throws SQLException if a database access error occurs
     * @return the newly created array
     */
    Object[] toArray(ResultSet resultSet) throws SQLException;

    /**
     * Create a JavaBean from the column values in one {@code ResultSet}
     * row.  The {@code ResultSet} should be positioned on a valid row before
     * passing it to this method.  Implementations of this method must not
     * alter the row position of the {@code ResultSet}.
     * @param <T> The type of bean to create
     * @param resultSet ResultSet that supplies the bean data
     * @param type Class from which to create the bean instance
     * @throws SQLException if a database access error occurs
     * @return the newly created bean
     */
    <T> T toBean(ResultSet resultSet, Class<? extends T> type) throws SQLException;

    /**
     * Create a {@code List} of JavaBeans from the column values in all
     * {@code ResultSet} rows.  {@code ResultSet.next()} should
     * <strong>not</strong> be called before passing it to this method.
     * @param <T> The type of bean to create
     * @param resultSet ResultSet that supplies the bean data
     * @param type Class from which to create the bean instance
     * @throws SQLException if a database access error occurs
     * @return A {@code List} of beans with the given type in the order
     * they were returned by the {@code ResultSet}.
     */
    <T> List<T> toBeanList(ResultSet resultSet, Class<? extends T> type) throws SQLException;

    /**
     * Create a {@code Map} from the column values in one
     * {@code ResultSet} row.  The {@code ResultSet} should be
     * positioned on a valid row before
     * passing it to this method.  Implementations of this method must not
     * alter the row position of the {@code ResultSet}.
     *
     * @param resultSet ResultSet that supplies the map data
     * @throws SQLException if a database access error occurs
     * @return the newly created Map
     */
    Map<String, Object> toMap(ResultSet resultSet) throws SQLException;

}
