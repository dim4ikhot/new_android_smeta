package ua.com.expertsoft.android_smeta.dialogs.dialogFragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import ua.com.expertsoft.android_smeta.R;

/**
 * Created by mityai on 23.02.2016.
 */
public class NotAuthorizedDialog extends DialogFragment {

    public interface OnShowAuthorizeDialog{
        void onShowDialog();
    }

    public Dialog onCreateDialog(Bundle params){
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle(R.string.not_logging_title);
        dialog.setMessage(R.string.not_logging_message);
        dialog.setPositiveButton(R.string.not_logging_positive_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
                ((OnShowAuthorizeDialog)getActivity()).onShowDialog();
            }
        });
        dialog.setNegativeButton(R.string.not_logging_negative_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });
        return dialog.create();
    }
}
