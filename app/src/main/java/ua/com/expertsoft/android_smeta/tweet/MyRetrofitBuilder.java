package ua.com.expertsoft.android_smeta.tweet;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by mityai on 26.05.2016.
 */
public class MyRetrofitBuilder {

    public Retrofit getRetrofit(String baseUrl, OkHttpClient client){
        Retrofit.Builder retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create());
        return retrofit.build();
    }
}
