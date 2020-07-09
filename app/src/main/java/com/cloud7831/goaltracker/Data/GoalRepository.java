package com.cloud7831.goaltracker.Data;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import com.cloud7831.goaltracker.Objects.Goal;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Semaphore;

import androidx.lifecycle.LiveData;

public class GoalRepository {
    public static final String LOGTAG = "GoalRepository";

    private GoalDao goalDao;
    private LiveData<List<Goal>> allGoals;

    private static Semaphore retrievedGoalSemaphore = new Semaphore(1);
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

    public void deleteByID(int id){
        new DeleteByIDAsyncTask(goalDao).execute(id);
    }

    public void deleteAllGoals(){
        new DeleteAllGoalsAsyncTask(goalDao).execute();
    }

    public LiveData<List<Goal>> getAllGoals() {
        return allGoals;
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

    public Goal lookupGoalByID(Integer id){
        // Search up the goal.
        retrieveGoal(id);
        // Wait until the goal has been retrieved before continuing.
        try{
            retrievedGoalSemaphore.acquire();
        } catch(InterruptedException e){
            e.printStackTrace();
        }
        retrievedGoalSemaphore.release();
        return retrievedGoal;
    }

    private static class LookupGoalAsyncTask extends AsyncTask<Integer, Void, Void>{
        private GoalDao goalDao;

        private LookupGoalAsyncTask(GoalDao goalDao){
            this.goalDao = goalDao;
        }

        @Override
        protected Void doInBackground(Integer... id){
            Goal ret = goalDao.lookupGoalByID(id[0]); // because we're being passed an array of ints
            Log.i(LOGTAG, "Goal retrieved: \n" + ret);
            retrievedGoal = ret;

            // Alert the UI thread that the goal has been retrieved.
            retrievedGoalSemaphore.release();
            return null;
        }

    }

    private void retrieveGoal(int id){
        Log.i(LOGTAG, "Entering retrieveGoal");
        try{
            retrievedGoalSemaphore.acquire();
            Log.i(LOGTAG, "RetrieveGoal permit acquired");
        } catch(InterruptedException e){
            e.printStackTrace();
        }
        // Retrieve the goal from the database.
        new LookupGoalAsyncTask(goalDao).execute(id);

    }

    private static class DeleteByIDAsyncTask extends AsyncTask<Integer, Void, Void>{
        private GoalDao goalDao;

        private DeleteByIDAsyncTask(GoalDao goalDao){
            this.goalDao = goalDao;
        }

        @Override
        protected Void doInBackground(Integer... id){
            goalDao.deleteByID(id[0]); // because we're being passed an array of goals, but our insert is just adding one goal. This means we only need to insert the first element of the array.
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
