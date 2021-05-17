package com.cloud7831.goaltracker.Objects;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "units_table")
public class Units {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String singular;
    private String plural;
    private String reducedSingular; // Max 4 letters. Examples are "min" "hr" "ml" etc
    private String reducedPlural; // Max 4 letters. Examples are "mins" "hrs" "mls" etc


}
