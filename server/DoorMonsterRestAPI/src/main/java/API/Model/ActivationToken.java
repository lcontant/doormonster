package API.Model;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ActivationToken {
    public String activationId;
    public int userId;

    public ActivationToken(String activationId, int userId) {
        this.activationId = activationId;
        this.userId = userId;
    }

    public ActivationToken(ResultSet rs) throws SQLException {
        this(rs.getString("activationid"), rs.getInt("userid"));
    }
}
