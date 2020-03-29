package com.cloud7831.goaltracker.Objects;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "goal_table")
public class Goal {

    // --------------------------- Behind the Scenes Data -----------------------
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int isHidden; // When a weekly goal or task is completed, hide it from the list.
    private int sessionsTally; // Counts how many sessions has been completed for this measuring cycle.
    private int quotaTally; // The running total of how much of the quota they've done for this measuring period.
    private int streak; // How many days/weeks in a row the goal has been completed

    // --------------------------- Overview Data -------------------------------
    private String title; // Name of the goal/task.
    private int intention; // Specifies if the user wants to break or build a habit.
    private int priority; // User defined so that things that are important to them are shown at the top of the goal list.
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

    public Goal(String title, int intention, int priority, int classification,
                int streak, int isPinned,
                int frequency, int deadline, int duration, int sessions, int scheduledTime,
                int isMeasurable, String units, int quota,
                int isHidden, int sessionsTally, int quotaTally) {
        this.title = title;
        this.intention = intention;
        this.priority = priority;
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
        this.quotaTally = quotaTally;
        this.sessionsTally = sessionsTally;
        this.streak = streak;
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

    public int getFrequency() {
        return frequency;
    }

    public int getIntention() {
        return intention;
    }

    public String getUnits() {
        return units;
    }

    public int getClassification() {
        return classification;
    }

    public int getStreak() {
        return streak;
    }

    public int getDuration() {
        return duration;
    }

    public int getSessions() {
        return sessions;
    }

    public int getSessionsTally() {
        return sessionsTally;
    }

    public int getScheduledTime() {
        return scheduledTime;
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

    public int getIsHidden() {
        return isHidden;
    }

    public int getPriority() {
        return priority;
    }

    public int getQuota() {
        return quota;
    }

    public static Goal buildUserGoal(String title, int intention, int priority, int classification,
                              int isPinned, int duration, int sessions, int scheduledTime,
                              int frequency, int deadline,
                              int isMeasurable, String units, int quota){
        // Users can define their own goals
        // Some of the goal variables will be set for them
        int streak = 0;
        int quotaTally = 0;
        int isHidden = 0;
        int sessionsTally = 0;

        return new Goal(title, intention, priority, classification, streak, isPinned,
         frequency, deadline, duration, sessions, scheduledTime, isMeasurable, units, quota,isHidden, sessionsTally, quotaTally);
    }
}
