package com.cloud7831.goaltracker.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

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
import com.cloud7831.goaltracker.Objects.Goals.Goal;

import com.cloud7831.goaltracker.Data.GoalsContract.*;
import com.cloud7831.goaltracker.R;

import java.util.List;

public class GoalsListFragment extends Fragment{
    public static final String LOGTAG = "GoalsListFragment";

    private GoalViewModel goalViewModel;
    private final GoalAdapter adapter = new GoalAdapter();
    private boolean showTodaysGoals = true;
    private final Observer<List<Goal>> goalsObs = new Observer<List<Goal>>(){
        @Override
        public void onChanged(@Nullable List<Goal> list){
            //update recyclerView whenever the database information changes.
            adapter.submitList(list);
        }
    };

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

        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        goalViewModel = new ViewModelProvider(getActivity(), ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication())).get(GoalViewModel.class);

        // Sets the goalViewModel's list to the list retrieved from the database.
        observeList();

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                // Used for drag and drop behaviour. Not needed for our app.
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Goal currentGoal = adapter.getGoalAt(viewHolder.getAdapterPosition());

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
            public void onItemClick(Goal goal) {
                startGoalEditorFrag(goal.getId(), goal.getType());
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
            case R.id.action_new_goal:
                // new goals don't have an id or type yet.
                startGoalEditorFrag(-1, GoalEntry.UNDEFINED);
                return true;
            case R.id.action_delete_all_goals:
                // TODO: bring up a confirmation dialog before allowing the user to delete everything.
                goalViewModel.deleteAllGoals();
                Toast.makeText(getContext(), "All goals have been deleted!", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.create_dummy_goals:
                goalViewModel.createDummyGoals();
                return true;
            case R.id.action_show_all_goals:
                showTodaysGoals = !showTodaysGoals;
                if(showTodaysGoals){
                    item.setTitle(R.string.menu_option_show_all);
                }
                else{
                    item.setTitle(R.string.menu_option_show_today);
                }

                observeList();

                return true;
            default:
                Log.i(LOGTAG, "default menu option.");
                return super.onOptionsItemSelected(item);
        }
    }

    private void observeList(){
        if(showTodaysGoals) {
            // Can't have the adapter observing two different lists, so remove the others.
            goalViewModel.getAllGoals().removeObservers(this);

            if(!goalViewModel.getTodaysGoals().hasActiveObservers()){
                // Only observe if this isn't already observing.
                goalViewModel.getTodaysGoals().observe(this,goalsObs);
            }
        }
        else{
            goalViewModel.getTodaysGoals().removeObservers(this);
            if(!goalViewModel.getAllGoals().hasActiveObservers()){
                goalViewModel.getAllGoals().observe(this, goalsObs);
            }
        }
    }

    private void startGoalEditorFrag(int goalID, int goalType){
        // Prepare the container with the fragments we will need.
        FragmentManager fragmentManagerNewGoal = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransactionNewGoal = fragmentManagerNewGoal.beginTransaction();

        //Prepare the data to be sent
        Bundle bundleNewGoal = new Bundle();
        bundleNewGoal.putInt(GoalEntry.KEY_GOAL_ID, goalID); // Indicates no ID
        bundleNewGoal.putInt(GoalEntry.KEY_GOAL_TYPE, goalType);

        GoalEditorFragment editorFragmentNewGoal = new GoalEditorFragment();
        editorFragmentNewGoal.setArguments(bundleNewGoal);

        // Add in the Goal List fragment
        fragmentTransactionNewGoal.replace(R.id.fragment_container, editorFragmentNewGoal);
        fragmentTransactionNewGoal.addToBackStack(null);

        // Commit all the changes.
        fragmentTransactionNewGoal.commit();
    }

}
