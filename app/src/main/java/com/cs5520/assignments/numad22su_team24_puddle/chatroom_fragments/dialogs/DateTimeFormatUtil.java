package com.cs5520.assignments.numad22su_team24_puddle.chatroom_fragments.dialogs;

import android.util.Log;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class DateTimeFormatUtil {

    public static String[] formatPresetTime(String datetime){
        String[] time = datetime.split(":");
        Integer hours = Integer.parseInt(time[0]);
        String amOrPm = hours < 12 ? "AM" : "PM";
        // Set the preset time for an event to the nearest half or full hour
        if (Integer.parseInt(time[1]) < 30){
            time[1] = "30";
        }
        else{
            time[1] = "00";
            time[0] = String.valueOf(hours + 1);
        }
        time[0] = Integer.parseInt(time[0]) % 12 != 0 ? String.valueOf((Integer.parseInt(time[0]) % 12)) : "12";
        String startingTime = time[0]+":"+time[1]+" "+amOrPm;
        String endingTime = Integer.parseInt(time[0])+1 % 12 != 0 ? String.valueOf((Integer.parseInt(time[0])+1 % 12)) : "12";
        return new String[]{startingTime,endingTime+":"+time[1]+" "+amOrPm};
    }

    public static String formatEventTime(int hours, int minutes){
        String amOrPm = hours < 12 ? "AM" : "PM";
        String hour = hours % 12 != 0 ? String.valueOf(hours % 12) : "12";
        return hour+":"+minutes+" "+amOrPm;
    }

    public static String formatEventDate(String date) {
        String[] currentDate = date.split("-");
        LocalDate formattedDate = LocalDate.of(Integer.parseInt(currentDate[0]), Integer.parseInt(currentDate[1]), Integer.parseInt(currentDate[2]));
        Log.d("here",formattedDate.toString());
        DayOfWeek dayOfWeek = DayOfWeek.from(formattedDate);
        return getDayName(dayOfWeek.getValue())+", " + getMonthName(Integer.parseInt(currentDate[1]))+" "+currentDate[2]+", "+currentDate[0];
    }

    private static String getMonthName(Integer month){
        String[] months = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        return months[month-1];
    }

    private static String getDayName(Integer day){
//        Log.d("here",String.valueOf(day));
        String[] days = new String[]{"Mon","Tue","Wed","Thu","Fri","Sat","Sun"};
        return days[day-1];
    }
}
