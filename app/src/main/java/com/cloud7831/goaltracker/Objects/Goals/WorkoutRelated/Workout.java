package com.cloud7831.goaltracker.Objects.Goals.WorkoutRelated;

import com.cloud7831.goaltracker.HelperClasses.ArrayStorageHelper;
import com.cloud7831.goaltracker.Objects.Goals.WeeklyHabit;

import java.lang.reflect.Array;

import androidx.room.Entity;

@Entity(tableName = "workout_table")
public class Workout extends WeeklyHabit {

    private String exerciseList; // Arrays can't be stored in Room, so store the id list as a string.
    private int dateLastCompleted;
    // Workouts are objects that store an ordering of exercises. Each workout can be made into a goal
    // so that the user doesn't have to make individual goals for the excercises. Workouts take care of
    // making individual goals for exercises and informing the user of which exercises still need to be
    // completed for that week.

}
