package example.com.demofon;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface IsApi {

    @POST("token")
    @FormUrlEncoded
    Call<TokenModel> getToken(@Field("code")String code,
                              @Field("client_id")String clientId,
                              @Field("client_secret")String secret,
                              @Field("redirect_uri")String redirectUri,
                              @Field("grant_type") String type);

    @GET("domofon/relays?pagesize=20&pagination=1")
    Call<List<RelayModel>> getRelaysList();

    @POST("domofon/relays/{id}/open")
    Call<Object> open(@Path("id")String id);
}
