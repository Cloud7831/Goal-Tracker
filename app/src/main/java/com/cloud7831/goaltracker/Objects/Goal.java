package com.cloud7831.goaltracker.Objects;

import com.cloud7831.goaltracker.Data.GoalsContract;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

// TODO: Goal has become a bit of a monster object. At the time of writing this it has over 20 member variables,
// TODO: so maybe it's best to break some of the member variables into their own objects such as
// TODO: a quota object, a schedule object, etc.

@Entity(tableName = "goal_table")
public class Goal {

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
        return quotaToday;
    }

    public int getIsHidden() {
        return isHidden;
    }

    public int getQuotaToday() {
        return quotaToday;
    }

    public int getQuotaWeek() {
        return quotaWeek;
    }

    public int getQuotaMonth() {
        return quotaMonth;
    }

    public int getComplexPriority() {
        return complexPriority;
    }

    public void setIsHidden(boolean hidden){
        isHidden = hidden ? 1 : 0;
    }

    public int getUserPriority() {
        return userPriority;
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

    //------------------------------------- QUOTA -----------------------------------------
    public int getQuota() {
        return quota;
    }

    public int getTodaysQuota() {
        // Returns the amount of quota that must be completed today in order to stay on track.
        // If time is the units, then the return int is in seconds.

        // TODO: this is only for weekly goals. Implement for other types later.
        if(quota < quotaWeek + quotaToday){
            // If the goal is already complete, then quotaWeek will be > quota
            // Therefore, calculate how much you would have had to do on day 1 to give a reasonable
            // goal for today.
            sessionsTally = 0;
            quotaWeek = 0;
        }

        int quotaLeft = quota - (quotaToday + quotaWeek);

        if(sessions == 0){
            // Just incase sessions was never set.
                sessions = 1;
                sessionsTally = 0;
        }

        // This will give a decimal, but we want the final values to be a nice int.
        if(sessions - sessionsTally == 1){
            return quotaLeft;
        }
        double exactVal = ((double) (quotaLeft)) /(sessions - sessionsTally);

        // The base value that each session will have.
        // I did a cast to an int as an intentional floor operation. Hence the / 5 * 5.
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

}
