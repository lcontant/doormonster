package API.BusinessLayer;

import API.Util.Repositories.ActivationTokenRepository;
import API.Util.Repositories.CommentRepository;
import API.Util.Repositories.SessionRepository;
import API.Util.Repositories.UserRepository;
import java.util.List;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

@Component
public class UserBusinessLayer {
    UserRepository userRepository;
    SessionRepository sessionRepository;
    CommentRepository commentRepository;
    ActivationTokenRepository activationTokenRepository;

    public UserBusinessLayer(UserRepository userRepository, SessionRepository sessionRepository, CommentRepository commentRepository, ActivationTokenRepository activationTokenRepository) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.commentRepository = commentRepository;
        this.activationTokenRepository = activationTokenRepository;
    }

    public boolean deleteUser(int userID) throws SQLException {
        this.activationTokenRepository.cleanTokensForUser(userID);
        return this.userRepository.deleteUserById(userID);
    }

    public void cleanupUnverifiedUsers() {
        List<Long> userIds = this.userRepository.getAllUnverifiedUsersOlderWithModifiedDateOlderThan14h();
        for (Long userId: userIds) {
           this.userRepository.deleteUserById(Math.toIntExact(userId));
        }
    }
}
