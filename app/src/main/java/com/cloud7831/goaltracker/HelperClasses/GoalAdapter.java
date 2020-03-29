package com.cloud7831.goaltracker.HelperClasses;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.cloud7831.goaltracker.Objects.Goal;
import com.cloud7831.goaltracker.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class GoalAdapter extends RecyclerView.Adapter<GoalAdapter.GoalHolder> {
    private List<Goal> goals = new ArrayList<>();
    private OnItemClickListener listener;

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
        holder.streakTextView.setText(String.valueOf(currentGoal.getStreak()) + " days"); //TODO Use resource string with placeholder.
        holder.scheduledTextView.setVisibility(View.GONE); //TODO: use this view later when I know how to schedule goals.

        String units = currentGoal.getUnits();
        if(units != null){
            if(units.equals("minutes")) {
                // Just make it a short form for more compact UI
                units = "mins";
            }

            holder.measureStartValueTextView.setText("0 " + currentGoal.getUnits());
            holder.measureEndValueTextView.setText(calcEndValue(currentGoal.getQuota(), currentGoal.getQuotaTally(), currentGoal.getSessions(), currentGoal.getSessionsTally()) + " " + currentGoal.getUnits());
        }

        holder.quotaTextView.setText("0/" + calcEndValue(currentGoal.getQuota(), currentGoal.getQuotaTally(), currentGoal.getSessions(), currentGoal.getSessionsTally()) + units);

//        // Set the seekbar listener so it updates when you slide it.
//        holder.measureSliderView.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                holder.quotaTextView
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });
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
        private SeekBar measureSliderView;

        private TextView quotaTextView;




        public GoalHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.name_text_view); //TODO: rename to title_text_view
            streakTextView = itemView.findViewById(R.id.streak_text_view);
            scheduledTextView = itemView.findViewById(R.id.scheduled_time_text_view);
            measureStartValueTextView = itemView.findViewById(R.id.measure_start_value);
            measureEndValueTextView = itemView.findViewById(R.id.measure_end_value);
            measureSliderView = itemView.findViewById(R.id.goal_measure_slider);
            quotaTextView = itemView.findViewById(R.id.measure_completed_today);

            // Set the onClickListener so that you can edit or delete a goal.
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    int position = getAdapterPosition();
                    if(listener != null && position != RecyclerView.NO_POSITION){
                        listener.onItemClick(goals.get(position));
                    }
                }
            });

        }
    }

    private int calcEndValue(int quota, int quotaTally, int sessions, int sessionsTally){
        //TODO: do (quota - quotaTally)/(sessions - sessionsTally) and then round it to a nice number

        if(sessions == 0){
            sessions = 1;
            sessionsTally = 0;
        }
        if(quota < quotaTally){
            sessions = 4;
            quotaTally = 0;
        }
        return (quota - quotaTally)/(sessions - sessionsTally);
    }

    public interface OnItemClickListener{
        void onItemClick(Goal goal);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

}
