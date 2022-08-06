package com.cs5520.assignments.numad22su_team24_puddle;

import androidx.annotation.NonNull;

public enum Category {
    MUSIC(0), TRAVEL(1), FINANCE(2), EDUCATION(3), SELECT(4);

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
            case SELECT: return "Select";
            default: return "Null Category";
        }
    }

    public int getId() {
        return this.id;
    }
}
