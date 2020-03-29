package com.cloud7831.goaltracker.Data;

import android.app.Application;
import android.os.AsyncTask;

import com.cloud7831.goaltracker.Objects.Goal;

import java.util.List;

import androidx.lifecycle.LiveData;

public class GoalRepository {
    private GoalDao goalDao;
    private LiveData<List<Goal>> allGoals;

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

    public LiveData<List<Goal>> getTodaysGoals() {
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
