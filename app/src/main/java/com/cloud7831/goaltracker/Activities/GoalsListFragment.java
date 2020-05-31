package com.cloud7831.goaltracker.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.ClipData;
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
    private GoalAdapter adapter = new GoalAdapter();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: move all the unnecessary lines in here to onStart()
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_goal_list, container, false);
        setHasOptionsMenu(true);

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

//        final GoalAdapter adapter = new GoalAdapter();
        recyclerView.setAdapter(adapter);

        goalViewModel = new ViewModelProvider(getActivity(), ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication())).get(GoalViewModel.class);
        // TODO: Add an option for goalViewModel.getAllGoals() in the options menu.

        // TODO: want to check if the date has changed. If it has, renew the list and update all
        // TODO: entries in the database.

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

                int quotaRecorded = currentGoal.getQuotaTally();
                currentGoal.addToQuotaToday(quotaRecorded);
                // TODO: calculate the updated priority for the task.
                // TODO: there shouldn't be a blank space when something is swiped off the list.
                if(direction == ItemTouchHelper.LEFT){
                    // User wanted to clear the goal from the list.
                    currentGoal.setIsHidden(true);
                }
                else if(direction == ItemTouchHelper.RIGHT){
                    if(currentGoal.getTodaysQuota() >= currentGoal.getQuotaToday()){
                        // Today's goal has been met, so hide the goal
                        currentGoal.setIsHidden(true);
                    }
                    System.out.println(currentGoal);
                    // TODO: check if quota is 0, because then something probably messed up.
                    // TODO: Should it do anything other than this?
                }
                goalViewModel.update(currentGoal);
                Toast.makeText(getContext(), "Goal updated with " + quotaRecorded + " quota for today.", Toast.LENGTH_SHORT).show();
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
//                Intent intent = new Intent(getActivity(), GoalEditorActivity.class);
//                // Get all the goals variables stored so the user doesn't have to re-enter all the values.
//                intent.putExtra(GoalEditorActivity.EXTRA_ID, goal.getId());
//                // Overview
//                intent.putExtra(GoalEditorActivity.EXTRA_TITLE, goal.getTitle());
//                intent.putExtra(GoalEditorActivity.EXTRA_INTENTION, goal.getIntention());
//                intent.putExtra(GoalEditorActivity.EXTRA_PRIORITY, goal.getUserPriority());
//                intent.putExtra(GoalEditorActivity.EXTRA_CLASSIFICATION, goal.getClassification());
//                intent.putExtra(GoalEditorActivity.EXTRA_IS_PINNED, goal.getIsPinned());
//                // Schedule
//                intent.putExtra(GoalEditorActivity.EXTRA_FREQUENCY, goal.getFrequency());
//                intent.putExtra(GoalEditorActivity.EXTRA_SESSIONS, goal.getSessions());
//                intent.putExtra(GoalEditorActivity.EXTRA_DEADLINE, goal.getDeadline());
//                intent.putExtra(GoalEditorActivity.EXTRA_DURATION, goal.getDuration());
//                intent.putExtra(GoalEditorActivity.EXTRA_SCHEDULED_TIME, goal.getScheduledTime());
//
//                // Measurement
//                intent.putExtra(GoalEditorActivity.EXTRA_QUOTA, goal.getQuota());
//                intent.putExtra(GoalEditorActivity.EXTRA_UNITS, goal.getUnits());
//                intent.putExtra(GoalEditorActivity.EXTRA_IS_MEASUREABLE, goal.getIsMeasurable());
//
//                startActivityForResult(intent, GOAL_EDITOR_EDIT_REQUEST);


                //TODO: Need to make the onclick highlight the goal so that it's selected.

                // Prepare the container with the fragments we will need.
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                //Prepare the data to be sent
                Bundle bundle = new Bundle();
                bundle.putInt(GoalEditorActivity.KEY_GOAL_ID, goal.getId());

                GoalEditorActivity editorActivity = new GoalEditorActivity();
                editorActivity.setArguments(bundle);

                // Add in the Goal List fragment
                fragmentTransaction.replace(R.id.fragment_container, editorActivity);
                fragmentTransaction.addToBackStack(null);

                // Commit all the changes.
                fragmentTransaction.commit();
            }
        });

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(getActivity(), GoalEditorActivity.class);
//                startActivityForResult(intent, GOAL_EDITOR_ADD_REQUEST);

                // Prepare the container with the fragments we will need.
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                //Prepare the data to be sent
                Bundle bundle = new Bundle();
                bundle.putInt(GoalEditorActivity.KEY_GOAL_ID, -1); // Indicates no ID

                GoalEditorActivity editorActivity = new GoalEditorActivity();
                editorActivity.setArguments(bundle);

                // Add in the Goal List fragment
                fragmentTransaction.replace(R.id.fragment_container, editorActivity);
                fragmentTransaction.addToBackStack(null);

                // Commit all the changes.
                fragmentTransaction.commit();
            }
        });

        return rootView;
    }

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
            case R.id.create_dummy_goals:
                goalViewModel.createDummyGoals();
                return true;

            case R.id.show_all_goals:
                goalViewModel.getAllGoals().observe(this, new Observer<List<Goal>>(){
                    @Override
                    public void onChanged(@Nullable List<Goal> goals){
                        //update recyclerView
                        adapter.submitList(goals);
                    }
                });
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveGoalProgress(Goal goal){

    }

}
