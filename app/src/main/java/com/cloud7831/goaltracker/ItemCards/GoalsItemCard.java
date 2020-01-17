package com.cloud7831.goaltracker.ItemCards;

import android.graphics.Color;

import com.cloud7831.goaltracker.Data.GoalsContract;
import com.cloud7831.goaltracker.Data.GoalsContract.*;

public interface GoalsItemCard {

    // Returns the type of the item card
    GoalsCardType getType();


    Boolean hasStreak();
    String getStreak();

    String getName();

    Boolean hasScheduledTime();
    String getScheduledTime();
}
