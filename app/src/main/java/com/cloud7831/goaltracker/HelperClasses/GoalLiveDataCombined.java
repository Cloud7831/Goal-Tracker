package com.cloud7831.goaltracker.HelperClasses;

import android.util.Log;

import com.cloud7831.goaltracker.Objects.DailyHabit;
import com.cloud7831.goaltracker.Objects.GoalRefactor;
import com.cloud7831.goaltracker.Objects.MonthlyHabit;
import com.cloud7831.goaltracker.Objects.Task;
import com.cloud7831.goaltracker.Objects.WeeklyHabit;
import com.cloud7831.goaltracker.Objects.Workout;

import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

public class GoalLiveDataCombined extends MediatorLiveData<List<GoalRefactor>>{
    private static final String LOGTAG = "GoalLiveDataCollection";
    private List<Task> listTasks;
    private List<DailyHabit> listDaily;
    private List<WeeklyHabit> listWeekly;
    private List<MonthlyHabit> listMonthly;
    private List<Workout> listWorkouts;
    private List<GoalRefactor> sortedList;

    GoalLiveDataCombined(@NonNull LiveData<List<Task>> tasksLiveData, @NonNull LiveData<List<DailyHabit>> dailyLiveData, @NonNull LiveData<List<WeeklyHabit>> weeklyLiveData, @NonNull LiveData<List<MonthlyHabit>> monthlyLiveData){
        listTasks = tasksLiveData.getValue();
        listDaily = dailyLiveData.getValue();
        listWeekly = weeklyLiveData.getValue();
        listMonthly = monthlyLiveData.getValue();

        sortedList = sortGoalListByComPriority();
        setValue(sortedList);

        addSource(tasksLiveData, new Observer<List<Task>>() {
                    @Override
                    public void onChanged(List<Task> tasks) {
                            listTasks = tasks;
                            sortedList = sortGoalListByComPriority();
                            setValue(sortedList);
                    }
                }

        );

        addSource(dailyLiveData, new Observer<List<DailyHabit>>() {
                    @Override
                    public void onChanged(List<DailyHabit> dailyHabits) {
                        listDaily = dailyHabits;
                        sortedList = sortGoalListByComPriority();
                        setValue(sortedList);
                    }
                }

        );

        addSource(weeklyLiveData, new Observer<List<WeeklyHabit>>() {
                    @Override
                    public void onChanged(List<WeeklyHabit> weeklyHabits) {
                        listWeekly = weeklyHabits;
                        sortedList = sortGoalListByComPriority();
                        setValue(sortedList);
                    }
                }

        );

        addSource(monthlyLiveData, new Observer<List<MonthlyHabit>>() {
                    @Override
                    public void onChanged(List<MonthlyHabit> monthlyHabits) {
                        listMonthly = monthlyHabits;
                        sortedList = sortGoalListByComPriority();
                        setValue(sortedList);
                    }
                }

        );

//        addSource(workoutsLiveData, new Observer<List<Workout>>() {
//                    @Override
//                    public void onChanged(List<Workout> workouts) {
//                        listWorkouts = workouts;
//                        sortedList = sortGoalListByComPriority();
//                        setValue(sortedList);
//                    }
//                }
//
//        );


    }

    public List<GoalRefactor> sortGoalListByComPriority(){
        // This function takes in all the sorted Goal lists and merges them into one
        // main list of type Goal. It doesn't get rid of the original lists and sorts based
        // on Complex Priority.

        final int MAX_VALUE = 200000000;
        List<GoalRefactor> sortedList = new ArrayList<>();
        int num = listTasks.size() + listDaily.size() + listMonthly.size() + listWeekly.size();// number of elements.

        int lowestBm = 0; // A bitmap used to tell which array has the lowest value.
        // Tournament Merge
        // The way the bitmap works, is that the first digit tells who won the whole thing,
        // the second and third values tells who won the semifinals
        // and the 4th-8th values tell who won the quarterfinals (first round).
        // a value of 1 means that the left array (or the first is sequential order) is smaller.
        // This current format of the tournament merge is designed to handle up to 8 different lists.

        int dailyIndex = 0;
        // I want the daily list to be left most, because it's most likely to have
        // the lowest element.
        int taskIndex = 0;
        int weeklyIndex = 0;
        int monthlyIndex = 0;
        int round11win;
        int round12win;
        int round13win;
        int round14win;
        int round21win;
        int round22win;
        int elem1;
        int elem2;
        int elem3;
        int elem4;
        int elem5 = MAX_VALUE; // Max val, because there is no 5th list yet.
        int elem6 = MAX_VALUE;
        int elem7 = MAX_VALUE;
        int elem8 = MAX_VALUE;

        // Set the initial values.
        if(listDaily.size() > 0){
            elem1 = listDaily.get(dailyIndex).getComplexPriority();
        }
        else{
            elem1 = MAX_VALUE;
        }
        if(listTasks.size() > 0){
            elem2 = listTasks.get(taskIndex).getComplexPriority();
        }
        else{
            elem2 = MAX_VALUE;
        }
        if(listWeekly.size() > 0){
            elem3 = listWeekly.get(weeklyIndex).getComplexPriority();
        }
        else{
            elem3 = MAX_VALUE;
        }
        if(listMonthly.size() > 0){
            elem4 = listMonthly.get(monthlyIndex).getComplexPriority();
        }
        else{
            elem4 = MAX_VALUE;
        }

        // First round
        if(elem1 <= elem2){
            lowestBm = 0b0001000;
            round11win = elem1;
        }
        else{
            round11win = elem2;
        }
        if(elem3 <= elem4){
            lowestBm = lowestBm | 0b0000100;
            round12win = elem3;
        }
        else{
            round12win = elem4;
        }
        if(elem5 <= elem6){
            lowestBm = lowestBm | 0b0000010;
            round13win = elem5;
        }
        else{
            round13win = elem6;
        }
        if(elem7 <= elem8){
            lowestBm = lowestBm | 0b0000001;
            round14win = elem7;
        }
        else{
            round14win = elem8;
        }

        // Second round --------------------------------------
        if(round11win <= round12win){
            lowestBm = lowestBm | 0b0100000;
            round21win = round11win;
        }
        else{
            round21win = round12win;
        }
        if(round13win <= round14win){
            lowestBm = lowestBm | 0b0010000;
            round22win = round13win;
        }
        else{
            round22win = round14win;
        }

        // Third round -------------------------------------
        // TODO: when there are more lists, this will need to have a comparison against round22win
        if(round21win <= round22win){
            lowestBm = lowestBm | 0b1000000;
        }

        // MAIN LOOP
        for(int i = 0; i < num; i++){

            // Add the lowest goal to the sorted list.
            if((lowestBm & 0b1101000) == 0b1101000){
                // DailyList was the lowest
                // Pop the top element
                sortedList.add(listDaily.get(dailyIndex));
                dailyIndex++;

                // Bubble up the tournament
                if(listDaily.size() >= dailyIndex){
                    // another element exists
                    elem1 = listDaily.get(dailyIndex).getComplexPriority();
                }
                else{
                    elem1 = MAX_VALUE;
                }

                // redo the matches where the old value was.
                if (elem1 <= elem2) {
                    // lowestBm doesn't need to be changed yet.
                    round11win = elem1;
                }
                else{
                    lowestBm = lowestBm ^ 0b0001000; // need to flip that bit to a zero
                    round11win = elem2;
                }

                // round 2
                if (round11win <= round12win) {
                    // lowestBm doesn't need to be changed yet.
                    round21win = round11win;
                }
                else{
                    lowestBm = lowestBm ^ 0b0100000; // need to flip that bit to a zero
                    round21win = round12win;
                }

                // round 3
                if (round21win > round22win) {
                    lowestBm = lowestBm ^ 0b1000000; // need to flip that bit to a zero
                }
                // sorted order has been restored.
            }
            else if((lowestBm & 0b1100000) == 0b1100000){
                // TaskList was the lowest
                // Pop the top element
                sortedList.add(listTasks.get(taskIndex));
                taskIndex++;

                // Bubble up the tournament
                if(listTasks.size() >= taskIndex){
                    // another element exists
                    elem2 = listTasks.get(taskIndex).getComplexPriority();
                }
                else{
                    elem2 = MAX_VALUE;
                }

                // redo the matches where the old value was.
                if (elem1 <= elem2) {
                    lowestBm = lowestBm | 0b0001000;
                    round11win = elem1;
                }
                else{
                    round11win = elem2;
                }

                // round 2
                if (round11win <= round12win) {
                    // lowestBm doesn't need to be changed yet.
                    round21win = round11win;
                }
                else{
                    lowestBm = lowestBm ^ 0b0100000; // need to flip that bit to a zero
                    round21win = round12win;
                }

                // round 3
                if (round21win > round22win) {
                    lowestBm = lowestBm ^ 0b1000000; // need to flip that bit to a zero
                }
                // sorted order has been restored.
            }

            // 3rd and 4th branch
            if((lowestBm & 0b1000100) == 0b1000100){
                // WeeklyList was the lowest
                // Pop the top element
                sortedList.add(listWeekly.get(weeklyIndex));
                weeklyIndex++;

                // Bubble up the tournament
                if(listWeekly.size() >= weeklyIndex){
                    // another element exists
                    elem3 = listWeekly.get(weeklyIndex).getComplexPriority();
                }
                else{
                    elem3 = MAX_VALUE;
                }

                // redo the matches where the old value was.
                if (elem3 <= elem4) {
                    // lowestBm doesn't need to be changed yet.
                    round12win = elem3;
                }
                else{
                    lowestBm = lowestBm ^ 0b0000100; // need to flip that bit to a zero
                    round12win = elem4;
                }

                // round 2
                if (round11win <= round12win) {
                    lowestBm = lowestBm | 0b0100000;
                    round21win = round11win;
                }
                else{
                    round21win = round12win;
                }

                // round 3
                if (round21win > round22win) {
                    lowestBm = lowestBm ^ 0b1000000; // need to flip that bit to a zero
                }
                // sorted order has been restored.
            }
            else if((lowestBm & 0b1000000) == 0b1000000){
                // MonthlyList was the lowest
                // Pop the top element
                sortedList.add(listMonthly.get(monthlyIndex));
                monthlyIndex++;

                // Bubble up the tournament
                if(listMonthly.size() >= monthlyIndex){
                    // another element exists
                    elem4 = listMonthly.get(monthlyIndex).getComplexPriority();
                }
                else{
                    elem4 = MAX_VALUE;
                }

                // redo the matches where the old value was.
                if (elem3 <= elem4) {
                    lowestBm = lowestBm | 0b0001000;
                    round12win = elem3;
                }
                else{
                    round12win = elem4;
                }

                // round 2
                if (round11win <= round12win) {
                    lowestBm = lowestBm | 0b0100000;
                    round21win = round11win;
                }
                else{
                    round21win = round12win;
                }

                // round 3
                if (round21win > round22win) {
                    lowestBm = lowestBm ^ 0b1000000; // need to flip that bit to a zero
                }
                // sorted order has been restored.
            }

            // TODO: there is no second half of the tree, yet.
            Log.e(LOGTAG, "The second half of the tree shouldn't be reached, so something went wrong");
            return sortedList;
//            if((lowestBm & 0b0010010) == 0b0010010){
//                // list5 was the lowest
//                // Pop the top element
//                sortedList.add(listDaily.get(dailyIndex));
//                dailyIndex++;
//
////                 Bubble up the tournament
//                if(listDaily.size() >= dailyIndex){
//                    // another element exists
//                    elem5 = listDaily.get(dailyIndex).getComplexPriority();
//                }
//                else{
//                    elem5 = MAX_VALUE;
//                }
//
//                // redo the matches where the old value was.
//                if (elem5 <= elem6) {
//                    // lowestBm doesn't need to be changed yet.
//                    round13win = elem5;
//                }
//                else{
//                    lowestBm = lowestBm ^ 0b0000010; // need to flip that bit to a zero
//                    round13win = elem6;
//                }
//
//                // round 2
//                if (round13win <= round14win) {
//                    // lowestBm doesn't need to be changed yet.
//                    round22win = round13win;
//                }
//                else{
//                    lowestBm = lowestBm ^ 0b0010000; // need to flip that bit to a zero
//                    round22win = round14win;
//                }
//
//                // round 3
//                if (round21win <= round22win) {
//                    lowestBm = lowestBm | 0b1000000; // need to flip that bit to a zero
//                }
//                // sorted order has been restored.
//            }
//            else if((lowestBm & 0b0100000) == 0b0100000){
//                // TaskList was the lowest
//                // Pop the top element
//                sortedList.add(listTasks.get(taskIndex));
//                taskIndex++;
//
//                // Bubble up the tournament
//                if(listTasks.size() >= taskIndex){
//                    // another element exists
//                    elem6 = listTasks.get(taskIndex).getComplexPriority();
//                }
//                else{
//                    elem6 = MAX_VALUE;
//                }
//
//                // redo the matches where the old value was.
//                if (elem5 <= elem6) {
//                    lowestBm = lowestBm | 0b0000010;
//                    round13win = elem5;
//                }
//                else{
//                    round13win = elem6;
//                }
//
//                // round 2
//                if (round13win <= round14win) {
//                    // lowestBm doesn't need to be changed yet.
//                    round22win = round13win;
//                }
//                else{
//                    lowestBm = lowestBm ^ 0b0010000; // need to flip that bit to a zero
//                    round22win = round14win;
//                }
//
//                // round 3
//                if (round21win <= round22win) {
//                    lowestBm = lowestBm | 0b1000000; // need to flip that bit to a zero
//                }
//                // sorted order has been restored.
//            }
//
//            // 7th and 8th branch
//            if((lowestBm & 0b0000001) == 0b0000001){
//                // WeeklyList was the lowest
//                // Pop the top element
//                sortedList.add(listWeekly.get(weeklyIndex));
//                weeklyIndex++;
//
//                // Bubble up the tournament
//                if(listWeekly.size() >= weeklyIndex){
//                    // another element exists
//                    elem7 = listWeekly.get(weeklyIndex).getComplexPriority();
//                }
//                else{
//                    elem7 = MAX_VALUE;
//                }
//
//                // redo the matches where the old value was.
//                if (elem7 <= elem8) {
//                    // lowestBm doesn't need to be changed yet.
//                    round14win = elem7;
//                }
//                else{
//                    lowestBm = lowestBm ^ 0b0000001; // need to flip that bit to a zero
//                    round14win = elem8;
//                }
//
//                // round 2
//                if (round13win <= round14win) {
//                    lowestBm = lowestBm | 0b0010000;
//                    round22win = round13win;
//                }
//                else{
//                    round22win = round14win;
//                }
//
//                // round 3
//                if (round21win <= round22win) {
//                    lowestBm = lowestBm | 0b1000000; // need to flip that bit to a zero
//                }
//                // sorted order has been restored.
//            }
//            else {
//                // Element 8 was the lowest
//                // Pop the top element
//                sortedList.add(listMonthly.get(monthlyIndex));
//                monthlyIndex++;
//
//                // Bubble up the tournament
//                if(listMonthly.size() >= monthlyIndex){
//                    // another element exists
//                    elem8 = listMonthly.get(monthlyIndex).getComplexPriority();
//                }
//                else{
//                    elem8 = MAX_VALUE;
//                }
//
//                // redo the matches where the old value was.
//                if (elem7 <= elem8) {
//                    lowestBm = lowestBm | 0b0000001;
//                    round14win = elem7;
//                }
//                else{
//                    round14win = elem8;
//                }
//
//                // round 2
//                if (round13win <= round14win) {
//                    lowestBm = lowestBm | 0b0010000;
//                    round22win = round13win;
//                }
//                else{
//                    round22win = round14win;
//                }
//
//                // round 3
//                if (round21win <= round22win) {
//                    lowestBm = lowestBm | 0b1000000; // need to flip that bit to a zero
//                }
//                // sorted order has been restored.
//            }
//
        }// Loop end
//
        return sortedList;
    }
    
}
