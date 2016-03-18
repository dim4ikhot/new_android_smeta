package ua.com.expertsoft.android_smeta.asynctasks;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.File;

import ua.com.expertsoft.android_smeta.R;
import ua.com.expertsoft.android_smeta.standard_project.FileUtils;
import ua.com.expertsoft.android_smeta.standard_project.parsers.ArpParser;
import ua.com.expertsoft.android_smeta.data.DBORM;
import ua.com.expertsoft.android_smeta.data.Projects;

/**
 * Created by mityai on 11.02.2016.
 */
public class ARPLoader extends AsyncTask<Void,Void,Boolean> {

    Context context;
    DBORM database;
    String arpPath;
    AsyncProgressDialog dialog;
    FileUtils converter;
    File convertedFile;
    ArpParser parser;
    Projects loadedProject;
    int loadingType;
    LoadingOcadBuild.OnGetLoadedProjectListener loadedListener;

    public ARPLoader(Context ctx, DBORM base, String arpPath, int type){
        context = ctx;
        database = base;
        this.arpPath = arpPath;
        dialog = new AsyncProgressDialog(ctx,getTitleByType(type),getMessageByType(type));
        loadingType = type;
        converter= new FileUtils();
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
            convertedFile = converter.encodeCP866ToUTF8(arpPath);
            parser = new ArpParser(context,
                    convertedFile,
                    database.getHelper(),
                    new File(arpPath).getName());
            if (parser.startParce()){
                loadedProject = parser.getProject();
                loadedListener.onGetLoadedProject(loadedProject, loadingType);
                convertedFile.delete();
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
        }else{
            Toast.makeText(context,
                    context.getResources().getString(R.string.toast_unsuccess),
                    Toast.LENGTH_SHORT).show();
        }
        dialog.freeDialog();
        super.onPostExecute(result);
    }
}
