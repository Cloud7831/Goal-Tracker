package com.cloud7831.goaltracker.Data;

import com.cloud7831.goaltracker.Objects.Goals.Task;
import com.cloud7831.goaltracker.Objects.Units;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface UnitsDao {

    @Insert
    void insert(Units units);

    @Update
    void update(Units units);

    @Delete
    void delete(Units units);

    @Query("SELECT * FROM units_table WHERE id LIKE :id")
    Units lookupUnitsByID(int id);

    @Query("SELECT * FROM units_table ORDER BY id DESC")
    LiveData<List<Units>> getAllUnits();
}
