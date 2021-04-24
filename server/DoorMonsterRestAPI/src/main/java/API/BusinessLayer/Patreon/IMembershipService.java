package API.BusinessLayer.Patreon;

import com.google.gson.JsonElement;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface IMembershipService {

    @GET("/api/oauth2/v2/identity?fields%5Bmember%5D=will_pay_amount_cents&include=memberships.campaign&fields%5Bcampaign%5D=vanity")
    public Call<JsonElement> getMembership(@Header("Authorization") String authorization);
}
