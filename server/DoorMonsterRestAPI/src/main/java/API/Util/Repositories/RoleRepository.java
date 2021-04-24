package API.Util.Repositories;

import API.Model.DBConnection;
import API.Model.Role;
import API.Model.UserRole;
import API.Util.SQLConnector.ConnectionPoolManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class RoleRepository {

    private static final String USER_ID_COLUMN_NAME = "USER_ID";
    private static final String ROLE_ID_COLUMN_NAME = "ROLE_ID";
    private static final String USER_ROLE_TABLE_NAME = "USER_ROLE";
    private static final String ROLE_TABLE_ID_COL_NAME = "ID";
    private static final String ROLE_TABLE_NAME = "ROLE";
    private static final String GET_USER_ROLE_BY_USER_ID_REQUEST = String.format("Select * from %s where %s = ?", USER_ROLE_TABLE_NAME, USER_ID_COLUMN_NAME);
    private static final String SET_USER_ROLE_REQUEST = String.format("update %s set %s = ? where %s = ?", USER_ROLE_TABLE_NAME, ROLE_ID_COLUMN_NAME, USER_ID_COLUMN_NAME);
    private static final String GET_ROLE_BY_ID_REQUEST = String.format("Select * from %s where %s = ?", ROLE_TABLE_NAME, ROLE_TABLE_ID_COL_NAME);
    private static final String GET_ROLE_BY_USER_ID_REQUEST = String.format("Select %s.* from %s join %s on %s.%s = %s.%s where %s.%s = ?", ROLE_TABLE_NAME, USER_ROLE_TABLE_NAME, ROLE_TABLE_NAME, ROLE_TABLE_NAME, ROLE_TABLE_ID_COL_NAME, USER_ROLE_TABLE_NAME, ROLE_ID_COLUMN_NAME, USER_ROLE_TABLE_NAME, USER_ID_COLUMN_NAME);
    private ConnectionPoolManager connectionPoolManager;
    private DBConnection dbConnection;
    private Connection connection;
    private PreparedStatement getUserRoleByUserIdStatement;
    private PreparedStatement setUserRoleStatement;
    private PreparedStatement getRoleByIdStatement;
    private PreparedStatement getRoleByUserIdStatement;

    public RoleRepository(ConnectionPoolManager connectionPoolManager) throws SQLException {
        this.connectionPoolManager = connectionPoolManager;
        this.dbConnection = this.connectionPoolManager.getConnection();
        this.connection = this.dbConnection.getConnection();
        this.initStatements();
    }

    private void initConnection() throws SQLException {
        this.dbConnection = this.connectionPoolManager.getConnection();
        this.connection = this.dbConnection.getConnection();
        this.initStatements();
    }

    private void initStatements() throws SQLException {
        this.getUserRoleByUserIdStatement = this.connection.prepareStatement(GET_USER_ROLE_BY_USER_ID_REQUEST);
        this.getRoleByIdStatement = this.connection.prepareStatement(GET_ROLE_BY_ID_REQUEST);
        this.setUserRoleStatement = this.connection.prepareStatement(SET_USER_ROLE_REQUEST);
        this.getRoleByUserIdStatement = this.connection.prepareStatement(GET_ROLE_BY_USER_ID_REQUEST);
    }

    public synchronized UserRole getUserRoleByUserId(int userId) {
        UserRole userRole = null;
        try {
            if (this.connection.isClosed()) {
                this.initConnection();
            }
            getUserRoleByUserIdStatement.setInt(1,userId);
            ResultSet rs = getUserRoleByUserIdStatement.executeQuery();
            if (rs.next()) {
                userRole = new UserRole(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userRole;
    }

    public synchronized ResponseEntity<String> setUserRole(UserRole userRole) throws SQLException {
        if (this.connection.isClosed()) {
            this.initConnection();
        }
        UserRole currentUserRole = this.getUserRoleByUserId(userRole.userId);
        int rowsUpdated;
        String updateMessage ="There was an error updating the role";
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        if (currentUserRole != null) {
            currentUserRole.roleId = userRole.roleId;
            setUserRoleStatement.setString(1,userRole.roleId);
            setUserRoleStatement.setInt(2, userRole.userId);
            rowsUpdated = setUserRoleStatement.executeUpdate();
            if (rowsUpdated == 1) {
                updateMessage = "Role updated";
                status = HttpStatus.OK;
            }
        }
        ResponseEntity<String> response = new ResponseEntity<>(updateMessage, status);
        return response;
    }

    public synchronized ResponseEntity<Role> getRoleById(String roleId) {
        Role role = null;
        try {
            if (this.connection.isClosed()) {
                this.initConnection();
            }
            getRoleByIdStatement.setString(1, roleId);
            ResultSet rs = getRoleByIdStatement.executeQuery();
            if (rs.next()) {
                role = new Role(rs);

            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(role, HttpStatus.OK);
    }

    public synchronized Role getRoleByUserId(int userId) throws SQLException {
        Role role= null;
        if (this.connection.isClosed()) {
           this.initConnection();
        }
        synchronized (this) {
            getRoleByUserIdStatement.setInt(1, userId);
            ResultSet rs = getRoleByUserIdStatement.executeQuery();
            if (rs.next()) {
                role = new Role(rs);
            }
            rs.close();
        }
        return role;
    }
}
