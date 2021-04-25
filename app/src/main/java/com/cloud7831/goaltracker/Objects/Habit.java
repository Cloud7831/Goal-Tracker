package com.cloud7831.goaltracker.Objects;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;

public abstract class Habit extends Task{
    private static final String LOGTAG = "HABIT CLASS";

    private int streak; // How many days/weeks/months in a row the goal has been completed

    @Override
    public abstract void nightlyUpdate();

    @Override
    public abstract void setStreakTextView(@NonNull TextView t);

    @Override
    public abstract void setProgressTextView(@NonNull TextView t);

    @Override
    protected abstract void smartIncreaseSessionsTally();

    protected void incStreak(){
        streak += 1;
    }

    protected void resetStreak(){
        streak = 0;
    }

    public void setStreak(int s){
        if(s < 0){
            Log.e(LOGTAG, "Streak can not be set to a negative value");
        }
        streak = s;
    }

    public int getStreak(){
        return streak;
    }

    public abstract int getQuotaCompletedToday();


    public String toString(){
        return super.toString() + "\nStreak: " + streak;
    }
}
