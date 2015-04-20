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

/**
 * Interface to define how implementations can interact with property handling when constructing a bean from a
 * {@link java.sql.ResultSet}.  PropertyHandlers do the work of coercing a value into the target type required.
 */
public interface PropertyHandler {

    /**
     * Test whether this <code>PropertyHandler</code> wants to handle setting <code>value</code> into something of type
     * <code>parameter</code>.
     *
     * @param parameter The type of the target parameter.
     * @param value The value to be set.
     * @return true is this property handler can/wants to handle this value; false otherwise.
     */
    boolean match(Class<?> parameter, Object value);

    /**
     * Do the work required to store <code>value</code> into something of type <code>parameter</code>. This method is
     * called only if this handler responded <code>true</code> after a call to {@link #match(Class, Object)}.
     *
     * @param parameter The type of the target parameter.
     * @param value The value to be set.
     * @return The converted value or the original value if something doesn't work out.
     */
    Object apply(Class<?> parameter, Object value);
}
