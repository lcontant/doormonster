package API.Util.Repositories;

import static API.databases.Tables.USER;

import API.Model.DBConnection;
import API.Model.SessionDto;
import API.Model.UserDto;
import API.Util.SQLConnector.ConnectionManager;
import API.Util.SQLConnector.ConnectionPoolManager;
import API.Util.SQLConnector.DSLContextUtil;
import API.databases.tables.User;
import org.jooq.DSLContext;
import org.jooq.DatePart;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class UserRepository {
    public static String USER_TABLE_NAME = "user";
    public static String USERNAME_COLUMN_NAME = "username";
    public static String LOCATION_COLUMN_NAME = "location";
    public static String AVATAR_COLUMN_NAME = "avatar";
    public static String EMAIL_COLUMN_NAME = "email";
    public static String ID_COLUMN_NAME = "userid";
    public static String BANNED_COLUMN_NAME = "BANNED";
    public static String IS_ACTIVATED_COLUMN_NAME = "isActivated";
    public static String EMAIL_NOTIFICATION_COLUMN_NAME = "SUBSCRIBED_EMAIL_NOTIFICATION";
    public static String PASSWORD_HASH_COLUMN_NAME = "passwordhash";
    public static String PASSWORD_SALT_COLUMN_NAME = "salt";
    public static String PATREON_TOKEN_COLUMN_NAME = "PatreonToken";
    public static String PATREON_REFRESH_TOKEN_COLUMN_NAME = "PatreonRefreshToken";
    public static String IS_PATRON_COLUMN_NAME = "isPatron";
    public static String FAILED_ATTEMPTS_COLUMN_NAME = "FailedAttempts";
    public static String PATREON_CONTRIBUTION = "PATREON_CONTRIBUTION";
    public static String USE_FULL_NAME = "useFullName";

    private static String GET_USER_BY_EMAIL_REQUEST = String.format("Select * from %s where %s = ?", USER_TABLE_NAME, EMAIL_COLUMN_NAME);
    private static String BAN_USER_BY_ID_REQUEST = String.format("UPDATE %s set %s = 1 where %s = ?", USER_TABLE_NAME, BANNED_COLUMN_NAME, ID_COLUMN_NAME);
    private static String UNBAN_USER_BY_ID_REQUEST = String.format("UPDATE %s set %s = 0 where %s = ?", USER_TABLE_NAME, BANNED_COLUMN_NAME, ID_COLUMN_NAME);
    private static String UPDATE_USER_DETAILS_REQUEST = String.format("Update %s \nSet %s = ?, %s=?, %s=?, %s=?, %s=? where %s=?", USER_TABLE_NAME, USERNAME_COLUMN_NAME, LOCATION_COLUMN_NAME, EMAIL_COLUMN_NAME, AVATAR_COLUMN_NAME, EMAIL_NOTIFICATION_COLUMN_NAME, ID_COLUMN_NAME);
    private static String DELETE_USER_BY_ID = String.format("DELETE from %s where %s = ?", USER_TABLE_NAME, ID_COLUMN_NAME);
    private static String GET_USER_BY_ID_REQUEST = String.format("Select * from %s where %s  = ?", USER_TABLE_NAME, ID_COLUMN_NAME);
    private static String GET_BY_USERNAME_REQUEST = String.format("Select * from %s where %s = ?", USER_TABLE_NAME, USERNAME_COLUMN_NAME);
    private static String ADD_FAILED_ATTEMPT_REQUEST = String.format("Update %s set %s = %s +1 where %s = ?", USER_TABLE_NAME, FAILED_ATTEMPTS_COLUMN_NAME, FAILED_ATTEMPTS_COLUMN_NAME, ID_COLUMN_NAME);
    private static String RESET_FAILED_ATTEMP_REQUEST = String.format("Update %s set %s = 0 where %s = ?", USER_TABLE_NAME, FAILED_ATTEMPTS_COLUMN_NAME, ID_COLUMN_NAME);
    private static String REGISTER_USER_REQUEST = String.format("Insert into %s (%s, %s,%s,%s,%s,%s,%s,%s) VALUES(?,?,?,?,?,?,?,?)",
            USER_TABLE_NAME
            , USERNAME_COLUMN_NAME
            , PASSWORD_HASH_COLUMN_NAME
            , PASSWORD_SALT_COLUMN_NAME
            , LOCATION_COLUMN_NAME
            , EMAIL_COLUMN_NAME
            , AVATAR_COLUMN_NAME
            , PATREON_CONTRIBUTION
            , USE_FULL_NAME);
    private static String ACTIVATE_USER_REQUEST = String.format("Update %s set %s = true where %s = ?"
            , USER_TABLE_NAME
            , IS_ACTIVATED_COLUMN_NAME
            , ID_COLUMN_NAME);
    private static String UPDATE_PASSWORD_REQUEST = String.format("Update %s set %s = ?, %s = ? where %s = ?", USER_TABLE_NAME, PASSWORD_HASH_COLUMN_NAME, PASSWORD_SALT_COLUMN_NAME, ID_COLUMN_NAME);
    private static String UPDATE_PATRON_STATUS_REQUEST = String.format("Update %s set %s = ?, %s = ? where %s = ?", USER_TABLE_NAME, IS_PATRON_COLUMN_NAME, PATREON_CONTRIBUTION, ID_COLUMN_NAME);
    private static String UPDATE_PATREON_TOKEN_REQUEST = String.format("Update %s set %s = ?, %s = ? where %s = ?", USER_TABLE_NAME, PATREON_TOKEN_COLUMN_NAME, PATREON_REFRESH_TOKEN_COLUMN_NAME, ID_COLUMN_NAME);
    private static String GET_ALL_USER = String.format("Select * from %s", USER_TABLE_NAME);

    private ConnectionPoolManager connectionPoolManager;
    private ConnectionManager connectionManager;
    private DBConnection dbConnection;
    private DSLContext context;
    private SessionRepository sessionRepository;
    private Connection connection;
    private PreparedStatement updatePatreonTokenStatement;
    private PreparedStatement getAllUserStatement;
    private PreparedStatement updatePatronStatusStatement;
    private PreparedStatement updatePasswordStatement;
    private PreparedStatement activateUserStatement;
    private PreparedStatement registerUserStatement;
    private PreparedStatement resetFailedAttemptsStatement;
    private PreparedStatement getByEmailStatement;
    private PreparedStatement getByUsernameStatement;
    private PreparedStatement addFailedAttemptsStatement;
    private PreparedStatement getUserByIdStatement;
    private PreparedStatement banUserByIdStatement;
    private PreparedStatement unbanUserByIdStatement;
    private PreparedStatement deleteUserByIdStatement;
    /**
     * Arguments:
     * 1: Username
     * 2: Location
     * 3: email
     * 4: Avatar
     * 5: emailNotification
     * 6: userId
     */
    private PreparedStatement updateUserDetailsStatement;

    public UserRepository(ConnectionManager connectionManager, ConnectionPoolManager connectionPoolManager, SessionRepository sessionRepository) throws SQLException {
        this.connectionManager = connectionManager;
        this.connectionPoolManager = connectionPoolManager;
        this.sessionRepository = sessionRepository;
        this.initConnection();
    }

    private void initConnection() throws SQLException {
        this.dbConnection = this.connectionPoolManager.getConnection();
        this.connection = this.dbConnection.getConnection();
        this.context = DSLContextUtil.getContext(this.connection);
        this.initStatements();
    }

    private void initStatements() throws SQLException {
        this.getByEmailStatement = this.connection.prepareStatement(GET_USER_BY_EMAIL_REQUEST);
        this.banUserByIdStatement = this.connection.prepareStatement(BAN_USER_BY_ID_REQUEST);
        this.unbanUserByIdStatement = this.connection.prepareStatement(UNBAN_USER_BY_ID_REQUEST);
        this.updateUserDetailsStatement = this.connection.prepareStatement(UPDATE_USER_DETAILS_REQUEST);
        this.deleteUserByIdStatement = this.connection.prepareStatement(DELETE_USER_BY_ID);
        this.getUserByIdStatement = this.connection.prepareStatement(GET_USER_BY_ID_REQUEST);
        this.getByUsernameStatement = this.connection.prepareStatement(GET_BY_USERNAME_REQUEST);
        this.addFailedAttemptsStatement = this.connection.prepareStatement(ADD_FAILED_ATTEMPT_REQUEST);
        this.resetFailedAttemptsStatement = this.connection.prepareStatement(RESET_FAILED_ATTEMP_REQUEST);
        this.registerUserStatement = this.connection.prepareStatement(REGISTER_USER_REQUEST);
        this.activateUserStatement = this.connection.prepareStatement(ACTIVATE_USER_REQUEST);
        this.updatePasswordStatement = this.connection.prepareStatement(UPDATE_PASSWORD_REQUEST);
        this.updatePatronStatusStatement = this.connection.prepareStatement(UPDATE_PATRON_STATUS_REQUEST);
        this.updatePatreonTokenStatement = this.connection.prepareStatement(UPDATE_PATREON_TOKEN_REQUEST);
        this.getAllUserStatement = this.connection.prepareStatement(GET_ALL_USER);
    }

    public List<UserDto> getAll() throws SQLException {
        if (this.connection.isClosed()) {
           this.initConnection();
        }
        ResultSet rs = this.getAllUserStatement.executeQuery();
        List<UserDto> users = new ArrayList<>();
        while (rs.next()) {
            users.add(new UserDto(rs));
        }
        return users;
    }

    public UserDto getByUserName(String username) throws SQLException {
        if (this.connection.isClosed()) {
            this.initConnection();
        }
        this.getByUsernameStatement.setString(1, username);
        ResultSet rs = this.getByUsernameStatement.executeQuery();
        UserDto user = null;
        if (rs.next()) {
            user = new UserDto(rs);
        }
        return user;
    }

    public void addFailedAttempt(int userId) throws SQLException {
        if (this.connection.isClosed()) {
            this.initConnection();
        }
        this.addFailedAttemptsStatement.setInt(1, userId);
        this.addFailedAttemptsStatement.executeUpdate();
    }

    public void resetAttempts(int userId) throws SQLException {
        if (this.connection.isClosed()) {
            this.initConnection();
        }
        this.resetFailedAttemptsStatement.setInt(1, userId);
        this.resetFailedAttemptsStatement.executeUpdate();
    }

    public UserDto getByEmail(String email) throws SQLException {
        if (this.connection.isClosed()) {
            this.initConnection();
        }
        UserDto user = null;
        getByEmailStatement.setString(1, email);
        ResultSet rs = getByEmailStatement.executeQuery();
        if (rs.next()) {
            user = new UserDto(rs);
        }
        return user;
    }

    public void registerUser(UserDto user) throws SQLException {
        if (this.connection.isClosed()) {
            this.initConnection();
        }
        this.registerUserStatement.setString(1, user.username);
        this.registerUserStatement.setString(2, user.getPassword());
        this.registerUserStatement.setString(3, user.getSalt());
        this.registerUserStatement.setString(4, user.location);
        this.registerUserStatement.setString(5, user.email);
        this.registerUserStatement.setString(6, user.avatar);
        this.registerUserStatement.setInt(7, user.patreonContribution);
        this.registerUserStatement.setBoolean(8, false);
        this.registerUserStatement.executeUpdate();
    }

    public boolean activateUser(int userId) throws SQLException {
        if (this.connection.isClosed()) {
            this.initConnection();
        }
        this.activateUserStatement.setInt(1, userId);
        int result = this.activateUserStatement.executeUpdate();
        return result == 1;
    }

    public UserDto updateUserPassword(int userId, String hash, String salt) throws SQLException {
        if (this.connection.isClosed()) {
            this.initConnection();
        }
        this.updatePasswordStatement.setString(1, hash);
        this.updatePasswordStatement.setString(2, salt);
        this.updatePasswordStatement.setInt(3, userId);
        int numberOfAffectedRows = this.updatePasswordStatement.executeUpdate();
        return getById(userId);
    }

    public boolean isActivated(String sessionId) throws SQLException {
        if (this.connection.isClosed()) {
            this.initConnection();
        }
        boolean isActivated = false;
        try {
            UserDto user = getBySessionId(sessionId);
            if (user != null) {
                isActivated = user.isActivated;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isActivated;
    }

    public UserDto updateUser(UserDto user) throws SQLException {
        if (this.connection.isClosed()) {
            this.initConnection();
        }
        this.context.update(User.USER).set(User.USER.USERNAME, user.username)
            .set(User.USER.LOCATION, user.location)
            .set(User.USER.EMAIL, user.email)
            .set(User.USER.AVATAR, user.avatar)
            .set(User.USER.SUBSCRIBED_EMAIL_NOTIFICATION, user.isSubscribedToEmailNotifications)
            .set(User.USER.FULLNAME, user.fullname)
            .set(User.USER.USEFULLNAME, user.useFullName)
            .where(User.USER.USERID.eq(Long.valueOf(user.userId)))
            .execute();
        return getById(user.userId);
    }

    public synchronized UserDto getById(int userId) throws SQLException {
        if (this.connection.isClosed()) {
            this.initConnection();
        }
        String request = "Select * from user where userId = '" + userId + "'";
        ResultSet rs = this.connectionManager.query(request);
        UserDto user = null;
        if (rs.next()) {
            user = new UserDto(rs);
        }
        return user;
    }

    public synchronized UserDto getBySessionId(String sessionId) throws SQLException {
        if (this.connection.isClosed()) {
            this.initConnection();
        }
        UserDto user = null;
        SessionDto sessionDto = this.sessionRepository.getSession(sessionId);
        if (sessionDto != null) {
            user = getById(sessionDto.userId);
        }
        return user;
    }

    //TODO: Return a boolean the string is just a proxy for a true false status anyway and make it so this method usage is more error prone
    public synchronized String updatePatronStatus(int userId, boolean patronStatus, int patreonContribution) throws SQLException {
        if (this.connection.isClosed()) {
            this.initConnection();
        }
        String response = "User updated";
        this.updatePatronStatusStatement.setBoolean(1, patronStatus);
        this.updatePatronStatusStatement.setInt(2, patreonContribution);
        this.updatePatronStatusStatement.setInt(3, userId);

        try {
            int rowsAffected = this.updatePatronStatusStatement.executeUpdate();
            if (rowsAffected != 1) {
                response = "there was a problem updating the user";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return response;
    }

    public synchronized boolean updateUserPatreonTokens(String oauthToken, String refreshToken, int userId) throws SQLException {
        if (this.connection.isClosed()) {
            this.initConnection();
        }
        this.updatePatreonTokenStatement.setString(1, oauthToken);
        this.updatePatreonTokenStatement.setString(2, refreshToken);
        this.updatePatreonTokenStatement.setInt(3, userId);
        int rowsAffected = this.updatePatreonTokenStatement.executeUpdate();
        if (rowsAffected != 1) {
            return false;
        }
        return true;
    }

    public synchronized boolean banUserById(int userId) throws SQLException {
        if (this.connection.isClosed()) {
            this.initConnection();
        }
        int rowsUpdated = 0;
        try {
            banUserByIdStatement.setInt(1, userId);
            rowsUpdated = banUserByIdStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rowsUpdated == 1;
    }

    public synchronized boolean unbanUserById(int userId) throws SQLException {
        if (this.connection.isClosed()) {
            this.initConnection();
        }
        int rowsUpdated = 0;
        unbanUserByIdStatement.setInt(1, userId);
        rowsUpdated = unbanUserByIdStatement.executeUpdate();
        return rowsUpdated == 1;
    }

    public  boolean deleteUserById(int userId) {
        int rowUpdated = 0;
        rowUpdated = this.context.deleteFrom(USER).where(USER.USERID.eq(Long.valueOf(userId))).execute();
        return rowUpdated == 1;
    }

    public List<Long> getAllUnverifiedUsersOlderWithModifiedDateOlderThan14h() {
        return this.context.selectFrom(User.USER)
             .where(User.USER.MODIFIEDON.lessThan(DSL.timestampSub(DSL.currentTimestamp(), 7, DatePart.DAY)).and(USER.ISACTIVATED.eq(false)))
            .fetch(USER.USERID, Long.class);
    }


}
