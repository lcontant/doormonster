package API.Model;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Vote {
    public boolean isUpVote;
    public int userId;
    public int commentId;

    public Vote() {
    }

    public Vote(boolean isUpVote, int userId, int commentId) {
        this.isUpVote = isUpVote;
        this.userId = userId;
        this.commentId = commentId;
    }

    public Vote(ResultSet rs) throws SQLException {
        this(rs.getBoolean("isUpVote")
                ,rs.getInt("userid")
                , rs.getInt("commentid"));
    }
}
