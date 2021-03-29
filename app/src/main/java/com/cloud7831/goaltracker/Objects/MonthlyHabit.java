package com.cloud7831.goaltracker.Objects;

import android.util.Log;

public class MonthlyHabit extends Habit{

    //    private int quotaWeek; // The running total of how much of the quota the user completed this week.

    private void setQuotaToday(int q){
        if(q < 0){
            Log.e(LOGTAG, "quotaToday can't be set to something negative.");
        }
        quotaToday = q;
    }

    private void setQuotaWeek(int q){
        if(quotaWeek < 0){
            Log.e(LOGTAG, "quotaWeek can't be set negative.");
        }
        quotaWeek = q;
    }


    //    private int quotaMonth; // Running total of how much of the quota was completed this mnoth.
}
