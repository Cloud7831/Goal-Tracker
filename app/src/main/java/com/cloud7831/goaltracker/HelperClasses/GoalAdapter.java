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
        holder.textViewTitle.setText(currentGoal.getTitle());
        //Holder.textViewSOMEint.setText(String.valueOf(currentNote.getPriority()));      for ints
    }

    @Override
    public int getItemCount() {
        return goals.size();
    }

    public void setGoals(List<Goal> goals){
        this.goals = goals;
        notifyDataSetChanged();
    }


    class GoalHolder extends RecyclerView.ViewHolder{
        private TextView textViewTitle;
        private TextView textViewQuota;


        public GoalHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.name_text_view); //TODO: rename to title_text_view


        }
    }
}
