package ua.com.expertsoft.android_smeta.tweet;

import retrofit2.http.Body;
import retrofit2.http.POST;

/*
 * Created by mityai on 24.05.2016.
 */
public interface TwitterRetrofit {

    @POST("request_token")
    String executeRequestToken(@Body String header);

    @POST("access_token")
    String executeAccessToken();
}
