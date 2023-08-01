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

/**
 * Defines how to process columns when constructing a bean from a {@link ResultSet}. Instances do the work of retrieving data from a {@code ResultSet}.
 *
 * @param <T> The return type.
 */
public interface ColumnHandler<T> {

    /**
     * Retrieves the current row's column value from a {@link ResultSet} and stores it into an instance of {@code propType}. This method is only called if
     * {@link #match(Class)} returns true.
     *
     * @param resultSet   The source result set. This must be on the correct row.
     * @param columnIndex The position of the column to retrieve, a 1-based index.
     * @return The converted value or the original value if something doesn't work out.
     * @throws SQLException if the columnIndex is not valid; if a database access error occurs or this method is called on a closed result set
     */
    T apply(ResultSet resultSet, int columnIndex) throws SQLException;

    /**
     * Tests whether to handle a column targeted for a value type matching {@code propType}.
     *
     * @param propType The type of the target parameter.
     * @return true is this property handler handles this {@code propType}; false otherwise.
     */
    boolean match(Class<?> propType);
}
