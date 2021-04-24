package API.BusinessLayer;

import API.BusinessLayer.Patreon.MembershipService;
import API.Model.DiscordUser;
import API.Model.Patreon.PostRequestResponse;
import API.Model.Supporter;
import API.Util.JSONMapper;
import API.Util.Repositories.DiscordUserRepository;
import API.Util.Repositories.SupporterRepository;
import API.Util.Repositories.UserRepository;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.patreon.PatreonAPI;
import com.patreon.PatreonOAuth;
import com.patreon.resources.Pledge;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class PatreonBL {


    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private String accessToken;
    private String refreshToken;
    private String sessionId = "PATREON_SESSSION_TOKEN";
    private String campaingId = "138316";
    private PatreonAPI client;
    private LocalDateTime lastPledgeUpdateDate;
    private List<Pledge> pledges;
    private UserRepository userRepository;
    private MembershipService membershipService;
    private DiscordUserRepository discordUserRepository;
    private SupporterRepository supporterRepository;
    private PubSubHandler pubSubHandler;

    public PatreonBL(UserRepository  userRepository, DiscordUserRepository discordUserRepository, SupporterRepository supporterRepository,
                     PubSubHandler pubSubHandler,
                     @Value("${Patreon.clientId}") String clientId
            , @Value("${Patreon.clientSecret}") String clientSecret
            , @Value("${Patreon.accessToken}") String accessToken
            , @Value("${Patreon.redirectUri}")String redirectUri
            , @Value("${Patreon.refreshToken}") String refreshToken) throws IOException, SQLException, NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException {
        this.accessToken = accessToken;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
        this.refreshToken = refreshToken;
        client = new PatreonAPI(this.accessToken);
        this.userRepository = userRepository;
        this.discordUserRepository = discordUserRepository;
        this.supporterRepository = supporterRepository;
        this.pubSubHandler = pubSubHandler;
        this.membershipService = new MembershipService();
    }

    @Cacheable(cacheNames="patreonPost", key = "#userIsPatron")
    public PostRequestResponse getAllPostsFromPatreon(int patreonTier) {
        HttpURLConnection connection = null;
        //Create connection
        URL url = null;
        try {
            url = new URL(String.format("https://www.patreon.com/api/campaigns/%s/posts?filter[is_by_creator]=true&page[count]=100", campaingId));
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        connection.setRequestProperty("Cookie", "session_id=" + sessionId);

        connection.setUseCaches(false);

        try {
            connection.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        InputStream inputStream = null;
        StringBuilder response = new StringBuilder();
        try {
            inputStream = connection.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
                response.append("\r\n");
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        PostRequestResponse responseModel = JSONMapper.getInstance().getMapper().fromJson(response.toString(), PostRequestResponse.class);
        return responseModel;
    }

    private void updatePledgeList() {
        try {
            pledges = client.fetchAllPledges(campaingId);
            lastPledgeUpdateDate = LocalDateTime.now();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Scheduled(cron = "0 0 12 * * ?")
    @CacheEvict(value = "patreonPost", allEntries = true)
    public void updateAllUserStatus() throws SQLException, InvalidKeySpecException, NoSuchAlgorithmException, InvalidKeyException, IOException {
        this.updatePledgeList();
    }

    private void updatePeopleDiscordStatus() throws SQLException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, IOException {
        List<DiscordUser> discordUsers = this.discordUserRepository.getAllDiscordUsers();
        for (int i = 0; i < discordUsers.size(); i++) {
            Supporter supporter = this.supporterRepository.getSupporterByUserId(discordUsers.get(i).userId);
            this.pubSubHandler.sendDiscordIdToPubSubListener(String.valueOf(discordUsers.get(i).discordId), supporter.subscriptionIsActive);
        }
    }

    public int getUserPatreonConribution(String patreonToken) {
        int currentPatreonContribution = 0;
        try {
            JsonElement response = this.membershipService.getUserMemberFromToken(patreonToken);
            for (JsonElement element : ((JsonObject) response).get("included").getAsJsonArray()) {
                String type = element.getAsJsonObject().get("type").getAsString();
                if (type.equals("member")) {
                    JsonElement attributes = element.getAsJsonObject().get("attributes");
                    JsonElement relationships = element.getAsJsonObject().get("relationships");
                    String campaignId = relationships
                            .getAsJsonObject().get("campaign")
                            .getAsJsonObject().get("data")
                            .getAsJsonObject().get("id")
                            .getAsString();
                    if (campaignId.equals("138316")) {
                        currentPatreonContribution = attributes.getAsJsonObject().get("will_pay_amount_cents").getAsInt();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return currentPatreonContribution;
    }

    public PatreonOAuth.TokensResponse getTokenFromOauthCode(String code) {
        PatreonOAuth oauthClient = new PatreonOAuth(clientId,clientSecret,redirectUri);
        PatreonOAuth.TokensResponse tokensReponse = null;
        try {
            tokensReponse = oauthClient.getTokens(code);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tokensReponse;
    }
}
