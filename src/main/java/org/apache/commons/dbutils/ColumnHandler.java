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
 * Interface to define how implementations can interact with column handling when constructing a bean from a
 * {@link java.sql.ResultSet}.  ColumnHandlers do the work of retrieving data correctly from the {@code ResultSet}.
 */
public interface ColumnHandler {
    /**
     * Test whether this {@code ColumnHandler} wants to handle a column targeted for a value type matching
     * {@code propType}.
     *
     * @param propType The type of the target parameter.
     * @return true is this property handler can/wants to handle this value; false otherwise.
     */
    boolean match(Class<?> propType);

    /**
     * Do the work required to retrieve and store a column from {@code ResultSet} into something of type
     * {@code propType}. This method is called only if this handler responded {@code true} after a call to
     * {@link #match(Class)}.
     *
     * @param rs The result set to get data from. This should be moved to the correct row already.
     * @param columnIndex The position of the column to retrieve.
     * @return The converted value or the original value if something doesn't work out.
     * @throws SQLException if the columnIndex is not valid; if a database access error occurs or this method is
     *                called on a closed result set
     */
    Object apply(ResultSet rs, int columnIndex) throws SQLException;
}
