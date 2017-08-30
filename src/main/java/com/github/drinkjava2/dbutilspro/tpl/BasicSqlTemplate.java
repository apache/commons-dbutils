/*
 * Copyright (C) 2016 Yong Zhu.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.github.drinkjava2.dbutilspro.tpl;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.drinkjava2.dbutilspro.inline.SqlAndParams;

/**
 * BasicSqlTemplate is a simple implementation of SqlTemplateSupport. It
 * translate a SQL template and parameter Map into a SqlAndParams instance. Use
 * InlineSupport.setGlobalSqlTemplateEngine() method can set to use an other
 * global template engine if it implemented SqlTemplateSupport interface.
 * 
 * @author Yong Zhu
 * @since 1.7.0
 */
public class BasicSqlTemplate implements SqlTemplateEngine {
	private String startDelimiter;
	private String endDelimiter;

	private static final String DIRECT_REPLACE_START_DELIMITER = "${";
	private static final String DIRECT_REPLACE_END_DELIMITER = "}";
	

	/** A lazy initialisation singleton pattern */
	private static class InnerBasicSqlTemplate {
		private InnerBasicSqlTemplate() {
		}

		private static final BasicSqlTemplate INSTANCE = new BasicSqlTemplate();
	}

	/** @return A singleton instance of BasicSqlTemplate */
	public static final BasicSqlTemplate instance() {
		return InnerBasicSqlTemplate.INSTANCE;
	}

	/**
	 * Build a BasicSqlTemplate instance, default use #{} as delimiter
	 */
	public BasicSqlTemplate() {
		this.startDelimiter = "#{";
		this.endDelimiter = "}";
	}

	/**
	 * Build a BasicSqlTemplate instance by given startDelimiter and endDelimiter,
	 * startDelimiter should be 1 or 2 characters and endDelimiter should be 1
	 * character
	 * 
	 * @param startDelimiter The start delimiter
	 * @param endDelimiter The end delimiter
	 */
	public BasicSqlTemplate(String startDelimiter, String endDelimiter) {
		if (isEmpty(startDelimiter) || isEmpty(endDelimiter) || startDelimiter.length() > 2
				|| endDelimiter.length() != 1)
			throw new BasicSqlTemplateException(
					"BasicSqlTemplate only support startDelimiter has 1 or 2 characters and endDelimiter has 1 character");
		this.startDelimiter = startDelimiter;
		this.endDelimiter = endDelimiter;
	}

	@Override
	public SqlAndParams render(String sqlTemplate, Map<String, Object> paramMap, Set<String> directReplaceNamesSet) {
		return doRender(sqlTemplate, paramMap, startDelimiter, endDelimiter, directReplaceNamesSet);
	}

	/**
	 * Render a template with
	 * @param template A SQL Template String
	 * @param paramMap A Map stored SQL parameters
	 * @param startDelimiter Start Delimiter of SQL Template
	 * @param endDelimiter End Delimiter of SQL Template
	 * @return A SqlAndParams instance
	 */
	protected static SqlAndParams doRender(String template, Map<String, Object> paramMap, String startDelimiter,
			String endDelimiter, Set<String> directReplaceNamesSet) {
		if (template == null)
			throw new NullPointerException("Template can not be null");
		StringBuilder sql = new StringBuilder();
		StringBuilder keyNameSB = new StringBuilder();
		List<Object> paramList = new ArrayList<Object>();
		char[] chars = ("   " + template + "   ").toCharArray();

		int lg = startDelimiter.length();
		char start1 = startDelimiter.charAt(0);
		char start2 = '\u0000';
		if (lg == 2)
			start2 = startDelimiter.charAt(1);
		char e = endDelimiter.charAt(0);

		int drlg = DIRECT_REPLACE_START_DELIMITER.length();
		char drst1 = DIRECT_REPLACE_START_DELIMITER.charAt(0);
		char drst2 = '\u0000';
		if (drlg == 2)
			drst2 = DIRECT_REPLACE_START_DELIMITER.charAt(1);
		char eDirect = DIRECT_REPLACE_END_DELIMITER.charAt(0);

		// - - # { - - - } - - - $ { - - - } - -
		// 0 0 1 1 2 2 2 3 0 0 0 1 1 2 2 2 3 0 0
		// - - - - - - - - - - - D D D D D - - -
		int status = 0; // 0:normal 1:start-delimiter 2:inside 3: end-delimiter
		boolean directRep = false; // direct replace tag
		for (int i = 3; i < chars.length - 2; i++) {
			char c = chars[i];
			char c1 = chars[i + 1];
			char cm1 = chars[i - 1];
			char cm2 = chars[i - 2];
			if (status == 0 && ((lg == 1 && c == start1) || (lg == 2 && c == start1 && c1 == start2))) {
				status = 1;
				keyNameSB.setLength(0);
				directRep = false;
			} else if (status == 0 && ((drlg == 1 && c == drst1) || (drlg == 2 && c == drst1 && c1 == drst2))) {
				status = 1;
				keyNameSB.setLength(0);
				directRep = true;
			} else if (status == 1 && ((lg == 1 && cm1 == start1) || (lg == 2 && (cm2 == start1 && cm1 == start2)))) {
				status = 2;
			} else if (status == 1 && ((drlg == 1 && cm1 == drst1) || (drlg == 2 && (cm2 == drst1 && cm1 == drst2)))) {
				status = 2;
			} else if (status == 2 && (((c == e) && !directRep) || ((c == eDirect) && directRep))) {
				status = 3;
				if (keyNameSB.length() == 0)
					throwEX("Empty parameter name '" + startDelimiter + endDelimiter + "' found in template: "
							+ template);
				String key = keyNameSB.toString();
				if (key.indexOf('.') >= 0) {// JavaBean
					String beanName = substringBefore(key, ".");
					String propertyName = substringAfter(key, ".");
					if (isEmpty(beanName) || isEmpty(propertyName))
						throwEX("illegal parameter name '" + key + "' found in template: " + template);
					boolean directReplaceType = isDirectReplaceTypeParameter(template, paramMap, directReplaceNamesSet,
							directRep, beanName);

					boolean hasValue = paramMap.containsKey(beanName);
					if (!hasValue)
						throwEX("Not found bean '" + beanName + "' when render template: " + template);

					Object bean = paramMap.get(beanName);
					PropertyDescriptor pd = null;
					try {
						pd = new PropertyDescriptor(propertyName, bean.getClass());
					} catch (IntrospectionException e1) {
						throwEX("IntrospectionException happen when get bean property '" + key + "' in template: "
								+ template, e1);
					}
					Method method = pd.getReadMethod();
					Object beanProperty = null;
					try {
						beanProperty = method.invoke(bean);
					} catch (Exception e1) {
						throwEX("Exception happen when read bean property '" + key + "' in template: " + template, e1);
					}
					if (directReplaceType) {
						sql.append(beanProperty);
					} else {
						sql.append("?");
						paramList.add(beanProperty);
					}
				} else {
					boolean directReplaceType = isDirectReplaceTypeParameter(template, paramMap, directReplaceNamesSet,
							directRep, key);
					boolean hasValue = paramMap.containsKey(key);
					if (!hasValue)
						throwEX("No parameter found for '" + key + "' in template: " + template);
					if (directReplaceType) {
						sql.append(paramMap.get(key));
					} else {
						sql.append("?");
						paramList.add(paramMap.get(key));
					}
				}
				keyNameSB.setLength(0);
			} else if (status == 3 && cm1 == e) {
				status = 0;
			}
			if (status == 0)
				sql.append(c);
			else if (status == 2)
				keyNameSB.append(c);
		}
		if (status != 0)
			throwEX("Missing end delimiter '" + endDelimiter + "' in template: " + template);
		SqlAndParams sp = new SqlAndParams();
		sql.setLength(sql.length() - 1);
		sp.setSql(sql.toString());
		sp.setParams(paramList.toArray(new Object[paramList.size()]));
		return sp;
	}

	private static boolean isDirectReplaceTypeParameter(String template, Map<String, Object> paramMap,
			Set<String> directReplaceNamesSet, boolean directRep, String beanName) {
		boolean directReplaceType = isDirectReplaceType(beanName, paramMap, directReplaceNamesSet);
		if (directReplaceType && !directRep)
			throwEX("'" + beanName
					+ "' is a SQL parameter, should use put() or put0() method to set SQL parameter, in template: "
					+ template);
		if (!directReplaceType && directRep)
			throwEX("'" + beanName
					+ "' is a direct-replace type parameter, should use replace() or replace0() method to put parameter, in template: "
					+ template);
		return directReplaceType;
	}

	private static boolean isDirectReplaceType(String keyName, Map<String, Object> paramMap,
			Set<String> directReplaceNamesSet) {
		if (directReplaceNamesSet == null)
			return false;
		if (directReplaceNamesSet.contains(keyName)) {
			if (!paramMap.containsKey(keyName))
				throwEX("'" + keyName + "' is indicated as a direct replace parameter but can not in parameter Map");
			return true;
		}
		return false;
	}

	private static void throwEX(String message, Exception... cause) {
		if (cause != null && cause.length > 0)
			throw new BasicSqlTemplateException(message, cause[0]);
		else
			throw new BasicSqlTemplateException(message);
	}

	// Below 3 methods are copied from org.apache.commons.lang3.StringUtils
	// -----------------------------------------------------------------------
	/**
	 * <p>
	 * Checks if a CharSequence is empty ("") or null.
	 * </p>
	 *
	 * <pre>
	 * StringUtils.isEmpty(null)      = true
	 * StringUtils.isEmpty("")        = true
	 * StringUtils.isEmpty(" ")       = false
	 * StringUtils.isEmpty("bob")     = false
	 * StringUtils.isEmpty("  bob  ") = false
	 * </pre>
	 *
	 * <p>
	 * NOTE: This method changed in Lang version 2.0. It no longer trims the
	 * CharSequence. That functionality is available in isBlank().
	 * </p>
	 *
	 * @param cs the CharSequence to check, may be null
	 * @return {@code true} if the CharSequence is empty or null
	 */
	public static boolean isEmpty(final CharSequence cs) {
		return cs == null || cs.length() == 0;
	}

	// -----------------------------------------------------------------------
	/**
	 * <p>
	 * Gets the substring before the first occurrence of a separator. The separator
	 * is not returned.
	 * </p>
	 *
	 * <p>
	 * A {@code null} string input will return {@code null}. An empty ("") string
	 * input will return the empty string. A {@code null} separator will return the
	 * input string.
	 * </p>
	 *
	 * <p>
	 * If nothing is found, the string input is returned.
	 * </p>
	 *
	 * <pre>
	 * StringUtils.substringBefore(null, *)      = null
	 * StringUtils.substringBefore("", *)        = ""
	 * StringUtils.substringBefore("abc", "a")   = ""
	 * StringUtils.substringBefore("abcba", "b") = "a"
	 * StringUtils.substringBefore("abc", "c")   = "ab"
	 * StringUtils.substringBefore("abc", "d")   = "abc"
	 * StringUtils.substringBefore("abc", "")    = ""
	 * StringUtils.substringBefore("abc", null)  = "abc"
	 * </pre>
	 *
	 * @param str the String to get a substring from, may be null
	 * @param separator the String to search for, may be null
	 * @return the substring before the first occurrence of the separator,
	 *         {@code null} if null String input
	 */
	public static String substringBefore(final String str, final String separator) {
		if (isEmpty(str) || separator == null) {
			return str;
		}
		if (separator.isEmpty()) {
			return "";
		}
		final int pos = str.indexOf(separator);
		if (pos == -1) {
			return str;
		}
		return str.substring(0, pos);
	}

	/**
	 * <p>
	 * Gets the substring after the first occurrence of a separator. The separator
	 * is not returned.
	 * </p>
	 *
	 * <p>
	 * A {@code null} string input will return {@code null}. An empty ("") string
	 * input will return the empty string. A {@code null} separator will return the
	 * empty string if the input string is not {@code null}.
	 * </p>
	 *
	 * <p>
	 * If nothing is found, the empty string is returned.
	 * </p>
	 *
	 * <pre>
	 * StringUtils.substringAfter(null, *)      = null
	 * StringUtils.substringAfter("", *)        = ""
	 * StringUtils.substringAfter(*, null)      = ""
	 * StringUtils.substringAfter("abc", "a")   = "bc"
	 * StringUtils.substringAfter("abcba", "b") = "cba"
	 * StringUtils.substringAfter("abc", "c")   = ""
	 * StringUtils.substringAfter("abc", "d")   = ""
	 * StringUtils.substringAfter("abc", "")    = "abc"
	 * </pre>
	 *
	 * @param str the String to get a substring from, may be null
	 * @param separator the String to search for, may be null
	 * @return the substring after the first occurrence of the separator,
	 *         {@code null} if null String input
	 */
	public static String substringAfter(final String str, final String separator) {
		if (isEmpty(str)) {
			return str;
		}
		if (separator == null) {
			return "";
		}
		final int pos = str.indexOf(separator);
		if (pos == -1) {
			return "";
		}
		return str.substring(pos + separator.length());
	}

	public static class BasicSqlTemplateException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public BasicSqlTemplateException(String msg) {
			super(msg);
		}

		public BasicSqlTemplateException(String msg, Throwable cause) {
			super(msg, cause);
		}
	}
}
