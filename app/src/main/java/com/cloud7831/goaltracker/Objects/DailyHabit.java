package com.cloud7831.goaltracker.Objects;

import android.util.Log;

public class DailyHabit extends Habit{

    public Goal editUserTaskSettings(String title, int userPriority, int isPinned, int intention, int classification,
                                     int isMeasurable, String units, int quota,
                                     int duration, int scheduledTime, int deadline, int sessions){
        // This function is called when the user wants to edit the settings of a goal in
        // GoalEditorActivity. If the goal goes from being weekly to monthly, etc, then calculations
        // must be done to preserve data integrity.

        if(this.frequency != frequency){
            // Daily -> Weekly
            if(this.frequency == GoalEntry.DAILYGOAL && frequency == GoalEntry.WEEKLYGOAL){
                streak = streak / 7; // integer division is fine, I don't mind if it rounds down.
                // Make sure there isn't a junk value in quotaWeek
                quotaWeek = 0;
            }
            // Daily -> Monthly
            else if(this.frequency == GoalEntry.DAILYGOAL && frequency == GoalEntry.MONTHLYGOAL){
                streak = streak / 30; // integer division is fine, I don't mind if it rounds down.
                // Make sure there isn't a junk value in quotaWeek
                quotaWeek = 0;
                quotaMonth = 0;
            }
            // Weekly -> Daily
            else if(this.frequency == GoalEntry.WEEKLYGOAL && frequency == GoalEntry.DAILYGOAL){
                streak = streak * 7;
                // quotaWeek is no longer needed.
                quotaWeek = 0;
            }
            // Weekly -> Monthly
            else if(this.frequency == GoalEntry.WEEKLYGOAL && frequency == GoalEntry.MONTHLYGOAL){
                streak = streak /4; // Let's just say that 4 weeks == 1 month
                // Make sure there isn't a junk value in quotaMonth
                quotaMonth = 0;
            }
            // Monthly -> Daily
            else if(this.frequency == GoalEntry.MONTHLYGOAL && frequency == GoalEntry.DAILYGOAL){
                streak = streak * 30;
                // quotaWeek and quotaMonth are no longer needed.
                quotaWeek = 0;
                quotaMonth = 0;
            }
            // Monthly -> Weekly
            else if(this.frequency == GoalEntry.MONTHLYGOAL && frequency == GoalEntry.WEEKLYGOAL){
                streak = streak * 4; // Let's just say there are 4 weeks in a month.
                // quotaMonth is no longer needed.
                quotaMonth = 0;
            }
        }

        this.title = title;
        this.classification = classification;
        this.intention = intention;
        this.userPriority = userPriority;
        this.isPinned = isPinned;
        this.isMeasurable = isMeasurable;
        this.units = units;
        this.quota = quota;
        this.frequency = frequency;
        this.deadline = deadline;
        this.duration = duration;
        this.scheduledTime = scheduledTime;
        this.sessions = sessions;

        recalculateComplexPriority();

        return this;
    }


    public void setQuotaToday(int q){
        if(q < 0){
            Log.e(LOGTAG, "quotaToday can't be set to something negative.");
        }
        quotaToday = q;
    }

    public void setQuotaWeek(int q){
        if(quotaWeek < 0){
            Log.e(LOGTAG, "quotaWeek can't be set negative.");
        }
        quotaWeek = q;
    }

    public void setQuotaMonth(int q){
        if(quotaMonth < 0){
            Log.e(LOGTAG, "quotaMonth can't be set negative.");
        }
        quotaMonth = q;
    }

    @Override
    protected void smartIncreaseSessionsTally() {

        // SessionsTally can not be equal to the number of sessions if the goal is not finished.

        boolean goalCompleted = false;
        if (frequency == GoalEntry.DAILYGOAL) {
            if (quotaToday >= quota) {
                goalCompleted = true;
            }
        }
        if (frequency == GoalEntry.WEEKLYGOAL) {
            if (quotaToday + quotaWeek >= quota) {
                goalCompleted = true;
            }
        }
        if (frequency == GoalEntry.MONTHLYGOAL) {
            if (quotaToday + quotaWeek + quotaMonth >= quota) {
                goalCompleted = true;
            }
        }

        if ((sessionsTally + 1 >= sessions) && (!goalCompleted)) {
            // Only increase sessionsTally if the goal quota has been met.
            // Otherwise, keep the sessions tally at sessions -1.
            return;
        }
        sessionsTally += 1;
    }
}
