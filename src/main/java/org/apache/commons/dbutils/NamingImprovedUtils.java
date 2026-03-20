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

class NamingImprovedUtils {

    private static final char CHAR_UNDERLINE = '_';

    public static String toCamelCase(String name) {
        if (name == null || name.isEmpty()) {
            return name;
        }
        if(!name.contains(String.valueOf(CHAR_UNDERLINE))){
            return name;
        }


        StringBuilder result = new StringBuilder(name.length());
        boolean toUpperCase = false;

        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (c == CHAR_UNDERLINE) {
                toUpperCase = true;
            } else {
                if (toUpperCase) {
                    result.append(Character.toUpperCase(c));
                    toUpperCase = false;
                } else {
                    result.append(c);
                }
            }
        }

        return result.toString();
    }


}
