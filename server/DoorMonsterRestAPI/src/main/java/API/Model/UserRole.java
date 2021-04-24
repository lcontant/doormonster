package API.Model;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRole {
    private   String USER_ID_COLUMN_NAME = "USER_ID";
    private   String ROLE_ID_COLUMN_NAME = "ROLE_ID";
    private   String ROLE_RANK_COLUMN_NAME = "ROLE_RANK";
    public int userId;
    public String roleId;


    public UserRole(int userId, String roleId) {
        this.userId = userId;
        this.roleId = roleId;
    }

    public UserRole(ResultSet result) throws SQLException {
        this(result.getInt("USER_ID"),result.getString("ROLE_ID"));
    }
}
