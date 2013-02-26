package org.apache.commons.dbutils;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

/**
 * This class provides the ability to execute a batch of statements.
 * 
 * It is really just a facade to an array of UpdateExecutors.
 * 
 * @author William Speirs <wspeirs@apache.org>
 */
public class BatchExecutor extends AbstractExecutor<BatchExecutor> {

    private final boolean closeConn;
    
    public BatchExecutor(final Connection conn, final String sql, final boolean closeConnection) throws SQLException {
        super(conn, sql);
        this.closeConn = closeConnection;
    }
    
    /**
     * Binds a parameter name to a value for a given statement.
     * @param statement the statement number to operate on.
     * @param name the name of the parameter.
     * @param value the value to bind to the parameter.
     * @return this object.
     * @throws SQLException thrown if the statement number does not exist, or any other SQLException.
     * @see org.apache.commons.dbutils.UpdateExecutor.bind(String, Object)
     */
    @Override
    public BatchExecutor bind(final String name, final Object value) throws SQLException {
        return bind(name, value, false);
    }
    
    /**
     * Binds null to a parameter.
     * Types.VARCHAR is used as the type's parameter.
     * This usually works, but fails with some Oracle and MS SQL drivers.
     * @param name the name of the parameter.
     * @return this execution object to provide the fluent style.
     * @throws SQLException throw if the parameter is not found, already bound, or there is an issue binding null.
     */
    @Override
    public BatchExecutor bindNull(final String name) throws SQLException {
        return bindNull(name, Types.VARCHAR, false);
    }
    
    /**
     * Binds null to a parameter, specifying the parameter's type.
     * @param name the name of the parameter.
     * @param sqlType the type of the parameter.
     * @return this execution object to provide the fluent style.
     * @throws SQLException throw if the parameter is not found, already bound, or there is an issue binding null.
     */
    @Override
    public BatchExecutor bindNull(final String name, final int sqlType) throws SQLException {
        return bindNull(name, sqlType, false);
    }
    
    /**
     * Adds the statement to the batch after binding all of the parameters.
     * @return this object.
     * @throws SQLException if a SQLException is thrown during the addBatch() call.
     * @see java.sql.PreparedStatement.addBatch()
     */
    public BatchExecutor addBatch() throws SQLException {
        try {
            getStatement().addBatch();
            clearValueMap();
        } catch(SQLException e) {
            rethrow(e);
        }
        
        return this;
    }

    /**
     * Calls batch after checking the parameters to ensure nothing is null.
     * @return an array containing the number of rows updated for each statement.
     * @throws SQLException If there are database or parameter errors.
     * @see org.apache.commons.dbutils.UpdateExecutor.update()
     */
    public int[] batch() throws SQLException {
        try {
            return getStatement().executeBatch();
        } catch (SQLException e) {
            rethrow(e);
        } finally {
            close(getStatement());
            if (closeConn) {
                close(getConnection());
            }
        }

        // we get here only if something is thrown
        return null;
    }

}
