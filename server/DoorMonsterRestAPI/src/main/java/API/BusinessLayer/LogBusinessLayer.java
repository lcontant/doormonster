package API.BusinessLayer;

import API.Model.Log;
import API.Model.UserDto;
import API.Util.Repositories.LogRepository;
import API.Util.Repositories.UserRepository;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

@Component
public class LogBusinessLayer {

    private LogRepository logRepository;
    private UserRepository userRepository;

    public LogBusinessLayer(LogRepository logRepository, UserRepository userRepository) {
        this.userRepository = userRepository;
        this.logRepository = logRepository;
    }

    public void log(String message) {
        Log log = new Log();
        log.message = message;
        try {
            this.logRepository.insertLog(log);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void log(String message, String sessionId) {
        Log log = new Log();
        UserDto user = null;
        try {
            user = this.userRepository.getBySessionId(sessionId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (user != null) {
            log.user_id = user.userId;
        }
        log.message = message;
        try {
            this.logRepository.insertLog(log);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void log(String message, int userID) {
        Log log = new Log();
        log.user_id = userID;
        log.message = message;
        try {
            this.logRepository.insertLog(log);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void LogError(Exception exception) {
        Log log = new Log();
        StackTraceElement[] stackTraceElements = exception.getStackTrace();
        String stackTrace = "";
        for (StackTraceElement stackTraceElement : stackTraceElements) {
            stackTrace += stackTraceElement.getClassName() + ":" + stackTraceElement.getLineNumber();
        }
        log.message = stackTrace;
        try {
            this.logRepository.insertLog(log);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
