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
package org.apache.commons.dbutils.handlers.columns;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbutils.ColumnHandler;

/**
 * A {@link Byte} column handler.
 */
public class ByteColumnHandler implements ColumnHandler<Byte> {

    /**
     * Constructs a new instance.
     */
    public ByteColumnHandler() {
        // empty
    }

    @Override
    public Byte apply(final ResultSet resultSet, final int columnIndex) throws SQLException {
        return Byte.valueOf(resultSet.getByte(columnIndex));
    }

    @Override
    public boolean match(final Class<?> propType) {
        return propType.equals(Byte.TYPE) || propType.equals(Byte.class);
    }
}
