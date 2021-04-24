package API.Controllers;

import API.Model.*;
import API.Util.JSONMapper;
import API.Util.Repositories.CommentRepository;
import API.Util.Repositories.SessionRepository;
import API.Util.Repositories.UserRepository;
import API.Util.Repositories.VideoRepository;
import com.google.gson.reflect.TypeToken;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import software.amazon.awssdk.http.HttpStatusCode;

import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static java.util.Comparator.*;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class CommentControllerTest {

    @Autowired
    CommentController commentController;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    VideoRepository videoRepository;

    @Autowired
    SessionRepository sessionRepository;

    private UserDto user;
    private Video video;
    private SessionDto sessionDto;

    public CommentControllerTest( ) throws SQLException {
    }

    @Before
    public void setUp() throws Exception {
        this.user = this.userRepository.getByUserName("Louis Contant");
        this.video = this.videoRepository.getLatestVideos(1).get(0);
        this.sessionDto = this.sessionRepository.createSession("testSession", this.user.userId);
    }

    @After
    public void tearDown() throws Exception {
        this.sessionRepository.deleteSession("testSession");
    }

    private Comment generateComment() {
        Comment comment = new Comment();
        comment.replies = new ArrayList<>();
        comment.modifiedOn = new Date(Calendar.getInstance().getTimeInMillis());
        comment.createdOn = new Date(Calendar.getInstance().getTimeInMillis());
        comment.author = this.user;
        comment.userId = this.user.userId;
        comment.mediaId = this.video.videoID;

        comment.text = "test";comment.score = 0;
        return comment;
    }

    @Test
    public void insertCommentShouldReturn400ForBadSessionId() {
        ResponseEntity<String> response = this.commentController.insertComment(generateComment(), "badSessionId");
        assertEquals("\"User has to be activated\"", response.getBody());
        assertEquals(response.getStatusCodeValue(), HttpStatusCode.BAD_REQUEST);
    }

    @Test
    public void insertCommentShouldWork() throws SQLException {
        ResponseEntity<String> response = this.commentController.insertComment(generateComment(), this.sessionDto.sessionId);
        List<UserPageComment> comments = this.commentRepository.getCommentsForUser(this.user.userId);
        comments.sort(comparing((Comment co) -> co.createdOn));
        this.commentRepository.deleteComment(comments.get(comments.size() - 1).commentId);
        assertEquals(response.getStatusCodeValue(), HttpStatusCode.OK);
    }

    @Test
    public void getUserCommentsShouldIncludeTheVideoTitle()  {
        ResponseEntity<String> response =this.commentController.getAllCommentsForUser(this.user.userId);
        List<UserPageComment> comments = JSONMapper.getInstance().getMapper().fromJson(response.getBody(),  new TypeToken<List<UserPageComment>>(){}.getType());
        assertNotNull(comments.get(0).title);
    }


}