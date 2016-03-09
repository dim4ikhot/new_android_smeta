package ua.com.expertsoft.android_smeta.settings;


import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.MenuItem;

import java.util.Locale;

import ua.com.expertsoft.android_smeta.MainActivity;
import ua.com.expertsoft.android_smeta.R;
import ua.com.expertsoft.android_smeta.language.UpdateLanguage;

/**
 * Created by mityai on 15.02.2016.
 */
public class SettingsActivity extends AppCompatActivity implements FragmentSettings.onChangeLanguageListener {

    ActionBar bar;
    CharSequence title;

    //TODO "B"
    @Override
    public void onBackPressed(){
        finish();
    }

    //TODO "C"
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateAppConfiguration();
        bar = getSupportActionBar();
        title = getResources().getString(R.string.preference_main);
        if(bar != null){
            bar.setTitle(title);
            bar.setHomeButtonEnabled(true);
            bar.setDisplayHomeAsUpEnabled(true);
        }
        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new FragmentSettings())
                .commit();

    }

    @Override
    public void onChangeLanguage() {
        refreshActivity();
    }


    //TODO "O"
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    //TODO "R"

    private void refreshActivity(){
        updateAppConfiguration();
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }


    //TODO "U"
    public void updateAppConfiguration(){
        UpdateLanguage.updateAppConfiguration(this, new UpdateLanguage.onUpdateLocaleListener() {
            @Override
            public void onUpdateLocale() {
                invalidateOptionsMenu();
            }
        });
    }

}
