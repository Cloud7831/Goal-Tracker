package com.cloud7831.goaltracker.Data;


import com.cloud7831.goaltracker.Objects.Goal;

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
    void insert(Goal goal);

    @Update
    void update(Goal goal);

    @Delete
    void delete(Goal goal);

    @Query("DELETE FROM goal_table")
    void deleteAllGoals();

    @Query("SELECT * FROM goal_table WHERE id LIKE :id")
    Goal lookupGoalByID(int id);

    @Query("SELECT * FROM goal_table ORDER BY userPriority DESC") // TODO: change this to complexPriority later.
    LiveData<List<Goal>> getAllGoals();

    @Query("SELECT * FROM goal_table WHERE isHidden LIKE 0 OR isPinned LIKE 1 ORDER BY userPriority DESC")
    LiveData<List<Goal>> getTodaysGoals();
}
