package API.BusinessLayer;


import API.Model.UserDto;
import API.Model.Video;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.net.URLEncoder;
import java.util.Properties;

@Component
public class MailHandler {

    private String mailUsername;
    private String mailPassword;
    public MailHandler(@Value("${aws.mailUsername}") String mailUsername, @Value("${aws.mailPassword}") String mailPassword) {
        this.mailPassword = mailPassword;
        this.mailUsername = mailUsername;
    }

    public void sendMail(String sender, String name, String body, String receiver, boolean useActualSender) throws MessagingException {
        String to = receiver;
        String from = useActualSender ? sender : "contact@doormonster.tv";
        Properties props = new Properties();
        //TODO: Change this to AWS
        props.put("mail.smtp.host", "email-smtp.us-east-1.amazonaws.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(mailUsername, mailPassword);
                    }
                });
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.addRecipients(Message.RecipientType.TO, new InternetAddress[]{new InternetAddress(to)});
        message.setReplyTo(new InternetAddress[]{new InternetAddress(sender)});

        message.setSubject(name );
        message.setText(body, "UTF-8", "html");
        Transport.send(message);
        System.out.println("message sent");
    }

    public void sendVerificationMail(UserDto user, String activationId) throws MessagingException, UnsupportedEncodingException {
        String body = String.format(getStringFromFile("AccountActivation.html"), URLEncoder.encode(activationId, "UTF-8"));
        this.sendMail("contact@doormonster.tv","DoorMonster Account Activation", body, user.email, true);
    }

    public void sendPasswordResetMail(String email, String token) throws UnsupportedEncodingException, MessagingException {
        String body = String.format(getStringFromFile("PasswordReset.html"), URLEncoder.encode(token, "UTF-8"));
        this.sendMail("contact@doormonster.tv", "Door monster password reset", body, email, true);
    }

    public void notifyUserByEmailAboutVideo(UserDto user, Video video) throws MessagingException {
        String body = String.format(getStringFromFile("VideoNotification.html"), video.videoID,video.videoThumbnail,video.videoTitle, "https://www.doormonster.tv/account/edit");
        this.sendMail("contact@doormonster.tv", "New Video from DMTV!", body, user.email, true);
    }

    public void sendFeedBack(String sender, String username, String message) throws MessagingException {
        sendMail(sender, username, message, "contact@doormonster.tv", false);
        sendMail(sender, username, message, "louis.contant.1@gmail.com", false);
    }

    public void reportBug(String report, String receiver) throws MessagingException {
        sendMail("contact@doormonster.tv", "Bug report", report, receiver, false);
    }

    private String getStringFromFile(String fileName) {
        String returnString = "";
        try {
            BufferedReader reader = new BufferedReader(new FileReader("src/main/java/API/Files/" + fileName));
            returnString = "";
            while (reader.ready()) {
                returnString += reader.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return returnString;
    }
}
