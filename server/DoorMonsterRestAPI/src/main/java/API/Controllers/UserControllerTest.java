package API.Controllers;

import API.BusinessLayer.UserBusinessLayer;
import API.Model.SessionDto;
import API.Model.UserDto;
import API.Util.JSONMapper;
import API.Util.Repositories.SessionRepository;
import API.Util.Repositories.UserRepository;
import com.google.gson.reflect.TypeToken;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class UserControllerTest {

    @Autowired
    UserController userController;

    @Autowired
    SessionRepository sessionRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserBusinessLayer userBusinessLayer;

    public UserControllerTest() {
    }

    @Before
    public void setUp() throws Exception {
    }

    private UserDto createBasicUser() {
        UserDto user = new UserDto();
        user.username = "test";
        user.setPassword("Some password");
        user.email = "uniqueTestEmail@mail.com";
        user.avatar = "";
        return user;
    }

    @Test
    public void getAllUsersReturns500WithInvalidSessionId() throws SQLException {
        ResponseEntity<String> response = this.userController.getAllUsers("badSession");
        assertSame(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    public void getAllUsersReturns500ForValidButUnauthorizedSessionId() throws SQLException {
        SessionDto sessionDto = this.sessionRepository.createSession("test", 72);
        ResponseEntity<String> response = this.userController.getAllUsers(sessionDto.sessionId);
        this.sessionRepository.deleteSession(sessionDto.sessionId);
        assertSame(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    public void getAllUsersReturnsAFullListOfUsersWithValidSessionId() throws SQLException {
        SessionDto sessionDto = this.sessionRepository.createSession("test", 71);
        ResponseEntity<String> response = this.userController.getAllUsers("test");
        this.sessionRepository.deleteSession(sessionDto.sessionId);
        List<UserDto> users = JSONMapper.getInstance().getMapper().fromJson(response.getBody(), new TypeToken<List<UserDto>>() {
        }.getType());
        assertTrue(users.size() > 0);
    }

    @Test
    public void createUserShouldFailWhenTheUserDoesntHaveAUserNameOrAnEmail() {
        UserDto user = this.createBasicUser();
        user.username = null;
        user.email = null;
        ResponseEntity<String> response = this.userController.createUser(user);
        assertSame(response.getStatusCode(), BAD_REQUEST);
    }

    @Test
    public void createUserShouldFailWhenTheUserDoesntHaveAPassword() {
        UserDto user = this.createBasicUser();
        user.setPassword(null);
        ResponseEntity<String> response = this.userController.createUser(user);
        assertSame(response.getStatusCode(), BAD_REQUEST);
        assertEquals(response.getBody(), JSONMapper.getInstance().JSONStringify("Password required"));
    }

    @Test
    public void createUserShouldFailWhenUsernameIsInUse() {
        UserDto user = this.createBasicUser();
        user.username = "test";
        ResponseEntity<String> response = this.userController.createUser(user);
        assertSame(response.getStatusCode(), BAD_REQUEST);
        assertEquals(response.getBody(), JSONMapper.getInstance().JSONStringify("Username already in use"));
    }

    @Test
    public void createUserShouldFailWhenEmailIsInUse() {
        UserDto user = this.createBasicUser();
        user.email = "test@mail.com";
        ResponseEntity<String> response = this.userController.createUser(user);
        assertSame(response.getStatusCode(), BAD_REQUEST);
        assertEquals(response.getBody(), JSONMapper.getInstance().JSONStringify("Email already in use"));
    }

    @Test
    public void createUserShouldWorkWithUniqueUsernameAndEmailAndWithAPassword() throws SQLException {
        UserDto user = this.createBasicUser();
        user.username = "UniqueUsername";
        UserDto exisitingUser = this.userRepository.getByUserName(user.username);
        if (exisitingUser != null) {
            this.userBusinessLayer.deleteUser(exisitingUser.userId);
        }
        ResponseEntity<String> response = this.userController.createUser(user);
        UserDto createdUser = this.userRepository.getByUserName("UniqueUserName");
        this.userBusinessLayer.deleteUser(createdUser.userId);
        assertSame(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void logoutUserShouldReturnBadRequestForInvalidSessionId() {
        ResponseEntity<Boolean> response = this.userController.logoutUser("InvalidSessionId");
        assertSame(response.getStatusCode(), BAD_REQUEST);
    }

    @Test
    public void logoutUserShouldDeleteTheSession() throws SQLException {
        UserDto user = new UserDto();
        user.username = "test";
        user.setPassword("test");
        ResponseEntity<String> response = this.userController.authenticateUser(user);
        SessionDto sessionDto = JSONMapper.getInstance().getMapper().fromJson(response.getBody(), SessionDto.class);
        this.userController.logoutUser(sessionDto.sessionId);
        assertNull(this.sessionRepository.getSession(sessionDto.sessionId));
    }

    @Test
    public void resendConfirmationShouldAnswerWithBadRequestForInvalidSessionId() {
        ResponseEntity<String> response = this.userController.resendConfirmationEmail("Bad sessionId");
        assertSame(BAD_REQUEST, response.getStatusCode());
    }


    @Test
    public void resendConfirmationShouldAnswer200() throws SQLException {
        SessionDto preExisitingSessionDto = this.sessionRepository.getSession("TestSession");
        if (preExisitingSessionDto != null) {
            this.sessionRepository.deleteSession("TestSession");
        }
        SessionDto sessionDto = this.sessionRepository.createSession("TestSession", 71);
        ResponseEntity<String> response = this.userController.resendConfirmationEmail(sessionDto.sessionId);
        this.sessionRepository.deleteSession(sessionDto.sessionId);
        assertSame(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void updateUserShouldFailForNonAdminWhenReferencingOtherUser() throws SQLException {
        UserDto user = this.userRepository.getById(74);
        SessionDto sessionDto = this.sessionRepository.createSession("test", 79);
        ResponseEntity<String> responseEntity = this.userController.updateUser(user, sessionDto.sessionId);
        this.sessionRepository.deleteSession(sessionDto.sessionId);
        assertSame(BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void updateShouldFailIfUserChangesToAlreadyUsedUsername() throws SQLException {
        UserDto user = this.userRepository.getAll().get(0);
        UserDto conflictingUser = this.userRepository.getAll().get(1);
        String cachingUsername = user.username;
        user.username = conflictingUser.username;
        SessionDto sessionDto = this.sessionRepository.createSession("test", user.userId);
        ResponseEntity<String> responseEntity = this.userController.updateUser(user, sessionDto.sessionId);
        this.sessionRepository.deleteSession(sessionDto.sessionId);
        assertSame(BAD_REQUEST, responseEntity.getStatusCode());
        user = this.userRepository.getAll().get(0);
        assertEquals(user.username, cachingUsername);
    }

    @Test
    public void udpateUserShouldFailToAlreadyUsedEmail() throws SQLException {
        List<UserDto> users = this.userRepository.getAll();
        UserDto user = users.get(0);
        UserDto conflictingUser = users.get(1);
        String originalEmail = user.email;
        user.email = conflictingUser.email;
        SessionDto sessionDto = this.sessionRepository.createSession("test", user.userId);
        ResponseEntity<String> responseEntity = this.userController.updateUser(user, sessionDto.sessionId);
        this.sessionRepository.deleteSession(sessionDto.sessionId);
        assertSame(BAD_REQUEST, responseEntity.getStatusCode());
        user = this.userRepository.getAll().get(0);
        assertEquals(user.email, originalEmail);
    }

    @Test
    public void updateUserShouldFailForInvalidSessionId() throws SQLException {
        UserDto user = this.userRepository.getAll().get(0);
        ResponseEntity<String> response = this.userController.updateUser(user, "badSessionId");
        assertSame(BAD_REQUEST, response.getStatusCode());
    }


}