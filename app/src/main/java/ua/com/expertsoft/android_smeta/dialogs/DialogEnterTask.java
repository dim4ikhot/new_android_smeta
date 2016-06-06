package ua.com.expertsoft.android_smeta.dialogs;

import android.app.Dialog;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.EditText;

import ua.com.expertsoft.android_smeta.R;

public class DialogEnterTask extends DialogFragment {

    AlertDialog dialog;

    public interface OnGetGroupTaskName{
        void getTaskName(String taskName);
    }

    OnGetGroupTaskName groupTasksActivity;
    View dialogView;

    public Dialog onCreateDialog(Bundle params){
      super.onCreateDialog(params);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogView = getActivity().getLayoutInflater().inflate(R.layout.fragment_dialog_enter_task, null);
        dialogBuilder.setTitle(R.string.new_task_enter_name_dialog_title);
        dialogBuilder.setPositiveButton("OK", btnOkListener);
        dialogBuilder.setNegativeButton(R.string.user_edit_dialog_cancel, btnOkListener);
        dialogBuilder.setView(dialogView);
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

    DialogInterface.OnClickListener btnOkListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch(which){
                case Dialog.BUTTON_POSITIVE:
                    groupTasksActivity = (OnGetGroupTaskName)getActivity();
                    EditText groupTask = (EditText)dialogView.findViewById(R.id.editTaskText);
                    String message = groupTask.getText().toString();
                    dialog.dismiss();
                    groupTasksActivity.getTaskName(message);
                    break;
                case Dialog.BUTTON_NEGATIVE:
                    dialog.dismiss();
                    break;
            }
        }
    };

}
