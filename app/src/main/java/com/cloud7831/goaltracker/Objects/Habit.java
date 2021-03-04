package com.cloud7831.goaltracker.Objects;

import android.util.Log;

public abstract class Habit extends Task{

    private int streak; // How many days/weeks/months in a row the goal has been completed
    private int quotaToday; // The running total of how much of the quota they've completed today.
    private int quotaWeek; // The running total of how much of the quota the user completed this week.
    private int quotaMonth; // Running total of how much of the quota was completed this mnoth.


    public Goal editUserTaskSettings(String title, int userPriority, int isPinned, int intention, int classification,
                                     int isMeasurable, String units, int quota,
                                     int duration, int scheduledTime, int deadline, int sessions){
        // This function is called when the user wants to edit the settings of a goal in
        // GoalEditorActivity. If the goal goes from being weekly to monthly, etc, then calculations
        // must be done to preserve data integrity.

        if(this.frequency != frequency){
            // Daily -> Weekly
            if(this.frequency == GoalEntry.DAILYGOAL && frequency == GoalEntry.WEEKLYGOAL){
                streak = streak / 7; // integer division is fine, I don't mind if it rounds down.
                // Make sure there isn't a junk value in quotaWeek
                quotaWeek = 0;
            }
            // Daily -> Monthly
            else if(this.frequency == GoalEntry.DAILYGOAL && frequency == GoalEntry.MONTHLYGOAL){
                streak = streak / 30; // integer division is fine, I don't mind if it rounds down.
                // Make sure there isn't a junk value in quotaWeek
                quotaWeek = 0;
                quotaMonth = 0;
            }
            // Weekly -> Daily
            else if(this.frequency == GoalEntry.WEEKLYGOAL && frequency == GoalEntry.DAILYGOAL){
                streak = streak * 7;
                // quotaWeek is no longer needed.
                quotaWeek = 0;
            }
            // Weekly -> Monthly
            else if(this.frequency == GoalEntry.WEEKLYGOAL && frequency == GoalEntry.MONTHLYGOAL){
                streak = streak /4; // Let's just say that 4 weeks == 1 month
                // Make sure there isn't a junk value in quotaMonth
                quotaMonth = 0;
            }
            // Monthly -> Daily
            else if(this.frequency == GoalEntry.MONTHLYGOAL && frequency == GoalEntry.DAILYGOAL){
                streak = streak * 30;
                // quotaWeek and quotaMonth are no longer needed.
                quotaWeek = 0;
                quotaMonth = 0;
            }
            // Monthly -> Weekly
            else if(this.frequency == GoalEntry.MONTHLYGOAL && frequency == GoalEntry.WEEKLYGOAL){
                streak = streak * 4; // Let's just say there are 4 weeks in a month.
                // quotaMonth is no longer needed.
                quotaMonth = 0;
            }
        }

        this.title = title;
        this.classification = classification;
        this.intention = intention;
        this.userPriority = userPriority;
        this.isPinned = isPinned;
        this.isMeasurable = isMeasurable;
        this.units = units;
        this.quota = quota;
        this.frequency = frequency;
        this.deadline = deadline;
        this.duration = duration;
        this.scheduledTime = scheduledTime;
        this.sessions = sessions;

        recalculateComplexPriority();

        return this;
    }

    public void setQuotaToday(int q){
        if(q < 0){
            Log.e(LOGTAG, "quotaToday can't be set to something negative.");
        }
        quotaToday = q;
    }

    public void setQuotaWeek(int q){
        if(quotaWeek < 0){
            Log.e(LOGTAG, "quotaWeek can't be set negative.");
        }
        quotaWeek = q;
    }

    public void setQuotaMonth(int q){
        if(quotaMonth < 0){
            Log.e(LOGTAG, "quotaMonth can't be set negative.");
        }
        quotaMonth = q;
    }

    public void incStreak(){
        streak += 1;
    }

    public void resetStreak(){
        streak = 0;
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
