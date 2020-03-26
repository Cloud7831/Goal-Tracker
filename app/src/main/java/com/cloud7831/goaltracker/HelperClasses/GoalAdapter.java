package com.cloud7831.goaltracker.HelperClasses;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cloud7831.goaltracker.Objects.Goal;
import com.cloud7831.goaltracker.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class GoalAdapter extends RecyclerView.Adapter<GoalAdapter.GoalHolder> {
    private List<Goal> goals = new ArrayList<>();

    @NonNull
    @Override
    public GoalHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.goal_item, parent, false);
        return new GoalHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GoalHolder holder, int position) {
        Goal currentGoal = goals.get(position);

        // Set the values of all the goal's textboxes.
        holder.titleTextView.setText(currentGoal.getTitle());
        holder.streakTextView.setText(String.valueOf(currentGoal.getPriority()) + " days"); //TODO Use resource string with placeholder.
        holder.scheduledTextView.setVisibility(View.GONE); //TODO: use this view later when I know how to schedule goals.
        holder.measureStartValueTextView.setText("0 " + currentGoal.getUnits());
        holder.measureEndValueTextView.setText(calcEndValue(currentGoal.getQuota(), currentGoal.getQuotaTally()) + " " + currentGoal.getUnits());

        holder.quotaTextView.setText("0/" + calcEndValue(currentGoal.getQuota(), currentGoal.getQuotaTally()));
    }

    @Override
    public int getItemCount() {
        return goals.size();
    }

    public void setGoals(List<Goal> goals){
        this.goals = goals;
        notifyDataSetChanged();
    }

    public Goal getGoalAt(int position){
        return goals.get(position);
    }

    class GoalHolder extends RecyclerView.ViewHolder{
        private TextView titleTextView;
        private TextView streakTextView;
        private TextView scheduledTextView;

        private TextView measureStartValueTextView;
        private TextView measureEndValueTextView;
        //private View measureSliderView;

        private TextView quotaTextView;




        public GoalHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.name_text_view); //TODO: rename to title_text_view
            streakTextView = itemView.findViewById(R.id.streak_text_view);
            scheduledTextView = itemView.findViewById(R.id.scheduled_time_text_view);
            measureStartValueTextView = itemView.findViewById(R.id.measure_start_value);
            measureEndValueTextView = itemView.findViewById(R.id.measure_end_value);
            //measureSliderView = itemView.findViewById(R.id.goal_measure_slider);
            quotaTextView = itemView.findViewById(R.id.measure_completed_today);

        }
    }

    private int calcEndValue(int quota, int quotaTally){
        //TODO: do (quota - quotaTally)/(sessions - sessionsTally) and then round it to a nice number

        return (quota - quotaTally)/4;
    }

}
