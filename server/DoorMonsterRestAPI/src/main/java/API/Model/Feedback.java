package API.Model;

import API.Util.Repositories.FeedbackRepository;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Feedback {
    public int userId;
    public int id;
    public String content;

    public Feedback() { }

    public Feedback(int userId, int id, String content) {
        this.userId = userId;
        this.id = id;
        this.content = content;
    }

    public Feedback(ResultSet resultSet) throws SQLException {
        this(resultSet.getInt(FeedbackRepository.USER_ID_COLUMN_NAME)
                , resultSet.getInt(FeedbackRepository.ID_COLUMN_NAME)
                , resultSet.getString(FeedbackRepository.CONTENT_COLUMN_NAME));
    }
}
