package API.Model;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity
public class Comment {
    @OneToOne
    @JoinColumn(name="userId")
    public UserDto author;
    @Column(name = "text")
    public String text;
    @Column(name = "mediaId")
    public String mediaId;
    @Id
    public Integer commentId;
    @Column(name = "userId")
    public int userId;

    public int parentCommentId;
    @Column(name= "score")
    public int score;
    @OneToMany(targetEntity = Comment.class, mappedBy = "Comments")
    @JoinColumn(name="parentCommentId")
    public List<Comment> replies;
    @Column(name= "createdOn")
    public Date createdOn;
    @Column(name = "modifiedOn")
    public Date modifiedOn;

    public Comment() {
    }

    public Comment(int commentId, int userId, UserDto author, String mediaId, int parentCommentId, String text, int score, Date createdOn, Date modifiedOn) {
        this.commentId = commentId;
        this.userId = userId;
        this.author = author;
        this.mediaId = mediaId;
        this.parentCommentId = parentCommentId;
        this.text = text;
        this.score = score;
        this.createdOn = createdOn;
        this.modifiedOn = modifiedOn;
        this.replies = new ArrayList<>();
    }

    public void addReply(Comment reply) {
        this.replies.add(reply);
    }

    public void addReplies(List<Comment> replies) {
        this.replies.addAll(replies);
    }

    public Comment(ResultSet rs) throws SQLException  {
        this(rs.getInt("commentid"),
                rs.getInt("userId"),
                new UserDto(rs),
                rs.getString("mediaid"),
                rs.getInt("parent_comment_id"),
                rs.getString("text"),
                rs.getInt("score"),
                rs.getDate("CreatedOn"),
                rs.getDate("modifiedOn")
                );
    }
}
