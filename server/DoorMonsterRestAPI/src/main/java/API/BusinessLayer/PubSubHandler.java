package API.BusinessLayer;

import API.Model.Discord.DiscordSubscriptionNotification;
import API.Model.PubSubListenner;
import API.Model.PubSubVideoNotification;
import API.Model.Video;
import API.Util.JSONMapper;
import API.Util.Repositories.DiscordPubSubRepository;
import API.Util.Repositories.VideoPubSubRepository;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.util.List;

@Component
public class PubSubHandler {

    private static String VIMEO_BASE_URL = "https://www.doormonster.tv/video/";
    private static String THUMBNAIL_BASE_URL = "https://s3.amazonaws.com/doormonster/assets/images/videos/";


    private VideoPubSubRepository pubSubRepository;
    private DiscordPubSubRepository discordPubSubRepository;
    private AuthenticationHandler authenticationHandler;

    public PubSubHandler(VideoPubSubRepository videoPubSubRepository, AuthenticationHandler authenticationHandler, DiscordPubSubRepository discordPubSubRepository) {
        this.pubSubRepository = videoPubSubRepository;
        this.authenticationHandler = authenticationHandler;
        this.discordPubSubRepository = discordPubSubRepository;
    }

    public void sendBackSubConfirmation(PubSubListenner listener) {
        URL url;
        HttpURLConnection connection;
        OutputStream connectionOutput;
        try {
            url = new URL(listener.endpoint);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-type", "application/json; charset=UTF-8");
            connectionOutput = connection.getOutputStream();
            connectionOutput.write(JSONMapper.getInstance().JSONStringify("Subscription successfull").getBytes());
            connection.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleChallenge(PubSubListenner listenner, boolean useDiscord) {
        URL url;
        HttpURLConnection connection;
        OutputStream connectionOutput;
        BufferedInputStream inputStream;
        BufferedReader reader;
        StringBuilder response;
        boolean challengeSuccessfull;
        int timeDifference = 20 * 10000;
        try {
            url = new URL(listenner.endpoint + "?challengeKey=" + listenner.challengKey);
            System.out.println(url.toString());
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-type", "application/json; charset=UTF-8");
            connection.connect();
           inputStream =  new BufferedInputStream(connection.getInputStream());
            reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            response = new StringBuilder();
            while (reader.ready()) {
                response.append(reader.readLine());
            }
            String key = response.toString();
            challengeSuccessfull = keyChallengeVerification(key, listenner.challengeEndpoint);
            if (challengeSuccessfull) {
                if (!useDiscord) {
                    this.pubSubRepository.validatePubSubListenner(listenner);
                } else {
                    this.discordPubSubRepository.validatePubSubListenner(listenner);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean keyChallengeVerification(String key, String source) throws SQLException {
        PubSubListenner listenner = this.pubSubRepository.getSubcriberByKey(key);
        boolean keyIsValid = false;
        if (listenner != null && listenner.challengeEndpoint.equals(source)) {
            keyIsValid = true;
        }
        return keyIsValid;
    }

    public void sendVideoToPubSubListeners(Video video) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException, InvalidKeyException, SQLException {
        List<PubSubListenner> pubSubListenners = this.pubSubRepository.getVerifiedSubscriberRequest();
        URL url;
        HttpURLConnection connection;
        OutputStream connectionOutput;
        PubSubVideoNotification notification = new PubSubVideoNotification(video.videoTitle, video.videoSummary, VIMEO_BASE_URL + video.videoID, THUMBNAIL_BASE_URL + video.videoThumbnail);
        String notificationJsonBody = JSONMapper.getInstance().JSONStringify(notification);
        String hash;
        MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");
        for (PubSubListenner subscriber : pubSubListenners) {
            OkHttpClient client = new OkHttpClient();
            String json = JSONMapper.getInstance().JSONStringify(video);
            RequestBody body = RequestBody.create(JSON, json);
            hash = this.authenticationHandler.hasPubSubPublishBody(json, subscriber.key.getBytes());
            Request request = new Request.Builder()
                    .url(subscriber.endpoint)
                    .post(body)
                    .addHeader("X-Hub-Signature", "sha1=" + hash)
                    .build();

            Response response = client.newCall(request).execute();
            System.out.println(response.body().string());
        }
    }

    public void sendDiscordIdToPubSubListener(String id, boolean subscriptionConfirmed) throws SQLException, InvalidKeySpecException, NoSuchAlgorithmException, InvalidKeyException, IOException {
        List<PubSubListenner> pubSubListenners = this.discordPubSubRepository.getVerifiedSubscriberRequest();
        URL url;
        HttpURLConnection connection;
        OutputStream connectionOutput;
        String hash;
        MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");
        System.out.println("Sending discord person");
        for (PubSubListenner subscriber : pubSubListenners) {
            System.out.println("Sending discord person");
            OkHttpClient client = new OkHttpClient();
            DiscordSubscriptionNotification discordSubscriptionNotification = new DiscordSubscriptionNotification();
            discordSubscriptionNotification.id = id;
            discordSubscriptionNotification.subscriptionConfirmed = subscriptionConfirmed;
            String json = JSONMapper.getInstance().JSONStringify(discordSubscriptionNotification);
            RequestBody body = RequestBody.create(JSON, json);
            hash = this.authenticationHandler.hasPubSubPublishBody(json, subscriber.key.getBytes());
            Request request = new Request.Builder()
                    .url(subscriber.endpoint)
                    .post(body)
                    .addHeader("X-Hub-Signature", "sha1=" + hash)
                    .build();

            Response response = client.newCall(request).execute();
            System.out.println(response.body().string());
        }
    }
}
