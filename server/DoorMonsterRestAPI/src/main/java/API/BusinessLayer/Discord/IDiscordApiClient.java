package API.BusinessLayer.Discord;

import API.Model.Discord.AccessTokenResponse;
import API.Model.Discord.User;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.HashMap;

public interface IDiscordApiClient {

    @GET("/users/@me")
    Call<User> getCurrentUser(@Header("Authorization") String token);

    @FormUrlEncoded
    @POST("/oauth2/token")
    Call<AccessTokenResponse> getTokenFromCode(@Field("client_id") String clientId
            , @Field("client_secret") String clientSecret
            , @Field("grant_type") String grantType
            , @Field("code") String code
            , @Field("redirect_uri")String redirect_uri
            , @Field("scope") String scopes);

}
