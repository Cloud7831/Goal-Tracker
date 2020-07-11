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
    private int selectedPosition = RecyclerView.NO_POSITION;

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
            return oldItem.getIsHidden() == newItem.getIsHidden() &&
                    oldItem.getSessionsTally() == newItem.getSessionsTally() &&
                    oldItem.getQuotaToday() == newItem.getQuotaToday() &&
                    oldItem.getQuotaWeek() == newItem.getQuotaWeek() &&
                    oldItem.getQuotaMonth() == newItem.getQuotaMonth() &&
                    oldItem.getStreak() == newItem.getStreak() &&
                    oldItem.getComplexPriority() == newItem.getComplexPriority() &&

                    oldItem.getTitle().equals(newItem.getTitle()) &&
                    oldItem.getIntention() == newItem.getIntention() &&
                    oldItem.getUserPriority() == newItem.getUserPriority() &&
                    oldItem.getClassification() == newItem.getClassification() &&
                    oldItem.getIsPinned() == newItem.getIsPinned() &&

                    oldItem.getFrequency() == newItem.getFrequency() &&
                    oldItem.getDeadline() == newItem.getDeadline() &&
                    oldItem.getDuration() == newItem.getDuration() &&
                    oldItem.getSessions() == newItem.getSessions() &&
                    oldItem.getScheduledTime() == newItem.getScheduledTime() &&

                    oldItem.getIsMeasurable() == newItem.getIsMeasurable() &&
                    oldItem.getUnits().equals(newItem.getUnits()) &&
                    oldItem.getQuota() == newItem.getQuota();
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

        holder.itemView.setSelected(selectedPosition == position);
        // Set the values of all the goal's textboxes.

        // Set the Title.
        holder.titleTextView.setText(currentGoal.getTitle());


        // Set the streak if applicable.
        if(currentGoal.getClassification() == GoalsContract.GoalEntry.HABIT){

            holder.streakTextView.setVisibility(View.VISIBLE);
            //TODO Use resource string with placeholder.
            String streakText = String.valueOf(currentGoal.getStreak());
            if(currentGoal.getFrequency() == GoalsContract.GoalEntry.DAILYGOAL){
                streakText += " day";
            }
            else if(currentGoal.getFrequency() == GoalsContract.GoalEntry.WEEKLYGOAL){
                streakText += " week";
            }
            else if(currentGoal.getFrequency() == GoalsContract.GoalEntry.MONTHLYGOAL) {
                streakText += " month";
            }

            if(currentGoal.getStreak() != 1){
                streakText += "s"; // make plural.
            }
            holder.streakTextView.setText(streakText);
        }
        else{
            // Events and tasks don't need a streak.
            holder.streakTextView.setVisibility(View.GONE);
        }

//        holder.scheduledTextView.setVisibility(View.GONE); //TODO: use this view later when I know how to schedule goals.

//        String units = currentGoal.getUnits();
//        holder.measureStartValueTextView.setText("0 " + currentGoal.getUnits() + "s");
//        holder.measureEndValueTextView.setText(GoalsContract.GoalEntry.roundAndConvertTime(currentGoal.getQuotaGoalForToday() - currentGoal.getQuotaToday()) + " " + currentGoal.getUnits() + "s");

        // TODO: set the notches to the correct amount so that scolling doesn't give that amount to
        // TODO: a different card.
        // Set up the measurement bar
        if(currentGoal.getIsMeasurable() == 1){
            // Set the amount of notches on the seekBar
            holder.measureSliderView.setMax(currentGoal.calcNotches());
            holder.quotaTextView.setText(currentGoal.todaysQuotaToString(0));
            holder.measureSliderView.setVisibility(View.VISIBLE);
            holder.quotaTextView.setVisibility(View.VISIBLE);
        }
        else{
            // Not measurable, so no need to
            holder.measureSliderView.setVisibility(View.GONE);
            holder.quotaTextView.setVisibility(View.GONE);
        }

        // TODO: if a task has passed the deadline, change the background to a red to indicate failure.
        // TODO: name that shade of red failure_background.
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

                    notifyItemChanged(selectedPosition);
                    if(position == selectedPosition){
                        // Toggle the selection off
                        selectedPosition = RecyclerView.NO_POSITION;
                    }
                    else{
                        selectedPosition = position;
                    }
                    notifyItemChanged(selectedPosition);

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
