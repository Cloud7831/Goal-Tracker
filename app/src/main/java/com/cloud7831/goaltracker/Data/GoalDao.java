package com.cloud7831.goaltracker.Data;

import com.cloud7831.goaltracker.Objects.DailyHabit;
import com.cloud7831.goaltracker.Objects.GoalRefactor;
import com.cloud7831.goaltracker.Objects.Habit;
import com.cloud7831.goaltracker.Objects.MonthlyHabit;
import com.cloud7831.goaltracker.Objects.Task;
import com.cloud7831.goaltracker.Objects.WeeklyHabit;

import java.time.Month;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface GoalDao {

    @Insert
    void insert(Task task);

    @Update
    void update(Task task);

    @Delete
    void delete(Task task);

    @Query("DELETE FROM task_table WHERE id LIKE :id")
    void deleteTaskByID(int id);

    @Query("DELETE FROM task_table")
    void deleteAllTasks();

    @Query("SELECT * FROM task_table WHERE id LIKE :id")
    Task lookupTaskByID(int id);

    @Query("SELECT * FROM task_table ORDER BY complexPriority DESC")
    LiveData<List<Task>> getAllTasks();

    // Used for updating the DB every night.
    @Query("SELECT * FROM task_table ORDER BY complexPriority DESC")
    List<Task> getAllTasksAsList();

    @Query("SELECT * FROM task_table WHERE isHidden LIKE 0 OR isPinned LIKE 1 ORDER BY complexPriority DESC")
    LiveData<List<Task>> getTodaysTasks();


    // Daily Habits
    @Insert
    void insert(DailyHabit habit);

    @Update
    void update(DailyHabit dailyHabit);

    @Delete
    void delete(DailyHabit habit);

    @Query("DELETE FROM daily_habit_table WHERE id LIKE :id")
    void deleteDailyHabitByID(int id);

    @Query("DELETE FROM daily_habit_table")
    void deleteAllDailyHabits();

    @Query("SELECT * FROM daily_habit_table WHERE id LIKE :id")
    DailyHabit lookupDailyHabitByID(int id);

    @Query("SELECT * FROM daily_habit_table ORDER BY complexPriority DESC")
    LiveData<List<DailyHabit>> getAllDailyHabits();

    // Used for updating the DB every night.
    @Query("SELECT * FROM daily_habit_table ORDER BY complexPriority DESC")
    List<DailyHabit> getAllDailyHabitsAsList();

    @Query("SELECT * FROM daily_habit_table WHERE isHidden LIKE 0 OR isPinned LIKE 1 ORDER BY complexPriority DESC")
    LiveData<List<DailyHabit>> getTodaysDailyHabits();

    // Weekly Habits
    @Insert
    void insert(WeeklyHabit habit);

    @Update
    void update(WeeklyHabit habit);

    @Delete
    void delete(WeeklyHabit habit);

    @Query("DELETE FROM weekly_habit_table WHERE id LIKE :id")
    void deleteWeeklyHabitByID(int id);

    @Query("DELETE FROM weekly_habit_table")
    void deleteAllWeeklyHabits();

    @Query("SELECT * FROM weekly_habit_table WHERE id LIKE :id")
    WeeklyHabit lookupWeeklyHabitByID(int id);

    @Query("SELECT * FROM weekly_habit_table ORDER BY complexPriority DESC")
    LiveData<List<WeeklyHabit>> getAllWeeklyHabits();

    // Used for updating the DB every night.
    @Query("SELECT * FROM weekly_habit_table ORDER BY complexPriority DESC")
    List<WeeklyHabit> getAllWeeklyHabitsAsList();

    @Query("SELECT * FROM weekly_habit_table WHERE isHidden LIKE 0 OR isPinned LIKE 1 ORDER BY complexPriority DESC")
    LiveData<List<WeeklyHabit>> getTodaysWeeklyHabits();

    // Monthly Habits
    @Insert
    void insert(MonthlyHabit habit);

    @Update
    void update(MonthlyHabit habit);

    @Delete
    void delete(MonthlyHabit habit);

    @Query("DELETE FROM monthly_habit_table WHERE id LIKE :id")
    void deleteMonthlyHabitByID(int id);

    @Query("DELETE FROM monthly_habit_table")
    void deleteAllMonthlyHabits();

    @Query("SELECT * FROM monthly_habit_table WHERE id LIKE :id")
    MonthlyHabit lookupMonthlyHabitByID(int id);

    @Query("SELECT * FROM monthly_habit_table ORDER BY complexPriority DESC")
    LiveData<List<MonthlyHabit>> getAllMonthlyHabits();

    // Used for updating the DB every night.
    @Query("SELECT * FROM monthly_habit_table ORDER BY complexPriority DESC")
    List<MonthlyHabit> getAllMonthlyHabitsAsList();

    @Query("SELECT * FROM monthly_habit_table WHERE isHidden LIKE 0 OR isPinned LIKE 1 ORDER BY complexPriority DESC")
    LiveData<List<MonthlyHabit>> getTodaysMonthlyHabits();
}
