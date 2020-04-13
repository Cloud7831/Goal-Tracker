package com.cloud7831.goaltracker.Objects;

import com.cloud7831.goaltracker.Data.GoalsContract;

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

    //------------------------------------- QUOTA -----------------------------------------
    public int getQuota() {
        return quota;
    }

    public int getTodaysQuota() {
        // Returns the amount of quota that must be completed today in order to stay on track.
        // If time is the units, then the return int is in seconds.

        if(quota < quotaTally){
            // If the goal is already complete, then quotaTally will be > quota
            // Therefore, calculate how much you would have had to do on day 1 to give a reasonable
            // goal for today.
            sessionsTally = 0;
            quotaTally = 0;
        }

        int quotaLeft = quota - quotaTally;

        // Convert to seconds
        if(units.equals("hour")){
            quotaLeft *= 60 * 60;
        }
        else if(units.equals("min")){
            quotaLeft *= 60;
        }

        if(sessions == 0){
            // Just incase sessions was never set.
                sessions = 1;
                sessionsTally = 0;
        }

        // This will give a decimal, but we want the final values to be a nice int.
        double exactVal = ((double) (quotaLeft)) /(sessions - sessionsTally);

        // The base value that each session will have.
        int baseVal = ((int)(exactVal/ 5.0)) * 5;


        int leftOver = quotaLeft - baseVal * (sessions - sessionsTally);

        int threshhold;
        if(exactVal < 50){
            threshhold = 5;
        }
        else if(exactVal < 100){
            threshhold = 10;
        }
        else if(exactVal < 250){
            threshhold = 25;
        }
        else{
            threshhold = 50;
        }

        int regrouping = leftOver - (threshhold * sessionsTally);
        if(regrouping >= threshhold){
            return threshhold + baseVal;
        }
        else if(regrouping < 0){
            return baseVal;
        }
        else {
            return baseVal + regrouping;
        }
    }

    private int calcQuotaProgress(int progress){
        // maxVal should have been calculated with getTodaysQuota()
        // if the units are time based, then maxVal is in seconds

//        if(units.equals(GoalsContract.GoalEntry.MINUTE_STRING) || units.equals(GoalsContract.GoalEntry.HOUR_STRING) || units.equals(GoalsContract.GoalEntry.SECOND_STRING)){
//            // The values are in times, so conversions may need to be done.
//            if(maxVal<= 100){
//
//            }
//        }

        return (getTodaysQuota()/10) * progress;
    }

    public String quotaToString(int progress){
        return calcQuotaProgress(progress) + "/" + getTodaysQuota() + units + "s";
    }

//    // Quota class deals with converting and calculating quotas.
//
//    public class Quota {
//        private int value;
//        private String units;
//
//
//        public Quota(int quota, int quotaTally, String units){
//            // I want to store everything as an int, but multiply by 100 to have 2 decimal spots.
//            value = quota * 100;
//
//        }
//
//        private int calcEndValue(int quota, int quotaTally, int sessions, int sessionsTally){
//            //TODO: do (quota - quotaTally)/(sessions - sessionsTally) and then round it to a nice number
//
//
//        }
}
