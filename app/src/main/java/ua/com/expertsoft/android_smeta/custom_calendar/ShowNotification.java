package ua.com.expertsoft.android_smeta.custom_calendar;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import ua.com.expertsoft.android_smeta.R;
import ua.com.expertsoft.android_smeta.data.CalendarDate;
import ua.com.expertsoft.android_smeta.data.DBORM;
import ua.com.expertsoft.android_smeta.data.UserTask;

public class ShowNotification extends Service {

    DBORM database;
    ArrayList<UserTask> tasks;

    long startTime = 0;
    GregorianCalendar date;
    GregorianCalendar time;
    NotificationManager nm;
    //runs without a timer by reposting this handler at the end of the runnable
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;
            date = new GregorianCalendar();
            date.set(Calendar.HOUR_OF_DAY, 0);
            date.set(Calendar.MINUTE, 0);
            date.set(Calendar.SECOND, 0);
            date.set(Calendar.MILLISECOND, 0);

            time = new GregorianCalendar();
            time.set(Calendar.DAY_OF_WEEK, 0);
            time.set(Calendar.MONTH, 0);
            time.set(Calendar.YEAR, 0);
            time.set(Calendar.SECOND, 0);
            time.set(Calendar.MILLISECOND, 0);

            tasks = database.getUsersTasksByDateTime(date.getTime(), time.getTime());
            if(tasks.size() != 0){
                nm =  (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                sendNotif();
            }
            timerHandler.postDelayed(this, 60 * 1000);
        }
    };

    void sendNotif() {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setOngoing(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentTitle("Tasks for today")
                .setContentText("You have " + tasks.size() + " task(s) for today");

        // 3-я часть
        Intent intent = new Intent(this, ViewCalendarTasks.class);
        CalendarDate caln = new CalendarDate();
        caln.setAllTasks(tasks);
        caln.setDate(date.getTime());
        intent.putExtra("infoFromNotify", caln);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pIntent);

        // 1-я часть
        Notification notif = builder.build();

        // отправляем
        nm.notify(1, notif);
    }

    @Override
    public void onCreate(){
        super.onCreate();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO something here
        database = new DBORM(ShowNotification.this);
        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 5*1000);
        return START_STICKY;
    }

    public ShowNotification() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
