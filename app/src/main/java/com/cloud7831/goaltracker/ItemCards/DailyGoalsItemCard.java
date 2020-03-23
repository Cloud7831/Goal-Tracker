package com.cloud7831.goaltracker.ItemCards;

import com.cloud7831.goaltracker.Data.GoalsContract;
import com.cloud7831.goaltracker.Objects.Goal;

public class DailyGoalsItemCard implements GoalsItemCard{

    private String name = "DAILY GOAL";
    private String schedule = "From 6:30pm - 7:30pm";
    private String streak = "0 days";
    private int frequency;

    public DailyGoalsItemCard(){
        //TODO: fill in the constructor later
    }

    public DailyGoalsItemCard(Goal goal){
        name = goal.getTitle();
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
    public int getInterval() {
        return frequency;
    }

    @Override
    public String getName() {
        return name;
    }

}
