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
import java.sql.SQLException;


public class UpdateExecutor extends AbstractExecutor<UpdateExecutor> {

    private final boolean closeConn;
    
    public UpdateExecutor(final Connection conn, final String sql, final boolean closeConnection) throws SQLException {
        super(conn, sql);
        this.closeConn = closeConnection;
    }

    /**
     * Calls update after checking the parameters to ensure nothing is null.
     * @return The number of rows updated.
     * @throws SQLException If there are database or parameter errors.
     */
    public int execute() throws SQLException {
        // throw an exception if there are unmapped parameters
        this.throwIfUnmappedParams();

        try {
            return getStatement().executeUpdate();
        } catch (SQLException e) {
            this.rethrow(e);

        } finally {
            close(getStatement());
            if (closeConn) {
                close(getConnection());
            }
        }

        // we get here only if something is thrown
        return 0;
    }

}
