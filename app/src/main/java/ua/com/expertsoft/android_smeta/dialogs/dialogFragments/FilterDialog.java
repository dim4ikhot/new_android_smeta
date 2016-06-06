package ua.com.expertsoft.android_smeta.dialogs.dialogFragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import ua.com.expertsoft.android_smeta.R;

/*
 * Created by mityai on 05.02.2016.
 */
public class FilterDialog extends DialogFragment {

    AlertDialog dialog;

    public interface OnGetFilterListener{
        void onGetFilter(String filter,int what, int which);
    }

    public Dialog onCreateDialog(Bundle params){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setTitle(R.string.filter_by);
        final String[] filter = getArguments().getStringArray("filters");
        final int whatFilter = getArguments().getInt("filter");
        if(isCanShowFilter(filter)) {
            dialogBuilder.setItems(filter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ((OnGetFilterListener) getActivity()).onGetFilter(filter[which],whatFilter, which);
                    dismiss();
                }
            });
        }else{
            dialogBuilder.setMessage(R.string.filters_not_found);
            dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dismiss();
                }
            });
        }
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

    private boolean isCanShowFilter(String[] filters){
        boolean result = filters.length > 0;
        for(String filter :  filters){
            if(! filter.equals("")) {
                result = true;
                break;
            }
        }
        return result;
    }
}
