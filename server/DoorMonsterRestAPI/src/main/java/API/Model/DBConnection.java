package API.Model;

import java.sql.Connection;

public class DBConnection {
    public boolean isUsed;
    public Connection connection;
    public int id;

    public DBConnection(boolean isUsed, Connection connection, int id) {
        this.isUsed = isUsed;
        this.connection = connection;
        this.id = id;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setUsed(boolean used) {
        isUsed = used;
    }
}
