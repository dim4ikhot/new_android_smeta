package ua.com.expertsoft.android_smeta.dialogs.dialogFragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import ua.com.expertsoft.android_smeta.R;

/*
 * Created by mityai on 16.01.2016.
 */
public class DialogAboutProgram extends DialogFragment implements DialogInterface.OnClickListener {

    AlertDialog dialog;

    @Override
    public Dialog onCreateDialog(Bundle params){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setView(R.layout.dialog_about_layout);
        dialogBuilder.setPositiveButton("OK",this);
        dialog = dialogBuilder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg) {
                dialog.getButton(DialogInterface.BUTTON_POSITIVE)
                        .setTextColor(getResources().getColor(R.color.colorPrimary));
            }
        });
        return dialog;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        dismiss();
    }
}
