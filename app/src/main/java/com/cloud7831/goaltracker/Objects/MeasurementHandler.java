package com.cloud7831.goaltracker.Objects;

import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import com.cloud7831.goaltracker.Data.GoalsContract;
import com.cloud7831.goaltracker.Data.GoalsContract.*;
import com.cloud7831.goaltracker.HelperClasses.StringHelper;
import com.cloud7831.goaltracker.HelperClasses.TimeHelper;

public class MeasurementHandler {
    private static final String LOGTAG = "MeasurementSliderObject";

    private Goal goal;
    private int quotaGoalForToday; // How much quota should be completed today. Only needs to be calculated once, but is used in a couple spots.
    private TextView quotaText;
    private int resizeScale = 1;

    public MeasurementHandler(Goal goal, SeekBar slider, TextView text){
        if(slider == null||text == null){
            Log.e(LOGTAG, "slider or text was null");
        }

        this.goal = goal;
        quotaGoalForToday = calcQuotaGoalForToday();
        quotaText = text;

        int numNotches = calcNotches();

//        Log.i(LOGTAG, "numNotches" + numNotches);

        slider.setMax(numNotches);
        todaysQuotaToString(0);
    }

    public int getQuotaGoalForToday(){
        if(quotaGoalForToday < 0){
            Log.e(LOGTAG, "quotaGoalForToday shouldn't be negative.");
        }
        return quotaGoalForToday;
    }

    public void updateQuotaTally(int progress){
        goal.setQuotaTally(calcQuotaProgress(progress));
    }

    public int getQuotaTally() {
        return goal.getQuotaTally();
    }

    public void todaysQuotaToString(int progress){
        // Returns a string for the progress bar slider.
        // Shows how much of the goal has been completed vs the goal for today.
        String quotaString = "";


        int progVal = calcQuotaProgress(progress);
        int maxVal = quotaGoalForToday - goal.getQuotaToday();
        quotaString = StringHelper.getStringQuotaProgressAndUnits(progVal, maxVal, goal.getUnits());

//        if(GoalEntry.isValidTime(goal.getUnits())){
//            quotaString += Double.toString(TimeHelper.roundAndConvertTime(progVal));
//        }
//        else{
//            quotaString += Integer.toString(progVal);
//        }
//
//        quotaString += "/";
//        // TODO: this needs to be properly format the time.
//        // TODO: for time values, the end value may need to be converted, and so do the units.
//        if(GoalEntry.isValidTime(goal.getUnits())){
//            quotaString += Double.toString(TimeHelper.roundAndConvertTime(maxVal));
//        }
//        else{
//            quotaString += Integer.toString(maxVal);
//        }
//
//        // TODO: make it so that units can be translated.
//        // TODO: time values may use units that aren't the initially declared units.
//        quotaString += " " + goal.getUnits() + "s";

        quotaText.setText(quotaString);
    }

    private int calcNotches(){
//        Log.i(LOGTAG, "entering calc notches");
        int maxVal = quotaGoalForToday - goal.getQuotaToday();
//        Log.i(LOGTAG, "maxVal " + maxVal);
        int quotaPerNotch = calcQuotaPerNotch();
//        Log.i(LOGTAG, "quotaPerNotch " + quotaPerNotch);

        int num = (int)Math.ceil((double)maxVal/quotaPerNotch);

//        Log.i(LOGTAG, "num " + num);
        if(num <= 0 || num > 10){
            Log.e(LOGTAG, "The calculation for the number of notches was not in the expected range.");
            return -1;
        }

//        Log.i(LOGTAG, "returning from calcNotches");
        return num;
    }

    private int calcQuotaPerNotch(){
        // Calculates how much quota per notch of the progress bar slider.
        int maxVal = quotaGoalForToday - goal.getQuotaToday();
        if(GoalEntry.isValidTime(goal.getUnits())){
            // The values are in times, so conversions may need to be done.
            // This quota per notch is returned in seconds.
            if(maxVal <= 10){
                // Break it into 1s intervals
                return 1;
            }
            else if(maxVal <= 20){
                // Break it into 2s intervals
                return 2;
            }
            else if(maxVal <= 50){
                // Break it into 5s intervals
                return 5;
            }
            else if (maxVal <= 100){
                // Break it into 10s intervals
                return 10;
            }
            else if(maxVal <= 300){
                // Break it into 30s intervals
                return 30;
            }
            else if(maxVal <= 600){
                // Break it into 1m intervals
                return 60;
            }
            else if(maxVal <= 1200){
                // Break it into 2m intervals
                return 120;
            }
            else if(maxVal < 3000){
                // Note, not <= because it's better if 50mins is handled by an increment of 10mins.
                // Break it into 5m intervals
                return 300;
            }
            else if(maxVal <= 5400){
                // Break it into 10m intervals
                return 600;
            }
            else if(maxVal <= 9000){
                // Break it into 15m intervals
                return 900;
            }
            else if(maxVal <= 18000){
                // Break it into 30m intervals
                return 1800;
            }
            else if(maxVal <= 36000){
                // Break it into 1h intervals
                return 3600;
            }
            else if(maxVal <= 72000){
                // Break it into 2h intervals
                return 7200;
            }
            else if(maxVal <= 86400){
                // Break it into 3h intervals
                return 10800;
            }
            else{
                // Something went wrong, because there was a time value I didn't account for
                Log.e(LOGTAG, "There was a time value that was too great.");
                return -1;
            }
        }
        else {
            if(maxVal < 10){
                return 1;
            }
            else if(maxVal <= 20){
                return 2;
            }
            else if(maxVal <= 50){
                return 5;
            }
            else if (maxVal <= 100){
                return 10;
            }
            else if(maxVal <= 250){
                return 25;
            }
            else if(maxVal <= 500){
                return 50;
            }
            else if(maxVal <= 1000){
                return 100;
            }
            else if(maxVal < 2500){
                return 250;
            }
            else if(maxVal <= 5000){
                return 500;
            }
            else if(maxVal <= 10000){
                return 1000;
            }
            else if(maxVal <= 25000){
                return 2500;
            }
            else if(maxVal <= 50000){
                return 5000;
            }
            else if(maxVal <= 100000){
                return 10000;
            }
            else{
                // Use integer division to round to the nearest 10000, then break it into 10 notches.
                return (maxVal/10000)*10000 / 10;
            }
        }

    }

    private int calcQuotaProgress(int progress){
        // maxVal should have been calculated with calcTodaysQuota()
        // if the units are time based, then maxVal is in seconds
        if(progress == 0){
            return 0;
        }

        // TODO: if these two values are equal, it causes errors.
        // TODO: make a default value for if the goal has already been met.
        int maxVal = quotaGoalForToday - goal.getQuotaToday();

        if(progress >= calcNotches()){
            // This is the max value of the progress bar, so just return maxVal.
            return maxVal;
        }
        else{
            return calcQuotaPerNotch() * progress;
        }
    }

    private int calcQuotaGoalForToday() {
        // Returns the amount of quota that must be completed today in order to stay on track.
        // If units are time-based, then the return int is in seconds.

        // Calculate the amount of Quota that needs to be completed over the goal period
        int quotaLeftOverPeriod;
        if(goal.getFrequency() == GoalsContract.GoalEntry.DAILYGOAL){
            quotaLeftOverPeriod = goal.getQuota() - goal.getQuotaToday();
        }
        else if(goal.getFrequency() == GoalsContract.GoalEntry.WEEKLYGOAL){
            quotaLeftOverPeriod = goal.getQuota() - goal.getQuotaWeek() - goal.getQuotaToday();
        }
        else if(goal.getFrequency() == GoalsContract.GoalEntry.MONTHLYGOAL){
            quotaLeftOverPeriod = goal.getQuota() - goal.getQuotaMonth() - goal.getQuotaWeek() - goal.getQuotaToday();
        }
//        else if(frequency == GoalsContract.GoalEntry.FIXEDGOAL){
//            //TODO:
//        }
        else{
            quotaLeftOverPeriod = goal.getQuota();
        }

        // Check that the goal is not already completed.
        if(quotaLeftOverPeriod < 0){
            // Therefore, calculate how much you would have had to do on day 1 to give a reasonable
            // goal for today.
            quotaLeftOverPeriod = goal.getQuota();
        }

        // Calculate how many sessions the user needs to complete their goal.
        int sessionsRemaining;
        if(goal.getSessions() == 0){
            // Just incase sessions was never set.
            sessionsRemaining = 1;
            goal.setSessions(1);
        }else{
            sessionsRemaining = goal.getSessions() - goal.getSessionsTally();
        }

        if(sessionsRemaining <=0){
            sessionsRemaining = goal.getSessions();
        }

        return sliceQuota(quotaLeftOverPeriod, sessionsRemaining);
    }

    private int sliceGenericQuota(int quota, int numSlices) {
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

    private int sliceQuota(int q, int s){
        if(GoalEntry.isValidTime(goal.getUnits())){
            return sliceTimeQuota(q, s);
        }
        else{
            return sliceGenericQuota(q, s);
        }
    }

    private int sliceTimeQuota(int seconds, int numSlices) {
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

}
