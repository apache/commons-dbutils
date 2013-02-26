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

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

/**
 * The base class for QueryRunner & AsyncQueryRunner. This class is thread safe.
 *
 */
public abstract class AbstractQueryRunner {
    /**
     * The DataSource to retrieve connections from.
     */
    private final DataSource ds;

    /**
     * Default constructor, sets pmdKnownBroken to false and ds to null.
     */
    public AbstractQueryRunner() {
        ds = null;
    }

    /**
     * Constructor to provide a <code>DataSource</code>. Methods that do not
     * take a <code>Connection</code> parameter will retrieve connections from
     * this <code>DataSource</code>.
     *
     * @param ds The <code>DataSource</code> to retrieve connections from.
     */
    public AbstractQueryRunner(DataSource ds) {
        this.ds = ds;
    }

    /**
     * Returns the <code>DataSource</code> this runner is using.
     * <code>QueryRunner</code> methods always call this method to get the
     * <code>DataSource</code> so subclasses can provide specialized behavior.
     *
     * @return DataSource the runner is using
     */
    public DataSource getDataSource() {
        return this.ds;
    }

    /**
     * Factory method that creates and initializes a <code>Connection</code>
     * object. <code>QueryRunner</code> methods always call this method to
     * retrieve connections from its DataSource. Subclasses can override this
     * method to provide special <code>Connection</code> configuration if
     * needed. This implementation simply calls <code>ds.getConnection()</code>.
     *
     * @return An initialized <code>Connection</code>.
     * @throws SQLException if a database access error occurs
     */
    protected Connection prepareConnection() throws SQLException {
        if (this.getDataSource() == null) {
            throw new SQLException(
                    "QueryRunner requires a DataSource to be "
                    + "invoked in this way, or a Connection should be passed in");
        }
        return this.getDataSource().getConnection();
    }

    /**
     * Fill the <code>PreparedStatement</code> replacement parameters with the
     * given object's bean property values.
     *
     * @param stmt PreparedStatement to fill
     * @param bean a JavaBean object
     * @param properties an ordered array of properties; this gives the order to insert
     *                   values in the statement
     * @throws SQLException if a database access error occurs
     */
/*
    public void fillStatementWithBean(PreparedStatement stmt,
                                      Object bean,
                                      PropertyDescriptor[] properties) throws SQLException {
        Object[] params = new Object[properties.length];
        for (int i = 0; i < properties.length; i++) {
            PropertyDescriptor property = properties[i];
            Object value = null;
            Method method = property.getReadMethod();
            if (method == null) {
                throw new RuntimeException("No read method for bean property "
                        + bean.getClass() + " " + property.getName());
            }
            try {
                value = method.invoke(bean, new Object[0]);
            } catch (InvocationTargetException e) {
                throw new RuntimeException("Couldn't invoke method: " + method,
                        e);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException(
                        "Couldn't invoke method with 0 arguments: " + method, e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Couldn't invoke method: " + method,
                        e);
            }
            params[i] = value;
        }
        fillStatement(stmt, params);
    }
*/

    /**
     * Fill the <code>PreparedStatement</code> replacement parameters with the
     * given object's bean property values.
     *
     * @param stmt
     *            PreparedStatement to fill
     * @param bean
     *            A JavaBean object
     * @param propertyNames
     *            An ordered array of property names (these should match the
     *            getters/setters); this gives the order to insert values in the
     *            statement
     * @throws SQLException
     *             If a database access error occurs
     */
/*
    public void fillStatementWithBean(PreparedStatement stmt, Object bean,
            String... propertyNames) throws SQLException {
        PropertyDescriptor[] descriptors;
        try {
            descriptors = Introspector.getBeanInfo(bean.getClass())
            .getPropertyDescriptors();
        } catch (IntrospectionException e) {
            throw new RuntimeException("Couldn't introspect bean "
                    + bean.getClass().toString(), e);
        }
        PropertyDescriptor[] sorted = new PropertyDescriptor[propertyNames.length];
        for (int i = 0; i < propertyNames.length; i++) {
            String propertyName = propertyNames[i];
            if (propertyName == null) {
                throw new NullPointerException("propertyName can't be null: "
                        + i);
            }
            boolean found = false;
            for (int j = 0; j < descriptors.length; j++) {
                PropertyDescriptor descriptor = descriptors[j];
                if (propertyName.equals(descriptor.getName())) {
                    sorted[i] = descriptor;
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new RuntimeException("Couldn't find bean property: "
                        + bean.getClass() + " " + propertyName);
            }
        }
        fillStatementWithBean(stmt, bean, sorted);
    }
*/
    
    /**
     * Close a <code>Connection</code>. This implementation avoids closing if
     * null and does <strong>not</strong> suppress any exceptions. Subclasses
     * can override to provide special handling like logging.
     *
     * @param conn Connection to close
     * @throws SQLException if a database access error occurs
     */
    protected void close(Connection conn) throws SQLException {
        DbUtils.close(conn);
    }

}
