package com.cloud7831.goaltracker.Objects.Goals.WorkoutRelated;

import com.cloud7831.goaltracker.Objects.Goals.WeeklyHabit;

import androidx.room.Entity;

@Entity(tableName = "weekly_exercise_table")
public class WeeklyExercise extends WeeklyHabit implements Exercise{
}
