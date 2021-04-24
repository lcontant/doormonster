package API.Model;

import API.Util.Repositories.VideoRepository;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserPageComment extends Comment {
    public String title;

    public UserPageComment() {
    }

    public UserPageComment(int commentId, int userId, UserDto author, String mediaId, int parentCommentId, String text, int score, Date createdOn, Date modifiedOn) {
        super(commentId, userId, author, mediaId, parentCommentId, text, score, createdOn, modifiedOn);
    }

    public UserPageComment(ResultSet rs) throws SQLException {
        super(rs);
        this.title = rs.getString(VideoRepository.VIDEO_TITLE_COLUM_NAME);
    }
}
