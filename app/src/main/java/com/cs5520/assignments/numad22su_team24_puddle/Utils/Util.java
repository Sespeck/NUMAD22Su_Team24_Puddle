package com.cs5520.assignments.numad22su_team24_puddle.Utils;

import android.content.Context;
import android.net.ConnectivityManager;

import com.cs5520.assignments.numad22su_team24_puddle.Category;
import com.cs5520.assignments.numad22su_team24_puddle.Model.User;
import com.cs5520.assignments.numad22su_team24_puddle.NotificationListener;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class Util {
    public static Map<String,Boolean> renderShimmerEffect = new HashMap<>();
    public static boolean eventsPopulated = false;
    public static boolean puddleListPopulated = false;
    public static Map<Integer, Category> categoryMap = getCategoryMap();
    public static boolean isForeground = true;
    public static boolean isPuddleListForeground = true;
    public static String foregroundedPuddle;
    public static User user;
    public static NotificationListener listener = new NotificationListener();

    public static String generateShimmerEffectID(String username, String puddleID, String fragmentID){
        return username+puddleID+fragmentID;
    }

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

    public static String getGMTTimestamp(){
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat("dd:MM:yyyy HH:mm:ss");
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormatGmt.format(new Date())+"";
    }

    public static String utcToLocalTime(String utc) {
        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();


        String inputValue = utc;
        Instant timestamp = Instant.parse(inputValue);
        ZonedDateTime losAngelesTime = timestamp.atZone(ZoneId.of(tz.getID()));
        return losAngelesTime.toString();
    }

    public static String convertTocurrentDateTime(String utc){
        String localTime = utcToLocalTime(utc);
        String[] dateTime = localTime.split("T");
        String[] date = dateTime[0].split("-");
        String[] temp = dateTime[1].split("-");
        String[] time = temp[0].split(":");

        String currentDate = Instant.now().toString().split("T")[0];
        String currentDay = currentDate.split("-")[2];
        String dateToShow = "";

        String[] dbDateTime = utc.split("T");
        String[] dbDate = dbDateTime[0].split("-");
        String[] dbTime = dbDateTime[1].split("-");

        if(currentDate.equals(dbDateTime[0])){
            dateToShow += "Today";
        } else if(Integer.valueOf(currentDay) - 1 == Integer.valueOf(dbDate[2])){
            dateToShow += "Yesterday";
        } else {
            dateToShow += date[0] + " ";
            switch (date[1]){
                case "01":
                    dateToShow += "Jan";
                    break;
                case "02":
                    dateToShow += "Feb";
                    break;
                case "03":
                    dateToShow += "Mar";
                    break;
                case "04":
                    dateToShow += "Apr";
                    break;
                case "05":
                    dateToShow += "May";
                    break;
                case "06":
                    dateToShow += "June";
                    break;
                case "07":
                    dateToShow += "July";
                    break;
                case "08":
                    dateToShow += "Aug";
                    break;
                case "09":
                    dateToShow += "Sept";
                    break;
                case "10":
                    dateToShow += "Oct";
                    break;
                case "11":
                    dateToShow += "Nov";
                    break;
                case "12":
                    dateToShow += "Dec";
                    break;
            }

            dateToShow += " " + date[2];
        }

        int t = Integer.valueOf(time[0]);
        if( t <= 11){
            dateToShow += " " + time[0] + ":" + time[1] + " AM";
        } else if(t == 12) {
            dateToShow += " " + time[0] + ":" + time[1] + " PM";
        } else {
            dateToShow += " " + (Integer.valueOf(time[0] )- 12) + ":" + time[1] + " PM";
        }

        return dateToShow;
    }
}
