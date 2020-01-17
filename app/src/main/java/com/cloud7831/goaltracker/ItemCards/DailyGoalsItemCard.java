package com.cloud7831.goaltracker.ItemCards;

import com.cloud7831.goaltracker.Data.GoalsContract;

public class DailyGoalsItemCard implements GoalsItemCard{

    private String name = "DAILY GOAL";
    private String schedule = "From 6:30pm - 7:30pm";
    private String streak = "0 days";

    public DailyGoalsItemCard(){
        //TODO: fill in the constructor later
    }

    @Override
    public Boolean hasScheduledTime() {
        return true;
    }

    @Override
    public String getScheduledTime() {
        return schedule;
    }

    @Override
    public Boolean hasStreak() {
        return true;
    }

    @Override
    public String getStreak() {
        return streak;
    }

    @Override
    public GoalsContract.GoalsCardType getType() {
        return GoalsContract.GoalsCardType.DAILYGOAL;
    }

    @Override
    public String getName() {
        return name;
    }

}
