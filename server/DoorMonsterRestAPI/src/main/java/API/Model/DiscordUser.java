package API.Model;

import javax.persistence.Column;
import javax.persistence.Id;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DiscordUser {
    public static final String TABLE_NAME = "DISCORD_USERS";
    public static final String ID_COLUMN_NAME = "ID";
    public static final String USER_ID_COLUMN_NAME = "USER_ID";
    public static final String DISCORD_ID_COLUMN_NAME = "DISCORD_ID";
    public static final String DISCORD_TOKEN_COLUMN_NAME = "DISCORD_TOKEN";
    public static final String DISCORD_USERNAME_COLUMN_NAME = "DISCORD_USERNAME";
    public static final String DISCORD_REFRESH_TOKEN_COLUMN_NAME = "DISCORD_REFRESH_TOKEN";
    @Id
    public int id;
    @Column(name = "USER_ID")
    public int userId;
    @Column(name = "DISCORD_ID")
    public String discordId;
    @Column(name = "DISCORD_TOKEN")
    public String discordToken;
    @Column(name = "DISCORD_USERNAME")
    public String discordUsername;
    @Column(name = "DISCORD_REFRESH_TOKEN")
    public String discordRefreshToken;

    public DiscordUser() {

    }

    public DiscordUser(int id, int userId, String discordId, String discordToken, String discordUsername, String discordRefreshToken) {
        this.id = id;
        this.userId = userId;
        this.discordId = discordId;
        this.discordToken = discordToken;
        this.discordUsername = discordUsername;
        this.discordRefreshToken = discordRefreshToken;
    }

    public DiscordUser(ResultSet resultSet) throws SQLException {
        this(
                resultSet.getInt(ID_COLUMN_NAME),
                resultSet.getInt(USER_ID_COLUMN_NAME),
                resultSet.getString(DISCORD_ID_COLUMN_NAME),
                resultSet.getString(DISCORD_TOKEN_COLUMN_NAME),
                resultSet.getString(DISCORD_USERNAME_COLUMN_NAME),
                resultSet.getString(DISCORD_REFRESH_TOKEN_COLUMN_NAME)
        );

    }
}
