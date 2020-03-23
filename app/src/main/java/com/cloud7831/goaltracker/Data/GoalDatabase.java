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
            goalDao.insert(new Goal("Test Goal", "weekly", "build habit", "minutes", 1, 420));
            goalDao.insert(new Goal("Test Goal Daily", "daily", "build habit", "minutes", 1, 10));
            goalDao.insert(new Goal("Test Goal Weekly", "weekly", "build habit", "minutes", 2, 200));
            return null;
        }
    }

}
