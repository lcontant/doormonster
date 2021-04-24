package API.Util.Repositories;

import API.Model.DBConnection;
import API.Model.Feedback;
import API.Ressource.FeedbackWithUser;
import API.Util.SQLConnector.ConnectionPoolManager;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class FeedbackRepository {

    public static final String TABLE_NAME = "FEEDBACK";
    public static final String ID_COLUMN_NAME = "ID";
    public static final String USER_ID_COLUMN_NAME = "USER_ID";
    public static final String CONTENT_COLUMN_NAME = "CONTENT";
    public static final String CREATED_ON_COLUMN = "CreatedOn";

    private final String insertFeedbackRequest = String.format("Insert into %s (%s, %s) values (?, ?)", TABLE_NAME, USER_ID_COLUMN_NAME, CONTENT_COLUMN_NAME);
    private final String getAllFeedbackRequest = String.format("Select * from %s as f join %s as u on u.%s = f.%s order by f.%s desc", TABLE_NAME, UserRepository.USER_TABLE_NAME, UserRepository.ID_COLUMN_NAME, USER_ID_COLUMN_NAME, CREATED_ON_COLUMN);

    private DBConnection dbConnection;
    private Connection connection;
    private ConnectionPoolManager connectionPoolManager;
    private UserRepository userRepository;

    private PreparedStatement insertFeedbackStatement;
    private PreparedStatement getAllFeedbackStatement;

    public FeedbackRepository(ConnectionPoolManager connectionPoolManager, UserRepository userRepository) throws SQLException {
        this.connectionPoolManager = connectionPoolManager;
        this.userRepository = userRepository;
        this.initConnection();
    }

    private void initConnection() throws SQLException {
        this.dbConnection = this.connectionPoolManager.getConnection();
        this.connection = this.dbConnection.getConnection();
        this.initStatements();
    }

    private void initStatements() throws SQLException {
        this.insertFeedbackStatement = this.connection.prepareStatement(insertFeedbackRequest);
        this.getAllFeedbackStatement = this.connection.prepareStatement(getAllFeedbackRequest);
    }

    public synchronized boolean insertFeedback(Feedback feedback) throws SQLException {
         if (this.connection.isClosed()) {
            this.initConnection();
         }
        insertFeedbackStatement.setInt(1, feedback.userId);
        insertFeedbackStatement.setString(2, feedback.content);
        int numRowsUpdate = insertFeedbackStatement.executeUpdate();
        return numRowsUpdate == 1;
    }

    public synchronized List<FeedbackWithUser> getAllFeedbackRequest() throws SQLException {
        if (this.connection.isClosed()) {
            this.initConnection();
        }
        List<FeedbackWithUser> feedback = new ArrayList<>();
        ResultSet rs = this.getAllFeedbackStatement.executeQuery();
        while (rs.next()) {
            FeedbackWithUser it = new FeedbackWithUser(rs);
            if (it.feedback.content.replace("\"", "").length() > 0) {
                feedback.add(it);
            }
        }
        return feedback;
    }


}
