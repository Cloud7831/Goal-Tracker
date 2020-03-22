package com.cloud7831.goaltracker.Activities;

import androidx.loader.content.CursorLoader;
import android.content.Intent;
import androidx.loader.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.cloud7831.goaltracker.Data.GoalsContract;
import com.cloud7831.goaltracker.HelperClasses.GoalsListAdapter;
import com.cloud7831.goaltracker.ItemCards.DailyGoalsItemCard;
import com.cloud7831.goaltracker.ItemCards.GoalsItemCard;
import com.cloud7831.goaltracker.R;

import java.util.ArrayList;

public class GoalsListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int LOADER_ID = 0;

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                GoalsContract.GoalEntry._ID,
                GoalsContract.GoalEntry.GOAL_NAME,
                GoalsContract.GoalEntry.GOAL_INTENTION,
                GoalsContract.GoalEntry.GOAL_QUOTA,
                GoalsContract.GoalEntry.GOAL_FREQUENCY,
                GoalsContract.GoalEntry.GOAL_UNITS};

        return new CursorLoader(getActivity(),   // Parent activity context
                GoalsContract.GoalEntry.CONTENT_URI,           // Provider content URI to query
                projection,                     // Columns to include in the resulting Cursor
                null,                  // No selection clause
                null,               // No selection args
                null);                 // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    public GoalsListFragment(){
        // Required empty public constructor.
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_goal_list, container, false);

        //TODO: Delete this. This is just placeholder dummy data so I can see what it looks like with the UI.
        ArrayList<GoalsItemCard> itemCards = new ArrayList<GoalsItemCard>();

        //getGoalsList(itemCards);

        //--------------------------------------------------------------------------------------------
        itemCards.add(new DailyGoalsItemCard());
        itemCards.add(new DailyGoalsItemCard());
        //TODO: Delete -------------------------------------------------------------------------------

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), GoalEditorActivity.class);
                startActivity(intent);
            }
        });

        GoalsListAdapter goalsListAdapter = new GoalsListAdapter(getActivity(), itemCards);

        ListView listView = (ListView) rootView.findViewById(R.id.list);

        listView.setAdapter(goalsListAdapter);

        return rootView;
    }

}
