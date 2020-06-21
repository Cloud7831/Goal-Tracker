package com.cloud7831.goaltracker.Data;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.cloud7831.goaltracker.Objects.Goal;
import com.cloud7831.goaltracker.Data.GoalsContract.GoalEntry;

import java.util.Calendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Goal.class}, version = 3)
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

    public static  void nightlyGoalUpdate(){
        // Note that this isn't on an AsyncTask Thread. This is because this function is called on a
        // worker thread and not from the UI thread.
        Log.i(LOGTAG, "starting nightly update");
        GoalDao dao = instance.goalDao();
        Log.i(LOGTAG, "dao retrieved");

        List<Goal> goalList = dao.getAllGoalsAsList();
        Log.i(LOGTAG, "Goal list retrieved");

        if(goalList == null){
            Log.e(LOGTAG, "Goal list was null!");
        }

        for (int i = 0; i < goalList.size(); i++){
            Calendar calendar = Calendar.getInstance();

            Goal curr = goalList.get(i);
            dailyGoalUpdate(curr);

            // Remember that the goals update between 3-5am so Monday = end of the week.
            if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY){
                weeklyGoalUpdate(curr);
            }

            if(calendar.get(Calendar.DAY_OF_MONTH) == 1){
                monthlyGoalUpdate(curr);
            }

            Log.i(LOGTAG, "Updating" + curr);
            dao.update(curr);
        }
    }

    private static void dailyGoalUpdate(Goal goal){
        Log.i(LOGTAG, "starting dailyGoalUpdate");
        int freq = goal.getFrequency();
        goal.setIsHidden(false);

        if(freq == GoalsContract.GoalEntry.DAILYGOAL){
            // If this is a dailyGoal, we need to update the streak
            if(goal.getQuotaToday() >= goal.getQuota()){
                goal.incStreak();
            }
            else{
                goal.resetStreak();
            }
            goal.resetSessionsTally();
            goal.setQuotaToday(0);
        }
        else if(freq == GoalsContract.GoalEntry.WEEKLYGOAL || freq == GoalsContract.GoalEntry.MONTHLYGOAL){
            // Today's quota needs to roll over to quota for the week.
            goal.setQuotaWeek(goal.getQuotaWeek() + goal.getQuotaToday());
            goal.setQuotaToday(0);
        }
        else{
            Log.e(LOGTAG, "DailyGoalUpdate: unaccounted for frequency");
        }
    }

    private static void weeklyGoalUpdate(Goal goal){
        Log.i(LOGTAG, "starting weeklyGoalUpdate");

        int freq = goal.getFrequency();

        if(freq == GoalsContract.GoalEntry.DAILYGOAL){
            // Nothing needs to be done.
        }
        else if(freq == GoalsContract.GoalEntry.WEEKLYGOAL){
            // If this is a weeklyGoal, we need to update the streak
            if(goal.getQuotaWeek() >= goal.getQuota()){
                goal.incStreak();
            }
            else{
                goal.resetStreak();
            }
            goal.resetSessionsTally();
            goal.setQuotaWeek(0);
        }
        else if(freq == GoalsContract.GoalEntry.MONTHLYGOAL){
            // This week's quota needs to roll over to quota for the month.
            goal.setQuotaMonth(goal.getQuotaWeek() + goal.getQuotaMonth());
            goal.setQuotaWeek(0);
        }
        else{
            Log.e(LOGTAG, "DailyGoalUpdate: unaccounted for frequency");
        }
    }

    private static void monthlyGoalUpdate(Goal goal){
        Log.i(LOGTAG, "starting MonthlyGoalUpdate");

        int freq = goal.getFrequency();

        if(freq == GoalsContract.GoalEntry.DAILYGOAL || freq == GoalsContract.GoalEntry.WEEKLYGOAL){
            // Nothing needs to be done.
        }
        else if(freq == GoalsContract.GoalEntry.MONTHLYGOAL){
            // A monthly goal might finish half way through a week. This means that what's recorded
            // so far for the week needs to be considered in the total, and that the monthly goal
            // must clear the weekly data.

            // If this is a monthlyGoal, we need to update the streak
            int totalQuota = goal.getQuotaWeek() + goal.getQuotaMonth();
            if(totalQuota >= goal.getQuota()){
                goal.incStreak();
            }
            else{
                goal.resetStreak();
            }
            goal.resetSessionsTally();
            goal.setQuotaWeek(0);
            goal.setQuotaMonth(0);
        }
        else{
            Log.e(LOGTAG, "DailyGoalUpdate: unaccounted for frequency");
        }
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
