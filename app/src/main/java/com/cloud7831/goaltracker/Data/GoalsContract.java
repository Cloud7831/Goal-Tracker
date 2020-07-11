package com.cloud7831.goaltracker.Data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import com.cloud7831.goaltracker.R;

public final class GoalsContract {

    public static class GoalEntry {
        public static final String LOGTAG = "GoalsContract.GoalEntry";

        public final static int UNDEFINED = 0;      // Used for all "emums"

        // Goals Frequencies
        public final static int DAILYGOAL = 2;      // Goals that you want to achieve every single day. Ex: Brush teeth, eat less than 3000 calories.
        public final static int WEEKLYGOAL = 1;     // Weekly goals are the main types of goals. More flexibility when you do something. Learn Japanese (10 hours/week). Exercise (5 days a week).
        public final static int MONTHLYGOAL = 3;    // Monthly goals are more for making sure you do something longterm. Ex: put $1,000 into savings.
        public final static int FIXEDGOAL = 4;      // This is for one off goals, or ones that occur for only two weeks or such.
        //YEARLYGOAL     // Yearly goals should almost never be used. Use Long-Term Goals/Milestones instead.
        //MULTIGOAL,      // This is when you want to mix both a weekly and monthly and potentially even a daily goal together.

        public static boolean isValidFrequency(int freq) {
            if (freq == DAILYGOAL || freq == UNDEFINED || freq == WEEKLYGOAL || freq == MONTHLYGOAL || freq == FIXEDGOAL) {
                return true;
            }
            return false;
        }

        // Goals Intentions
        public final static int BUILDING = 1;
        public final static int BREAKING = 2;

        public static boolean isValidIntention(int inten) {
            if (inten == UNDEFINED || inten == BREAKING || inten == BUILDING) {
                return true;
            }
            return false;
        }

        // Goal Classification
        public final static int TASK = 1;
        public final static int HABIT = 2;
        public final static int EVENT = 3;
        public final static int LIST = 4;

        // Goal Priority
        public final static int PRIORITY_VERY_LOW = 1;
        public final static int PRIORITY_LOW = 2;
        public final static int PRIORITY_MEDIUM = 3;
        public final static int PRIORITY_HIGH = 4;
        public final static int PRIORITY_VERY_HIGH = 5;


        public static boolean isValidClassification(int classification) {
            if (classification == UNDEFINED || classification == BREAKING || classification == BUILDING) {
                return true;
            }
            return false;
        }


        // The options for the units spinner.
        public static final int MINUTES = 1;
        public static final int HOURS = 0;
        public static final int SECONDS = 2;
        public static final int TIMES = 3;
        public static final int REPS = 4;
        public static final int PAGES = 5;


        // TODO: add reps and times as options to the unit spinner.

        public static final String REPS_STRING = "rep";
        public static final String TIMES_STRING = "time";
        public static final String PAGES_STRING = "page";
        public static final String UNITS_NULL = "";

        // -------------------------------- Time ------------------------------------

        public static boolean isValidTime(String units) {
            boolean returnVal;
            if (units.equals(MINUTE_STRING) || units.equals(HOUR_STRING) || units.equals(SECOND_STRING)) {
                returnVal = true;
            }
            else{
                returnVal = false;
            }
            return returnVal;
        }

        public static boolean isValidPriority(int p) {
            boolean returnVal;
            if (p == PRIORITY_VERY_HIGH || p == PRIORITY_HIGH || p == PRIORITY_MEDIUM || p == PRIORITY_LOW || p == PRIORITY_VERY_LOW) {
                returnVal = true;
            }
            else{
                returnVal = false;
            }
            return returnVal;
        }

        // These are for internal use and not for displaying to the user.
        public static final String SECOND_STRING = "sec";
        public static final String MINUTE_STRING = "min";
        public static final String HOUR_STRING = "hour";

        public static int convertToSeconds(double t, String u){
            // Takes in an amount of time (t) and a type of units (u) and returns that number in seconds.
            // t = 5, u = hours, would return 5*60*60 seconds.
            int seconds;
            if(u == GoalsContract.GoalEntry.HOUR_STRING){
                seconds = (int)(t * 60 *60); // hours -> minutes -> seconds
            }
            else if(u == GoalsContract.GoalEntry.MINUTE_STRING){
                seconds = (int)(t * 60);
            }
            else if(u == GoalsContract.GoalEntry.SECOND_STRING){
                seconds = (int)t;
            }
            else{
                Log.i(LOGTAG, "convertToSeconds did not recognize the time units.");
                return (int)t;
            }
            return seconds;
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
}
