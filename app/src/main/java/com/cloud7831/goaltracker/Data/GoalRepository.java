package com.cloud7831.goaltracker.Data;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import com.cloud7831.goaltracker.Objects.DailyHabit;
import com.cloud7831.goaltracker.Objects.GoalRefactor;
import com.cloud7831.goaltracker.Objects.MonthlyHabit;
import com.cloud7831.goaltracker.Objects.Task;
import com.cloud7831.goaltracker.Objects.WeeklyHabit;
import com.cloud7831.goaltracker.Objects.Workout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

public class GoalRepository {
    public static final String LOGTAG = "GoalRepository";

    private GoalDao goalDao;
//    private TaskDao taskDao;
    // TODO: make a dailyhabit, weeklyhabit and monthlyhabit dao
    private LiveData<List<GoalRefactor>> allGoals;

    private static Semaphore retrievedGoalSemaphore = new Semaphore(1);
    private static GoalRefactor retrievedGoal;

    public GoalRepository(Application application){
        GoalDatabase database = GoalDatabase.getInstance(application);
        goalDao = database.goalDao();
//        allGoals = initAllGoals(); // TODO:
//        allGoals = goalDao.getAllGoals();
    }

    //region INSERT ---------------------------------------------------------------------------

    public void insert(GoalRefactor goal){
        new InsertGoalAsyncTask(goalDao).execute(goal);
    }

    private static class InsertGoalAsyncTask extends AsyncTask<GoalRefactor, Void, Void>{
        private GoalDao goalDao;

        private InsertGoalAsyncTask(GoalDao goalDao){
            this.goalDao = goalDao;
        }

        @Override
        protected Void doInBackground(GoalRefactor... goals){
            // because we're being passed an array of goals, but our insert is just adding one
            // goal. This means we only need to insert the first element of the array.

            if(goals[0] instanceof DailyHabit){
                goalDao.insert((DailyHabit)goals[0]);
            }
            else if(goals[0] instanceof Task){
                goalDao.insert((Task)goals[0]);
            }
            else{
                Log.e(LOGTAG, "There was an unknown type of goal when trying to insert");
            }

            return null;
        }
    }
    //endregion INSERT ---------------------------------------------------------------------------

    //region UPDATE ---------------------------------------------------------------------------

    public void update(GoalRefactor goal){
        new UpdateGoalAsyncTask(goalDao).execute(goal);
    }

    private static class UpdateGoalAsyncTask extends AsyncTask<GoalRefactor, Void, Void>{
        private GoalDao goalDao;

        private UpdateGoalAsyncTask(GoalDao goalDao){
            this.goalDao = goalDao;
        }

        @Override
        protected Void doInBackground(GoalRefactor... goals){
            // because we're being passed an array of goals, but our insert is just adding one
            // goal. This means we only need to insert the first element of the array.

            if(goals[0] instanceof DailyHabit){
                goalDao.update((DailyHabit)goals[0]);
            }
            else if(goals[0] instanceof Task){
                goalDao.update((Task)goals[0]);
            }
            else{
                Log.e(LOGTAG, "There was an unknown type of goal when trying to insert");
            }

            return null;
        }
    }
    //endregion UPDATE ---------------------------------------------------------------------------

    //region DELETE ---------------------------------------------------------------------------
    public void delete(GoalRefactor goal){
        new DeleteGoalAsyncTask(goalDao).execute(goal);
    }

    private static class DeleteGoalAsyncTask extends AsyncTask<GoalRefactor, Void, Void>{
        private GoalDao goalDao;

        private DeleteGoalAsyncTask(GoalDao goalDao){
            this.goalDao = goalDao;
        }

        @Override
        protected Void doInBackground(GoalRefactor... goals){
            // because we're being passed an array of goals, but our insert is just adding one
            // goal. This means we only need to insert the first element of the array.

            if(goals[0] instanceof DailyHabit){
                goalDao.delete((DailyHabit)goals[0]);
            }
            else if(goals[0] instanceof Task){
                goalDao.delete((Task)goals[0]);
            }
            else{
                Log.e(LOGTAG, "There was an unknown type of goal when trying to insert");
            }
            return null;
        }
    }
    //endregion DELETE ---------------------------------------------------------------------------

    //region DELETE_BY_ID ---------------------------------------------------------------------
    public void deleteByID(int id){
        new DeleteByIDAsyncTask(goalDao).execute(id);
    }

    private static class DeleteByIDAsyncTask extends AsyncTask<Integer, Void, Void>{
        private GoalDao goalDao;

        private DeleteByIDAsyncTask(GoalDao goalDao){
            this.goalDao = goalDao;
        }

        @Override
        protected Void doInBackground(Integer... id){
            // TODO: this function rewrite is going to be a bit more tricky, because I don't
            //  know what type goal is or what table to delete from.
//            goalDao.deleteByID(id[0]); // because we're being passed an array of goals, but our insert is just adding one goal. This means we only need to insert the first element of the array.
            return null;
        }
    }
    //endregion DELETE_BY_ID ---------------------------------------------------------------------

    //region DELETE_ALL_GOALS -----------------------------------------------------------------
    public void deleteAllGoals(){
        new DeleteAllGoalsAsyncTask(goalDao).execute();
    }

    private static class DeleteAllGoalsAsyncTask extends AsyncTask<Void, Void, Void>{
        private GoalDao goalDao;

        private DeleteAllGoalsAsyncTask(GoalDao goalDao){
            this.goalDao = goalDao;
        }

        @Override
        protected Void doInBackground(Void... voids){
            goalDao.deleteAllTasks();
            goalDao.deleteAllDailyHabits();
            // TODO: delete the rest of the tables
            return null;
        }
    }
    //endregion DELETE_ALL_GOALS -----------------------------------------------------------------

    public LiveData<List<GoalRefactor>> getAllGoals() {
        return allGoals;
    }

//    public LiveData<List<GoalRefactor>> getTodaysGoals() {
//        //TODO: put this in an async task.
//
//        LiveData<List<Task>> taskLiveData = goalDao.getTodaysTasks();
//
//        final MediatorLiveData<List<GoalRefactor>> retList = new MediatorLiveData<>();
//        retList.addSource(taskLiveData, value -> retList.setValue(value));
//
//        return goalDao.getTodaysGoals();
//    }

    public LiveData<List<Task>> getTodaysTasks() {
        //TODO: put this in an async task.

        return goalDao.getTodaysTasks();
    }

    public LiveData<List<DailyHabit>> getTodaysDailyHabits() {
        //TODO: put this in an async task.

        return goalDao.getTodaysDailyHabits();
    }

    public LiveData<List<WeeklyHabit>> getTodaysWeeklyHabits() {
        //TODO: put this in an async task.

        return goalDao.getTodaysWeeklyHabits();
    }

    public LiveData<List<MonthlyHabit>> getTodaysMonthlyHabits() {
        //TODO: put this in an async task.

        return goalDao.getTodaysMonthlyHabits();
    }
//
//    public LiveData<List<Workout>> getTodaysWorkouts() {
//        //TODO: put this in an async task.
//
//        return goalDao.getTodaysWorkouts();
//    }

    public GoalRefactor lookupGoalByID(Integer id){
        // Wait until the goal has been retrieved before continuing.
        try{
            retrievedGoalSemaphore.acquire();
        } catch(InterruptedException e){
            e.printStackTrace();
        }
        // Retrieve the goal from the database.
        new LookupGoalAsyncTask(goalDao).execute(id);

        return retrievedGoal;
    }

    private static class LookupGoalAsyncTask extends AsyncTask<Integer, Void, Void>{
        private GoalDao goalDao;

        private LookupGoalAsyncTask(GoalDao goalDao){
            this.goalDao = goalDao;
        }

        @Override
        protected Void doInBackground(Integer... id){
            GoalRefactor ret = goalDao.lookupGoalByID(id[0]); // because we're being passed an array of ints
            retrievedGoal = ret;

            // Alert the UI thread that the goal has been retrieved.
            retrievedGoalSemaphore.release();
            return null;
        }

    }

}


//public class GoalRepository {
//    public static final String LOGTAG = "GoalRepository";
//
//    private GoalDao goalDao;
//    private LiveData<List<GoalRefactor>> allGoals;
//
//    private static Semaphore retrievedGoalSemaphore = new Semaphore(1);
//    private static GoalRefactor retrievedGoal;
//
//    public GoalRepository(Application application){
//        GoalDatabase database = GoalDatabase.getInstance(application);
//        goalDao = database.goalDao();
//        allGoals = goalDao.getAllGoals();
//    }
//
//    public void insert(GoalRefactor goal){
//        new InsertGoalAsyncTask(goalDao).execute(goal);
//    }
//
//    public void update(GoalRefactor goal){
//        new UpdateGoalAsyncTask(goalDao).execute(goal);
//    }
//
//    public void delete(GoalRefactor goal){
//        new DeleteGoalAsyncTask(goalDao).execute(goal);
//    }
//
//    public void deleteByID(int id){
//        new DeleteByIDAsyncTask(goalDao).execute(id);
//    }
//
//    public void deleteAllGoals(){
//        new DeleteAllGoalsAsyncTask(goalDao).execute();
//    }
//
//    public LiveData<List<GoalRefactor>> getAllGoals() {
//        return allGoals;
//    }
//
//    public LiveData<List<GoalRefactor>> getTodaysGoals() {
//        //TODO: put this in an async task.
//        return goalDao.getTodaysGoals();
//    }
//
//    private static class InsertGoalAsyncTask extends AsyncTask<GoalRefactor, Void, Void>{
//        private GoalDao goalDao;
//
//        private InsertGoalAsyncTask(GoalDao goalDao){
//            this.goalDao = goalDao;
//        }
//
//        @Override
//        protected Void doInBackground(GoalRefactor... goals){
//            goalDao.insert(goals[0]); // because we're being passed an array of goals, but our insert is just adding one goal. This means we only need to insert the first element of the array.
//            return null;
//        }
//    }
//
//    private static class UpdateGoalAsyncTask extends AsyncTask<GoalRefactor, Void, Void>{
//        private GoalDao goalDao;
//
//        private UpdateGoalAsyncTask(GoalDao goalDao){
//            this.goalDao = goalDao;
//        }
//
//        @Override
//        protected Void doInBackground(GoalRefactor... goals){
//            goalDao.update(goals[0]); // because we're being passed an array of goals, but our insert is just adding one goal. This means we only need to insert the first element of the array.
//            return null;
//        }
//    }
//
//    public GoalRefactor lookupGoalByID(Integer id){
//        // Search up the goal.
//        retrieveGoal(id);
//        // Wait until the goal has been retrieved before continuing.
//        try{
//            retrievedGoalSemaphore.acquire();
//        } catch(InterruptedException e){
//            e.printStackTrace();
//        }
//        retrievedGoalSemaphore.release();
//        return retrievedGoal;
//    }
//
//    private static class LookupGoalAsyncTask extends AsyncTask<Integer, Void, Void>{
//        private GoalDao goalDao;
//
//        private LookupGoalAsyncTask(GoalDao goalDao){
//            this.goalDao = goalDao;
//        }
//
//        @Override
//        protected Void doInBackground(Integer... id){
//            GoalRefactor ret = goalDao.lookupGoalByID(id[0]); // because we're being passed an array of ints
//            retrievedGoal = ret;
//
//            // Alert the UI thread that the goal has been retrieved.
//            retrievedGoalSemaphore.release();
//            return null;
//        }
//
//    }
//
//    private void retrieveGoal(int id){
//        try{
//            retrievedGoalSemaphore.acquire();
//        } catch(InterruptedException e){
//            e.printStackTrace();
//        }
//        // Retrieve the goal from the database.
//        new LookupGoalAsyncTask(goalDao).execute(id);
//
//    }
//
//    private static class DeleteByIDAsyncTask extends AsyncTask<Integer, Void, Void>{
//        private GoalDao goalDao;
//
//        private DeleteByIDAsyncTask(GoalDao goalDao){
//            this.goalDao = goalDao;
//        }
//
//        @Override
//        protected Void doInBackground(Integer... id){
//            goalDao.deleteByID(id[0]); // because we're being passed an array of goals, but our insert is just adding one goal. This means we only need to insert the first element of the array.
//            return null;
//        }
//    }
//
//    private static class DeleteGoalAsyncTask extends AsyncTask<GoalRefactor, Void, Void>{
//        private GoalDao goalDao;
//
//        private DeleteGoalAsyncTask(GoalDao goalDao){
//            this.goalDao = goalDao;
//        }
//
//        @Override
//        protected Void doInBackground(GoalRefactor... goals){
//            goalDao.delete(goals[0]); // because we're being passed an array of goals, but our insert is just adding one goal. This means we only need to insert the first element of the array.
//            return null;
//        }
//    }
//
//    private static class DeleteAllGoalsAsyncTask extends AsyncTask<GoalRefactor, Void, Void>{
//        private GoalDao goalDao;
//
//        private DeleteAllGoalsAsyncTask(GoalDao goalDao){
//            this.goalDao = goalDao;
//        }
//
//        @Override
//        protected Void doInBackground(GoalRefactor... goals){
//            goalDao.deleteAllGoals();
//            return null;
//        }
//    }
//
//}