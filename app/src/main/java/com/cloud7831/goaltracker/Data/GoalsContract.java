package com.cloud7831.goaltracker.Data;

public final class GoalsContract {

    // All the possible types of a goal card
    public enum GoalsCardType{
        UNDEFINED,
        TASK,           //used for one time tasks (that might be repeated in the future). Ex: do to dentist. Call doctor.
        MONITORING,      // Not so much a goal, but instead just about data tracking for whatever you choose. Hours watching TV, hours playing video games, etc.
        AVOIDANCE,       // For the user to break a habit, such as fast food, drinking, alcohol, etc.
        RECURRING        // Recurring is essentially any weekly, monthly, daily goal.
    }

    public enum GoalsInterval{
        DAILYGOAL,      // Goals that you want to achieve every single day. Ex: Brush teeth, eat less than 3000 calories.
        WEEKLYGOAL,     // Weekly goals are the main types of goals. More flexibility when you do something. Learn Japanese (10 hours/week). Exercise (5 days a week).
        MONTHLYGOAL,    // Monthly goals are more for making sure you do something longterm. Ex: put $1,000 into savings.
        YEARLYGOAL,     // Yearly goals should almost never be used. Use Long-Term Goals/Milestones instead.
        MULTIGOAL,      // This is when you want to mix both a weekly and monthly and potentially even a daily goal together.
    }
}
