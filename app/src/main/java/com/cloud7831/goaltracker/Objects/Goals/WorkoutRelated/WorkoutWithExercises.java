package com.cloud7831.goaltracker.Objects.Goals.WorkoutRelated;

import java.util.List;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

public class WorkoutWithExercises {
    @Embedded
    private Workout workout;

    @Relation(
            parentColumn = "id",
            entityColumn = "entryId",
            associateBy = @Junction(WorkoutExerciseCrossRef.class)
    )
    private List<ExerciseEntry> entries;
    
}
