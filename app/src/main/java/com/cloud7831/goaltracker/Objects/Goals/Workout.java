package com.cloud7831.goaltracker.Objects.Goals;

public class Workout{

    protected String exerciseList; // Arrays can't be stored in Room, so store the id list as a string.

    // Workouts are objects that store an ordering of exercises. Each workout can be made into a goal
    // so that the user doesn't have to make individual goals for the excercises. Workouts take care of
    // making individual goals for exercises and informing the user of which exercises still need to be
    // completed for that week.

    // It's not easy to store an array in Room, so I think I'll need to store the exercise IDs as a
    // string...


}
