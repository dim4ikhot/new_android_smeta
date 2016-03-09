package ua.com.expertsoft.android_smeta.CustomCalendar;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import ua.com.expertsoft.android_smeta.R;
import ua.com.expertsoft.android_smeta.SplashActivity;
import ua.com.expertsoft.android_smeta.data.CalendarDate;

/**
 * Created by mityai on 18.02.2016.
 */
public class CalendarShower extends AppCompatActivity implements View.OnLongClickListener{

    ExCalendar cal;
    TextView month, year;

    protected void onCreate(Bundle params){
        super.onCreate(params);
        setContentView(R.layout.calendar_layout);
        month = (TextView)findViewById(R.id.showMonth);
        year = (TextView)findViewById(R.id.showYear);
        cal = (ExCalendar)findViewById(R.id.ex_calendar);
        month.setText(cal.getShortMonthName());
        year.setText(cal.getCurrentYear());
        cal.setOnLongClickListener(this);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onStart(){
        super.onStart();
        Handler handler = new Handler();
        // run a thread after 2 seconds to start the home screen
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(isMyServiceRunning(ShowNotification.class)){
                   stopService(new Intent(CalendarShower.this, ShowNotification.class));
                }
                startService(new Intent(CalendarShower.this, ShowNotification.class));

            }
        });

    }

    @Override
    public boolean onLongClick(View v) {
        Toast.makeText(this, "ALL CALENDAR CLICKABLE", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        MenuItem showFrame =  menu.findItem(R.id.showFrame);
        MenuItem hideFrame =  menu.findItem(R.id.hideFrame);
        MenuItem showBack =  menu.findItem(R.id.showBackGround);
        MenuItem hideBack =  menu.findItem(R.id.hideBackGround);
        if (cal.getShowBackground()){
            hideBack.setVisible(false);
            showBack.setVisible(true);
        }else{
            hideBack.setVisible(true);
            showBack.setVisible(false);
        }

        if(cal.getShowFrame()){
            hideFrame.setVisible(false);
            showFrame.setVisible(true);
        }else{
            hideFrame.setVisible(true);
            showFrame.setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.calendar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.circleFrame:
                cal.setFrameType(0);
                break;
            case R.id.rectFrame:
                cal.setFrameType(1);
                break;
            case R.id.showFrame:
                cal.setShowFrame(true);
                break;
            case R.id.hideFrame:
                cal.setShowFrame(false);
                break;
            case R.id.showBackGround:
                cal.setShowBackground(true);
                break;
            case R.id.hideBackGround:
                cal.setShowBackground(false);
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void nextMonthClick(View v){
        cal.nextMonth();
        month.setText(cal.getShortMonthName());
        year.setText(cal.getCurrentYear());
    }

    public void priorMonthClick(View v){
        cal.priorMonth();
        month.setText(cal.getShortMonthName());
        year.setText(cal.getCurrentYear());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        switch(requestCode){
            case 1:
                cal.refreshSelectedItemTag(data.getSerializableExtra("calendarDate"));
                break;
        }
    }
}
