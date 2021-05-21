package com.cloud7831.goaltracker;

import com.cloud7831.goaltracker.Data.GoalsContract.*;
import com.cloud7831.goaltracker.Objects.Goals.DailyHabit;
import com.cloud7831.goaltracker.Objects.Goals.Goal;
import com.cloud7831.goaltracker.Objects.Goals.MonthlyHabit;
import com.cloud7831.goaltracker.Objects.Goals.Task;
import com.cloud7831.goaltracker.Objects.Goals.WeeklyHabit;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    @Test
    public void sorting_isCorrect() {

        // TODO: this doesn't even test properly, because the lists themselves should be sorted on CP, but right now it's just assumed they're correctly sorted.
        List<Task> listTasks = new ArrayList<>();
        listTasks.add(new Task("test", 4, 1, 1, 1, 1, "days", 25, 0, 0, 0, 1, 0, 0, 0));
        listTasks.add(new Task("Buy Dress Shoes", GoalEntry.PRIORITY_LOW, 0, GoalEntry.BUILDING, 0, 0, GoalEntry.TIMES_STRING, 1, 0, 0, 0, 1, 0, 0, 0));

        List<DailyHabit> listDaily = new ArrayList<>();
        listDaily.add(new DailyHabit("test", 4, 1, 1, 1, 1, "days", 25, 0, 0, 0, 3, 0, 0, 0, 6));
        listDaily.add(new DailyHabit("test2", 2, 1, 1, 1, 1, "days", 25, 0, 0, 0, 3, 0, 0, 0, 0));
        listDaily.add(new DailyHabit("test3", 1, 1, 1, 1, 1, "days", 25, 0, 0, 0, 3, 0, 0, 0, 10));
        listDaily.add(new DailyHabit("Drink Water", GoalEntry.PRIORITY_HIGH, 0, GoalEntry.BUILDING, 0, 1, "ml", 1500, 0, 0, 0, 3, 0, 0, 0, 0));
        listDaily.add(new DailyHabit("Wanikani", GoalEntry.PRIORITY_VERY_HIGH, 0, GoalEntry.BUILDING, 0, 0, GoalEntry.TIMES_STRING, 1, 0, 0, 0, 1, 0, 0, 0, 147));
        listDaily.add(new DailyHabit("Posture Exercise", GoalEntry.PRIORITY_HIGH, 0, GoalEntry.BUILDING, 0, 0, GoalEntry.TIMES_STRING, 1, 0, 0, 0, 1, 0, 0, 0, 0));
        listDaily.add(new DailyHabit("Sleep Before Midnight", GoalEntry.PRIORITY_MEDIUM, 0, GoalEntry.BUILDING, 0, 0, GoalEntry.TIMES_STRING, 1, 0, 0, 0, 1, 0, 0, 0, 0));
        listDaily.add(new DailyHabit("ShakingSats", GoalEntry.PRIORITY_VERY_HIGH, 0, GoalEntry.BUILDING, 0, 0, GoalEntry.TIMES_STRING, 1, 0, 0, 0, 1, 0, 0, 0, 10));

        List<WeeklyHabit> listWeekly = new ArrayList<>();
        listWeekly.add(new WeeklyHabit("test", 1, 1, 1, 1, 1, "days", 25, 0, 0, 0, 3, 0, 0, 0, 6, 1));
        listWeekly.add(new WeeklyHabit("test2", 5, 1, 1, 1, 1, "days", 25, 0, 0, 0, 3, 0, 0, 0, 0, 1));
        listWeekly.add(new WeeklyHabit("test3", 3, 1, 1, 1, 1, "days", 25, 0, 0, 0, 3, 0, 0, 0, 10, 0));
        listWeekly.add(new WeeklyHabit("Push-ups", GoalEntry.PRIORITY_HIGH, 0, GoalEntry.BUILDING, 0, 1, GoalEntry.REPS_STRING, 150, 0, 0, 0, 6, 0, 0, 0, 0, 2));
        listWeekly.add(new WeeklyHabit("Sit-ups", GoalEntry.PRIORITY_MEDIUM, 0, GoalEntry.BUILDING, 0, 1, GoalEntry.REPS_STRING, 150, 0, 0, 0, 5, 0, 0, 0, 0, 1));
        listWeekly.add(new WeeklyHabit("Curls", GoalEntry.PRIORITY_MEDIUM, 0, GoalEntry.BUILDING, 0, 1, GoalEntry.REPS_STRING, 100, 0, 0, 0, 5, 0, 0, 0, 0, 0));
        listWeekly.add(new WeeklyHabit("Arnold Press", GoalEntry.PRIORITY_LOW, 0, GoalEntry.BUILDING, 0, 1, GoalEntry.REPS_STRING, 50, 0, 0, 0, 3, 0, 0, 0, 0, 0));
        listWeekly.add(new WeeklyHabit("Skull Crushers", GoalEntry.PRIORITY_MEDIUM, 0, GoalEntry.BUILDING, 0, 1, GoalEntry.REPS_STRING, 120, 0, 0, 0, 4, 0, 0, 0, 0, 0));
        listWeekly.add(new WeeklyHabit("Dips", GoalEntry.PRIORITY_MEDIUM, 0, GoalEntry.BUILDING, 0, 1, GoalEntry.REPS_STRING, 75, 0, 0, 0, 3, 0, 0, 0, 0, 0));
        listWeekly.add(new WeeklyHabit("Pull-ups", GoalEntry.PRIORITY_HIGH, 0, GoalEntry.BUILDING, 0, 1, GoalEntry.REPS_STRING, 30, 0, 0, 0, 5, 0, 0, 0, 0, 1));
        listWeekly.add(new WeeklyHabit("Chin-ups", GoalEntry.PRIORITY_HIGH, 0, GoalEntry.BUILDING, 0, 1, GoalEntry.REPS_STRING, 25, 0, 0, 0, 5, 0, 0, 0, 0, 1));
        listWeekly.add(new WeeklyHabit("Flies", GoalEntry.PRIORITY_VERY_LOW, 0, GoalEntry.BUILDING, 0, 1, GoalEntry.REPS_STRING, 30, 0, 0, 0, 3, 0, 0, 0, 0, 0));
        listWeekly.add(new WeeklyHabit("Squats", GoalEntry.PRIORITY_HIGH, 0, GoalEntry.BUILDING, 0, 1, GoalEntry.REPS_STRING, 120, 0, 0, 0, 4, 0, 0, 0, 0, 0));
        listWeekly.add(new WeeklyHabit("Split Squats", GoalEntry.PRIORITY_LOW, 0, GoalEntry.BUILDING, 0, 1, GoalEntry.REPS_STRING, 60, 0, 0, 0, 2, 0, 0, 0, 0, 0));
        listWeekly.add(new WeeklyHabit("Calf Raise", GoalEntry.PRIORITY_LOW, 0, GoalEntry.BUILDING, 0, 1, GoalEntry.REPS_STRING, 100, 0, 0, 0, 3, 0, 0, 0, 0, 0));
        listWeekly.add(new WeeklyHabit("Crunches", GoalEntry.PRIORITY_HIGH, 0, GoalEntry.BUILDING, 0, 1, GoalEntry.REPS_STRING, 120, 0, 0, 0, 4, 0, 0, 0, 0, 1));
        listWeekly.add(new WeeklyHabit("Heel Touch", GoalEntry.PRIORITY_MEDIUM, 0, GoalEntry.BUILDING, 0, 1, GoalEntry.REPS_STRING, 40, 0, 0, 0, 2, 0, 0, 0, 0, 0));
        listWeekly.add(new WeeklyHabit("Russian Twist", GoalEntry.PRIORITY_MEDIUM, 0, GoalEntry.BUILDING, 0, 1, GoalEntry.REPS_STRING, 80, 0, 0, 0, 2, 0, 0, 0, 0, 2));
        listWeekly.add(new WeeklyHabit("Leg Lifts", GoalEntry.PRIORITY_MEDIUM, 0, GoalEntry.BUILDING, 0, 1, GoalEntry.REPS_STRING, 30, 0, 0, 0, 2, 0, 0, 0, 0, 0));
        listWeekly.add(new WeeklyHabit("Grammar Practice", GoalEntry.PRIORITY_VERY_HIGH, 0, GoalEntry.BUILDING, 0, 1, GoalEntry.HOUR_STRING, 10800, 0, 0, 0, 6, 0, 0, 0, 0, 3));
        listWeekly.add(new WeeklyHabit("Run", GoalEntry.PRIORITY_MEDIUM, 0, GoalEntry.BUILDING, 0, 1, GoalEntry.HOUR_STRING, 60*60, 0, 0, 0, 3, 0, 0, 0, 0, 0));
        listWeekly.add(new WeeklyHabit("Flips", GoalEntry.PRIORITY_LOW, 0, GoalEntry.BUILDING, 0, 1, GoalEntry.REPS_STRING, 150, 0, 0, 0, 3, 0, 0, 0, 0, 0));
        listWeekly.add(new WeeklyHabit("Android Dev", GoalEntry.PRIORITY_MEDIUM, 0, GoalEntry.BUILDING, 0, 1, GoalEntry.HOUR_STRING, 40*60*60, 0, 0, 0, 80, 0, 0, 0, 0, 0));

        List<MonthlyHabit> listMonthly = new ArrayList<>();
        listMonthly.add(new MonthlyHabit("Clean Room", GoalEntry.PRIORITY_MEDIUM, 0, GoalEntry.BUILDING, 0, 0, GoalEntry.TIMES_STRING, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0));
        listMonthly.add(new MonthlyHabit("Read a Book", GoalEntry.PRIORITY_LOW, 0, GoalEntry.BUILDING, 0, 1, GoalEntry.PAGES_STRING, 100, 0, 0, 0, 4, 0, 0, 0, 0, 0, 2));





//        GoalLiveDataCombined data = new GoalLiveDataCombined(taskList, dailyList, weeklyList, monthlyList);





    /*
        List<GoalRefactor> sorted = data.sortGoalListByComPriority();
        int lastPriority = 2000000000;

//        for (GoalRefactor i: sorted) {
//            assert(i.getComplexPriority() <= lastPriority);
//            lastPriority = i.getComplexPriority();
//        }
        assertEquals(35, dailyList.size() + weeklyList.size() + monthlyList.size() + taskList.size());
        assertEquals(dailyList.size() + weeklyList.size() + monthlyList.size() + taskList.size(), sorted.size());
*/

        // This function takes in all the sorted Goal lists and merges them into one
        // main list of type Goal. It doesn't get rid of the original lists and sorts based
        // on Complex Priority descending.

        final int MIN_VALUE = -1;
        List<Goal> sortedList = new ArrayList<>();

        if(listTasks == null){
            listTasks = new ArrayList<>();
        }
        if(listDaily == null){
            listDaily = new ArrayList<>();
        }
        if(listWeekly == null){
            listWeekly = new ArrayList<>();
        }
        if(listMonthly == null){
            listMonthly = new ArrayList<>();
        }
        int num = listTasks.size() + listDaily.size() + listMonthly.size() + listWeekly.size();// number of elements.

        assertEquals(35, num);

        int highestBm = 0; // A bitmap used to tell which array has the highest value.
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
        int elem5 = MIN_VALUE;
        int elem6 = MIN_VALUE;
        int elem7 = MIN_VALUE;
        int elem8 = MIN_VALUE;

        // Set the initial values.
        if(listDaily.size() > 0){
            elem1 = listDaily.get(dailyIndex).getComplexPriority();
        }
        else{
            elem1 = MIN_VALUE;
        }
        if(listTasks.size() > 0){
            elem2 = listTasks.get(taskIndex).getComplexPriority();
        }
        else{
            elem2 = MIN_VALUE;
        }
        if(listWeekly.size() > 0){
            elem3 = listWeekly.get(weeklyIndex).getComplexPriority();
        }
        else{
            elem3 = MIN_VALUE;
        }
        if(listMonthly.size() > 0){
            elem4 = listMonthly.get(monthlyIndex).getComplexPriority();
        }
        else{
            elem4 = MIN_VALUE;
        }

        System.out.println("initial vals: " + elem1 + " " + elem2 + " " + elem3 + " " + elem4);

        // First round
        if(elem1 >= elem2){
            highestBm = 0b0001000;
            round11win = elem1;
        }
        else{
            round11win = elem2;
        }
        if(elem3 >= elem4){
            highestBm = highestBm | 0b0000100;
            round12win = elem3;
        }
        else{
            round12win = elem4;
        }
        if(elem5 >= elem6){
            highestBm = highestBm | 0b0000010;
            round13win = elem5;
        }
        else{
            round13win = elem6;
        }
        if(elem7 >= elem8){
            highestBm = highestBm | 0b0000001;
            round14win = elem7;
        }
        else{
            round14win = elem8;
        }

        System.out.println(Integer.toBinaryString(highestBm));

        // Second round --------------------------------------
        if(round11win >= round12win){
            highestBm = highestBm | 0b0100000;
            round21win = round11win;
        }
        else{
            round21win = round12win;
        }
        if(round13win >= round14win){
            highestBm = highestBm | 0b0010000;
            round22win = round13win;
        }
        else{
            round22win = round14win;
        }

        System.out.println(Integer.toBinaryString(highestBm));

        // Third round -------------------------------------
        if(round21win >= round22win){
            highestBm = highestBm | 0b1000000;
        }

        System.out.println(Integer.toBinaryString(highestBm));

        // MAIN LOOP
        for(int i = 0; i < num; i++){

            System.out.println("starting main loop with: " + Integer.toBinaryString(highestBm) + " " + elem1 + " " + elem2 + " " + elem3 + " " + elem4);
            // Add the lowest goal to the sorted list.
            if((highestBm & 0b1101000) == 0b1101000){
                assert(elem1 >= elem2);
                assert(elem1 >= elem3);
                assert(elem1 >= elem4);
                // DailyList was the lowest
                // Pop the top element
                if(dailyIndex < listDaily.size()) {
                    sortedList.add(listDaily.get(dailyIndex));
//                    Log.i(LOGTAG, "Added " + listDaily.get(dailyIndex).getTitle() + " to the sorted list.");
                }
                dailyIndex++;

                // Bubble up the tournament
                if(listDaily.size() > dailyIndex){
                    // another element exists
                    elem1 = listDaily.get(dailyIndex).getComplexPriority();
                }
                else{
                    elem1 = MIN_VALUE;
                }

                // redo the matches where the old value was.
                if (elem1 >= elem2) {
                    // highestBm doesn't need to be changed yet.
                    round11win = elem1;
                }
                else{
                    highestBm = highestBm ^ 0b0001000; // need to flip that bit to a zero
                    round11win = elem2;
                }

                // round 2
                if (round11win >= round12win) {
                    // highestBm doesn't need to be changed yet.
                    round21win = round11win;
                }
                else{
                    highestBm = highestBm ^ 0b0100000; // need to flip that bit to a zero
                    round21win = round12win;
                }

                // round 3
                if (round21win < round22win) {
                    highestBm = highestBm ^ 0b1000000; // need to flip that bit to a zero
                }
                // sorted order has been restored.
            }
            else if((highestBm & 0b1100000) == 0b1100000){
                assert(elem2 > elem1);
                assert(elem2 >= elem3);
                assert(elem2 >= elem4);
                // TaskList was the lowest
                // Pop the top element
                sortedList.add(listTasks.get(taskIndex));
//                Log.i(LOGTAG, "Added " + listTasks.get(taskIndex).getTitle() + " to the sorted list.");
                taskIndex++;

                // Bubble up the tournament
                if(listTasks.size() > taskIndex){
                    // another element exists
                    elem2 = listTasks.get(taskIndex).getComplexPriority();
                }
                else{
                    elem2 = MIN_VALUE;
                }

                // redo the matches where the old value was.
                if (elem1 >= elem2) {
                    highestBm = highestBm | 0b0001000;
                    round11win = elem1;
                }
                else{
                    round11win = elem2;
                }

                // round 2
                if (round11win >= round12win) {
                    // highestBm doesn't need to be changed yet.
                    round21win = round11win;
                }
                else{
                    highestBm = highestBm ^ 0b0100000; // need to flip that bit to a zero
                    round21win = round12win;
                }

                // round 3
                if (round21win < round22win) {
                    highestBm = highestBm ^ 0b1000000; // need to flip that bit to a zero
                }
                // sorted order has been restored.
            }

            // 3rd and 4th branch
            else if((highestBm & 0b1000100) == 0b1000100){
                assert(elem3 > elem2);
                assert(elem3 > elem1);
                assert(elem3 >= elem4);
                // WeeklyList was the lowest
                // Pop the top element
                sortedList.add(listWeekly.get(weeklyIndex));
//                Log.i(LOGTAG, "Added " + listWeekly.get(weeklyIndex).getTitle() + " to the sorted list.");
                weeklyIndex++;

                // Bubble up the tournament
                if(listWeekly.size() > weeklyIndex){
                    // another element exists
                    elem3 = listWeekly.get(weeklyIndex).getComplexPriority();
                }
                else{
                    elem3 = MIN_VALUE;
                }

                // redo the matches where the old value was.
                if (elem3 >= elem4) {
                    // highestBm doesn't need to be changed yet.
                    round12win = elem3;
                }
                else{
                    highestBm = highestBm ^ 0b0000100; // need to flip that bit to a zero
                    round12win = elem4;
                }

                // round 2
                if (round11win >= round12win) {
                    highestBm = highestBm | 0b0100000;
                    round21win = round11win;
                }
                else{
                    round21win = round12win;
                }

                // round 3
                if (round21win < round22win) {
                    highestBm = highestBm ^ 0b1000000; // need to flip that bit to a zero
                }
                // sorted order has been restored.
            }
            else if((highestBm & 0b1000000) == 0b1000000){
                assert(elem4 > elem2);
                assert(elem4 > elem3);
                assert(elem4 > elem1);
                // MonthlyList was the lowest
                // Pop the top element
                if(monthlyIndex < listMonthly.size()){
                    sortedList.add(listMonthly.get(monthlyIndex));
//                    Log.i(LOGTAG, "Added " + listMonthly.get(monthlyIndex).getTitle() + " to the sorted list.");
                }
                monthlyIndex++;

                // Bubble up the tournament
                if(listMonthly.size() > monthlyIndex){
                    // another element exists
                    elem4 = listMonthly.get(monthlyIndex).getComplexPriority();
                }
                else{
                    elem4 = MIN_VALUE;
                }

                // redo the matches where the old value was.
                if (elem3 >= elem4) {
                    highestBm = highestBm | 0b0000100;
                    round12win = elem3;
                }
                else{
                    round12win = elem4;
                }

                // round 2
                if (round11win >= round12win) {
                    highestBm = highestBm | 0b0100000;
                    round21win = round11win;
                }
                else{
                    round21win = round12win;
                }

                // round 3
                if (round21win < round22win) {
                    highestBm = highestBm ^ 0b1000000; // need to flip that bit to a zero
                }
                // sorted order has been restored.
            }
            else{
                System.out.println("errror, this shouldn't be reached.");
            }
//
//            // TODO: there is no second half of the tree, yet.
//            Log.e(LOGTAG, "The second half of the tree shouldn't be reached, so something went wrong");
//            return sortedList;

//            else if((highestBm & 0b0010010) == 0b0010010){
//                // list5 was the lowest
//                // Pop the top element
//                sortedList.add(listDaily.get(dailyIndex));
//                dailyIndex++;
//
////                 Bubble up the tournament
//                if(listDaily.size() > dailyIndex){
//                    // another element exists
//                    elem5 = listDaily.get(dailyIndex).getComplexPriority();
//                }
//                else{
//                    elem5 = MIN_VALUE;
//                }
//
//                // redo the matches where the old value was.
//                if (elem5 <= elem6) {
//                    // highestBm doesn't need to be changed yet.
//                    round13win = elem5;
//                }
//                else{
//                    highestBm = highestBm ^ 0b0000010; // need to flip that bit to a zero
//                    round13win = elem6;
//                }
//
//                // round 2
//                if (round13win <= round14win) {
//                    // highestBm doesn't need to be changed yet.
//                    round22win = round13win;
//                }
//                else{
//                    highestBm = highestBm ^ 0b0010000; // need to flip that bit to a zero
//                    round22win = round14win;
//                }
//
//                // round 3
//                if (round21win <= round22win) {
//                    highestBm = highestBm | 0b1000000; // need to flip that bit to a zero
//                }
//                // sorted order has been restored.
//            }
//            else if((highestBm & 0b0100000) == 0b0100000){
//                // TaskList was the lowest
//                // Pop the top element
//                sortedList.add(listTasks.get(taskIndex));
//                taskIndex++;
//
//                // Bubble up the tournament
//                if(listTasks.size() > taskIndex){
//                    // another element exists
//                    elem6 = listTasks.get(taskIndex).getComplexPriority();
//                }
//                else{
//                    elem6 = MIN_VALUE;
//                }
//
//                // redo the matches where the old value was.
//                if (elem5 <= elem6) {
//                    highestBm = highestBm | 0b0000010;
//                    round13win = elem5;
//                }
//                else{
//                    round13win = elem6;
//                }
//
//                // round 2
//                if (round13win <= round14win) {
//                    // highestBm doesn't need to be changed yet.
//                    round22win = round13win;
//                }
//                else{
//                    highestBm = highestBm ^ 0b0010000; // need to flip that bit to a zero
//                    round22win = round14win;
//                }
//
//                // round 3
//                if (round21win <= round22win) {
//                    highestBm = highestBm | 0b1000000; // need to flip that bit to a zero
//                }
//                // sorted order has been restored.
//            }
//
//            // 7th and 8th branch
//            if((highestBm & 0b0000001) == 0b0000001){
//                // WeeklyList was the lowest
//                // Pop the top element
//                sortedList.add(listWeekly.get(weeklyIndex));
//                weeklyIndex++;
//
//                // Bubble up the tournament
//                if(listWeekly.size() > weeklyIndex){
//                    // another element exists
//                    elem7 = listWeekly.get(weeklyIndex).getComplexPriority();
//                }
//                else{
//                    elem7 = MIN_VALUE;
//                }
//
//                // redo the matches where the old value was.
//                if (elem7 <= elem8) {
//                    // highestBm doesn't need to be changed yet.
//                    round14win = elem7;
//                }
//                else{
//                    highestBm = highestBm ^ 0b0000001; // need to flip that bit to a zero
//                    round14win = elem8;
//                }
//
//                // round 2
//                if (round13win <= round14win) {
//                    highestBm = highestBm | 0b0010000;
//                    round22win = round13win;
//                }
//                else{
//                    round22win = round14win;
//                }
//
//                // round 3
//                if (round21win <= round22win) {
//                    highestBm = highestBm | 0b1000000; // need to flip that bit to a zero
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
//                if(listMonthly.size() > monthlyIndex){
//                    // another element exists
//                    elem8 = listMonthly.get(monthlyIndex).getComplexPriority();
//                }
//                else{
//                    elem8 = MIN_VALUE;
//                }
//
//                // redo the matches where the old value was.
//                if (elem7 <= elem8) {
//                    highestBm = highestBm | 0b0000001;
//                    round14win = elem7;
//                }
//                else{
//                    round14win = elem8;
//                }
//
//                // round 2
//                if (round13win <= round14win) {
//                    highestBm = highestBm | 0b0010000;
//                    round22win = round13win;
//                }
//                else{
//                    round22win = round14win;
//                }
//
//                // round 3
//                if (round21win <= round22win) {
//                    highestBm = highestBm | 0b1000000; // need to flip that bit to a zero
//                }
//                // sorted order has been restored.
//            }
//
        }// Loop end
//
        assertEquals(35, sortedList.size());
    }
}