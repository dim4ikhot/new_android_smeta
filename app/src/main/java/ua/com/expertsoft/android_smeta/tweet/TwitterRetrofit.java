package ua.com.expertsoft.android_smeta.tweet;

import android.util.Xml;

import java.net.URLEncoder;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/*
 * Created by mityai on 24.05.2016.
 */
public interface TwitterRetrofit {

    String toQuery = "?oauth_consumer_key=fRuPxDVC1J58r05jdJIDn7qCJ&oauth_nonce=244ab5464a8c3674346efd0d0f4ceb68&oauth_signature=s5iKi85PUcYRAb6A5zbT8FSqPBU%3D&oauth_signature_method=HMAC-SHA1&oauth_timestamp=1464246657&oauth_token=3996686661-Zrhwq55zZpMV83YudVke38hm8oeECSNXrwwsati&oauth_version=1.0";

    @POST("request_token")
    String executeRequestToken(@Body String header);

    @POST("access_token")
    String executeAccessToken();

    @GET("account/verify_credentials.json")
    Call<VerifyCredentials> getVerifyCredentials();
}
