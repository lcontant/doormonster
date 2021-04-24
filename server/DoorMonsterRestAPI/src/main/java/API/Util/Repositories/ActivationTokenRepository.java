package API.Util.Repositories;

import API.Model.ActivationToken;
import API.Util.SQLConnector.ConnectionManager;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class ActivationTokenRepository {
    public static String TABLE_NAME = "ActivationTokens";
    public static String ID_COLUMN_NAME = "activationid";
    public static String USER_ID_COLUMN_NAME = "userid";
    ConnectionManager connectionManager;

    public ActivationTokenRepository(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    public synchronized boolean createToken(ActivationToken token) throws SQLException {
        String request = String.format("Insert into %s (%s, %s) VALUES(?,?)", TABLE_NAME, ID_COLUMN_NAME, USER_ID_COLUMN_NAME);
        PreparedStatement statement = connectionManager.prepareStatement(request);
        statement.setString(1,token.activationId);
        statement.setInt(2, token.userId);
        int result = connectionManager.executeUpdateQuery(statement);
        return result == 1;
    }

    public synchronized ActivationToken getTokenByUserId(int userId) throws SQLException {
        String request = String.format("Select * from %s where %s = ?", TABLE_NAME, USER_ID_COLUMN_NAME);
        ResultSet rs;
        ActivationToken foundToken;
        PreparedStatement statement = connectionManager.prepareStatement(request);
        statement.setInt(1, userId);
        rs = connectionManager.executePreparedQuery(statement);
        foundToken = null;
        if (rs.next()){
           foundToken = new ActivationToken(rs);
        }
        return foundToken;
    }

    public synchronized ActivationToken getTokenById(String activationId) throws SQLException {
        String request = String.format("Select * from %s where %s = ?",TABLE_NAME,ID_COLUMN_NAME);
        PreparedStatement statement = this.connectionManager.prepareStatement(request);
        statement.setString(1, activationId);
        ResultSet rs = this.connectionManager.executePreparedQuery(statement);
        ActivationToken foundToken = null;
        if (rs.next()) {
            foundToken = new ActivationToken(rs);
        }
        return foundToken;
    }

    public synchronized boolean cleanTokensForUser(int userId) throws SQLException {
        String request = String.format("Delete from %s where %s =?",TABLE_NAME, USER_ID_COLUMN_NAME);
        PreparedStatement statement = this.connectionManager.prepareStatement(request);
        statement.setInt(1, userId);
        int rowUpdated = this.connectionManager.executeUpdateQuery(statement);
        return rowUpdated == 1;
    }
}
