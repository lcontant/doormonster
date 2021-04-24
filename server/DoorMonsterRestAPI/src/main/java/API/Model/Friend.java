package API.Model;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Friend {
    String friendId;
    String friendName;
    String friendLink;
    String friendImage;

    public Friend(String friendId, String friendName, String friendLink, String friendImage) {
        this.friendId = friendId;
        this.friendName = friendName;
        this.friendLink = friendLink;
        this.friendImage = friendImage;
    }

    public Friend(ResultSet rs) throws SQLException {
        this(rs.getString("friendId")
                ,rs.getString("friendName")
                ,rs.getString("friendLink")
                ,rs.getString("friendImage"));
    }
}
