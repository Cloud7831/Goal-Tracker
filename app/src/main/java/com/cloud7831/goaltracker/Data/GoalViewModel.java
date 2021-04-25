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
//        Log.i("TEST", "starting lookup with id: " + id);
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

//        repository.insert(new Goal("Read a Book",
//                GoalEntry.HABIT, GoalEntry.BUILDING, GoalEntry.PRIORITY_HIGH, 0,
//                1, GoalEntry.PAGES_STRING, 25,
//                GoalEntry.WEEKLYGOAL, 0, 0, 0, 1,
//                0, 0, 0, 0, 0, 0, 0));
//
//        repository.insert(new Goal("Push-ups",
//                GoalEntry.HABIT, GoalEntry.BUILDING, GoalEntry.PRIORITY_MEDIUM, 0,
//                1, GoalEntry.REPS_STRING, 175,
//                GoalEntry.WEEKLYGOAL, 0, 0, 0, 4,
//                0, 1, 0, 0, 0, 0, 0));
//
//        repository.insert(new Goal("Ab Exercises",
//                GoalEntry.HABIT, GoalEntry.BUILDING, GoalEntry.PRIORITY_LOW, 0,
//                1, GoalEntry.REPS_STRING, 300,
//                GoalEntry.WEEKLYGOAL, 0, 0, 0, 4,
//                0, 0, 0, 0, 0, 0, 0));
//
//        repository.insert(new Goal("Kanji Practice",
//                GoalEntry.HABIT, GoalEntry.BUILDING, GoalEntry.PRIORITY_MEDIUM, 0,
//                1, GoalEntry.MINUTE_STRING, 10*60,
//                GoalEntry.DAILYGOAL, 0, 0, 0, 1,
//                0, 4, 0, 0, 0, 0, 0));
//
//        repository.insert(new Goal("Run",
//                GoalEntry.HABIT, GoalEntry.BUILDING, GoalEntry.PRIORITY_MEDIUM, 0,
//                1, GoalEntry.MINUTE_STRING, 60 * 60,
//                GoalEntry.WEEKLYGOAL, 0, 0, 0, 3,
//                0, 1, 0, 0, 0, 0, 0));
//
//        repository.insert(new Goal("Japanese Vocab",
//                GoalEntry.HABIT, GoalEntry.BUILDING, GoalEntry.PRIORITY_MEDIUM, 0,
//                1, "word", 25,
//                GoalEntry.WEEKLYGOAL, 0, 0, 0, 3,
//                0, 1, 0, 0, 0, 0, 0));
//
//        repository.insert(new Goal("Japanese Grammar",
//                GoalEntry.HABIT, GoalEntry.BUILDING, GoalEntry.PRIORITY_MEDIUM, 1,
//                1, GoalEntry.HOUR_STRING, 60*60,
//                GoalEntry.WEEKLYGOAL, 0, 0, 0, 4,
//                0, 1, 0, 0, 0, 0, 0));
//
//        repository.insert(new Goal("Android Dev",
//                GoalEntry.HABIT, GoalEntry.BUILDING, GoalEntry.PRIORITY_MEDIUM, 0,
//                1, GoalEntry.HOUR_STRING, 7*60*60,
//                GoalEntry.WEEKLYGOAL, 0, 0, 0, 4,
//                0, 1, 0, 0, 0, 0, 0));
//
//        repository.insert(new Goal("Workout",
//                GoalEntry.HABIT, GoalEntry.BUILDING, GoalEntry.PRIORITY_VERY_HIGH, 0,
//                1, GoalEntry.HOUR_STRING, 7*60*60,
//                GoalEntry.WEEKLYGOAL, 0, 0, 0, 5,
//                0, 1, 0, 0, 0, 0, 0));
//
//        repository.insert(new Goal("Reflect",
//                GoalEntry.HABIT, GoalEntry.BUILDING, GoalEntry.PRIORITY_VERY_LOW, 0,
//                0, "", 1,
//                GoalEntry.DAILYGOAL, 0, 0, 0, 1,
//                0, 4, 0, 0, 0, 0, 0));
//
//        repository.insert(new Goal("Posture Exercise",
//                GoalEntry.HABIT, GoalEntry.BUILDING, GoalEntry.PRIORITY_MEDIUM, 0,
//                0, GoalEntry.TIMES_STRING, 10,
//                GoalEntry.WEEKLYGOAL, 0, 0, 0, 10,
//                0, 1, 0, 0, 0, 0, 0));
//
//        repository.insert(new Goal("Sleep Before Midnight",
//                GoalEntry.HABIT, GoalEntry.BUILDING, GoalEntry.PRIORITY_MEDIUM, 0,
//                0, GoalEntry.UNITS_NULL, 1,
//                GoalEntry.DAILYGOAL, 0, 0, 0, 1,
//                0, 1, 0, 0, 0, 0, 0));
//
//        repository.insert(new Goal("Clean Room",
//                GoalEntry.HABIT, GoalEntry.BUILDING, GoalEntry.PRIORITY_LOW, 0,
//                0, GoalEntry.UNITS_NULL, 1,
//                GoalEntry.WEEKLYGOAL, 0, 0, 0, 1,
//                0, 1, 0, 0, 0, 0, 0));
//
//        // Some test Tasks
//        repository.insert(new Goal("Buy New Shoes",
//                GoalEntry.TASK, GoalEntry.UNDEFINED, GoalEntry.PRIORITY_LOW, 0,
//                0, GoalEntry.UNITS_NULL, 1,
//                GoalEntry.UNDEFINED, 0, 0, 0, 1,
//                0, 1, 0, 0, 0, 0, 0));
    }



}
