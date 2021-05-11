package com.cloud7831.goaltracker.Activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cloud7831.goaltracker.Data.GoalViewModel;
import com.cloud7831.goaltracker.Data.GoalsContract;
import com.cloud7831.goaltracker.HelperClasses.TimeHelper;
import com.cloud7831.goaltracker.Objects.DailyHabit;
import com.cloud7831.goaltracker.Objects.GoalRefactor;
import com.cloud7831.goaltracker.Objects.Habit;
import com.cloud7831.goaltracker.Objects.MonthlyHabit;
import com.cloud7831.goaltracker.Objects.Task;
import com.cloud7831.goaltracker.Objects.WeeklyHabit;
import com.cloud7831.goaltracker.Objects.Workout;
import com.cloud7831.goaltracker.R;
import com.cloud7831.goaltracker.Data.GoalsContract.GoalEntry;

import java.util.Calendar;

public class GoalEditorFragment extends Fragment {
    public static final String LOGTAG = "GoalEditorActivity";
    private static final int MONTHLY_SESSIONS_DEFAULT = 15;
    private static final int WEEKLY_SESSIONS_DEFAULT = 4;
    private static final int DAILY_SESSIONS_DEFAULT = 1;
    private static final int FIXED_SESSIONS_DEFAULT = 1;

    private GoalRefactor goalToSave; // Refers to the goal that is being editted or created.

    //region UI REFERENCES
    // Behind the scenes
    private int goalID = -1; // -1 indicates that a goal ID was not passed and that this is a new goal.
    private int goalType = GoalEntry.UNDEFINED;

    private GoalViewModel viewModel;

    private boolean goalHasChanged = false;

    /** EditText field to enter the goal's name */
    private EditText titleEditText;
    private EditText quotaEditText;
    private TextView quotaUnitsTextView;
    private EditText sessionsEditText;
    private TextView sessionsUnitsTextView;

    /** Spinners for selectable options */
    private Spinner frequencySpinner;
    private int frequencySelected = GoalEntry.FREQ_WEEKLY;

    private Spinner intentionSpinner;
    private int intentionSelected = GoalEntry.INT_BUILDING;

    private Spinner prioritySpinner;
    private int prioritySelected = GoalEntry.PRIORITY_MEDIUM;

    private Spinner classificationSpinner;
    private int classificationSelected = GoalEntry.CLASS_HABIT;

    private Spinner unitsSpinner;
    private String unitsSelected = GoalEntry.HOUR_STRING;

    /** Buttons */
    private Button deadlineButton;

    /** Checkboxes */
    private CheckBox measurableCheckBox;
    private int isMeasurable = 0;

    private CheckBox pinnedCheckBox;
    private int isPinned = 0;

    /** LinearLayout Rows */
    private View unitsRow;
    private View quotaRow;
    private View intenRow;
    private View freqRow;

    private View.OnTouchListener touchListener = new View.OnTouchListener(){
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent){
            goalHasChanged = true;
            return false;
        }
    };
    //endregion UI REFERENCES

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_goal_editor, container, false);
        setHasOptionsMenu(true);

        viewModel = new ViewModelProvider(getActivity(), ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication())).get(GoalViewModel.class);

        //TODO: Create a FAB to use as a save button.

        getActivity().setTitle(getString(R.string.app_name));

        // Retrieve the Goal ID to determine if we're creating a new goal, or editing and existing one.
        goalID = this.getArguments().getInt(GoalEntry.KEY_GOAL_ID);

        // Sets all of the View member variables, sets up the spinners, etc
        initializeViews(view);

        if(goalID < 0){
            // A goalID wasn't passed, which means that this is a new goal.

            // hide the measurementViews until the user checks the isMeasurable box.
            toggleMeasurementViews(false);
        }
        else{
            // Edit the goal based on the goal id passed to the fragment.
            goalType = this.getArguments().getInt(GoalEntry.KEY_GOAL_TYPE);

            //TODO: create a loading screen that's visible until the goal is finished loading.
            goalToSave = viewModel.lookupGoalByID(goalID, goalType);

            // Fills in all the checkboxes, edit texts, and spinners.
            prefillGoalData(goalToSave);
        }
        return view;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    //region SPINNER SETUPS
    /**
     * Setup the dropdown spinner that allows the user to select the classification of the goal.
     */
    private void setupClassificationSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter classificationSpinnerAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.array_classification_options, R.layout.spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        classificationSpinnerAdapter.setDropDownViewResource(R.layout.spinner_item);

        // Apply the adapter to the spinner
        classificationSpinner.setAdapter(classificationSpinnerAdapter);

        // Set the integer frequencySelected to the constant values
        classificationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.goal_classification_task))) {
                        classificationSelected = GoalEntry.CLASS_TASK;
                    } else if (selection.equals(getString(R.string.goal_classification_habit))) {
                        classificationSelected = GoalEntry.CLASS_HABIT;
                    } else if (selection.equals(getString(R.string.goal_classification_event))) {
                        classificationSelected = GoalEntry.CLASS_EVENT;
                    } else if (selection.equals(getString(R.string.goal_classification_workout))){
                        classificationSelected = GoalEntry.CLASS_WORKOUT;
                    } else if (selection.equals(getString(R.string.goal_classification_tracker))){
                        classificationSelected = GoalEntry.CLASS_TRACKER;
                    } else {
                        Log.e(LOGTAG, "Classification was set to undefined.");
                        classificationSelected = GoalsContract.GoalEntry.UNDEFINED;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                classificationSelected = GoalsContract.GoalEntry.UNDEFINED;
                Log.i(LOGTAG, "Classification onNothingSelected was called");
            }
        });

        classificationSpinner.setSelection(0);// Set the default to Habit (the 0th array element)
    }

    /**
     * Setup the dropdown spinner that allows the user to select the frequency of the goal.
     */
    private void setupFrequencySpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter frequencySpinnerAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.array_frequency_options, R.layout.spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        frequencySpinnerAdapter.setDropDownViewResource(R.layout.spinner_item);

        // Apply the adapter to the spinner
        frequencySpinner.setAdapter(frequencySpinnerAdapter);

        // Set the integer frequencySelected to the constant values
        frequencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.goal_frequency_daily))) {
                        frequencySelected = GoalEntry.FREQ_DAILY;
                    } else if (selection.equals(getString(R.string.goal_frequency_weekly))) {
                        frequencySelected = GoalEntry.FREQ_WEEKLY;
                    } else if (selection.equals(getString(R.string.goal_frequency_monthly))) {
                        frequencySelected = GoalEntry.FREQ_MONTHLY;
                    }
//                    else if (selection.equals(getString(R.string.goal_frequency_fixed))) {
//                        frequencySelected = GoalsContract.GoalEntry.FREQ_FIXED;
//                    }
                    else {
                        frequencySelected = GoalsContract.GoalEntry.UNDEFINED;
                    }
                }
                updateUnitsTextViews();
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                frequencySelected = GoalsContract.GoalEntry.UNDEFINED;
            }
        });

        frequencySpinner.setSelection(1);// Set the default to WeeklyGoal
    }

    /**
     * Setup the dropdown spinner that allows the user to select the units of the goal.
     */
    private void setupUnitsSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter unitsSpinnerAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.array_unit_options, R.layout.spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        unitsSpinnerAdapter.setDropDownViewResource(R.layout.spinner_item);

        // Apply the adapter to the spinner
        unitsSpinner.setAdapter(unitsSpinnerAdapter);

        // Set the integer frequencySelected to the constant values
        unitsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.goal_unit_minute_plural))){
                        unitsSelected = GoalEntry.MINUTE_STRING;
                    } else if (selection.equals(getString(R.string.goal_unit_hour_plural))) {
                        unitsSelected = GoalEntry.HOUR_STRING;
                    }
                    else if(selection.equals(getString(R.string.goal_unit_second_plural))){
                        unitsSelected = GoalEntry.SECOND_STRING;
                    }
                    else if(selection.equals(getString(R.string.goal_unit_time_plural))){
                        unitsSelected = GoalEntry.TIMES_STRING;
                    }
                    else if(selection.equals(getString(R.string.goal_unit_rep_plural))){
                        unitsSelected = GoalEntry.REPS_STRING;
                    }
                    else if(selection.equals(getString(R.string.goal_unit_page_plural))){
                        unitsSelected = GoalEntry.PAGES_STRING;
                    }
                    else {
                        unitsSelected = "UNDEFINED";
                    }
                }
                updateUnitsTextViews();
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                unitsSelected = "UNDEFINED";
            }
        });
    }

    /**
     * Setup the dropdown spinner that allows the user to select the priority of the goal.
     */
    private void setupPrioritySpinner(){
        // Create adapter for spinner. The list options are from the String array it will use.
        // the spinner will use the default layout
        ArrayAdapter prioritySpinnerAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.array_priority_options, R.layout.spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        prioritySpinnerAdapter.setDropDownViewResource(R.layout.spinner_item);

        // Apply the adapter to the spinner
        prioritySpinner.setAdapter(prioritySpinnerAdapter);

        // Set the integer prioritySelected to the constant values
        prioritySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.goal_priority_very_low))) {
                        prioritySelected = GoalEntry.PRIORITY_VERY_LOW;
                    } else if (selection.equals(getString(R.string.goal_priority_low))) {
                        prioritySelected = GoalEntry.PRIORITY_LOW;
                    } else if (selection.equals(getString(R.string.goal_priority_medium))) {
                        prioritySelected = GoalEntry.PRIORITY_MEDIUM;
                    } else if (selection.equals(getString(R.string.goal_priority_high))) {
                        prioritySelected = GoalEntry.PRIORITY_HIGH;
                    } else if (selection.equals(getString(R.string.goal_priority_very_high))) {
                        prioritySelected = GoalEntry.PRIORITY_VERY_HIGH;
                    } else {
                        prioritySelected = GoalEntry.PRIORITY_MEDIUM;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                prioritySelected = GoalsContract.GoalEntry.UNDEFINED;
            }
        });

        prioritySpinner.setSelection(2);// Set the default to medium
    }

    /**
     * Setup the dropdown spinner that allows the user to select the intention of the goal.
     */
    private void setupIntentionSpinner(){
        // Create adapter for spinner. The list options are from the String array it will use.
        // the spinner will use the default layout
        ArrayAdapter intentionSpinnerAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.array_intention_options, R.layout.spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        intentionSpinnerAdapter.setDropDownViewResource(R.layout.spinner_item);

        // Apply the adapter to the spinner
        intentionSpinner.setAdapter(intentionSpinnerAdapter);

        // Set the integer intentionSelected to the constant values
        intentionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.goal_intention_avoidance))) {
                        intentionSelected = GoalEntry.INT_BREAKING;
                    } else if (selection.equals(getString(R.string.goal_intention_building))) {
                        intentionSelected = GoalEntry.INT_BUILDING;
                    } else if (selection.equals(getString(R.string.goal_intention_gain))) {
                        intentionSelected = GoalEntry.INT_GAIN;
                    } else if (selection.equals(getString(R.string.goal_intention_lose))) {
                        intentionSelected = GoalEntry.INT_LOSE;
                    } else {
                        intentionSelected = GoalsContract.GoalEntry.UNDEFINED;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                intentionSelected = GoalsContract.GoalEntry.UNDEFINED;
            }
        });
    }
    //endregion SPINNER SETUPS

    private void showUnsavedChangesDialog(DialogInterface .OnClickListener discardButtonClickListener){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Discard your changes and quit editing?");
        builder.setPositiveButton("Discard", discardButtonClickListener);
        builder.setNegativeButton("Keep editing", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id){
                // User clicked the "Keep editing" button, so dismiss the dialog and continue editing
                if(dialog != null){
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //TODO: onBackPressed() doesn't exist for fragments.
//    @Override
//    public void onBackPressed(){
//        // If the goal hasn't changed, continue with handling back button press
//        FragmentManager fm = getActivity().getSupportFragmentManager();
//        if(!goalHasChanged){
//            fm.popBackStack();
//            return;
//        }
//
//        DialogInterface.OnClickListener discardButtonClickListener =
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        getActivity().getSupportFragmentManager().popBackStack();
//                    }
//                };
//
//        // Show dialog that there are unsaved changes
//        showUnsavedChangesDialog(discardButtonClickListener);
//    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        inflater.inflate(R.menu.menu_editor, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO: fix the menu in the editor fragment.

        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save the goal data to the database.
                saveGoal();
                return true;
            case R.id.action_delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Are you sure you want to delete this goal?");
                builder.setNegativeButton("No, take me back",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                // User clicked the "Keep editing" button, so dismiss the dialog and continue editing
                                if(dialog != null){
                                    dialog.dismiss();
                                }
                            }
                        });
                builder.setPositiveButton("Delete",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteGoal();
                            }
                        });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                return true;
            case android.R.id.home:
//                // If the goal hasn't changed, continue with navigating up to parent activity
//                if(!goalHasChanged){
//                    // Navigate back to parent activity (The Goal List Fragment/Main Activity)
//                    NavUtils.navigateUpFromSameTask(this);
//                    return true;
//                }
//
//                // There are unsaved changes potentially so warn the user.
//                DialogInterface.OnClickListener discardButtonClickListener =
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                NavUtils.navigateUpFromSameTask(GoalEditorActivity.this);
//                            }
//                        };
//
//                showUnsavedChangesDialog(discardButtonClickListener);
//                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveGoal() {
        // Get all the data the user entered and save it to the database
        int deadline = 0; //TODO: complete this
        int duration = 0; //TODO: complete this
        int scheduledTime = 0; //TODO: complete this

        // --------------------------------- TITLE -------------------------------------
        String titleString = titleEditText.getText().toString().trim();
        if(TextUtils.isEmpty(titleString)){
            Toast.makeText(getContext(), "You cannot save a goal without a name.", Toast.LENGTH_SHORT).show();
            return;
        }

        // --------------------------------- Sessions -------------------------------------
        int sessions = getSessionsFromEditText();

        // --------------------------------- QUOTA -------------------------------------
        String quotaString = quotaEditText.getText().toString().trim();
        double dQuota;
        int quota = -1;
        if(!TextUtils.isEmpty(quotaString)){
            dQuota = Double.parseDouble(quotaString);

            if(GoalEntry.isValidTime(unitsSelected)){
                // The quota must be stored in seconds
                quota = GoalEntry.convertToSeconds(dQuota, unitsSelected);
            }
            else{
                quota = (int)dQuota;
            }
        }

        // ---------------------------------- isPinned ----------------------------------------
        if(pinnedCheckBox.isChecked()){
            isPinned = 1;
        } else{
            isPinned = 0;
        }

        // -------------------------------- isMeasurable ---------------------------------------
        if(measurableCheckBox.isChecked()){
            isMeasurable = 1;
        } else{
            isMeasurable = 0;

            // The goal wasn't measurable which means that the quota wasn't set.
            // therefore, make it the same as the number of sessions
            quota = sessions;
            unitsSelected = GoalEntry.TIMES_STRING;
        }

        // -------------------------------- Make the Goal -------------------------------
        if(goalID <0){
            // The goal needs to be built from scratch.
            // TODO: fix this

            // First convert to the correct type of goal.
            if(classificationSelected == GoalEntry.CLASS_HABIT){
                if(frequencySelected == GoalEntry.FREQ_DAILY){
                    goalToSave = DailyHabit.buildNewDailyHabit(titleString, prioritySelected, isPinned, intentionSelected, classificationSelected,
                            isMeasurable, unitsSelected, quota,
                            duration, scheduledTime, deadline, sessions);
                }
                else if(frequencySelected == GoalEntry.FREQ_WEEKLY){
                    goalToSave = WeeklyHabit.buildNewWeeklyHabit(titleString, prioritySelected, isPinned, intentionSelected, classificationSelected,
                            isMeasurable, unitsSelected, quota,
                            duration, scheduledTime, deadline, sessions);
                }
                else if(frequencySelected == GoalEntry.FREQ_MONTHLY){
                    goalToSave = MonthlyHabit.buildNewMonthlyHabit(titleString, prioritySelected, isPinned, intentionSelected, classificationSelected,
                            isMeasurable, unitsSelected, quota,
                            duration, scheduledTime, deadline, sessions);
                }
            }
            else if(classificationSelected == GoalEntry.CLASS_TASK){
                goalToSave = Task.buildNewTask(titleString, prioritySelected, isPinned, intentionSelected, classificationSelected,
                        isMeasurable, unitsSelected, quota,
                        duration, scheduledTime, deadline, sessions);
            }
//            else if(frequencySelected == GoalEntry.WORKOUTGOAL){
//            goalToSave = Workout.buildNewWorkout(titleString, prioritySelected, isPinned, intentionSelected, classificationSelected,
//                    isMeasurable, unitsSelected, quota,
//                    duration, scheduledTime, deadline, sessions);
//            }
            else{
                Log.e(LOGTAG, "tried to build a new user goal with an unknown type of goal");
            }
        }
        else{
            if(goalToSave == null){
                Log.e(LOGTAG, "There was supposed to be a goal, yet goalToSave was null");
            }
            // The goal only needs to update the settings defined by the user. The hidden internal
            // variables must remain the same so that the progress isn't wiped.
            // TODO: fix this

            // First convert to the correct type of goal.
            if(classificationSelected == GoalEntry.CLASS_HABIT){
                if(frequencySelected == GoalEntry.FREQ_DAILY){
                    goalToSave.convertToDailyHabit();
                }
                else if(frequencySelected == GoalEntry.FREQ_WEEKLY){
                    goalToSave.convertToWeeklyHabit();
                }
                else if(frequencySelected == GoalEntry.FREQ_MONTHLY){
                    goalToSave.convertToMonthlyHabit();
                }
            }
            else if(classificationSelected == GoalEntry.CLASS_TASK){
                goalToSave.convertToTask();
            }
//            else if(frequencySelected == GoalEntry.WORKOUTGOAL){
//                goalToSave.convertToWorkout();
//            }
            else{
                Log.e(LOGTAG, "tried to convert to an unknown type of goal");
            }

            goalToSave.editUserSettings(titleString, prioritySelected, isPinned, intentionSelected, classificationSelected,
                    isMeasurable, unitsSelected, quota,
                    duration, scheduledTime, deadline, sessions);
        }

        commitToDatabase(); // takes the goalToSave and updates or inserts it in the database

        getActivity().getSupportFragmentManager().popBackStack();
    }

    private void commitToDatabase(){
        if(goalID < 0){
            // This is a new goal, so it needs to be inserted into the database.

            if(viewModel == null){
                Log.e(LOGTAG, "ViewModel was null when attempting to save the goal");
            }
            Log.i(LOGTAG, "goal being saved with: " + goalToSave);

            viewModel.insert(goalToSave);
        }
        else{
            // This is a goal that needs to be updated with new information.
            if(goalToSave == null){
                Log.e(LOGTAG, "goalToSave was null when trying to update the existing goal.");
            }
            viewModel.update(goalToSave);
        }
    }

    private void prefillCheckBoxes(int pinned, int measurable){
        // Fill in the checkboxes with the data from the intent
        isPinned = pinned;
        pinnedCheckBox.setChecked(isPinned == 1);

        isMeasurable = measurable;
        measurableCheckBox.setChecked(isMeasurable == 1);
        toggleMeasurementViews(isMeasurable == 1);
    }

    private void deleteGoal(){
        //TODO: pop up a warning to make sure the user really wants to delete.
        Log.i(LOGTAG, "attempting to delete.");
//        viewModel.deleteByID(goalID);
        getActivity().getSupportFragmentManager().popBackStack();
    }

    private void initializeViews(View view){
        // Find all relevant views that we will need to read user input from:
        // Overview
        titleEditText = view.findViewById(R.id.edit_goal_name);
        prioritySpinner = view.findViewById(R.id.spinner_goal_priority);
        classificationSpinner = view.findViewById(R.id.spinner_goal_classification);
        intentionSpinner = view.findViewById(R.id.spinner_goal_intention);
        pinnedCheckBox = view.findViewById(R.id.pinCheckBox);
        quotaRow = view.findViewById(R.id.quota_layout_view);
        unitsRow = view.findViewById(R.id.units_layout_view);
        intenRow = view.findViewById(R.id.intention_layout_view);
        freqRow = view.findViewById(R.id.freq_layout_view);
        deadlineButton = view.findViewById(R.id.deadline_button);
        deadlineButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

//                new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
//                    @Override
//                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//
//                    }
//                }, 2015, 02, 26).show();


                dialogDeadlinePicker();

//                View deadlineDatePickerView = getLayoutInflater().inflate(R.layout.deadline_date_picker, null);
//
//                final Calendar calendar = Calendar.getInstance();
//                int day = calendar.get(Calendar.DAY_OF_MONTH);
//                int month = calendar.get(Calendar.MONTH);
//                int year = calendar.get(Calendar.YEAR);
//
//                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//                builder.setView(deadlineDatePickerView);
//                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                });
//                builder.setTitle("Set a Deadline");
//
//                builder.create().show();
            }
        });

        // Schedule
        frequencySpinner = view.findViewById(R.id.spinner_goal_frequency);

        // Measurement
        measurableCheckBox = view.findViewById(R.id.measurableCheckBox);
        measurableCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                toggleMeasurementViews(isChecked);
            }
        });

        unitsSpinner = view.findViewById(R.id.spinner_goal_units);
        quotaEditText = view.findViewById(R.id.edit_goal_quota);
        quotaUnitsTextView = view.findViewById(R.id.label_quota_units);
        sessionsEditText = view.findViewById(R.id.edit_goal_sessions);
        sessionsUnitsTextView = view.findViewById(R.id.label_sessions_units);

        // Set the OnTouchListener so we know if they've modified data.
        //TODO: do this for all the options.
//        titleEditText.setOnTouchListener(touchListener);
//        frequencySpinner.setOnTouchListener(touchListener);

        setupFrequencySpinner();
        setupIntentionSpinner();
        setupPrioritySpinner();
//        setupClassificationSpinner();
        setupUnitsSpinner();
    }

    private void prefillGoalData(GoalRefactor goal){
        // Fill in all the edit texts, spinner values, and checkboxes
        // Title
        titleEditText.setText(goal.getTitle());

        prefillCheckBoxes(goal.getIsPinned(), goal.getIsMeasurable());

        //Set the frequency spinner
        if(goal instanceof Habit){
            freqRow.setVisibility(View.VISIBLE);
            int freq = 0;
            if(goal instanceof DailyHabit){
                freq = GoalEntry.FREQ_DAILY;
            }
            else if(goal instanceof WeeklyHabit){
                freq = GoalEntry.FREQ_WEEKLY;
            }
            else if(goal instanceof MonthlyHabit){
                freq = GoalEntry.FREQ_MONTHLY;
            }
            else{
                Log.e(LOGTAG, "unknown frequency for the habit.");
            }
            // subtract 1, because undefined (0) is not an option shown to the user.
            frequencySpinner.setSelection(freq - 1);
        } else{
            freqRow.setVisibility(View.GONE);
        }

        // Set the intention spinner
        if(goal instanceof Task){
            intenRow.setVisibility(View.VISIBLE);
            int intention = ((Task) goal).getIntention();
            // -1 because undefined isn't shown to the user, which is the 0th element.
            intentionSpinner.setSelection(intention - 1);
        }
        else{
            intenRow.setVisibility(View.GONE);
        }

        // Set the priority spinner
        int priority = goal.getUserPriority();
        prioritySpinner.setSelection(priority - 1);
        if(goal.getUserPriority() != prioritySelected){
            Log.i(LOGTAG, "The prioritySelected wasn't set properly!");
        }

        // Set the classification spinner
        int classification;
        if(goal instanceof Habit){
            classification = GoalEntry.CLASS_HABIT;
        }
        else if(goal instanceof Task){
            classification = GoalEntry.CLASS_TASK;
        }
        else{
            classification = GoalEntry.CLASS_HABIT;
        }
        classificationSpinner.setSelection(classification - 1);

        // Set the units spinner
        if(goal instanceof Task){
            String units = null;
            units = ((Task) goal).getUnits();
            Log.i(LOGTAG, "units - " + units);
            if(units == null){
                unitsSelected = GoalEntry.TIMES_STRING;
            } else if(units.equals(GoalEntry.MINUTE_STRING)){
                unitsSpinner.setSelection(GoalEntry.MINUTES - 1);
                unitsSelected = GoalEntry.MINUTE_STRING;
            }
            else if(units.equals(GoalEntry.HOUR_STRING)){
                unitsSpinner.setSelection(GoalEntry.HOURS - 1);
                unitsSelected = GoalEntry.HOUR_STRING;
            }
            else if(units.equals(GoalEntry.SECOND_STRING)){
                unitsSpinner.setSelection(GoalEntry.SECONDS - 1);
                unitsSelected = GoalEntry.SECOND_STRING;
            }
            else if(units.equals(GoalEntry.TIMES_STRING)){
                unitsSpinner.setSelection(GoalEntry.TIMES - 1);
                unitsSelected = GoalEntry.TIMES_STRING;
            }
            else if(units.equals(GoalEntry.REPS_STRING)){
                unitsSpinner.setSelection(GoalEntry.REPS - 1);
                unitsSelected = GoalEntry.REPS_STRING;
            }
            else if(units.equals(GoalEntry.PAGES_STRING)){
                unitsSpinner.setSelection(GoalEntry.PAGES - 1);
                unitsSelected = GoalEntry.PAGES_STRING;
            }
            else{
                Log.e(LOGTAG, "The units were not recognized when preloading data.");
            }

            if(GoalEntry.isValidTime(unitsSelected)){

                quotaEditText.setText(Double.toString(TimeHelper.roundAndConvertTime(((Task)goal).getQuota())));
            }
            else {
                quotaEditText.setText(Integer.toString(((Task)goal).getQuota()));
            }

            int sessions = ((Task)goal).getSessions();

            // Set the sessions for the user.
            sessionsEditText.setText(Integer.toString(sessions));
        }
        else{
            unitsSelected = GoalEntry.TIMES_STRING;
        }

        updateUnitsTextViews();
    }

    private void updateUnitsTextViews(){
        // Set the units for the quota line and sessions line
        // TODO: should only update the views if the views are visible in the first place.
        String freqUnits = null;
        if(frequencySelected == GoalEntry.FREQ_MONTHLY){
            freqUnits = "month";
        }
        else if(frequencySelected == GoalEntry.FREQ_WEEKLY){
            freqUnits = "week";
        }
        else if(frequencySelected == GoalEntry.FREQ_DAILY){
            freqUnits = "day";
        }
        else{
            Log.e(LOGTAG, "Units were not an expected frequency when setting the units text");
            freqUnits = "ERROR";
        }

        int sessions = getSessionsFromEditText();
        String quotaUnits = unitsSelected;
        String sessionsUnits;
        if(sessions == 1){
            sessionsUnits = "session";
        } else{
            sessionsUnits = "sessions";
        }

        quotaUnits += "/" + freqUnits;
        sessionsUnits += "/" + freqUnits;

        quotaUnitsTextView.setText(quotaUnits);
        sessionsUnitsTextView.setText(sessionsUnits);
    }


    private void dialogDeadlinePicker(){
        DialogFragment dialogFragment = new GoalDialogFragment();
        dialogFragment.show(getActivity().getSupportFragmentManager(), "GoalDialogFragment");
    }

    private void toggleMeasurementViews(boolean isChecked){
        if(isChecked){
            // Make the views visible, so the user can alter them.
            unitsRow.setVisibility(View.VISIBLE);
            quotaRow.setVisibility(View.VISIBLE);
        }
        else{
            // If the goal is not measurable
            unitsRow.setVisibility(View.GONE);
            quotaRow.setVisibility(View.GONE);
        }
    }

    private int getSessionsFromEditText(){
        // --------------------------------- Sessions -------------------------------------
        String sessionsString = sessionsEditText.getText().toString();
        int sessions;
        if(sessionsString.equals("")){
            // Give the default number of sessions
            if(frequencySelected == GoalEntry.FREQ_DAILY){
                sessions = DAILY_SESSIONS_DEFAULT;
            }
//            else if(frequencySelected == GoalEntry.FREQ_){
//                sessions = FIXED_SESSIONS_DEFAULT;
//            }
            else if(frequencySelected == GoalEntry.FREQ_WEEKLY){
                sessions = WEEKLY_SESSIONS_DEFAULT;
            }
            else if(frequencySelected == GoalEntry.FREQ_MONTHLY){
                sessions = MONTHLY_SESSIONS_DEFAULT;
            }
            else{
                Log.e(LOGTAG, "Sessions default value was set to 1 because the frequency was not an expected value.");
                sessions = 1;
            }
        }
        else {
            // Was not blank, so use the value the user provided.
            sessions = Integer.parseInt(sessionsString);
        }
        return sessions;
    }

}