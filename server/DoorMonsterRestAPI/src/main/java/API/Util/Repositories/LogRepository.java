package API.Util.Repositories;

import API.Model.DBConnection;
import API.Model.Log;
import API.Util.SQLConnector.ConnectionPoolManager;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Null;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class LogRepository {

    private static String GET_ALL_LOGS_REQUEST = String.format("SELECT * FROM %s", Log.TABLE_NAME);
    private static String INSERT_NEW_LOG = String.format("Insert into %s (%s, %s) values(?, ?)", Log.TABLE_NAME, Log.USER_COLUMN_NAME, Log.MESSAGE_COLUMN_NAME);

    private ConnectionPoolManager connectionPoolManager;
    private DBConnection dbConnection;
    private Connection connection;

    private PreparedStatement getAllLogsStatement;
    private PreparedStatement insertNewLogStatement;

    public LogRepository(ConnectionPoolManager connectionPoolManager) throws SQLException {
        this.connectionPoolManager = connectionPoolManager;
        this.initConnection();
    }

    private void initConnection() throws SQLException {
        this.dbConnection = this.connectionPoolManager.getConnection();
        this.connection = this.dbConnection.getConnection();
        this.initStatements();
    }

    private void initStatements() throws SQLException {
        this.getAllLogsStatement = this.connection.prepareStatement(GET_ALL_LOGS_REQUEST);
        this.insertNewLogStatement = this.connection.prepareStatement(INSERT_NEW_LOG);
    }

    public List<Log> getAllLogs() throws SQLException {
        ResultSet rs = this.getAllLogsStatement.executeQuery();
        List<Log> logs = new ArrayList<>();
        while (rs.next()) {
            logs.add(new Log(rs));
        }
        return logs;
    }

    public boolean insertLog(Log log) throws SQLException {
        int rowsUpdated = 0;
        if (log.user_id != 0) {
            this.insertNewLogStatement.setInt(1, log.user_id);
        } else {
            this.insertNewLogStatement.setObject(1, null);
        }
        this.insertNewLogStatement.setString(2, log.message);
        rowsUpdated = this.insertNewLogStatement.executeUpdate();
        return rowsUpdated == 1;
    }

}
