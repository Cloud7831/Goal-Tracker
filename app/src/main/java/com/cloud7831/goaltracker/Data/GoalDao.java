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

    @Query("SELECT * FROM goal_table ORDER BY priority DESC")
    LiveData<List<Goal>> getAllNotes();
}
