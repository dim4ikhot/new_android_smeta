package ua.com.expertsoft.android_smeta.dialogs.dialogFragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;

import ua.com.expertsoft.android_smeta.R;
import ua.com.expertsoft.android_smeta.selected_project.ProjectInfo;

/*
 * Created by mityai on 18.01.2016.
 */
public class ProjectExistsDialog  extends DialogFragment implements DialogInterface.OnClickListener{

    AlertDialog dialog;

    public interface OnProjectLodsOptions{
        void OnProjectLoding(int type);
    }

    Context context;

    public ProjectExistsDialog(){
    }

    public void setContext(Context ctx){
        context = ctx;
    }

    @Override
    public Dialog onCreateDialog(Bundle params){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setTitle(R.string.dialog_project_exists_title);
        String[] connections = {getActivity().getResources().getString(R.string.message_project_replace),
                getActivity().getResources().getString(R.string.message_project_update)/*,
                getActivity().getResources().getString(R.string.message_project_load_as_new)*/};
        dialogBuilder.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    ProjectInfo.project = null;
                    ProjectInfo.PROJECT_GUID = "";
                    dialog.dismiss();
                }
                return true;
            }
        });
        dialogBuilder.setSingleChoiceItems(connections, -1, this);
        return dialogBuilder.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        OnProjectLodsOptions loadingType = (OnProjectLodsOptions)getActivity();
        loadingType.OnProjectLoding(which);
        dialog.dismiss();
    }
}
