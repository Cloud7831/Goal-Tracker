package com.cloud7831.goaltracker.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.cloud7831.goaltracker.Data.GoalsContract.*;

public class GoalDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "GoalTracker.db";

    public GoalDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        String SQL_CREATE_GOALS_TABLE = "CREATE TABLE "  + GoalEntry.TABLE_NAME + "("
                + GoalEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + GoalEntry.GOAL_NAME + " TEXT NOT NULL, "
                + GoalEntry.GOAL_FREQUENCY + " TEXT NOT NULL, "
                + GoalEntry.GOAL_QUOTA + " INTEGER NOT NULL, "
                + GoalEntry.GOAL_UNITS + " TEXT, "
                + GoalEntry.GOAL_INTENTION + " INTEGER);";
        db.execSQL(SQL_CREATE_GOALS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1){

    }



}
