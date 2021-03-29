package com.cloud7831.goaltracker.Objects;

import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.cloud7831.goaltracker.Data.GoalsContract;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "goal_table")
public abstract class GoalRefactor {
    private static final String LOGTAG = "GOAL CLASS";

    //region MEMBER VARIABLES -----------------------------------------------------------------
    // --------------------------- Behind the Scenes Data -----------------------
    @PrimaryKey(autoGenerate = true)
    protected int id;
    protected int isHidden; // When a weekly goal or task is completed, hide it from the list.
    protected int complexPriority; // Calculated with the user priority, but also criteria like how close to the deadline.

    // --------------------------- Overview Data -------------------------------
    protected String title; // Name of the goal/task.
    protected int userPriority; // User defined so that things that are important to them are shown at the top of the goal list.
    protected int isPinned; // Pinning prevents the goal from leaving the list.

    // --------------------------- Schedule Data -------------------------------
    protected int duration; // How long the task takes to complete
    protected int scheduledTime; // When the task or event is supposed to begin

    //endregion MEMBER VARIABLES---------------

    public void onSwipe(int direction){
        if(direction == ItemTouchHelper.RIGHT){
            // Generally a right swipe indicates success/completion.
            onSwipeRight();
        }
        else if(direction == ItemTouchHelper.LEFT){
            // Generally a left swipe indicates a failure, or the user just wants to hide it.
            onSwipeLeft();
        }
        else{
            Log.e(LOGTAG, "The item was swiped in an unaccounted for direction.");
        }
    }

    protected abstract void onSwipeRight();

    protected void onSwipeLeft(){
        // Left swipes mean to hide the goal from the list.
        setIsHidden(0);
        // TODO: consider making a worker unhide the goal after a certain amount of time.

    }

    protected void recalculateComplexPriority(){

        int baseScaling = 400*userPriority;
        double curveScaling = 20; // larger means that CP will converge to zero faster.

        double hoursFromDeadline = 1; // TODO: calculate this from deadline

        // TODO: give habits some extra CP if there's a streak for the goal.
        complexPriority = (int)(baseScaling - ( baseScaling*(1 - curveScaling / (hoursFromDeadline + curveScaling))));

        if(isPinned == 1){
            complexPriority += 4096000; // Just a large number so that it's bumped to the top of the list.
        }
    }

    // Every night, goals need to update their streaks, isHidden, etc depending on the state of the goal.
    public abstract void nightlyUpdate();

    // Used to update the display card if something in the database has changed.
    public abstract boolean hasGoalChanged(@NonNull Goal newGoal);

    // For when the user picks new parameters in the GoalEditorActivity
    public abstract void editUserSettings(String title, int userPriority, int isPinned, int intention, int classification,
                                          int isMeasurable, String units, int quota,
                                          int duration, int scheduledTime, int deadline, int sessions);

    //region GOAL LIST DISPLAY FUNCITONS -------------------------------------------------------
    public abstract void setTitleTextView(@NonNull TextView t);
    public abstract void setStreakTextView(@NonNull TextView t);
    public abstract void setProgressTextView(@NonNull TextView t);
    public abstract void setMeasurementView(@NonNull View v, SeekBar b, TextView plus, TextView minus, TextView quota);
    //endregion GOAL LIST DISPLAY FUNCITONS -------------------------------------------------------

    //region GOAL CONVERSION FUNCTIONS ----------------------------------------------------------
    public abstract DailyHabit convertToDailyHabit();
    public abstract WeeklyHabit convertToWeeklyHabit();
    public abstract MonthlyHabit convertToMonthlyHabit();
    public abstract Task convertToTask();
    //endregion GOAL CONVERSION FUNCTIONS ----------------------------------------------------------

    //region SETTER FUNCTIONS -----------------------------------------------------------------
    public void setId(int id) {
        this.id = id;
    }

    public void setIsHidden(int hidden){
        // booleans must be stored as ints in room.
        isHidden = hidden;
    }

    public void setComplexPriority(int c){
        // TODO: are there invalid values for complexPriority?
        complexPriority = c;
    }

    public void setDuration(int d){
        if(d < 0){
            Log.e(LOGTAG, "Duration can not be set to a negative value.");
        }
        duration = d;
    }

    public void setScheduledTime(int s){
        if(s < 0){
            Log.e(LOGTAG, "ScheduledTime can not be set to a negative value.");
        }
        scheduledTime = s;
    }

    public void setTitle(String t){
        if(t == "" || t == null){
            title = "Unnamed Goal";
            Log.e(LOGTAG, "A goal's title was null or empty string.");
        }
        else{
            title = t;
        }
    }

    public void setUserPriority(int u){
        if(GoalsContract.GoalEntry.isValidPriority(u)){
            userPriority = u;
        }
        else{
            Log.e(LOGTAG, "An invalid value was passed to setUserPriority.");
        }
    }

    public void setIsPinned(int isPinned) {
        // booleans must be stored as ints in room.
        this.isPinned = isPinned;
    }

    //endregion SETTER FUNCTIONS ----------------------------------------------------------------

    //region GETTER FUNCTIONS -------------------------------------------------------------------

    public String getTitle() {
        return title;
    }

    public int getUserPriority() {
        return userPriority;
    }

    public int getIsPinned() {
        return isPinned;
    }

    public int getDuration() {
        return duration;
    }

    public int getScheduledTime() {
        return scheduledTime;
    }

    public int getIsHidden() {
        return isHidden;
    }

    public int getComplexPriority() {
        return complexPriority;
    }

    //endregion GETTER FUNCTIONS -------------------------------------------------------------------

    public String toString(){
        return  "\nID: " + id +
                "\nTitle: " + title +
                "\nUser Priority: " + userPriority +
                "\nIs Pinned: " + isPinned +
                "\nScheduled Time: " + scheduledTime +
                "\nDuration: " + duration +
                "\nIs Hidden: " + isHidden +
                "\nComplex Priority: " + complexPriority;
    }
}
