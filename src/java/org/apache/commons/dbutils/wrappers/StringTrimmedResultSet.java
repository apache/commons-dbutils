/* 
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//dbutils/src/java/org/apache/commons/dbutils/wrappers/StringTrimmedResultSet.java,v 1.1 2003/11/02 19:15:23 dgraham Exp $
 * $Revision: 1.1 $
 * $Date: 2003/11/02 19:15:23 $
 * 
 * ====================================================================
 * 
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.apache.commons.dbutils.wrappers;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.ResultSet;

import org.apache.commons.dbutils.ProxyFactory;

/**
 * Wraps a <code>ResultSet</code> to trim strings returned by the
 * <code>getString()</code> and <code>getObject()</code> methods.
 * 
 * <p>
 * Usage Example:
 * This example shows how to decorate ResultSets so processing continues as 
 * normal but all Strings are trimmed before being returned from the 
 * <code>ResultSet</code>.
 * </p>
 * 
 * <pre>
 * ResultSet rs = // somehow get a ResultSet;
 * 
 * // Substitute wrapped ResultSet with additional behavior for real ResultSet
 * rs = StringTrimmedResultSet.wrap(rs);
 * 
 * // Pass wrapped ResultSet to processor
 * List list = BasicRowProcessor.instance().toBeanList(rs);
 * </pre>
 *
 * @author <a href="stevencaswell@apache.org">Steven Caswell</a>
 * @author David Graham
 * @version $Id: StringTrimmedResultSet.java,v 1.1 2003/11/02 19:15:23 dgraham Exp $
 */
public class StringTrimmedResultSet implements InvocationHandler {

    /**
     * The factory to create proxies with.
     */
    private static final ProxyFactory factory = ProxyFactory.instance();

    /**
     * Wraps the <code>ResultSet</code> in an instance of this class.  This is
     * equivalent to:
     * <pre>
     * ProxyFactory.instance().createResultSet(new StringTrimmedResultSet(rs));
     * </pre>
     * 
     * @param rs The <code>ResultSet</code> to wrap.
     */
    public static ResultSet wrap(ResultSet rs) {
        return factory.createResultSet(new StringTrimmedResultSet(rs));
    }

    /**
     * The wrapped result. 
     */
    private final ResultSet rs;

    /**
     * Constructs a new instance of <code>StringTrimmedResultSet</code>
     * to wrap the specified <code>ResultSet</code>.
     */
    public StringTrimmedResultSet(ResultSet rs) {
        super();
        this.rs = rs;
    }

    /**
     * Intercept calls to the <code>getString()</code> and 
     * <code>getObject()</code> methods and trim any Strings before they're
     * returned.
     * 
     * @throws Throwable
     * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
     */
    public Object invoke(Object proxy, Method method, Object[] args)
        throws Throwable {

        Object result = method.invoke(this.rs, args);

        if (method.getName().equals("getObject")
            || method.getName().equals("getString")) {

            if (result instanceof String) {
                result = ((String) result).trim();
            }
        }

        return result;
    }

}
