package ua.com.expertsoft.android_smeta.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;

import ua.com.expertsoft.android_smeta.R;
import ua.com.expertsoft.android_smeta.data.UserProjects;

/*
 * Created by mityai on 21.12.2015.
 */
public class EditExistsUserProject extends DialogFragment implements DialogInterface.OnClickListener{

    public interface OnDeleteProjectListener{
        void onDeleteProject(UserProjects proj, int position);
    }
    public interface OnUpdateProjectListener{
        void onUpdateProject(UserProjects proj, String newName, int position);
    }

    View dialogView;
    UserProjects projects;
    Bundle arg;
    EditText etName;
    int pos;

    public EditExistsUserProject(){
    }

    @Override
    public Dialog onCreateDialog(Bundle params){
        super.onCreateDialog(params);
        arg = this.getArguments();
        projects = (UserProjects)arg.getSerializable("userProject");
        pos = arg.getInt("listPosition");
        AlertDialog.Builder editDialog = new AlertDialog.Builder(getActivity());
        editDialog.setTitle(R.string.user_edit_dialog_title);
        editDialog.setPositiveButton(R.string.user_edit_dialog_edit, this);
        editDialog.setNegativeButton(R.string.user_edit_dialog_delete, this);
        editDialog.setNeutralButton(R.string.user_edit_dialog_cancel, this);
        dialogView = getActivity().getLayoutInflater().inflate(R.layout.fragment_dialog_enter_task, null);
        etName = (EditText)dialogView.findViewById(R.id.editTaskText);
        etName.setText(projects.getUserProjName());
        editDialog.setView(dialogView);
        return editDialog.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch(which){
            case Dialog.BUTTON_POSITIVE:
                OnUpdateProjectListener updateProject = (OnUpdateProjectListener)getActivity();
                updateProject.onUpdateProject(projects,etName.getText().toString(), pos);
                dialog.dismiss();
                break;
            case Dialog.BUTTON_NEGATIVE:
                //delete project
                OnDeleteProjectListener deletingProject = (OnDeleteProjectListener)getActivity();
                deletingProject.onDeleteProject(projects, pos);
                dialog.dismiss();
                break;
            case Dialog.BUTTON_NEUTRAL:
                dialog.dismiss();
                break;
        }
    }
}
