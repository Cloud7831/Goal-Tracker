package com.cloud7831.goaltracker.Data;

import android.app.Application;

import com.cloud7831.goaltracker.Objects.Goal;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.cloud7831.goaltracker.Data.GoalsContract.*;

public class GoalViewModel extends AndroidViewModel {

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

    public void deleteAllGoals(){
        repository.deleteAllGoals();
    }

    public void createDummyGoals(){
        repository.insert(new Goal("Study Japanese",
                GoalEntry.HABIT, GoalEntry.BUILDING, 3,
                1,1, GoalEntry.HOUR_STRING, 10*60*60,
                GoalEntry.WEEKLYGOAL, 0, 60*60, 0, 4,
                0, 10, 0, 0, 0, 0, 0));

        repository.insert(new Goal("Drink Water",
                GoalEntry.TASK, GoalEntry.BUILDING, 4, 0,
                0, "", 1,
                GoalEntry.DAILYGOAL, 0, 0, 0, 1,
                0, 33, 0, 0, 0, 0, 0));

        repository.insert(new Goal("Read a Book",
                GoalEntry.HABIT, GoalEntry.BUILDING, 4, 0,
                0, "", 1,
                GoalEntry.DAILYGOAL, 0, 0, 0, 1,
                0, 33, 0, 0, 0, 0, 0));
    }

    public LiveData<List<Goal>> getAllGoals(){
        return allGoals;
    }

    public LiveData<List<Goal>> getTodaysGoals(){
        return repository.getTodaysGoals();
    }


}
