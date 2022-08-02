package com.cs5520.assignments.numad22su_team24_puddle.chatroom_fragments.dialogs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.cs5520.assignments.numad22su_team24_puddle.R;

public class EventCalendarPickerDialog extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {
    private TextView view;
    private AddNewEventDialog dialog;

    public EventCalendarPickerDialog(View view, AddNewEventDialog dialog){
        this.view = (TextView) view;
        this.dialog = dialog;
    }


    public EventCalendarPickerDialog(View view){
        this.view = (TextView) view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(requireContext(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        String date = year + "-" + (month + 1) + "-" + day;
        this.view.setText(DateTimeFormatUtil.formatEventDate(date));
        if (view.getId() == R.id.starting_date_text_view) {
            dialog.acceptPickerStartingDate(date);
        } else{
            dialog.acceptPickerEndingDate(date);
        }
    }
}