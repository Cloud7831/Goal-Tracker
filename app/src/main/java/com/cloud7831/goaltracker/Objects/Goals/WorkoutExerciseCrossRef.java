package com.cloud7831.goaltracker.Objects.Goals;

import androidx.room.Entity;

@Entity(primaryKeys = {"id", "entryId"})
public class WorkoutExerciseCrossRef {
    private int id;
    private int entryId;
}
