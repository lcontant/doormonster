package Test;

import API.BusinessLayer.MailHandler;
import API.Model.UserDto;
import API.Model.Video;
import org.junit.Before;
import org.junit.Test;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

import static org.junit.Assert.assertTrue;

public class MailHandlerTest {
    UserDto user;
    MailHandler mailHandler;
    @Before
    public void setUp() throws Exception {
        this.user = new UserDto();
        user.email = "treymaryott@gmail.com";
        this.mailHandler = new MailHandler();
    }

    @Test
    public void sendVerificationMail() throws UnsupportedEncodingException, MessagingException {
        this.mailHandler.sendVerificationMail(this.user, "testing");
        assertTrue(true);
    }

    @Test
    public void sendPasswordResetMail() throws UnsupportedEncodingException, MessagingException {
        this.mailHandler.sendPasswordResetMail(this.user.email, "aweawefawefawef");
        assertTrue(true);
    }

    @Test
    public void notifyUserByEmailAboutVideo() throws MessagingException {
        Video video = new Video();
        video.videoTitle = "Test video";
        video.videoID = "315237846";
        video.videoThumbnail = "vidsaboutthings/maxresdefault.jpg";
        this.mailHandler.notifyUserByEmailAboutVideo(this.user, video);
        assertTrue(true);
    }

    @Test
    public void testLook() throws MessagingException {
       this.mailHandler.sendMail("louis.contant.1@ens.etsmtl.ca", "Test for reply to field", "Hey reply to this message to see if this works", "louis.contant.1@gmail.com", false);
    }
}