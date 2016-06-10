package ua.com.expertsoft.android_smeta;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.LinearLayout;

import ua.com.expertsoft.android_smeta.admob.DynamicAdMob;
import ua.com.expertsoft.android_smeta.language.UpdateLanguage;
import ua.com.expertsoft.android_smeta.static_data.SelectedWork;
import ua.com.expertsoft.android_smeta.data.Works;

public class ShowWorksParam extends AppCompatActivity {

    TabLayout worksTab;
    ViewPager viewPager;
    public TabsPagerAdapter adapter;
    CharSequence mTitle;
    ActionBar bar;
    Works currWork;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        updateAppConfiguration();
        setContentView(R.layout.activity_show_works_param);
        new DynamicAdMob(this, (LinearLayout)findViewById(R.id.params_main_screen)).showAdMob();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        bar = getSupportActionBar();
        mTitle = getResources().getString(R.string.title_activity_show_works_param);
        currWork = SelectedWork.work;//(Works)getIntent().getSerializableExtra("selectedWork");
        if(currWork != null) {
            mTitle = mTitle + " " + currWork.getWName();
            if (bar != null) {
                bar.setHomeButtonEnabled(true);
                bar.setDisplayHomeAsUpEnabled(true);
                bar.setTitle(mTitle);
            }
            worksTab = (TabLayout) findViewById(R.id.worksTabs);
            viewPager = (ViewPager) findViewById(R.id.viewPager);
            adapter = new TabsPagerAdapter(getSupportFragmentManager(), this, currWork);
            viewPager.setAdapter(adapter);
            try {
                worksTab.setupWithViewPager(viewPager);
                worksTab.setTabTextColors(R.color.colorAccent, R.color.colorNoImpotent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
/*
    @Override
    public void onBackPressed(){
        Intent intent = new Intent();
        intent.putExtra("changedWork",currWork);
        setResult(RESULT_OK,intent);
        finish();
    }*/

    public void onBackPressed1(){
        Intent intent = new Intent();
        //intent.putExtra("changedWork",currWork);
        SelectedWork.work = currWork;
        setResult(RESULT_OK,intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case android.R.id.home:
                onBackPressed1();
                break;
        }
        return super.onOptionsItemSelected(item);
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
