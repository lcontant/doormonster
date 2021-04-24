package API.Util.Repositories;

import API.Model.PubSubListenner;
import API.Util.SQLConnector.ConnectionManager;
import API.Util.SQLConnector.ConnectionPoolManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.*;

public class VideoPubSubRepositoryTest {

    ConnectionPoolManager connectionPoolManager;
    @Before
    public void setUp() throws Exception {
        connectionPoolManager = new ConnectionPoolManager("whiteli4_wlhqdata_dev","2idCX73JzHUV3y9f", "root", "www.doormonster.tv");
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void addSubscriber() throws SQLException {
        PubSubListenner listenner = new PubSubListenner();
        listenner.challengKey = "awefawef";
        listenner.challengeEndpoint = "-";
        listenner.endpoint = "test";
        listenner.key = "testtest";
        VideoPubSubRepository pubSubRepository = new VideoPubSubRepository(connectionPoolManager);
        pubSubRepository.addSubscriber(listenner);
    }

    @Test
    public void getAllSubscribers() {
    }

    @Test
    public void getSubscriberByGet() {
    }
}