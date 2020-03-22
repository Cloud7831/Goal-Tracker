package com.cloud7831.goaltracker.Objects;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "goal_table")
public class Goal {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;

    private String frequency;

    private String intention;

    private String units;

    private int priority;

    private int quota;

    public Goal(String title, String frequency, String intention, String units, int priority, int quota) {
        this.title = title;
        this.frequency = frequency;
        this.intention = intention;
        this.units = units;
        this.priority = priority;
        this.quota = quota;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getFrequency() {
        return frequency;
    }

    public String getIntention() {
        return intention;
    }

    public String getUnits() {
        return units;
    }

    public int getPriority() {
        return priority;
    }

    public int getQuota() {
        return quota;
    }
}
