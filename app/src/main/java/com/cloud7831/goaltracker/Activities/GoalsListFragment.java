package com.cloud7831.goaltracker.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cloud7831.goaltracker.Data.GoalViewModel;
import com.cloud7831.goaltracker.HelperClasses.GoalAdapter;
import com.cloud7831.goaltracker.Objects.Goal;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.cloud7831.goaltracker.Data.GoalsContract;
import com.cloud7831.goaltracker.R;

import java.util.ArrayList;
import java.util.List;

public class GoalsListFragment extends Fragment{
    public static final int GOAL_EDITOR_ADD_REQUEST = 1;
    public static final int GOAL_EDITOR_EDIT_REQUEST = 2;

    private GoalViewModel goalViewModel;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_goal_list, container, false);
        setHasOptionsMenu(true);

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        final GoalAdapter adapter = new GoalAdapter();
        recyclerView.setAdapter(adapter);

        goalViewModel = new ViewModelProvider(getActivity(), ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication())).get(GoalViewModel.class);
        // TODO: Add an option for goalViewModel.getAllGoals() in the options menu.
        goalViewModel.getTodaysGoals().observe(this, new Observer<List<Goal>>(){
            @Override
            public void onChanged(@Nullable List<Goal> goals){
                //update recyclerView
                adapter.submitList(goals);
            }
        });

        // TODO: re-enable this when I have the functionality for hiding
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                // Used for drag and drop behaviour. Not needed for our app.
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Goal currentGoal = adapter.getGoalAt(viewHolder.getAdapterPosition());

                //goalViewModel.delete(adapter.getGoalAt(viewHolder.getAdapterPosition()));
                Toast.makeText(getContext(), "Goal deleted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public float getSwipeEscapeVelocity(float defaultValue){
                // TODO: Find a value that's not too sensitive, but also not too hard to swipe.
                return defaultValue * 4;
            }
        }).attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener(new GoalAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Goal goal) {
                Intent intent = new Intent(getActivity(), GoalEditorActivity.class);
                // Get all the goals variables stored so the user doesn't have to re-enter all the values.
                intent.putExtra(GoalEditorActivity.EXTRA_ID, goal.getId());
                // Overview
                intent.putExtra(GoalEditorActivity.EXTRA_TITLE, goal.getTitle());
                intent.putExtra(GoalEditorActivity.EXTRA_INTENTION, goal.getIntention());
                intent.putExtra(GoalEditorActivity.EXTRA_PRIORITY, goal.getPriority());
                intent.putExtra(GoalEditorActivity.EXTRA_CLASSIFICATION, goal.getClassification());
                intent.putExtra(GoalEditorActivity.EXTRA_IS_PINNED, goal.getIsPinned());
                // Schedule
                intent.putExtra(GoalEditorActivity.EXTRA_FREQUENCY, goal.getFrequency());
                intent.putExtra(GoalEditorActivity.EXTRA_SESSIONS, goal.getSessions());
                intent.putExtra(GoalEditorActivity.EXTRA_DEADLINE, goal.getDeadline());
                intent.putExtra(GoalEditorActivity.EXTRA_DURATION, goal.getDuration());
                intent.putExtra(GoalEditorActivity.EXTRA_SCHEDULED_TIME, goal.getScheduledTime());

                // Measurement
                intent.putExtra(GoalEditorActivity.EXTRA_QUOTA, goal.getQuota());
                intent.putExtra(GoalEditorActivity.EXTRA_UNITS, goal.getUnits());
                intent.putExtra(GoalEditorActivity.EXTRA_IS_MEASUREABLE, goal.getIsMeasurable());

                startActivityForResult(intent, GOAL_EDITOR_EDIT_REQUEST);
            }
        });

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), GoalEditorActivity.class);
                startActivityForResult(intent, GOAL_EDITOR_ADD_REQUEST);
            }
        });

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GOAL_EDITOR_ADD_REQUEST && resultCode == Activity.RESULT_OK){
            String title = data.getStringExtra(GoalEditorActivity.EXTRA_TITLE);
            int quota = data.getIntExtra(GoalEditorActivity.EXTRA_QUOTA, -1);
            int intention = data.getIntExtra(GoalEditorActivity.EXTRA_INTENTION, 0);
            int freq = data.getIntExtra(GoalEditorActivity.EXTRA_FREQUENCY, 0);
            String units = data.getStringExtra(GoalEditorActivity.EXTRA_UNITS);
            int priority = data.getIntExtra(GoalEditorActivity.EXTRA_PRIORITY, 0);
            int isMeasurable = data.getIntExtra(GoalEditorActivity.EXTRA_IS_MEASUREABLE, 0);
            int sessions = data.getIntExtra(GoalEditorActivity.EXTRA_SESSIONS, 0);
            int isPinned = data.getIntExtra(GoalEditorActivity.EXTRA_IS_PINNED, 0);
            int classification = data.getIntExtra(GoalEditorActivity.EXTRA_CLASSIFICATION, 0);

            Goal goal = Goal.buildUserGoal(title, intention, priority, classification, isPinned, 0,
                    sessions, 0, freq, 0, isMeasurable, units, quota);
            goalViewModel.insert(goal);

            Toast.makeText(getContext(), "Goal saved with " + isMeasurable, Toast.LENGTH_SHORT).show();

        }
        else if(requestCode == GOAL_EDITOR_EDIT_REQUEST && resultCode == Activity.RESULT_OK){
            int id = data.getIntExtra(GoalEditorActivity.EXTRA_ID, -1);
            if(id == -1){
                Toast.makeText(getActivity(), "Goal can't be updated", Toast.LENGTH_SHORT).show();
                return;
            }

            String title = data.getStringExtra(GoalEditorActivity.EXTRA_TITLE);
            int quota = data.getIntExtra(GoalEditorActivity.EXTRA_QUOTA, 0);
            int intention = data.getIntExtra(GoalEditorActivity.EXTRA_INTENTION, GoalsContract.GoalEntry.UNDEFINED);
            int freq = data.getIntExtra(GoalEditorActivity.EXTRA_FREQUENCY, 0);
            String units = data.getStringExtra(GoalEditorActivity.EXTRA_UNITS);
            int priority = data.getIntExtra(GoalEditorActivity.EXTRA_PRIORITY, 0);
            int isMeasurable = data.getBooleanExtra(GoalEditorActivity.EXTRA_IS_MEASUREABLE, true) ? 1 : 0;

            // Create a new goal with the correct values to replace the current goal
            Goal goal = Goal.buildUserGoal(title, intention, priority, 1, 0, 0,
                    0, 0, freq, 0, isMeasurable, units, quota);
            goal.setId(id);
            goalViewModel.update(goal);

            Toast.makeText(getActivity(), "Goal updated", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getContext(), "Goal not saved", Toast.LENGTH_SHORT).show();
        }
    }


    //TODO: I don't know how to show the menu in a fragment.
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.goal_list_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.delete_all_goals:
                goalViewModel.deleteAllGoals();
                Toast.makeText(getContext(), "All goals have been deleted!", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
