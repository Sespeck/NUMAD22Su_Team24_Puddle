package com.cs5520.assignments.numad22su_team24_puddle.Utils;

import android.content.Context;
import android.net.ConnectivityManager;

import com.cs5520.assignments.numad22su_team24_puddle.Category;

import java.util.HashMap;
import java.util.Map;

public class Util {

    public static Map<Integer, Category> categoryMap = getCategoryMap();

    private static Map<Integer, Category> getCategoryMap() {
        Map<Integer, Category> map = new HashMap<>();
        for(Category category: Category.values()) {
            map.put(category.getId(), category);
        }
        return map;
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null;
    }


}
