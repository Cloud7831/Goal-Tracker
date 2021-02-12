package com.cloud7831.goaltracker.HelperClasses;

import java.util.Calendar;
import java.util.Date;

public final class TimeHelper {

    private static final int millisecondsPerSecond = 1000;

    public static long calcMilliUntilNewDay(){
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR_OF_DAY, 4);
        c.set(Calendar.MINUTE, 0);
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

    public static double roundAndConvertTime(int s){
        // takes in a time 's' in seconds and converts it to a more appropriate size
        double time = s;
        // I always want the time to round up when displaying.
        if(s <= 15){
            time = s;
        }
        else if(s <= 120){
            // Round to a multiple of 5.
            time = Math.ceil(time/5) * 5;
        }
        else if(s <= 600){
            // Round so that it's displayed in minutes
            // The decimals should be in increments of 0.25
            time = Math.ceil(time/15)/4;
        }
        else if(s <= 1800){
            // Round so that it's displayed in minutes
            // The decimals should be in increments of 0.5
            time = Math.ceil(time/30)/2;
        }
        else if(s <= 7200){
            // Round so that it's displayed in minutes
            time = Math.ceil(time/60);
        }
        else if(s <= 10800){
            // Round so that it's displayed in hours
            // The decimals should be in increments of 0.25
            time = Math.ceil(time/60/15)/4;
        }
        else{
            // Round so that it's displayed in hours
            // The decimals should be in increments of 0.5
            time = Math.ceil(time/60/30)/2;
        }

        return time;
    }

}
