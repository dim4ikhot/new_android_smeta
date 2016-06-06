package ua.com.expertsoft.android_smeta.dialogs.dialogFragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import ua.com.expertsoft.android_smeta.R;

/*
 * Created by mityai on 17.05.2016.
 */
public class OperationWithFacts extends DialogFragment {

    AlertDialog dialog;

    public interface OnGetAccessRecalcFactsListener{
        void OnGetAccessRecalcFacts();
    }

    public Dialog onCreateDialog(Bundle params){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setMessage(R.string.contractor_execution_changed_message);
        dialogBuilder.setPositiveButton(R.string.not_logging_positive_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                ((OnGetAccessRecalcFactsListener)getActivity()).OnGetAccessRecalcFacts();
            }
        });
        dialogBuilder.setNegativeButton(R.string.not_logging_negative_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
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
