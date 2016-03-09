package ua.com.expertsoft.android_smeta;

import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import ua.com.expertsoft.android_smeta.language.UpdateLanguage;
import ua.com.expertsoft.android_smeta.static_data.CommonData;
import ua.com.expertsoft.android_smeta.asynktasks.LoadingNavigatinMenu;

public class SplashActivity extends AppCompatActivity implements LoadingNavigatinMenu.OnTaskFinished {

    LoadingNavigatinMenu loadingNavigatinMenu;
    public ProgressBar waitProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateAppConfiguration();
        setContentView(R.layout.activity_splash);
        ((ImageView) findViewById(R.id.imageSplash)).setImageResource(R.mipmap.splash_image);
//        View decorView = getWindow().getDecorView();
//        // Hide the status bar.
//        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
//        decorView.setSystemUiVisibility(uiOptions);
        waitProgress = (ProgressBar) findViewById(R.id.waitProgress);
    }

    protected void onStart(){
        super.onStart();
        Handler handler = new Handler();
        // run a thread after 2 seconds to start the home screen
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadingNavigatinMenu = (LoadingNavigatinMenu)getLastCustomNonConfigurationInstance();
                if(loadingNavigatinMenu == null) {
                    loadingNavigatinMenu = new LoadingNavigatinMenu(CommonData.context,
                            CommonData.navigation,
                            CommonData.database,
                            CommonData.userCollection, SplashActivity.this);
                    loadingNavigatinMenu.execute(0);
                }
            }
        }, 2000);
    }

    @Override
    public void onFinished() {
        finish();
    }

    public Object onRetainCustomNonConfigurationInstance() {
        return loadingNavigatinMenu;
    }

    public void updateAppConfiguration(){
        UpdateLanguage.updateAppConfiguration(this, new UpdateLanguage.onUpdateLocaleListener() {
            @Override
            public void onUpdateLocale() {
                invalidateOptionsMenu();
            }
        });
    }
}
