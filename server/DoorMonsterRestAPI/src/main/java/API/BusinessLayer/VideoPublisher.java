package API.BusinessLayer;

import API.Model.UserDto;
import API.Model.Video;
import API.Util.Repositories.UserRepository;
import API.Util.Repositories.VideoRepository;
import API.BusinessLayer.Storage.FeedHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class VideoPublisher {
    FeedHandler feedHandler;
    MailHandler mailHandler;
    PubSubHandler pubSubHandler;
    VideoRepository videoRepository;
    UserRepository userRepository;


    @Autowired
    private Environment environment;

    public VideoPublisher(FeedHandler feedHandler, MailHandler mailHandler, PubSubHandler pubSubHandler, VideoRepository videoRepository, UserRepository userRepository) throws SQLException, MessagingException {
        this.feedHandler = feedHandler;
        this.mailHandler = mailHandler;
        this.pubSubHandler = pubSubHandler;
        this.videoRepository = videoRepository;
        this.userRepository = userRepository;
    }


    @Scheduled(cron = "0 0 12 * * ?")
    public void checkForVideoUploads() {
        try {
            List<Video> videos = this.videoRepository.getAllVideos();
            for (Video video : videos) {
                if (!video.published && video.videoPublishDate.isBefore(LocalDateTime.now())) {
                    this.publishVideo(video);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void publishVideo(Video video) throws SQLException  {
        this.videoRepository.publishVideo(video.videoID);
        if (!this.environment.getActiveProfiles()[0].equals("dev")) {
            try {
                this.pubSubHandler.sendVideoToPubSubListeners(video);
            } catch (IOException | InvalidKeySpecException | NoSuchAlgorithmException | InvalidKeyException | SQLException e) {
                e.printStackTrace();
            }
            try {
                this.feedHandler.updateRSSFeed();
            } catch (Exception e) {
                e.printStackTrace();
            }
            List<UserDto> users = this.userRepository.getAll();
            for (UserDto user : users) {
                if (user.isActivated && user.isSubscribedToEmailNotifications) {
                    try {
                        this.mailHandler.notifyUserByEmailAboutVideo(user, video);
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
