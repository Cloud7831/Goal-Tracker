package com.cloud7831.goaltracker.Activities;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cloud7831.goaltracker.Data.GoalViewModel;
import com.cloud7831.goaltracker.Data.GoalsContract;
import com.cloud7831.goaltracker.Objects.Goal;
import com.cloud7831.goaltracker.R;
import com.cloud7831.goaltracker.Data.GoalsContract.GoalEntry;

public class GoalEditorActivity extends Fragment {
    public static final String LOGTAG = "GoalEditorActivity";

    // Behind the scenes
    public static final String KEY_GOAL_ID = "Goal ID";
    private int goalID = -1;

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
    private int frequencySelected = GoalsContract.GoalEntry.WEEKLYGOAL;

    private Spinner intentionSpinner;
    private int intentionSelected = GoalsContract.GoalEntry.BUILDING;

    private Spinner prioritySpinner;
    private int prioritySelected = GoalEntry.PRIORITY_MEDIUM;

    private Spinner classificationSpinner;
    private int classificationSelected = GoalEntry.HABIT;

    private Spinner unitsSpinner;
    private String unitsSelected = GoalEntry.HOUR_STRING;

    /** Checkboxes */
    private CheckBox measurableCheckBox;
    private int isMeasurable = 0;

    private CheckBox pinnedCheckBox;
    private int isPinned = 0;

    private View.OnTouchListener touchListener = new View.OnTouchListener(){
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent){
            goalHasChanged = true;
            return false;
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_goal_editor, container, false);
        setHasOptionsMenu(true);

        viewModel = new ViewModelProvider(getActivity(), ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication())).get(GoalViewModel.class);

        //TODO: Create a FAB to use as a save button.

        // Sets all of the View member variables, sets up the spinners, etc
        initializeViews(view);

        // Retrieve the Goal ID to determine if we're creating a new goal, or editing and existing one.
        goalID = this.getArguments().getInt(KEY_GOAL_ID);

        if(goalID < 1){
            // This is a new goal, so change the app bar to say "Add a New Goal".
            getActivity().setTitle(getString(R.string.editor_activity_title_new_goal));
            // It doesn't make sense to delete a goal so we can hide that option.
//            invalidateOptionsMenu();
        }
        else{
            // Edit the goal based on the goal id passed to the fragment.

            //TODO: create a loading screen that's visible until the goal is finished loading.
            Goal goal = viewModel.lookupGoalByID(goalID);

            getActivity().setTitle(goal.getTitle());

            prefillGoalData(goal);
        }
        return view;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }


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
                        classificationSelected = GoalEntry.TASK;
                    } else if (selection.equals(getString(R.string.goal_classification_habit))) {
                        classificationSelected = GoalEntry.HABIT;
                    } else if (selection.equals(getString(R.string.goal_classification_event))) {
                        classificationSelected = GoalEntry.EVENT;
                    } else {
                        classificationSelected = GoalsContract.GoalEntry.UNDEFINED;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                frequencySelected = GoalsContract.GoalEntry.UNDEFINED;
            }
        });
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
                        frequencySelected = GoalsContract.GoalEntry.DAILYGOAL;
                        //TODO: set callbacks that update the labels in the other parts of the editor.
                    } else if (selection.equals(getString(R.string.goal_frequency_weekly))) {
                        frequencySelected = GoalsContract.GoalEntry.WEEKLYGOAL;
                    } else if (selection.equals(getString(R.string.goal_frequency_monthly))) {
                        frequencySelected = GoalsContract.GoalEntry.MONTHLYGOAL;
                    } else if (selection.equals(getString(R.string.goal_frequency_fixed))) {
                        frequencySelected = GoalsContract.GoalEntry.FIXEDGOAL;
                    } else {
                        frequencySelected = GoalsContract.GoalEntry.UNDEFINED;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                frequencySelected = GoalsContract.GoalEntry.UNDEFINED;
            }
        });
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
                Log.i(LOGTAG, "Units set to " + unitsSelected);
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
                intentionSelected = GoalsContract.GoalEntry.UNDEFINED;
            }
        });
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
                        intentionSelected = GoalsContract.GoalEntry.BREAKING;
                    } else if (selection.equals(getString(R.string.goal_intention_building))) {
                        intentionSelected = GoalsContract.GoalEntry.BUILDING;
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
                //TODO: after saving the goal, I need to make sure the goal in the goal list updates before the back button is pressed. Otherwise, the old data might persist.
            case R.id.action_delete:
//                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                builder.setMessage("Are you sure you want to delete this goal?");
//                builder.setNegativeButton("No take me back",
//                        new DialogInterface.OnClickListener(){
//                            public void onClick(DialogInterface dialog, int id){
//                                // User clicked the "Keep editing" button, so dismiss the dialog and continue editing
//                                if(dialog != null){
//                                    dialog.dismiss();
//                                }
//                            }
//                        });
//                builder.setPositiveButton("Delete",
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                deleteGoal();
//                            }
//                        });
//
//                AlertDialog alertDialog = builder.create();
//                alertDialog.show();
//
//                return true;
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
        // Get all the data the user entered and send it back as an intent.
        int deadline = 0; //TODO: complete this
        int duration = 0; //TODO: complete this
        int scheduledTime = 0; //TODO: complete this

        // --------------------------------- TITLE -------------------------------------
        String titleString = titleEditText.getText().toString().trim();
        if(TextUtils.isEmpty(titleString)){
            Toast.makeText(getContext(), "You cannot save a goal without a name.", Toast.LENGTH_SHORT).show();
            return;
        }

        // --------------------------------- QUOTA -------------------------------------
        String quotaString = quotaEditText.getText().toString().trim();
        double dQuota;
        int quota = 0;
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

        // --------------------------------- Sessions -------------------------------------
        String sessionsString = sessionsEditText.getText().toString();
        int sessions;
        if(sessionsString.equals(null) || sessionsString.equals("")){
            // Give the default number of sessions
            if(frequencySelected == GoalEntry.DAILYGOAL || frequencySelected == GoalEntry.FIXEDGOAL){
                sessions = 1;
            }
            else if(frequencySelected == GoalEntry.WEEKLYGOAL){
                sessions = 4;
            }
            else if(frequencySelected == GoalEntry.MONTHLYGOAL){
                sessions = 15;
            }
            else{
                sessions = 0;
            }
        }
        else {
            // Was not blank, so use the value the user provided.
            sessions = Integer.parseInt(sessionsString);
        }

        // isPinned
        if(pinnedCheckBox.isChecked()){
            isPinned = 1;
        } else{
            isPinned = 0;
        }

        // isMeasurable
        if(measurableCheckBox.isChecked()){
            isMeasurable = 1;
        } else{
            isMeasurable = 0;

            // quota and sessions need to be 1 so that it essentially becomes a task that needs to
            // be done once.
            quota = 1;
            sessions = 1;
        }

        // -------------------------------- Make the Goal -------------------------------
        Goal goal = Goal.buildUserGoal(titleString, classificationSelected, intentionSelected, prioritySelected, isPinned,
                                        isMeasurable, unitsSelected, quota,
                                        frequencySelected, deadline, duration, scheduledTime, sessions);

        if(goalID < 0){
            // This is a new goal, so it needs to be inserted into the database.
            Log.i(LOGTAG, "goal being saved with: " + goal);
            Log.i(LOGTAG, "viewModel null?: " + (viewModel == null));
            viewModel.insert(goal);
        }
        else{
            // This is a goal that needs to be updated with new information.
            goal.setId(goalID);
            viewModel.update(goal);
        }

        getActivity().getSupportFragmentManager().popBackStack();
    }

    private void prefillCheckBoxes(int pinned, int measurable){
        // Fill in the checkboxes with the data from the intent
        isPinned = pinned;
        pinnedCheckBox.setChecked(isPinned == 1);

        isMeasurable = measurable;
        measurableCheckBox.setChecked(isMeasurable == 1);
    }

    private void deleteGoal(){
        //TODO: delete the goal
        //TODO: pop up a warning to make sure the user really wants to delete.
//        Toast.makeText(this, "The goal has been deleted.", Toast.LENGTH_SHORT).show();
    }

    private void initializeViews(View view){
        // Find all relevant views that we will need to read user input from:
        // Overview
        titleEditText = view.findViewById(R.id.edit_goal_name);
        prioritySpinner = view.findViewById(R.id.spinner_goal_priority);
        classificationSpinner = view.findViewById(R.id.spinner_goal_classification);
        intentionSpinner = view.findViewById(R.id.spinner_goal_intention);
        pinnedCheckBox = view.findViewById(R.id.pinCheckBox);

        // Schedule
        frequencySpinner = view.findViewById(R.id.spinner_goal_frequency);

        // Measurement
        measurableCheckBox = view.findViewById(R.id.measurableCheckBox);
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
        setupClassificationSpinner();
        setupUnitsSpinner();
    }

    private void prefillGoalData(Goal goal){
        // Fill in all the edit texts
        titleEditText.setText(goal.getTitle());

        prefillCheckBoxes(goal.getIsPinned(), goal.getIsMeasurable());

        //Set the frequency spinner
        int freq = goal.getFrequency();
        // subtract 1, because undefined (0) is not an option shown to the user.
        frequencySpinner.setSelection(freq - 1);

        // Set the intention spinner
        int intention = goal.getIntention();
        intentionSpinner.setSelection(intention - 1);

        // Set the priority spinner
        int priority = goal.getUserPriority();
        prioritySpinner.setSelection(priority - 1);

        // Set the classification spinner
        int classification = goal.getClassification();
        classificationSpinner.setSelection(classification - 1);

        // Set the units spinner
        String units = goal.getUnits();
        // Instead of doing the -1 I'm just going to ignore Undefined for units.
        Log.i(LOGTAG, "units - " + units);
        if(units == null){
            unitsSelected = GoalEntry.HOUR_STRING;
        } else if(units.equals(GoalEntry.MINUTE_STRING)){
            unitsSpinner.setSelection(GoalEntry.MINUTES);
        }
        else if(units.equals(GoalEntry.HOUR_STRING)){
            unitsSpinner.setSelection(GoalEntry.HOURS);
        }
        else if(units.equals(GoalEntry.SECOND_STRING)){
            unitsSpinner.setSelection(GoalEntry.SECONDS);
        }
        else if(units.equals(GoalEntry.TIMES_STRING)){
            unitsSpinner.setSelection(GoalEntry.TIMES);
        }
        else if(units.equals(GoalEntry.REPS_STRING)){
            unitsSpinner.setSelection(GoalEntry.REPS);
        }
        else if(units.equals(GoalEntry.PAGES_STRING)){
            unitsSpinner.setSelection(GoalEntry.PAGES);
        }
        else{
            Log.e(LOGTAG, "The units were not recognized when preloading data.");
        }

        if(GoalEntry.isValidTime(unitsSelected)){

            quotaEditText.setText(Double.toString(GoalEntry.roundAndConvertTime(goal.getQuota())));
        }
        else {
            quotaEditText.setText(Integer.toString(goal.getQuota()));
        }

        int sessions = goal.getSessions();

        // Set the units for the quota line and sessions line
        String freqUnits = null;
        if(freq == GoalEntry.MONTHLYGOAL){
            freqUnits = "month";
        } else if(freq == GoalEntry.WEEKLYGOAL){
            freqUnits = "week";
        } else if(freq == GoalEntry.DAILYGOAL){
            freqUnits = "day";
        }

        String quotaUnits = units;
        String sessionsUnits;
        if(sessions == 1){
            sessionsUnits = "session";
        } else{
            sessionsUnits = "sessions";
        }

        if(freqUnits != null){
            quotaUnits += "/" + freqUnits;
            sessionsUnits += "/" + freqUnits;
        }
        quotaUnitsTextView.setText(quotaUnits);
        sessionsUnitsTextView.setText(sessionsUnits);
    }
}