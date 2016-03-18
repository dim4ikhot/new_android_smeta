package ua.com.expertsoft.android_smeta.asynctasks;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;

import ua.com.expertsoft.android_smeta.R;
import ua.com.expertsoft.android_smeta.standard_project.parsers.CplnParser;
import ua.com.expertsoft.android_smeta.data.DBORM;
import ua.com.expertsoft.android_smeta.data.Projects;

/**
 * Created by mityai on 12.02.2016.
 */
public class CPLNLoader extends AsyncTask<Void,Void,Boolean> {

    Context context;
    DBORM database;
    String zipPath;
    AsyncProgressDialog dialog;
    File unZippedFile;
    CplnParser parser;
    Projects loadedProject;
    int loadingType;
    LoadingOcadBuild.OnGetLoadedProjectListener loadedListener;

    public CPLNLoader(Context ctx, DBORM base, String zipPath, int type){
        context = ctx;
        database = base;
        this.zipPath = zipPath;
        dialog = new AsyncProgressDialog(ctx,getTitleByType(type),getMessageByType(type));
        loadingType = type;
        //  unZipBuild = new UnZipBuild(zipPath,new File(zipPath).getParent());
        loadedListener = (LoadingOcadBuild.OnGetLoadedProjectListener)context;
    }

    private String getTitleByType(int type){
        if(type == 0){
            return context.getResources().getString(R.string.dialogLoadingTitle);
        }else{
            return context.getResources().getString(R.string.dialog_update_title);
        }
    }

    private String getMessageByType(int type){
        if(type == 0){
            return context.getResources().getString(R.string.dialogWaitForLoading);
        }else{
            return context.getResources().getString(R.string.dialog_update_message);
        }
    }

    @Override
    protected void onPreExecute(){
        dialog.createDialog();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            unZippedFile = new File(zipPath);
            parser = new CplnParser(new FileInputStream(unZippedFile), database.getHelper(), loadingType);
            if (parser.startParser()){
                loadedProject = parser.getProject();
                loadedListener.onGetLoadedProject(loadedProject, loadingType);
                unZippedFile.delete();
            }else{
                return false;
            }
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }


    @Override
    protected void onPostExecute(Boolean result){
        if (result){
            loadedListener.onShowLoadedProject();
            if(loadingType != 0) {
                Toast.makeText(context,
                        context.getResources().getString(R.string.toast_success_update),
                        Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(context,
                    context.getResources().getString(R.string.toast_unsuccess),
                    Toast.LENGTH_SHORT).show();
        }
        dialog.freeDialog();
        super.onPostExecute(result);
    }
}
