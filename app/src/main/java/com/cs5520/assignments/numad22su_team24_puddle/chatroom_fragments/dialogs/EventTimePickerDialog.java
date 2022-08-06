package com.cs5520.assignments.numad22su_team24_puddle.chatroom_fragments.dialogs;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.fragment.app.DialogFragment;

import com.cs5520.assignments.numad22su_team24_puddle.R;

public class EventTimePickerDialog extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    private TextView view;
    private AddNewEventDialog dialog;
    public void acceptViews(TextView view, AddNewEventDialog dialog){
        this.view = view;
        this.dialog = dialog;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        this.view.setText(DateTimeFormatUtil.formatEventTime(hourOfDay,minute));
        if (this.view.getId() == R.id.starting_time_text_view){
            dialog.acceptPickerStartTime(DateTimeFormatUtil.formatEventTime(hourOfDay,minute));
        }
        else{
            dialog.acceptPickerEndingTime(DateTimeFormatUtil.formatEventTime(hourOfDay,minute));
        }
    }
}