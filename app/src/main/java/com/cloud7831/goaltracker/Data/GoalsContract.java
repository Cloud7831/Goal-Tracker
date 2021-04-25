package com.cloud7831.goaltracker.Data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import com.cloud7831.goaltracker.R;

public final class GoalsContract {

    public static class GoalEntry {
        public static final String LOGTAG = "GoalsContract.GoalEntry";

        public static final String KEY_GOAL_ID = "Goal ID";
        public static final String KEY_GOAL_TYPE = "Goal type";

        public final static int UNDEFINED = 0;      // Used for all "emums"

        // Goals Frequencies
        public final static int DAILYGOAL = 1;      // Goals that you want to achieve every single day. Ex: Brush teeth, eat less than 3000 calories.
        public final static int WEEKLYGOAL = 2;     // Weekly goals are the main types of goals. More flexibility when you do something. Learn Japanese (10 hours/week). Exercise (5 days a week).
        public final static int MONTHLYGOAL = 3;    // Monthly goals are more for making sure you do something longterm. Ex: put $1,000 into savings.
        public final static int TASKGOAL = 4;      // This is for one off goals, or ones that occur for only two weeks or such.
        public final static int WORKOUTGOAL = 5;
        //YEARLYGOAL     // Yearly goals should almost never be used. Use Long-Term Goals/Milestones instead.
        //MULTIGOAL,      // This is when you want to mix both a weekly and monthly and potentially even a daily goal together.

        public static boolean isValidFrequency(int freq) {
            if (freq == DAILYGOAL || freq == UNDEFINED || freq == WEEKLYGOAL || freq == MONTHLYGOAL || freq == TASKGOAL || freq == WORKOUTGOAL) {
                return true;
            }
            Log.i(LOGTAG, "invalid frequency");
            return false;
        }

        // Goals Intentions
        // The order of these must match up with the ordering of the array in the spinner.
        public final static int BUILDING = 1;
        public final static int BREAKING = 2;
        public final static int TASK_ACTIVE = 3;
        public final static int TASK_INACTIVE = 4;

        public static boolean isValidIntention(int inten) {
            if (inten == UNDEFINED || inten == BREAKING || inten == BUILDING || inten == TASK_ACTIVE || inten == TASK_INACTIVE) {
                return true;
            }
            Log.i(LOGTAG, "invalid intention");
            return false;
        }

        // Goal Classification
        // The order of these must match up with the ordering of the array in the spinner.
        public final static int HABIT = 1;
        public final static int TASK = 2;
        public final static int EVENT = 3;
        public final static int LIST = 4;

        // Goal Priority
        // The order of these must match up with the ordering of the array in the spinner.
        public final static int PRIORITY_VERY_LOW = 1;
        public final static int PRIORITY_LOW = 2;
        public final static int PRIORITY_MEDIUM = 3;
        public final static int PRIORITY_HIGH = 4;
        public final static int PRIORITY_VERY_HIGH = 5;


        public static boolean isValidClassification(int classification) {
            if (classification == UNDEFINED || classification == BREAKING || classification == BUILDING) {
                return true;
            }
            Log.i(LOGTAG, "invalid classification");
            return false;
        }


        // The options for the units spinner.
        // The order of these must match up with the ordering of the array in the spinner.
        public static final int SECONDS = 1;
        public static final int MINUTES = 2;
        public static final int HOURS = 3;
        public static final int TIMES = 4;
        public static final int REPS = 5;
        public static final int PAGES = 6;


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
    }
}
