package com.cloud7831.goaltracker.Objects;

import com.cloud7831.goaltracker.Data.GoalsContract;

import androidx.room.Entity;
import androidx.room.Ignore;
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
        quotaTally = 0;
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

    public void setQuotaTally(int q){
        quotaTally = q;
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

    public void addToQuotaToday(int q){
        quotaToday += q;
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


        // TODO: Handle time quotas differently.
        if(sessions == 0){
            // Just incase sessions was never set.
            sessions = 1;
            sessionsTally = 0;
        }

        // TODO: this is only for weekly goals. Implement for other types later.
        if(quota < quotaWeek){
            // If the goal is already complete, then quotaWeek will be > quota
            // Therefore, calculate how much you would have had to do on day 1 to give a reasonable
            // goal for today.
            sessionsTally = 0;
            // TODO: it might be bad to set quotaWeek to 0, because it affects the stats of how much you did that week.
            quotaWeek = 0;
        }

        int quotaLeft = quota - quotaWeek;

        if(sessions - sessionsTally == 1){
            return quotaLeft;
        }

        // This will give a decimal, but we want the final values to be a nice int.
        double exactVal = ((double) (quotaLeft)) /(sessions - sessionsTally);

        // exactVal is the exact amount of quota assigned per session.
        // The goal is to split this exact amount into a nice int so that there are no decimals.

        int baseVal; // The minimum amount of quota that each day has.
        int leftOver; // The amount of quota that needs to be redistributed amongst the days remaining.

        // If exactVal is smaller than 10, then each increment is just 1, and the notches of the
        // bar gets adjusted to match.
        // TODO: make the bar adjust its notches based on TodaysQuota
        if(exactVal <= 15){
            baseVal = ((int)(exactVal)); // cast to int is an intentional floor option.
            leftOver = quotaLeft - baseVal * (sessions - sessionsTally);
            if(leftOver >= 1){
                return baseVal + 1;
            }
            else {
                return baseVal;
            }
        }
        else {
            // used for regrouping. Each quota will be a factor of the threshhold.

            //TODO: adjust these threshholds when I have a better understanding of what the numbers should be.
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

            // I did a cast to an int as an intentional floor operation. Hence the / 5 * 5.
            baseVal = ((int)(exactVal/ threshhold)) * 5;
            leftOver = quotaLeft - baseVal * (sessions - sessionsTally);

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
    }

    public int calcQuotaProgress(int progress){
        // maxVal should have been calculated with getTodaysQuota()
        // if the units are time based, then maxVal is in seconds

        int maxVal = getTodaysQuota();
        if(progress == 10){
            // This is the max value of the progress bar, so just return maxVal.
            return maxVal;
        }
        int adjustedProgress;
        String unitsToUse = units;
        if(units.equals(GoalsContract.GoalEntry.MINUTE_STRING) || units.equals(GoalsContract.GoalEntry.HOUR_STRING) || units.equals(GoalsContract.GoalEntry.SECOND_STRING)){
            // The values are in times, so conversions may need to be done.
            if(maxVal <= 150){
                // Break it into 15s intervals
                if(maxVal - (progress * 15) < 0){
                    // A full 15s interval couldn't be made for the last value, so just use whatever's left.
                    adjustedProgress = maxVal - ((progress - 1) * 15);
                }
                else{
                    adjustedProgress = progress * 15;
                }
            }
            else if(maxVal <= 300){
                // Break it into 30s intervals
                if(maxVal - (progress * 30) < 0){
                    // A full 30s interval couldn't be made for the last value, so just use whatever's left.
                    adjustedProgress = maxVal - ((progress - 1) * 30);
                }
                else{
                    adjustedProgress = progress * 30;
                }
            }
            else if(maxVal <= 600){
                // Break it into 1m intervals
                if(maxVal - (progress * 60) < 0){
                    // A full 60s interval couldn't be made for the last value, so just use whatever's left.
                    adjustedProgress = maxVal - ((progress - 1) * 60);
                }
                else{
                    adjustedProgress = progress * 60;
                }
            }
            else if(maxVal <= 600){
                // Break it into 1m intervals
                if(maxVal - (progress * 60) < 0){
                    // A full 60s interval couldn't be made for the last value, so just use whatever's left.
                    adjustedProgress = maxVal - ((progress - 1) * 60);
                }
                else{
                    adjustedProgress = progress * 60;
                }
            }
            else if(maxVal <= 1200){
                // Break it into 2m intervals
                if(maxVal - (progress * 120) < 0){
                    // A full 120s interval couldn't be made for the last value, so just use whatever's left.
                    adjustedProgress = maxVal - ((progress - 1) * 120);
                }
                else{
                    adjustedProgress = progress * 120;
                }
            }
            else if(maxVal < 3000){
                // Note, not <= because it's better if 50mins is handled by an increment of 10mins.
                // Break it into 5m intervals
                if(maxVal - (progress * 300) < 0){
                    // A full 300s interval couldn't be made for the last value, so just use whatever's left.
                    adjustedProgress = maxVal - ((progress - 1) * 300);
                }
                else{
                    adjustedProgress = progress * 300;
                }
            }
            else if(maxVal <= 5400){
                // Break it into 10m intervals
                if(maxVal - (progress * 600) < 0){
                    // A full 600s interval couldn't be made for the last value, so just use whatever's left.
                    adjustedProgress = maxVal - ((progress - 1) * 600);
                }
                else{
                    adjustedProgress = progress * 600;
                }
            }
            else if(maxVal <= 9000){
                // Break it into 15m intervals
                if(maxVal - (progress * 900) < 0){
                    // A full 900s interval couldn't be made for the last value, so just use whatever's left.
                    adjustedProgress = maxVal - ((progress - 1) * 900);
                }
                else{
                    adjustedProgress = progress * 900;
                }
            }
            else if(maxVal <= 18000){
                // Break it into 30m intervals
                if(maxVal - (progress * 1800) < 0){
                    // A full 1800s interval couldn't be made for the last value, so just use whatever's left.
                    adjustedProgress = maxVal - ((progress - 1) * 1800);
                }
                else{
                    adjustedProgress = progress * 1800;
                }
            }
            else if(maxVal <= 36000){
                // Break it into 1h intervals
                if(maxVal - (progress * 3600) < 0){
                    // A full 3600s interval couldn't be made for the last value, so just use whatever's left.
                    adjustedProgress = maxVal - ((progress - 1) * 3600);
                }
                else{
                    adjustedProgress = progress * 3600;
                }
            }
            else if(maxVal <= 72000){
                // Break it into 2h intervals
                if(maxVal - (progress * 7200) < 0){
                    // A full 2h interval couldn't be made for the last value, so just use whatever's left.
                    adjustedProgress = maxVal - ((progress - 1) * 7200);
                }
                else{
                    adjustedProgress = progress * 7200;
                }
            }
            else if(maxVal <= 86400){
                // Break it into 3h intervals
                if(maxVal - (progress * 10800) < 0){
                    // A full 3h interval couldn't be made for the last value, so just use whatever's left.
                    adjustedProgress = maxVal - ((progress - 1) * 10800);
                }
                else{
                    adjustedProgress = progress * 10800;
                }
            }
            else{
                // Something went wrong, because there was a time value I didn't account for
                adjustedProgress = -1;
            }
        }
        else {
            if(maxVal < 10){
                adjustedProgress = progress;
            }
            else{
                // integer division so that it turncates the decimal.
                adjustedProgress = (maxVal / 10) * progress;
            }
        }

        return adjustedProgress;
    }

    public String quotaToString(int progress){
        return (calcQuotaProgress(progress)+ quotaToday) + "/" + getTodaysQuota() + units + "s";
    }

    public String toString(){
        return "Title: " + title +
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
