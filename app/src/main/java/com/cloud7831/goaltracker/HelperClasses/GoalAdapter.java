package com.cloud7831.goaltracker.HelperClasses;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.cloud7831.goaltracker.Data.GoalsContract;
import com.cloud7831.goaltracker.Objects.Goal;
import com.cloud7831.goaltracker.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class GoalAdapter extends ListAdapter<Goal, GoalAdapter.GoalHolder> {
    private OnItemClickListener listener;

    public GoalAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<Goal> DIFF_CALLBACK = new DiffUtil.ItemCallback<Goal>() {
        @Override
        public boolean areItemsTheSame(@NonNull Goal oldItem, @NonNull Goal newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Goal oldItem, @NonNull Goal newItem) {
            return oldItem.getTitle().equals(newItem.getTitle()) &&
                    oldItem.getUnits().equals(newItem.getUnits()) &&
                    oldItem.getClassification() == newItem.getClassification() &&
                    oldItem.getDeadline() == newItem.getDeadline() &&
                    oldItem.getQuota() == newItem.getQuota()
                    // TODO: update this for all the fields...
                    ;
        }
    };

    @NonNull
    @Override
    public GoalHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.goal_item, parent, false);

        // TODO: implement a custom layout for each of the card types by implementing getItemViewType
        // TODO: https://stackoverflow.com/questions/26245139/how-to-create-recyclerview-with-multiple-view-type
        return new GoalHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GoalHolder holder, int position) {
        Goal currentGoal = getItem(position);

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

            holder.measureStartValueTextView.setText("0 " + currentGoal.getUnits() + "s");
            holder.measureEndValueTextView.setText((currentGoal.getQuotaGoalForToday() - currentGoal.getQuotaToday()) + " " + currentGoal.getUnits() + "s");
        }

        holder.quotaTextView.setText(currentGoal.todaysQuotaToString(0));
    }

    public Goal getGoalAt(int position){
        return getItem(position);
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
                        listener.onItemClick(getItem(position));
                    }
                }
            });
            // Set the seekbar listener so it updates when you slide it.
            measureSliderView.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    // Update the quota for the week here based on the position of the slider
                    measureSliderView.setProgress(progress);

                    // Retrieve the goal so that we can translate slider position into quota.
                    Goal currentGoal = getItem(getAdapterPosition());

                    quotaTextView.setText(currentGoal.todaysQuotaToString(progress));
                    currentGoal.setQuotaTally(currentGoal.calcQuotaProgress(progress));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

        }
    }

    public interface OnItemClickListener{
        void onItemClick(Goal goal);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

}
