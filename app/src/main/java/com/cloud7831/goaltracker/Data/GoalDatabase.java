package com.cloud7831.goaltracker.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.cloud7831.goaltracker.Objects.Goal;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Goal.class}, version = 1)
public abstract class GoalDatabase extends RoomDatabase {

    private static GoalDatabase instance;

    public abstract GoalDao goalDao();

    public static synchronized GoalDatabase getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    GoalDatabase.class, "note_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db){
            super.onCreate(db);
            new PopulateDbAsyncTask(instance).execute();
        }
    };

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void>{
        private GoalDao goalDao;

        private PopulateDbAsyncTask(GoalDatabase db){
            goalDao = db.goalDao();
        }

        @Override
        protected Void doInBackground(Void... voids){
            goalDao.insert(new Goal("Test Goal", "build",1,"Habit", 0, 0,"weekly", 0, 1,"minutes", 420, 0, 0));
            goalDao.insert(new Goal("Test Goal 2", "break",3,"Habit", 5, 0,"daily", 0, 1,"minutes", 300, 60, 0));
            goalDao.insert(new Goal("Test Goal", "build habit",1,"Habit", 0, 0,"weekly", 0, 1,"minutes", 420, 0, 0));
            return null;
        }
    }

}
