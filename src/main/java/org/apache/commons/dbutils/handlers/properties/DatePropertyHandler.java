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

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.LocalDate;
import java.time.Instant;

import org.apache.commons.dbutils.PropertyHandler;

public class DatePropertyHandler implements PropertyHandler {
    public DatePropertyHandler() {
    }

    public boolean match(Class<?> parameter, Object value) {
        if (value instanceof java.util.Date) {
            String targetType = parameter.getName();
            if ("java.sql.Date".equals(targetType)) {
                return true;
            }

            if ("java.sql.Time".equals(targetType)) {
                return true;
            }

            if ("java.sql.Timestamp".equals(targetType)) {
                return true;
            }

            if ("java.time.LocalDate".equals(targetType)) {
                return true;
            }

            if("java.time.LocalDateTime".equals(targetType)){
                return true;
            }

            if ("java.time.Instant".equals(targetType)) {
                return true;
            }
        }

        return false;
    }

    public Object apply(Class<?> parameter, Object value) {
        String targetType = parameter.getName();
        if ("java.sql.Date".equals(targetType)) {
            value = new java.sql.Date(((java.util.Date) value).getTime());
        } else if ("java.sql.Time".equals(targetType)) {
            value = new java.sql.Time(((java.util.Date) value).getTime());
        } else if ("java.sql.Timestamp".equals(targetType)) {
            Timestamp tsValue = (Timestamp) value;
            int nanos = tsValue.getNanos();
            value = new java.sql.Timestamp(tsValue.getTime());
            ((Timestamp) value).setNanos(nanos);
        } else if ("java.time.LocalDate".equals(targetType)) {
            LocalDate localDate = ((java.sql.Date)value).toLocalDate();
            value = localDate;
        } else if("java.time.LocalDateTime".equals(targetType)){
            LocalDateTime localDateTime = LocalDateTime.ofInstant(((java.util.Date) value).toInstant(), ZoneId.systemDefault());
            value = localDateTime;
        } else if ("java.time.Instant".equals(targetType)) {
            Instant instant = ((java.util.Date) value).toInstant();
            value = instant;
        }

        return value;
    }
}


