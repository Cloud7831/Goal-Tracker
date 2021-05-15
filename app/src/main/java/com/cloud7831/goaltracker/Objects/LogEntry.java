package com.cloud7831.goaltracker.Objects;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "log_table")
public class LogEntry {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private long date;
    private int goalID;
    private int quota;
    private int units; // the id of a units object, not the object itself

    // Tells how this log entry affects the goal. Was it quota being recorded? A new max being set?
    private int tag;


    public static final int RECORD_QUOTA = 1;
    public static final int UPDATE_QUOTA_GOAL = 2;


    LogEntry(long date, int id, int quota, int unitID, int tag){
        this.date = date;
        this.goalID = id;
        this.quota = quota;
        this.units = unitID;
        this.tag = tag;
    }

    public int getId(){
        return id;
    }
    public int getQuota(){
        return quota;
    }
    public int getUnits(){
        return units;
    }
    public int getGoalID(){
        return goalID;
    }
    public int getTag(){
        return tag;
    }


}
