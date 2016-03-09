package ua.com.expertsoft.android_smeta.dialogs.dialogFragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import ua.com.expertsoft.android_smeta.R;

/**
 * Created by mityai on 18.01.2016.
 */
public class ProjectExistsDialog  extends DialogFragment implements DialogInterface.OnClickListener{

    public interface OnProjectLodsOptions{
        void OnProjectLoding(int type);
    }

    Context context;
    private static int CONNECTIONS = 7;

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
