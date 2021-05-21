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
        // Goals Types
        // The order of these must match up with the ordering of the array in the spinner.
        public final static int TYPE_DAILYGOAL = 1;      // Goals that you want to achieve every single day. Ex: Brush teeth, eat less than 3000 calories.
        public final static int TYPE_WEEKLYGOAL = 2;     // Weekly goals are the main types of goals. More flexibility when you do something. Learn Japanese (10 hours/week). Exercise (5 days a week).
        public final static int TYPE_MONTHLYGOAL = 3;    // Monthly goals are more for making sure you do something longterm. Ex: put $1,000 into savings.
        public final static int TYPE_TASK = 4;      // This is for one off goals, or ones that occur for only two weeks or such.
        public final static int TYPE_WORKOUT = 5;      // Extends habit and is used to track a collection of exercises
        public final static int TYPE_EVENT = 6;      // Used to keep track of upcoming dates
        public final static int TYPE_TRACKER = 7;      // Used to track the change in a measured value (i.e weight gain)

        //YEARLYGOAL     // Yearly goals should almost never be used. Use Long-Term Goals/Milestones instead.
        //MULTIGOAL,      // This is when you want to mix both a weekly and monthly and potentially even a daily goal together.

        public static boolean isValidGoalType(int type) {
            if (type == TYPE_DAILYGOAL
                    || type == TYPE_WEEKLYGOAL
                    || type == TYPE_MONTHLYGOAL
                    || type == TYPE_TASK
                    || type == TYPE_WORKOUT
                    || type == TYPE_EVENT
                    || type == TYPE_TRACKER){
                return true;
            }
            if(type == UNDEFINED){
                Log.e(LOGTAG, "the type of the goal was undefined");
                return false;
            }
            Log.e(LOGTAG, "invalid goal type");
            return false;
        }

        // Goals are reset based on the frequency.
        public final static int FREQ_DAILY = 1;
        public final static int FREQ_WEEKLY = 2;
        public final static int FREQ_MONTHLY = 3;
        // FREQ_FIXED

        public static boolean isValidFrequency(int freq) {
            if (freq == FREQ_DAILY  || freq == FREQ_WEEKLY || freq == FREQ_MONTHLY) {
                return true;
            }
            if(freq == UNDEFINED){
                Log.e(LOGTAG, "the frequency was never set");
                return false;
            }
            Log.e(LOGTAG, "invalid frequency");
            return false;
        }

        // Goals Intentions
        // The order of these must match up with the ordering of the array in the spinner.
        public final static int INT_BUILDING = 1;
        public final static int INT_BREAKING = 2;
        public static final int INT_GAIN = 3;
        public static final int INT_LOSE = 4;

        public static boolean isValidIntention(int inten) {
            if (inten == INT_BREAKING || inten == INT_BUILDING || inten == INT_GAIN || inten == INT_LOSE) {
                return true;
            }
            if(inten == UNDEFINED){
                Log.e(LOGTAG, "Intention was undefined");
                return false;
            }
            Log.e(LOGTAG, "invalid intention");
            return false;
        }

        // Is Hidden / Visibility
        public static final int VISIBILITY_HIDDEN = 1;
        public static final int VISIBILITY_UNHIDDEN = 2;
        public final static int VISIBILITY_INACTIVE = 3;
        public final static int VISIBILITY_PAUSED = 4;

        public static boolean isValidIsHidden(int h) {
            if (h == VISIBILITY_UNHIDDEN || h == VISIBILITY_HIDDEN || h == VISIBILITY_INACTIVE || h ==  VISIBILITY_PAUSED) {
                return true;
            }
            else if (h == UNDEFINED){
                Log.e(LOGTAG, "IsHidden was undefined");
                return false;
            }
            Log.i(LOGTAG, "invalid visibility");
            return false;
        }

        // Goal Classification
        // The order of these must match up with the ordering of the array in the spinner.
        public final static int CLASS_TASK = 1;
        public final static int CLASS_HABIT = 2;
        public final static int CLASS_EVENT = 3;
        public final static int CLASS_WORKOUT = 4;
        public static final int CLASS_TRACKER = 5;

        public static boolean isValidClassification(int classification) {
            if (classification == CLASS_HABIT
                    || classification == CLASS_TASK
                    || classification == CLASS_EVENT
                    || classification == CLASS_WORKOUT
                    || classification == CLASS_TRACKER){
                return true;
            }
            if(classification == UNDEFINED){
//                Log.e(LOGTAG, "classification was undefined.");
            }
//            Log.i(LOGTAG, "invalid classification");
            return false;
        }

        // Goal Priority
        // The order of these must match up with the ordering of the array in the spinner.
        public final static int PRIORITY_VERY_LOW = 1;
        public final static int PRIORITY_LOW = 2;
        public final static int PRIORITY_MEDIUM = 3;
        public final static int PRIORITY_HIGH = 4;
        public final static int PRIORITY_VERY_HIGH = 5;

        public static boolean isValidPriority(int p) {
            if (p == PRIORITY_VERY_HIGH || p == PRIORITY_HIGH || p == PRIORITY_MEDIUM || p == PRIORITY_LOW || p == PRIORITY_VERY_LOW) {
                return true;
            }
            else if(p == UNDEFINED){
                Log.e(LOGTAG, "priority was undefined.");
                return false;
            }
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

        public static final String REPS_STRING = "rep";
        public static final String TIMES_STRING = "time";
        public static final String PAGES_STRING = "page";
        public static final String UNITS_NULL = "";
        // These are for internal use and not for displaying to the user.
        public static final String SECOND_STRING = "sec";
        public static final String MINUTE_STRING = "min";
        public static final String HOUR_STRING = "hour";

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
