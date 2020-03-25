package com.cloud7831.goaltracker.Activities;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.loader.content.CursorLoader;

import android.app.Activity;
import android.content.Intent;
import androidx.loader.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.cloud7831.goaltracker.Data.GoalViewModel;
import com.cloud7831.goaltracker.HelperClasses.GoalAdapter;
import com.cloud7831.goaltracker.Objects.Goal;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.cloud7831.goaltracker.Data.GoalsContract;
//import com.cloud7831.goaltracker.HelperClasses.GoalsListAdapter;
//import com.cloud7831.goaltracker.ItemCards.DailyGoalsItemCard;
//import com.cloud7831.goaltracker.ItemCards.GoalsItemCard;
import com.cloud7831.goaltracker.R;

import java.util.ArrayList;
import java.util.List;

public class GoalsListFragment extends Fragment{
    public static final int GOAL_EDITOR_REQUEST = 1;


    private GoalViewModel goalViewModel;
    private static final int LOADER_ID = 0;

//    @Override
//    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
//        String[] projection = {
//                GoalsContract.GoalEntry._ID,
//                GoalsContract.GoalEntry.GOAL_NAME,
//                GoalsContract.GoalEntry.GOAL_INTENTION,
//                GoalsContract.GoalEntry.GOAL_QUOTA,
//                GoalsContract.GoalEntry.GOAL_FREQUENCY,
//                GoalsContract.GoalEntry.GOAL_UNITS};
//
//        return new CursorLoader(getActivity(),   // Parent activity context
//                GoalsContract.GoalEntry.CONTENT_URI,           // Provider content URI to query
//                projection,                     // Columns to include in the resulting Cursor
//                null,                  // No selection clause
//                null,               // No selection args
//                null);                 // Default sort order
//    }

//    @Override
//    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
//
//    }
//
//    @Override
//    public void onLoaderReset(Loader<Cursor> loader) {
//    }
//
//    public GoalsListFragment(){
//        // Required empty public constructor.
//    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
//        getLoaderManager().initLoader(LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_goal_list, container, false);

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        final GoalAdapter adapter = new GoalAdapter();
        recyclerView.setAdapter(adapter);

        goalViewModel = new ViewModelProvider(getActivity(), ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication())).get(GoalViewModel.class);
        goalViewModel.getAllGoals().observe(this, new Observer<List<Goal>>(){
            @Override
            public void onChanged(@Nullable List<Goal> goals){
                //update recyclerView
                adapter.setGoals(goals);
            }
        });

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), GoalEditorActivity.class);
                startActivityForResult(intent, GOAL_EDITOR_REQUEST);
            }
        });




//        //TODO: Delete this. This is just placeholder dummy data so I can see what it looks like with the UI.
//        ArrayList<GoalsItemCard> itemCards = new ArrayList<GoalsItemCard>();
//
//        //getGoalsList(itemCards);
//
//        //--------------------------------------------------------------------------------------------
//        itemCards.add(new DailyGoalsItemCard());
//        itemCards.add(new DailyGoalsItemCard());
//        //TODO: Delete -------------------------------------------------------------------------------
//
//
//        GoalsListAdapter goalsListAdapter = new GoalsListAdapter(getActivity(), itemCards);
//
//        ListView listView = (ListView) rootView.findViewById(R.id.list);
//
//        listView.setAdapter(goalsListAdapter);

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GOAL_EDITOR_REQUEST && resultCode == Activity.RESULT_OK){
            String title = data.getStringExtra(GoalEditorActivity.EXTRA_TITLE);
            int quota = data.getIntExtra(GoalEditorActivity.EXTRA_QUOTA, 0);
            String intention = data.getStringExtra(GoalEditorActivity.EXTRA_INTENTION);
            String freq = data.getStringExtra(GoalEditorActivity.EXTRA_FREQUENCY);
            String units = data.getStringExtra(GoalEditorActivity.EXTRA_UNITS);
            int priority = data.getIntExtra(GoalEditorActivity.EXTRA_PRIORITY, 0);

            Goal goal = Goal.buildUserGoal(title, intention, priority, "habit", 0,
                    freq, 0, 0, units, quota);
            goalViewModel.insert(goal);

            Toast.makeText(getContext(), "Goal saved", Toast.LENGTH_SHORT).show();

        } else{
            Toast.makeText(getContext(), "Goal not saved", Toast.LENGTH_SHORT).show();
        }
    }

}
