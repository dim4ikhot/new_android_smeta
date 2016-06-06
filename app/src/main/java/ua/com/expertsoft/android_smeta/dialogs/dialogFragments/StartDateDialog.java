package ua.com.expertsoft.android_smeta.dialogs.dialogFragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;

import ua.com.expertsoft.android_smeta.R;

/*
 * Created by mityai on 06.01.2016.
 */
public class StartDateDialog extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    public interface OnGetDateListener{
        void OnGetDate(int year, int monthOfYear, int dayOfMonth, int which);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dlg = new DatePickerDialog(getActivity(), this, year, month, day);
        dlg.setButton(DialogInterface.BUTTON_NEGATIVE,
                getActivity().getResources().getString(R.string.btnCancelCaption),
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });
        // Create a new instance of DatePickerDialog and return it
        return dlg;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        int which = getArguments().getInt("which");
        OnGetDateListener dateGetter = (OnGetDateListener)getActivity()
                .getSupportFragmentManager()
                .findFragmentByTag("factsdialog");
        if (dateGetter != null) {
            dateGetter.OnGetDate(year, monthOfYear, dayOfMonth, which);
        }
    }
}
