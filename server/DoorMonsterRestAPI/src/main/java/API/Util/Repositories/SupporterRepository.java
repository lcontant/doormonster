package API.Util.Repositories;

import API.Model.DBConnection;
import API.Model.Supporter;
import API.Model.SupporterWithUser;
import API.Util.SQLConnector.ConnectionPoolManager;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class SupporterRepository {
    public static final String TABLE_NAME = "SUPPORTER";
    public static final String ID_COLUMN_NAME = "ID";
    public static final String USER_ID_COLUMN_NAME = "USER_ID";
    public static final String AMOUNT_COLUMN_NAME = "AMOUNT";
    public static final String LAST_PAYMENT_SUCCESSFUL_COLUMN_NAME = "LAST_PAYMENT_SUCCESFULL";
    public static final String STRIPE_CUSTOMER_ID_COLUMN_NAME = "STRIPE_CUSTOMER_ID";
    public static final String STRIPE_SUBSCRIPTION_COLUMN_NAME = "STRIPE_SUBSCRIPTION_ID";
    public static final String SUBSCRIPTION_ACTIVE_COLUMN_NAME = "SUBSCRIPTION_ACTIVE";

    private final String GET_SUPPORTER_BY_USERID_REQUEST = String.format("Select * from %s where %s = ?", TABLE_NAME, USER_ID_COLUMN_NAME);
    private final String INSERT_SUPPORTER_REQUEST = String.format("Insert into %s (%s,%s,%s, %s, %s, %s) values(?,?,?,?,?,?)", TABLE_NAME, USER_ID_COLUMN_NAME, AMOUNT_COLUMN_NAME, STRIPE_CUSTOMER_ID_COLUMN_NAME, STRIPE_SUBSCRIPTION_COLUMN_NAME, SUBSCRIPTION_ACTIVE_COLUMN_NAME, LAST_PAYMENT_SUCCESSFUL_COLUMN_NAME);
    private final String GET_SUPPORTER_BY_CUSTOMER_ID_REQUEST = String.format("Select * from %s where %s = ?", TABLE_NAME, STRIPE_CUSTOMER_ID_COLUMN_NAME);
    private final String UPDATE_SUPPORTER_BY_USER_ID = String.format("Update %s set %s=?, %s =?, %s=?,%s=?, %s=? where %s = ?", TABLE_NAME, AMOUNT_COLUMN_NAME, STRIPE_CUSTOMER_ID_COLUMN_NAME, STRIPE_SUBSCRIPTION_COLUMN_NAME, LAST_PAYMENT_SUCCESSFUL_COLUMN_NAME, SUBSCRIPTION_ACTIVE_COLUMN_NAME, USER_ID_COLUMN_NAME);
    private final String GET_ACTIVE_SUPPORTER_BY_USER_ID = String.format("Select * from %s where %s=1 AND %s = ?", TABLE_NAME, SUBSCRIPTION_ACTIVE_COLUMN_NAME, USER_ID_COLUMN_NAME);
    private final String GET_ALL_ACTIVE_SUPPORTERS_WITH_USER = String.format("Select * from %s as s join %s as u on s.%s = u.%s and %s = true", TABLE_NAME, UserRepository.USER_TABLE_NAME, USER_ID_COLUMN_NAME, UserRepository.ID_COLUMN_NAME, SUBSCRIPTION_ACTIVE_COLUMN_NAME);

    private PreparedStatement getSupporterByUserIdStatement;
    private PreparedStatement insertSupporterStatement;
    private PreparedStatement getSupporterByCustomerIdStatement;
    private PreparedStatement updateSupporterByUserIdStatement;
    private PreparedStatement getActiveSupporterByUserId;
    private PreparedStatement getAllSupportersWithUserStatement;

    private ConnectionPoolManager poolManager;

    private Connection connection;
    private DBConnection dbConnection;

    public SupporterRepository(ConnectionPoolManager poolManager) throws SQLException {
        this.poolManager = poolManager;
        this.initConnection();
    }

    private void initConnection() throws SQLException {
        this.dbConnection = this.poolManager.getConnection();
        this.connection = this.dbConnection.getConnection();
       this.initStatements();
    }

    private void initStatements() throws SQLException {
        this.getSupporterByUserIdStatement = this.connection.prepareStatement(GET_SUPPORTER_BY_USERID_REQUEST);
        this.insertSupporterStatement = this.connection.prepareStatement(INSERT_SUPPORTER_REQUEST);
        this.getSupporterByCustomerIdStatement = this.connection.prepareStatement(GET_SUPPORTER_BY_CUSTOMER_ID_REQUEST);
        this.updateSupporterByUserIdStatement = this.connection.prepareStatement(UPDATE_SUPPORTER_BY_USER_ID);
        this.getActiveSupporterByUserId = this.connection.prepareStatement(GET_ACTIVE_SUPPORTER_BY_USER_ID);
        this.getAllSupportersWithUserStatement = this.connection.prepareStatement(GET_ALL_ACTIVE_SUPPORTERS_WITH_USER);
    }

    public Supporter getSupporterByUserId(int userID) throws SQLException {
        connectionCheck();
        ResultSet rs;
        Supporter supporter = null;
        this.getSupporterByUserIdStatement.setInt(1, userID);
        rs = this.getSupporterByUserIdStatement.executeQuery();
        if (rs.next()) {
            supporter = new Supporter(rs);
        }
        return supporter;
    }

    private void connectionCheck() throws SQLException {
        if (this.connection.isClosed()) {
           this.initConnection();
        }
    }

    public Supporter registerSupporter(Supporter supporter) throws SQLException {
        connectionCheck();
        int rowsUpdated = 0;
        Supporter updatedSupporter;
        this.insertSupporterStatement.setInt(1,supporter.userId);
        this.insertSupporterStatement.setInt(2, supporter.ammount);
        this.insertSupporterStatement.setString(3, supporter.striperCustomerId);
        this.insertSupporterStatement.setString(4, supporter.stripeSubscriptionId);
        this.insertSupporterStatement.setBoolean(5, supporter.subscriptionIsActive);
        this.insertSupporterStatement.setBoolean(6, supporter.lastPaymentSuccessful);
        rowsUpdated = this.insertSupporterStatement.executeUpdate();
        if (rowsUpdated == 1) {
            updatedSupporter = this.getSupporterByUserId(supporter.userId);
        } else {
            updatedSupporter = null;
        }
        return updatedSupporter;
    }

    public Supporter getSupporterByCustomerId(String customerId) throws SQLException {
        connectionCheck();
        Supporter correspondingSupporter = null;
        this.getSupporterByCustomerIdStatement.setString(1, customerId);
        ResultSet rs = this.getSupporterByCustomerIdStatement.executeQuery();
        if (rs.next()) {
            correspondingSupporter = new Supporter(rs);
        }
        return correspondingSupporter;
    }

    public boolean updateSupporterByUserId(Supporter supporter) throws SQLException {
        connectionCheck();
        int numRowsUpdate = 0;
        this.updateSupporterByUserIdStatement.setInt(1, supporter.ammount);
        this.updateSupporterByUserIdStatement.setString(2, supporter.striperCustomerId);
        this.updateSupporterByUserIdStatement.setString(3, supporter.stripeSubscriptionId);
        this.updateSupporterByUserIdStatement.setBoolean(4, supporter.lastPaymentSuccessful);
        this.updateSupporterByUserIdStatement.setBoolean(5, supporter.subscriptionIsActive);
        this.updateSupporterByUserIdStatement.setInt(6, supporter.userId);
        numRowsUpdate = this.updateSupporterByUserIdStatement.executeUpdate();
        return numRowsUpdate == 1;
    }

    public Supporter getActiveSupporterByUserId(int userID) throws SQLException {
        connectionCheck();
        ResultSet rs;
        Supporter correspondingSupporter = null;
        this.getActiveSupporterByUserId.setInt(1, userID);
        rs = this.getActiveSupporterByUserId.executeQuery();
        if (rs.next()) {
            correspondingSupporter = new Supporter(rs);
        }
        return correspondingSupporter;
    }

    public List<SupporterWithUser> getAllSupporterWithUser() throws SQLException {
        connectionCheck();
        ResultSet rs = this.getAllSupportersWithUserStatement.executeQuery();
        List<SupporterWithUser> supporterWithUsers = new ArrayList<>();
        while (rs.next()){
            supporterWithUsers.add(new SupporterWithUser(rs));
        }
        return supporterWithUsers;
    }


}
