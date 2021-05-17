package com.cloud7831.goaltracker.Data;

import android.content.Context;
import android.util.Log;

import com.cloud7831.goaltracker.Objects.Goals.DailyHabit;
import com.cloud7831.goaltracker.Objects.Goals.GoalRefactor;
import com.cloud7831.goaltracker.Objects.Goals.MonthlyHabit;
import com.cloud7831.goaltracker.Objects.Goals.Task;
import com.cloud7831.goaltracker.Objects.Goals.WeeklyHabit;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {GoalRefactor.class, Task.class, DailyHabit.class, WeeklyHabit.class, MonthlyHabit.class}, version = 1)
public abstract class GoalDatabase extends RoomDatabase {
    public static final String LOGTAG = "GoalDatabase";

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
            // TODO: make some default goals for new users.
//            new PopulateDbAsyncTask(instance).execute();
        }
    };


    public static void nightlyGoalUpdate(){
        // Note that this isn't on an AsyncTask Thread. This is because this function is called on a
        // worker thread and not from the UI thread.
        Log.i(LOGTAG, "starting nightly update");
        GoalDao dao = instance.goalDao();
        Log.i(LOGTAG, "dao retrieved");

        List<GoalRefactor> goals = mergeGoals(dao);

        // Update and save for each goal in the database.
        for (GoalRefactor i: goals){
            i.nightlyUpdate();

            i.updateGoalInDB(dao);
        }
    }

    private static List<GoalRefactor> mergeGoals(GoalDao dao){
        List<GoalRefactor> goals = new ArrayList<>();
        goals.addAll(dao.getAllDailyHabitsAsList());
        goals.addAll(dao.getAllWeeklyHabitsAsList());
        goals.addAll(dao.getAllMonthlyHabitsAsList());
        goals.addAll(dao.getAllTasksAsList());

        return goals;
    }

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
