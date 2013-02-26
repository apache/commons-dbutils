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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Abstract class for executing a query, insert, update, or batch.
 * 
 * @since 2.0
 * @author William Speirs <wspeirs@apache.org>
 */
abstract class AbstractExecutor<T extends AbstractExecutor<T>> {
    
    private static final String COLON = ":";  // TODO: change this to any character

    private final Connection conn;
    private final String sql;
    private final PreparedStatement stmt;

    private final Map<String, List<Integer>> paramPosMap;
    private final Map<String, Object> paramValueMap;
    private Integer currentPosition = Integer.valueOf(0);
    
    public AbstractExecutor(final Connection conn, final String sql) throws SQLException {
        this.conn = conn;
        this.sql = sql;
        this.paramPosMap = new HashMap<String, List<Integer>>();
        this.paramValueMap = new HashMap<String, Object>();
        
        final Pattern paramPattern = Pattern.compile("(:\\w+)");
        final Matcher matcher = paramPattern.matcher(sql);

        // go through finding params
        while(matcher.find()) {
            insertParamPosition(matcher.group().replace(COLON, ""));
        }
        
        // replace all of the :names with ?, and create a prepared statement
        stmt = conn.prepareStatement(sql.replaceAll(":\\w+", "\\?"));
    }
    
    /**
     * Helper method to insert params and the current position into the map.
     * @param param the SQL param.
     */
    private void insertParamPosition(final String param) {
        List<Integer> posList = paramPosMap.get(param);
        
        // create a new list if we need to
        if(posList == null) {
            posList = new ArrayList<Integer>();
            paramPosMap.put(param, posList);
        }
        
        // increment first, so we match SQL numbering
        posList.add(++currentPosition);
    }
    
    /**
     * Gets the SQL statement that was passed into the constructor.
     * @return the SQL statement passed into the constructor.
     */
    protected String getSql() {
        return sql;
    }
    
    /**
     * Returns the underlying prepared statement.
     * @return the underlying prepared statement.
     */
    protected PreparedStatement getStatement() {
        return stmt;
    }
    
    /**
     * Returns the underlying connection.
     * @return the underlying connection.
     */
    protected Connection getConnection() {
        return conn;
    }
    
    /**
     * Throws an exception if there are unmapped params.
     * @throws SQLException if there are unmapped params.
     */
    protected void throwIfUnmappedParams() throws SQLException {        
        if(paramPosMap.size() != 0) {
            final Set<String> unmappedParams = paramPosMap.keySet();
            final StringBuilder sb = new StringBuilder("There are unbound parameters: ");
            
            for(String param:unmappedParams) {
                sb.append(param);
                sb.append(", ");
            }
            
            // remove the last comma
            sb.delete(sb.length()-2, sb.length());
            
            // throw our exception
            throw new SQLException(sb.toString());
        }
    }
    
    /**
     * Binds a named parameter to a value.
     * 
     * @param name the name of the parameter in the SQL statement.
     * @param value the value of the parameter in the SQL statement.
     * @return this execution object to provide the fluent style.
     * @throws SQLException thrown if the parameter is not found, already bound, or there is an issue binding it.
     */
    public T bind(String name, final Object value) throws SQLException {
        return bind(name, value, true);
    }

    /**
     * Binds value to name, but does not do the bookkeeping.
     * @param name the parameter name.
     * @param value the value.
     * @return this
     * @throws SQLException if there is any SQLException during binding.
     */
    protected T bind(String name, final Object value, boolean removeFromPosMap) throws SQLException {
        name = name.replace(COLON, ""); // so we can take ":name" or "name"

        final List<Integer> pos = removeFromPosMap ? paramPosMap.remove(name) : paramPosMap.get(name);
        
        if(pos == null) {
            throw new SQLException(name + " is not found in the SQL statement");
        }
        
        // go through and bind all of the positions for this name
        for(Integer p:pos) {
            // TODO: need to figure out how to bind NULL
            stmt.setObject(p, value);
        }
        
        // add the param and value to our map
        paramValueMap.put(name, value);
        
        // suppressed because the casting will always work here
        @SuppressWarnings("unchecked")
        final T ret = (T) this;
        
        return ret;
    }
    
    /**
     * Used for batch calls so we can clear the map after the addBatch call.
     */
    protected void clearValueMap() {
        paramValueMap.clear();
    }

    /**
     * Throws a new exception with a more informative error message.
     *
     * @param cause The original exception that will be chained to the new
     *              exception when it's rethrown.
     *
     * @throws SQLException if a database access error occurs
     */
    protected void rethrow(SQLException cause) throws SQLException {

        String causeMessage = cause.getMessage();
        
        if (causeMessage == null) {
            causeMessage = "";
        }
        
        final StringBuffer msg = new StringBuffer(causeMessage);

        msg.append(" Query: ");
        msg.append(sql);
        msg.append(" Parameters: ");

        // loop through adding the parameter to value mappings
        for(Map.Entry<String, Object> param:paramValueMap.entrySet()) {
            msg.append(param.getKey());
            msg.append("=");
            msg.append(param.getValue());
            msg.append(" ");
        }

        final SQLException e = new SQLException(msg.toString(), cause.getSQLState(), cause.getErrorCode());
        e.setNextException(cause);

        throw e;
    }

    /**
     * Wrap the <code>ResultSet</code> in a decorator before processing it. This
     * implementation returns the <code>ResultSet</code> it is given without any
     * decoration.
     *
     * @param rs The <code>ResultSet</code> to decorate; never <code>null</code>.
     * @return The <code>ResultSet</code> wrapped in some decorator.
     */
    protected ResultSet wrap(ResultSet rs) {
        return rs;
    }

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

    /**
     * Close a <code>Statement</code>. This implementation avoids closing if
     * null and does <strong>not</strong> suppress any exceptions. Subclasses
     * can override to provide special handling like logging.
     *
     * @param stmt Statement to close
     * @throws SQLException if a database access error occurs
     */
    protected void close(Statement stmt) throws SQLException {
        DbUtils.close(stmt);
    }

    /**
     * Close a <code>ResultSet</code>. This implementation avoids closing if
     * null and does <strong>not</strong> suppress any exceptions. Subclasses
     * can override to provide special handling like logging.
     *
     * @param rs ResultSet to close
     * @throws SQLException if a database access error occurs
     */
    protected void close(ResultSet rs) throws SQLException {
        DbUtils.close(rs);
    }


}
