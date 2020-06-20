package com.cloud7831.goaltracker.Objects;

import android.util.Log;

import com.cloud7831.goaltracker.Data.GoalsContract;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

// TODO: Goal has become a bit of a monster object. At the time of writing this it has over 20 member variables,
// TODO: so maybe it's best to break some of the member variables into their own objects such as
// TODO: a quota object, a schedule object, etc.

@Entity(tableName = "goal_table")
public class Goal {
    private static final String LOGTAG = "GOAL CLASS";

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
    @Ignore
    private int quotaGoalForToday; // How much quota should be completed today. Only needs to be calculated once, but is used in a couple spots.

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
        quotaGoalForToday = calcQuotaGoalForToday();
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

    public int getDuration() {
        return duration;
    }

    public int getSessions() {
        return sessions;
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

    public int getStreak() {
        return streak;
    }

    public void incStreak(){
        streak += 1;
    }

    public void resetStreak(){
        streak = 0;
    }

    public int getSessionsTally() {
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
        return quota;
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

    public void setQuotaToday(int q){
        quotaToday = q;
    }

    public int getQuotaWeek() {
        return quotaWeek;
    }

    public void setQuotaWeek(int q){
        quotaWeek = q;
    }

    public int getQuotaMonth() {
        return quotaMonth;
    }

    public void setQuotaMonth(int q){
        quotaMonth = q;
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

    public int getQuotaGoalForToday(){
        return quotaGoalForToday;

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

    private int sliceTimeQuota(int seconds, int numSlices) {
        // Takes in time as seconds, and then tries to split it into formatted slices
        // Slices need to be a number appropriate for displaying simply
        // For example, if the time measures to be 3201 seconds, it be rounded up to 55 minutes,
        // instead of 53.35 minutes.
        // Returns the number of seconds of the first slice.
        // The first slice will be the biggest (because it's better for the user to do the large
        // slices first)

        if (numSlices <= 0) {
            //TODO: Throw an error.
            Log.i(LOGTAG, "Function sliceTimeSegment: numSlices was less than 1");
            return -1;
        }

        // This will give a decimal, but we want the final values to be a nice int.
        double exactVal = ((double) (seconds)) / (numSlices);

        // exactVal is the exact amount of quota assigned per session.
        // The goal is to split this exact amount into a nice int so that there are no decimals.

        int baseVal; // The minimum amount of quota that each day has.
        int leftOver; // The amount of quota that needs to be redistributed amongst the days remaining.
        int threshhold;
        // If exactVal is smaller than 10, then each increment is just 1, and the notches of the
        // bar gets adjusted to match.
        if (exactVal <= 15){
            threshhold = 1;
        }
        else if(exactVal <= 60) {
            threshhold = 5;
        }
        else if (exactVal <= 300) {
            threshhold = 15;
        }
        else if (exactVal <= 600) {
            threshhold = 30;
        }
        else if (exactVal <= 900) {
            threshhold = 60;
        }
        else if (exactVal <= 3600) {
            threshhold = 300;
        }
        else if (exactVal <= 10800) {
            threshhold = 900;
        }
        else {
            threshhold = 1800;
        }

        baseVal = ((int) ((exactVal)/threshhold)) * threshhold; // cast to int is an intentional floor option.
        leftOver = seconds - (baseVal * numSlices);
        if (leftOver > 0) {
            return baseVal + threshhold;
        } else {
            return baseVal;
        }

    }

    private int sliceGenericQuota(int quota, int numSlices) {
        // Takes in quota and then tries to split it into formatted slices
        // Slices need to be a number appropriate for displaying in a simple manner.
        // For example, if the quota measures to be 221 units, it be rounded up to 225 or 250,
        // Returns the quota of the first (largest) slice.
        // The first slice will be the biggest (because it's better for the user to do the large
        // slices first)

        if (numSlices <= 0) {
            //TODO: Throw an error.
            Log.i(LOGTAG, "Function sliceQuota: numSlices was less than 1");
            return -1;
        }

        // This will give a decimal, but we want the final values to be a nice int.
        double exactVal = ((double) (quota)) / (numSlices);

        // exactVal is the exact amount of quota assigned per session.
        // The goal is to split this exact amount into a nice int so that there are no decimals.

        int baseVal; // The minimum amount of quota that each day has.
        int leftOver; // The amount of quota that needs to be redistributed amongst the days remaining.
        int threshhold = 1;
        // If exactVal is smaller than 10, then each increment is just 1, and the notches of the
        // bar gets adjusted to match.
        if (exactVal <= 25){
            threshhold = 1;
        }
        else if(exactVal <= 100) {
            threshhold = 5;
        }
        else if (exactVal <= 200) {
            threshhold = 10;
        }
        else if (exactVal <= 500) {
            threshhold = 25;
        }
        else if (exactVal <= 1000) {
            threshhold = 50;
        }
        else if (exactVal <= 2500) {
            threshhold = 100;
        }
        else if (exactVal <= 5000) {
            threshhold = 250;
        }
        else {
            threshhold = 500;
        }

        baseVal = ((int) ((exactVal)/threshhold)) * threshhold; // cast to int is an intentional floor option.
        leftOver = quota - (baseVal * numSlices);
        if (leftOver > 0) {
            return baseVal + threshhold;
        } else {
            return baseVal;
        }

    }

    private int sliceQuota(int q, int s){
        if(GoalsContract.GoalEntry.isValidTime(units)){
            return sliceTimeQuota(q, s);
        }
        else{
            return sliceGenericQuota(q, s);
        }
    }

    private int calcQuotaGoalForToday() {
        // Returns the amount of quota that must be completed today in order to stay on track.
        // If units are time-based, then the return int is in seconds.

        // Calculate the amount of Quota that needs to be completed over the goal period
        int quotaLeftOverPeriod;
        if(frequency == GoalsContract.GoalEntry.DAILYGOAL){
            quotaLeftOverPeriod = quota - quotaToday;
        }
        else if(frequency == GoalsContract.GoalEntry.WEEKLYGOAL){
            quotaLeftOverPeriod = quota - quotaWeek - quotaToday;
        }
        else if(frequency == GoalsContract.GoalEntry.MONTHLYGOAL){
            quotaLeftOverPeriod = quota - quotaMonth - quotaWeek - quotaToday;
        }
//        else if(frequency == GoalsContract.GoalEntry.FIXEDGOAL){
//            //TODO:
//        }
        else{
            quotaLeftOverPeriod = quota;
        }

        // Check that the goal is not already completed.
        if(quotaLeftOverPeriod < 0){
            // Therefore, calculate how much you would have had to do on day 1 to give a reasonable
            // goal for today.
            quotaLeftOverPeriod = quota;
        }

        // Calculate how many sessions the user needs to complete their goal.
        int sessionsRemaining;
        if(sessions == 0){
            // Just incase sessions was never set.
            sessionsRemaining = 1;
            sessions = 1;
        }else{
            sessionsRemaining = sessions - sessionsTally;
        }

        if(sessionsRemaining <=0){
            sessionsRemaining = sessions;
        }

        return sliceQuota(quotaLeftOverPeriod, sessionsRemaining);
    }

    public int calcQuotaProgress(int progress){
        // maxVal should have been calculated with calcTodaysQuota()
        // if the units are time based, then maxVal is in seconds
        if(progress == 0){
            return 0;
        }

        int maxVal = quotaGoalForToday - quotaToday;

        if(progress >= calcNotches()){
            // This is the max value of the progress bar, so just return maxVal.
            return maxVal;
        }
        else{
            return calcQuotaPerNotch() * progress;
        }
    }

    private int calcQuotaPerNotch(){
        // Calculates how much quota per notch of the progress bar slider.
        int maxVal = quotaGoalForToday - quotaToday;
        if(GoalsContract.GoalEntry.isValidTime(units)){
            // The values are in times, so conversions may need to be done.
            // This quota per notch is returned in seconds.
            if(maxVal <= 10){
                // Break it into 1s intervals
                return 1;
            }
            else if(maxVal <= 20){
                // Break it into 2s intervals
                return 2;
            }
            else if(maxVal <= 45){
                // Break it into 5s intervals
                return 5;
            }
            else if (maxVal <= 150){
                // Break it into 15s intervals
                return 15;
            }
            else if(maxVal <= 300){
                // Break it into 30s intervals
                return 30;
            }
            else if(maxVal <= 600){
                // Break it into 1m intervals
                return 60;
            }
            else if(maxVal <= 1200){
                // Break it into 2m intervals
                return 120;
            }
            else if(maxVal < 3000){
                // Note, not <= because it's better if 50mins is handled by an increment of 10mins.
                // Break it into 5m intervals
                return 300;
            }
            else if(maxVal <= 5400){
                // Break it into 10m intervals
                return 600;
            }
            else if(maxVal <= 9000){
                // Break it into 15m intervals
                return 900;
            }
            else if(maxVal <= 18000){
                // Break it into 30m intervals
                return 1800;
            }
            else if(maxVal <= 36000){
                // Break it into 1h intervals
                return 3600;
            }
            else if(maxVal <= 72000){
                // Break it into 2h intervals
                return 7200;
            }
            else if(maxVal <= 86400){
                // Break it into 3h intervals
                return 10800;
            }
            else{
                // Something went wrong, because there was a time value I didn't account for
                return -1;
                // TODO: throw an error.
            }
        }
        else {
            if(maxVal < 10){
                return 1;
            }
            else if(maxVal <= 20){
                return 2;
            }
            else if(maxVal <= 50){
                return 5;
            }
            else if (maxVal <= 100){
                return 10;
            }
            else if(maxVal <= 250){
                return 25;
            }
            else if(maxVal <= 500){
                return 50;
            }
            else if(maxVal <= 1000){
                return 100;
            }
            else if(maxVal < 2500){
                return 250;
            }
            else if(maxVal <= 5000){
                return 500;
            }
            else if(maxVal <= 10000){
                return 1000;
            }
            else if(maxVal <= 25000){
                return 2500;
            }
            else if(maxVal <= 50000){
                return 5000;
            }
            else if(maxVal <= 100000){
                return 10000;
            }
            else{
                // Use integer division to round to the nearest 10000, then break it into 10 notches.
                return (maxVal/10000)*10000 / 10;
            }
        }

    }

    public int calcNotches(){
        int maxVal = quotaGoalForToday - quotaToday;

        int quotaPerNotch = calcQuotaPerNotch();

        int num = (int)Math.ceil((double)maxVal/quotaPerNotch);

        if(num <= 0 || num > 10){
            Log.e(LOGTAG, "The calculation for the number of notches was not in the expected range.");
            return -1;
        }

        return num;
    }

    public String todaysQuotaToString(int progress){
        // Returns a string for the progress bar slider.
        // Shows how much of the goal has been completed vs the goal for today.
        String quotaString = "";


        int progVal = calcQuotaProgress(progress);
        if(GoalsContract.GoalEntry.isValidTime(units)){
            quotaString += Double.toString(GoalsContract.GoalEntry.roundAndConvertTime(progVal));
        }
        else{
            quotaString += Integer.toString(progVal);
        }

        quotaString += "/";
        // TODO: this needs to be properly format the time.
        // TODO: for time values, the end value may need to be converted, and so do the units.

        int maxVal = quotaGoalForToday - quotaToday;
        if(GoalsContract.GoalEntry.isValidTime(units)){
            quotaString += Double.toString(GoalsContract.GoalEntry.roundAndConvertTime(maxVal));
        }
        else{
            quotaString += Integer.toString(maxVal);
        }

        // TODO: make it so that units can be translated.
        // TODO: time values may use units that aren't the initially declared units.
        quotaString += " " + units + "s";

        return quotaString;
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
