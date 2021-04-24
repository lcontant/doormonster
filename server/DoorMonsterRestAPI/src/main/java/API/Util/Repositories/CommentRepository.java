package API.Util.Repositories;

import API.Model.*;
import API.Util.SQLConnector.ConnectionManager;
import API.Util.SQLConnector.ConnectionPoolManager;
import API.Util.SQLConnector.DSLContextUtil;
import API.databases.tables.Comments;
import API.databases.tables.User;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import org.jooq.DSLContext;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class CommentRepository {

    public static String TABLE_NAME = "comments";
    public static String ID_COLUMN_NAME = "commentid";
    public static String TEXT_COLUMN_NAME = "text";
    public static String USER_ID_COLUMN_NAME = "userid";
    public static String MEDIA_ID_COLUMN_NAME = "mediaid";
    public static String PARENT_COMMENT_ID_COLUMN_NAME = "parent_comment_id";
    public static String CREATED_ON_COLUMN_NAME = "createdOn";
    public static String SCORE_COLUMN_NAME = "score";

    private static String BASIC_GET_ALL_INFO = "Select * \n" +
            "from comments as c\n" +
            "join `user` as u on u.userId = c.userid";
    private static String GET_COMMENTS_BY_ID_REQUEST = String.format("%s where c.%s = ?", BASIC_GET_ALL_INFO, ID_COLUMN_NAME);
    private static String GET_COMMENTS_FOR_VIDEO_REQUEST = String.format("%s where c.%s = ? AND c.%s IS NULL", BASIC_GET_ALL_INFO, MEDIA_ID_COLUMN_NAME, PARENT_COMMENT_ID_COLUMN_NAME);
    private static String GET_REPLIES_FOR_COMMENT_REQUEST = String.format("%s where c.%s = ?", BASIC_GET_ALL_INFO, PARENT_COMMENT_ID_COLUMN_NAME);
    private static String INSERT_COMMENT_WITH_PARENT_COMMENT_ID = String.format("Insert into comments (%s, %s, %s,%s) VALUES(?, ?, ?, ?)"
            , TEXT_COLUMN_NAME, USER_ID_COLUMN_NAME, MEDIA_ID_COLUMN_NAME, PARENT_COMMENT_ID_COLUMN_NAME);
    private static String INSERT_COMMENT_WITHOUT_PARENT_COMMENT_ID = String.format("Insert into comments (%s, %s, %s) VALUES(" +
            "?, ?, ?)", TEXT_COLUMN_NAME, USER_ID_COLUMN_NAME, MEDIA_ID_COLUMN_NAME);
    private static String UPDATE_COMMENT_TEXT_REQUEST = String.format("Update %s Set %s = ? where %s = ?", TABLE_NAME, TEXT_COLUMN_NAME, ID_COLUMN_NAME);
    private static String UPVOTE_COMMENT_REQUEST = String.format("Update %s set %s=%s+1 where %s=?", TABLE_NAME, SCORE_COLUMN_NAME, SCORE_COLUMN_NAME, ID_COLUMN_NAME);
    private static String DOWNVOTE_COMMENT_REQUEST = String.format("Update %s set %s=%s-1 where %s=?", TABLE_NAME, SCORE_COLUMN_NAME, SCORE_COLUMN_NAME, ID_COLUMN_NAME);
    private static String DELETE_COMMENT_REQUEST = String.format("Delete from %s where %s = ?", TABLE_NAME, ID_COLUMN_NAME);
    private static String DELETE_COMMENT_REPLIES_REQUEST = String.format("Delete from %s where %s  = ?", TABLE_NAME, PARENT_COMMENT_ID_COLUMN_NAME);
    private static String GET_REPLIES_FOR_VIDEO_REQUEST = String.format("%s where c.%s = ? AND c.%s IS NOT NULL", BASIC_GET_ALL_INFO, MEDIA_ID_COLUMN_NAME, PARENT_COMMENT_ID_COLUMN_NAME);
    private static String GET_COMMENT_FOR_USER_FOR_VIDEO_REQUEST = String.format("%s " +
                    "join %s as v on v.%s = c.%s " +
                    "where c.%s = ? AND c.%s = ? AND c.%s = ? AND c.%s >= DATE_ADD(CURDATE(), INTERVAL -15 MINUTE);"
            , BASIC_GET_ALL_INFO
            , VideoRepository.TABLE_NAME
            , VideoRepository.VIMEO_ID_COLUMN_NAME
            , MEDIA_ID_COLUMN_NAME
            , MEDIA_ID_COLUMN_NAME
            , USER_ID_COLUMN_NAME
            , TEXT_COLUMN_NAME
            , CREATED_ON_COLUMN_NAME);
    private static String GET_COMMENTS_FOR_USER = String.format("%s " +
            "join %s as v on v.%s = c.%s " +
            "where c.%s = ? and c.%s IS NULL"
            , BASIC_GET_ALL_INFO
            , VideoRepository.TABLE_NAME
            , VideoRepository.VIMEO_ID_COLUMN_NAME
            , MEDIA_ID_COLUMN_NAME
            , USER_ID_COLUMN_NAME
            , PARENT_COMMENT_ID_COLUMN_NAME);
    private static String GET_REPLIES_FOR_USER = String.format("select r.*, v.*, u.* from %s as c" +
                    " join %s as r on r.%s = c.%s " +
                    " join %s as u on u.%s = r.%s" +
                    " join %s as v on v.%s = r.%s" +
                    " where c.%s = ?"
            , TABLE_NAME
            , TABLE_NAME, PARENT_COMMENT_ID_COLUMN_NAME, ID_COLUMN_NAME
            , UserRepository.USER_TABLE_NAME, UserRepository.ID_COLUMN_NAME, USER_ID_COLUMN_NAME
            , VideoRepository.TABLE_NAME, VideoRepository.VIMEO_ID_COLUMN_NAME, MEDIA_ID_COLUMN_NAME
            , USER_ID_COLUMN_NAME);
    ConnectionManager connectionManager;
    VoteRepository voteRepository;
    UserRepository userRepository;
    RoleRepository roleRepository;
    ConnectionPoolManager connectionPoolManager;
    Connection connection;
    DSLContext create;
    DBConnection dbConnection;

    private PreparedStatement getCommentByIdStatement;
    private PreparedStatement getCommentsForVideoStatement;
    private PreparedStatement getRepliesForCommentStatement;
    private PreparedStatement inserCommentWithParentCommetnStatement;
    private PreparedStatement insertCommentWithoutParentCommentStatement;
    private PreparedStatement updateCommentTextStatement;
    private PreparedStatement upvoteCommentStatement;
    private PreparedStatement downvoteCommentStatement;
    private PreparedStatement deleteCommentStatement;
    private PreparedStatement deleteCommentRepliesStatement;
    private PreparedStatement getRepliesForVideoStatement;
    private PreparedStatement getCommentForUserForVideoStatement;
    private PreparedStatement getCommentForUserStatement;
    private PreparedStatement getRepliesToUserCommentsStatement;

    public CommentRepository(ConnectionManager connectionManager
            , VoteRepository voteRepository
            , UserRepository userRepository
            , ConnectionPoolManager connectionPoolManager
            , RoleRepository roleRepository) throws SQLException {
        this.connectionManager = connectionManager;
        this.voteRepository = voteRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.connectionPoolManager = connectionPoolManager;
        this.initConnection();
    }

    private void initConnection() throws SQLException {
        this.dbConnection = this.connectionPoolManager.getConnection();
        this.connection = this.dbConnection.getConnection();
        this.create = DSLContextUtil.getContext(this.connection);
        this.initStatements();
    }

    private void initStatements() throws SQLException {
        this.getCommentByIdStatement = this.connection.prepareStatement(GET_COMMENTS_BY_ID_REQUEST);
        this.getCommentsForVideoStatement = this.connection.prepareStatement(GET_COMMENTS_FOR_VIDEO_REQUEST);
        this.getRepliesForCommentStatement = this.connection.prepareStatement(GET_REPLIES_FOR_COMMENT_REQUEST);
        this.inserCommentWithParentCommetnStatement = this.connection.prepareStatement(INSERT_COMMENT_WITH_PARENT_COMMENT_ID);
        this.insertCommentWithoutParentCommentStatement = this.connection.prepareStatement(INSERT_COMMENT_WITHOUT_PARENT_COMMENT_ID);
        this.updateCommentTextStatement = this.connection.prepareStatement(UPDATE_COMMENT_TEXT_REQUEST);
        this.upvoteCommentStatement = this.connection.prepareStatement(UPVOTE_COMMENT_REQUEST);
        this.downvoteCommentStatement = this.connection.prepareStatement(DOWNVOTE_COMMENT_REQUEST);
        this.deleteCommentStatement = this.connection.prepareStatement(DELETE_COMMENT_REQUEST);
        this.deleteCommentRepliesStatement = this.connection.prepareStatement(DELETE_COMMENT_REPLIES_REQUEST);
        this.getRepliesForVideoStatement = this.connection.prepareStatement(GET_REPLIES_FOR_VIDEO_REQUEST);
        this.getCommentForUserStatement = this.connection.prepareStatement(GET_COMMENTS_FOR_USER);
        this.getRepliesToUserCommentsStatement = this.connection.prepareStatement(GET_REPLIES_FOR_USER);
        this.getCommentForUserForVideoStatement = this.connection.prepareStatement(GET_COMMENT_FOR_USER_FOR_VIDEO_REQUEST);
    }

    public synchronized Comment getById(int commentId) throws SQLException, ParseException {
        validateConnection();
        Comment returnComment = null;
        this.getCommentByIdStatement.setInt(1, commentId);
        ResultSet rs = this.getCommentByIdStatement.executeQuery();
        if (rs.next()) {
            returnComment = new Comment(rs);
        }
        return returnComment;
    }

    public synchronized List<Comment> getCommentsForMedia(String mediaId) throws SQLException, ParseException {
        validateConnection();
        List<Comment> comments = new ArrayList<>();
        List<Comment> replies = this.getRepliesForVideo(mediaId);
        HashMap<Integer, Comment> commentMap = new HashMap<>();
        this.getCommentsForVideoStatement.setString(1, mediaId);
        ResultSet rs = this.getCommentsForVideoStatement.executeQuery();
        while (rs.next()) {
            Comment comment = new Comment(rs);
            commentMap.put(comment.commentId, comment);
            comments.add(comment);
        }
        for (Comment reply : replies) {
            commentMap.get(reply.parentCommentId).addReply(reply);
        }
        return comments;
    }

    private synchronized  List<Comment> getRepliesForVideo(String videoId) throws SQLException, ParseException {
        validateConnection();
        List<Comment> comments = new ArrayList<>();
        this.getRepliesForVideoStatement.setString(1, videoId);
        ResultSet rs = this.getRepliesForVideoStatement.executeQuery();
        while (rs.next()) {
            comments.add(new Comment(rs));
        }
        return comments;
    }



    @Cacheable(value = "comments", key="#commentId")
    public synchronized  List<Comment> getRepliesForComment(int commentId) throws SQLException, ParseException {
        validateConnection();
        List<Comment> replies = new ArrayList<>();
        synchronized (this.getRepliesForCommentStatement) {
            this.getRepliesForCommentStatement.setInt(1, commentId);
            ResultSet rs = this.getRepliesForCommentStatement.executeQuery();
            while (rs.next()) {
                replies.add(new Comment(rs));
            }
        }
        return replies;
    }


    public synchronized boolean canEditComment(int commentId, String sessionId) throws SQLException, ParseException {
        validateConnection();
        Comment comment = getById(commentId);
        boolean canEdit = false;
        try {
            UserDto user = this.userRepository.getBySessionId(sessionId);
            Role role = this.roleRepository.getRoleByUserId(user.userId);
            if (user.userId == comment.userId && user.isActivated || role != null && role.ranking == 0) {
                canEdit = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return canEdit;
    }

    public synchronized boolean insertComment(Comment comment) throws SQLException, ParseException {
        validateConnection();
        int numberOfRowsAffected = 0;
        if (comment.parentCommentId != 0) {
            Comment parentComment = this.getById(comment.parentCommentId);
            if (parentComment.parentCommentId != 0) {
                comment.parentCommentId = parentComment.parentCommentId;
            }
            this.inserCommentWithParentCommetnStatement.setString(1, comment.text);
            this.inserCommentWithParentCommetnStatement.setInt(2, comment.userId);
            this.inserCommentWithParentCommetnStatement.setString(3, comment.mediaId);
            this.inserCommentWithParentCommetnStatement.setInt(4, comment.parentCommentId);
            numberOfRowsAffected = this.inserCommentWithParentCommetnStatement.executeUpdate();
        } else {
            this.insertCommentWithoutParentCommentStatement.setString(1, comment.text);
            this.insertCommentWithoutParentCommentStatement.setInt(2, comment.userId);
            this.insertCommentWithoutParentCommentStatement.setString(3, comment.mediaId);
            numberOfRowsAffected = this.insertCommentWithoutParentCommentStatement.executeUpdate();
        }
        return numberOfRowsAffected == 1;
    }

    public synchronized boolean deleteComment(int commentId) throws SQLException {
        validateConnection();
        this.voteRepository.deleteVotesOnComment(commentId);
        int numberOfRowsAffected = 0;
        this.deleteCommentRepliesStatement.setInt(1, commentId);
        this.deleteCommentRepliesStatement.executeUpdate();
        this.deleteCommentStatement.setInt(1, commentId);
        numberOfRowsAffected = this.deleteCommentStatement.executeUpdate();
        return numberOfRowsAffected == 1;
    }

    public  synchronized Comment updateComment(int commentId, String newText) throws SQLException, ParseException {
        validateConnection();
        int numberOfRowsAffected = 0;
        this.updateCommentTextStatement.setString(1, newText);
        this.updateCommentTextStatement.setInt(2, commentId);
        numberOfRowsAffected = this.updateCommentTextStatement.executeUpdate();
        return getById(commentId);
    }

    public synchronized boolean UpvoteComment(int commentId) throws SQLException {
        validateConnection();
        boolean hasUpvoted = false;
        int rowsAffected;
        this.upvoteCommentStatement.setInt(1, commentId);
        rowsAffected = this.upvoteCommentStatement.executeUpdate();
        hasUpvoted = rowsAffected == 1;
        if (!hasUpvoted && rowsAffected > 1) {
            downVoteComment(commentId);
        }
        return hasUpvoted;
    }

    public synchronized boolean downVoteComment(int commentId) throws SQLException {
        validateConnection();
        boolean hasUpvoted = false;
        synchronized (this.downvoteCommentStatement) {
            this.downvoteCommentStatement.setInt(1, commentId);
            int rowsAffected = downvoteCommentStatement.executeUpdate();
            hasUpvoted = rowsAffected == 1;
            if (!hasUpvoted && rowsAffected > 1) {
                UpvoteComment(commentId);
            }
        }
        return hasUpvoted;
    }

    public synchronized boolean isDuplicateComment(Comment comment) throws SQLException {
        validateConnection();
        boolean isDuplicate = false;
        this.getCommentForUserForVideoStatement.setString(1, comment.mediaId);
        this.getCommentForUserForVideoStatement.setInt(2, comment.userId);
        this.getCommentForUserForVideoStatement.setString(3, comment.text);
        ResultSet rs = this.getCommentForUserForVideoStatement.executeQuery();
        if (rs.next()) {
           isDuplicate = true;
        }
        return isDuplicate;
    }

    private void validateConnection() throws SQLException {
        if (this.connection.isClosed()) {
            this.initConnection();
        }
    }

    public synchronized List<UserPageComment> getCommentsForUser(int userId) throws SQLException  {
        if (this.connection.isClosed()) {
            this.initConnection();
        }
        HashMap<Integer, UserPageComment> commentsHashMap = new HashMap<>();
        List<UserPageComment> comments = new ArrayList<>();
        this.getCommentForUserStatement.setInt(1, userId);
        ResultSet commentsResultSet = this.getCommentForUserStatement.executeQuery();
        this.getRepliesToUserCommentsStatement.setInt(1, userId);
        ResultSet repliesResultSet = this.getRepliesToUserCommentsStatement.executeQuery();
        while (commentsResultSet.next()) {
            UserPageComment comment = new UserPageComment(commentsResultSet);
            commentsHashMap.put(comment.commentId, comment);
        }
        while (repliesResultSet.next()) {
            UserPageComment reply = new UserPageComment(repliesResultSet);
            commentsHashMap.get(reply.parentCommentId).replies.add(reply);
        }
        for (int key: commentsHashMap.keySet()) {
           comments.add(commentsHashMap.get(key));
        }
        return comments;
    }

    public List<Comment> getTopCommentsOfTheWeek() {
        User author = User.USER.as("author");
         Map<Comment, List<UserDto>> result = this.create.selectFrom(Comments.COMMENTS.join(author).on(Comments.COMMENTS.USERID.eq(author.USERID)))
            .where(Comments.COMMENTS.CREATEDON.gt(new Timestamp(Instant.now().minus(7, ChronoUnit.DAYS).toEpochMilli())))
            .fetchGroups(Comment.class, UserDto.class);
         List<Comment> comments = new ArrayList<>();
        result.forEach((comment, userDtos) -> {
            comment.author = userDtos.get(0);
            comments.add(comment);
        });
        comments.sort((o1, o2) -> o1.score - o2.score);
        return comments;
    }

}
