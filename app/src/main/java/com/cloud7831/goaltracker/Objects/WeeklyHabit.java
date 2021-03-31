package com.cloud7831.goaltracker.Objects;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.cloud7831.goaltracker.HelperClasses.StringHelper;

import java.util.Calendar;

import androidx.annotation.NonNull;

public class WeeklyHabit extends Habit{
    private static final String LOGTAG = "WEEKLY_HABIT CLASS";

    private int quotaToday; // The running total of how much of the quota they've completed today.

    // Default constructor
    public WeeklyHabit(){
    }

    public WeeklyHabit(String title, int userPriority, int isPinned, int intention, int classification,
                      int isMeasurable, String units, int quota,
                      int duration, int scheduledTime, int deadline, int sessions,
                      int isHidden, int sessionsTally, int quotaTally, int quotaToday, int streak) {

        // Overview
        setTitle(title);
        setUserPriority(userPriority);
        setIsPinned(isPinned);
        setIntention(intention);
        setClassification(classification);

        // Schedule
        setDuration(duration);
        setScheduledTime(scheduledTime);
        setDeadline(deadline);
        setSessions(sessions);


        // Measurement
        setIsMeasurable(isMeasurable);
        setUnits(units);
        setQuota(quota);

        // Behind the Scenes
        setIsHidden(isHidden);
        setSessionsTally(sessionsTally);
        setQuotaTally(quotaTally);
        setQuotaToday(quotaToday);
        setStreak(streak);
        recalculateComplexPriority();
    }

    public WeeklyHabit(int isHidden, int sessionsTally, int quotaTally, int quotaToday, int streak){
        // Used to create a new WeeklyHabit with only the hidden variables set.
        // This function is just used when converting to a WeeklyHabit.

        setIsHidden(isHidden);
        setSessionsTally(sessionsTally);
        setQuotaTally(quotaTally);
        setQuotaToday(quotaToday);
        setStreak(streak);
    }

//    public void editUserSettings(String title, int userPriority, int isPinned, int intention, int classification,
//                                 int isMeasurable, String units, int quota,
//                                 int duration, int scheduledTime, int deadline, int sessions) {
//        // This function is called when the user wants to edit the settings of a goal in
//        // GoalEditorActivity.
//        // Hidden variables don't need to be adjusted at all.
//        setTitle(title);
//        setUserPriority(userPriority);
//        setIsPinned(isPinned);
//        setIntention(intention);
//        setClassification(classification);
//
//        setIsMeasurable(isMeasurable);
//        setUnits(units);
//        setQuota(quota);
//
//        setDuration(duration);
//        setScheduledTime(scheduledTime);
//        setDeadline(deadline);
//        setSessions(sessions);
//
//        recalculateComplexPriority();
//    }

    public static WeeklyHabit buildNewWeeklyHabit(String title, int userPriority,  int isPinned, int intention, int classification,
                                                int isMeasurable, String units, int quota,
                                                int duration, int scheduledTime, int deadline, int sessions){
        // Users can define their own goals

        // The following hidden variables are set for them.
        int isHidden = 0;
        int sessionsTally = 0;
        int quotaTally = 0;
        int quotaToday = 0;
        int streak = 0;

        return new WeeklyHabit(title, userPriority, isPinned, intention, classification,
                isMeasurable, units, quota,
                duration, scheduledTime, deadline, sessions,
                isHidden, sessionsTally, quotaTally, quotaToday, streak);
    }

    //region CONVERSION FUNCTIONS ----------------------------------------------------------------

    public DailyHabit convertToDailyHabit(){
        // Convert the streak from a weekly streak to a daily streak
        int streak = getStreak() * 7;

        // GetQuotaToday() is used for the DaillyHabit's QuotaTally, because that's just the DailyHabit
        // Equivalent. The WeeklyHabit's QuotaTally's data is lost.
        return new DailyHabit(getIsHidden(), getSessionsTally(), getQuotaToday(), streak);
        // TODO: delete the old DailyHabit. I'm not entirely sure the best way to do that, because
        // TODO: it might not be possible in this function of DailyHabit...
    }

    public WeeklyHabit convertToWeeklyHabit(){
        // No conversion necessary.
        return this;
    }

    public MonthlyHabit convertToMonthlyHabit(){
        // Convert the streak from a weekly streak to a monthly streak
        // TODO: Fix how streaks are stored so that they're stored in days, and then converted to
        //  months when needed for the UI.
        int streak = getStreak() / 4; // integer division is fine, I don't mind if it rounds down.

        // GetQuotaTally() is used for the MonthlyHabit's QuotaWeek, because that's just the MonthlyHabit's
        // Equivalent. The MonthlyHabit's QuotaTally can be set to 0 because that now measures how
        // much in the month (aside from the amount this week).
        return new MonthlyHabit(getIsHidden(), getSessionsTally(), getQuotaToday(), getQuotaTally(), 0, streak);
        // TODO: delete the old DailyHabit. I'm not entirely sure the best way to do that, because
        // TODO: it might not be possible in this function of DailyHabit...
    }

    public Task convertToTask(){
        // Just need to copy over the hidden variables. Everything else is set by the user later.
        return new Task(getIsHidden(), getSessionsTally(), getQuotaTally());
    }

    // TODO: make functions that convert from DailyHabit to WeeklyHabit, etc.
//        if(this.frequency != frequency){
//            // Daily -> Weekly
//            if(this.frequency == GoalEntry.DAILYGOAL && frequency == GoalEntry.WEEKLYGOAL){
//                streak = streak / 7; // integer division is fine, I don't mind if it rounds down.
//                // Make sure there isn't a junk value in quotaWeek
//                quotaWeek = 0;
//            }
//            // Daily -> Monthly
//            else if(this.frequency == GoalEntry.DAILYGOAL && frequency == GoalEntry.MONTHLYGOAL){
//                streak = streak / 30; // integer division is fine, I don't mind if it rounds down.
//                // Make sure there isn't a junk value in quotaWeek
//                quotaWeek = 0;
//                quotaMonth = 0;
//            }
//            // Weekly -> Daily
//            else if(this.frequency == GoalEntry.WEEKLYGOAL && frequency == GoalEntry.DAILYGOAL){
//                streak = streak * 7;
//                // quotaWeek is no longer needed.
//                quotaWeek = 0;
//            }
//            // Weekly -> Monthly
//            else if(this.frequency == GoalEntry.WEEKLYGOAL && frequency == GoalEntry.MONTHLYGOAL){
//                streak = streak /4; // Let's just say that 4 weeks == 1 month
//                // Make sure there isn't a junk value in quotaMonth
//                quotaMonth = 0;
//            }
//            // Monthly -> Daily
//            else if(this.frequency == GoalEntry.MONTHLYGOAL && frequency == GoalEntry.DAILYGOAL){
//                streak = streak * 30;
//                // quotaWeek and quotaMonth are no longer needed.
//                quotaWeek = 0;
//                quotaMonth = 0;
//            }
//            // Monthly -> Weekly
//            else if(this.frequency == GoalEntry.MONTHLYGOAL && frequency == GoalEntry.WEEKLYGOAL){
//                streak = streak * 4; // Let's just say there are 4 weeks in a month.
//                // quotaMonth is no longer needed.
//                quotaMonth = 0;
//            }

    //endregion CONVERSION FUNCTIONS ----------------------------------------------------------------

    @Override
    protected void onSwipeRight(){
        // TODO: make the swipe record the quota correctly.
    }

    public void nightlyUpdate(){
        // Called when the database updates every night

        // Move all the quotaToday into quotaTally because quotaTally represents how much quota was
        // completed this week.
        setQuotaTally(getQuotaTally() + getQuotaToday());
        setQuotaToday(0); // Preparing for the new day, so reset the value.

        if(getQuotaTally() < getQuota()){
            // Unhide the goal so that the user can continue working on completing it.
            // TODO: the goal should not unhide when the goal is paused
            setIsHidden(0);
        }

        // Streak updates at the end of the week.
        // Remember that the goals update between 3-5am so Monday = end of the week.
        // TODO: make the last day of the month a variable so that the user can specify when their
        //  week ends.
        if(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY){
            // TODO: the goal should not unhide when the goal is paused
            setIsHidden(0); // New week, so the goal needs to be shown

            // Update streak
            if(getQuotaTally() >= getQuota()){
                incStreak(); // Goal was completed! Yay!
            }
            else{
                resetStreak();
            }
            resetSessionsTally();
            setQuotaTally(0);
        }
    }

    public boolean hasGoalChanged(@NonNull Goal newGoal){
        return getIsHidden() == newGoal.getIsHidden() &&
                getSessionsTally() == newGoal.getSessionsTally() &&
                getQuotaToday() == newGoal.getQuotaToday() &&
                //getQuotaWeek() == newGoal.getQuotaWeek() &&
                //getQuotaMonth() == newGoal.getQuotaMonth() &&
                getStreak() == newGoal.getStreak() &&
                getComplexPriority() == newGoal.getComplexPriority() &&
                getIsHidden() == newGoal.getIsHidden() &&
                getQuotaTally() == newGoal.getQuotaTally() &&

                getTitle().equals(newGoal.getTitle()) &&
                getIntention() == newGoal.getIntention() &&
                getUserPriority() == newGoal.getUserPriority() &&
                getClassification() == newGoal.getClassification() &&
                getIsPinned() == newGoal.getIsPinned() &&

                getDeadline() == newGoal.getDeadline() &&
                getDuration() == newGoal.getDuration() &&
                getSessions() == newGoal.getSessions() &&
                getScheduledTime() == newGoal.getScheduledTime() &&

                getIsMeasurable() == newGoal.getIsMeasurable() &&
                getUnits().equals(newGoal.getUnits()) &&
                getQuota() == newGoal.getQuota();
    }

    public void setStreakTextView(@NonNull TextView t){
        t.setVisibility(View.VISIBLE);
        int streak = getStreak();
        if(streak == 1){
            t.setText("1 week");
        }
        else{
            t.setText(streak + " weeks");
        }
    }

    public void setProgressTextView(@NonNull TextView t){
        // Sets the progress textView to indicate how much of the quota has been completed.

        // Tasks that are not measureable do not need this text view and should hide it.
        if(isMeasurable == 0 && sessions == 1){
            // The goal is not measurable and it just needs to be completed.
            t.setVisibility(View.GONE);
            return;
        }

        // The task now either needs to show how much of the goal has been completed, or how many
        // sessions the user has completed, depending on if it's measurable or not.
        t.setVisibility(View.VISIBLE);

        /** The output string should look like one of the following:
         * Completed 1/3 times this week
         * Completed 25/220 pages this week
         * Completed 60/180 minutes this week
         */
        String output = "Completed " + StringHelper.getStringQuotaProgressAndUnits(quotaTally, quota, units);

        // This is the only thing that's different from the Task's method.
        output += " this week";

        t.setText(output);
    }

    @Override
    protected void smartIncreaseSessionsTally() {

        // SessionsTally can not be equal to the number of sessions if the goal is not finished.

        boolean goalCompleted = false;
        // quotaTally = the quota completed on the days of the week before today, but not
        // including today.
        if (quotaTally + quotaToday >= quota) {
            goalCompleted = true;
        }

        if ((sessionsTally + 1 >= sessions) && (!goalCompleted)) {
            // Only increase sessionsTally if the goal quota has been met.
            // Otherwise, keep the sessions tally at sessions -1.
            return;
        }
        sessionsTally += 1;
    }

    private void setQuotaToday(int q){
        if(q < 0){
            Log.e(LOGTAG, "quotaToday can't be set to something negative.");
        }
        quotaToday = q;
    }

    private int getQuotaToday(){
        return quotaToday;
    }
    
}
