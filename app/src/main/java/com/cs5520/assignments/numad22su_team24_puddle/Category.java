package com.cs5520.assignments.numad22su_team24_puddle;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public enum Category {
    MUSIC(0), TRAVEL(1), FINANCE(2), EDUCATION(3), SPORTS(4);

    int id;

    Category(int id) {
        this.id = id;
    }

    @NonNull
    @Override
    public String toString() {
        switch (this) {
            case MUSIC: return "Music";
            case TRAVEL: return "Travel";
            case FINANCE: return "Finance";
            case EDUCATION: return "Education";
            case SPORTS: return "Sports";
            default: return "Null Category";
        }
    }

    public int getId() {
        return this.id;
    }

    public static List<String> getCategoryNames() {
        List<String> names = new ArrayList<>();
        for (Category category: Category.values()) {
            names.add(category.toString());
        }
        return names;
    }
}
