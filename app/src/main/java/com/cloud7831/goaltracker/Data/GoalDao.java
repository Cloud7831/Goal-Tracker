package com.cloud7831.goaltracker.Data;

import com.cloud7831.goaltracker.Objects.GoalRefactor;

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
    void insert(GoalRefactor goal);

    @Update
    void update(GoalRefactor goal);

    @Delete
    void delete(GoalRefactor goal);

    @Query("DELETE FROM goal_table WHERE id LIKE :id")
    void deleteByID(int id);

    @Query("DELETE FROM goal_table")
    void deleteAllGoals();

    @Query("SELECT * FROM goal_table WHERE id LIKE :id")
    GoalRefactor lookupGoalByID(int id);

    @Query("SELECT * FROM goal_table ORDER BY complexPriority DESC")
    LiveData<List<GoalRefactor>> getAllGoals();

    @Query("SELECT * FROM goal_table ORDER BY complexPriority DESC")
    List<GoalRefactor> getAllGoalsAsList();

    @Query("SELECT * FROM goal_table WHERE isHidden LIKE 0 OR isPinned LIKE 1 ORDER BY complexPriority DESC")
    LiveData<List<GoalRefactor>> getTodaysGoals();
}
