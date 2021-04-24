package API.Controllers;

import API.BusinessLayer.AuthenticationHandler;
import API.BusinessLayer.Discord.DiscordBusinessLayer;
import API.BusinessLayer.LogBusinessLayer;
import API.BusinessLayer.MailHandler;
import API.BusinessLayer.PubSubHandler;
import API.BusinessLayer.Storage.StorageHandler;
import API.Model.*;
import API.Util.JSONMapper;
import API.Util.Repositories.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.util.List;

import static API.Model.MESSAGES_CONSTANTS.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
@RequestMapping("user")
public class UserController {

    MailHandler mailHandler;
    AuthenticationHandler authenticationHandler;
    StorageHandler storageHandler;
    ActivationTokenRepository activationTokenRepository;
    UserRepository userRepository;
    SessionRepository sessionRepository;
    PasswordResetRepository passwordResetRepository;
    RoleRepository roleRepository;
    LogBusinessLayer logBusinessLayer;
    DiscordBusinessLayer discordBusinessLayer;
    DiscordUserRepository discordUserRepository;
    PubSubHandler pubSubHandler;

    public UserController(
            MailHandler mailHandler
            , AuthenticationHandler authenticationHandler
            , StorageHandler storageHandler
            , ActivationTokenRepository activationTokenRepository
            , UserRepository userRepository
            , SessionRepository sessionRepository
            , PasswordResetRepository passwordResetRepository
            , RoleRepository roleRepository
            , LogBusinessLayer logBusinessLayer
            , DiscordBusinessLayer discordBusinessLayer
            , DiscordUserRepository discordUserRepository
            , PubSubHandler pubSubHandler
    ) {

        this.mailHandler = mailHandler;
        this.authenticationHandler = authenticationHandler;
        this.storageHandler = storageHandler;
        this.activationTokenRepository = activationTokenRepository;
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.passwordResetRepository = passwordResetRepository;
        this.roleRepository = roleRepository;
        this.logBusinessLayer = logBusinessLayer;
        this.discordBusinessLayer = discordBusinessLayer;
        this.discordUserRepository = discordUserRepository;
        this.pubSubHandler = pubSubHandler;
    }

    @GetMapping("/all")
    public ResponseEntity<String> getAllUsers(@RequestHeader("SessionId") String sessionID) {
        String response = "Error while getting the users";
        HttpStatus responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        List<UserDto> users;
        this.logBusinessLayer.log("Request for all users", sessionID);
        try {
            UserDto correspondingUser = this.userRepository.getBySessionId(sessionID);
            if (correspondingUser != null) {
                UserRole correspondingRole = this.roleRepository.getUserRoleByUserId(correspondingUser.userId);
                ResponseEntity<Role> roleResponseEntity = this.roleRepository.getRoleById(correspondingRole.roleId);
                if (roleResponseEntity.getStatusCode() == HttpStatus.OK && roleResponseEntity.getBody().ranking == 0) {
                    users = this.userRepository.getAll();
                    response = JSONMapper.getInstance().JSONStringify(users);
                    responseStatus = HttpStatus.OK;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            this.logBusinessLayer.log(e.getMessage(), sessionID);
        }
        return new ResponseEntity<>(response, responseStatus);
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getUserById(@RequestHeader("SessionId") String sessionId, @PathVariable("id") int userId) {
        String response = "Error while getting the user";
        HttpStatus responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        UserDto user;
        UserDto demander;
        this.logBusinessLayer.log(String.format("Request for user %s by id", userId), sessionId);
        try {
            demander = this.userRepository.getBySessionId(sessionId);
                user = this.userRepository.getById(userId);
                response = JSONMapper.getInstance().JSONStringify(user);
                responseStatus = HttpStatus.OK;
        } catch (SQLException e) {
            e.printStackTrace();
            this.logBusinessLayer.log(e.getMessage(), sessionId);
        }
        return new ResponseEntity<>(response, responseStatus);
    }

    @RequestMapping(path = "/create", method = POST, consumes = "application/json")
    public ResponseEntity<String> createUser(@RequestBody UserDto user) {
        this.logBusinessLayer.log(String.format("Request to create user %s with email %s", user.username, user.email));
        String response = JSONMapper.getInstance().JSONStringify("Couldn't create the user account");
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        UserDto request = new UserDto(user.username, user.fullname,user.useFullName,user.getPassword(), null, user.location, user.email, -1, false, 0, 0, null, null, false, false, "");
        try {
            if (user.getPassword() == null) {
                response = JSONMapper.getInstance().JSONStringify("Password required");
                status = HttpStatus.BAD_REQUEST;
            } else if (user.username == null || user.email == null) {
                response = JSONMapper.getInstance().JSONStringify("Username or email required");
                status = HttpStatus.BAD_REQUEST;
            } else if (this.userRepository.getByEmail(user.email) != null) {
                response = JSONMapper.getInstance().JSONStringify("Email already in use");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            } else if (this.userRepository.getByUserName(user.username) != null) {
                response = JSONMapper.getInstance().JSONStringify("Username already in use");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            } else {
                request = this.authenticationHandler.hashAndSaltUserPassword(request);
                this.userRepository.registerUser(request);
                UserDto createdUser = this.userRepository.getByUserName(request.username);
                String activationId = this.authenticationHandler.generateActivationId(createdUser);
                this.activationTokenRepository.createToken(new ActivationToken(activationId, createdUser.userId));

                this.mailHandler.sendVerificationMail(createdUser, activationId);
                response = JSONMapper.getInstance().JSONStringify("Authenticated user");
                status = HttpStatus.OK;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            this.logBusinessLayer.log(e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            this.logBusinessLayer.log(e.getMessage());
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
            this.logBusinessLayer.log(e.getMessage());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            this.logBusinessLayer.log(e.getMessage());
        } catch (MessagingException e) {
            e.printStackTrace();
            this.logBusinessLayer.log(e.getMessage());
        }
        return new ResponseEntity<>(response, status);
    }

    @RequestMapping(path = "/logout", method = PUT)
    public ResponseEntity<Boolean> logoutUser(@RequestHeader("SessionId") String sessionId) {
        this.logBusinessLayer.log("Log out", sessionId);
        Boolean response = false;
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        try {
            UserDto referenceUser = this.userRepository.getBySessionId(sessionId);
            if (referenceUser != null) {
                this.userRepository.resetAttempts(referenceUser.userId);
                this.sessionRepository.deleteSession(sessionId);
                response = true;
                status = HttpStatus.OK;
            } else {
                status = HttpStatus.BAD_REQUEST;
            }
        } catch (SQLException e) {
            this.logBusinessLayer.log(e.getMessage(), sessionId);
        }
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(path = "/resendConfirmation", method = PUT)
    public ResponseEntity<String> resendConfirmationEmail(@RequestHeader("SessionId") String sessionId) {
        this.logBusinessLayer.log("confirmation resend request", sessionId);
        String response = "Couldn't send the confirmation email";
        HttpStatus responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        try {
            UserDto user = userRepository.getBySessionId(sessionId);
            if (user != null) {
                ActivationToken activationToken = activationTokenRepository.getTokenByUserId(user.userId);
                if (activationToken == null) {
                    String activationId = this.authenticationHandler.generateActivationId(user);
                    this.activationTokenRepository.createToken(new ActivationToken(activationId, user.userId));
                    activationToken = this.activationTokenRepository.getTokenByUserId(user.userId);
                }
                if (activationToken != null) {
                    this.mailHandler.sendVerificationMail(user, activationToken.activationId);
                    response = "Activation email resent";
                    responseStatus = HttpStatus.OK;
                }
            } else {
                responseStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (SQLException | UnsupportedEncodingException | MessagingException e) {
            e.printStackTrace();
            this.logBusinessLayer.log(e.getMessage(), sessionId);
            responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            response = "There was an error sending the verification email";
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
            this.logBusinessLayer.log(e.getMessage(), sessionId);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            this.logBusinessLayer.log(e.getMessage(), sessionId);
        }
        return new ResponseEntity<>(JSONMapper.getInstance().JSONStringify(response), responseStatus);
    }

    @GetMapping(path = "/admin/resendConfirmation/{userId}")
    public ResponseEntity<String> sendConfirmationEmailFromAdminPanel(@RequestHeader("SessionId") String sessionId, @PathVariable("userId") int userId) {
        this.logBusinessLayer.log("confirmation resend request from admin panel", sessionId);
        String responseMessage = JSONMapper.getInstance().JSONStringify(UNEXPECTED_ERROR_ENCOUNTERED);
        HttpStatus responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        ResponseEntity<String> response = null;
        UserDto user;
        try {
            boolean userIsAdmin = this.authenticationHandler.userIsAdmin(sessionId);
            if (userIsAdmin) {
                user = this.userRepository.getById(userId);
                this.logBusinessLayer.log("confirmation resend request from admin panel", userId);
                if (user != null) {
                    ActivationToken token = this.activationTokenRepository.getTokenByUserId(userId);
                    if (token == null) {
                        String activationId = this.authenticationHandler.generateActivationId(user);
                        this.activationTokenRepository.createToken(new ActivationToken(activationId, userId));
                        token = this.activationTokenRepository.getTokenByUserId(userId);
                    }
                    if (token != null) {
                        this.mailHandler.sendVerificationMail(user, token.activationId);
                        responseMessage = JSONMapper.getInstance().JSONStringify("Activation email resent");
                        responseStatus = HttpStatus.OK;
                    }
                }
            } else {
                responseMessage = JSONMapper.getInstance().JSONStringify(UNAUTHORIZED_ERROR);
                responseStatus = HttpStatus.UNAUTHORIZED;
            }
        } catch (SQLException | InvalidKeySpecException | NoSuchAlgorithmException | MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
            this.logBusinessLayer.log(e.getMessage(), sessionId);
        }
        response = new ResponseEntity<>(responseMessage, responseStatus);
        return response;
    }

    //TODO: Redo this whole thing. Jesus is it ugly
    @RequestMapping(path = "/update", method = PUT, consumes = "application/json")
    public ResponseEntity<String> updateUser(@RequestBody UserDto user, @RequestHeader("SessionId") String sessionId) {
        this.logBusinessLayer.log(String.format("user update"), sessionId);
        UserDto referenceUser = null;
        try {
            referenceUser = this.userRepository.getBySessionId(sessionId);
            if (referenceUser != null) {
                Role role = this.roleRepository.getRoleByUserId(referenceUser.userId);
                if (referenceUser != null && (user.userId == referenceUser.userId || role != null && role.ranking == 0)) {
                    if ((!referenceUser.username.equals(user.username) && this.userRepository.getByUserName(user.username) != null)) {
                        return new ResponseEntity<>("username not available", HttpStatus.BAD_REQUEST);
                    } else if (!referenceUser.email.equals(user.email) && this.userRepository.getByEmail(user.email) != null) {
                        return new ResponseEntity<>("email not available", HttpStatus.BAD_REQUEST);
                    }
                    return new ResponseEntity<>(JSONMapper.getInstance().JSONStringify(this.userRepository.updateUser(user)), HttpStatus.OK);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            this.logBusinessLayer.log(e.getMessage(), sessionId);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/ban/{userId}")
    public ResponseEntity<String> banUser(@RequestHeader("SessionId") String sessionId, @PathVariable("userId") int userId) {
        this.logBusinessLayer.log(String.format("requested to ban user %s", userId), sessionId);
        UserDto demander;
        Role demanderRole;
        boolean userBanned = false;
        String response = JSONMapper.getInstance().JSONStringify(false);
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        try {
            demander = this.userRepository.getBySessionId(sessionId);
            demanderRole = this.roleRepository.getRoleByUserId(demander.userId);
            if (demanderRole.ranking == 0) {
                userBanned = this.userRepository.banUserById(userId);
                if (userBanned) {
                    response = JSONMapper.getInstance().JSONStringify(true);
                    status = HttpStatus.OK;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            this.logBusinessLayer.log(e.getMessage(), sessionId);
        }
        return new ResponseEntity<>(response, status);
    }

    @PostMapping("/unban/{userId}")
    public ResponseEntity<String> unbanUser(@RequestHeader("SessionId") String sessionId, @PathVariable("userId") int userId) {
        this.logBusinessLayer.log(String.format("requested to unban user %s", userId), sessionId);
        UserDto demander;
        Role demanderRole;
        boolean userUnbanned = false;
        String response = JSONMapper.getInstance().JSONStringify(false);
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        try {
            demander = this.userRepository.getBySessionId(sessionId);
            demanderRole = this.roleRepository.getRoleByUserId(demander.userId);
            if (demanderRole.ranking == 0) {
                userUnbanned = this.userRepository.unbanUserById(userId);
                if (userUnbanned) {
                    status = HttpStatus.OK;
                    response = JSONMapper.getInstance().JSONStringify(true);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            this.logBusinessLayer.log(e.getMessage(), sessionId);
        }
        return new ResponseEntity<>(response, status);
    }

    @RequestMapping(path = "/password", method = PUT)
    public ResponseEntity<String> updatePassword(@RequestHeader("token") String token, @RequestHeader("password") String password) {
        this.logBusinessLayer.log(String.format("requested to update password with token %s", token));
        PasswordResetToken resetToken = this.passwordResetRepository.getByToken(token);
        if (resetToken != null) {
            try {
                this.logBusinessLayer.log(String.format("requested to update password with token %s", token), resetToken.userId);
                HashedPassword newHashedPassword = this.authenticationHandler.hashPassword(password);
                this.userRepository.updateUserPassword(resetToken.userId, newHashedPassword.password, newHashedPassword.salt);
                this.userRepository.resetAttempts(resetToken.userId);
                this.passwordResetRepository.deleteTokensForUser(resetToken.userId);
            } catch (SQLException e) {
                e.printStackTrace();
                this.logBusinessLayer.log(e.getMessage());
            }
            return new ResponseEntity<>(JSONMapper.getInstance().JSONStringify("password updated"), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(JSONMapper.getInstance().JSONStringify("Couldn't update the password"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(path = "/passwordEdit", method = PUT)
    public ResponseEntity<String> editPassword(@RequestHeader("SessionId") String sessionId, @RequestHeader("oldPassword") String oldPassword, @RequestHeader("newPassword") String newPassword) {
        this.logBusinessLayer.log(String.format("update password from account edit", sessionId));
        try {
            SessionDto sessionDto = this.sessionRepository.getSession(sessionId);
            UserDto referenceUser = this.userRepository.getById(sessionDto.userId);
            if (referenceUser != null && this.authenticationHandler.authenticateUser(referenceUser.username, oldPassword)) {
                HashedPassword newHashedPassword = this.authenticationHandler.hashPassword(newPassword);
                this.userRepository.updateUserPassword(sessionDto.userId, newHashedPassword.password, newHashedPassword.salt);
                this.userRepository.resetAttempts(sessionDto.userId);
                return new ResponseEntity<>(JSONMapper.getInstance().JSONStringify("password updated"), HttpStatus.OK);
            } else {
                this.userRepository.addFailedAttempt(sessionDto.userId);
                return new ResponseEntity<>(JSONMapper.getInstance().JSONStringify("Couldn't update the password"), HttpStatus.BAD_REQUEST);
            }
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | SQLException | IOException e) {
            this.logBusinessLayer.log(e.getMessage(), sessionId);
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(path = "/passwordReset", method = GET)
    public ResponseEntity<String> sendPasswordReset(@RequestHeader("Email") String email) {
        this.logBusinessLayer.log(String.format("Password reset request for email %s", email));
        try {
            UserDto actualUser = this.userRepository.getByEmail(email);
            if (actualUser != null) {
                PasswordResetToken token = new PasswordResetToken(this.authenticationHandler.generatePasswordResetToken(), actualUser.userId);
                this.passwordResetRepository.insertPasswordToken(token);
                try {
                    this.mailHandler.sendPasswordResetMail(email, token.token);
                } catch (UnsupportedEncodingException | MessagingException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            this.logBusinessLayer.log(e.getMessage());
            e.printStackTrace();
        }
        return new ResponseEntity<>(JSONMapper.getInstance().JSONStringify("If the email was valid it should be in your inbox shortly"), HttpStatus.OK);
    }

    @RequestMapping(path = "/authenticate", method = POST, consumes = "application/json")
    public ResponseEntity<String> authenticateUser(@RequestBody UserDto user) {
        this.logBusinessLayer.log(String.format("Authentication request for user %s", user.username));
        String response = "";
        UserDto actualUser = null;
        try {
            actualUser = this.userRepository.getByUserName(user.username);
            if (actualUser == null) {
                actualUser = this.userRepository.getByEmail(user.username);
            }
            if (actualUser != null && this.authenticationHandler.authenticateUser(actualUser.username, user.getPassword())) {
                if (actualUser.failedAttemps > 3) {
                    response = "Your account has been blocked due to too many failed login attempts\n to unlock your account, click on forgot password and ask for a password reset";
                    return new ResponseEntity<>(JSONMapper.getInstance().JSONStringify(response), HttpStatus.BAD_REQUEST);
                } else {
                    String session = this.authenticationHandler.generateSession(actualUser);
                    response = JSONMapper.getInstance().JSONStringify(this.sessionRepository.createSession(session, actualUser.userId));
                    this.userRepository.resetAttempts(actualUser.userId);
                    this.logBusinessLayer.log(String.format("Authentication request for user %s", user.username), user.userId);
                }
            } else {
                response = JSONMapper.getInstance().JSONStringify("Couldn't authenticate user");
                if (actualUser != null) {
                    this.userRepository.addFailedAttempt(actualUser.userId);
                    this.logBusinessLayer.log(String.format("Authentication request failed for user %s", actualUser.username), user.userId);
                }
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (SQLException | InvalidKeySpecException | UnsupportedEncodingException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @PostMapping(path = "/avatar", consumes = "multipart/form-data")
    public ResponseEntity<String> handleFileUpload(@RequestHeader("SessionId") String sessionId, @RequestHeader("FileName") String fileName, @RequestParam("file") MultipartFile file) {
        this.logBusinessLayer.log(String.format("Avatar update"), sessionId);
        String response = JSONMapper.getInstance().JSONStringify("Couldn't upload the avatar");
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        UserDto user = null;
        if (this.storageHandler.fileIsSupportedFormat(fileName)) {
            try {
                user = this.userRepository.getBySessionId(sessionId);
                response = JSONMapper.getInstance().JSONStringify("Couldn't find user");
                status = HttpStatus.BAD_REQUEST;
                if (user != null) {
                    user.avatar = this.storageHandler.storeUserProfileImage(file, String.valueOf(user.userId), fileName);
                    user.avatar = "" + user.userId + this.storageHandler.getFileExtension(fileName);
                    this.userRepository.updateUser(user);
                    response = JSONMapper.getInstance().JSONStringify("Avatar updated");
                    status = HttpStatus.OK;
                }
            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }
        }
        return new ResponseEntity<>(response, status);
    }

    @RequestMapping(path = "/session", method = GET)
    public ResponseEntity<String> getUserBySession(@RequestHeader("SessionId") String sessionId) {
        this.logBusinessLayer.log(String.format("Authentication via session"), sessionId);
        UserDto correspondingUser = null;
        String message = JSONMapper.getInstance().JSONStringify("Couldn't authenticate user");
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        try {
            correspondingUser = this.userRepository.getBySessionId(sessionId);
            if (correspondingUser != null) {
                this.logBusinessLayer.log(String.format("Authentication via session"), correspondingUser.userId);
                message = JSONMapper.getInstance().JSONStringify(correspondingUser);
                status = HttpStatus.OK;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(message, status);
    }

    @GetMapping("/role")
    public ResponseEntity<String> getUserRoleBySessionId(@RequestHeader("SessionId") String sessionId) {
        this.logBusinessLayer.log(String.format("Role request"), sessionId);
        UserDto user;
        Role role;
        String response = JSONMapper.getInstance().JSONStringify("Couldn't get user");
        HttpStatus status = HttpStatus.BAD_REQUEST;
        try {
            user = this.userRepository.getBySessionId(sessionId);
            if (user != null) {
                role = this.roleRepository.getRoleByUserId(user.userId);
                if (role != null) {
                    response = JSONMapper.getInstance().JSONStringify(role);
                    status = HttpStatus.OK;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(response, status);
    }

    @PostMapping("/role")
    public ResponseEntity<String> assignRoleToUser(@RequestHeader("SessionId") String sessionId, @RequestHeader("userId") int userId, @RequestHeader("roleId") String roleId) {
        this.logBusinessLayer.log(String.format("Role %s request to be assigned to %s", roleId, userId), sessionId);
        UserDto responsibleUser;
        UserDto affectedUser;
        UserRole affectedUserRole;
        UserRole currentUserRole;
        Role responsibleRole;
        Role affectedRole;
        try {
            responsibleUser = this.userRepository.getBySessionId(sessionId);
            affectedUser = this.userRepository.getById(userId);
            if (responsibleUser != null && affectedUser != null) {
                currentUserRole = this.roleRepository.getUserRoleByUserId(responsibleUser.userId);
                affectedUserRole = this.roleRepository.getUserRoleByUserId(affectedUser.userId);
                ResponseEntity<Role> responsibleRoleResponse = this.roleRepository.getRoleById(currentUserRole.roleId);
                ResponseEntity<Role> affectedRoleResponse = this.roleRepository.getRoleById(affectedUserRole.roleId);
                if (responsibleRoleResponse.getStatusCode() != HttpStatus.INTERNAL_SERVER_ERROR && affectedRoleResponse.getStatusCode() != HttpStatus.INTERNAL_SERVER_ERROR) {
                    affectedRole = affectedRoleResponse.getBody();
                    responsibleRole = responsibleRoleResponse.getBody();
                    if (responsibleRole.ranking < affectedRole.ranking) {
                        return new ResponseEntity<>("Can't edit this user", HttpStatus.BAD_REQUEST);
                    } else {
                        UserRole newRole = new UserRole(affectedUser.userId, roleId);
                        this.roleRepository.setUserRole(newRole);
                    }
                } else {
                    return new ResponseEntity<>("Can't edit this user", HttpStatus.BAD_REQUEST);
                }
            } else {
                return new ResponseEntity<>("Can't edit this user", HttpStatus.BAD_REQUEST);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new ResponseEntity<>("An error happened", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("Role changes", HttpStatus.OK);
    }

    @RequestMapping(path = "/activation", method = PUT)
    public ResponseEntity<String> activateUser(@RequestHeader("ActivationId") String activationId) {
        this.logBusinessLayer.log(String.format("Activation request", activationId));
        try {
            ActivationToken correspondingToken = this.activationTokenRepository.getTokenById(activationId);
            UserDto correspondingUser = this.userRepository.getById(correspondingToken.userId);
            if (correspondingUser != null && correspondingUser.isActivated) {
                this.logBusinessLayer.log(String.format("User already activated", activationId), correspondingUser.userId);
                return new ResponseEntity<>(JSONMapper.getInstance().JSONStringify("User already activated"), HttpStatus.OK);
            } else {
                this.logBusinessLayer.log(String.format("user activation request", activationId), correspondingToken.userId);
                this.userRepository.activateUser(correspondingToken.userId);
                this.activationTokenRepository.cleanTokensForUser(correspondingToken.userId);
            }
            return new ResponseEntity<>(JSONMapper.getInstance().JSONStringify("User activated"), HttpStatus.OK);
        } catch (SQLException e) {
            return new ResponseEntity<>(JSONMapper.getInstance().JSONStringify("User activated"), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/discord/link")
    public ResponseEntity<String> linkDiscord(@RequestHeader("SessionId") String sessionId, @RequestHeader("code") String discord_code) {
        String response = JSONMapper.getInstance().JSONStringify("Couldn't link the account");
        HttpStatus status = HttpStatus.OK;
        DiscordUser discordUser = null;
        UserDto user = null;
        try {
            user = this.userRepository.getBySessionId(sessionId);
            if (user != null) {
                discordUser = this.discordBusinessLayer.getDiscordUserFromCode(discord_code);
                discordUser.userId = user.userId;
                if (this.discordUserRepository.insertDiscordUser(discordUser)) {
                    this.pubSubHandler.sendDiscordIdToPubSubListener(discordUser.discordId, true);
                    response = JSONMapper.getInstance().JSONStringify("Link successfull");
                    status = HttpStatus.OK;
                }
            }
        } catch (SQLException | IOException | NoSuchAlgorithmException | InvalidKeyException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(response, status);
    }

    @GetMapping("discord/get")
    public ResponseEntity<String> getDiscordUser(@RequestHeader("SessionId") String sessionId) {
        String response = JSONMapper.getInstance().JSONStringify("Couldn't get the discord info");
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        DiscordUser discordUser = null;
        UserDto user = null;
        try {
            user = this.userRepository.getBySessionId(sessionId);
            if (user != null) {
                if (discordUser != null) {
                    response = JSONMapper.getInstance().JSONStringify(discordUser);
                    status = HttpStatus.OK;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(response, status);
    }
}
