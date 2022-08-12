package com.cs5520.assignments.numad22su_team24_puddle;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.switchmaterial.SwitchMaterial;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BottomFilterModal extends BottomSheetDialogFragment {
    private static final DecimalFormat df = new DecimalFormat("0.00");
    private ArrayAdapter<String> fullAdapter;
    private RangeSlider slider;
    private String spinnerResults;
    private double distanceResults;
    private TextView distanceIndicator;
    private TextView filterByCategory;
    private Spinner membershipSpinner;
    private String[] categories;
    private String membershipFilterResults;
    private SwitchMaterial globalSwitch;
    private ArrayList<Integer> selectedCategoryIndexes;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_modal, container, false);
        distanceIndicator = view.findViewById(R.id.location_bottom_modal_text_view);
        filterByCategory = view.findViewById(R.id.filter_by_category_view);
        membershipSpinner = view.findViewById(R.id.membership_spinner);
        globalSwitch = view.findViewById(R.id.global_switch);
        categories = new String[]{"Music", "Sports", "Finance", "Travel", "Education"};
        boolean[] selectedCategory = new boolean[categories.length];
        initalizeSpinnerAdapter(membershipSpinner);

        selectedCategoryIndexes = new ArrayList<>();

        filterByCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // initialize selected language array
                boolean[] selectedCategory = new boolean[categories.length];
                for(int i: selectedCategoryIndexes) {
                    selectedCategory[i] = true;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle("Select Member Count");
                builder.setCancelable(false);
                builder.setMultiChoiceItems(categories, selectedCategory, (dialogInterface, i, b) -> {
                    if (b) {
                        selectedCategoryIndexes.add(i);
                        Collections.sort(selectedCategoryIndexes);
                    } else {
                        selectedCategoryIndexes.remove(Integer.valueOf(i));
                    }
                });

                builder.setPositiveButton("OK", (dialogInterface, i) -> {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int j = 0; j < selectedCategoryIndexes.size(); j++) {
                        stringBuilder.append(categories[selectedCategoryIndexes.get(j)]);
                        if (j != selectedCategoryIndexes.size() - 1) {
                            stringBuilder.append(", ");
                        }
                    }
                    filterByCategory.setText(stringBuilder.toString());
                });

                builder.setNegativeButton("Cancel", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                });
                builder.setNeutralButton("Clear All", (dialogInterface, i) -> {
                    for (int j = 0; j < selectedCategory.length; j++) {
                        // remove all selection
                        selectedCategory[j] = false;
                        // clear language list
                        selectedCategoryIndexes.clear();
                        // clear text view value
                        filterByCategory.setText("Filter By Category");
                    }
                });
                // show dialog
                builder.show();
            }
        });
        this.slider = view.findViewById(R.id.slider_filter);
        slider.setLabelFormatter(value -> {
            distanceIndicator.setText(value + " miles");
            distanceResults = Double.parseDouble(df.format(1609.34 * value));
            return value + " m";
        });
        view.findViewById(R.id.add_filter_save_button).setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            if (selectedCategoryIndexes.size() >= 1) {
                ArrayList<String> selectedCategories = new ArrayList<>();
                for (Integer index :
                        selectedCategoryIndexes) {
                    selectedCategories.add(categories[index]);
                }
                bundle.putStringArrayList("selected_categories", selectedCategories);
            }
            if (globalSwitch.isChecked()) {
                bundle.putBoolean("is_checked", globalSwitch.isChecked());
            }
            if (distanceResults != 0) {
                bundle.putDouble("distance", distanceResults);
            } else {
                bundle.putDouble("distance", Double.parseDouble(df.format(1609.34 * 20)));
            }
            if (membershipFilterResults != null)
                bundle.putString("membership_filter", membershipFilterResults);
            getParentFragmentManager().setFragmentResult("filter_result", bundle);
            dismiss();
        });
        return view;
    }


    public void initalizeSpinnerAdapter(Spinner spinner) {
        List<String> interests = new ArrayList<>();
        interests.add(0, "Filter by Membership");
        interests.add("<10");
        interests.add("10");
        interests.add("50");
        interests.add("100");
        interests.add("500");
        interests.add("1000");
        interests.add(">1000");
        fullAdapter = new ArrayAdapter(getActivity(), R.layout.filter_spinner_item, interests);
        fullAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(fullAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    membershipFilterResults = interests.get(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
}

