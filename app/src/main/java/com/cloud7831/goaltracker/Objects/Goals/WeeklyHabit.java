package com.cloud7831.goaltracker.Objects.Goals;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.cloud7831.goaltracker.Data.GoalDao;
import com.cloud7831.goaltracker.Data.GoalsContract;
import com.cloud7831.goaltracker.HelperClasses.StringHelper;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;

@Entity(tableName = "weekly_habit_table")
public class WeeklyHabit extends Habit {
    private static final String LOGTAG = "WEEKLY_HABIT CLASS";

    private int quotaToday; // The running total of how much of the quota they've completed today.

    // This is the constructor used by the Room database.
    public WeeklyHabit(){
        recalculateComplexPriority();
    }

    @Ignore
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

    @Ignore
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

    @Override
    public int getType(){
        return GoalsContract.GoalEntry.TYPE_WEEKLYGOAL;
    }

    @Override
    public void insertGoalInDB(GoalDao dao){
        dao.insert((WeeklyHabit)this);
    }

    @Override
    public void updateGoalInDB(GoalDao dao){
        dao.update((WeeklyHabit)this);
    }

    @Override
    public void deleteGoalInDB(GoalDao dao){
        dao.delete((WeeklyHabit) this);
    }

    //region CONVERSION FUNCTIONS ----------------------------------------------------------------

    public DailyHabit convertToDailyHabit(){
        // Convert the streak from a weekly streak to a daily streak
        int streak = getStreak() * 7;

        // GetQuotaToday() is used for the DaillyHabit's QuotaTally, because that's just the DailyHabit
        // Equivalent. The WeeklyHabit's QuotaTally's data is lost.
        return new DailyHabit(getIsHidden(), getSessionsTally(), getQuotaToday(), streak);
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
    }

    public Task convertToTask(){
        // Just need to copy over the hidden variables. Everything else is set by the user later.
        return new Task(getIsHidden(), getSessionsTally(), getQuotaTally() + getQuotaToday());
    }

    //endregion CONVERSION FUNCTIONS ----------------------------------------------------------------

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
        // TODO: make the last day of the week a variable so that the user can specify when their
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

    @Override
    public boolean equals(@NonNull Goal newGoal){

        if(!(newGoal instanceof WeeklyHabit)){
            Log.i(LOGTAG, "The goal is not a weekly habit: " + newGoal.getTitle());
            return false;// obviously the goal changed, because the new goal isn't even a WeeklyHabit.
        }
        else {
            WeeklyHabit habit = (WeeklyHabit) newGoal;

            if(!(getIsHidden() == habit.getIsHidden())){
                Log.i(LOGTAG, "hidden");
                return false;
            }
            else if(!(getComplexPriority()== habit.getComplexPriority())){
                Log.i(LOGTAG, "CP");
                return false;
            }
            else if(!(getSessionsTally()  == habit.getSessionsTally())){
                Log.i(LOGTAG, "ST");
                return false;
            }
            else if(!(getQuotaTally()     == habit.getQuotaTally())){
                Log.i(LOGTAG, "QTall");
                return false;
            }
            else if(!(getQuotaToday()     == habit.getQuotaToday())){
                Log.i(LOGTAG, "QToday");
                return false;
            }
            else if(!(getTitle().equals(habit.getTitle()))){
                Log.i(LOGTAG, "title");
                return false;
            }
            else if(!(getUserPriority()   == habit.getUserPriority())){
                Log.i(LOGTAG, "UP");
                return false;
            }
            else if(!(getIsPinned()       == habit.getIsPinned())){
                Log.i(LOGTAG, "IP");
                return false;
            }
            else if(!(getIntention()      == habit.getIntention())){
                Log.i(LOGTAG, "int");
                return false;
            }
            else if(!(getClassification() == habit.getClassification())){
                Log.i(LOGTAG, "class");
                return false;
            }
            else if(!(getDuration()       == habit.getDuration())){
                Log.i(LOGTAG, "dur");
                return false;
            }
            else if(!(getScheduledTime()  == habit.getScheduledTime() )){
                Log.i(LOGTAG, "SchedT");
                return false;
            }
            else if(!(getDeadline()       == habit.getDeadline()     )){
                Log.i(LOGTAG, "dead");
                return false;
            }
            else if(!(getSessions()       == habit.getSessions()      )){
                Log.i(LOGTAG, "sess");
                return false;
            }
            else if(!(getIsMeasurable()   == habit.getIsMeasurable() )){
                Log.i(LOGTAG, "isMeas");
                return false;
            }
            else if(!(getUnits().equals(habit.getUnits()))){
                Log.i(LOGTAG, "units");
                return false;
            }
            else if(!(getQuotaInSlider()  == habit.getQuotaInSlider())){
                Log.i(LOGTAG, "slider");
                return false;
            }
            else if(!(getQuota()          == habit.getQuota()        )){
                Log.i(LOGTAG, "quota");
                return false;
            }else{
                return true;
            }


//            return  getIsHidden()       == habit.getIsHidden()      &&
//                    getComplexPriority()== habit.getComplexPriority()&&
//                    getSessionsTally()  == habit.getSessionsTally() &&
//                    getQuotaTally()     == habit.getQuotaTally()    &&
//                    getQuotaToday()     == habit.getQuotaToday()    &&
//
//                    getTitle().equals(habit.getTitle())             &&
//                    getUserPriority()   == habit.getUserPriority()  &&
//                    getIsPinned()       == habit.getIsPinned()      &&
//                    getIntention()      == habit.getIntention()     &&
//                    getClassification() == habit.getClassification()&&
//
//                    getDuration()       == habit.getDuration()      &&
//                    getScheduledTime()  == habit.getScheduledTime() &&
//                    getDeadline()       == habit.getDeadline()      &&
//                    getSessions()       == habit.getSessions()      &&
//
//                    getIsMeasurable()   == habit.getIsMeasurable()  &&
//                    getUnits().equals(habit.getUnits())             &&
//                    getQuota()          == habit.getQuota()         &&
//
//                    getQuotaInSlider()  == habit.getQuotaInSlider();
        }
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

    public void setQuotaToday(int q){
        if(q < 0){
            Log.e(LOGTAG, "quotaToday can't be set to something negative.");
        }
        quotaToday = q;
    }

    public int getQuotaToday(){
        return quotaToday;
    }

    @Override
    public int getQuotaCompletedToday(){
        return quotaToday;
    }
}
