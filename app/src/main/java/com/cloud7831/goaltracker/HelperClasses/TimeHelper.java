package com.cloud7831.goaltracker.HelperClasses;

import java.util.Calendar;
import java.util.Date;

public final class TimeHelper {

    private static final int millisecondsPerSecond = 1000;

    public static long calcMilliUntilMidnight(){
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
//        c.add(Calendar.MINUTE, 1);    // For testing purposes only
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return (c.getTimeInMillis()-System.currentTimeMillis()); // num milliseconds.
    }

    public static int convertDateToInt(Date date){
        // Good until Jan 18th 2038. After that I'll have to cut off the seconds.
        return (int) (date.getTime()/millisecondsPerSecond);// gets rid of the miliseconds
    }

    public static Date convertIntToDate(int i){
        // i is the amount of seconds since Jan 1, 1970
        return new Date((long)i*millisecondsPerSecond);
    }

}
