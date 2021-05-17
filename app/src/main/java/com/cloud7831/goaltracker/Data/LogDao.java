package com.cloud7831.goaltracker.Data;

import com.cloud7831.goaltracker.Objects.Logging.LogEntry;
import com.cloud7831.goaltracker.Objects.Goals.Task;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface LogDao {

    @Insert
    void insert(LogEntry log);

    // No need to update a log entry atm.
//    @Update
//    void update(LogEntry log);

    @Delete
    void delete(LogEntry log);

    @Query("DELETE FROM log_table WHERE id LIKE :id")
    void deleteLogEntryByID(int id);

    @Query("DELETE FROM log_table")
    void deleteAllTasks();

    @Query("SELECT * FROM log_table WHERE goalID LIKE :id ORDER BY date DESC")
    Task lookupLogByGoalID(int id);

}
