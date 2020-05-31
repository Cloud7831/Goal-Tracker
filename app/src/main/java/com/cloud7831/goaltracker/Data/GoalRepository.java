package com.cloud7831.goaltracker.Data;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import com.cloud7831.goaltracker.Objects.Goal;

import java.util.List;

import androidx.lifecycle.LiveData;

public class GoalRepository {
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

    public void dailyUpdate(){
        new DailyGoalUpdateAsyncTask(goalDao).execute();
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

    private static class DailyGoalUpdateAsyncTask extends AsyncTask<Goal, Void, Void>{
        private GoalDao goalDao;

        private DailyGoalUpdateAsyncTask(GoalDao goalDao){
            this.goalDao = goalDao;
        }

        @Override
        protected Void doInBackground(Goal... goals){
            goalDao.update(goals[0]);
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
}
