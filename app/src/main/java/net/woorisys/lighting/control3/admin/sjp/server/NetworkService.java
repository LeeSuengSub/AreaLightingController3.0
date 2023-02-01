package net.woorisys.lighting.control3.admin.sjp.server;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface NetworkService {

   String onepass="onepass/rest";

    @FormUrlEncoded
    @POST("/"+onepass+"/login")
    Call<LoginResult> Login
            (
                    @Field("userId") String userId,
                    @Field("password") String password
            );



}
