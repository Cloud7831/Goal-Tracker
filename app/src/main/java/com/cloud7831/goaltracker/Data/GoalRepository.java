package com.cloud7831.goaltracker.Data;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import com.cloud7831.goaltracker.HelperClasses.ArrayStorageHelper;
import com.cloud7831.goaltracker.Objects.Goals.DailyHabit;
import com.cloud7831.goaltracker.Objects.Goals.Goal;
import com.cloud7831.goaltracker.Objects.Goals.MonthlyHabit;
import com.cloud7831.goaltracker.Objects.Goals.Task;
import com.cloud7831.goaltracker.Objects.Goals.WeeklyHabit;
import com.cloud7831.goaltracker.Objects.Goals.WorkoutRelated.Workout;
import com.cloud7831.goaltracker.Data.GoalsContract.*;

import java.util.List;
import java.util.concurrent.Semaphore;

import androidx.lifecycle.LiveData;

public class GoalRepository {
    public static final String LOGTAG = "GoalRepository";

    private GoalDao goalDao;
    private ExerciseDao exerciseDao;
//    private LiveData<List<GoalRefactor>> allGoals;

    private static Semaphore retrievedGoalSemaphore = new Semaphore(1);
    private static Goal retrievedGoal;

    public GoalRepository(Application application){
        GoalDatabase database = GoalDatabase.getInstance(application);
        goalDao = database.goalDao();
        exerciseDao = database.exerciseDao();
//        allGoals = goalDao.getAllGoals();
    }

    //region INSERT ---------------------------------------------------------------------------

    public void insert(Goal goal){
        new InsertGoalAsyncTask(goalDao).execute(goal);
    }

    private static class InsertGoalAsyncTask extends AsyncTask<Goal, Void, Void>{
        private GoalDao goalDao;

        private InsertGoalAsyncTask(GoalDao goalDao){
            this.goalDao = goalDao;
        }

        @Override
        protected Void doInBackground(Goal... goals){
            // because we're being passed an array of goals, but our insert is just adding one
            // goal. This means we only need to insert the first element of the array.

            goals[0].insertGoalInDB(goalDao);

            return null;
        }
    }
    //endregion INSERT ---------------------------------------------------------------------------

    //region UPDATE ---------------------------------------------------------------------------

    public void update(Goal goal){
        new UpdateGoalAsyncTask(goalDao).execute(goal);
    }

    private static class UpdateGoalAsyncTask extends AsyncTask<Goal, Void, Void>{
        private GoalDao goalDao;

        private UpdateGoalAsyncTask(GoalDao goalDao){
            this.goalDao = goalDao;
        }

        @Override
        protected Void doInBackground(Goal... goals){
            // because we're being passed an array of goals, but our insert is just adding one
            // goal. This means we only need to insert the first element of the array.

            goals[0].updateGoalInDB(goalDao);

            return null;
        }
    }
    //endregion UPDATE ---------------------------------------------------------------------------

    //region DELETE ---------------------------------------------------------------------------
    public void delete(Goal goal){
        new DeleteGoalAsyncTask(goalDao).execute(goal);
    }

    private static class DeleteGoalAsyncTask extends AsyncTask<Goal, Void, Void>{
        private GoalDao goalDao;

        private DeleteGoalAsyncTask(GoalDao goalDao){
            this.goalDao = goalDao;
        }

        @Override
        protected Void doInBackground(Goal... goals){
            // because we're being passed an array of goals, but our insert is just adding one
            // goal. This means we only need to insert the first element of the array.

            goals[0].deleteGoalInDB(goalDao);
            return null;
        }
    }
    //endregion DELETE ---------------------------------------------------------------------------

    //region DELETE_BY_ID ---------------------------------------------------------------------
    public void deleteByID(int id, int type){
        new DeleteByIDAsyncTask(goalDao).execute(new IdTypePair(id, type));
    }

    private static class DeleteByIDAsyncTask extends AsyncTask<IdTypePair, Void, Void>{
        private GoalDao goalDao;

        private DeleteByIDAsyncTask(GoalDao goalDao){
            this.goalDao = goalDao;
        }

        @Override
        protected Void doInBackground(IdTypePair... pair){
            // TODO: should I use a semaphore?
            if(pair[0].getType() == GoalsContract.GoalEntry.TYPE_TASK){
                goalDao.deleteTaskByID(pair[0].getId()); // because we're being passed an array of ints
            }
            else if(pair[0].getType() == GoalsContract.GoalEntry.TYPE_DAILYGOAL){
                goalDao.deleteDailyHabitByID(pair[0].getId()); // because we're being passed an array of ints
            }
            else if(pair[0].getType() == GoalsContract.GoalEntry.TYPE_WEEKLYGOAL){
                goalDao.deleteWeeklyHabitByID(pair[0].getId()); // because we're being passed an array of ints
            }
            else if(pair[0].getType() == GoalsContract.GoalEntry.TYPE_MONTHLYGOAL){
                goalDao.deleteMonthlyHabitByID(pair[0].getId()); // because we're being passed an array of ints
            }
            else{
                Log.e(LOGTAG, "Delete goal by id tried to delete an unknown goal type.");
            }
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
            goalDao.deleteAllWeeklyHabits();
            goalDao.deleteAllMonthlyHabits();
            // TODO: delete the rest of the tables
            return null;
        }
    }
    //endregion DELETE_ALL_GOALS -----------------------------------------------------------------

//    public LiveData<List<GoalRefactor>> getAllGoals() {
//        // TODO: this will need to use a mediatorLiveData to combine all the sources.
//        return allGoals;
//    }

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

    public LiveData<List<Task>> getAllTasks() {
        //TODO: put this in an async task.

        return goalDao.getAllTasks();
    }

    public LiveData<List<DailyHabit>> getAllDailyHabits() {
        //TODO: put this in an async task.

        return goalDao.getAllDailyHabits();
    }

    public LiveData<List<WeeklyHabit>> getAllWeeklyHabits() {
        //TODO: put this in an async task.

        return goalDao.getAllWeeklyHabits();
    }

    public LiveData<List<MonthlyHabit>> getAllMonthlyHabits() {
        //TODO: put this in an async task.

        return goalDao.getAllMonthlyHabits();
    }

//
//    public LiveData<List<Workout>> getTodaysWorkouts() {
//        //TODO: put this in an async task.
//
//        return goalDao.getTodaysWorkouts();
//    }

    public Goal lookupGoalByID(int id, int type){
        // Wait until the goal has been retrieved before continuing.
        // TODO: eventually get rid of the semaphore for a callback.
        Log.i(LOGTAG, "starting lookup in the repo: " + id + ", " + type);
        try{
            retrievedGoalSemaphore.acquire();
        } catch(InterruptedException e){
            e.printStackTrace();
        }
        // Retrieve the goal from the database.
        new LookupGoalAsyncTask(goalDao).execute(new IdTypePair(id, type));

        try{
            retrievedGoalSemaphore.acquire();
        } catch(InterruptedException e){
            e.printStackTrace();
        }
        retrievedGoalSemaphore.release();

        return retrievedGoal;
    }

    private static class LookupGoalAsyncTask extends AsyncTask<IdTypePair, Void, Void>{
        private GoalDao goalDao;

        private LookupGoalAsyncTask(GoalDao goalDao){
            this.goalDao = goalDao;
        }

        @Override
        protected Void doInBackground(IdTypePair... pair){
            if(pair[0].getType() == GoalEntry.TYPE_TASK){
                retrievedGoal = goalDao.lookupTaskByID(pair[0].getId()); // because we're being passed an array of ints
            }
            else if(pair[0].getType() == GoalEntry.TYPE_DAILYGOAL){
                retrievedGoal = goalDao.lookupDailyHabitByID(pair[0].getId()); // because we're being passed an array of ints
            }
            else if(pair[0].getType() == GoalEntry.TYPE_WEEKLYGOAL){
                retrievedGoal = goalDao.lookupWeeklyHabitByID(pair[0].getId()); // because we're being passed an array of ints
            }
            else if(pair[0].getType() == GoalEntry.TYPE_MONTHLYGOAL){
                retrievedGoal = goalDao.lookupMonthlyHabitByID(pair[0].getId()); // because we're being passed an array of ints
            }
            else{
                Log.e(LOGTAG, "Lookup goal tried to look up an unknown goal type.");
            }

            // Alert the UI thread that the goal has been retrieved.
            retrievedGoalSemaphore.release();
            return null;
        }

    }

    private class IdTypePair {
        int id;
        int type;

        IdTypePair(int id, int type){
            this.id = id;
            this.type = type;
        }

        private int getId(){
            return id;
        }

        private int getType(){
            return type;
        }
    }

    public void getExerciseEntries(Workout workout){

    }

    public void getExercises(Workout workout){

    }

    private static class LookupExercisesAsyncTask extends AsyncTask<Workout, Void, Void>{
        private ExerciseDao exerciseDao;

        private LookupExercisesAsyncTask(ExerciseDao exerciseDao){
            this.exerciseDao = exerciseDao;
        }

        @Override
        protected Void doInBackground(Workout... workouts){
            int[] data = ArrayStorageHelper.convertStrArrToInt(workouts[0].getExerciseStorageList());
            int endPoint = data.length/2;
            workouts[0].setTotalItems(endPoint);

            // TODO: retrieve each exercise based on the data.
            for(int i = 0; i < endPoint; i++){
                if(data[i*2] == GoalEntry.FREQ_DAILY){
                    workouts[0].appendExercise(exerciseDao.lookupDailyExerciseByID(data[i*2 + 1]));
                }
                else if(data[i*2] == GoalEntry.FREQ_WEEKLY){
                    workouts[0].appendExercise(exerciseDao.lookupWeeklyExerciseByID(data[i*2 + 1]));
                }
                else if(data[i*2] == GoalEntry.FREQ_MONTHLY){
                    workouts[0].appendExercise(exerciseDao.lookupMonthlyExerciseByID(data[i*2 + 1]));
                }
                else{
                    Log.e(LOGTAG, "Lookup exercise tried to look up an unknown exercise frequency type.");
                }
            }
            return null;
        }

    }
}