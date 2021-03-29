package com.cloud7831.goaltracker.Objects;

import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.cloud7831.goaltracker.Data.GoalsContract;
import com.cloud7831.goaltracker.HelperClasses.StringHelper;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.room.Entity;
import androidx.room.Ignore;

@Entity(tableName = "task_table")
public class Task extends GoalRefactor{
    private static final String LOGTAG = "TASK CLASS";

    // --------------------------- Behind the Scenes Data -----------------------
    protected int sessionsTally; // Counts how many sessions has been completed for this measuring cycle.
    protected int quotaTally; // Used to keep track of quota the user has completed.
    @Ignore
    protected MeasurementHandler measurementHandler;

    // --------------------------- Overview Data -------------------------------
    protected int intention; // Specifies if the user wants to break or build a habit.
    protected int classification; // Sorts into a focus (I.e Health and Fitness, Career, Japanese)

    // --------------------------- Schedule Data -------------------------------
    protected int deadline; // Some deadlines are user defined, but weekly goals (etc) have an inferred deadline
    protected int sessions; // How many sessions per week or day.

    // --------------------------- Measurement Data -----------------------------
    protected int isMeasurable; // Can be measured using some type of unit
    protected String units; // Can be user defined. Most goals will probably use a combo of minutes/hours
    protected int quota; // How much "work" must be measured in order to set the goal as completed.


    //region CONSTRUCTORS AND BUILDERS

    // Default constructor
    public Task(){
    }

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

    public Task(int isHidden, int sessionsTally, int quotaTally){
        // This constructor is used when converting from one type of goal to another.
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

        Task newTask = new Task(title, userPriority, isPinned, intention, classification,
                                isMeasurable, units, quota,
                                duration, scheduledTime, deadline, sessions,
                                isHidden, sessionsTally, quotaTally);

        return newTask;
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

    // TODO: should this call the callback to check if the items are the same?
    protected void onSwipeRight(){
        updateQuotaTallyOnSwipe();
        // updating the sessionsTally must be done after updating quotaTally.
        smartIncreaseSessionsTally();
        setIsHidden(0);
        recalculateComplexPriority();
    }

    private void updateQuotaTallyOnSwipe(){
        // First thing that needs to be determined is if the Goal has been completed.
        // And update the quota.
        if(getIsMeasurable() == 1){
            // The goal has a quota

            // Increase the quotaTally by the amount in the slider.
            // TODO: change how the goal calculates how much quota has been completed.
            setQuotaTally(measurementHandler.getQuotaInSlider() + quotaTally);
        }
        else{
            // The goal isn't measurable, so it's simply a yes/no
            setQuotaTally(quotaTally + 1);
        }
    }

    public void nightlyUpdate(){
        // Called when the database updates every night
        // Needs to unhide if the task was hidden and if it's still not completed.

        if(quotaTally <= quota){
            // The goal still hasn't been completed, so unhide it!
            setIsHidden(0);
        }
    }

    public boolean hasGoalChanged(@NonNull Goal newGoal){

        return getIsHidden() == newGoal.getIsHidden() &&
                getSessionsTally() == newGoal.getSessionsTally() &&
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

    public void setMeasurementView(View measurementHolderView, SeekBar measureSliderView, TextView increaseButton, TextView decreaseButton, TextView quotaTextView){
        setMeasurementHandler(measureSliderView, quotaTextView);

        if(isMeasurable == 0){
            // The task doesn't need a measurementHandler view, so just hide the whole thing.
            measurementHolderView.setVisibility(View.GONE);
            measurementHandler.setIsHidden(true);
            return;
        }

        measurementHandler.setIsHidden(false);
        measurementHolderView.setVisibility(View.VISIBLE);

        final MeasurementHandler MEASURE_FINAL = measurementHandler;
        if(MEASURE_FINAL == null){
            // Something went wrong and the measurement handler couldn't be set.
            Log.e(LOGTAG, "MEASURE_FINAL is null.");
            return;
        }

        // Slider increase and decrease buttons
        increaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MEASURE_FINAL.increaseScaling();
                MEASURE_FINAL.todaysQuotaToString();
            }
        });
        decreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MEASURE_FINAL.decreaseScaling();
                MEASURE_FINAL.todaysQuotaToString();
            }
        });

        // Set the seekbar listener so it updates when you slide it.
        measureSliderView.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Update the quota based on the position of the slider

                // Set the quota tally so we know how much quota to record when the goal is swiped.
                MEASURE_FINAL.updateQuotaTally(progress); // TODO: this will have to be changed now that QuotaTally is completely different and not all goals have a quotaTally.
                // Update the text at the bottom of the goal
                MEASURE_FINAL.todaysQuotaToString();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
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


    protected void smartIncreaseSessionsTally(){
        // SessionsTally can not be equal to the number of sessions if the goal is not finished.

        boolean goalCompleted = false;
        if(quotaTally >= quota){
            goalCompleted = true;
        }

        if((sessionsTally + 1 >= sessions)&&(!goalCompleted)){
            // Only increase sessionsTally if the goal quota has been met.
            // Otherwise, keep the sessions tally at sessions -1.
            return;
        }
        sessionsTally += 1;
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
    }

    public void setMeasurementHandler(SeekBar slider, TextView quotaText){
        // TODO: measurement handlers take in a goal atm, but need to take in a Task.
        measurementHandler = new MeasurementHandler(this, slider, quotaText);
        Log.i(LOGTAG, "Measurement Handler Set for: " + title);
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
            Log.e(LOGTAG, "classification was set to an invalid value.");
        }
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

    //endregion GETTER FUNCTIONS -----------------------------------------------------------------

    @Override
    public String toString(){

        return super.toString() +
                "\nClassification: " + classification +
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
