package com.cloud7831.goaltracker.Data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class GoalsContract {

    public static class GoalEntry {

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

        // The options for the units spinner.
        public static final int MINUTES = 0;
        public static final int HOURS = 1;

        public static final String SECOND_STRING = "sec";
        public static final String MINUTE_STRING = "min";
        public static final String HOUR_STRING = "hour";

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


        public static boolean isValidClassification(int classification) {
            if (classification == UNDEFINED || classification == BREAKING || classification == BUILDING) {
                return true;
            }
            return false;
        }
    }
}
