package com.cloud7831.goaltracker.Objects.Goals;

import com.cloud7831.goaltracker.Objects.Units;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "exercise_entry_table")
public class ExerciseEntry {
    @PrimaryKey
    private long entryId;
    private String exerciseName;
    private int frequency; // So we know what table to look up the exercise in.
    private int exerciseId; // points to an ID of an exercise in either DailyExercise, WeeklyExercise... tables
}
