package com.cloud7831.goaltracker.Data;

import android.app.Application;
import android.util.Log;

import com.cloud7831.goaltracker.HelperClasses.GoalLiveDataCombined;
import com.cloud7831.goaltracker.HelperClasses.TimeHelper;
import com.cloud7831.goaltracker.Objects.DailyHabit;

import java.util.Calendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import com.cloud7831.goaltracker.Data.GoalsContract.*;
import com.cloud7831.goaltracker.Objects.GoalRefactor;
import com.cloud7831.goaltracker.Objects.MonthlyHabit;
import com.cloud7831.goaltracker.Objects.Task;
import com.cloud7831.goaltracker.Objects.WeeklyHabit;
import com.cloud7831.goaltracker.Objects.Workout;

public class GoalViewModel extends AndroidViewModel {
    private static final int UPDATE_HOUR = 0; // TODO: set this to midnight -- why is this in the viewmodel???

    private GoalRepository repository;
    private LiveData<List<GoalRefactor>> allGoals;

    public GoalViewModel(@NonNull Application application){
        super(application);
        repository = new GoalRepository(application);
//        allGoals = repository.getAllGoals();
    }

    public void insert(GoalRefactor goal){
        repository.insert(goal);
    }

    public void update(GoalRefactor goal){
        repository.update(goal);
    }

    public void delete(GoalRefactor goal){
        repository.delete(goal);
    }

    public void deleteByID(int id, int type){
        repository.deleteByID(id, type);
    }

    public void deleteAllGoals(){
        repository.deleteAllGoals();
    }

    public GoalRefactor lookupGoalByID(int id, int type){
        Log.i("TEST", "starting lookup with id: " + id);
        return repository.lookupGoalByID(id, type);
    }

    public GoalLiveDataCombined getAllGoals(){
        GoalLiveDataCombined data = new GoalLiveDataCombined(repository.getAllTasks(), repository.getAllDailyHabits(), repository.getAllWeeklyHabits(), repository.getAllMonthlyHabits());

        return data;
    }

    public GoalLiveDataCombined getTodaysGoals(){
        GoalLiveDataCombined data = new GoalLiveDataCombined(repository.getTodaysTasks(), repository.getTodaysDailyHabits(), repository.getTodaysWeeklyHabits(), repository.getTodaysMonthlyHabits());

        return data;
    }

    public void createDummyGoals(){
        repository.insert(new DailyHabit("Drink Water",
                GoalEntry.PRIORITY_HIGH, 0, GoalEntry.BUILDING, 0,
                1, "ml", 1500,
                0, 0, 0, 3,
                0, 0, 0, 0));

        repository.insert(new MonthlyHabit("Read a Book", GoalEntry.PRIORITY_LOW, 0, GoalEntry.BUILDING, 0,
                1, GoalEntry.PAGES_STRING, 100,
                0, 0, 0, 4,
                0, 0, 0, 0, 0, 2));

        repository.insert(new WeeklyHabit("Push-ups", GoalEntry.PRIORITY_HIGH, 0, GoalEntry.BUILDING, 0,
                1, GoalEntry.REPS_STRING, 150,
                0, 0, 0, 6,
                0, 0, 0, 0, 2));

        repository.insert(new WeeklyHabit("Sit-ups", GoalEntry.PRIORITY_MEDIUM, 0, GoalEntry.BUILDING, 0,
                1, GoalEntry.REPS_STRING, 150,
                0, 0, 0, 5,
                0, 0, 0, 0, 1));

        repository.insert(new WeeklyHabit("Curls", GoalEntry.PRIORITY_MEDIUM, 0, GoalEntry.BUILDING, 0,
                1, GoalEntry.REPS_STRING, 100,
                0, 0, 0, 5,
                0, 0, 0, 0, 0));

        repository.insert(new WeeklyHabit("Arnold Press", GoalEntry.PRIORITY_LOW, 0, GoalEntry.BUILDING, 0,
                1, GoalEntry.REPS_STRING, 50,
                0, 0, 0, 3,
                0, 0, 0, 0, 0));

        repository.insert(new WeeklyHabit("Skull Crushers", GoalEntry.PRIORITY_MEDIUM, 0, GoalEntry.BUILDING, 0,
                1, GoalEntry.REPS_STRING, 120,
                0, 0, 0, 4,
                0, 0, 0, 0, 0));

        repository.insert(new WeeklyHabit("Dips", GoalEntry.PRIORITY_MEDIUM, 0, GoalEntry.BUILDING, 0,
                1, GoalEntry.REPS_STRING, 75,
                0, 0, 0, 3,
                0, 0, 0, 0, 0));

        repository.insert(new WeeklyHabit("Pull-ups", GoalEntry.PRIORITY_HIGH, 0, GoalEntry.BUILDING, 0,
                1, GoalEntry.REPS_STRING, 30,
                0, 0, 0, 5,
                0, 0, 0, 0, 1));

        repository.insert(new WeeklyHabit("Chin-ups", GoalEntry.PRIORITY_HIGH, 0, GoalEntry.BUILDING, 0,
                1, GoalEntry.REPS_STRING, 25,
                0, 0, 0, 5,
                0, 0, 0, 0, 1));

        repository.insert(new WeeklyHabit("Flies", GoalEntry.PRIORITY_VERY_LOW, 0, GoalEntry.BUILDING, 0,
                1, GoalEntry.REPS_STRING, 30,
                0, 0, 0, 3,
                0, 0, 0, 0, 0));

        repository.insert(new WeeklyHabit("Squats", GoalEntry.PRIORITY_HIGH, 0, GoalEntry.BUILDING, 0,
                1, GoalEntry.REPS_STRING, 120,
                0, 0, 0, 4,
                0, 0, 0, 0, 0));

        repository.insert(new WeeklyHabit("Split Squats", GoalEntry.PRIORITY_LOW, 0, GoalEntry.BUILDING, 0,
                1, GoalEntry.REPS_STRING, 60,
                0, 0, 0, 2,
                0, 0, 0, 0, 0));

        repository.insert(new WeeklyHabit("Calf Raise", GoalEntry.PRIORITY_LOW, 0, GoalEntry.BUILDING, 0,
                1, GoalEntry.REPS_STRING, 100,
                0, 0, 0, 3,
                0, 0, 0, 0, 0));

        repository.insert(new WeeklyHabit("Crunches", GoalEntry.PRIORITY_HIGH, 0, GoalEntry.BUILDING, 0,
                1, GoalEntry.REPS_STRING, 120,
                0, 0, 0, 4,
                0, 0, 0, 0, 1));

        repository.insert(new WeeklyHabit("Heel Touch", GoalEntry.PRIORITY_MEDIUM, 0, GoalEntry.BUILDING, 0,
                1, GoalEntry.REPS_STRING, 40,
                0, 0, 0, 2,
                0, 0, 0, 0, 0));

        repository.insert(new WeeklyHabit("Russian Twist", GoalEntry.PRIORITY_MEDIUM, 0, GoalEntry.BUILDING, 0,
                1, GoalEntry.REPS_STRING, 80,
                0, 0, 0, 2,
                0, 0, 0, 0, 2));

        repository.insert(new WeeklyHabit("Leg Lifts", GoalEntry.PRIORITY_MEDIUM, 0, GoalEntry.BUILDING, 0,
                1, GoalEntry.REPS_STRING, 30,
                0, 0, 0, 2,
                0, 0, 0, 0, 0));

        repository.insert(new DailyHabit("Wanikani", GoalEntry.PRIORITY_VERY_HIGH, 0, GoalEntry.BUILDING, 0,
                0, GoalEntry.TIMES_STRING, 1,
                0, 0, 0, 1,
                0, 0, 0, 147));

        repository.insert(new WeeklyHabit("Grammar Practice", GoalEntry.PRIORITY_VERY_HIGH, 0, GoalEntry.BUILDING, 0,
                1, GoalEntry.HOUR_STRING, 10800,
                0, 0, 0, 6,
                0, 0, 0, 0, 3));

        repository.insert(new WeeklyHabit("Run", GoalEntry.PRIORITY_MEDIUM, 0, GoalEntry.BUILDING, 0,
                1, GoalEntry.HOUR_STRING, 60*60,
                0, 0, 0, 3,
                0, 0, 0, 0, 0));

        repository.insert(new WeeklyHabit("Flips", GoalEntry.PRIORITY_LOW, 0, GoalEntry.BUILDING, 0,
                1, GoalEntry.REPS_STRING, 150,
                0, 0, 0, 3,
                0, 0, 0, 0, 0));

        repository.insert(new WeeklyHabit("Android Dev", GoalEntry.PRIORITY_MEDIUM, 0, GoalEntry.BUILDING, 0,
                1, GoalEntry.HOUR_STRING, 40*60*60,
                0, 0, 0, 80,
                0, 0, 0, 0, 0));

        repository.insert(new DailyHabit("Posture Exercise", GoalEntry.PRIORITY_HIGH, 0, GoalEntry.BUILDING, 0,
                0, GoalEntry.TIMES_STRING, 1,
                0, 0, 0, 1,
                0, 0, 0, 0));

        repository.insert(new DailyHabit("Sleep Before Midnight", GoalEntry.PRIORITY_MEDIUM, 0, GoalEntry.BUILDING, 0,
                0, GoalEntry.TIMES_STRING, 1,
                0, 0, 0, 1,
                0, 0, 0, 0));

        repository.insert(new DailyHabit("ShakingSats", GoalEntry.PRIORITY_VERY_HIGH, 0, GoalEntry.BUILDING, 0,
                0, GoalEntry.TIMES_STRING, 1,
                0, 0, 0, 1,
                0, 0, 0, 10));

        repository.insert(new MonthlyHabit("Clean Room", GoalEntry.PRIORITY_MEDIUM, 0, GoalEntry.BUILDING, 0,
                0, GoalEntry.TIMES_STRING, 1,
                0, 0, 0, 1,
                0, 0, 0, 0, 0, 0));

        repository.insert(new Task("Buy Dress Shoes", GoalEntry.PRIORITY_LOW, 0, GoalEntry.BUILDING, 0,
                0, GoalEntry.TIMES_STRING, 1,
                0, 0, 0, 1,
                0, 0, 0));
    }



}
