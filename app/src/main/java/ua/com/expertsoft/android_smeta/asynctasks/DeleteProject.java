package ua.com.expertsoft.android_smeta.asynctasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import ua.com.expertsoft.android_smeta.MainActivity;
import ua.com.expertsoft.android_smeta.R;

/**
 * Created by mityai on 29.01.2016.
 */
public class DeleteProject extends AsyncTask<Void, Void, Void> {

    ProgressDialog waitDialog;
    Context context;

    public DeleteProject(){}
    public DeleteProject(Context context){
        this.context = context;
    }

    public void createDialog(){
        if(waitDialog == null)
        {
            waitDialog = new ProgressDialog(context);
            waitDialog.setTitle(context.getResources().getString(R.string.dialogDeleteProjectTitle));
            waitDialog.setMessage(context.getResources().getString(R.string.dialogWaitForDeleting));
            // Change style
            waitDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            // switch-on animation
            waitDialog.setIndeterminate(true);
            waitDialog.setCancelable(false);
            waitDialog.setCanceledOnTouchOutside(false);
            waitDialog.show();
        }
    }

    public void freeDialog(){
        try{
            if ((waitDialog!= null)&(waitDialog.isShowing())){
                waitDialog.dismiss();
            }
        }catch(IllegalArgumentException e){
            e.printStackTrace();
            waitDialog = null;
        }
    }

    @Override
    public void onPreExecute(){
        createDialog();
    }

    @Override
    protected Void doInBackground(Void... params) {
        ((MainActivity) context).deleteProject();
        return null;
    }

    @Override
    public void onPostExecute(Void result){
        ((MainActivity) context).showProjectsInList(
                ((MainActivity) context).projectsData
                        .getProjectsTypeStandart()
                        .getAllProjects());
        ((MainActivity) context).clearStaticClasses();
        ((MainActivity) context).fab.setImageResource(R.drawable.ic_add_white);
        ((MainActivity) context).refreshNavMenuCount();
        freeDialog();
    }
}
