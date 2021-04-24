package Test;

import API.BusinessLayer.MailHandler;
import API.Model.Video;
import API.Util.Repositories.UserRepository;
import API.Util.Repositories.VideoRepository;
import API.Util.SQLConnector.ConnectionManager;
import API.Util.SQLConnector.ConnectionPoolManager;
import API.BusinessLayer.PubSubHandler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("dev")
public class VideoPublisherTest {
    @Autowired
    UserRepository userRepository;

    @Autowired
    MailHandler mailHandler;

    @Autowired
    VideoRepository videoRepository;

    @Autowired
    ConnectionPoolManager connectionPoolManager;

    @Autowired
    ConnectionManager connectionManager;

    @Autowired
    PubSubHandler pubSubHandler;

    @Before
    public void setUp() throws Exception {
    }


    @Test
    public void sendVideoToVideoPublisher() throws SQLException, InvalidKeySpecException, NoSuchAlgorithmException, IOException, InvalidKeyException {
        Video video = this.videoRepository.getLatestVideos(1).get(0);
        this.pubSubHandler.sendVideoToPubSubListeners(video);
    }
}