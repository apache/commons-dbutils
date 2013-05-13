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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Fluent class for executing a query.
 *
 * @since 2.0
 */
public class QueryExecutor extends AbstractExecutor<QueryExecutor> {

    private final boolean closeConn;

    /**
     * Constructs a QueryExecutor given a connection and SQL statement.
     * 
     * @param conn The connection to use during execution.
     * @param sql The SQL statement.
     * @param closeConnection If the connection should be closed or not.
     * @throws SQLException thrown if there is an error during execution.
     */
    QueryExecutor(final Connection conn, final String sql, final boolean closeConnection) throws SQLException {
        super(conn, sql);
        this.closeConn = closeConnection;
    }

    /**
     * Calls query after checking the parameters to ensure nothing is null.
     *
     * @param handler The handler that converts the results into an object.
     *
     * @return The results of the query.
     * @throws SQLException If there are database or parameter errors.
     */
    public <T> T execute(ResultSetHandler<T> handler) throws SQLException {
        // throw an exception if there are unmapped parameters
        this.throwIfUnmappedParams();

        // make sure our handler is not null
        if (handler == null) {
            if (closeConn) {
                close(getConnection());
            }
            throw new SQLException("Null ResultSetHandler");
        }

        ResultSet resultSet = null;

        try {
            // execute the query, wrapping it
            resultSet = this.wrap(getStatement().executeQuery());
            // execute the handler
            return handler.handle(resultSet);
        } catch (SQLException e) {
            // rethrow our exception printing more information
            this.rethrow(e);
        } finally {
            try {
                close(resultSet);
            } finally {
                close(getStatement());
                if (closeConn) {
                    close(getConnection());
                }
            }
        }

        // we get here only if something is thrown
        return null;
    }
}
