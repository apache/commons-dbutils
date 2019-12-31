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
package org.apache.commons.dbutils2;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Entity;
import javax.sql.DataSource;

import org.apache.commons.beanutils.PropertyUtils;

import org.apache.commons.dbutils2.handlers.BeanHandler;
import org.apache.commons.dbutils2.handlers.BeanListHandler;

/**
 * Executes SQL queries with pluggable strategies for handling
 * <code>ResultSet</code>s.  This class is thread safe.
 *
 * @see ResultSetHandler
 */
public class QueryRunner {
    /**
     * The DataSource to retrieve connections from.
     */
    private final DataSource ds;

    /**
     * Constructor for QueryRunner.
     */
    public QueryRunner() {
        ds = null;
    }

    /**
     * Constructor for QueryRunner that takes a <code>DataSource</code> to use.
     *
     * Methods that do not take a <code>Connection</code> parameter will retrieve connections from this
     * <code>DataSource</code>.
     *
     * @param ds The <code>DataSource</code> to retrieve connections from.
     */
    public QueryRunner(final DataSource ds) {
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
     * Close a <code>Connection</code>. This implementation avoids closing if
     * null and does <strong>not</strong> suppress any exceptions. Subclasses
     * can override to provide special handling like logging.
     *
     * @param conn Connection to close
     * @throws SQLException if a database access error occurs
     */
    private void close(Connection conn) throws SQLException {
        DbUtils.close(conn);
    }

    /**
     * Creates an {@link BatchExecutor} for the given SQL.
     * <code>Connection</code> is retrieved from the <code>DataSource</code>
     * set in the constructor.  This <code>Connection</code> must be in
     * auto-commit mode or the insert will not be saved. The <code>Connection</code> is
     * closed after the call.
     *
     * @param sql The SQL statement to execute.
     *
     * @return An {@link BatchExecutor} for this SQL statement.
     * @throws SQLException If there are database or parameter errors.
     */
    public BatchExecutor batch(String sql) throws SQLException {
        return this.batch(this.prepareConnection(), true, sql);
    }

    /**
     * Creates an {@link BatchExecutor} for the given SQL statement and connection.
     * The connection is <b>NOT</b> closed after execution.
     *
     * @param conn The connection to use for the batch call.
     * @param sql The SQL statement to execute.
     *
     * @return An {@link BatchExecutor} for this SQL statement.
     * @throws SQLException If there are database or parameter errors.
     */
    public BatchExecutor batch(Connection conn, String sql) throws SQLException {
        return this.batch(conn, true, sql);
    }

    /**
     * Creates an {@link BatchExecutor} for the given SQL statement and connection.
     *
     * @param conn The connection to use for the batch call.
     * @param closeConn True if the connection should be closed, false otherwise.
     * @param sql The SQL statement to execute.
     *
     * @return An {@link BatchExecutor} for this SQL statement.
     * @throws SQLException If there are database or parameter errors.
     */
    public BatchExecutor batch(Connection conn, boolean closeConn, String sql) throws SQLException {
        if (conn == null) {
            throw new SQLException("Null connection");
        }

        if (sql == null) {
            if (closeConn) {
                close(conn);
            }
            throw new SQLException("Null SQL statement");
        }

        return new BatchExecutor(conn, sql, closeConn);
    }

    /**
     * Creates an {@link QueryExecutor} for the given SQL.
     * <code>Connection</code> is retrieved from the <code>DataSource</code>
     * set in the constructor.  This <code>Connection</code> must be in
     * auto-commit mode or the insert will not be saved. The <code>Connection</code> is
     * closed after the call.
     *
     * @param sql The SQL statement to execute.
     *
     * @return A {@link QueryExecutor} for this SQL statement.
     * @throws SQLException If there are database or parameter errors.
     */
    public QueryExecutor query(String sql) throws SQLException {
        return this.query(this.prepareConnection(), true, sql);
    }

    /**
     * Creates an {@link QueryExecutor} for the given SQL statement and connection.
     * The connection is <b>NOT</b> closed after execution.
     *
     * @param conn The connection to use for the update call.
     * @param sql The SQL statement to execute.
     *
     * @return An {@link QueryExecutor} for this SQL statement.
     * @throws SQLException If there are database or parameter errors.
     */
    public QueryExecutor query(Connection conn, String sql) throws SQLException {
        return this.query(conn, false, sql);
    }

    /**
     * Creates an {@link QueryExecutor} for the given SQL statement and connection.
     *
     * @param conn The connection to use for the query call.
     * @param closeConn True if the connection should be closed, false otherwise.
     * @param sql The SQL statement to execute.
     *
     * @return An {@link QueryExecutor} for this SQL statement.
     * @throws SQLException If there are database or parameter errors.
     */
    public QueryExecutor query(Connection conn, boolean closeConn, String sql) throws SQLException {
        if (conn == null) {
            throw new SQLException("Null connection");
        }

        if (sql == null) {
            if (closeConn) {
                close(conn);
            }
            throw new SQLException("Null SQL statement");
        }

        return new QueryExecutor(conn, sql, closeConn);
    }

    /**
     * Creates an {@link UpdateExecutor} for the given SQL.
     * <code>Connection</code> is retrieved from the <code>DataSource</code>
     * set in the constructor.  This <code>Connection</code> must be in
     * auto-commit mode or the insert will not be saved. The <code>Connection</code> is
     * closed after the call.
     *
     * @param sql The SQL statement to execute.
     *
     * @return An {@link UpdateExecutor} for this SQL statement.
     * @throws SQLException if a database access error occurs
     */
    public UpdateExecutor update(String sql) throws SQLException {
        return this.update(this.prepareConnection(), true, sql);
    }

    /**
     * Creates an {@link UpdateExecutor} for the given SQL statement and connection.
     * The connection is <b>NOT</b> closed after execution.
     *
     * @param conn The connection to use for the update call.
     * @param sql The SQL statement to execute.
     *
     * @return An {@link UpdateExecutor} for this SQL statement.
     * @throws SQLException If there are database or parameter errors.
     */
    public UpdateExecutor update(Connection conn, String sql) throws SQLException {
        return this.update(conn, false, sql);
    }

    /**
     * Creates an {@link UpdateExecutor} for the given SQL statement and connection.
     *
     * @param conn The connection to use for the update call.
     * @param closeConn True if the connection should be closed, false otherwise.
     * @param sql The SQL statement to execute.
     *
     * @return An {@link UpdateExecutor} for this SQL statement.
     * @throws SQLException If there are database or parameter errors.
     */
    public UpdateExecutor update(Connection conn, boolean closeConn, String sql) throws SQLException {
        if (conn == null) {
            throw new SQLException("Null connection");
        }

        if (sql == null) {
            if (closeConn) {
                close(conn);
            }
            throw new SQLException("Null SQL statement");
        }

        return new UpdateExecutor(conn, sql, closeConn);
    }

    /**
     * Creates an {@link InsertExecutor} for the given SQL.
     * <code>Connection</code> is retrieved from the <code>DataSource</code>
     * set in the constructor.  This <code>Connection</code> must be in
     * auto-commit mode or the insert will not be saved. The <code>Connection</code> is
     * closed after the call.
     *
     * @param sql The SQL statement to execute.
     *
     * @return An {@link InsertExecutor} for this SQL statement.
     * @throws SQLException If there are database or parameter errors.
     */
    public InsertExecutor insert(String sql) throws SQLException {
        return insert(this.prepareConnection(), true, sql);
    }

    /**
     * Creates an {@link InsertExecutor} for the given SQL and connection
     * The connection is <b>NOT</b> closed after execution.
     *
     * @param conn The connection to use for the query call.
     * @param sql The SQL statement to execute.
     *
     * @return An {@link InsertExecutor} for this SQL statement.
     * @throws SQLException If there are database or parameter errors.
     */
    public InsertExecutor insert(Connection conn, String sql) throws SQLException {
        return insert(conn, false, sql);
    }

    /**
     * Creates an {@link InsertExecutor} for the given SQL and connection.
     *
     * @param conn The connection to use for the insert call.
     * @param closeConn True if the connection should be closed, false otherwise.
     * @param sql The SQL statement to execute.
     *
     * @return An {@link InsertExecutor} for this SQL statement.
     * @throws SQLException If there are database or parameter errors.
     */
    public InsertExecutor insert(Connection conn, boolean closeConn, String sql) throws SQLException {
        if (conn == null) {
            throw new SQLException("Null connection");
        }

        if (sql == null) {
            if (closeConn) {
                close(conn);
            }
            throw new SQLException("Null SQL statement");
        }

        return new InsertExecutor(conn, sql, closeConn);
    }

    //
    // Entity methods
    //

    /**
     * Creates a new entity in the database by calling insert.
     * @param entity the entity to insert.
     * @throws SQLException if there is a problem inserting the entity.
     */
    public <T> void create(final Class<? extends T> entityClass, final T entity) throws SQLException {
        internalEntityCreate(entityClass, entity, new HashSet<String>()).execute();
    }

    /*
     * Internal method that returns the InsertExecutor making it easier to extend.
     */
    protected <T> InsertExecutor internalEntityCreate(final Class<? extends T> entityClass,
                                                      final T entity,
                                                      final Set<String> excludeColumns) throws SQLException {
        final String tableName = EntityUtils.getTableName(entity.getClass());
        final Map<String, String> columns = EntityUtils.getColumns(entityClass);

        final StringBuilder sb = new StringBuilder("insert into ");

        // create the SQL command
        sb.append(tableName);
        sb.append(" (");
        sb.append(EntityUtils.joinColumnsWithComma(columns.keySet(), null));
        sb.append(") values(");
        sb.append(EntityUtils.joinColumnsWithComma(columns.keySet(), ":"));
        sb.append(")");

        // create the executor
        final InsertExecutor exec = new InsertExecutor(this.prepareConnection(), sb.toString(), true);

        for(String column:columns.keySet()) {
            // don't bind the exclude columns
            if(excludeColumns.contains(column)) {
                continue;
            }

            try {
                // bind all of the values
                final Object value = PropertyUtils.getSimpleProperty(entity, columns.get(column));

                if(value == null) {
                    exec.bindNull(column);
                } else {
                    exec.bind(column, value);
                }
            } catch (final IllegalAccessException e) {
                throw new SQLException(e);
            } catch (final InvocationTargetException e) {
                throw new SQLException(e);
            } catch (final NoSuchMethodException e) {
                throw new SQLException(e);
            }
        }

        return exec;
    }

    /**
     * Reads all of the entities of a given type.
     * @param entity an entity marked with the {@link Entity} annotation.
     * @return a list of the entities read .
     * @throws SQLException If there are database or parameter errors.
     */
    public <T> List<T> read(final Class<T> entityClass) throws SQLException {
        final Entity annotation = entityClass.getAnnotation(Entity.class);

        if(annotation == null) {
            throw new IllegalArgumentException(entityClass.getName() + " does not have the Entity annotation");
        }

        // get the table's name
        final String tableName = EntityUtils.getTableName(entityClass);

        final StringBuilder sb = new StringBuilder("select * from ");

        sb.append(tableName);

        // setup the QueryExecutor
        final QueryExecutor exec = new QueryExecutor(prepareConnection(), sb.toString(), true);

        // execute using the BeanHandler
        return exec.execute(new BeanListHandler<T>(entityClass));
    }

    /**
     * Reads a given entity based off the @Id columns.
     * @param entityClass an entity marked with the {@link Entity} annotation.
     * @param entity the entity to read.
     * @return the entity read from the db.
     * @throws SQLException If there are database or parameter errors.
     */
    public <T> T read(final Class<T> entityClass, final T entity) throws SQLException {
        final Entity annotation = entityClass.getAnnotation(Entity.class);
        final Map<String, String> idColumns = EntityUtils.getIdColumns(entityClass);

        if(annotation == null) {
            throw new IllegalArgumentException(entityClass.getName() + " does not have the Entity annotation");
        }

        if(idColumns.isEmpty()) {
            throw new SQLException("Cannot read " + entityClass.getName() + " because it does not have any @Id columns");
        }

        // get the table's name
        final String tableName = EntityUtils.getTableName(entityClass);

        final StringBuilder sb = new StringBuilder("select * from ");

        sb.append(tableName);
        sb.append(" where ");
        sb.append(EntityUtils.joinColumnsEquals(idColumns.keySet(), " and "));

        // setup the QueryExecutor
        final QueryExecutor exec = new QueryExecutor(prepareConnection(), sb.toString(), true);

        // bind all the id columns
        bindColumnValues(exec, idColumns, entity, Collections.<String>emptySet());

        // execute using the BeanHandler
        return exec.execute(new BeanHandler<T>(entityClass));
    }

    /**
     * Constructs an {@link UpdateEntityExecutor} used to update entities.
     * @param entity an entity marked with the {@link Entity} annotation.
     * @return a {@link UpdateEntityExecutor} used to update entities.
     * @throws SQLException If there are database or parameter errors.
     */
    public <T> int update(final Class<T> classType, final T entity) throws SQLException {
        return update(classType, entity, Collections.<String>emptySet());
    }

    /**
     * Constructs an {@link UpdateEntityExecutor} used to update entities that excludes columns during binding.
     * @param entity an entity marked with the {@link Entity} annotation.
     * @param excludeColumns a collection of columns to exclude.
     * @return a {@link UpdateEntityExecutor} used to update entities.
     * @throws SQLException If there are database or parameter errors.
     */
    public <T> int update(final Class<T> entityClass, final T entity, final Collection<String> excludeColumns) throws SQLException {
        final Map<String, String> updateColumns = EntityUtils.getColumns(entityClass, true);
        final Map<String, String> idColumns = EntityUtils.getIdColumns(entityClass);
        final Entity annotation = entityClass.getAnnotation(Entity.class);

        if(annotation == null) {
            throw new IllegalArgumentException(entityClass.getName() + " does not have the Entity annotation");
        }

        if(idColumns.isEmpty()) {
            throw new SQLException("Cannot update " + entityClass.getName() + " because it does not have any @Id columns");
        }

        // get the table's name
        final String tableName = EntityUtils.getTableName(entityClass);

        final StringBuilder sb = new StringBuilder("update ");

        // create the SQL command
        sb.append(tableName);
        sb.append(" set ");
        sb.append(EntityUtils.joinColumnsEquals(updateColumns.keySet(), ", "));

        sb.append(" where ");
        sb.append(EntityUtils.joinColumnsEquals(idColumns.keySet(), " and "));

        // setup the QueryExecutor
        final UpdateExecutor exec = new UpdateExecutor(prepareConnection(), sb.toString(), true);

        // bind all the update column values
        bindColumnValues(exec, updateColumns, entity, excludeColumns);

        // bind all the id columns
        bindColumnValues(exec, idColumns, entity, Collections.<String>emptySet());

        // execute using the BeanHandler
        return exec.execute();
    }

    /**
     * Constructs an {@link DeleteEntityExecutor} used to delete entities.
     * @param entity an entity marked with the {@link Entity} annotation.
     * @return a {@link DeleteEntityExecutor} used to delete entities.
     * @throws SQLException If there are database or parameter errors.
     */
    public <T> int delete(final Class<T> entityClass, final T entity) throws SQLException {
        final Map<String, String> idColumns = EntityUtils.getIdColumns(entityClass);
        final Entity annotation = entityClass.getAnnotation(Entity.class);

        if(annotation == null) {
            throw new IllegalArgumentException(entityClass.getName() + " does not have the Entity annotation");
        }

        if(idColumns.isEmpty()) {
            throw new SQLException("Cannot update " + entityClass.getName() + " because it does not have any @Id columns");
        }

        // get the table's name
        final String tableName = EntityUtils.getTableName(entityClass);

        final StringBuilder sb = new StringBuilder("delete from ");

        sb.append(tableName);

        sb.append(" where ");
        sb.append(EntityUtils.joinColumnsEquals(idColumns.keySet(), " and "));

        // setup the QueryExecutor
        final UpdateExecutor exec = new UpdateExecutor(prepareConnection(), sb.toString(), true);

        // bind all the id columns
        bindColumnValues(exec, idColumns, entity, Collections.<String>emptySet());

        // execute using the BeanHandler
        return exec.execute();
    }

    /**
     * Binds values to an executor.
     * @param exec
     * @param columns
     * @param entity
     * @param excludes
     * @throws SQLException
     */
    private <T> void bindColumnValues(final AbstractExecutor<?> exec,
                                      final Map<String, String> columns,
                                      final T entity,
                                      final Collection<String> excludes) throws SQLException {
        for(String column:columns.keySet()) {
            // skip anything in the exclude set
            if(excludes.contains(column)) {
                continue;
            }

            try {
                // bind all of the values
                final Object value = PropertyUtils.getSimpleProperty(entity, columns.get(column));

                if(value == null) {
                    exec.bindNull(column);
                } else {
                    exec.bind(column, value);
                }
            } catch (final IllegalAccessException e) {
                throw new SQLException(e);
            } catch (final InvocationTargetException e) {
                throw new SQLException(e);
            } catch (final NoSuchMethodException e) {
                throw new SQLException(e);
            }
        }
    }

}
