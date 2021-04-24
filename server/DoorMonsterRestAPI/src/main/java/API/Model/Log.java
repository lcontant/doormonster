package API.Model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class Log {
    public static String TABLE_NAME = "LOG";
    public static String ID_COLUMN_NAME = "ID";
    public static String USER_COLUMN_NAME = "USER_ID";
    public static String MESSAGE_COLUMN_NAME = "MESSAGE";
    public static String TIME_COLUMN_NAME = "TIME";

    public int id;
    public int user_id;
    public String message;
    public Date time;


    public Log(int id, int user_id, String message, Date time) {
        this.id = id;
        this.user_id = user_id;
        this.message = message;
        this.time = time;
    }

    public Log(ResultSet resultSet) throws SQLException {
        this(
                resultSet.getInt(ID_COLUMN_NAME),
                resultSet.getInt(USER_COLUMN_NAME),
                resultSet.getString(MESSAGE_COLUMN_NAME),
                resultSet.getDate(TIME_COLUMN_NAME)
        );
    }

    public Log() {

    }
}
