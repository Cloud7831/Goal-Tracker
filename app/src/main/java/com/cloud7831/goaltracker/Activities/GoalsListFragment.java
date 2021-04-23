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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cloud7831.goaltracker.Data.GoalViewModel;
import com.cloud7831.goaltracker.HelperClasses.GoalAdapter;
import com.cloud7831.goaltracker.Objects.GoalRefactor;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.cloud7831.goaltracker.Data.GoalsContract;
import com.cloud7831.goaltracker.R;

import java.util.ArrayList;
import java.util.List;

public class GoalsListFragment extends Fragment{
    public static final int GOAL_EDITOR_ADD_REQUEST = 1;
    public static final int GOAL_EDITOR_EDIT_REQUEST = 2;

    public static final String LOGTAG = "GoalsListFragment";

    private GoalViewModel goalViewModel;
    private GoalAdapter adapter = new GoalAdapter();
    private int selectedGoalID = -1;


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
//        recyclerView.setHasFixedSize(true);

//        final GoalAdapter adapter = new GoalAdapter();
        recyclerView.setAdapter(adapter);

        goalViewModel = new ViewModelProvider(getActivity(), ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication())).get(GoalViewModel.class);
        // TODO: Add an option for goalViewModel.getAllGoals() in the options menu.

        // TODO: want to check if the date has changed. If it has, renew the list and update all
        // TODO: entries in the database.

//        goalViewModel.getTodaysGoals().observe(this, new Observer<List<GoalRefactor>>(){
//            @Override
//            public void onChanged(@Nullable List<GoalRefactor> goals){
//                //update recyclerView
//                adapter.submitList(goals);
//            }
//        });

        goalViewModel.getTodaysGoals().observe(this, new Observer<List<GoalRefactor>>(){
            @Override
            public void onChanged(@Nullable List<GoalRefactor> list){
                //update recyclerView
                adapter.submitList(list);
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                // Used for drag and drop behaviour. Not needed for our app.
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                GoalRefactor currentGoal = adapter.getGoalAt(viewHolder.getAdapterPosition());

//                Toast.makeText(getContext(), "Goal being recorded with: " + currentGoal.getQuotaTally(), Toast.LENGTH_SHORT).show();
//                Log.i(LOGTAG, "Goal Title: " + currentGoal.getTitle() + " is being saved with: " + currentGoal.getQuotaTally() );


                currentGoal.onSwipe(direction);

                goalViewModel.update(currentGoal);
            }

            @Override
            public float getSwipeEscapeVelocity(float defaultValue){
                return defaultValue * 4;
            }
        }).attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener(new GoalAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(GoalRefactor goal) {

//                selectedGoalID = goal.getId();

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

//        // Setup FAB to open EditorActivity
//        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                // Prepare the container with the fragments we will need.
//                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//
//                //Prepare the data to be sent
//                Bundle bundle = new Bundle();
//                bundle.putInt(GoalEditorActivity.KEY_GOAL_ID, -1); // Indicates no ID
//
//                GoalEditorActivity editorActivity = new GoalEditorActivity();
//                editorActivity.setArguments(bundle);
//
//                // Add in the Goal List fragment
//                fragmentTransaction.replace(R.id.fragment_container, editorActivity);
//                fragmentTransaction.addToBackStack(null);
//
//                // Commit all the changes.
//                fragmentTransaction.commit();
//            }
//        });

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.goal_list_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_new_goal:

                // Prepare the container with the fragments we will need.
                FragmentManager fragmentManagerNewGoal = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransactionNewGoal = fragmentManagerNewGoal.beginTransaction();

                //Prepare the data to be sent
                Bundle bundleNewGoal = new Bundle();
                bundleNewGoal.putInt(GoalEditorActivity.KEY_GOAL_ID, -1); // Indicates no ID

                GoalEditorActivity editorActivityNewGoal = new GoalEditorActivity();
                editorActivityNewGoal.setArguments(bundleNewGoal);

                // Add in the Goal List fragment
                fragmentTransactionNewGoal.replace(R.id.fragment_container, editorActivityNewGoal);
                fragmentTransactionNewGoal.addToBackStack(null);

                // Commit all the changes.
                fragmentTransactionNewGoal.commit();
                return true;
            case R.id.action_delete_all_goals:
                goalViewModel.deleteAllGoals();
                Toast.makeText(getContext(), "All goals have been deleted!", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.create_dummy_goals:
                goalViewModel.createDummyGoals();
                return true;

//            case R.id.action_edit_goal:
//                if(selectedGoalID != -1){
//                    // Prepare the container with the fragments we will need.
//                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//
//                    //Prepare the data to be sent
//                    Bundle bundle = new Bundle();
//                    bundle.putInt(GoalEditorActivity.KEY_GOAL_ID, selectedGoalID);
//
//                    GoalEditorActivity editorActivity = new GoalEditorActivity();
//                    editorActivity.setArguments(bundle);
//
//                    // Add in the Goal List fragment
//                    fragmentTransaction.replace(R.id.fragment_container, editorActivity);
//                    fragmentTransaction.addToBackStack(null);
//
//                    // Commit all the changes.
//                    fragmentTransaction.commit();
//                }
//                return true;
//
//            case R.id.action_delete_goal:
//                Log.i(LOGTAG, "clicked delete menu option.");
//                    if(selectedGoalID != -1){
//                        // TODO: make a confirmation dialog confirming the user wants to delete the action
//                        Log.i(LOGTAG, "attempting to delete.");
//                        goalViewModel.deleteByID(selectedGoalID);
//                        Toast.makeText(getContext(), "Goal with id " + selectedGoalID + " has been deleted!", Toast.LENGTH_SHORT).show();
//                    }
//                return true;

            case R.id.action_show_all_goals:
                goalViewModel.getAllGoals().observe(this, new Observer<List<GoalRefactor>>(){
                    @Override
                    public void onChanged(@Nullable List<GoalRefactor> goals){
                        //update recyclerView
                        adapter.submitList(goals);
                    }
                });
            default:
                Log.i(LOGTAG, "default menu option.");
                return super.onOptionsItemSelected(item);
        }
    }

}
