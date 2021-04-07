package com.cloud7831.goaltracker.Objects;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.cloud7831.goaltracker.HelperClasses.StringHelper;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.room.Entity;

@Entity(tableName = "monthly_habit_table")
public class MonthlyHabit extends Habit{
    private static final String LOGTAG = "MONTHLY_HABIT CLASS";

    private int quotaToday; // The running total of how much of the quota they've completed today.
    private int quotaWeek; // The running total of how much of the quota the user completed this week.

    // Default constructor
    public MonthlyHabit(){
    }

    public MonthlyHabit(String title, int userPriority, int isPinned, int intention, int classification,
                       int isMeasurable, String units, int quota,
                       int duration, int scheduledTime, int deadline, int sessions,
                       int isHidden, int sessionsTally, int quotaTally, int quotaToday, int quotaWeek,
                       int streak) {

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
        setQuotaWeek(quotaWeek);
        setStreak(streak);
        recalculateComplexPriority();
    }

    public MonthlyHabit(int isHidden, int sessionsTally,
                        int quotaTally, int quotaToday, int quotaWeek,
                        int streak){
        // Used to create a new MonthlyHabit with only the hidden variables set.
        // This function is just used when converting to a MonthlyHabit.

        setIsHidden(isHidden);
        setSessionsTally(sessionsTally);
        setQuotaTally(quotaTally);
        setQuotaToday(quotaToday);
        setQuotaWeek(quotaWeek);
        setStreak(streak);
    }

    public static MonthlyHabit buildNewMonthlyHabit(String title, int userPriority,  int isPinned, int intention, int classification,
                                                  int isMeasurable, String units, int quota,
                                                  int duration, int scheduledTime, int deadline, int sessions){
        // Users can define their own goals

        // The following hidden variables are set for them.
        int isHidden = 0;
        int sessionsTally = 0;
        int quotaTally = 0;
        int quotaToday = 0;
        int quotaWeek = 0;
        int streak = 0;

        return new MonthlyHabit(title, userPriority, isPinned, intention, classification,
                isMeasurable, units, quota,
                duration, scheduledTime, deadline, sessions,
                isHidden, sessionsTally, quotaTally, quotaToday, quotaWeek, streak);
    }

    //region CONVERSION FUNCTIONS ----------------------------------------------------------------

    public DailyHabit convertToDailyHabit(){
        // Convert the streak from a monthly streak to a daily streak
        int streak = getStreak() * 30; // 1 monthly streak = 30 days.

        // GetQuotaToday() is used for the DaillyHabit's QuotaTally, because that's just the DailyHabit
        // Equivalent. The MonthlyHabit's QuotaTally and QuotaWeek's data is lost.
        return new DailyHabit(getIsHidden(), getSessionsTally(), getQuotaToday(), streak);
        // TODO: delete the old DailyHabit. I'm not entirely sure the best way to do that, because
        // TODO: it might not be possible in this function of DailyHabit...
    }

    public WeeklyHabit convertToWeeklyHabit(){
        // Convert the streak from a monthly streak to a weekly streak
        int streak = getStreak() / 4; // integer division is fine, I don't mind if it rounds down.

        return new WeeklyHabit(getIsHidden(), getSessionsTally(), getQuotaWeek(), getQuotaToday(), streak);
        // TODO: delete the old DailyHabit. I'm not entirely sure the best way to do that, because
        // TODO: it might not be possible in this function of DailyHabit...
    }

    public MonthlyHabit convertToMonthlyHabit(){
        // No conversion necessary.
        return this;
    }

    public Task convertToTask(){
        // Just need to copy over the hidden variables. Everything else is set by the user later.
        return new Task(getIsHidden(), getSessionsTally(), getQuotaTally() + getQuotaWeek() +getQuotaToday());
    }

    //endregion CONVERSION FUNCTIONS ----------------------------------------------------------------

    @Override
    protected void onSwipeRight(){
        // TODO: make the swipe record the quota correctly.
    }

    public void nightlyUpdate(){
        // Called when the database updates every night

        // Move all the quotaToday into quotaWeek because quotaWeek represents how much quota was
        // completed this week.
        setQuotaWeek(getQuotaWeek() + getQuotaToday());
        setQuotaToday(0); // Preparing for the new day, so reset the value.

        if(getQuotaTally() + getQuotaWeek() < getQuota()){
            // Goal hasn't been completed.
            // Unhide the goal so that the user can continue working on completing it.
            // TODO: the goal should not unhide when the goal is paused
            setIsHidden(0);
        }

        // If it's the end of the week, QuotaWeek has to be dumped into QuotaTally
        // Remember that the goals update between 3-5am so Monday is the end of the week.
        // TODO: make the last day of the week a variable so that the user can specify when their
        //  week ends.
        if(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY){
            setQuotaTally(getQuotaTally() + getQuotaWeek());
            setQuotaWeek(0);
        }

        // End of the month, so check if the goal has been completed.
        if(Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == 1){
            // TODO: the goal should not unhide when the goal is paused
            setIsHidden(0); // New month, so the goal needs to be shown regardless of completion.

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

    @Override
    public boolean hasGoalChanged(@NonNull GoalRefactor newGoal){

        if(!(newGoal instanceof MonthlyHabit)){
            return true;// obviously the goal changed, because the new goal isn't even a MonhlyHabit.
        }

        return getIsHidden() == newGoal.getIsHidden() &&
                getSessionsTally() == ((MonthlyHabit)newGoal).getSessionsTally() &&
                getStreak() == ((MonthlyHabit)newGoal).getStreak() &&
                getComplexPriority() == newGoal.getComplexPriority() &&
                getIsHidden() == newGoal.getIsHidden() &&
                getQuotaTally() == ((MonthlyHabit)newGoal).getQuotaTally() &&
                getQuotaToday() == ((MonthlyHabit)newGoal).getQuotaToday() &&
                getQuotaWeek() == ((MonthlyHabit)newGoal).getQuotaWeek() &&

                getTitle().equals(newGoal.getTitle()) &&
                getIntention() == ((MonthlyHabit)newGoal).getIntention() &&
                getUserPriority() == newGoal.getUserPriority() &&
                getClassification() == ((MonthlyHabit)newGoal).getClassification() &&
                getIsPinned() == newGoal.getIsPinned() &&

                getDeadline() == ((MonthlyHabit)newGoal).getDeadline() &&
                getDuration() == newGoal.getDuration() &&
                getSessions() == ((MonthlyHabit)newGoal).getSessions() &&
                getScheduledTime() == newGoal.getScheduledTime() &&

                getIsMeasurable() == ((MonthlyHabit)newGoal).getIsMeasurable() &&
                getUnits().equals(((MonthlyHabit)newGoal).getUnits()) &&
                getQuota() == ((MonthlyHabit)newGoal).getQuota();
    }

    public void setStreakTextView(@NonNull TextView t){
        t.setVisibility(View.VISIBLE);
        int streak = getStreak();
        if(streak == 1){
            t.setText("1 month");
        }
        else{
            t.setText(streak + " months");
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
         * Completed 1/3 times this month
         * Completed 25/220 pages this month
         * Completed 60/180 minutes this month
         */
        String output = "Completed " + StringHelper.getStringQuotaProgressAndUnits(quotaTally, quota, units);

        // This is the only thing that's different from the Task's method.
        output += " this month";

        t.setText(output);
    }

    @Override
    protected void smartIncreaseSessionsTally() {

        // SessionsTally can not be equal to the number of sessions if the goal is not finished.

        boolean goalCompleted = false;
        // quotaTally = the quota completed on the days of the month before this week, but not
        // including this week.
        if (quotaTally + quotaWeek +quotaToday >= quota) {
            goalCompleted = true;
        }

        if ((sessionsTally + 1 >= sessions) && (!goalCompleted)) {
            // Only increase sessionsTally if the goal quota has been met.
            // Otherwise, keep the sessions tally at sessions -1.
            return;
        }
        sessionsTally += 1;
    }

    private int getQuotaToday(){
        return quotaToday;
    }

    private void setQuotaToday(int q){
        if(q < 0){
            Log.e(LOGTAG, "quotaToday can't be set to something negative.");
        }
        quotaToday = q;
    }

    public int getQuotaWeek(){
        return quotaWeek;
    }

    private void setQuotaWeek(int q){
        if(quotaWeek < 0){
            Log.e(LOGTAG, "quotaWeek can't be set negative.");
        }
        quotaWeek = q;
    }

    @Override
    public int getQuotaCompletedToday(){
        return quotaToday;
    }

}
