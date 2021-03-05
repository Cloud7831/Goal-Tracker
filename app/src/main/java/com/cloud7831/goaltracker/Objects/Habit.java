package com.cloud7831.goaltracker.Objects;

import android.util.Log;

import androidx.recyclerview.widget.ItemTouchHelper;

public abstract class Habit extends Task{

    private int streak; // How many days/weeks/months in a row the goal has been completed
//    private int quotaToday; // The running total of how much of the quota they've completed today.
//    private int quotaWeek; // The running total of how much of the quota the user completed this week.
//    private int quotaMonth; // Running total of how much of the quota was completed this mnoth.




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

    @Override
    protected void smartIncreaseSessionsTally(){

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
