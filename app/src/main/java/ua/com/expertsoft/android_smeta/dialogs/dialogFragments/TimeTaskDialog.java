package ua.com.expertsoft.android_smeta.dialogs.dialogFragments;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by mityai on 06.01.2016.
 */
public class TimeTaskDialog extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    public interface OnGetTimeTaskListener{
        void OnGetTime(int hour, int minute);
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        OnGetTimeTaskListener dateGetter = (OnGetTimeTaskListener)getActivity();
        if (dateGetter != null) {
            dateGetter.OnGetTime(hourOfDay, minute);
        }
    }
}
