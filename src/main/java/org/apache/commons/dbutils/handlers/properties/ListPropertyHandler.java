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
package org.apache.commons.dbutils.handlers.properties;

import org.apache.commons.dbutils.PropertyHandler;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * {@link PropertyHandler} for List . Will convert strings to List .
 */
public class ListPropertyHandler implements PropertyHandler {

    /**
     * Constructs a new instance.
     */
    public ListPropertyHandler() {
        // empty
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object apply(final Class<?> parameter, Type genericParameterType, final Object value) {
        if (!(genericParameterType instanceof ParameterizedType)) {
            throw new IllegalStateException("List must be generic");
        }
        if (value == null) {
            return null;
        }
        String strValue = (String) value;
        String[] arr = strValue.split(",");

        ParameterizedType pt = (ParameterizedType) genericParameterType;
        Type itemType = pt.getActualTypeArguments()[0];

        List<String> list = new ArrayList<>();
        for (String s : arr) {
            if (!s.isBlank()) {
                list.add(s);
            }
        }

        if (itemType == String.class) {
            return list;
        }

        if (itemType == Integer.class) {
            return list.stream().map(Integer::valueOf).collect(Collectors.toList());
        }
        if (itemType == Long.class) {
            return list.stream().map(Long::valueOf).collect(Collectors.toList());
        }
        if (itemType == Double.class) {
            return list.stream().map(Double::valueOf).collect(Collectors.toList());
        }
        if (itemType == Float.class) {
            return list.stream().map(Float::valueOf).collect(Collectors.toList());
        }
        if (itemType == BigDecimal.class) {
            return list.stream().map(BigDecimal::new).collect(Collectors.toList());
        }


        throw new IllegalStateException("not support List " + itemType);
    }

    @Override
    public boolean match(final Class<?> parameter, Type genericParameterType, final Object value) {
        if (!(value instanceof String)) {
            return false;
        }

        if (!List.class.isAssignableFrom(parameter)) {
            return false;
        }

        if (!(genericParameterType instanceof ParameterizedType)) {
            return false;
        }

        return true;
    }


}
