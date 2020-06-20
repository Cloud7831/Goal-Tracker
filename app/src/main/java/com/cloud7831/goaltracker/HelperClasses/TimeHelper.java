package com.cloud7831.goaltracker.HelperClasses;

import java.util.Calendar;

public final class TimeHelper {

    public static long calcMilliUntilMidnight(){
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return (c.getTimeInMillis()-System.currentTimeMillis()); // num milliseconds.
    }

}
