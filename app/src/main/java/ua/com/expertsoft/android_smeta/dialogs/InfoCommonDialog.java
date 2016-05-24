package ua.com.expertsoft.android_smeta.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/*
 * Created by mityai on 15.04.2016.
 */
public class InfoCommonDialog extends DialogFragment {

    String title = "";
    String message = "";

    public InfoCommonDialog(){}

    public void setTitle(String title){
        this.title = title;
    }

    public void setMessage(String message){
        this.message = message;
    }

    public Dialog onCreateDialog(Bundle params){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        return builder.create();
    }
}
