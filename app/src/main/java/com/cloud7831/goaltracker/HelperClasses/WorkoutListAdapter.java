package com.cloud7831.goaltracker.HelperClasses;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.cloud7831.goaltracker.Objects.Goals.Goal;
import com.cloud7831.goaltracker.Objects.Goals.WorkoutRelated.ExerciseEntry;
import com.cloud7831.goaltracker.Objects.Goals.WorkoutRelated.Workout;
import com.cloud7831.goaltracker.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class WorkoutListAdapter extends ListAdapter<Workout, WorkoutListAdapter.WorkoutHolder> {
    private static final String LOGTAG = "WorkoutListAdapter";

    private WorkoutListAdapter.OnItemClickListener listener;

    public WorkoutListAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<Workout> DIFF_CALLBACK = new DiffUtil.ItemCallback<Workout>() {
        @Override
        public boolean areItemsTheSame(@NonNull Workout oldItem, @NonNull Workout newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Workout oldItem, @NonNull Workout newItem) {
            return  oldItem.equals(newItem);
        }
    };

    @NonNull
    @Override
    public WorkoutListAdapter.WorkoutHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.workout_list_item, parent, false);

        // TODO: implement a custom layout for each of the card types by implementing getItemViewType
        // TODO: https://stackoverflow.com/questions/26245139/how-to-create-recyclerview-with-multiple-view-type
        return new WorkoutListAdapter.WorkoutHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutListAdapter.WorkoutHolder holder, int position) {
        Workout workout = getItem(position);

        // ------------------- Title ------------------------
        holder.titleTextView.setText(workout.getTitle());

        // ------------------- Streak -----------------------
        int lastCompleted = workout.getDateLastCompleted();
        if(lastCompleted == 0){
            holder.lastCompletedTextView.setText("");
        }
        else{
            holder.lastCompletedTextView.setText(workout.getDateLastCompleted() + " days ago");
        }

        // ------------------- ExerciseEntry ListAdapter -----------------------

        Context context = holder.tableLayout.getContext();
        // Clear out any existing views from other workouts.
        holder.tableLayout.removeAllViews();

        List<ExerciseEntry> list = workout.getEntryList();
        for(int i = 0; i < workout.getEntryList().size(); i++){
            // Create a table row for out exercise entry to live in.
            TableRow tr = new TableRow(context);
            tr.setBackgroundColor(context.getResources().getColor(R.color.darkmodePrimary, null));        // part1
            tr.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));

            // Create a TextView
            TextView title = new TextView(context);
            title.setText(list.get(i).getExerciseName());
            title.setTextColor(context.getColor(R.color.darkmodeTextDark));

            float scale = context.getResources().getDisplayMetrics().density;
            int dpAsPixels = (int) (16*scale + 0.5f);
            int dpAsPixelsTopAndBot = (int) (3*scale + 0.5f);
            title.setPadding(dpAsPixels, dpAsPixelsTopAndBot, dpAsPixels, dpAsPixelsTopAndBot);
            tr.addView(title);// add the column to the table row here

            holder.tableLayout.addView(tr);
        }


    }



    public Workout getWorkoutAt(int position){
        return getItem(position);
    }

    public interface OnItemClickListener{
        void onItemClick(Workout workout);
    }

    public void setOnItemClickListener(WorkoutListAdapter.OnItemClickListener listener){
        this.listener = listener;
    }

    class WorkoutHolder extends RecyclerView.ViewHolder{
        private final TextView titleTextView;
        private final TextView lastCompletedTextView;
        private final TableLayout tableLayout;


        public WorkoutHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title_text_view); //TODO: rename to title_text_view
            lastCompletedTextView = itemView.findViewById(R.id.last_completed_text_view);
            tableLayout = itemView.findViewById(R.id.exercise_entry_table);

        }
    }

}
