package com.cloud7831.goaltracker;

import android.util.Log;

import com.cloud7831.goaltracker.HelperClasses.GoalLiveDataCombined;
import com.cloud7831.goaltracker.Objects.DailyHabit;
import com.cloud7831.goaltracker.Objects.GoalRefactor;
import com.cloud7831.goaltracker.Objects.Task;
import com.cloud7831.goaltracker.Objects.WeeklyHabit;

import org.junit.Test;

import java.lang.reflect.Array;
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
        List<Task> taskList = new ArrayList<>();
        taskList.add(new Task("test", 4, 1, 1, 1, 1, "days", 25, 0, 0, 0, 1, 0, 0, 0));

        List<DailyHabit> dailyList = new ArrayList<>();
        dailyList.add(new DailyHabit("test", 4, 1, 1, 1, 1, "days", 25, 0, 0, 0, 3, 0, 0, 0, 6));
        dailyList.add(new DailyHabit("test2", 2, 1, 1, 1, 1, "days", 25, 0, 0, 0, 3, 0, 0, 0, 0));
        dailyList.add(new DailyHabit("test3", 1, 1, 1, 1, 1, "days", 25, 0, 0, 0, 3, 0, 0, 0, 10));

        List<WeeklyHabit> weeklyList = new ArrayList<>();
        weeklyList.add(new WeeklyHabit("test", 1, 1, 1, 1, 1, "days", 25, 0, 0, 0, 3, 0, 0, 0, 6, 1));
        weeklyList.add(new WeeklyHabit("test2", 5, 1, 1, 1, 1, "days", 25, 0, 0, 0, 3, 0, 0, 0, 0, 1));
        weeklyList.add(new WeeklyHabit("test3", 3, 1, 1, 1, 1, "days", 25, 0, 0, 0, 3, 0, 0, 0, 10, 0));

        GoalLiveDataCombined data = new GoalLiveDataCombined(taskList, dailyList, null, null);

        List<GoalRefactor> sorted = data.sortGoalListByComPriority();
        int lastPriority = 2000000000;

        for (GoalRefactor i: sorted) {
            assert(i.getComplexPriority() <= lastPriority);
            lastPriority = i.getComplexPriority();
        }
        assertEquals(4, sorted.size());


        assertEquals(4, 2 + 2);
    }
}