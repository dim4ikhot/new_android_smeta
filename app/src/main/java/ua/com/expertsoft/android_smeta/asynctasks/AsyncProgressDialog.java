package ua.com.expertsoft.android_smeta.asynctasks;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by mityai on 02.02.2016.
 */
public class AsyncProgressDialog {
    
    ProgressDialog dialog;
    Context context;
    String title, message;

    public AsyncProgressDialog(){}

    public AsyncProgressDialog(Context ctx, int title, int message){
        context = ctx;
        this.title = context.getResources().getString(title);
        this.message = context.getResources().getString(message);
    }
    public AsyncProgressDialog(Context ctx, String title, String message){
        context = ctx;
        this.title = title;
        this.message = message;
    }

    public void createDialog(){
        if(dialog == null)
        {
            dialog = new ProgressDialog(context);
            dialog.setTitle(title);
            dialog.setMessage(message);
            // Change style
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            // switch-on animation
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
    }

    public void freeDialog(){
        try{
            if ((dialog!= null)&(dialog.isShowing())){
                dialog.dismiss();
            }
        }catch(IllegalArgumentException e){
            e.printStackTrace();
            dialog = null;
        }
    }
}
