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

    public interface OnGetGroupTaskName{
        void getTaskName(String taskName);
    }

    OnGetGroupTaskName groupTasksActivity;
    View dialogView;

    public Dialog onCreateDialog(Bundle params){
      super.onCreateDialog(params);
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialogView = getActivity().getLayoutInflater().inflate(R.layout.fragment_dialog_enter_task, null);
        dialog.setTitle(R.string.new_task_enter_name_dialog_title);
        dialog.setPositiveButton("OK", btnOkListener);
        dialog.setNegativeButton(R.string.user_edit_dialog_cancel, btnOkListener);
        dialog.setView(dialogView);
        return dialog.create();
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
