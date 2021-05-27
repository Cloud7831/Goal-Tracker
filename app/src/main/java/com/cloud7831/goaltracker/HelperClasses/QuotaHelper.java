package com.cloud7831.goaltracker.HelperClasses;

import android.util.Log;

import com.cloud7831.goaltracker.Data.GoalsContract.*;
import com.cloud7831.goaltracker.Objects.Goals.DailyHabit;
import com.cloud7831.goaltracker.Objects.Goals.MonthlyHabit;
import com.cloud7831.goaltracker.Objects.Goals.Task;
import com.cloud7831.goaltracker.Objects.Goals.WeeklyHabit;

import java.util.Calendar;

public final class QuotaHelper {

    private static final String LOGTAG = "QuotaHelper";

    /**
     * Decides how much quota the user should try to achieve today. Depending on the progress of
     * the goal, the user may have to complete multiple sessions.
     * @param task from goal list
     * @return a flat amount of quota that the user can realistically hope to achieve today. If the
     * task uses time based units, the return value will be in seconds.
     */
    public static int calcQuotaGoalForToday(Task task) {
        // TODO: I should look into this further, because I don't want to return a number that is
        //  too small. I think this works the way I want, but I need to write tests for it.

        //TODO: Depending on how on track the user is, they may have to do more than one session
        // today in order to meet the deadline for the Task.

        // Returns the amount of quota that must be completed today in order to stay on track.
        // If units are time-based, then the return int is in seconds.

        // Calculate the amount of Quota that needs to be completed over the goal period
        // Note: quotaLeftOverPeriod should not include the quota completed today. The point is that
        // this function must return the same value, regardless of what time of day it is, or how
        // much quota was already completed today.
        int goalQuota = task.getQuota();
        if(goalQuota <= 0){
            Log.e(LOGTAG, "A quota was never set for the task " + task.getTitle());
            goalQuota = 1;
        }

        // How much of the quota hasn't been completed for this measuring period
        int quotaLeftOverPeriod;
        if(task instanceof DailyHabit){
            quotaLeftOverPeriod = goalQuota;
        }
        else if(task instanceof WeeklyHabit){
            quotaLeftOverPeriod = goalQuota - task.getQuotaTally();
        }
        else if(task instanceof MonthlyHabit){
            quotaLeftOverPeriod = goalQuota - task.getQuotaTally() - ((MonthlyHabit)task).getQuotaWeek();
        }
        else {
            // Task or some other type.
            // TODO: right now task is the only other type, but when I add workouts/exercises I will need to fix this.
            quotaLeftOverPeriod = goalQuota - task.getQuotaTally();
        }

        // Calculate how many sessions the user has remaining to complete their goal.
        int sessionsRemaining;
        if(task.getSessions() <= 0){
            // Just incase sessions was never set.
            Log.e(LOGTAG, "The number of sessions was never set.");
            sessionsRemaining = 1;
            task.setSessions(1);
        }
        else{
            sessionsRemaining = task.getSessions() - task.getSessionsTally();
        }

        // Check that the goal is not already completed.
        if(quotaLeftOverPeriod <= 0){
            // Use the amount that would have been needed on day 1 to give a reasonable goal for today.
            quotaLeftOverPeriod = goalQuota;
            sessionsRemaining = task.getSessions();
        }

        // Just incase the sessions got changed to something smaller than it originally was, but
        // the goal still isn't completed.
        if(sessionsRemaining <=0){
            sessionsRemaining = 1;
        }

        return QuotaHelper.sliceQuota(quotaLeftOverPeriod, sessionsRemaining, task.getUnits());
    }


    public static int quotaProgressionRate(int freq, int quotaCompleted, int quota, int sessions){
        // TODO: this function is unfinished.
        // It can be a bit much to make the user aim to work every day of the measure period
        // so I think it's better to give them a couple rest days to catch up if they're behind.
        // Of course, the user can still choose to work everyday, but because they leave it later,
        // their progression rate will be higher because they're at risk of not completing the goal.
        // For weekly goals, I think it's good to aim to work 5 of the 7 days at most.
        final int daysForWorking;
        if(freq == GoalEntry.FREQ_WEEKLY){
            daysForWorking = 5;
        }
        else if(freq == GoalEntry.FREQ_DAILY){
            // Obviously for a DailyHabit, it doesn't make sense to split the goal over 3 days,
            // but instead we'll split it over three periods throughout the day.
            daysForWorking = 3;
        }
        else if(freq == GoalEntry.FREQ_MONTHLY){
            // Basically it's 4 weeks of working M-F. Anything more than that would be too much for
            // a monthly goal.
            daysForWorking = 20;
        }
        else{
            // TODO: figure out what to do for Tasks that don't have a deadline. For now, I'll just
            //  assume that the goal is progressing at a standard rate. Returning 50 means that it'll
            //  be hidden after swiping.
            return 50;
        }

        // Round this double to get how many sessions should be completed by that day
        // if 1*sessionsPerDayWorking == 1.75, that means the first day should complete 2 sessions.
        final double sessionsPerDayWorking = ((double)sessions)/daysForWorking;

        int todaysDate = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        // Calendar gives Sunday == 1, Saturday == 7, but
        // I want Monday to be day 1 and Sunday to be day 7
        if(todaysDate == 1){
            todaysDate = 7;
        }
        else{
            todaysDate -= 1;
        }

        return 50;

    }

    //region FUNCTIONS FOR SLICING QUOTA

    /**
     * Takes in a quota q and an amount of sessions s and returns the amount of quota that should
     * be completed for the next session.
     * @param q the amount of quota to slice
     * @param s the amount of slices to cut the quota into
     * @param units units are either time based or generic (pages, reps, lbs, etc)
     * @return a nice flat amount of quota that is roughly q/s.
     */

    public static int sliceQuota(int q, int s, String units){
        if(GoalEntry.isValidTime(units)){
            return sliceTimeQuota(q, s);
        }
        else{
            return sliceGenericQuota(q, s);
        }
    }

    private static int sliceGenericQuota(int quota, int numSlices) {
        // Takes in quota and then tries to split it into formatted slices
        // Slices need to be a number appropriate for displaying in a simple manner.
        // For example, if the quota measures to be 221 units, it be rounded up to 225 or 250,
        // Returns the quota of the first (largest) slice.
        // The first slice will be the biggest (because it's better for the user to do the large
        // slices first)

        if (numSlices <= 0) {
            Log.e(LOGTAG, "Function sliceQuota: numSlices was less than 1");
            return -1;
        }

        // This will give a decimal, but we want the final values to be a nice int.
        double exactVal = ((double) (quota)) / (numSlices);

        // exactVal is the exact amount of quota assigned per session.
        // The goal is to split this exact amount into a nice int so that there are no decimals.

        int baseVal; // The minimum amount of quota that each day has.
        int leftOver; // The amount of quota that needs to be redistributed amongst the days remaining.
        int threshhold = 1;
        // If exactVal is smaller than 10, then each increment is just 1, and the notches of the
        // bar gets adjusted to match.
        if (exactVal <= 25){
            threshhold = 1;
        }
        else if(exactVal <= 100) {
            threshhold = 5;
        }
        else if (exactVal <= 200) {
            threshhold = 10;
        }
        else if (exactVal <= 500) {
            threshhold = 25;
        }
        else if (exactVal <= 1000) {
            threshhold = 50;
        }
        else if (exactVal <= 2500) {
            threshhold = 100;
        }
        else if (exactVal <= 5000) {
            threshhold = 250;
        }
        else {
            threshhold = 500;
        }

        baseVal = ((int) ((exactVal)/threshhold)) * threshhold; // cast to int is an intentional floor option.
        leftOver = quota - (baseVal * numSlices);
        if (leftOver > 0) {
            return baseVal + threshhold;
        } else {
            return baseVal;
        }

    }

    private static int sliceTimeQuota(int seconds, int numSlices) {
        // Takes in time as seconds, and then tries to split it into formatted slices
        // Slices need to be a number appropriate for displaying simply
        // For example, if the time measures to be 3201 seconds, it be rounded up to 55 minutes,
        // instead of 53.35 minutes.
        // Returns the number of seconds of the first slice.
        // The first slice will be the biggest (because it's better for the user to do the large
        // slices first)

        if (numSlices <= 0) {
            Log.e(LOGTAG, "Function sliceTimeSegment: numSlices was less than 1");
            return -1;
        }

        // This will give a decimal, but we want the final values to be a nice int.
        double exactVal = ((double) (seconds)) / (numSlices);

        // exactVal is the exact amount of quota assigned per session.
        // The goal is to split this exact amount into a nice int so that there are no decimals.

        int baseVal; // The minimum amount of quota that each day has.
        int leftOver; // The amount of quota that needs to be redistributed amongst the days remaining.
        int threshhold;
        // If exactVal is smaller than 10, then each increment is just 1, and the notches of the
        // bar gets adjusted to match.
        if (exactVal <= 15){
            threshhold = 1;
        }
        else if(exactVal <= 60) {
            threshhold = 5;
        }
        else if (exactVal <= 300) {
            threshhold = 15;
        }
        else if (exactVal <= 600) {
            threshhold = 30;
        }
        else if (exactVal <= 900) {
            threshhold = 60;
        }
        else if (exactVal <= 3600) {
            threshhold = 300;
        }
        else if (exactVal <= 10800) {
            threshhold = 900;
        }
        else {
            threshhold = 1800;
        }

        baseVal = ((int) ((exactVal)/threshhold)) * threshhold; // cast to int is an intentional floor option.
        leftOver = seconds - (baseVal * numSlices);
        if (leftOver > 0) {
            return baseVal + threshhold;
        } else {
            return baseVal;
        }

    }
    //endregion FUNCTIONS FOR SLICING QUOTA

}
