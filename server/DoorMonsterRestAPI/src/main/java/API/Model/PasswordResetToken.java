package API.Model;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PasswordResetToken {
    public String token;
    public int userId;

    public PasswordResetToken(String token, int userId) {
        this.token = token;
        this.userId = userId;
    }

    public PasswordResetToken(ResultSet rs) throws SQLException {
        this(rs.getString("resetToken"),
                rs.getInt("userid"));
    }
}
