package com.cloud7831.goaltracker.HelperClasses;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.cloud7831.goaltracker.Activities.GoalEditorActivity;
import com.cloud7831.goaltracker.Data.GoalsContract;
import com.cloud7831.goaltracker.Objects.Goal;
import com.cloud7831.goaltracker.Objects.MeasurementHandler;
import com.cloud7831.goaltracker.R;

import org.w3c.dom.Text;

import java.util.Set;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class GoalAdapter extends ListAdapter<Goal, GoalAdapter.GoalHolder> {
    private static final String LOGTAG = "GoalAdapter";

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
        public boolean areContentsTheSame(@NonNull Goal oldGoal, @NonNull Goal newGoal) {
            return GoalHasChanged(oldGoal, newGoal);
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

        // Set the Title.
        holder.titleTextView.setText(currentGoal.getTitle());

        // Set the streak if applicable.
        if(currentGoal.getClassification() == GoalsContract.GoalEntry.HABIT){

            holder.streakTextView.setVisibility(View.VISIBLE);
            holder.streakTextView.setText(GetStreakText(currentGoal));
        }
        else{
            // Events and tasks don't need a streak.
            holder.streakTextView.setVisibility(View.GONE);
        }

        //TODO: set the Deadline textbox if there is a deadline.
        holder.deadlineTextView.setVisibility(View.GONE);

        setProgressTextView(holder.progressTextView, currentGoal.getQuota(), currentGoal.getQuotaToday(), currentGoal.getQuotaWeek(), currentGoal.getQuotaMonth(), currentGoal.getUnits(), currentGoal.getClassification(), currentGoal.getFrequency());

        // Set up the measurement bar
        currentGoal.setMeasurementHandler(holder.measureSliderView, holder.quotaTextView);
        if(currentGoal.getIsMeasurable() == 1){
            currentGoal.getMeasurementHandler().setIsHidden(false);
            holder.measurementHolderView.setVisibility(View.VISIBLE);

            final MeasurementHandler MEASURE_FINAL = currentGoal.getMeasurementHandler();
            if(MEASURE_FINAL == null){
                Log.e(LOGTAG, "MEASURE_FINAL is null.");
            }
            // Slider increase and decrease buttons
            holder.increaseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MEASURE_FINAL.increaseScaling();
                    MEASURE_FINAL.todaysQuotaToString();
                }
            });
            holder.decreaseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MEASURE_FINAL.decreaseScaling();
                    MEASURE_FINAL.todaysQuotaToString();
                }
            });

            // Set the seekbar listener so it updates when you slide it.
            holder.measureSliderView.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    // Update the quota for the week here based on the position of the slider

                    // Retrieve the goal so that we can translate slider position into quota.
                    if(MEASURE_FINAL == null){
                        // Handler wasn't set yet, which means that the goal isn't ready to update
                        // the slider yet.
                        Log.i(LOGTAG, "A Seekbar Changelistener was never set.");
                        return;
                    }

                    // Set the quota tally so we know how much quota to record when the goal is swiped.
                    MEASURE_FINAL.updateQuotaTally(progress);
                    // Update the text at the bottom of the goal
                    MEASURE_FINAL.todaysQuotaToString();
                }
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
        }
        else{
            // Not measurable, so no need to have a slider
            holder.measurementHolderView.setVisibility(View.GONE);
            currentGoal.getMeasurementHandler().setIsHidden(true);
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
        private SeekBar measureSliderView;
        private View measurementHolderView;
        private TextView progressTextView;
        private TextView deadlineTextView;
        private TextView increaseButton;
        private TextView decreaseButton;

        private TextView quotaTextView;

        public GoalHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.name_text_view); //TODO: rename to title_text_view
            streakTextView = itemView.findViewById(R.id.streak_text_view);
            measureSliderView = itemView.findViewById(R.id.goal_measure_slider);
            quotaTextView = itemView.findViewById(R.id.measure_completed_today);
            measurementHolderView = itemView.findViewById(R.id.measurement_holder_view);
            progressTextView = itemView.findViewById(R.id.progress_text_view);
            deadlineTextView = itemView.findViewById(R.id.deadline_text_view);
            increaseButton = itemView.findViewById(R.id.increase_slider_button);
            decreaseButton = itemView.findViewById(R.id.decrease_slider_button);

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

        }
    }

    public interface OnItemClickListener{
        void onItemClick(Goal goal);
    }

    private static boolean GoalHasChanged(Goal oldGoal, Goal newGoal){
        return oldGoal.getIsHidden() == newGoal.getIsHidden() &&
                oldGoal.getSessionsTally() == newGoal.getSessionsTally() &&
                oldGoal.getQuotaToday() == newGoal.getQuotaToday() &&
                oldGoal.getQuotaWeek() == newGoal.getQuotaWeek() &&
                oldGoal.getQuotaMonth() == newGoal.getQuotaMonth() &&
                oldGoal.getStreak() == newGoal.getStreak() &&
                oldGoal.getComplexPriority() == newGoal.getComplexPriority() &&

                oldGoal.getQuotaTally() == newGoal.getQuotaTally() &&

                oldGoal.getTitle().equals(newGoal.getTitle()) &&
                oldGoal.getIntention() == newGoal.getIntention() &&
                oldGoal.getUserPriority() == newGoal.getUserPriority() &&
                oldGoal.getClassification() == newGoal.getClassification() &&
                oldGoal.getIsPinned() == newGoal.getIsPinned() &&

                oldGoal.getFrequency() == newGoal.getFrequency() &&
                oldGoal.getDeadline() == newGoal.getDeadline() &&
                oldGoal.getDuration() == newGoal.getDuration() &&
                oldGoal.getSessions() == newGoal.getSessions() &&
                oldGoal.getScheduledTime() == newGoal.getScheduledTime() &&

                oldGoal.getIsMeasurable() == newGoal.getIsMeasurable() &&
                oldGoal.getUnits().equals(newGoal.getUnits()) &&
                oldGoal.getQuota() == newGoal.getQuota();
    }

    private String GetStreakText(Goal currentGoal){
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
        return streakText;
    }

    private void setProgressTextView(TextView textView, int quota, int quotaToday, int quotaWeek, int quotaMonth, String units, int classification, int frequency){
        String text = "";
        int quotaCompletedThisPeriod = 0;
        if(classification == GoalsContract.GoalEntry.HABIT){
            textView.setVisibility(View.VISIBLE);

            quotaCompletedThisPeriod += quotaToday;

            if(frequency == GoalsContract.GoalEntry.WEEKLYGOAL || frequency == GoalsContract.GoalEntry.MONTHLYGOAL){
                quotaCompletedThisPeriod += quotaWeek;
            }

            if(frequency == GoalsContract.GoalEntry.MONTHLYGOAL){
                quotaCompletedThisPeriod += quotaMonth;
            }

            text = StringHelper.getStringQuotaProgressAndUnits(quotaCompletedThisPeriod, quota, units);
            text += " completed ";

            if(frequency == GoalsContract.GoalEntry.DAILYGOAL){
                text += "today";
            }
            if(frequency == GoalsContract.GoalEntry.WEEKLYGOAL){
                text += "this week";
            }
            if(frequency == GoalsContract.GoalEntry.MONTHLYGOAL){
                text += "this month";
            }

            textView.setText(text);
        }
        else{
            textView.setVisibility(View.GONE);
        }
        textView.setVisibility(View.VISIBLE);
    }


    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

}
