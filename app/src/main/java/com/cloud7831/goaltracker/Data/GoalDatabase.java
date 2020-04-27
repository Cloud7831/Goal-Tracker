package com.cloud7831.goaltracker.Data;

import android.content.Context;
import android.os.AsyncTask;

import com.cloud7831.goaltracker.Objects.Goal;
import com.cloud7831.goaltracker.Data.GoalsContract.GoalEntry;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Goal.class}, version = 3)
public abstract class GoalDatabase extends RoomDatabase {

    private static GoalDatabase instance;

    public abstract GoalDao goalDao();

    public static synchronized GoalDatabase getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    GoalDatabase.class, "goal_database")
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
//            new PopulateDbAsyncTask(instance).execute();
        }
    };

//    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void>{
//        private GoalDao goalDao;
//
//        private PopulateDbAsyncTask(GoalDatabase db){
//            goalDao = db.goalDao();
//        }
//
//        @Override
//        protected Void doInBackground(Void... voids){
//            goalDao.insert(new Goal("Test Goal", 1,1,1, 0, 0, GoalEntry.WEEKLYGOAL, 0, 0, 4, 0, 1,"minutes", 420, 0, 0, 0));
//            goalDao.insert(new Goal("Test Goal 2", 2,3,1, 5, 0, GoalEntry.DAILYGOAL, 0, 0, 4, 0, 0, "minutes", 300, 60, 1, 60));
//            goalDao.insert(new Goal("Test Goal 3", 1,1,1, 0, 0, GoalEntry.MONTHLYGOAL, 0, 0, 4, 0, 1,"minutes", 420, 0, 0, 0));
//            return null;
//        }
//    }

}
