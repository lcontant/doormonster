package API.Ressource;

import API.Model.Feedback;
import API.Model.UserDto;
import API.Util.Repositories.FeedbackRepository;
import API.Util.Repositories.UserRepository;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FeedbackWithUser {
    public Feedback feedback;
    public UserDto user;

    public FeedbackWithUser(ResultSet rs) throws SQLException {
        this.feedback = new Feedback();
        this.user = new UserDto();
        user.userId = rs.getInt( UserRepository.ID_COLUMN_NAME);
        user.username = rs.getString(UserRepository.USERNAME_COLUMN_NAME);
        user.email = rs.getString( UserRepository.EMAIL_COLUMN_NAME);
        user.isActivated = rs.getBoolean( UserRepository.IS_ACTIVATED_COLUMN_NAME);
        user.avatar = rs.getString( UserRepository.AVATAR_COLUMN_NAME);
        user.location = rs.getString( UserRepository.LOCATION_COLUMN_NAME);
        user.isSubscribedToEmailNotifications = rs.getBoolean( UserRepository.EMAIL_NOTIFICATION_COLUMN_NAME);
        this.feedback.content = rs.getString(FeedbackRepository.CONTENT_COLUMN_NAME);
        this.feedback.userId = rs.getInt(FeedbackRepository.USER_ID_COLUMN_NAME);
        this.feedback.id = rs.getInt(FeedbackRepository.ID_COLUMN_NAME);
    }
}
