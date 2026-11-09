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

import java.beans.PropertyDescriptor;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;

/**
 * Provides generous name matching (e.g. underscore-aware) from DB
 * columns to Java Bean properties.
 *
 * @since 1.6
 */
public class GenerousBeanProcessor extends BeanProcessor {

    /**
     * Default constructor.
     */
    public GenerousBeanProcessor() {
    }

    @Override
    protected int[] mapColumnsToProperties(final ResultSetMetaData rsmd,
            final PropertyDescriptor[] props) throws SQLException {

        final int cols = rsmd.getColumnCount();
        final int[] columnToProperty = new int[cols + 1];
        Arrays.fill(columnToProperty, PROPERTY_NOT_FOUND);

        for (int col = 1; col <= cols; col++) {
            String columnName = rsmd.getColumnLabel(col);

            if (columnName == null || 0 == columnName.length()) {
                columnName = rsmd.getColumnName(col);
            }

            final String generousColumnName = columnName
                    .replace("_", "")   // more idiomatic to Java
                    .replace(" ", "");  // can't have spaces in property names

            // a @Column annotation is an explicit mark, so it is tried before the generous name
            columnToProperty[col] = findProperty(props, columnName, true);
            if (columnToProperty[col] == PROPERTY_NOT_FOUND) {
                columnToProperty[col] = findProperty(props, generousColumnName, false);
            }
        }

        return columnToProperty;
    }

    /**
     * Finds the property matching a column name.
     *
     * @param props The properties of the bean.
     * @param columnName The column name to match.
     * @param exact Whether the annotated column name rather than the generous one is compared.
     * @return The index of the matching property, or {@link #PROPERTY_NOT_FOUND}.
     */
    private int findProperty(final PropertyDescriptor[] props, final String columnName, final boolean exact) {
        for (int i = 0; i < props.length; i++) {
            final PropertyDescriptor prop = props[i];
            final String name = exact ? getPropertyColumnName(prop) : prop.getName();
            if (columnName.equalsIgnoreCase(name)) {
                return i;
            }
        }
        return PROPERTY_NOT_FOUND;
    }
}
