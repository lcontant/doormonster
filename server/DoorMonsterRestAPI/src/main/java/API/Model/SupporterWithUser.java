package API.Model;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SupporterWithUser {
    Supporter supporter;
    UserDto user;

    public SupporterWithUser(Supporter supporter, UserDto user) {
        this.supporter = supporter;
        this.user = user;
    }

    public SupporterWithUser(ResultSet resultSet) throws SQLException {
        this(
                new Supporter(resultSet),
                new UserDto(resultSet)
        );
    }
}
