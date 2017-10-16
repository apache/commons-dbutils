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

/**
 * This DbRuntimeException only used to wrap a SQLException to a Runtime type
 * Exception to avoid force user to catch SQLException. Usually used together
 * with an IOC/AOP tool like Spring, it will catch any runtime Exception and
 * roll back the transaction.
 *
 * @author Yong Zhu
 * @since 1.7.0
 */
public class DbRuntimeException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public DbRuntimeException() {
		super();
	}

	public DbRuntimeException(Throwable cause) {
		super(cause);
	}

	public DbRuntimeException(String msg) {
		super(msg);
	}

	public DbRuntimeException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public static void assertNotNull(Object object, String msg) {
		if (object == null)
			throw new DbRuntimeException(msg);
	}
}
