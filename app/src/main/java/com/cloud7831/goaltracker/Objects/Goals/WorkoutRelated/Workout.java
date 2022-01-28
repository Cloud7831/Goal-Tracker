package com.cloud7831.goaltracker.Objects.Goals.WorkoutRelated;

import android.util.Log;

import com.cloud7831.goaltracker.Data.GoalViewModel;
import com.cloud7831.goaltracker.HelperClasses.ArrayStorageHelper;
import com.cloud7831.goaltracker.Objects.Goals.WeeklyHabit;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.ViewModel;
import androidx.room.Entity;
import androidx.room.Ignore;

@Entity(tableName = "workout_table")
public class Workout extends WeeklyHabit {
    @Ignore
    private static final String LOGTAG = "Workout";

    /**
     * Arrays can't be stored in Room, so store the id list as a string.
     * Format: numElements-exerFreq-exerID-exerFreq-exerID-...-exerFreq-exerID
     * where numElements is equal to the number of exerID x 2.
     */
    private String exerciseStorageList;
    private int dateLastCompleted;
    @Ignore
    private List<ExerciseEntry> entryList;
    @Ignore
    private List<Exercise> exerciseList;
    @Ignore
    private int itemsLoaded; // Used to keep track of how many of the entryList or exerciseList itmes have been retrieved from the database.
    @Ignore
    private int totalItems;

    // Workouts are objects that store an ordering of exercises. Each workout can be made into a goal
    // so that the user doesn't have to make individual goals for the excercises. Workouts take care of
    // making individual goals for exercises and informing the user of which exercises still need to be
    // completed for that week.
    public Workout(){
        itemsLoaded = 0;
        totalItems = 0;
    }

    public static Workout buildNewWorkout(String title, List<ExerciseEntry> entryList){
        Workout workout = new Workout();

        workout.setTitle(title);
        workout.setDateLastCompleted(0);

        // Incase the user appended some base data while creating the workout.
        workout.setExerciseEntryList(entryList);
        workout.setExerciseList(new ArrayList<Exercise>());
        return workout;
    }

    private void loadEntryData(GoalViewModel vm){
        int[] data = ArrayStorageHelper.convertStrArrToInt(exerciseStorageList);
        entryList = new ArrayList<>();
        totalItems = data.length / 2;
        vm.getExerciseEntries(this, data);
    }

    public void appendEntry(ExerciseEntry entry){
        entryList.add(entry);
        itemsLoaded++;
    }

    private void loadExerciseData(GoalViewModel vm){
        int[] data = ArrayStorageHelper.convertStrArrToInt(exerciseStorageList);
        exerciseList = new ArrayList<>();
        totalItems = data.length / 2;
        vm.getExercises(this, data);
    }

    public void appendExercise(Exercise exercise){
        exerciseList.add(exercise);
        itemsLoaded++;
    }

    public void setTotalItems(int i){
        if(i < 0){
            Log.e(LOGTAG, "Total Items can't be set to a negative value");
            totalItems = 0;
            return;
        }

        totalItems = i;
    }

    public void setExerciseEntryList(List<ExerciseEntry> list){
        if(list == null){
            entryList = new ArrayList<>();
        }
        entryList = list;
    }

    public void setExerciseList(List<Exercise> list){
        if(list == null){
            exerciseList = new ArrayList<>();
        }
        exerciseList = list;
    }

    public void setExerciseStorageList(String list){
        exerciseStorageList = list;
    }

    public String getExerciseStorageList(){
        return exerciseStorageList;
    }

    public void setDateLastCompleted(int i){
        dateLastCompleted = i;
    }

    public int getDateLastCompleted(){
        // TODO: may want to return a Date object instead.
        return dateLastCompleted;
    }

    public List<ExerciseEntry> getEntryList(){
        return entryList;
    }

    public boolean equals(Workout newWorkout){
        if(dateLastCompleted == newWorkout.getDateLastCompleted()){
            return true;
        }
        return false;
    }
}
