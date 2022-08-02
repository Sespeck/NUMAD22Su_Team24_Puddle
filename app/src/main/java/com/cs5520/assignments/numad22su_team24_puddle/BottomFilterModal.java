package com.cs5520.assignments.numad22su_team24_puddle;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.cs5520.assignments.numad22su_team24_puddle.chatroom_fragments.dialogs.DateTimeFormatUtil;
import com.cs5520.assignments.numad22su_team24_puddle.chatroom_fragments.dialogs.EventCalendarPickerDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.slider.BasicLabelFormatter;
import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.Slider;

import java.util.ArrayList;
import java.util.List;

public class BottomFilterModal extends BottomSheetDialogFragment {
    private ArrayAdapter<String> fullAdapter;
    private Slider slider;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_modal, container, false);
        Spinner spinner = view.findViewById(R.id.category_modal_text);
        initalizeSpinnerAdapter(spinner);
        view.findViewById(R.id.start_dates_bottom_modal_text_view).setOnClickListener(v -> {
            DialogFragment newFragment = new EventCalendarPickerDialog(v);
            newFragment.show(getParentFragmentManager(), "datePicker");
        });
        view.findViewById(R.id.end_date_bottom_modal_text_view).setOnClickListener(v -> {
            DialogFragment newFragment = new EventCalendarPickerDialog(v);
            newFragment.show(getParentFragmentManager(), "datePicker");
        });
        this.slider = view.findViewById(R.id.slider_filter);
        slider.setLabelFormatter(value -> value +"m");
        return view;
    }


    public static class DatePickerCalendarFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {
        private TextView view;

        public DatePickerCalendarFragment(View view) {
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
            // Implement pushing this data to the main activity to filter via date.
        }
    }

    public void initalizeSpinnerAdapter(Spinner spinner) {
        List<String> interests = new ArrayList<>();
        interests.add(0, "Filter by Category");
        interests.add("Music");
        interests.add("Travel");
        interests.add("Business");
        interests.add("Education");
        fullAdapter = new ArrayAdapter(getActivity(), R.layout.filter_spinner_item, interests);
        fullAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(fullAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Implement pushing this to the main activity to filter puddles via category
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
}
