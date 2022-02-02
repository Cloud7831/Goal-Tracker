package com.cloud7831.goaltracker.Objects.Goals;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.cloud7831.goaltracker.Data.GoalDao;
import com.cloud7831.goaltracker.Data.GoalsContract;
import com.cloud7831.goaltracker.HelperClasses.StringHelper;
import com.cloud7831.goaltracker.Objects.MeasurementHandler;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;

/**
 * Tasks are Goals that have a defined objective. Tasks can be Yes/No such as "book a doctor's
 * appointment" or "clean the bathroom". Tasks can also measure the amount of progress until
 * the task is finished. For example "drink 2L of water" or "Save $5,000". Depending on the
 * difficulty of the task, it can be broken down into sessions. Once a task is complete, it
 * is hidden, but not deleted so that it may be reused in the future.
 */

@Entity(tableName = "task_table")
public class Task extends Goal {
    @Ignore
    private static final String LOGTAG = "TASK CLASS";

    // --------------------------- Behind the Scenes Data -----------------------
    protected int sessionsTally; // Counts how many sessions has been completed for this measuring cycle.
    protected int quotaTally; // Used to keep track of quota the user has completed.
    @Ignore
    protected int quotaInSlider = 0;

    // --------------------------- Overview Data -------------------------------
    protected int intention; // Specifies if the user wants to break or build a habit.
    protected int classification; // Sorts into a focus (I.e Health and Fitness, Career, Japanese)

    // --------------------------- Schedule Data -------------------------------
    protected int deadline; // Some deadlines are user defined, but weekly goals (etc) have an inferred deadline
    protected int sessions; // How many sessions per week or day. Quota is broken up accordingly

    // --------------------------- Measurement Data -----------------------------
    protected int isMeasurable; // Can be measured using some type of unit
    protected String units; // Can be user defined. Most goals will probably use a combo of minutes/hours
    protected int quota; // How much "work" must be measured in order to set the goal as completed.

    //region CONSTRUCTORS AND BUILDERS

    // This is the constructor used by the Room database.
    public Task(){
        recalculateComplexPriority();
    }

    @Ignore
    public Task(String title, int userPriority, int isPinned, int intention, int classification,
                int isMeasurable, String units, int quota,
                int duration, int scheduledTime, int deadline, int sessions,
                int isHidden, int sessionsTally, int quotaTally) {

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
        recalculateComplexPriority();
    }

    @Ignore
    public Task(int isHidden, int sessionsTally, int quotaTally){
        // This constructor is used when converting from one type of goal to another.
        // This function is first called to create a new Goal with the hidden parameters set. Next
        // the variables the user has access to will be set.
        this.isHidden = isHidden;
        this.sessionsTally = sessionsTally;
        this.quotaTally = quotaTally;
    }

    public static Task buildNewTask(String title, int userPriority,  int isPinned, int intention, int classification,
                                        int isMeasurable, String units, int quota,
                                        int duration, int scheduledTime, int deadline, int sessions){
        // Users can define their own goals

        // The following hidden variables are set for them.
        int isHidden = 0;
        int sessionsTally = 0;
        int quotaTally = 0;

        return new Task(title, userPriority, isPinned, intention, classification,
                                isMeasurable, units, quota,
                                duration, scheduledTime, deadline, sessions,
                                isHidden, sessionsTally, quotaTally);
    }

    public void editUserSettings(String title, int userPriority, int isPinned, int intention, int classification,
                                 int isMeasurable, String units, int quota,
                                 int duration, int scheduledTime, int deadline, int sessions){
        // This function is called when the user wants to edit the settings of a goal in
        // GoalEditorActivity.
        setTitle(title);
        setUserPriority(userPriority);
        setIsPinned(isPinned);
        setIntention(intention);
        setClassification(classification);

        setIsMeasurable(isMeasurable);
        setUnits(units);
        setQuota(quota);

        setDuration(duration);
        setScheduledTime(scheduledTime);
        setDeadline(deadline);
        setSessions(sessions);

        recalculateComplexPriority();
    }
    //endregion CONSTRUCTORS AND BUILDERS

    @Override
    protected void onSwipeRight(){
        updateQuotaTallyOnSwipe();
        // updating the sessionsTally must be done after updating quotaTally.
        smartIncreaseSessionsTally();
        recalculateComplexPriority();
    }

    protected void updateQuotaTallyOnSwipe(){
        // First thing that needs to be determined is if the Goal has been completed.
        // And update the quota.
        if(getIsMeasurable() == 1){
            // The goal has a quota

            // Increase the quotaTally by the amount in the slider.
            setQuotaTally(getQuotaInSlider() + getQuotaTally());
            updateGoalVisibility();
        }
        else{
            // The goal isn't measurable, so it's simply a yes/no
            setQuotaTally(getQuotaTally() + 1);
            updateGoalVisibility();
        }
    }

    protected void updateGoalVisibility(){
        // TODO: only hide the goal if the quotaGoal for today has been met (daily, weekly, monthly only).
        setIsHidden(1);
    }

    protected void smartIncreaseSessionsTally(){
        // SessionsTally can not be equal to the number of sessions if the goal is not finished.

        boolean goalCompleted = false;
        if(quotaTally >= quota){
            goalCompleted = true;
        }

        if((sessionsTally + 1 >= sessions)&&(!goalCompleted)){
            // Only increase sessionsTally if the goal quota has been met.
            // Otherwise, keep the sessions tally at sessions - 1.
            return;
        }
        sessionsTally += 1;
    }

    public void nightlyUpdate(){
        // Called when the database updates every night
        // Needs to unhide if the task was hidden and if it's still not completed.

        if(quotaTally <= quota){
            // The goal still hasn't been completed, so unhide it!
            setIsHidden(0);
        }
    }

    @Override
    public int getType(){
        return GoalsContract.GoalEntry.TYPE_TASK;
    }

    @Override
    public void insertGoalInDB(GoalDao dao){
        dao.insert(this);
    }

    @Override
    public void updateGoalInDB(GoalDao dao){
        dao.update(this);
    }

    @Override
    public void deleteGoalInDB(GoalDao dao){
        dao.delete(this);
    }

    @Override
    public boolean equals(@NonNull Goal newGoal){

        if(!(newGoal instanceof Task)){
            return false;// obviously the goal changed, because the new goal isn't even a task.
        }
        else{
            Task task = (Task)newGoal;
            //TODO: I want to know if there's a better way. There are so many variables here. What
            // if I forget a variable? What if I add a new variable and don't update this list.
            // what if I change or delete a variable... It seems like there should be a way to check
            // all of the variables in this class so that I don't miss anything, or it at least will
            // throw me an error.
            return getIsHidden()        == task.getIsHidden()       &&
                    getComplexPriority()== task.getComplexPriority()&&
                    getSessionsTally()  == task.getSessionsTally()  &&
                    getQuotaTally()     == task.getQuotaTally()     &&

                    getTitle().equals(task.getTitle())              &&
                    getUserPriority()   == task.getUserPriority()   &&
                    getIsPinned()       == task.getIsPinned()       &&
                    getIntention()      == task.getIntention()      &&
                    getClassification() == task.getClassification() &&

                    getDuration()       == task.getDuration()       &&
                    getScheduledTime()  == task.getScheduledTime()  &&
                    getDeadline()       == task.getDeadline()       &&
                    getSessions()       == task.getSessions()       &&

                    getIsMeasurable()   == task.getIsMeasurable()   &&
                    getUnits().equals(task.getUnits())              &&
                    getQuota()          == task.getQuota()          &&

                    getQuotaInSlider()  == task.getQuotaInSlider();
        }
    }

    public void setTitleTextView(@NonNull TextView t){
        t.setVisibility(View.VISIBLE); // Might be redundant, because nothing hides it as of yet.
        t.setText(title);
    }

    public void setStreakTextView(@NonNull TextView t){
        // Tasks don't use a streak, so hide the view from the user.
        t.setVisibility(View.GONE);
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
         * Completed 1/3 times
         * Completed 25/220 pages
         * Completed 60/180 minutes
        */
        String output = "Completed " + StringHelper.getStringQuotaProgressAndUnits(quotaTally, quota, units);
        t.setText(output);
    }

    public void setMeasurementView(@NonNull View measurementHolderView, MeasurementHandler handler){
        if(isMeasurable == 0){
            // The task doesn't need a measurementHandler view, so just hide the whole thing.
            measurementHolderView.setVisibility(View.GONE);
        }
        else{
            handler.setTask(this);
            measurementHolderView.setVisibility(View.VISIBLE);
        }
    }

    public DailyHabit convertToDailyHabit(){
        // The hidden values need to be copied over to the DailyHabit.
        // Everything else is set by the user.

        return new DailyHabit(isHidden, sessionsTally, quotaTally, 0); // Can't determine the streak, so just start fresh.
    }

    public WeeklyHabit convertToWeeklyHabit(){
        // The hidden values need to be copied over to the WeeklyHabit.
        // Everything else is set by the user.

        // The quotaToday isn't easy to figure out, so just set it to 0.
        // TODO: when I have logging for when quota has been completed, it will be
        //  possible to figure out when the quota was completed
        // Can't determine the streak, so just start fresh.
        return new WeeklyHabit(isHidden, sessionsTally, quotaTally, 0, 0);
    }

    public MonthlyHabit convertToMonthlyHabit(){
        // The hidden values need to be copied over to the MonthlyHabit.
        // Everything else is set by the user.

        // The quotaToday isn't easy to figure out, so just set it to 0.
        // TODO: when I have logging for when quota has been completed, it will be
        //  possible to figure out when the quota was completed
        // The quotaWeek isn't easy to figure out, so just use 0.
        // Can't determine the streak, so just start fresh.
        return new MonthlyHabit(isHidden, sessionsTally, quotaTally, 0, 0, 0);
    }

    public Task convertToTask(){
        // Nothing needs to be done, it's already a task.
        return this;
    }

    //region SETTER FUNCTIONS

    public void setSessionsTally(int s){
        if(s < 0){
            Log.e(LOGTAG, "SessionsTally was set to a negative number.");
        }
        sessionsTally = s;
    }

    public void resetSessionsTally(){
        sessionsTally = 0;
    }

    public void setQuotaTally(int q){
        if(q < 0){
            Log.e(LOGTAG, "QuotaTally was set to a negative value.");
        }
        quotaTally = q;
//        Log.i(LOGTAG, "quota tally for " + title + " has been set to " + q);
    }

    public void setIsMeasurable(int m){
        isMeasurable = m;
    }

    public void setUnits(String units){
        if(units == "" || units == null){
            Log.e(LOGTAG, "Units were set to an empty string or null.");
        }
        this.units = units;
    }

    public void setQuota(int q){
        if(q <= 0){
            // Every task has to have a non zero quota so that the app knows how many times
            // it must be completed.
            Log.e(LOGTAG, "Quota was set to a negative number or zero");
        }
        quota = q;
    }

    public void setDeadline(int d){
        if(d < 0){
            Log.e(LOGTAG, "Deadline was set to a negative value");
        }
        deadline = d;
    }

    public void setSessions(int i){
        if(i <= 0){
            Log.e(LOGTAG, "error, sessions shouldn't be set to less than 1.");
        }
        sessions = i;
    }

    @Override
    public void setTitle(String t){
        if(t == "" || t == null){
            title = "Unnamed Task";
        }
        else{
            title = t;
        }
    }

    public void setIntention(int i){
        if(GoalsContract.GoalEntry.isValidIntention(i)){
            intention = i;
        }
        else{
            Log.e(LOGTAG, "Intention was set to an invalid value.");
        }
    }

    public void setClassification(int c){
        if(GoalsContract.GoalEntry.isValidClassification(c)){
            classification = c;
        }
        else{
//            Log.e(LOGTAG, "classification was set to an invalid value.");
        }
    }

    public void setQuotaInSlider(int q){
        quotaInSlider = q;
    }

    //endregion SETTER FUNCTIONS

    //region GETTER FUNCTIONS -----------------------------------------------------------------

    public int getIntention() {
        return intention;
    }

    public int getClassification() {
        return classification;
    }

    public int getDeadline() {
        return deadline;
    }

    public int getSessions() {
        return sessions;
    }

    @Override
    public int getIsMeasurable() {
        return isMeasurable;
    }

    public String getUnits() {
        return units;
    }

    public int getQuota() {
        return quota;
    }

    public int getSessionsTally() {
        return sessionsTally;
    }

    public int getQuotaTally() {
        return quotaTally;
    }

    // Used for the calcMaxQuota function.
    // For tasks I don't really care how much quota was done today, just base the
    // size of the max Quota off how many sessions are left.
    // If this becomes a problem for other functions, it's possible to add a quotaToday
    // variable for Tasks (which will be redundant in DailyHabits).
    public int getQuotaCompletedToday(){
        return 0;
    }

    public int getQuotaInSlider(){
        return quotaInSlider;
    }

    //endregion GETTER FUNCTIONS -----------------------------------------------------------------

    @Override
    public String toString(){

        return super.toString() +
                "\nClassification: " + classification +
                "\nGoal Type: " + getType() +
                "\nIntention: " + intention +
                "\nIs Measurable: " + isMeasurable +
                "\nUnits: " + units +
                "\nQuota: " + quota +
                "\nDeadline: " + deadline +
                "\nScheduled Time: " + scheduledTime +
                "\nSessions: " + sessions +
                "\nSessions Tally: " + sessionsTally +
                "\nQuota Tally: " + quotaTally;
    }
}
