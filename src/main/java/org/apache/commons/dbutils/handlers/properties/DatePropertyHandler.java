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
package org.apache.commons.dbutils.handlers.properties;

import org.apache.commons.dbutils.PropertyHandler;

import java.sql.Timestamp;
import java.util.Date;

/**
 * {@link PropertyHandler} for date fields. Will convert {@link java.sql.Date}, {@link java.sql.Time}, and
 * {@link java.sql.Timestamp} from SQL types to java types.
 */
public class DatePropertyHandler implements PropertyHandler {
    @Override
    public boolean match(final Class<?> parameter, final Object value) {
        if (value instanceof Date) {
            final String targetType = parameter.getName();
            if ("java.sql.Date".equals(targetType)) {
                return true;
            } else
            if ("java.sql.Time".equals(targetType)) {
                return true;
            } else
            if ("java.sql.Timestamp".equals(targetType)
                    && !Timestamp.class.isInstance(value)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Object apply(final Class<?> parameter, Object value) {
        final String targetType = parameter.getName();
        final Date dateValue = (Date) value;
        final long time = dateValue.getTime();

        if ("java.sql.Date".equals(targetType)) {
            value = new java.sql.Date(time);
        } else
        if ("java.sql.Time".equals(targetType)) {
            value = new java.sql.Time(time);
        } else
        if ("java.sql.Timestamp".equals(targetType)) {
            value = new Timestamp(time);
        }

        return value;
    }
}
