package com.cs5520.assignments.numad22su_team24_puddle.chatroom_fragments.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.cs5520.assignments.numad22su_team24_puddle.R;

public class AddNewEventDialog extends DialogFragment {
    private Button exitButton;
    private TextView startingTimeView;
    private TextView endingTimeView;
    private TextView startingDateView;
    private TextView endingDateView;



    /** the system calls this to get the dialogfragment's layout, regardless
     of whether it's being displayed as a dialog or an embedded fragment. */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_event_dialog, container, false);
        exitButton = view.findViewById(R.id.add_event_dialog_exit_button);
        startingTimeView = view.findViewById(R.id.starting_time_text_view);
        endingTimeView = view.findViewById(R.id.ending_time_text_view);
        startingDateView = view.findViewById(R.id.starting_date_text_view);
        endingDateView = view.findViewById(R.id.ending_date_text_view);
        String[] initalTime = DateTimeFormatUtil.formatPresetTime(java.time.LocalTime.now().toString());
        String date = DateTimeFormatUtil.formatEventDate(java.time.LocalDate.now().toString());
        startingTimeView.setText(initalTime[0]);
        endingTimeView.setText(initalTime[1]);
        startingDateView.setText(date);
        endingDateView.setText(date);
        startingDateView.setOnClickListener(this::showDatePickerDialog);
        endingDateView.setOnClickListener(this::showDatePickerDialog);
        startingTimeView.setOnClickListener(this::showTimePickerDialog);
        endingTimeView.setOnClickListener(this::showTimePickerDialog);

        exitButton.setOnClickListener(v -> {
            dismiss();
        });
        // Inflate the layout to use as dialog or embedded fragment
        return view;
    }

    /** The system calls this only when creating the layout in a dialog. */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new EventCalendarPickerDialog(v);
        newFragment.show(getParentFragmentManager(), "datePicker");
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new EventTimePickerDialog(v);
        newFragment.show(getParentFragmentManager(), "timePicker");
    }
}