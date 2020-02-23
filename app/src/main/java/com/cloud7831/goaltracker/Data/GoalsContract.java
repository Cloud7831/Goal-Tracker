package com.cloud7831.goaltracker.Data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class GoalsContract {

    public static final String CONTENT_AUTHORITY = "com.cloud7831.goaltracker";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_GOALS = "goals";

    public static abstract class GoalEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_GOALS);


        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of goals.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_GOALS;



        /**
         * The MIME type of the {@link #CONTENT_URI} for a single goal.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_GOALS;


        public final static String TABLE_NAME = "goals";

        public final static String _ID = BaseColumns._ID;
        public final static String GOAL_NAME = "name";
        public final static String GOAL_CLASSIFICATION = "classification";
        public final static String GOAL_INTENTION = "intention";
        public final static String GOAL_FREQUENCY = "frequency";
        public final static String GOAL_DISPLAY_DATE = "display_date";
        public final static String GOAL_QUOTA = "quota";
        public final static String GOAL_UNITS = "units";
        public final static String GOAL_DURATION = "duration";
        public final static String GOAL_DEADLINE = "deadline";
        public final static String GOAL_START_DATE = "start_date";
        public final static String GOAL_SKIP = "skip";
        public final static String GOAL_PRIORITY = "priority";

        public final static int UNDEFINED = 0;      // Used for all "emums"

        // Goals Frequencies
        public final static int DAILYGOAL = 1;      // Goals that you want to achieve every single day. Ex: Brush teeth, eat less than 3000 calories.
        public final static int WEEKLYGOAL = 2;     // Weekly goals are the main types of goals. More flexibility when you do something. Learn Japanese (10 hours/week). Exercise (5 days a week).
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

        // Goals Units
        public final static int HOURS = 1;
        public final static int MINUTES = 2;
        public static boolean isValidUnits(int units) {
            if (units == HOURS || units == UNDEFINED || units == MINUTES) {
                return true;
            }
            return false;
        }

        // Goals Intentions
        public final static int AVOIDANCE = 1;
        public final static int REGULAR = 2;       // This is for any goal where you're trying to build a positive habit.

        public static boolean isValidIntention(int inten) {
            if (inten == UNDEFINED || inten == AVOIDANCE || inten == REGULAR) {
                return true;
            }
            return false;
        }
    }


    // TODO: this is outdated and can probably be deleted.
    // All the possible types of a goal card
    public enum GoalsCardType{
        UNDEFINED,
        TASK,           //used for one time tasks (that might be repeated in the future). Ex: do to dentist. Call doctor.
        MONITORING,      // Not so much a goal, but instead just about data tracking for whatever you choose. Hours watching TV, hours playing video games, etc.
        AVOIDANCE,       // For the user to break a habit, such as fast food, drinking, alcohol, etc.
        RECURRING        // Recurring is essentially any weekly, monthly, daily goal.
    }
}
