package com.cloud7831.goaltracker.Data;

import android.app.Application;
import android.util.Log;

import com.cloud7831.goaltracker.HelperClasses.TimeHelper;
import com.cloud7831.goaltracker.Objects.Goal;

import java.util.Calendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.cloud7831.goaltracker.Data.GoalsContract.*;

public class GoalViewModel extends AndroidViewModel {
    private static final int UPDATE_HOUR = 0; // TODO: set this to midnight

    private GoalRepository repository;
    private LiveData<List<Goal>> allGoals;

    public GoalViewModel(@NonNull Application application){
        super(application);
        repository = new GoalRepository(application);
        allGoals = repository.getAllGoals();
    }

    public void insert(Goal goal){
        repository.insert(goal);
    }

    public void update(Goal goal){
        repository.update(goal);
    }

    public void delete(Goal goal){
        repository.delete(goal);
    }

    public void deleteByID(int id){
        repository.deleteByID(id);
    }

    public void deleteAllGoals(){
        repository.deleteAllGoals();
    }

    public Goal lookupGoalByID(int id){
        Log.i("TEST", "starting lookup with id: " + id);
        return repository.lookupGoalByID(id);
    }

    public void createDummyGoals(){
        repository.insert(new Goal("Drink Water",
                GoalEntry.HABIT, GoalEntry.BUILDING, GoalEntry.PRIORITY_HIGH, 0,
                1, "ml", 2250,
                GoalEntry.DAILYGOAL, 0, 0, 0, 3,
                0, 9, 0, 0, 0, 0, 0));

        repository.insert(new Goal("Read a Book",
                GoalEntry.HABIT, GoalEntry.BUILDING, GoalEntry.PRIORITY_HIGH, 0,
                1, GoalEntry.PAGES_STRING, 25,
                GoalEntry.WEEKLYGOAL, 0, 0, 0, 1,
                0, 0, 0, 0, 0, 0, 0));

        repository.insert(new Goal("Push-ups",
                GoalEntry.HABIT, GoalEntry.BUILDING, GoalEntry.PRIORITY_MEDIUM, 0,
                1, GoalEntry.REPS_STRING, 175,
                GoalEntry.WEEKLYGOAL, 0, 0, 0, 4,
                0, 1, 0, 0, 0, 0, 0));

        repository.insert(new Goal("Ab Exercises",
                GoalEntry.HABIT, GoalEntry.BUILDING, GoalEntry.PRIORITY_LOW, 0,
                1, GoalEntry.REPS_STRING, 300,
                GoalEntry.WEEKLYGOAL, 0, 0, 0, 4,
                0, 0, 0, 0, 0, 0, 0));

        repository.insert(new Goal("Kanji Practice",
                GoalEntry.HABIT, GoalEntry.BUILDING, GoalEntry.PRIORITY_MEDIUM, 0,
                1, GoalEntry.MINUTE_STRING, 10*60,
                GoalEntry.DAILYGOAL, 0, 0, 0, 1,
                0, 4, 0, 0, 0, 0, 0));

        repository.insert(new Goal("Run",
                GoalEntry.HABIT, GoalEntry.BUILDING, GoalEntry.PRIORITY_MEDIUM, 0,
                1, GoalEntry.MINUTE_STRING, 60 * 60,
                GoalEntry.WEEKLYGOAL, 0, 0, 0, 3,
                0, 1, 0, 0, 0, 0, 0));

        repository.insert(new Goal("Japanese Vocab",
                GoalEntry.HABIT, GoalEntry.BUILDING, GoalEntry.PRIORITY_MEDIUM, 0,
                1, "word", 25,
                GoalEntry.WEEKLYGOAL, 0, 0, 0, 3,
                0, 1, 0, 0, 0, 0, 0));

        repository.insert(new Goal("Japanese Grammar",
                GoalEntry.HABIT, GoalEntry.BUILDING, GoalEntry.PRIORITY_MEDIUM, 1,
                1, GoalEntry.HOUR_STRING, 60*60,
                GoalEntry.WEEKLYGOAL, 0, 0, 0, 4,
                0, 1, 0, 0, 0, 0, 0));

        repository.insert(new Goal("Android Dev",
                GoalEntry.HABIT, GoalEntry.BUILDING, GoalEntry.PRIORITY_MEDIUM, 0,
                1, GoalEntry.HOUR_STRING, 7*60*60,
                GoalEntry.WEEKLYGOAL, 0, 0, 0, 4,
                0, 1, 0, 0, 0, 0, 0));

        repository.insert(new Goal("Workout",
                GoalEntry.HABIT, GoalEntry.BUILDING, GoalEntry.PRIORITY_VERY_HIGH, 0,
                1, GoalEntry.HOUR_STRING, 7*60*60,
                GoalEntry.WEEKLYGOAL, 0, 0, 0, 5,
                0, 1, 0, 0, 0, 0, 0));

        repository.insert(new Goal("Reflect",
                GoalEntry.HABIT, GoalEntry.BUILDING, GoalEntry.PRIORITY_VERY_LOW, 0,
                0, "", 1,
                GoalEntry.DAILYGOAL, 0, 0, 0, 1,
                0, 4, 0, 0, 0, 0, 0));

        repository.insert(new Goal("Posture Exercise",
                GoalEntry.HABIT, GoalEntry.BUILDING, GoalEntry.PRIORITY_MEDIUM, 0,
                0, GoalEntry.TIMES_STRING, 10,
                GoalEntry.WEEKLYGOAL, 0, 0, 0, 10,
                0, 1, 0, 0, 0, 0, 0));

        repository.insert(new Goal("Sleep Before Midnight",
                GoalEntry.HABIT, GoalEntry.BUILDING, GoalEntry.PRIORITY_MEDIUM, 0,
                0, GoalEntry.UNITS_NULL, 1,
                GoalEntry.DAILYGOAL, 0, 0, 0, 1,
                0, 1, 0, 0, 0, 0, 0));

        repository.insert(new Goal("Clean Room",
                GoalEntry.HABIT, GoalEntry.BUILDING, GoalEntry.PRIORITY_LOW, 0,
                0, GoalEntry.UNITS_NULL, 1,
                GoalEntry.WEEKLYGOAL, 0, 0, 0, 1,
                0, 1, 0, 0, 0, 0, 0));

        // Some test Tasks
        repository.insert(new Goal("Buy New Shoes",
                GoalEntry.TASK, GoalEntry.UNDEFINED, GoalEntry.PRIORITY_LOW, 0,
                0, GoalEntry.UNITS_NULL, 1,
                GoalEntry.UNDEFINED, 0, 0, 0, 1,
                0, 1, 0, 0, 0, 0, 0));
    }

    public LiveData<List<Goal>> getAllGoals(){
        return allGoals;
    }

    public LiveData<List<Goal>> getTodaysGoals(){
        return repository.getTodaysGoals();
    }


}
