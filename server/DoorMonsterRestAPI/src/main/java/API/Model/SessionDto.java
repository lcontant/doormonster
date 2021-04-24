package API.Model;

import API.databases.tables.records.SessionRecord;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SessionDto {
    public String sessionId;
    public int userId;

    public SessionDto() {
    }

    public SessionDto(String sessionToken, int userId) {
        this.sessionId = sessionToken;
        this.userId = userId;
    }

    public SessionDto(ResultSet resultSet) throws SQLException {
        this(resultSet.getString("SessionId")
                , resultSet.getInt("userId"));
    }

    public SessionDto(SessionRecord sessionRecord) {
       this(sessionRecord.getSessionid(), Math.toIntExact(sessionRecord.getUserid()));
    }
}
