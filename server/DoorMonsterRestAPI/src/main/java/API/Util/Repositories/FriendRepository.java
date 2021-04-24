package API.Util.Repositories;



import API.Model.Friend;
import API.Util.SQLConnector.ConnectionManager;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
@Component
public class FriendRepository {

    ConnectionManager connectionManager;

    public FriendRepository(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    public synchronized List<Friend> getList() throws SQLException {
        List<Friend> friends = new ArrayList<>();
        String request = "SELECT * FROM friend";
        ResultSet rs = this.connectionManager.query(request);
        while(rs.next()) {
            friends.add(new Friend(rs));
        }
        return friends;
    }

    public synchronized List<Friend> getNRandom(int n) throws SQLException {
        List<Friend> friends = new ArrayList<>();
        String request = "SELECT * FROM friend ORDER BY RAND() LIMIT 7";
        ResultSet rs = this.connectionManager.query(request);
        while (rs.next()) {
            friends.add(new Friend(rs));
        }
        return friends;
    }
}
