package com.piapps.flashcard.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by abduaziz on 6/10/17.
 */

public class DateUtils {

    public static String getFullTime(Long d){
        return getDay(d) + " " + getYear(d) + " " + getHourMinute(d);
    }

    public static String getHourMinute(Long d) {
        Date date = new Date(d);
        SimpleDateFormat time = new SimpleDateFormat("HH:mm");
        return time.format(date);
    }

    public static String getDay(Long d) {
        Date date = new Date(d);
        SimpleDateFormat day = new SimpleDateFormat("MMM dd");
        SimpleDateFormat year = new SimpleDateFormat("yyyy");
        return day.format(date);
    }

    public static String getYear(Long d) {
        Date date = new Date(d);
        SimpleDateFormat year = new SimpleDateFormat("yyyy");
        return year.format(date);
    }

}
