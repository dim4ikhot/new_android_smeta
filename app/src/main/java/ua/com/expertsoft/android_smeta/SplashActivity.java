package ua.com.expertsoft.android_smeta;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;

import ua.com.expertsoft.android_smeta.language.UpdateLanguage;
import ua.com.expertsoft.android_smeta.static_data.CommonData;
import ua.com.expertsoft.android_smeta.asynctasks.LoadingNavigatinMenu;

public class SplashActivity extends AppCompatActivity implements LoadingNavigatinMenu.OnTaskFinished {

    LoadingNavigatinMenu loadingNavigatinMenu;
    public ProgressBar waitProgress;
    ImageView splash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateAppConfiguration();
        setContentView(R.layout.activity_splash);
        splash = (ImageView) findViewById(R.id.imageSplash);
        if(splash != null) {
            splash.setImageResource(R.mipmap.splash_image);
        }
//        View decorView = getWindow().getDecorView();
//        Hide the status bar.
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
        MainActivity.startLoadFile();
        finish();
        //new EndlessLog().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,(Void)null);
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
