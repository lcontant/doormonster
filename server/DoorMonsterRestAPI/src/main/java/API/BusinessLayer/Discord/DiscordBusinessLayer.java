package API.BusinessLayer.Discord;

import API.Model.Discord.AccessTokenResponse;
import API.Model.Discord.User;
import API.Model.DiscordUser;
import API.Util.JSONMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

@Component
public class DiscordBusinessLayer {
    Retrofit retrofit;
    String clientId;
    String clientSecret;
    String scopes;
    String redirectUrls;
    String grantType;
    IDiscordApiClient discordClientService;

    private Logger logger = LogManager.getLogger("RequestLogger");
    public DiscordBusinessLayer(@Value("${Discord.clientId}") String cliendId
            ,@Value("${Discord.clientSecret}") String clientSecret
            ,@Value("${Discord.redirect}")String redirectUrls
            ,@Value("${Discord.scopes}")String scopes
            ,@Value("${Discord.grantType}") String grantType) {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        this.retrofit = new Retrofit.Builder()
                .baseUrl("https://discordapp.com/api/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        this.discordClientService = this.retrofit.create(IDiscordApiClient.class);
        this.clientId = cliendId;
        this.clientSecret = clientSecret;
        this.redirectUrls = redirectUrls;
        this.scopes = scopes;
        this.grantType = grantType;
    }

    private User getCurrentUser(String token) throws IOException {
        User user = null;
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://discordapp.com/api/users/@me")
                .get()
                .addHeader("Authorization", "Bearer " + token)
                .build();

        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            user = JSONMapper.getInstance().getMapper().fromJson(response.body().string(), User.class);
        }
        return user;
    }

    private AccessTokenResponse getTokenFromCode(String code) throws IOException {
        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "client_id=573124024331403275" +
                "&client_secret=Qr9RDbEc0OwIzj3tHg5glDbSxGv7HCGC" +
                "&grant_type=authorization_code" +
                "&code=" + code +
                "&redirect_uri=" + this.redirectUrls + "&scope=identify");
        Request request = new Request.Builder()
                .url("https://discordapp.com/api/oauth2/token")
                .post(body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();

        okhttp3.Response response = client.newCall(request).execute();
        AccessTokenResponse accessTokenResponse = null;
        if (response.isSuccessful()) {
           accessTokenResponse = JSONMapper.getInstance().getMapper().fromJson(response.body().string(),AccessTokenResponse.class);
        } else {
           this.logger.error("Error eccountered with discord : " + response.body().string());
        }
        return accessTokenResponse;
    }

    public DiscordUser getDiscordUserFromCode(String code) throws IOException {
        DiscordUser discordUser = new DiscordUser();
        AccessTokenResponse accessTokenResponse =  this.getTokenFromCode(code);
        if (accessTokenResponse != null) {
            discordUser = new DiscordUser();
            discordUser.discordToken =  accessTokenResponse.access_token;
            discordUser.discordRefreshToken = accessTokenResponse.refresh_token;
            User user = this.getCurrentUser(discordUser.discordToken);
            discordUser.discordId = user.id;
            discordUser.discordUsername = user.username;
        }
        return discordUser;
    }


}
