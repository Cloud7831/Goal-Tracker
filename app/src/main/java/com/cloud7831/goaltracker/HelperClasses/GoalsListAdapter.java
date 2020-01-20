package com.cloud7831.goaltracker.HelperClasses;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.cloud7831.goaltracker.Data.GoalsContract;
import com.cloud7831.goaltracker.Data.GoalsContract.*;
import com.cloud7831.goaltracker.ItemCards.GoalsItemCard;
import com.cloud7831.goaltracker.R;

import java.util.ArrayList;

public class GoalsListAdapter extends ArrayAdapter<GoalsItemCard> {

    public GoalsListAdapter(Activity context, ArrayList<GoalsItemCard> cards){
        super(context, 0, cards);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View listItemView = convertView;

        GoalsItemCard currentCard = getItem(position);
        GoalsContract.GoalsInterval cardInterval = currentCard.getInterval();

        if(listItemView == null){
            // This is the first time making this item card or the item card was recycled and needs to be recreated.

            // The layout for the card type needs to be created based on the type.

            switch(cardInterval){
                case DAILYGOAL:
                    listItemView = LayoutInflater.from(getContext()).inflate(R.layout.daily_goal_card_layout, parent, false);
                    break;
                default:
                    Log.e("WorkoutAdapter", "Unknown card type when creating view.");
                    return listItemView;
            }
        }

        // Fill in the details of the item card.

        // Set the name of the goal
        TextView nameView = listItemView.findViewById(R.id.name_text_view);
        nameView.setText(currentCard.getName());

        // Set the streak of the goal
        if(currentCard.hasStreak()){
            TextView streakView = listItemView.findViewById(R.id.streak_text_view);
            streakView.setText(currentCard.getStreak());
        }

        // Set the description of the goal
        if(currentCard.hasScheduledTime()){
            TextView scheduleView = listItemView.findViewById(R.id.scheduled_time_text_view);
            scheduleView.setText(currentCard.getScheduledTime());
        }

        return listItemView; // Return the view for the Item Card we just created.
    }

}
