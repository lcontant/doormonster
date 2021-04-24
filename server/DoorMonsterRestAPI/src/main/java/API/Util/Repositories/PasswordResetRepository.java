package API.Util.Repositories;

import API.Model.PasswordResetToken;
import API.Util.SQLConnector.ConnectionManager;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerErrorException;

import java.sql.ResultSet;
import java.sql.SQLException;
@Component
public class PasswordResetRepository {

    ConnectionManager connectionManager;

    public PasswordResetRepository(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    public synchronized PasswordResetToken getByUserId(int userId) {
        String request = String.format("Select * from passwordResetTokens where userid='%d'", userId);
        PasswordResetToken resetToken = null;
        ResultSet result = null;
        try {
            result = this.connectionManager.query(request);
        } catch (SQLException e) {
            throw new ServerErrorException("Error connecting to the database", e);
        }
        try {
            if (result.next()) {
                resetToken = new PasswordResetToken(result);
            }
        } catch (SQLException e) {
            throw new ServerErrorException("Error getting the token", e);
        }
        return resetToken;
    }

    public synchronized void deleteTokensForUser(int  userId ) {
        String request = String.format("Delete from passwordResetTokens where userid = %d", userId);
        try {
            this.connectionManager.update(request);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public synchronized PasswordResetToken getByToken(String token) {
        String request = String.format("Select * from passwordResetTokens where resetToken='%s'", token);
        PasswordResetToken resetToken = null;
        ResultSet result = null;
        try {
            result = this.connectionManager.query(request);
        } catch (SQLException e) {
            return null;
        }
        try {
            if (result.next()) {
                resetToken = new PasswordResetToken(result);
            }
        } catch (SQLException e) {
            return null;
        }
        return resetToken;
    }

    public synchronized boolean insertPasswordToken(PasswordResetToken resetToken) {
        this.deleteTokensForUser(resetToken.userId);
        String request = "Insert into passwordResetTokens (resetToken, userid) VALUES ('" + resetToken.token + "','" + resetToken.userId +"')";
        int result = -1;
        try {
            result = this.connectionManager.update(request);
        } catch (SQLException e) {
        }
        return result == 1;
    }
}
