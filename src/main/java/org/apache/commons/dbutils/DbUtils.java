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

import static java.sql.DriverManager.registerDriver;

import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Statement;
import java.util.logging.Logger;
import java.util.Properties;

/**
 * A collection of JDBC helper methods.  This class is thread safe.
 */
public final class DbUtils {

    /**
     * Default constructor.
     *
     * Utility classes should not have a public or default constructor,
     * but this one preserves retro-compatibility.
     *
     * @since 1.4
     */
    public DbUtils() {
        // do nothing
    }

    /**
     * Close a {@code Connection}, avoid closing if null.
     *
     * @param conn Connection to close.
     * @throws SQLException if a database access error occurs
     */
    public static void close(final Connection conn) throws SQLException {
        if (conn != null) {
            conn.close();
        }
    }

    /**
     * Close a {@code ResultSet}, avoid closing if null.
     *
     * @param rs ResultSet to close.
     * @throws SQLException if a database access error occurs
     */
    public static void close(final ResultSet rs) throws SQLException {
        if (rs != null) {
            rs.close();
        }
    }

    /**
     * Close a {@code Statement}, avoid closing if null.
     *
     * @param stmt Statement to close.
     * @throws SQLException if a database access error occurs
     */
    public static void close(final Statement stmt) throws SQLException {
        if (stmt != null) {
            stmt.close();
        }
    }

    /**
     * Close a {@code Connection}, avoid closing if null and hide
     * any SQLExceptions that occur.
     *
     * @param conn Connection to close.
     */
    public static void closeQuietly(final Connection conn) {
        try {
            close(conn);
        } catch (final SQLException e) { // NOPMD
            // quiet
        }
    }

    /**
     * Close a {@code Connection}, {@code Statement} and
     * {@code ResultSet}.  Avoid closing if null and hide any
     * SQLExceptions that occur.
     *
     * @param conn Connection to close.
     * @param stmt Statement to close.
     * @param rs ResultSet to close.
     */
    public static void closeQuietly(final Connection conn, final Statement stmt,
            final ResultSet rs) {

        try {
            closeQuietly(rs);
        } finally {
            try {
                closeQuietly(stmt);
            } finally {
                closeQuietly(conn);
            }
        }

    }

    /**
     * Close a {@code ResultSet}, avoid closing if null and hide any
     * SQLExceptions that occur.
     *
     * @param rs ResultSet to close.
     */
    public static void closeQuietly(final ResultSet rs) {
        try {
            close(rs);
        } catch (final SQLException e) { // NOPMD
            // quiet
        }
    }

    /**
     * Close a {@code Statement}, avoid closing if null and hide
     * any SQLExceptions that occur.
     *
     * @param stmt Statement to close.
     */
    public static void closeQuietly(final Statement stmt) {
        try {
            close(stmt);
        } catch (final SQLException e) { // NOPMD
            // quiet
        }
    }

    /**
     * Commits a {@code Connection} then closes it, avoid closing if null.
     *
     * @param conn Connection to close.
     * @throws SQLException if a database access error occurs
     */
    public static void commitAndClose(final Connection conn) throws SQLException {
        if (conn != null) {
            try {
                conn.commit();
            } finally {
                conn.close();
            }
        }
    }

    /**
     * Commits a {@code Connection} then closes it, avoid closing if null
     * and hide any SQLExceptions that occur.
     *
     * @param conn Connection to close.
     */
    public static void commitAndCloseQuietly(final Connection conn) {
        try {
            commitAndClose(conn);
        } catch (final SQLException e) { // NOPMD
            // quiet
        }
    }

    /**
     * Loads and registers a database driver class.
     * If this succeeds, it returns true, else it returns false.
     *
     * @param driverClassName of driver to load
     * @return boolean {@code true} if the driver was found, otherwise {@code false}
     */
    public static boolean loadDriver(final String driverClassName) {
        return loadDriver(DbUtils.class.getClassLoader(), driverClassName);
    }

    /**
     * Loads and registers a database driver class.
     * If this succeeds, it returns true, else it returns false.
     *
     * @param classLoader the class loader used to load the driver class
     * @param driverClassName of driver to load
     * @return boolean {@code true} if the driver was found, otherwise {@code false}
     * @since 1.4
     */
    public static boolean loadDriver(final ClassLoader classLoader, final String driverClassName) {
        try {
            final Class<?> loadedClass = classLoader.loadClass(driverClassName);

            if (!Driver.class.isAssignableFrom(loadedClass)) {
                return false;
            }

            @SuppressWarnings("unchecked") // guarded by previous check
            final
            Class<Driver> driverClass = (Class<Driver>) loadedClass;
            final Constructor<Driver> driverConstructor = driverClass.getConstructor();

            // make Constructor accessible if it is private
            @SuppressWarnings("deprecation")
            // TODO This is deprecated in Java9 and canAccess() should be used. Adding suppression for building on
            //      later JDKs without a warning.
            final boolean isConstructorAccessible = driverConstructor.isAccessible();
            if (!isConstructorAccessible) {
                driverConstructor.setAccessible(true);
            }

            try {
                final Driver driver = driverConstructor.newInstance();
                registerDriver(new DriverProxy(driver));
            } finally {
                driverConstructor.setAccessible(isConstructorAccessible);
            }

            return true;
        } catch (final RuntimeException e) {
            return false;
        } catch (final Exception e) {
            return false;
        }
    }

    /**
     * Print the stack trace for a SQLException to STDERR.
     *
     * @param e SQLException to print stack trace of
     */
    public static void printStackTrace(final SQLException e) {
        printStackTrace(e, new PrintWriter(System.err));
    }

    /**
     * Print the stack trace for a SQLException to a
     * specified PrintWriter.
     *
     * @param e SQLException to print stack trace of
     * @param pw PrintWriter to print to
     */
    public static void printStackTrace(final SQLException e, final PrintWriter pw) {

        SQLException next = e;
        while (next != null) {
            next.printStackTrace(pw);
            next = next.getNextException();
            if (next != null) {
                pw.println("Next SQLException:");
            }
        }
    }

    /**
     * Print warnings on a Connection to STDERR.
     *
     * @param conn Connection to print warnings from
     */
    public static void printWarnings(final Connection conn) {
        printWarnings(conn, new PrintWriter(System.err));
    }

    /**
     * Print warnings on a Connection to a specified PrintWriter.
     *
     * @param conn Connection to print warnings from
     * @param pw PrintWriter to print to
     */
    public static void printWarnings(final Connection conn, final PrintWriter pw) {
        if (conn != null) {
            try {
                printStackTrace(conn.getWarnings(), pw);
            } catch (final SQLException e) {
                printStackTrace(e, pw);
            }
        }
    }

    /**
     * Rollback any changes made on the given connection.
     * @param conn Connection to rollback.  A null value is legal.
     * @throws SQLException if a database access error occurs
     */
    public static void rollback(final Connection conn) throws SQLException {
        if (conn != null) {
            conn.rollback();
        }
    }

    /**
     * Performs a rollback on the {@code Connection} then closes it,
     * avoid closing if null.
     *
     * @param conn Connection to rollback.  A null value is legal.
     * @throws SQLException if a database access error occurs
     * @since DbUtils 1.1
     */
    public static void rollbackAndClose(final Connection conn) throws SQLException {
        if (conn != null) {
            try {
                conn.rollback();
            } finally {
                conn.close();
            }
        }
    }

    /**
     * Performs a rollback on the {@code Connection} then closes it,
     * avoid closing if null and hide any SQLExceptions that occur.
     *
     * @param conn Connection to rollback.  A null value is legal.
     * @since DbUtils 1.1
     */
    public static void rollbackAndCloseQuietly(final Connection conn) {
        try {
            rollbackAndClose(conn);
        } catch (final SQLException e) { // NOPMD
            // quiet
        }
    }

    /**
     * Simple {@link Driver} proxy class that proxies a JDBC Driver loaded dynamically.
     *
     * @since 1.6
     */
    static final class DriverProxy implements Driver {

        private boolean parentLoggerSupported = true;

        /**
         * The adapted JDBC Driver loaded dynamically.
         */
        private final Driver adapted;

        /**
         * Creates a new JDBC Driver that adapts a JDBC Driver loaded dynamically.
         *
         * @param adapted the adapted JDBC Driver loaded dynamically.
         */
        public DriverProxy(final Driver adapted) {
            this.adapted = adapted;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean acceptsURL(final String url) throws SQLException {
            return adapted.acceptsURL(url);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Connection connect(final String url, final Properties info) throws SQLException {
            return adapted.connect(url, info);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int getMajorVersion() {
            return adapted.getMajorVersion();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int getMinorVersion() {
            return adapted.getMinorVersion();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public DriverPropertyInfo[] getPropertyInfo(final String url, final Properties info) throws SQLException {
            return adapted.getPropertyInfo(url, info);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean jdbcCompliant() {
            return adapted.jdbcCompliant();
        }

        /**
         * Java 1.7 method.
         */
        @Override
        public Logger getParentLogger() throws SQLFeatureNotSupportedException {
            if (parentLoggerSupported) {
                try {
                    final Method method = adapted.getClass().getMethod("getParentLogger");
                    return (Logger)method.invoke(adapted);
                } catch (final NoSuchMethodException e) {
                    parentLoggerSupported = false;
                    throw new SQLFeatureNotSupportedException(e);
                } catch (final IllegalAccessException e) {
                    parentLoggerSupported = false;
                    throw new SQLFeatureNotSupportedException(e);
                } catch (final InvocationTargetException e) {
                    parentLoggerSupported = false;
                    throw new SQLFeatureNotSupportedException(e);
                }
            }
            throw new SQLFeatureNotSupportedException();
        }

    }

}
