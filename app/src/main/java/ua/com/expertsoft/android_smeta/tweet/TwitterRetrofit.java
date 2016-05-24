package ua.com.expertsoft.android_smeta.tweet;

import retrofit.http.POST;

/*
 * Created by mityai on 24.05.2016.
 */
public interface TwitterRetrofit {

    @POST("/request_token")
    String executeRequestToken();

    @POST("/access_token")
    String executeAccessToken();
}
