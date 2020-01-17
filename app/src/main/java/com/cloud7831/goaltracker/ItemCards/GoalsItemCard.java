package com.cloud7831.goaltracker.ItemCards;

import com.cloud7831.goaltracker.Data.GoalsContract;
import com.cloud7831.goaltracker.Data.GoalsContract.*;

abstract class GoalItemCard {
    private GoalsCardType type = GoalsCardType.UNDEFINED;

    // Returns the type of the item card
    public GoalsCardType getType(){
        return type;
    }
}
