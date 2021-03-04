package com.cloud7831.goaltracker.Objects;

import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import com.cloud7831.goaltracker.Data.GoalsContract;

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

    public static Task buildNewUserTask(String title, int userPriority,  int isPinned, int intention, int classification,
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

    public Task editUserTaskSettings(String title, int userPriority, int isPinned, int intention, int classification,
                                 int isMeasurable, String units, int quota,
                                 int duration, int scheduledTime, int deadline, int sessions){
        // This function is called when the user wants to edit the settings of a goal in
        // GoalEditorActivity.

        this.title = title;
        this.userPriority = userPriority;
        this.isPinned = isPinned;
        this.intention = intention;
        this.classification = classification;

        this.isMeasurable = isMeasurable;
        this.units = units;
        this.quota = quota;

        this.duration = duration;
        this.scheduledTime = scheduledTime;
        this.deadline = deadline;
        this.sessions = sessions;

        recalculateComplexPriority();

        return this;
    }
    //endregion CONSTRUCTORS AND BUILDERS

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

    public void onSwipe(int direction){
        // When a user swipes this task this function is called.
        // Based on the type of goal and direction swiped, the function needs to set various
        // variables such as sessionsTally, isHidden, and quotaToday

        if(classification == GoalEntry.HABIT || classification == GoalEntry.TASK) {
            // Regardless of a left or a right swipe, hide the goal.
            // TODO: make the goal only hide if the user is ahead of its schedule.
            isHidden = 1;

            // First thing that needs to be determined is if the Goal has been completed.
            // And update the quota.
            if(getIsMeasurable() == 1){
                // The goal has a quota

                // Increase the quotaToday by the amount in the slider.
                setQuotaToday(quotaToday + quotaTally);
            }
            else{
                // The goal isn't measurable, so it's simply a yes/no
                if(direction == ItemTouchHelper.RIGHT){
                    setQuotaToday(quotaToday + 1);
                }
            }

            if(direction == ItemTouchHelper.RIGHT){
                // Sessions Tally won't be set to the max if the goal isn't complete.
                smartIncreaseSessionsTally();
            }

            recalculateComplexPriority();
        }
        else{
            Log.e(LOGTAG, "Swiping was preformed on an unexpected goal classification. App may have unexpected performance.");
        }

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
