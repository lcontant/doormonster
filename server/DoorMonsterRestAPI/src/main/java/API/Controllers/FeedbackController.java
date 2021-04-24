package API.Controllers;

import API.BusinessLayer.MailHandler;
import API.Model.Feedback;
import API.Model.Role;
import API.Model.UserDto;
import API.Ressource.FeedbackWithUser;
import API.Util.JSONMapper;
import API.Util.Repositories.FeedbackRepository;
import API.Util.Repositories.RoleRepository;
import API.Util.Repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/feedback")
public class FeedbackController {

    private MailHandler mailHandler;
    private UserRepository userRepository;
    private FeedbackRepository feedbackRepository;
    private RoleRepository roleRepository;

    public FeedbackController(MailHandler mailHandler, UserRepository userRepository, FeedbackRepository feedbackRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.mailHandler = mailHandler;
        this.feedbackRepository = feedbackRepository;
        this.roleRepository = roleRepository;
    }


    @PostMapping("/creator")
    public ResponseEntity<String> sendCreatorSuggestion(@RequestHeader("sessionId") String sender_sessiond_id, @RequestBody Feedback suggestion) {
        UserDto actualSender;
        String response = JSONMapper.getInstance().JSONStringify("Couldn't send the feedback");
        HttpStatus response_status = HttpStatus.INTERNAL_SERVER_ERROR;
        try {
            UserDto user = this.userRepository.getBySessionId(sender_sessiond_id);
            if (user != null) {
                actualSender = user;
                this.mailHandler.sendFeedBack(user.email, user.username, suggestion.content);
                response = JSONMapper.getInstance().JSONStringify("feedback sent");
                response_status = HttpStatus.OK;
            } else {
                response = JSONMapper.getInstance().JSONStringify("Invalid session id");
                response_status = HttpStatus.BAD_REQUEST;
            }

        } catch (SQLException | MessagingException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(response, response_status);
    }

    @PostMapping("/user")
    public ResponseEntity<String> sendUserFeedback(@RequestHeader("sessionId") String sesionId, @RequestBody Feedback  feedbackContent) {
        String response = JSONMapper.getInstance().JSONStringify("Couldn't send your feedback sorry");
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        boolean insertSuccesfful = false;
        UserDto user;
        try {
            user = this.userRepository.getBySessionId(sesionId);
            if (user != null) {
                Feedback feedback = new Feedback(user.userId, -1, feedbackContent.content);
                insertSuccesfful = this.feedbackRepository.insertFeedback(feedback);
                if (insertSuccesfful) {
                    response = JSONMapper.getInstance().JSONStringify("Feedback received");
                    status = HttpStatus.OK;
                }
            } else {
                response = JSONMapper.getInstance().JSONStringify("Couldn't find the user");
                status = HttpStatus.BAD_REQUEST;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(response, status);
    }

    @GetMapping()
    public ResponseEntity<String> getAllUserFeedback(@RequestHeader("sessionId") String sessionId) {
        Role demanderRole;
        UserDto demander;
        String response = JSONMapper.getInstance().JSONStringify("Couldn't get the feedback sorry");
        HttpStatus responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        List<FeedbackWithUser> userFeedback;
        try {
            demander = this.userRepository.getBySessionId(sessionId);
            if (demander != null) {
                demanderRole = this.roleRepository.getRoleByUserId(demander.userId);
                if (demanderRole != null && demanderRole.ranking == 0) {
                    userFeedback = this.feedbackRepository.getAllFeedbackRequest();
                    response = JSONMapper.getInstance().JSONStringify(userFeedback);
                    responseStatus = HttpStatus.OK;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(response, responseStatus);
    }
}
