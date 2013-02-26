package org.apache.commons.dbutils;

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
    public int update() throws SQLException {
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
