package API.BusinessLayer;

import API.Model.Supporter;
import API.Model.UserDto;
import API.Util.Repositories.SupporterRepository;
import API.Util.Repositories.UserRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.Card;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class StripeHandlerTest {
    @Autowired
    StripeHandler stripeHandler;

    @Autowired
    SupporterRepository supporterRepository;

    @Autowired
    UserRepository userRepository;


    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void getCardsForLouisShouldReturnSomething() throws SQLException, StripeException {
        UserDto user = this.userRepository.getByUserName("Louis Contant");
        Supporter supporter = this.supporterRepository.getActiveSupporterByUserId(user.userId);
        List<Card> cards = this.stripeHandler.getCardsForCustomer(supporter.striperCustomerId);
        assertNotNull(cards);
        assertTrue(cards.size() > 0);
    }
}