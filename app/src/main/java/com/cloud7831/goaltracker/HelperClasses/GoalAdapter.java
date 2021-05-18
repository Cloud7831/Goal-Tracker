package com.cloud7831.goaltracker.HelperClasses;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.cloud7831.goaltracker.Objects.Goals.GoalRefactor;
import com.cloud7831.goaltracker.Objects.MeasurementHandler;
import com.cloud7831.goaltracker.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class GoalAdapter extends ListAdapter<GoalRefactor, GoalAdapter.GoalHolder> {
    private static final String LOGTAG = "GoalAdapter";

    private OnItemClickListener listener;
    private int selectedPosition = RecyclerView.NO_POSITION;

    public GoalAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<GoalRefactor> DIFF_CALLBACK = new DiffUtil.ItemCallback<GoalRefactor>() {
        @Override
        public boolean areItemsTheSame(@NonNull GoalRefactor oldItem, @NonNull GoalRefactor newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull GoalRefactor oldGoal, @NonNull GoalRefactor newGoal) {
            boolean wasSame =  oldGoal.equals(newGoal);
            if(!wasSame){
                Log.i(LOGTAG, "goal: " + oldGoal.getTitle() + " wasSame  = " + wasSame);
            }
            return wasSame;
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
        GoalRefactor goal = getItem(position);

        // ------------------- Title ------------------------
        goal.setTitleTextView(holder.titleTextView);

        // ------------------- Streak -----------------------
        goal.setStreakTextView(holder.streakTextView);

        // ------------------- Deadline ---------------------
        // TODO: set the Deadline textbox if there is a deadline.
        // TODO: if a task has passed the deadline, change the background to a red to indicate failure.
        // TODO: name that shade of red failure_background.
        holder.deadlineTextView.setVisibility(View.GONE);

        // ------------------- Progress TextView ------------
        goal.setProgressTextView(holder.progressTextView);

        // ------------------- Measurement Bar --------------
        goal.setMeasurementView(holder.measurementHolderView, holder.handler);

    }

    public GoalRefactor getGoalAt(int position){
        return getItem(position);
    }

    public interface OnItemClickListener{
        void onItemClick(GoalRefactor goal);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    class GoalHolder extends RecyclerView.ViewHolder{
        private final TextView titleTextView;
        private final TextView streakTextView;
        private final SeekBar measureSliderView;
        private final View measurementHolderView;
        private final TextView progressTextView;
        private final TextView deadlineTextView;
        private final MeasurementHandler handler;

        public GoalHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.name_text_view); //TODO: rename to title_text_view
            streakTextView = itemView.findViewById(R.id.streak_text_view);
            measureSliderView = itemView.findViewById(R.id.goal_measure_slider);
            measurementHolderView = itemView.findViewById(R.id.measurement_holder_view);
            progressTextView = itemView.findViewById(R.id.progress_text_view);
            deadlineTextView = itemView.findViewById(R.id.deadline_text_view);

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

            handler = new MeasurementHandler(measureSliderView, (TextView)itemView.findViewById(R.id.measure_completed_today));

            // Slider increase and decrease buttons
            itemView.findViewById(R.id.increase_slider_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handler.increaseScaling();
                    handler.todaysQuotaToString();
                }
            });
            itemView.findViewById(R.id.decrease_slider_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(LOGTAG, "decrease button clicked");
                    handler.decreaseScaling();
                    handler.todaysQuotaToString();
                }
            });

            // Set the seekbar listener so it updates when you slide it.
            measureSliderView.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    // Update the quota based on the position of the slider

                    // Set the quota tally so we know how much quota to record when the goal is swiped.
                    handler.setQuotaInSlider(progress);
                    // Update the text at the bottom of the goal
                    handler.todaysQuotaToString();
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

}
