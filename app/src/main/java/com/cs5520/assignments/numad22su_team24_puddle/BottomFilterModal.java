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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private int membershipFilterValue;
    private SwitchMaterial globalSwitch;
    private SwitchMaterial privateSwitch;
    private ArrayList<Integer> selectedCategoryIndexes;
    Map<String, Integer> spinnerMap;
    List<String> interests;

    private List<String> filteredCategories;
    private int filteredMembership;
    private boolean filteredGlobal;
    private boolean filteredPrivate;
    private boolean isNearMe;

    public BottomFilterModal(double filteredDistance, List<String> filteredCategories, int filteredMembership, boolean filteredGlobal, boolean filteredPrivate, boolean isNearby) {
        this.isNearMe = isNearby;
        this.distanceResults = filteredDistance;
        this.filteredCategories = filteredCategories;
        this.filteredMembership = filteredMembership;
        this.filteredGlobal = filteredGlobal;
        this.filteredPrivate = filteredPrivate;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_modal, container, false);
        distanceIndicator = view.findViewById(R.id.location_bottom_modal_text_view);
        filterByCategory = view.findViewById(R.id.filter_by_category_view);
        membershipSpinner = view.findViewById(R.id.membership_spinner);
        globalSwitch = view.findViewById(R.id.global_switch);
        privateSwitch = view.findViewById(R.id.private_switch);

        List<String> allCategories = Category.getCategoryNames();
        categories = new String[allCategories.size()];
        for (int i = 0; i < allCategories.size(); i++) {
            categories[i] = allCategories.get(i);
        }

        spinnerMap = new HashMap<>();
        interests = new ArrayList<>();
        initalizeSpinnerAdapter(membershipSpinner);

        selectedCategoryIndexes = new ArrayList<>();

        for (int i = 0; i < categories.length; i++) {
            if (filteredCategories.contains(categories[i])) {
                selectedCategoryIndexes.add(i);
            }
        }

        setCategoryString();
        distanceIndicator.setText(distanceResults + " miles");
        for(String val: spinnerMap.keySet()) {
            if(spinnerMap.get(val)==filteredMembership) {
                membershipSpinner.setSelection(interests.indexOf(val));
                break;
            }
        }

        globalSwitch.setChecked(filteredGlobal);
        privateSwitch.setChecked(filteredPrivate);
        if(isNearMe) {
           privateSwitch.setVisibility(View.GONE);
        } else {
            view.findViewById(R.id.filter_distance).setVisibility(View.GONE);
        }

        this.slider = view.findViewById(R.id.slider_filter);
        slider.setValues((float) distanceResults);

        for (String key : spinnerMap.keySet()) {
            if (spinnerMap.get(key) == filteredMembership) {
                membershipFilterResults = key;
                break;
            }
        }

        filterByCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // initialize selected language array
                boolean[] selectedCategory = new boolean[categories.length];
                for (int i : selectedCategoryIndexes) {
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
                    setCategoryString();
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

        slider.setLabelFormatter(value -> {
            distanceIndicator.setText(value + " miles");
            distanceResults = value;
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
            } else {
                bundle.putStringArrayList("selected_categories", new ArrayList<>(allCategories));
            }
            bundle.putBoolean("is_checked", globalSwitch.isChecked());
            bundle.putBoolean("private_puddle", privateSwitch.isChecked());
            bundle.putDouble("distance", distanceResults);
            bundle.putInt("membership_filter", spinnerMap.get(membershipFilterResults));
            getParentFragmentManager().setFragmentResult("filter_result", bundle);
            dismiss();
        });
        return view;
    }


    public void initalizeSpinnerAdapter(Spinner spinner) {
        interests = new ArrayList<>();
        interests.add(0, "Filter by Membership");
        interests.add("10");
        interests.add("50");
        interests.add("100");
        interests.add("500");
        interests.add("1000");
        spinnerMap.put("Filter by Membership", Integer.MAX_VALUE);
        spinnerMap.put("10", 10);
        spinnerMap.put("50", 50);
        spinnerMap.put("100", 100);
        spinnerMap.put("500", 500);
        spinnerMap.put("1000", 1000);
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

    public void setCategoryString() {
        if (selectedCategoryIndexes.size() == 0) {
            filterByCategory.setText("Filter By Category");
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int j = 0; j < Math.min(2, selectedCategoryIndexes.size()); j++) {
            stringBuilder.append(categories[selectedCategoryIndexes.get(j)]);
            if (j != Math.min(2, selectedCategoryIndexes.size()) - 1) {
                stringBuilder.append(", ");
            }
        }
        if (selectedCategoryIndexes.size() > 2) {
            stringBuilder.append(",+");
            stringBuilder.append(selectedCategoryIndexes.size() - 2);
        }
        filterByCategory.setText(stringBuilder.toString());

    }
}

