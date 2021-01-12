package com.cloud7831.goaltracker.HelperClasses;

import com.cloud7831.goaltracker.Data.GoalsContract;

public final class StringHelper {

    public static String getStringQuotaProgressAndUnits(int quotaCompleted, int quotaGoal, String units){
        // The point of this function is to express a quota in a string such as
        // "15/90 minutes"
        // so that the progress of a goal can be displayed to a user.
        // Some units, such as time, need to be converted (minutes to hours, etc) and therefore
        // this function's purpose is to convert all corner cases properly.

        String str = "";
        String convertedUnits = units; // Some types of units such as time will need to convert.
        double convertedQuotaGoal = quotaGoal;
        double convertedQuotaCompleted = quotaCompleted;

        if(GoalsContract.GoalEntry.isValidTime(units)){
            // The quota was given in seconds, but it may need to be converted to minutes, or hours.
            // I always want the time to round up when displaying.
            if(quotaGoal <= 15){
                convertedUnits = GoalsContract.GoalEntry.SECOND_STRING;
            }
            else if(quotaGoal <= 120){
                // Round to a multiple of 5.
                convertedQuotaGoal = Math.ceil(convertedQuotaGoal/5) * 5;
                convertedUnits = GoalsContract.GoalEntry.SECOND_STRING;
            }
            else if(quotaGoal <= 600){
                // Round so that it's displayed in minutes
                // The decimals should be in increments of 0.25
                convertedQuotaGoal = Math.ceil(convertedQuotaGoal/15)/4;
                convertedQuotaCompleted = Math.ceil(convertedQuotaCompleted/15)/4;
                convertedUnits = GoalsContract.GoalEntry.MINUTE_STRING;
            }
            else if(quotaGoal <= 1800){
                // Round so that it's displayed in minutes
                // The decimals should be in increments of 0.5
                convertedQuotaGoal = Math.ceil(convertedQuotaGoal/30)/2;
                convertedQuotaCompleted = Math.ceil(convertedQuotaCompleted/30)/2;
                convertedUnits = GoalsContract.GoalEntry.MINUTE_STRING;
            }
            else if(quotaGoal <= 7200){
                // Round so that it's displayed in minutes
                convertedQuotaGoal = Math.ceil(convertedQuotaGoal/60);
                convertedQuotaCompleted = Math.ceil(convertedQuotaCompleted/60);
                convertedUnits = GoalsContract.GoalEntry.MINUTE_STRING;
            }
            else if(quotaGoal <= 10800){
                // Round so that it's displayed in hours
                // The decimals should be in increments of 0.25
                convertedQuotaGoal = Math.ceil(convertedQuotaGoal/60/15)/4;
                convertedQuotaCompleted = Math.ceil(convertedQuotaCompleted/60/15)/4;
                convertedUnits = GoalsContract.GoalEntry.HOUR_STRING;
            }
            else{
                // Round so that it's displayed in hours
                // The decimals should be in increments of 0.5
                convertedQuotaGoal = Math.ceil(convertedQuotaGoal/60/30)/2;
                convertedQuotaCompleted = Math.ceil(convertedQuotaCompleted/60/30)/2;
                convertedUnits = GoalsContract.GoalEntry.HOUR_STRING;
            }
        }

        // Now that all the quota values and units have been converted, format it as a string.
        str = Double.toString(convertedQuotaCompleted) + "/" + Double.toString(convertedQuotaGoal) + " " + convertedUnits + "s";
        return str;
    }


}
