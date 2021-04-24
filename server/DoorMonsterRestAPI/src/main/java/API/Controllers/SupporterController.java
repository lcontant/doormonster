package API.Controllers;

import API.BusinessLayer.LogBusinessLayer;
import API.BusinessLayer.StripeHandler;
import API.Model.Role;
import API.Model.Supporter;
import API.Model.SupporterWithUser;
import API.Model.UserDto;
import API.Util.JSONMapper;
import API.Util.Repositories.RoleRepository;
import API.Util.Repositories.SupporterRepository;
import API.Util.Repositories.UserRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.Card;
import com.stripe.model.Customer;
import com.stripe.model.Subscription;
import com.stripe.model.Token;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import retrofit2.http.Body;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("support")
public class SupporterController {

    StripeHandler stripeHandler;
    UserRepository userRepository;
    SupporterRepository supporterRepository;
    LogBusinessLayer logBusinessLayer;
    RoleRepository roleRepository;

    public SupporterController(StripeHandler stripeHandler, UserRepository userRepository, SupporterRepository supporterRepository, LogBusinessLayer logBusinessLayer, RoleRepository roleRepository) {
        this.stripeHandler = stripeHandler;
        this.userRepository = userRepository;
        this.supporterRepository = supporterRepository;
        this.logBusinessLayer = logBusinessLayer;
        this.roleRepository = roleRepository;
    }

    @GetMapping("/list")
    public ResponseEntity<String> listAllSupporters(@RequestHeader("SessionId") String sessionId) {
        UserDto user = null;
        Role role = null;
        List<SupporterWithUser> supporters;
        String response = JSONMapper.getInstance().JSONStringify("Couldn't get all users");
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        try {
            user = this.userRepository.getBySessionId(sessionId);
            if (user != null ) {
               role = this.roleRepository.getRoleByUserId(user.userId);
               if (role != null && role.ranking == 0) {
                  supporters = this.supporterRepository.getAllSupporterWithUser();
                   response = JSONMapper.getInstance().JSONStringify(supporters);
                   status = HttpStatus.OK;
               }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(response, status);
    }

    @PostMapping("/subscribe")
    public ResponseEntity<String> subscribe(@RequestHeader("SessionId") String sessionId, @RequestBody Token token, @RequestHeader("Amount") int amount) {
        UserDto correspondingUser = null;
        Customer createdCustomer;
        Subscription correspondingSubscription = null;
        Supporter supporter;
        String responesMessage = JSONMapper.getInstance().JSONStringify("There was an error creating the customer");
        HttpStatus responseStatus = HttpStatus.OK;
        String plan = this.stripeHandler.getPlanIdFromAmount(amount);
        if (plan != null) {
            try {
                correspondingUser = this.userRepository.getBySessionId(sessionId);
                if (correspondingUser != null) {
                    createdCustomer = this.stripeHandler.createCustomerId(token.getId(), correspondingUser);

                    correspondingSubscription = this.stripeHandler.createSubscriptionForCustomer(createdCustomer.getId(), plan);
                    supporter = new Supporter(-1,
                            correspondingUser.userId,
                            amount, createdCustomer.getId(),
                            correspondingSubscription.getId(),
                            correspondingSubscription.getStatus().equals("active"),
                            true);
                    supporter = this.supporterRepository.registerSupporter(supporter);
                    this.logBusinessLayer.log("Subscribed for " + amount / 100 + "$ a month", sessionId);
                    responesMessage = JSONObject.valueToString(supporter);
                    responseStatus = HttpStatus.OK;
                }
            } catch (SQLException |  StripeException e) {
                e.printStackTrace();
                if (correspondingSubscription != null && correspondingUser != null) {
                    try {
                        this.stripeHandler.cancelSubscriptionForUser(correspondingUser);
                        this.logBusinessLayer.log("Error " + e.getMessage() + " while trying to subscribe for " + amount / 100  + "$", sessionId);
                    } catch (SQLException | StripeException e1) {
                        e1.printStackTrace();
                        this.logBusinessLayer.log(e.getMessage());
                    }
                }
            }
        }
        return new ResponseEntity<>(responesMessage, responseStatus);
    }

    @GetMapping("/periodEnd")
    public ResponseEntity<String> getPeriodEnd(@RequestHeader("SessionId") String sessionId) {
        String responseMessage = JSONMapper.getInstance().JSONStringify("Couldn't acquire subscription information");
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        Date date = null;
        UserDto user = null;
        try {
            user = this.userRepository.getBySessionId(sessionId);
            if (user != null) {
                long timestampLong = this.stripeHandler.getPeriodEndForSubscription(user.userId);
                Timestamp timestamp = new Timestamp(timestampLong * 1000);
                date = new Date(timestamp.getTime());
                responseMessage = JSONMapper.getInstance().JSONStringify(date);
                status = HttpStatus.OK;
            }
        } catch (SQLException | StripeException e) {
            e.printStackTrace();
            this.logBusinessLayer.log(e.getMessage());
        }
        return new ResponseEntity<>(responseMessage, status);
    }

    @PutMapping("/renew")
    public ResponseEntity<String> renewSubscription(@RequestHeader("SessionId") String sessionId) {
        String responseMessage = JSONMapper.getInstance().JSONStringify("Couldn't update subscription information");
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        UserDto user = null;
        try {
            user = this.userRepository.getBySessionId(sessionId);
            if (user != null) {
                boolean updatedSuccessful = this.stripeHandler.renewSubscription(user.userId);
                if (updatedSuccessful) {
                    responseMessage = JSONMapper.getInstance().JSONStringify("Subscription renewed");
                    status = HttpStatus.OK;
                    this.logBusinessLayer.log("Subscription renewed", sessionId);
                }
            }
        } catch (SQLException | StripeException e) {
            e.printStackTrace();
            this.logBusinessLayer.log(e.getMessage());
        }
        return new ResponseEntity<>(responseMessage, status);
    }

    @PutMapping("/unsubscribe")
    public ResponseEntity<String> unsubscribe(@RequestHeader("SessionId") String sessionId) {
        String responseMessage = JSONMapper.getInstance().JSONStringify("Couldn't cancel the subscription");
        HttpStatus responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        UserDto correspondingUser;
        boolean subscriptionUpdated = false;
        try {
            correspondingUser = this.userRepository.getBySessionId(sessionId);
            if (correspondingUser != null) {
                subscriptionUpdated = this.stripeHandler.cancelSubscriptionForUser(correspondingUser);
                if (subscriptionUpdated) {
                    responseMessage = JSONMapper.getInstance().JSONStringify("Subscription updated");
                    responseStatus = HttpStatus.OK;
                    this.logBusinessLayer.log("Cancelled subscription", sessionId);
                }
            }
        } catch (SQLException | StripeException e) {
            e.printStackTrace();
            this.logBusinessLayer.log(e.getMessage());
        }
        return new ResponseEntity<>(responseMessage, responseStatus);
    }

    @GetMapping("/current")
    public ResponseEntity<String> getCurrentSupporterBySessionId(@RequestHeader("SessionId") String sessionId) {
        UserDto correspondingUser = null;
        Supporter correspondingSupporter = null;
        String responseMessage = JSONObject.valueToString("No corresponding supporter");
        HttpStatus responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        try {
            correspondingUser = this.userRepository.getBySessionId(sessionId);
            if (correspondingUser != null) {
                correspondingSupporter = this.supporterRepository.getActiveSupporterByUserId(correspondingUser.userId);
                if (correspondingSupporter != null) {
                    //Removing stripe specific info because security
                    boolean toBeCanceled = this.stripeHandler.getSubscriptionToBeCanceled(correspondingUser.userId);
                    correspondingSupporter.toBeCanceled = toBeCanceled;
                    correspondingSupporter.stripeSubscriptionId = null;
                    correspondingSupporter.striperCustomerId = null;
                    responseMessage = JSONMapper.getInstance().JSONStringify(correspondingSupporter);
                    responseStatus = HttpStatus.OK;
                } else {
                    correspondingSupporter = null;
                    responseMessage = JSONObject.valueToString(null);
                    responseStatus = HttpStatus.OK;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            this.logBusinessLayer.log(e.getMessage());
        } catch (StripeException e) {
            e.printStackTrace();
            this.logBusinessLayer.log(e.getMessage());
        }
        return new ResponseEntity<>(responseMessage, responseStatus);
    }

    @DeleteMapping("/card/delete")
    public ResponseEntity<String> deleteCardForCustomer(@RequestHeader("SessionId") String sessionId, @RequestHeader("CardID") String cardId) {
        String responseMessage = JSONMapper.getInstance().JSONStringify("Couldn't delete the card");
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        try {
            UserDto user = this.userRepository.getBySessionId(sessionId);
            if (user != null) {
                Supporter supporter = this.supporterRepository.getActiveSupporterByUserId(user.userId);
                List<Card> cards = this.stripeHandler.getCardsForCustomer(supporter.striperCustomerId);
                if (cards.size() > 1) {
                    if (supporter != null) {
                         List<Card> returnCards = this.stripeHandler.deleteCardForCustomer(supporter.striperCustomerId, cardId);
                        if (returnCards != null) {
                            responseMessage = JSONMapper.getInstance().JSONStringify(returnCards);
                            status = HttpStatus.OK;
                            this.logBusinessLayer.log("Added a new card", sessionId);
                        }
                    }
                } else {
                    status = HttpStatus.BAD_REQUEST;
                    responseMessage = JSONObject.valueToString("The user has only one card left");
                }
            }
        } catch (SQLException | StripeException e) {
            e.printStackTrace();
            this.logBusinessLayer.log(e.getMessage());
        }
        return new ResponseEntity<>(responseMessage, status);
    }

    @PutMapping("/card/update")
    public ResponseEntity<String> updateCardForCustomer(@RequestHeader("SessionId") String sessionId, @RequestHeader("cardID") String cardID, @Body Map<String, Object> updateParams) {
        String responseMessage = JSONMapper.getInstance().JSONStringify("Couldn't update the card");
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        try {
            UserDto user = this.userRepository.getBySessionId(sessionId);
            if (user != null) {
                Supporter supporter = this.supporterRepository.getActiveSupporterByUserId(user.userId);
                if (supporter != null) {
                    List<Card> cards = this.stripeHandler.updateCardForCustomer(updateParams, cardID, supporter.striperCustomerId);
                    if (cards != null) {
                        responseMessage = JSONMapper.getInstance().JSONStringify(cards);
                        status = HttpStatus.OK;
                    }
                }
            }
        } catch (SQLException | StripeException e) {
            e.printStackTrace();
            this.logBusinessLayer.log(e.getMessage());
        }
        return new ResponseEntity<>(responseMessage, status);
    }

    @PostMapping("/card/create")
    public ResponseEntity<String> addNewCardForCustomer(@RequestHeader("SessionId") String sessionId, @RequestBody Map<String, Object> card) {
        String responseMessage = JSONMapper.getInstance().JSONStringify("Couldn't add card");
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        try {
            if (card != null) {
                UserDto user = this.userRepository.getBySessionId(sessionId);
                if (user != null) {
                    Supporter supporter = this.supporterRepository.getActiveSupporterByUserId(user.userId);
                    if (supporter != null) {
                        List<Card> cards = this.stripeHandler.createNewCardForCustomer(card.get("id").toString(), supporter.striperCustomerId);
                        if (cards != null) {
                            responseMessage = JSONMapper.getInstance().JSONStringify(cards);
                            status = HttpStatus.OK;
                            this.logBusinessLayer.log("Added a card", sessionId);
                        }
                    }
                }
            } else {
                responseMessage = JSONMapper.getInstance().JSONStringify("missing token in requestBody");
                status = HttpStatus.BAD_REQUEST;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            this.logBusinessLayer.log(e.getMessage());
        }
        return new ResponseEntity<>(responseMessage, status);
    }

    @GetMapping("/card/list")
    public ResponseEntity<String> listAllCardsForSupporter(@RequestHeader("SessionId") String sessionId) {
        String responesMessage = JSONMapper.getInstance().JSONStringify("Couldn't get the cards");
        HttpStatus status = HttpStatus.OK;
        try {
            UserDto user = this.userRepository.getBySessionId(sessionId);
            if (user != null) {
                Supporter supporter = this.supporterRepository.getActiveSupporterByUserId(user.userId);
                if (supporter != null) {
                    List<Card> cards = this.stripeHandler.getCardsForCustomer(supporter.striperCustomerId);
                    if (cards != null) {
                        responesMessage = JSONMapper.getInstance().JSONStringify(cards);
                        status = HttpStatus.OK;
                    }
                } else {
                    responesMessage = JSONMapper.getInstance().JSONStringify("No active subscription");
                    status = HttpStatus.BAD_REQUEST;
                }
            } else {
                responesMessage = JSONMapper.getInstance().JSONStringify("Bad sessionId token");
                status = HttpStatus.BAD_REQUEST;
            }
        } catch (SQLException | StripeException e) {
            e.printStackTrace();
            this.logBusinessLayer.log(e.getMessage());
        }
        return new ResponseEntity<>(responesMessage, status);
    }

    @PutMapping("/upgrade")
    public ResponseEntity<String> upgradePlan(@RequestHeader("SessionId") String sessionId, @RequestHeader("ammount") int ammount) {
        String response = JSONMapper.getInstance().JSONStringify("Couldn't update the plan");
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        UserDto user;
        Supporter supporter;
        String planId;
        String previousplan;
        int previousAmmount = -1;
        try {
            user = this.userRepository.getBySessionId(sessionId);
            if (user != null) {
                supporter = this.supporterRepository.getSupporterByUserId(user.userId);
                if (supporter != null) {
                    planId = this.stripeHandler.getPlanIdFromAmount(ammount);
                    if (planId != null) {
                        previousAmmount = supporter.ammount;
                        previousplan = this.stripeHandler.getPlanIdFromAmount(previousAmmount);
                        Subscription subscription = this.stripeHandler.upgradePlan(supporter.stripeSubscriptionId, planId);
                        if (subscription != null) {
                            supporter.ammount = Math.toIntExact(subscription.getPlan().getAmount());
                            boolean updateSuccessFull = this.supporterRepository.updateSupporterByUserId(supporter);
                            if (!updateSuccessFull) {
                                this.stripeHandler.upgradePlan(supporter.stripeSubscriptionId, previousplan);
                            } else {
                                response = JSONMapper.getInstance().JSONStringify(supporter);
                                status = HttpStatus.OK;
                                this.logBusinessLayer.log("Upgraded to " + ammount / 100 + "$ a month", sessionId);
                            }
                        }
                    } else {
                        response = JSONMapper.getInstance().JSONStringify("Wrong ammount, no corresponding plan");
                        status = HttpStatus.BAD_REQUEST;
                    }
                } else {
                    response = JSONMapper.getInstance().JSONStringify("No currentSubscription");
                    status = HttpStatus.BAD_REQUEST;
                }
            } else {
                response = JSONMapper.getInstance().JSONStringify("No corresponding user");
                status = HttpStatus.BAD_REQUEST;
            }
        } catch (SQLException | StripeException e) {
            e.printStackTrace();
            this.logBusinessLayer.log(e.getMessage());
        }
        return new ResponseEntity<>(response, status);
    }

}
