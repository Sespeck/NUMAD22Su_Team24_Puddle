package com.cs5520.assignments.numad22su_team24_puddle;

import androidx.annotation.NonNull;

public enum Category {
    MUSIC(0), TRAVEL(1), FINANCE(2), EDUCATION(3), A(4), B(5), C(6), D(7), E(8), F(9);

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
            default: return "Null Category";
        }
    }

    public int getId() {
        return this.id;
    }
}
