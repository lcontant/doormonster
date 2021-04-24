package API.Model;

import API.Util.Repositories.SupporterRepository;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Supporter {
    public int id;
    public int userId;
    public int ammount;
    public String striperCustomerId;
    public String stripeSubscriptionId;
    public boolean lastPaymentSuccessful;
    public boolean subscriptionIsActive;
    public boolean toBeCanceled;

    public Supporter() {

    }

    public Supporter(int id, int userId, int ammount, String stripe_user_account,String stripe_subscription_id, boolean lastPaymentSuccessful, boolean subscriptionIsActive) {
        this.id = id;
        this.userId = userId;
        this.ammount = ammount;
        this.striperCustomerId = stripe_user_account;
        this.stripeSubscriptionId = stripe_subscription_id;
        this.lastPaymentSuccessful = lastPaymentSuccessful;
        this.subscriptionIsActive = subscriptionIsActive;
    }

    public Supporter(ResultSet rs) throws SQLException {
        this(rs.getInt(SupporterRepository.ID_COLUMN_NAME),
                rs.getInt(SupporterRepository.USER_ID_COLUMN_NAME),
                rs.getInt(SupporterRepository.AMOUNT_COLUMN_NAME),
                rs.getString(SupporterRepository.STRIPE_CUSTOMER_ID_COLUMN_NAME),
                rs.getString(SupporterRepository.STRIPE_SUBSCRIPTION_COLUMN_NAME),
                rs.getBoolean(SupporterRepository.LAST_PAYMENT_SUCCESSFUL_COLUMN_NAME),
                rs.getBoolean(SupporterRepository.SUBSCRIPTION_ACTIVE_COLUMN_NAME));

    }
}
