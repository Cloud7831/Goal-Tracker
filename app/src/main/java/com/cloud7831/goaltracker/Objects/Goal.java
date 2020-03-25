package com.cloud7831.goaltracker.Objects;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "goal_table")
public class Goal {

    @PrimaryKey(autoGenerate = true)
    private int id;


    // --------------------------- Overview Data -------------------------------
    private String title; // Name of the goal/task.
    private String intention; // Specifies if the user wants to break or build a habit.
    private int priority; // User defined so that things that are important to them are shown at the top of the goal list.
    private String classification; // Specifies if it's a Task/Habit/Event
    private int streak; // How many days/weeks in a row the goal has been completed
    private int isPinned; // Pinning prevents the goal from leaving the list.
    //TODO: Focus (renamed from long term goals that you can group goals under)
    // TODO: Duration, Sessions, SessionsTally, Notes, CardColour, Log, ScheduledTime, ScheduledDays, RepopulateTime

    // --------------------------- Schedule Data -------------------------------
    private String frequency; // Once/Weekly/Monthly/
    private int deadline; // Some deadlines are user defined, but weekly goals (etc) have an inferred deadline

    // --------------------------- Measurement Data -----------------------------
    private int isMeasurable; // Can be measured using some type of unit
    private String units; // Can be user defined. Most goals will probably use a combo of minutes/hours
    private int quota; // How much "work" must be measured in order to set the goal as completed.
    private int quotaTally; // The running total of how much of the quota they've done for this measuring period.

    // --------------------------- Behind the Scenes Data -----------------------
    private int isCompleted; // Completed goals are hidden from the goal list and given lower priority.

    public Goal(String title, String intention, int priority, String classification,
                int streak, int isPinned,
                String frequency, int deadline,
                int isMeasurable, String units, int quota, int quotaTally,
                int isCompleted) {
        this.title = title;
        this.intention = intention;
        this.priority = priority;
        this.classification = classification;
        this.streak = streak;
        this.isPinned = isPinned;

        this.frequency = frequency;
        this.deadline = deadline;

        this.isMeasurable = isMeasurable;
        this.units = units;
        this.quota = quota;
        this.quotaTally = quotaTally;

        this.isCompleted = isCompleted;
    }


    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getFrequency() {
        return frequency;
    }

    public String getIntention() {
        return intention;
    }

    public String getUnits() {
        return units;
    }

    public String getClassification() {
        return classification;
    }

    public int getStreak() {
        return streak;
    }

    public int getIsPinned() {
        return isPinned;
    }

    public int getDeadline() {
        return deadline;
    }

    public int getIsMeasurable() {
        return isMeasurable;
    }

    public int getQuotaTally() {
        return quotaTally;
    }

    public int getIsCompleted() {
        return isCompleted;
    }

    public int getPriority() {
        return priority;
    }

    public int getQuota() {
        return quota;
    }

    public static Goal buildUserGoal(String title, String intention, int priority, String classification,
                              int isPinned,
                              String frequency, int deadline,
                              int isMeasurable, String units, int quota){
        // Users can define their own goals
        // Some of the goal variables will be set for them
        int streak = 0;
        int quotaTally = 0;
        int isCompleted = 0;

        return new Goal(title, intention, priority, classification, streak, isPinned,
         frequency, deadline, isMeasurable, units, quota, quotaTally, isCompleted);
    }
}
