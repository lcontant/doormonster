package API.Util.Repositories;

import API.Model.DiscordUser;
import API.Util.SQLConnector.ConnectionPoolManager;
import API.Util.SQLConnector.DSLContextUtil;
import org.jooq.DSLContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static API.Model.DiscordUser.*;
import static API.databases.Tables.DISCORD_USERS;

@Component
public class DiscordUserRepository {
    private static final String GET_DISCORD_USER_BY_ID = String.format("Select * from %s where %s = ?", TABLE_NAME, USER_ID_COLUMN_NAME);
    private static final String INSERT_DISCORD_USER = String.format("Insert into %s (%s, %s, %s, %s, %s) VALUES (?,?,?,?,?)", TABLE_NAME, USER_ID_COLUMN_NAME, DISCORD_TOKEN_COLUMN_NAME, DISCORD_USERNAME_COLUMN_NAME, DISCORD_ID_COLUMN_NAME, DISCORD_REFRESH_TOKEN_COLUMN_NAME);

    private ConnectionPoolManager connectionPoolManager;
    private Connection connection;

    private PreparedStatement getDiscordUserByIdStatement;
    private PreparedStatement insertDiscordUserStatement;

    private DSLContext context;

    public DiscordUserRepository(ConnectionPoolManager connectionPoolManager) throws SQLException {
        this.connectionPoolManager = connectionPoolManager;
        this.initConnection();
    }
    private void initConnection() throws SQLException {
        this.connection = this.connectionPoolManager.getConnection().getConnection();
        this.context = DSLContextUtil.getContext(this.connection);
        this.initStatements();
    }

    private void initStatements() throws SQLException {
        this.getDiscordUserByIdStatement = this.connection.prepareStatement(GET_DISCORD_USER_BY_ID);
        this.insertDiscordUserStatement = this.connection.prepareStatement(INSERT_DISCORD_USER);
    }

    public DiscordUser getDiscordUserById(int userId) throws SQLException {
        DiscordUser discordUser = null;
        ResultSet rs = null;
        this.getDiscordUserByIdStatement.setInt(1, userId);
        rs = this.getDiscordUserByIdStatement.executeQuery();
        if (rs.next()) {
            discordUser = new DiscordUser(rs);
        }
        return discordUser;
    }

    public boolean insertDiscordUser(DiscordUser discordUser) throws SQLException {
        this.insertDiscordUserStatement.setInt(1, discordUser.userId);
        this.insertDiscordUserStatement.setString(2, discordUser.discordToken);
        this.insertDiscordUserStatement.setString(3, discordUser.discordUsername);
        this.insertDiscordUserStatement.setString(4, discordUser.discordId);
        this.insertDiscordUserStatement.setString(5,discordUser.discordRefreshToken);
        return this.insertDiscordUserStatement.executeUpdate() == 1;
    }

    public List<DiscordUser> getAllDiscordUsers() {
        return this.context.selectFrom(DISCORD_USERS).fetchInto(DiscordUser.class);
    }
}
