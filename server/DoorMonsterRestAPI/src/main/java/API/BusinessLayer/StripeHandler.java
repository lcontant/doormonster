package API.BusinessLayer;


import API.Model.Supporter;
import API.Model.UserDto;
import API.Util.Repositories.SupporterRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class StripeHandler {

    private SupporterRepository supporterRepository;
    private String apiKey;

    public StripeHandler(SupporterRepository supporterRepository, @Value("${Stripe.key}") String stripeKey) throws StripeException {
        this.supporterRepository = supporterRepository;
        this.apiKey = stripeKey;
        Stripe.apiKey = this.apiKey;
    }

    public Customer createCustomerId(String cardToken, UserDto user) throws  StripeException {
        Stripe.apiKey = this.apiKey;
        Map<String, Object> customer_creation_params = new HashMap<>();
        customer_creation_params.put("description", String.format("User %s id:%s", user.email, user.userId));
        customer_creation_params.put("source", cardToken);
        Customer customer = Customer.create(customer_creation_params);
        return customer;
    }

    public Subscription createSubscriptionForCustomer(String customerId, String planId) throws StripeException {
        Stripe.apiKey = this.apiKey;
        Map<String, Object> item = new HashMap<String, Object>();
        item.put("plan", planId);

        Map<String, Object> items = new HashMap<String, Object>();
        items.put("0", item);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("customer", customerId);
        params.put("items", items);

        return Subscription.create(params);
    }

    public String getPlanIdFromAmount(int amount) {
        String planId = null;
        switch (amount) {
            case 100:
                planId= "gary";
                break;
            case 500:
                planId="zucchini_milkshake";
                break;
            case 1000:
                 planId="ikea_new";
                break;
            case 2000:
                planId="hatch_ladder";
                break;
            case 5000:
                planId = "boat-mormon";
                break;
            case 50000:
                planId = "living-forever";
                break;
        }
        return planId;

    }

    public Subscription upgradePlan(String subscriptionId,String planId) throws StripeException, SQLException   {
        Stripe.apiKey = this.apiKey;
        Subscription subscription = Subscription.retrieve(subscriptionId);
        Map<String, Object> item = new HashMap<>();
        item.put("id", subscription.getSubscriptionItems().getData().get(0).getId());
        item.put("plan", planId);

        Map<String, Object> items = new HashMap<>();
        items.put("0", item);

        Map<String, Object> params = new HashMap<>();
        params.put("cancel_at_period_end", false);
        params.put("items", items);

        return subscription.update(params);
    }

    public boolean cancelSubscriptionForUser(UserDto user) throws SQLException, StripeException {
        Stripe.apiKey = this.apiKey;
        Supporter correspondingSupporter = this.supporterRepository.getActiveSupporterByUserId(user.userId);
        Boolean subscriptionUpdated = false;
        if (correspondingSupporter != null) {
            Subscription subscription = Subscription.retrieve(correspondingSupporter.stripeSubscriptionId);
            Map<String, Object> params = new HashMap<>();
            params.put("cancel_at_period_end", true);
            subscription.update(params);
            subscriptionUpdated = true;
        }
        return subscriptionUpdated;
    }

    public String getCurrentSubscriptionStatus(int userId) throws SQLException, StripeException {
        Supporter supporter = this.supporterRepository.getActiveSupporterByUserId(userId);
        String status = null;
        if (supporter != null) {
            Subscription subscription = Subscription.retrieve(supporter.stripeSubscriptionId);
            status =subscription.getStatus();
        }
        return status;
    }

    public boolean getSubscriptionToBeCanceled(int userId) throws SQLException, StripeException {
        Supporter suppoerter = this.supporterRepository.getActiveSupporterByUserId(userId);
        Stripe.apiKey = this.apiKey;
        boolean toBeCanceled = false;
        if (suppoerter !=  null) {
            Subscription subscription = Subscription.retrieve(suppoerter.stripeSubscriptionId);
            toBeCanceled = subscription.getCancelAtPeriodEnd();
        }

        return toBeCanceled;
    }

    public long  getPeriodEndForSubscription(int userId) throws SQLException, StripeException {
        Supporter supporter = this.supporterRepository.getSupporterByUserId(userId);
        Stripe.apiKey = this.apiKey;
        long periodEnd = -1;
        if (supporter != null) {
            Subscription subscription = Subscription.retrieve(supporter.stripeSubscriptionId);
            periodEnd = subscription.getCurrentPeriodEnd();
        }
        return periodEnd;
    }

    public boolean renewSubscription(int userId) throws SQLException, StripeException {
        Supporter supporter = this.supporterRepository.getSupporterByUserId(userId);
        Stripe.apiKey = this.apiKey;
        Map<String, Object> params = new HashMap<>();
        params.put("cancel_at_period_end", false);
        boolean subscriptionUpdated = false;
        if (supporter != null) {
            Subscription subscription = Subscription.retrieve(supporter.stripeSubscriptionId);
            subscription = subscription.update(params);
            subscriptionUpdated = !subscription.getCancelAtPeriodEnd();
        }
        return subscriptionUpdated;
    }

    @Scheduled(cron = "0 0 12 * * ?")
    public void checkSubscriptionsStatus() throws StripeException {
        Stripe.apiKey = this.apiKey;

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("limit", 100);
        params.put("status", "all");
        SubscriptionCollection subscriptionsRequest = Subscription.list(params);
        Iterable<Subscription> subscriptions = subscriptionsRequest.autoPagingIterable();
        for (Subscription subscription : subscriptions) {
            String status = subscription.getStatus();
            try {
                Supporter correspondingSupporter = this.supporterRepository.getSupporterByCustomerId(subscription.getCustomer());
                if (correspondingSupporter != null) {
                    correspondingSupporter.ammount = Math.toIntExact(subscription.getPlan().getAmount());
                    switch (status) {
                        case "active":
                            correspondingSupporter.lastPaymentSuccessful = true;
                            correspondingSupporter.subscriptionIsActive = true;
                            break;
                        case "unpaid":
                            correspondingSupporter.lastPaymentSuccessful = false;
                        case "canceled":
                            correspondingSupporter.subscriptionIsActive = false;
                            break;
                        default:
                            correspondingSupporter.lastPaymentSuccessful = false;
                            correspondingSupporter.subscriptionIsActive = false;
                            break;
                    }
                    this.supporterRepository.updateSupporterByUserId(correspondingSupporter);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public List<Card> getCardsForCustomer(String customerId) throws StripeException {
        Stripe.apiKey = this.apiKey;
        Customer customer = Customer.retrieve(customerId);
        Map<String, Object> cardParams = new HashMap<>();
        cardParams.put("object", "card");
        ExternalAccountCollection externalAccountCollection = customer.getSources().list(cardParams);
        List<Card> cards = new ArrayList<>();
        for (ExternalAccount externalAccount: externalAccountCollection.autoPagingIterable()) {
            Card currentCard = (Card) customer.getSources().retrieve(externalAccount.getId());
            if (currentCard != null) {
                cards.add(currentCard);
            }
        }
        return cards;
    }

    public List<Card> deleteCardForCustomer(String customerId,String cardId) throws StripeException {
        Stripe.apiKey = this.apiKey;
        List<Card> cards = new ArrayList<>();
        Customer customer = Customer.retrieve(customerId);
        customer.getSources().retrieve(cardId).delete();
        cards = getCardsForCustomer(customerId);
        return cards;
    }

    public List<Card> updateCardForCustomer(Map<String, Object> updateParam, String cardId, String customerId) throws StripeException {
        Stripe.apiKey = this.apiKey;
        List<Card> cards = new ArrayList<>();
        Customer customer = Customer.retrieve(customerId);
        if (customer != null) {
            Card card = (Card) customer.getSources().retrieve(cardId);
            if (card != null) {
                card.update(updateParam);
            }
            return getCardsForCustomer(customerId);
        }
        return null;
    }

    public List<Card> createNewCardForCustomer(String token, String customerId) {
        Stripe.apiKey = this.apiKey;
        List<Card> cards = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        Card card = null;
        param.put("source", token);
        try {
            Customer customer = Customer.retrieve(customerId);
            if (customer != null){
                card = (Card) customer.getSources().create(param);
                if (card != null) {
                    cards = this.getCardsForCustomer(customerId);
                }
            }
        } catch (StripeException e) {
            e.printStackTrace();
        }
        return cards;
    }
}
