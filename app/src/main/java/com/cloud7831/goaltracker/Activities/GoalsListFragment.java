package com.cloud7831.goaltracker.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.support.design.widget.FloatingActionButton;

import com.cloud7831.goaltracker.HelperClasses.GoalsListAdapter;
import com.cloud7831.goaltracker.ItemCards.DailyGoalsItemCard;
import com.cloud7831.goaltracker.ItemCards.GoalsItemCard;
import com.cloud7831.goaltracker.R;

import java.util.ArrayList;

public class GoalsListFragment extends Fragment {

    public GoalsListFragment(){
        // Required empty public constructor.
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_goal_list, container, false);

        //TODO: Delete this. This is just placeholder dummy data so I can see what it looks like with the UI.
        final ArrayList<GoalsItemCard> itemCards = new ArrayList<GoalsItemCard>();
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
