package com.example.tasksapp.Utils;

import com.example.tasksapp.Models.Date;
import com.example.tasksapp.Models.Task;

import java.util.Calendar;
import java.util.TimeZone;

public abstract class IsraelTimeZone {
    public static Task getCurrentTimeAsTask() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Jerusalem"));
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH)+1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);
        return new Task("", hour + " : " + min, new Date(Integer.toString(day), Integer.toString(month), Integer.toString(year)), "");
    }
}
