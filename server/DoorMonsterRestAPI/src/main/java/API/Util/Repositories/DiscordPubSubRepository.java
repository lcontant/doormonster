package API.Util.Repositories;

import API.Model.DBConnection;
import API.Model.PubSubListenner;
import API.Util.SQLConnector.ConnectionPoolManager;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class DiscordPubSubRepository {
    public static final String TABLE_NAME = "DISCORD_PUB_SUB";
    public static String KEY_COLUMN_NAME = "KEY";
    public static String URL_COLUMN_NAME = "ENDPOINT";
    public static String CHALLENGE_KEY = "CHALLENGE_KEY";
    public static String CHALLENGE_ENDPOINT = "CHALLENGE_ENDPOINT";
    public static String VERIFIED_COLUMN_NAME = "IS_VERIFIED";
    public static String ID_COLUMN_NAME = "ID";

    private ConnectionPoolManager connectionPoolManager;

    private static String ADD_SUBSCRIBER_REQUEST = String.format("INSERT INTO %s (%s.%s, %s,%s,%s) values (?, ?,?,?)", TABLE_NAME, TABLE_NAME, KEY_COLUMN_NAME, URL_COLUMN_NAME, CHALLENGE_KEY, CHALLENGE_ENDPOINT);
    private static String GET_ALL_SUBSCRIBER_REQUEST = String.format("Select * from %s", TABLE_NAME);
    private static String GET_ALL_VERIFIED_SUBSCRIBER_REQUEST = String.format("Select * from %s where %s = 1", TABLE_NAME, VERIFIED_COLUMN_NAME);
    private static String GET_SUBCRIBER_BY_CHALLENGE_KEY_REQUEST = String.format("Select * from %s where %s = ?", TABLE_NAME, CHALLENGE_KEY);
    private static String VALIDATE_SUBSCRIBER_REQUEST = String.format("Update %s set %s = 1 where %s.%s = ?", TABLE_NAME, VERIFIED_COLUMN_NAME, TABLE_NAME, KEY_COLUMN_NAME);
    private static String UPDATE_SUBSCRIBER_KEY_REQUEST = String.format("Update %s set %s.%s = ? where %s = ?", TABLE_NAME, TABLE_NAME,KEY_COLUMN_NAME, ID_COLUMN_NAME);
    private static String GET_SUBSCRIBER_BY_ENDPOINT = String.format("Select * from %s where %s = ?", TABLE_NAME, URL_COLUMN_NAME);

    private DBConnection dbConnection;
    private Connection connection;

    private PreparedStatement addSubscriberRequest;
    private PreparedStatement getAllSubscribersRequest;
    private PreparedStatement getSubsciberByChallengeKey;
    private PreparedStatement validateSubscriberStatement;
    private PreparedStatement updateSubscriberKeyStatement;
    private PreparedStatement getSubscriberByEndpoint;
    private PreparedStatement getVerifiedSubcriberStatement;

    public DiscordPubSubRepository(ConnectionPoolManager connectionPoolManager) throws SQLException {
        this.connectionPoolManager = connectionPoolManager;
        this.dbConnection = this.connectionPoolManager.getConnection();
        this.connection = this.dbConnection.getConnection();
        this.initStatements();
    }

    private void initConnnection() throws SQLException {
        this.dbConnection = this.connectionPoolManager.getConnection();
        this.connection = this.dbConnection.getConnection();
        this.initStatements();
    }

    private void initStatements() throws SQLException {
        this.addSubscriberRequest = this.connection.prepareStatement(ADD_SUBSCRIBER_REQUEST);
        this.getAllSubscribersRequest = this.connection.prepareStatement(GET_ALL_SUBSCRIBER_REQUEST);
        this.getSubsciberByChallengeKey = this.connection.prepareStatement(GET_SUBCRIBER_BY_CHALLENGE_KEY_REQUEST);
        this.validateSubscriberStatement = this.connection.prepareStatement(VALIDATE_SUBSCRIBER_REQUEST);
        this.updateSubscriberKeyStatement = this.connection.prepareStatement(UPDATE_SUBSCRIBER_KEY_REQUEST);
        this.getSubscriberByEndpoint = this.connection.prepareStatement(GET_SUBSCRIBER_BY_ENDPOINT);
        this.getVerifiedSubcriberStatement = this.connection.prepareStatement(GET_ALL_VERIFIED_SUBSCRIBER_REQUEST);
    }
    public synchronized boolean addSubscriber(PubSubListenner pubSubSub) throws SQLException {
        if (connection.isClosed()) {
            this.initConnnection();
        }
        boolean subcribtionSuccessful = false;
        int numRowsUpdated = 0;
        try {
            this.addSubscriberRequest.setString(1, pubSubSub.key);
            this.addSubscriberRequest.setString(2, pubSubSub.endpoint);
            this.addSubscriberRequest.setString(3, pubSubSub.challengKey);
            this.addSubscriberRequest.setString(4, pubSubSub.challengeEndpoint);

            numRowsUpdated = this.addSubscriberRequest.executeUpdate();
            subcribtionSuccessful = numRowsUpdated == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return subcribtionSuccessful;
    }

    public synchronized List<PubSubListenner> getAllSubscribers() throws SQLException {
        if (connection.isClosed()) {
            this.initConnnection();
        }
        List<PubSubListenner> subListenners = new ArrayList<>();
        ResultSet rs;
        try {
            rs = this.getAllSubscribersRequest.executeQuery();
            while (rs.next()) {
                subListenners.add(new PubSubListenner(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return subListenners;
    }

    public synchronized PubSubListenner getSubcriberByKey(String key) throws SQLException {
        if (connection.isClosed()) {
            this.initConnnection();
        }
        PubSubListenner correspondingListener = null;
        ResultSet rs;
        System.out.println("Getting by challenge key " + key);
        this.getSubsciberByChallengeKey.setString(1, key);
        System.out.println(this.getSubsciberByChallengeKey.toString());
        rs = this.getSubsciberByChallengeKey.executeQuery();
        if (rs.next()) {
            correspondingListener = new PubSubListenner(rs);
        }
        return correspondingListener;
    }

    public synchronized PubSubListenner validatePubSubListenner(PubSubListenner listener) throws SQLException {
        if (connection.isClosed()) {
            this.initConnnection();
        }
        this.validateSubscriberStatement.setString(1, listener.key);
        int numRowsUpadted = this.validateSubscriberStatement.executeUpdate();
        PubSubListenner listenner = this.getSubcriberByKey(listener.key);
        return listener;
    }

    public boolean updateSubscriberKey(PubSubListenner listenner, int id) throws SQLException {
        if (connection.isClosed()) {
            this.initConnnection();
        }
        int numRowsUpdated = 0;
        this.updateSubscriberKeyStatement.setString(1, listenner.key);
        this.updateSubscriberKeyStatement.setInt(2, id);
        System.out.println(this.updateSubscriberKeyStatement.toString());
        numRowsUpdated = this.updateSubscriberKeyStatement.executeUpdate();
        return numRowsUpdated == 1;
    }

    public PubSubListenner getSubscriberByEnpoint(String endpoint) throws SQLException {
        if (connection.isClosed()) {
            this.initConnnection();
        }
        PubSubListenner listenner = null;
        this.getSubscriberByEndpoint.setString(1, endpoint);
        ResultSet rs = this.getSubscriberByEndpoint.executeQuery();
        if (rs.next()) {
            listenner = new PubSubListenner(rs);
        }
        return listenner;
    }

    public List<PubSubListenner> getVerifiedSubscriberRequest() throws SQLException {
        ResultSet rs = this.getVerifiedSubcriberStatement.executeQuery();
        List<PubSubListenner> pubSubListenners = new ArrayList<>();
        while (rs.next()) {
            pubSubListenners.add(new PubSubListenner(rs));
        }
        return pubSubListenners;
    }
}
