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
package com.github.drinkjava2.dbutilspro;

import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * For logger output, to avoid logger jar version conflict, default use JDK log,
 * if found commons log, use it, if found Log4j use it..., by this way this
 * project has no dependency to any logger jar.
 * 
 * @author Yong Zhu
 * @since 1.7.0
 */
public class DbProLogger {
	private Object commonLogger;
	private Method commonLoggerInfoMethod;
	private Method commonLoggerWarnMethod;
	private Method commonLoggerErrorMethod;
	private Logger jdkLogger;
	private static boolean firstRun = true;
	private static boolean enableLog = true;
	public static DbProLogger INSTANCE = null;// NOSONAR

	static {
		INSTANCE = new DbProLogger(DbProLogger.class);
		firstRun = false;
	}

	public DbProLogger(Class<?> targetClass) {
		if (targetClass == null)
			throw new AssertionError("DbProLogger error: targetClass can not be null.");
		try {
			Class<?> logFactoryClass = Class.forName("org.apache.commons.logging.LogFactory");
			Method method = logFactoryClass.getMethod("getLog", Class.class);
			commonLogger = method.invoke(logFactoryClass, targetClass);
			commonLoggerInfoMethod = commonLogger.getClass().getMethod("info", Object.class);
			commonLoggerWarnMethod = commonLogger.getClass().getMethod("warn", Object.class);
			commonLoggerErrorMethod = commonLogger.getClass().getMethod("error", Object.class);
		} catch (Exception e) {
			// do nothing
		}

		if (commonLogger == null || commonLoggerWarnMethod == null) {
			if (firstRun)
				System.err.println("DbProLogger failed to load org.apache.commons.logging.LogFactory. Use JDK logger.");// NOSONAR
			jdkLogger = Logger.getLogger(targetClass.getName());// use JDK log
		} else if (firstRun)
			System.out.println("org.apache.commons.logging.LogFactory loaded, DbProLogger use it as logger.");// NOSONAR
	}

	/**
	 * Build a DbProLogger instance by given targetClass
	 * @param targetClass
	 * @return A DbProLogger instance
	 */
	public static DbProLogger getLog(Class<?> targetClass) {
		return new DbProLogger(targetClass);
	}

	public static void setEnableLog(boolean enablelog) {
		enableLog = enablelog;
	}

	public void info(String msg) {
		if (!enableLog)
			return;
		if (jdkLogger != null) {
			jdkLogger.log(Level.INFO, msg);
			return;
		}
		try {
			commonLoggerInfoMethod.invoke(commonLogger, msg);
		} catch (Exception e) {
			throw new AssertionError(e.getMessage());
		}
	}

	public void warn(String msg) {
		if (!enableLog)
			return;
		if (jdkLogger != null) {
			jdkLogger.log(Level.WARNING, msg);
			return;
		}
		try {
			commonLoggerWarnMethod.invoke(commonLogger, msg);
		} catch (Exception e) {
			throw new AssertionError(e.getMessage());
		}
	}

	public void error(String msg) {
		if (!enableLog)
			return;
		if (jdkLogger != null) {
			jdkLogger.log(Level.SEVERE, msg);
			return;
		}
		try {
			commonLoggerErrorMethod.invoke(commonLogger, msg);
		} catch (Exception e) {
			throw new AssertionError(e.getMessage());
		}
	}
}
