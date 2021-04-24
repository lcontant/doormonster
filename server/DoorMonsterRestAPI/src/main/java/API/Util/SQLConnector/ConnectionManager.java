package API.Util.SQLConnector;







import API.Model.DBConnection;
import org.springframework.stereotype.Component;

import java.sql.*;

@Component
public class ConnectionManager {

    DBConnection dbConnection;
    ConnectionPoolManager poolManager;
    public ConnectionManager(ConnectionPoolManager poolManager) throws SQLException {
        this.poolManager = poolManager;
    }

    public PreparedStatement prepareStatement(String query) throws SQLException {
        dbConnection = poolManager.getConnection();
        PreparedStatement statement = dbConnection.connection.prepareStatement(query);
        return statement;
    }

    public ResultSet executePreparedQuery(PreparedStatement preparedStatement) throws SQLException {
        ResultSet resultSet = preparedStatement.executeQuery();
        poolManager.freeConnection(dbConnection);
        return resultSet;
    }

    public int executeUpdateQuery(PreparedStatement preparedStatement) throws SQLException {
        int updatedRows = preparedStatement.executeUpdate();
        poolManager.freeConnection(dbConnection);
        return updatedRows;
    }

    public ResultSet query(String query) throws SQLException {
        dbConnection = poolManager.getConnection();
        Statement statement = dbConnection.connection.createStatement();
        ResultSet rs = statement.executeQuery(query);
        poolManager.freeConnection(dbConnection);
        return rs;
    }

    public int update(String updateQuery) throws SQLException {
        dbConnection = poolManager.getConnection();
        Statement statement = dbConnection.connection.createStatement();
        poolManager.freeConnection(dbConnection);
        return statement.executeUpdate(updateQuery);
    }
}
