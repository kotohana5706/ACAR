package kr.edcan.acar.utils;

import kr.edcan.acar.models.FacebookUser;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by JunseokOh on 2016. 9. 3..
 */
public interface NetworkInterface {

    @POST("/user/update/pushtoken")
    @FormUrlEncoded
    Call<ResponseBody> pushToken(
            @Field("gcm_token") String gcmToken,
            @Field("id") String userId
    );


    @GET("/auth/facebook/token")
    Call<FacebookUser> userLogin(
        @Query("access_token") String accessToken
    );
}
