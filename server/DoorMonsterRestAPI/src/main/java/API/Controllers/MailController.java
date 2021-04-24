package API.Controllers;

import API.BusinessLayer.MailHandler;
import API.Model.MailRequest;
import API.Util.JSONMapper;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/mail")
public class MailController {

    MailHandler mailHandler;

    private Environment environment;

    public MailController(MailHandler mailHandler, Environment environment) {
        this.mailHandler = mailHandler;
        this.environment = environment;
    }

    @RequestMapping(value = "/send", method = POST)
    public ResponseEntity<String> sendMail(@RequestBody String body) {
        MailRequest request = JSONMapper.getInstance().getMapper().fromJson(body, MailRequest.class);
        String message = JSONMapper.getInstance().JSONStringify("Problem encountered while sending the email");
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String profile = this.environment.getActiveProfiles()[0];

        if (!profile.equals("dev")) {
            try {
                this.mailHandler.sendMail(request.email, request.sender, request.body, "contact@doormonster.tv", false);
                this.mailHandler.sendMail(request.email, request.sender, request.body, "louis.contant.1@gmail.com", false);
                message = JSONMapper.getInstance().JSONStringify("Message sent");
                status = HttpStatus.OK;
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
        return new ResponseEntity<>(message, status);
    }

    @PostMapping("/report")
    public ResponseEntity<String> sendBugReport(@RequestBody String message) {
        String response = JSONMapper.getInstance().JSONStringify("Problem encountered while sending the email");
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String profile = this.environment.getActiveProfiles()[0];
            try {
                if (!profile.equals("dev")) {
                this.mailHandler.reportBug(message, "contact@doormonster.tv");
                }
                this.mailHandler.reportBug(message, "louis.contant.1@gmail.com");
                response = JSONMapper.getInstance().JSONStringify("Bug reported");
                status = HttpStatus.OK;
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        return new ResponseEntity<>(response, status);
    }
}

