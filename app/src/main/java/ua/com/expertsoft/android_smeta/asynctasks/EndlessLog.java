package ua.com.expertsoft.android_smeta.asynctasks;

import android.os.AsyncTask;
import android.util.Log;

import java.util.concurrent.TimeUnit;

/**
 * Created by mityai on 27.04.2016.
 */
public class EndlessLog extends AsyncTask<Void,Void,Void> {

    @Override
    protected Void doInBackground(Void... params) {
        while (true) {
            Log.d("EndlessLog", "Just log, tells nothing :) ");
            try {
                TimeUnit.MILLISECONDS.sleep(30000);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}
