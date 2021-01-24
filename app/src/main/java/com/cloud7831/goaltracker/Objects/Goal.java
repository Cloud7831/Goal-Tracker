package com.cloud7831.goaltracker.Objects;

import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import com.cloud7831.goaltracker.Data.GoalsContract;
import com.cloud7831.goaltracker.Data.GoalsContract.*;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

// TODO: Goal has become a bit of a monster object. At the time of writing this it has over 20 member variables,
// TODO: so maybe it's best to break some of the member variables into their own objects such as
// TODO: a quota object, a schedule object, etc.

@Entity(tableName = "goal_table")
public class Goal {
    private static final String LOGTAG = "GOAL CLASS";

    //region MEMBER VARIABLES
    // --------------------------- Behind the Scenes Data -----------------------
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int isHidden; // When a weekly goal or task is completed, hide it from the list.
    private int sessionsTally; // Counts how many sessions has been completed for this measuring cycle.
    private int quotaToday; // The running total of how much of the quota they've completed today.
    private int quotaWeek; // The running total of how much of the quota the user completed this week.
    private int quotaMonth; // Running total of how much of the quota was completed this mnoth.
    private int streak; // How many days/weeks in a row the goal has been completed
    private int complexPriority; // Calculated with the user priority, but also criteria like how close to the deadline.
    @Ignore
    private MeasurementHandler measurementSlider;
    @Ignore
    private int quotaTally; // Used to keep track of unsaved quota increments.

    // --------------------------- Overview Data -------------------------------
    private String title; // Name of the goal/task.
    private int intention; // Specifies if the user wants to break or build a habit.
    private int userPriority; // User defined so that things that are important to them are shown at the top of the goal list.
    private int classification; // Specifies if it's a Task/Habit/Event
    private int isPinned; // Pinning prevents the goal from leaving the list.

    // --------------------------- Schedule Data -------------------------------
    private int frequency; // Once/Weekly/Monthly/Daily
    private int deadline; // Some deadlines are user defined, but weekly goals (etc) have an inferred deadline
    private int duration; // How long the task takes to complete
    private int sessions; // How many sessions per week or day.
    private int scheduledTime; // When the task or event is supposed to begin

    // --------------------------- Measurement Data -----------------------------
    private int isMeasurable; // Can be measured using some type of unit
    private String units; // Can be user defined. Most goals will probably use a combo of minutes/hours
    private int quota; // How much "work" must be measured in order to set the goal as completed.

    //endregion MEMBER VARIABLES

    //region CONSTRUCTORS AND BUILDERS
    public Goal(String title, int classification, int intention, int userPriority, int isPinned,
                int isMeasurable, String units, int quota,
                int frequency, int deadline, int duration, int scheduledTime, int sessions,
                int isHidden, int streak, int complexPriority, int sessionsTally,
                int quotaToday, int quotaWeek, int quotaMonth) {
        this.title = title;
        this.intention = intention;
        this.userPriority = userPriority;
        this.classification = classification;
        this.isPinned = isPinned;

        this.frequency = frequency;
        this.deadline = deadline;
        this.duration = duration;
        this.sessions = sessions;
        this.scheduledTime = scheduledTime;

        this.isMeasurable = isMeasurable;
        this.units = units;
        this.quota = quota;

        this.isHidden = isHidden;
        this.quotaToday = quotaToday;
        this.quotaWeek = quotaWeek;
        this.quotaMonth = quotaMonth;
        this.complexPriority = complexPriority;
        this.sessionsTally = sessionsTally;
        this.streak = streak;
    }

    public static Goal buildUserGoal(String title, int classification, int intention, int userPriority, int isPinned,
                                     int isMeasurable, String units, int quota,
                                     int frequency, int deadline, int duration, int scheduledTime, int sessions){
        // Users can define their own goals
        // Some of the goal variables will be set for them
        int streak = 0;
        int quotaToday = 0;
        int quotaWeek = 0;
        int quotaMonth = 0;
        int complexPriority = userPriority;// TODO: update this later once I have a method to calculate complexPriority.
        int isHidden = 0;
        int sessionsTally = 0;

        return new Goal(title, classification, intention, userPriority, isPinned,
                isMeasurable, units, quota,
                frequency, deadline, duration, scheduledTime, sessions, isHidden, streak, complexPriority,
                sessionsTally, quotaToday, quotaWeek, quotaMonth);

    }
    //endregion CONSTRUCTORS AND BUILDERS

    public void calculateAfterSwipe(int direction){
        // When a user swipes this task this function is called.
        // Based on the type of goal and direction swiped, the function needs to set various
        // variables such as sessionsTally, isHidden, and quotaToday

        if(classification == GoalEntry.TASK || classification == GoalEntry.HABIT) {

        }
        else{
            Log.e(LOGTAG, "Swiping was preformed on an unexpected goal classification. App may have unexpected performance.");
        }

        // First thing that needs to be determined is if the Goal has been completed.
        if(getIsMeasurable() == 1){
            // The goal has a quota

            // Increase the quotaToday by the amount in the slider.
            setQuotaToday(quotaToday + quotaTally);
            if(quotaToday >= measurementSlider.getQuotaGoalForToday()){
                // Today's goal has been met, so we can reduce the amount of sessions left.
                isHidden = 1;
                Log.i(LOGTAG, "The quota goal for today has been met!");
            }
        }
        else{
            // The goal doesn't have a quota, so it's simply a yes/no
            if(direction == ItemTouchHelper.RIGHT){
                setQuotaToday(quotaToday + 1);
                isHidden = 1;
            }
        }

        Log.i(LOGTAG, "quotaToday = " + Integer.toString(quotaToday));

        if(direction == ItemTouchHelper.LEFT){
            isHidden = 1;
        }

        if(direction == ItemTouchHelper.RIGHT){
            smartIncreaseSessionsTally();
        }

        recalculateComplexPriority();
    }

    private void smartIncreaseSessionsTally(){
        // SessionsTally can not be equal to the number of sessions if the goal is not finished.

        boolean goalCompleted = false;
        if(frequency == GoalEntry.DAILYGOAL){
            if(quotaToday >= quota){
                goalCompleted = true;
            }
        }
        if(frequency == GoalEntry.WEEKLYGOAL){
            if(quotaToday + quotaWeek >= quota){
                goalCompleted = true;
            }
        }
        if(frequency == GoalEntry.MONTHLYGOAL){
            if(quotaToday + quotaWeek + quotaMonth >= quota){
                goalCompleted = true;
            }
        }

        if((sessionsTally + 1 >= sessions)&&(!goalCompleted)){
            // Only increase sessionsTally if the goal quota has been met.
            // Otherwise, keep the sessions tally at sessions -1.
            return;
        }
        sessionsTally += 1;
    }

    private void recalculateComplexPriority(){

        int baseScaling = 400*userPriority;
        double curveScaling = 20; // larger means that CP will converge to zero faster.

        double hoursFromDeadline = 1; // TODO: calculate this from deadline

        complexPriority = (int)(baseScaling - ( baseScaling*(1 - curveScaling / (hoursFromDeadline + curveScaling)))) + streak;

        if(isPinned == 1){
            complexPriority += 4096000; // Just a large number so that it's bumped to the top of the list.
        }
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        if(id < 0){
            Log.e(LOGTAG, "retrieved id was negative...");
        }
        return id;
    }

    public String getTitle() {
        if(title.equals(null)){
            Log.e(LOGTAG, "retrieved title was null.");
        }
        return title;
    }

    public int getFrequency() {
        if(!GoalsContract.GoalEntry.isValidFrequency(frequency)){
            Log.e(LOGTAG, "frequency was not a recognized value.");
        }
        if(frequency == GoalsContract.GoalEntry.UNDEFINED){
            Log.e(LOGTAG, "frequency was undefined.");
        }
        return frequency;
    }

    public int getIntention() {
        if(!GoalsContract.GoalEntry.isValidIntention(intention)){
            Log.e(LOGTAG, "intention was not a recognized value.");
        }
        return intention;
    }

    public String getUnits() {
        if(units.equals(null)){
            Log.e(LOGTAG, "units were null.");
        }
        return units;
    }

    public int getClassification() {
        if(!GoalsContract.GoalEntry.isValidClassification(classification)){
            Log.e(LOGTAG, "classification was not a recognized value.");
        }
        if(classification == GoalsContract.GoalEntry.UNDEFINED){
            Log.e(LOGTAG, "classification was undefined.");
        }
        return classification;
    }

    public int getDuration() {
        if(duration < -1){
            Log.e(LOGTAG, "duration was an unusual negative number.");
        }
        return duration;
    }

    public int getSessions() {
        if(sessions < 1){
            Log.e(LOGTAG, "sessions was below 1.");
        }
        return sessions;
    }

    public int getScheduledTime() {
        if(scheduledTime < -1){
            Log.e(LOGTAG, "scheduledTime was an unexpected negative number.");
        }
        return scheduledTime;
    }

    public int getIsPinned() {
        if(!(isPinned == 1 || isPinned == 0)){
            Log.e(LOGTAG, "isPinned is supposed to be a boolean, but has a value other than 1 and 0.");
        }
        return isPinned;
    }

    public int getDeadline() {
        if(deadline < -1){
            Log.e(LOGTAG, "deadline should not be less than negative 1.");
        }
        return deadline;
    }

    public int getIsMeasurable() {
        if(!(isMeasurable == 1 || isMeasurable == 0)){
            Log.e(LOGTAG, "isMeasurable is supposed to be a boolean, but has a value other than 1 and 0.");
        }
        return isMeasurable;
    }

    public int getStreak() {
        if(streak < 0){
            Log.e(LOGTAG, "streak can't be negative.");
        }
        return streak;
    }

    public void incStreak(){
        streak += 1;
    }

    public void resetStreak(){
        streak = 0;
    }

    public int getSessionsTally() {
        if(sessionsTally < 0){
            Log.e(LOGTAG, "sessionsTally can't be negative.");
        }
        return sessionsTally;
    }

    public void resetSessionsTally(){
        sessionsTally = 0;
    }

    public void incSessionsTally(){
        sessionsTally += 1;
    }

    //------------------------------------- QUOTA -----------------------------------------
    public int getQuota() {
        if(quota < 1){
            Log.e(LOGTAG, "quota has to be at least 1.");
        }
        return quota;
    }

    public int getIsHidden() {
        if(!(isHidden == 1 || isHidden == 0)){
            Log.e(LOGTAG, "isHidden is supposed to be a boolean, but has a value other than 1 and 0.");
        }
        return isHidden;
    }

    public void setIsHidden(boolean hidden){
        if(!(isHidden == 1 || isHidden == 0)){
            Log.e(LOGTAG, "isHidden is supposed to be a boolean, but has a value other than 1 and 0.");
        }
        isHidden = hidden ? 1 : 0;
    }

    public void setMeasurementHandler(SeekBar slider, TextView quotaText){
        Log.i(LOGTAG, "starting setMeasurementHandler");
        measurementSlider = new MeasurementHandler(this, slider, quotaText);
        Log.i(LOGTAG, "finishing setMeasurementHandler");
    }

    public MeasurementHandler getMeasurementHandler(){
        if(measurementSlider == null){
            Log.e(LOGTAG, title + ": A null measurementSlider was passed back. Never initialized");
        }
        return measurementSlider;
    }

    public int getQuotaToday() {
        if(quotaToday < 0){
            Log.e(LOGTAG, "quotaToday can't be negative.");
        }
        return quotaToday;
    }

    public void setQuotaToday(int q){
        if(q < 0){
            Log.e(LOGTAG, "quotaToday can't be set to something negative.");
        }
        quotaToday = q;
    }

    public int getQuotaWeek() {
        if(quotaWeek < 0){
            Log.e(LOGTAG, "quotaWeek can't be negative.");
        }
        return quotaWeek;
    }

    public void setQuotaWeek(int q){
        if(quotaWeek < 0){
            Log.e(LOGTAG, "quotaWeek can't be set negative.");
        }
        quotaWeek = q;
    }

    public int getQuotaMonth() {
        if(quotaMonth < 0){
            Log.e(LOGTAG, "quotaMonth can't be negative.");
        }
        return quotaMonth;
    }

    public void setQuotaMonth(int q){
        if(quotaMonth < 0){
            Log.e(LOGTAG, "quotaMonth can't be set negative.");
        }
        quotaMonth = q;
    }

    public void setSessions(int i){
        if(i <= 0){
            Log.e(LOGTAG, "error, sessions shouldn't be set to less than 1.");
        }
        sessions = i;
    }

    public int getComplexPriority() {
        if(complexPriority < 0){
            Log.e(LOGTAG, "complexPriority can't be negative.");
        }
        return complexPriority;
    }

    public int getUserPriority() {
        if(!GoalsContract.GoalEntry.isValidPriority(userPriority)){
            Log.e(LOGTAG, "userPriority was not set.");
        }
        return userPriority;
    }

    public void setQuotaTally(int q){
        quotaTally = q;
    }
    public int getQuotaTally(){
        if(quotaTally < 0){
            Log.e(LOGTAG, "quotaTally can't be negative");
        }
        return quotaTally;
    }

    public String toString(){
        return "\nTitle: " + title +
                "\nClassification: " + classification +
                "\nIntention: " + intention +
                "\nUser Priority: " + userPriority +
                "\nIs Pinned: " + isPinned +
                "\nIs Measurable: " + isMeasurable +
                "\nUnits: " + units +
                "\nQuota: " + quota +
                "\nFrequency: " + frequency +
                "\nDeadline: " + deadline +
                "\nScheduled Time: " + scheduledTime +
                "\nSessions: " + sessions +
                "\nID: " + id +
                "\nIs Hidden: " + isHidden +
                "\nStreak: " + streak +
                "\nComplex Priority: " + complexPriority +
                "\nSessions Tally: " + sessionsTally +
                "\nQuota Today: " + quotaToday +
                "\nQuota Week: " + quotaWeek +
                "\nQuota Month: " + quotaMonth;
    }

}
