package com.cs5520.assignments.numad22su_team24_puddle.Utils;

import android.content.Context;
import android.net.ConnectivityManager;

public class Util {

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null;
    }
}
