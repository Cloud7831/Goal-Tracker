package com.cloud7831.goaltracker.Data;

import com.cloud7831.goaltracker.Objects.DailyHabit;
import com.cloud7831.goaltracker.Objects.GoalRefactor;
import com.cloud7831.goaltracker.Objects.Task;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

public interface TaskDao {

//    @Insert
//    void insert(Task task);
//
//    @Update
//    void updateTask(Task task);
//
//    @Delete
//    void deleteTask(Task task);
//
//    @Query("DELETE FROM task_table WHERE id LIKE :id")
//    void deleteTaskByID(int id);
//
//    @Query("DELETE FROM task_table")
//    void deleteAllTasks();
//
//    @Query("SELECT * FROM task_table WHERE id LIKE :id")
//    GoalRefactor lookupGoalByID(int id);
//
//    @Query("SELECT * FROM task_table ORDER BY complexPriority DESC")
//    LiveData<List<Task>> getAllTasks();
//
//    @Query("SELECT * FROM task_table")
//    List<Task> getAllTasksAsList();
//
//    @Query("SELECT * FROM task_table WHERE isHidden LIKE 0 OR isPinned LIKE 1 ORDER BY complexPriority DESC")
//    LiveData<List<Task>> getTodaysTasks();
}
