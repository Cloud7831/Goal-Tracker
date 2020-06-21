package com.cloud7831.goaltracker.Data;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import com.cloud7831.goaltracker.Objects.Goal;

import java.util.Calendar;
import java.util.List;

import androidx.lifecycle.LiveData;

public class GoalRepository {
    public static final String LOGTAG = "GoalRepository";

    private GoalDao goalDao;
    private LiveData<List<Goal>> allGoals;
    private static Goal retrievedGoal;

    public GoalRepository(Application application){
        GoalDatabase database = GoalDatabase.getInstance(application);
        goalDao = database.goalDao();
        allGoals = goalDao.getAllGoals();
    }

    public void insert(Goal goal){
        new InsertGoalAsyncTask(goalDao).execute(goal);
    }

    public void update(Goal goal){
        new UpdateGoalAsyncTask(goalDao).execute(goal);
    }

    public void delete(Goal goal){
        new DeleteGoalAsyncTask(goalDao).execute(goal);
    }

    public void deleteAllGoals(){
        new DeleteAllGoalsAsyncTask(goalDao).execute();
    }

    public LiveData<List<Goal>> getAllGoals() {
        return allGoals;
    }

    public Goal lookupGoalByID(Integer id){
        retrievedGoal = null; // TODO: this is only so that it waits for the new goal to load.
        new LookupGoalAsyncTask(goalDao).execute(id);
        Log.i("TESTING:", "retrieved goal: " + retrievedGoal);
        while (retrievedGoal == null){
            // TODO: remove this. I need to set a call back, so that when the goal is finished loading, it's passed. Otherwise I just pass a null Goal.
        }
        return retrievedGoal;
    }


    public LiveData<List<Goal>> getTodaysGoals() {
        //TODO: put this in an async task.
        return goalDao.getTodaysGoals();
    }

    private static class InsertGoalAsyncTask extends AsyncTask<Goal, Void, Void>{
        private GoalDao goalDao;

        private InsertGoalAsyncTask(GoalDao goalDao){
            this.goalDao = goalDao;
        }

        @Override
        protected Void doInBackground(Goal... goals){
            goalDao.insert(goals[0]); // because we're being passed an array of goals, but our insert is just adding one goal. This means we only need to insert the first element of the array.
            return null;
        }
    }

    private static class UpdateGoalAsyncTask extends AsyncTask<Goal, Void, Void>{
        private GoalDao goalDao;

        private UpdateGoalAsyncTask(GoalDao goalDao){
            this.goalDao = goalDao;
        }

        @Override
        protected Void doInBackground(Goal... goals){
            goalDao.update(goals[0]); // because we're being passed an array of goals, but our insert is just adding one goal. This means we only need to insert the first element of the array.
            return null;
        }
    }

    private static class LookupGoalAsyncTask extends AsyncTask<Integer, Void, Void>{
        private GoalDao goalDao;

        private LookupGoalAsyncTask(GoalDao goalDao){
            this.goalDao = goalDao;
        }

        @Override
        protected Void doInBackground(Integer... id){
            retrievedGoal = goalDao.lookupGoalByID(id[0]); // because we're being passed an array of ints
            Log.i("TESTING", "Goal retrieved:" + retrievedGoal);
            return null;
        }

    }

    private static class DeleteGoalAsyncTask extends AsyncTask<Goal, Void, Void>{
        private GoalDao goalDao;

        private DeleteGoalAsyncTask(GoalDao goalDao){
            this.goalDao = goalDao;
        }

        @Override
        protected Void doInBackground(Goal... goals){
            goalDao.delete(goals[0]); // because we're being passed an array of goals, but our insert is just adding one goal. This means we only need to insert the first element of the array.
            return null;
        }
    }

    private static class DeleteAllGoalsAsyncTask extends AsyncTask<Goal, Void, Void>{
        private GoalDao goalDao;

        private DeleteAllGoalsAsyncTask(GoalDao goalDao){
            this.goalDao = goalDao;
        }

        @Override
        protected Void doInBackground(Goal... goals){
            goalDao.deleteAllGoals();
            return null;
        }
    }

//    public void nightlyGoalUpdate(){
//        // Note that this isn't on an AsyncTask Thread. This is because this function is called on a
//        // worker thread and not from the UI thread.
//        List<Goal> goalList = allGoals.getValue();
//
//        for (int i = 0; i < goalList.size(); i++){
//            Calendar calendar = Calendar.getInstance();
//
//            Goal curr = goalList.get(i);
//            dailyGoalUpdate(curr);
//
//            // Remember that the goals update between 3-5am so Monday = end of the week.
//            if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY){
//                weeklyGoalUpdate(curr);
//            }
//
//            if(calendar.get(Calendar.DAY_OF_MONTH) == 1){
//                monthlyGoalUpdate(curr);
//            }
//
//            goalDao.update(curr);
//        }
//    }
//
//    private void dailyGoalUpdate(Goal goal){
//        int freq = goal.getFrequency();
//
//        if(freq == GoalsContract.GoalEntry.DAILYGOAL){
//            // If this is a dailyGoal, we need to update the streak
//            if(goal.getQuotaToday() >= goal.getQuota()){
//                goal.incStreak();
//            }
//            else{
//                goal.resetStreak();
//            }
//            goal.resetSessionsTally();
//            goal.setQuotaToday(0);
//        }
//        else if(freq == GoalsContract.GoalEntry.WEEKLYGOAL || freq == GoalsContract.GoalEntry.MONTHLYGOAL){
//            // Today's quota needs to roll over to quota for the week.
//            goal.setQuotaWeek(goal.getQuotaWeek() + goal.getQuotaToday());
//            goal.setQuotaToday(0);
//        }
//        else{
//            Log.e(LOGTAG, "DailyGoalUpdate: unaccounted for frequency");
//        }
//    }
//
//    private void weeklyGoalUpdate(Goal goal){
//
//        int freq = goal.getFrequency();
//
//        if(freq == GoalsContract.GoalEntry.DAILYGOAL){
//            // Nothing needs to be done.
//        }
//        else if(freq == GoalsContract.GoalEntry.WEEKLYGOAL){
//            // If this is a weeklyGoal, we need to update the streak
//            if(goal.getQuotaWeek() >= goal.getQuota()){
//                goal.incStreak();
//            }
//            else{
//                goal.resetStreak();
//            }
//            goal.resetSessionsTally();
//            goal.setQuotaWeek(0);
//        }
//        else if(freq == GoalsContract.GoalEntry.MONTHLYGOAL){
//            // This week's quota needs to roll over to quota for the month.
//            goal.setQuotaMonth(goal.getQuotaWeek() + goal.getQuotaMonth());
//            goal.setQuotaWeek(0);
//        }
//        else{
//            Log.e(LOGTAG, "DailyGoalUpdate: unaccounted for frequency");
//        }
//    }
//
//    private void monthlyGoalUpdate(Goal goal){
//
//        int freq = goal.getFrequency();
//
//        if(freq == GoalsContract.GoalEntry.DAILYGOAL || freq == GoalsContract.GoalEntry.WEEKLYGOAL){
//            // Nothing needs to be done.
//        }
//        else if(freq == GoalsContract.GoalEntry.MONTHLYGOAL){
//            // A monthly goal might finish half way through a week. This means that what's recorded
//            // so far for the week needs to be considered in the total, and that the monthly goal
//            // must clear the weekly data.
//
//            // If this is a monthlyGoal, we need to update the streak
//            int totalQuota = goal.getQuotaWeek() + goal.getQuotaMonth();
//            if(totalQuota >= goal.getQuota()){
//                goal.incStreak();
//            }
//            else{
//                goal.resetStreak();
//            }
//            goal.resetSessionsTally();
//            goal.setQuotaWeek(0);
//            goal.setQuotaMonth(0);
//        }
//        else{
//            Log.e(LOGTAG, "DailyGoalUpdate: unaccounted for frequency");
//        }
//    }
}
