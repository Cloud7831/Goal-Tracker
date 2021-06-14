package com.cloud7831.goaltracker.Data;

import com.cloud7831.goaltracker.Objects.Goals.Task;
import com.cloud7831.goaltracker.Objects.Goals.WorkoutRelated.DailyExercise;
import com.cloud7831.goaltracker.Objects.Goals.WorkoutRelated.MonthlyExercise;
import com.cloud7831.goaltracker.Objects.Goals.WorkoutRelated.WeeklyExercise;

import androidx.room.Dao;
import androidx.room.Query;

@Dao
public interface ExerciseDao {

    @Query("SELECT * FROM monthly_exercise_table WHERE id LIKE :id")
    MonthlyExercise lookupMonthlyExerciseByID(int id);

    @Query("SELECT * FROM weekly_exercise_table WHERE id LIKE :id")
    WeeklyExercise lookupWeeklyExerciseByID(int id);

    @Query("SELECT * FROM daily_exercise_table WHERE id LIKE :id")
    DailyExercise lookupDailyExerciseByID(int id);
}
