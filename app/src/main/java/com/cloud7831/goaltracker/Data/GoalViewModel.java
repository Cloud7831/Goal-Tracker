package com.cloud7831.goaltracker.Data;

import android.app.Application;

import com.cloud7831.goaltracker.Objects.Goal;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

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

    public LiveData<List<Goal>> getAllGoals(){
        return allGoals;
    }

    public LiveData<List<Goal>> getTodaysGoals(){
        return repository.getTodaysGoals();
    }


}
