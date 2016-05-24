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

    public interface OnGetAccessRecalcFactsListener{
        void OnGetAccessRecalcFacts();
    }

    public Dialog onCreateDialog(Bundle params){
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setMessage(R.string.contractor_execution_changed_message);
        dialog.setPositiveButton(R.string.not_logging_positive_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                ((OnGetAccessRecalcFactsListener)getActivity()).OnGetAccessRecalcFacts();
            }
        });
        dialog.setNegativeButton(R.string.not_logging_negative_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        return dialog.create();
    }
}
