package com.cs5520.assignments.numad22su_team24_puddle.chatroom_fragments.dialogs;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.cs5520.assignments.numad22su_team24_puddle.R;

import java.text.ParseException;

public class EventCalendarEndingDatePickerDialog extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {
    private TextView view;
    private AddNewEventDialog dialog;

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
        // Dates stored as yy-mm-dd
        String date = year + "-" + (month + 1) + "-" + day;
        Bundle bundle = new Bundle();
        bundle.putString("date",date);
        getParentFragmentManager().setFragmentResult("ending_date",bundle);
    }
}
