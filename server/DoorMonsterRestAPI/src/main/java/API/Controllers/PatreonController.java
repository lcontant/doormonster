package API.Controllers;

import API.BusinessLayer.PatreonBL;
import API.Model.Patreon.PostRequestResponse;
import API.Model.UserDto;
import API.Util.JSONMapper;
import API.Util.Repositories.SessionRepository;
import API.Util.Repositories.UserRepository;
import com.patreon.PatreonOAuth;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@RestController
@RequestMapping("/patreon")
public class PatreonController {

    PatreonBL patreonHandler;
    SessionRepository sessionRepository;
    UserRepository userRepository;

    public PatreonController(PatreonBL patreonHandler
            , SessionRepository sessionRepository
            , UserRepository userRepository) {
        this.patreonHandler = patreonHandler;
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/posts")
    public ResponseEntity<String> getPosts(@RequestHeader("sessionId") String sessionId) {
        String responseMessage = "";
        HttpStatus responseStatus = HttpStatus.OK;
        ResponseEntity<String> response;
        int currentPatreonContribution = 0;
        UserDto user = null;
        if (!sessionId.isEmpty()) {
            try {
                user = this.userRepository.getBySessionId(sessionId);
                if (user != null) {
                    currentPatreonContribution = user.patreonContribution;
                }
            } catch (SQLException e) {
                e.printStackTrace();

            }
        }
        PostRequestResponse patreonResponse = this.patreonHandler.getAllPostsFromPatreon(currentPatreonContribution);
        if (currentPatreonContribution < 1200) {
            int finalCurrentPatreonContribution = currentPatreonContribution;
            patreonResponse.data.removeIf(post -> {
                return finalCurrentPatreonContribution < post.attributes.min_cents_pledged_to_view;
            });
        }
        response = new ResponseEntity<String>(JSONMapper.getInstance().JSONStringify(patreonResponse),responseStatus);
        return response;
    }

    @PostMapping("/registerToken")
    public ResponseEntity<String> registerToken(@RequestHeader("SessionId") String sessionId, @RequestHeader("code") String code) {
        ResponseEntity<String> response = new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
        String responseMessage ="";
        HttpStatus responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        try {
            UserDto user = this.userRepository.getBySessionId(sessionId);
            if (user != null) {
                PatreonOAuth.TokensResponse tokensResponse = this.patreonHandler.getTokenFromOauthCode(code);
                this.userRepository.updateUserPatreonTokens(tokensResponse.getAccessToken(),tokensResponse.getRefreshToken(),user.userId);
                responseMessage = JSONMapper.getInstance().JSONStringify("Successfully update user status");
                responseStatus = HttpStatus.OK;
            } else {
                return response;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return response;
        }
        return new ResponseEntity<>(responseMessage, responseStatus);
    }

    @GetMapping("/isPatron")
    public ResponseEntity<String> syncPatron(@RequestHeader("SessionId") String sessionId) {
        boolean isPatron = false;
        int patreonContribution = 0;
        String response = null;
        response = JSONMapper.getInstance().JSONStringify(isPatron);
        try {
            UserDto user = userRepository.getBySessionId(sessionId);
            if (user != null) {
                patreonContribution = this.patreonHandler.getUserPatreonConribution(user.getPatreonToken());
                isPatron = patreonContribution > 0;
                this.userRepository.updatePatronStatus(user.userId, isPatron, patreonContribution);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        response = JSONMapper.getInstance().JSONStringify(isPatron);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
