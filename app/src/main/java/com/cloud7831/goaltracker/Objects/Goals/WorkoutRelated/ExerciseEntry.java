package com.cloud7831.goaltracker.Objects.Goals.WorkoutRelated;

import com.cloud7831.goaltracker.Objects.Units;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * This class is for displaying Exercises in the Goal List part of the UI when contained in a Workout.
 * The Workout doesn't yet need to know all the details of the exercise, such as current reps and
 * what not.
 */
@Entity(tableName = "exercise_entry_table")
public class ExerciseEntry {

    @PrimaryKey(autoGenerate = true)
    private long entryId;
    private String exerciseName;
    private int frequency; // So we know what table to look up the exercise in.
    private int exerciseId; // points to an ID of an exercise in either DailyExercise, WeeklyExercise... tables
    private int isBehindProgress; // Set to true if the exercise should be completed today.

    // Empty constructor for the Room Database.
    public ExerciseEntry(){
    }

    @Ignore
    public ExerciseEntry(String name, int freq, int id, boolean isBehind){
        exerciseName = name;
        frequency = freq;
        exerciseId = id;
        if(isBehind){
            isBehindProgress = 1;
        }
        else{
            isBehindProgress = 0;
        }
    }

    public String getExerciseName(){
        return exerciseName;
    }


}
