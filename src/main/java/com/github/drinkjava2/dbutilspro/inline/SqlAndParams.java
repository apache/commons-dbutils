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
package com.github.drinkjava2.dbutilspro.inline;

import java.util.Arrays;

/**
 * SqlAndParams is a POJO used for store a SQL String and a parameter array
 * 
 * @author Yong Zhu
 * @since 1.7.0
 */
public class SqlAndParams {
	private String sql;

	/**
	 * Sql parameters
	 */
	private Object[] params;

	public SqlAndParams() {
		// default Constructor
	}

	public SqlAndParams(String sql, Object[] parameters) {
		this.sql = sql;
		this.params = parameters;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public Object[] getParams() {
		if (params == null)
			return new Object[] {};
		return params;
	}

	public void setParams(Object[] params) {
		this.params = params;
	}

	public String getDebugInfo() {
		return new StringBuffer("SQL: ").append(this.getSql()).append("\nParameters: ")
				.append(Arrays.deepToString(this.getParams())).append("\n").toString();
	}
}
