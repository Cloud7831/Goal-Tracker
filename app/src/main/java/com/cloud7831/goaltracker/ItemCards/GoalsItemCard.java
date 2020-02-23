package com.cloud7831.goaltracker.ItemCards;

import com.cloud7831.goaltracker.Data.GoalsContract.*;

public interface GoalsItemCard {

    int getInterval();


    Boolean hasStreak();
    String getStreak();

    String getName();

    Boolean hasScheduledTime();
    String getScheduledTime();
}
