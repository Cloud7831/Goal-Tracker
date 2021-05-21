package com.cloud7831.goaltracker.Objects;

import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import com.cloud7831.goaltracker.Data.GoalsContract.*;
import com.cloud7831.goaltracker.HelperClasses.QuotaHelper;
import com.cloud7831.goaltracker.HelperClasses.StringHelper;
import com.cloud7831.goaltracker.Objects.Goals.DailyHabit;
import com.cloud7831.goaltracker.Objects.Goals.MonthlyHabit;
import com.cloud7831.goaltracker.Objects.Goals.Task;
import com.cloud7831.goaltracker.Objects.Goals.WeeklyHabit;

public class MeasurementHandler {
    private static final String LOGTAG = "MeasurementHandler";

    private Task task;
    private int quotaGoalForToday; // How much quota should be completed today. Only needs to be calculated once, but is used in a couple spots.
    private final TextView quotaText;
    private final SeekBar slider;
    private double resizeScale;

    public MeasurementHandler(SeekBar slider, TextView text){
        if(slider == null||text == null){
            Log.e(LOGTAG, "slider or text was null");
        }
        this.slider = slider;
        quotaText = text;
        resizeScale = 1;
    }

    public void setTask(Task task){
        this.task = task;
        quotaGoalForToday = QuotaHelper.calcQuotaGoalForToday(task);

        int numNotches = calcNotches();
        this.slider.setMax(numNotches);
        setProgressUsingQuotaInSlider(numNotches);
        todaysQuotaToString();
    }

    public void setQuotaInSlider(int progress){
        task.setQuotaInSlider(calcQuotaProgress(progress));
    }

    public void todaysQuotaToString(){
        // Updates the string for the progress bar slider.
        // Shows how much of the goal has been completed vs the goal for today.

        String quotaString;

        int maxVal = calcMaxQuota();
        quotaString = StringHelper.getStringQuotaProgressAndUnits(task.getQuotaInSlider(), maxVal, task.getUnits());
        quotaText.setText(quotaString);
    }

    //region RESIZING THE SLIDER
    public void increaseScaling(){
        // Don't want to let the user scale the slider to extreme amounts.
        if( resizeScale <= 4.0){
            resizeScale *= 2;
        }
        recalculateHandler();
    }

    public void decreaseScaling(){
        // Don't want to let the user scale the slider to extreme amounts.
        if( resizeScale >= 0.5){
            resizeScale = resizeScale/2.0;
        }
        recalculateHandler();
    }

    private void recalculateHandler(){
        // When the sizing changes, many parts of the handler need to be adjusted
        int numNotches = calcNotches();
        slider.setMax(numNotches);

        // Because the amount of notches has potentially changed, the progress of the slider
        // needs to be adjusted to match.
        setProgressUsingQuotaInSlider(numNotches);
    }
    //endregion RESIZING THE SLIDER

    private void setProgressUsingQuotaInSlider(int maxNotches){
        // The progress of the slider will be set, based on what the quotaInSlider is.
        // It's possible that the quotaInSlider won't accurately match up and will need to be
        // adjusted to fit the current set of quota per notch.

        int quotaPerNotch = calcQuotaPerNotch();
        if(quotaPerNotch == 0){
            Log.e(LOGTAG, "quotaPerNotch was zero when it shouldn't be.");
            quotaPerNotch = 1;
        }

        if(task.getQuotaInSlider() > 0){
            Log.e(LOGTAG, "Task: " + task.getTitle() + " had " + task.getQuotaInSlider() + " quota in the slider");
        }

        int progress = task.getQuotaInSlider()/quotaPerNotch; // Integer division is used to round down if needed.

        // Set the progess in the slider.
        if(progress <= maxNotches){
            slider.setProgress(progress);
        }
        else{
            // Can't set it higher than the max, so cap it.
            slider.setProgress(maxNotches);
        }

        // Update the quotaInSlider of the goal to reflect the new value of the progress.
        setQuotaInSlider(calcQuotaProgress(progress));
    }

    private int calcNotches(){
        int maxVal = calcMaxQuota();
        int quotaPerNotch = calcQuotaPerNotch();

        int num = (int)Math.ceil((double)maxVal/quotaPerNotch);

        if(num <= 0 || num > 10){
            Log.e(LOGTAG, "The calculation for the number of notches was not in the expected range.");
            return -1;
        }
        return num;
    }

    private int calcQuotaPerNotch(){
        // Calculates how much quota per notch of the progress bar slider.
        int maxVal = calcMaxQuota();
        if(GoalEntry.isValidTime(task.getUnits())){
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
        // Calculates the amount of quota completed depending on the position of the slider.
        // if the units are time based, then maxVal is in seconds

        if(progress == 0){
            // Don't need to calculate or scale zero so just return early.
            return 0;
        }

        int maxVal = calcMaxQuota();

        if(progress >= calcNotches()){
            // This is the max value of the progress bar, so just return maxVal.
            return maxVal;
        }
        else{
            return calcQuotaPerNotch() * progress;
        }
    }

    private int calcMaxQuota(){
        // Calculates the amount of quota for the upper threshhold of the slider.

        int quotaReturned = (int)(resizeScale * (quotaGoalForToday - task.getQuotaCompletedToday()));

        if(quotaReturned <= 0 ){
            // The goal has already been completed for today, but we can't give a slider with a
            // value of zero or less. Instead, just return the quotaGoalForToday/2 to try to
            // incentivize the user to complete 1.5x of their goal for today.
            quotaReturned = quotaGoalForToday/2;

            // Check if the quotaGoal is STILL 0.
            if(quotaReturned <= 0){
                // We don't want to return a value of 0 for the max.
                // If quotaReturned is still 0 (from rounding) then just set it to 1.
                quotaReturned = 1;
            }
        }

        return quotaReturned;
    }



}
