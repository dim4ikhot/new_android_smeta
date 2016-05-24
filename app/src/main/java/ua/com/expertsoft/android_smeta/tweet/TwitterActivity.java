package ua.com.expertsoft.android_smeta.tweet;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import ua.com.expertsoft.android_smeta.R;

public class TwitterActivity extends AppCompatActivity {

    static String CONSUMER_KEY = "fRuPxDVC1J58r05jdJIDn7qCJ";
    static String CONSUMER_SECRET = "pPsf4VkVOFmpIhr3IQXEyqbza7ISIvGRWKjF8W8CsGAEqqt5At";
    static String CALLBACK_URL = "x-oauthflow-twitter://twitterlogin";
    public static String ACCESS_TOKEN_KEY = "access_token_key";
    public static String ACCESS_SECRET_KEY = "access_token_secret";
    public static String IS_AUTHORIZED_KEY = "is_authorized";

    static Twitter twitter;
    static RequestToken requestToken;
    static AccessToken accessToken;
    static String verifier;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twitter);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(! checkIsAuthorized()) {
                        new Authorize().execute((Void) null);
                    }
                    else{
                        String accessToken_ = preferences.getString(ACCESS_TOKEN_KEY,"");
                        String accessSecret = preferences.getString(ACCESS_SECRET_KEY, "");

                        accessToken = new AccessToken(accessToken_,accessSecret);
                        twitter = new TwitterFactory().getInstance(accessToken);

                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl("http://api.twitter.com/oauth/")
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();
                        TwitterRetrofit tr = retrofit.create(TwitterRetrofit.class);
                    }
                }
            });
        }
    }

    public boolean checkIsAuthorized(){
        return preferences.getBoolean(IS_AUTHORIZED_KEY, false);
    }

    public void setKeysAfterSuccessAuthorised(){
        SharedPreferences.Editor e = preferences.edit();
        e.putString(ACCESS_TOKEN_KEY, accessToken.getToken());
        e.putString(ACCESS_SECRET_KEY, accessToken.getTokenSecret());
        e.putBoolean(IS_AUTHORIZED_KEY, true);
        e.apply();
    }

    public class Authorize extends AsyncTask<Void,Void,String>{
        @Override
        protected String doInBackground(Void... params) {

            twitter = new TwitterFactory().getInstance();
            twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
            try {
                requestToken = twitter.getOAuthRequestToken(CALLBACK_URL);
            }catch(Exception e){
                e.printStackTrace();
            }

            return requestToken.getAuthorizationURL();
        }

        public void onPostExecute(String result){
            AuthorizeDialog dialog = new AuthorizeDialog();
            Bundle params = new Bundle();
            params.putString("AuthorizationURL", result);
            dialog.setArguments(params);
            dialog.show(getSupportFragmentManager(), "AuthorizeDialog");
            //startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri.parse(result)), 0);
        }
    }

    public static class AuthorizeDialog extends DialogFragment{

        public AuthorizeDialog(){}

        public Dialog onCreateDialog(Bundle params){
            final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(R.layout.browser_authorize, null, false);
            WebView webView = (WebView)v.findViewById(R.id.webView);
            dialog.setView(v);
            if(webView != null){
                webView.getSettings().setJavaScriptEnabled(true);
                Bundle param = getArguments();
                webView.loadUrl(param.getString("AuthorizationURL"));
                webView.setWebViewClient(new WebViewClient() {
                    boolean authComplete = false;

                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        super.onPageStarted(view, url, favicon);
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        if (url.contains("oauth_verifier") && authComplete == false) {
                            authComplete = true;
                            Log.e("AsyncTask", url);
                            Uri uri = Uri.parse(url);
                            verifier = uri.getQueryParameter("oauth_verifier");
                            dismiss();
                            //revoke access token asynctask
                            new GetAccessToken((TwitterActivity) getActivity()).execute();
                        } else if (url.contains("denied")) {

                        }
                    }
                });
            }
            return dialog.create();
        }
    }


    public static class GetAccessToken extends AsyncTask<Void,Void,AccessToken>{

        TwitterActivity twitterActivity;

        public GetAccessToken(TwitterActivity activity){
            twitterActivity = activity;
        }

        @Override
        protected AccessToken doInBackground(Void... params) {
            try{
                accessToken = twitter.getOAuthAccessToken(requestToken, verifier);
            }catch(Exception e){
                e.printStackTrace();
            }
            return accessToken;
        }

        public void onPostExecute(AccessToken result){
            twitterActivity.setKeysAfterSuccessAuthorised();
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
    }

}
