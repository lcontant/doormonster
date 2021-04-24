package API.Util.SQLConnector;

import API.Model.DBConnection;
import com.mysql.cj.jdbc.MysqlDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Stack;

@Component
public class ConnectionPoolManager {

    private String dbPassWord;
    private String dbUsername;
    private String dbServerName;
    private MysqlDataSource dataSource;
    private ArrayList<DBConnection> connections;
    private Stack<Integer> freeConnectionIndexStack;

    private static ConnectionPoolManager instance;
    private String dbName;


    public ConnectionPoolManager(@Value("${DB.name}") String dbName
            ,@Value("${DB.pass}") String dbPassWord
            ,@Value("${DB.username}") String dbUsername
            ,@Value("${DB.address}") String address) throws SQLException {
        this.dbUsername = dbUsername;
        this.dbName = dbName;
        this.dbPassWord = dbPassWord;
        this.dbServerName = address;
        dataSource = new MysqlDataSource();
        connections = new ArrayList<>();
        freeConnectionIndexStack = new Stack<>();

        dataSource.setUser(this.dbUsername);
        dataSource.setPassword(this.dbPassWord);
        dataSource.setServerName(this.dbServerName);
        dataSource.setDatabaseName(this.dbName);
        dataSource.setServerTimezone("UTC");
        dataSource.setAutoReconnect(true);
        dataSource.setUseSSL(true);
        instanceConnectionPool(0);
    }

    public synchronized DBConnection getConnection() {
        DBConnection connectionInstance = null;
        int freeIndex = 0;
        if (!freeConnectionIndexStack.empty()) {
            try {
                freeIndex = freeConnectionIndexStack.pop();
            } catch (EmptyStackException e) {
                e.printStackTrace();
                //Retry because it was a timing issue
                return getConnection();
            }
            connectionInstance = this.connections.get(freeIndex);
            connectionInstance.isUsed = true;
            try {
                if (connectionInstance.connection.isClosed()) {
                    connectionInstance.connection = dataSource.getConnection();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                connectionInstance = new DBConnection(true,dataSource.getConnection(),this.connections.size());
                this.freeConnectionIndexStack.push(this.connections.size());
                this.connections.add(connectionInstance);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return connectionInstance;
    }

    public void freeConnection(DBConnection freedConnection) {
        DBConnection actualConnection = this.connections.get(freedConnection.id);
        if (actualConnection != null) {
            actualConnection.isUsed = false;
            this.freeConnectionIndexStack.push(actualConnection.id);
        }
    }

    private void instanceConnectionPool(int poolSize) {
        DBConnection connectionInstance = null;
        for (int i = 0; i < poolSize; i++) {
            try {
                connectionInstance = new DBConnection(true,dataSource.getConnection(),this.connections.size());
                connectionInstance.connection = dataSource.getConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            this.freeConnectionIndexStack.push(this.connections.size());
            this.connections.add(connectionInstance);
        }
    }

}
