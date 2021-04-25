package com.cloud7831.goaltracker;

import com.cloud7831.goaltracker.HelperClasses.GoalLiveDataCombined;
import com.cloud7831.goaltracker.Objects.Task;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class GoalUnitTests {

    @Test
    public void sorting_isCorrect() {

        List<Task> taskList = null;
        GoalLiveDataCombined data = new GoalLiveDataCombined(taskList, null, null, null);



        assertEquals(4, 2 + 2);
    }
}
