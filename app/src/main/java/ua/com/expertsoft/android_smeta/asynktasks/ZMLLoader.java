package ua.com.expertsoft.android_smeta.asynktasks;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;

import ua.com.expertsoft.android_smeta.R;
import ua.com.expertsoft.android_smeta.standard_projects.UnZipBuild;
import ua.com.expertsoft.android_smeta.standard_projects.parsers.ZmlParser;
import ua.com.expertsoft.android_smeta.data.DBORM;
import ua.com.expertsoft.android_smeta.data.Projects;

/**
 * Created by mityai on 06.02.2016.
 */
public class ZMLLoader extends AsyncTask<Void,Void,Boolean> {

    Context context;
    DBORM database;
    String zipPath;
    AsyncProgressDialog dialog;
    UnZipBuild unZipBuild;
    File unZippedFile;
    ZmlParser parser;
    Projects loadedProject;
    int loadingType;
    LoadingOcadBuild.OnGetLoadedProjectListener loadedListener;

    public ZMLLoader(Context ctx, DBORM base, String zipPath, int type){
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
        //    unZippedFile = unZipBuild.ExUnzip();
            unZippedFile = new File(zipPath);
            parser = new ZmlParser(new FileInputStream(unZippedFile), database.getHelper(), loadingType);
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
