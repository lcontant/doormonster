package API.Model;

import API.Util.Repositories.UserRepository;

import API.databases.tables.User;
import com.github.jasminb.jsonapi.annotations.Id;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class UserDto {
    @Id
    public int userId;
    @Column(name = "username")
    public String username;
    @Column(name = "location")
    public String location;
    @Column(name = "email")
    public String email;
    @Column(name = "avatar")
    public String avatar;
    @Column(name = "patreonToken")
    private String patreonToken;
    @Column(name = "fullname")
    public String fullname;
    @Column(name = "useFullname")
    public boolean useFullName;
    @Column(name= "patreonRefreshToken")
    private String patreonRefreshToken;
    @Column(name="isActivated")
    public boolean isActivated;
    @Column(name="PATREON_CONTRIBUTION")
    public int patreonContribution;
    @Column(name="failedAttemps")
    public int failedAttemps;
    @Column(name="BANNED")
    public boolean isBanned;
    @Column(name = "SUBSCRIBED_EMAIL_NOTIFICATION")
    public boolean isSubscribedToEmailNotifications;

    @Column(name= "salt")
    private String salt;
    private String password;


    public UserDto() {

    }

    public UserDto(String username,
                String fullname,
                boolean useFullName,
                String password,
                String salt, String location,
                String email, int userId, boolean isActivated, int failedAttempts, int patreonContribution, String patreonToken,
                String patreonRefreshToken,
                boolean isBanned,
                boolean isSubscriedToEmailNotifications,
                String avatar) {
        this.username = username;
        this.useFullName = useFullName;
        this.fullname = fullname;
        this.password = password;
        this.location = location;
        this.email = email;
        this.salt = salt;
        this.userId = userId;
        this.isActivated = isActivated;
        this.failedAttemps = failedAttempts;
        this.patreonContribution = patreonContribution;
        this.patreonToken = patreonToken;
        this.patreonRefreshToken = patreonRefreshToken;
        this.isBanned = isBanned;
        this.isSubscribedToEmailNotifications = isSubscriedToEmailNotifications;
        this.avatar = avatar;
    }

    public UserDto(String username, String password) {
        this.username = username;
        this.password  = password;
    }

    public UserDto(String username, String password, String salt) {
        this.username= username;
        this.password = password;
        this.salt = salt;

    }

    public UserDto(ResultSet rs) throws SQLException {
        this(
                rs.getString(User.USER.USERNAME.getName())
                , rs.getString(User.USER.FULLNAME.getName())
                , rs.getBoolean(User.USER.USEFULLNAME.getName())
                , rs.getString(User.USER.PASSWORDHASH.getName())
                , rs.getString(User.USER.SALT.getName())
                , rs.getString(User.USER.LOCATION.getName())
                , rs.getString(User.USER.EMAIL.getName())
                , rs.getInt(User.USER.USERID.getName())
                , rs.getBoolean(User.USER.ISACTIVATED.getName())
                , rs.getInt(User.USER.FAILEDATTEMPTS.getName())
                , rs.getInt(User.USER.PATREON_CONTRIBUTION.getName())
                , rs.getString(User.USER.PATREONTOKEN.getName())
                , rs.getString(User.USER.PATREONREFRESHTOKEN.getName())
                , rs.getBoolean(User.USER.BANNED.getName())
                , rs.getBoolean(UserRepository.EMAIL_NOTIFICATION_COLUMN_NAME)
                , rs.getString(User.USER.AVATAR.getName())
        );
    }

    public String getPassword(){
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }



    public String getSalt() { return this.salt; }

    public void setSalt(String salt) {this.salt = salt;}

    public byte[] getEncodedSalt() {
        Base64.Decoder dec = Base64.getDecoder();
        return  dec.decode(this.salt);
    }


    public String getPatreonToken() {
        return patreonToken;
    }

    public String getPatreonRefreshToken() {
        return patreonRefreshToken;
    }
}
