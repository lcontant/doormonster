package API.Controllers;

import API.BusinessLayer.AuthenticationHandler;
import API.BusinessLayer.PubSubHandler;
import API.Model.PubSubChallengeResponse;
import API.Model.PubSubListenner;
import API.Util.JSONMapper;
import API.Util.Repositories.DiscordPubSubRepository;
import API.Util.Repositories.VideoPubSubRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.Base64;

@RestController
@RequestMapping("PubSub")
public class PubSubController {
    private VideoPubSubRepository videoPubSubRepository;
    private PubSubHandler pubSubHandler;
    private AuthenticationHandler authenticationHandler;
    private DiscordPubSubRepository discordPubSubRepository;

    public PubSubController(VideoPubSubRepository videoPubSubRepository, PubSubHandler pubSubHandler, AuthenticationHandler authenticationHandler, DiscordPubSubRepository discordPubSubRepository) {
        this.videoPubSubRepository = videoPubSubRepository;
        this.pubSubHandler = pubSubHandler;
        this.authenticationHandler = authenticationHandler;
        this.discordPubSubRepository = discordPubSubRepository;
    }

    @PostMapping("/subscribe")
    public ResponseEntity<String> subscribeToVideoUploads(@RequestBody PubSubListenner subscription, HttpServletRequest request) {
        String response = JSONMapper.getInstance().JSONStringify("Request received");
        System.out.println(subscription.endpoint);
        System.out.println(subscription.key);
        HttpStatus status = HttpStatus.OK;
        subscription.challengeEndpoint = subscription.endpoint;
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] keyBytes = this.authenticationHandler.generateSalt();
        String key = encoder.encodeToString(keyBytes);
        subscription.challengKey = key;
        boolean subscribed = false;
        try {
            PubSubListenner preExisitingSubscriber = this.videoPubSubRepository.getSubscriberByEnpoint(subscription.endpoint);
            System.out.println(subscription.endpoint);
            if (preExisitingSubscriber == null) {
                subscribed = this.videoPubSubRepository.addSubscriber(subscription);
            } else {
                subscribed = this.videoPubSubRepository.updateSubscriberKey(subscription, preExisitingSubscriber.Id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (subscribed) {
            this.pubSubHandler.handleChallenge(subscription, false);
        }
        return new ResponseEntity<>(response, status);
    }

    @PostMapping("/challenge")
    public ResponseEntity<String> handleChallenge(@RequestBody PubSubChallengeResponse key, HttpServletRequest request) {
        String response = JSONMapper.getInstance().JSONStringify("Invalid Key");
        HttpStatus status = HttpStatus.BAD_REQUEST;
        try {
            boolean isValid = this.pubSubHandler.keyChallengeVerification(key.key, request.getRemoteAddr());
            if (isValid) {
                response = JSONMapper.getInstance().JSONStringify("Challenge passed");
                status = HttpStatus.OK;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(response, status);
    }

    @PostMapping("/subscribe/discord")
    public ResponseEntity<String> discordSubscribe(@RequestBody PubSubListenner subscription, HttpServletRequest request) {
        String response = JSONMapper.getInstance().JSONStringify("Request received");
        System.out.println(subscription.endpoint);
        System.out.println(subscription.key);
        HttpStatus status = HttpStatus.OK;
        subscription.challengeEndpoint = subscription.endpoint;
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] keyBytes = this.authenticationHandler.generateSalt();
        String key = encoder.encodeToString(keyBytes);
        subscription.challengKey = key;
        boolean subscribed = false;
        try {
            PubSubListenner preExisitingSubscriber = this.discordPubSubRepository.getSubscriberByEnpoint(subscription.endpoint);
            System.out.println(subscription.endpoint);
            if (preExisitingSubscriber == null) {
                subscribed = this.discordPubSubRepository.addSubscriber(subscription);
            } else {
                subscribed = this.discordPubSubRepository.updateSubscriberKey(subscription, preExisitingSubscriber.Id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (subscribed) {
            this.pubSubHandler.handleChallenge(subscription, true);
        }
        return new ResponseEntity<>(response, status);
    }

}
