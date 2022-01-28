package com.cloud7831.goaltracker.Activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cloud7831.goaltracker.Data.GoalViewModel;
import com.cloud7831.goaltracker.HelperClasses.GoalAdapter;
import com.cloud7831.goaltracker.HelperClasses.WorkoutListAdapter;
import com.cloud7831.goaltracker.Objects.Goals.Goal;
import com.cloud7831.goaltracker.Objects.Goals.WorkoutRelated.ExerciseEntry;
import com.cloud7831.goaltracker.Objects.Goals.WorkoutRelated.Workout;
import com.cloud7831.goaltracker.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class WorkoutListFragment extends Fragment {

    private final WorkoutListAdapter adapter = new WorkoutListAdapter();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: move all the unnecessary lines in here to onStart()
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_workout_list, container, false);





        RecyclerView recyclerView = rootView.findViewById(R.id.workouts_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        List<Workout> workoutList = new ArrayList<>();
        List<ExerciseEntry> entryList = new ArrayList<>();
        entryList.add(new ExerciseEntry("Push-ups", 0, 0, false));
        entryList.add(new ExerciseEntry("Bench", 0, 0, false));
        entryList.add(new ExerciseEntry("Push-downs", 0, 0, false));
        entryList.add(new ExerciseEntry("Bench with dumbells", 0, 0, false));
        entryList.add(new ExerciseEntry("Inclined Press", 0, 0, true));

        workoutList.add(Workout.buildNewWorkout("Chest", entryList));
        adapter.submitList(workoutList);

        adapter.setOnItemClickListener(new WorkoutListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Workout workout) {
//                startGoalEditorFrag(goal.getId(), goal.getType());
            }
        });





        return rootView;
    }
}
