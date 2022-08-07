package com.cs5520.assignments.numad22su_team24_puddle;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.util.Log;
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
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.slider.RangeSlider;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BottomFilterModal extends BottomSheetDialogFragment {
    private static final DecimalFormat df = new DecimalFormat("0.00");
    private ArrayAdapter<String> fullAdapter;
    private RangeSlider slider;
    private String spinnerResults;
    private String selectedCategory;
    private double distanceResults;
    private TextView distanceIndicator;
    private TextView startDate;
    private TextView endDate;
    private String startDateResults;
    private String endDateResults;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_modal, container, false);
        Spinner spinner = view.findViewById(R.id.category_modal_text);
        initalizeSpinnerAdapter(spinner);
        startDate = view.findViewById(R.id.start_dates_bottom_modal_text_view);
        endDate = view.findViewById(R.id.end_date_bottom_modal_text_view);
        view.findViewById(R.id.start_dates_bottom_modal_text_view).setOnClickListener(v -> {
            DialogFragment newFragment = new StartingDatePickerCalendarFragment();
            getParentFragmentManager().setFragmentResultListener("starting_date_picker_results",this,((requestKey, result) -> {
                startDateResults = result.getString("date");
                startDate.setText(DateTimeFormatUtil.formatEventDate(result.getString("date")));
            }));
            newFragment.show(getParentFragmentManager(), "datePicker");

        });
        view.findViewById(R.id.end_date_bottom_modal_text_view).setOnClickListener(v -> {
            DialogFragment newFragment = new EndingDatePickerCalendarFragment();
            getParentFragmentManager().setFragmentResultListener("ending_date_picker_results",this,((requestKey, result) -> {
                endDateResults = result.getString("date");
                endDate.setText(DateTimeFormatUtil.formatEventDate(result.getString("date")));
            }));
            newFragment.show(getParentFragmentManager(), "datePicker");
        });
        distanceIndicator = view.findViewById(R.id.location_bottom_modal_text_view);
        this.slider = view.findViewById(R.id.slider_filter);
        slider.setLabelFormatter(value -> {
            distanceIndicator.setText(value + "m");
            distanceResults = Double.parseDouble(df.format(1609.34 * value));
            return value + "m";
        });
        view.findViewById(R.id.add_filter_save_button).setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            if (selectedCategory != null) bundle.putString("category",selectedCategory);
            if (distanceResults != 0) bundle.putDouble("distance", distanceResults);
            if (startDateResults != null && endDateResults != null){
                bundle.putString("start_date",startDateResults);
                bundle.putString("end_date",startDateResults);
            }
            getParentFragmentManager().setFragmentResult("filter_result",bundle);
            dismiss();
        });
        return view;
    }


    public static class StartingDatePickerCalendarFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

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
            Bundle bundle = new Bundle();
            bundle.putString("date",date);
            getParentFragmentManager().setFragmentResult("starting_date_picker_results",bundle);
        }
    }

    public static class EndingDatePickerCalendarFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

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
            Bundle bundle = new Bundle();
            bundle.putString("date",date);
            getParentFragmentManager().setFragmentResult("ending_date_picker_results",bundle);
        }
    }

    public void initalizeSpinnerAdapter(Spinner spinner) {
        List<String> interests = new ArrayList<>();
        interests.add(0, "Filter by Category");
        interests.add(Category.MUSIC.toString());
        interests.add(Category.TRAVEL.toString());
        interests.add(Category.FINANCE.toString());
        interests.add(Category.EDUCATION.toString());
        fullAdapter = new ArrayAdapter(getActivity(), R.layout.filter_spinner_item, interests);
        fullAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(fullAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0){
                    selectedCategory = interests.get(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
}
