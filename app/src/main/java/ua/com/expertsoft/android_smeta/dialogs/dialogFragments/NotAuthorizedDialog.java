package ua.com.expertsoft.android_smeta.dialogs.dialogFragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import ua.com.expertsoft.android_smeta.R;

/*
 * Created by mityai on 23.02.2016.
 */
public class NotAuthorizedDialog extends DialogFragment {

    AlertDialog dialog;

    public interface OnShowAuthorizeDialog{
        void onShowDialog();
    }

    public Dialog onCreateDialog(Bundle params){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setTitle(R.string.not_logging_title);
        dialogBuilder.setMessage(R.string.not_logging_message);
        dialogBuilder.setPositiveButton(R.string.not_logging_positive_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
                ((OnShowAuthorizeDialog)getActivity()).onShowDialog();
            }
        });
        dialogBuilder.setNegativeButton(R.string.not_logging_negative_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });
        dialog = dialogBuilder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg) {
                dialog.getButton(DialogInterface.BUTTON_POSITIVE)
                        .setTextColor(getResources().getColor(R.color.colorPrimary));
                dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
                        .setTextColor(getResources().getColor(R.color.colorPrimary));
            }
        });

        return dialog;
    }
}
