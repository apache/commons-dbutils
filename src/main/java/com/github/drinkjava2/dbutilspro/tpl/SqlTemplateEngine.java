/* 
 * Copyright (C) 2016 Yong Zhu.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.drinkjava2.dbutilspro.tpl;

import java.util.Map;
import java.util.Set;

import com.github.drinkjava2.dbutilspro.inline.SqlAndParams;

/**
 * A SqlTemplateEngine render a SQL Template String and a Map<String, Object>
 * into a {@link SqlAndParams} instance
 * 
 * @author Yong Zhu
 * @since 1.7.0
 */
public interface SqlTemplateEngine {

	/**
	 * Render a SQL Template String and a Map<String, Object> instance into a
	 * {@link SqlAndParams} instance
	 * 
	 * @param sqlTemplate
	 *            A SQL template String.
	 * @param paramMap
	 *            A Map instance, key is String type, value is Object type
	 * @param directReplaceNamesSet
	 *            Optional, A Set includes key names in Template should only use
	 *            replace() or replace0() method to put real values;
	 * 
	 *            <pre>
	 *            For example: 
	 *            Template "delete from ${tb}", use replace("tb","users") method,   will get "delete from users"
	 *            Template "delete from #{tb}", use replace("tb","users") method,   will cause an Exception
	 *            Template "delete from ${tb}", use bind("tb","users") method,      will cause an Exception
	 *            Template "delete from #{tb}", use bind("tb","users") method,      will get "delete from ?"
	 *            </pre>
	 * 
	 *            This design is to avoid typing mistake cause a SQL injection
	 *            security leak, when programmer use replace() or replace0()
	 *            method, he will aware this is a String direct replace method,
	 *            not a SQL parameter, SQL parameter always use bind() or
	 *            bind0() method.
	 * 
	 * @return SqlAndParams instance
	 */
	public SqlAndParams render(String sqlTemplate, Map<String, Object> paramMap, Set<String> directReplaceNamesSet);

}
