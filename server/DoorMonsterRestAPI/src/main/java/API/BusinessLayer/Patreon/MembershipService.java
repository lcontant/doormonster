package API.BusinessLayer.Patreon;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

public class MembershipService {

    private static final String  BASE_API_URL = "https://www.patreon.com/";

    Retrofit retrofit;
    IMembershipService membershipService;

    public MembershipService() {
        Gson gson = new GsonBuilder().setLenient().create();
        GsonConverterFactory gsonConverterFactory = GsonConverterFactory.create(gson);

        this.retrofit = new Retrofit.Builder()
                .baseUrl(BASE_API_URL)
                .addConverterFactory(new ToStringConverterFactory())
                .addConverterFactory(gsonConverterFactory)
                .build();
        this.membershipService = retrofit.create(IMembershipService.class);
    }

    public JsonElement getUserMemberFromToken(String token) throws IOException {
        String authorization = "Bearer " + token;
        Call<JsonElement> call = this.membershipService.getMembership(authorization);
        Response<JsonElement> response = call.execute();
        return response.body();
    }
}
