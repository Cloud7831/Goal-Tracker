package com.cloud7831.goaltracker.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.core.app.NavUtils;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cloud7831.goaltracker.Data.GoalsContract;
import com.cloud7831.goaltracker.Objects.Goal;
import com.cloud7831.goaltracker.R;
import com.cloud7831.goaltracker.Data.GoalsContract.GoalEntry;

import java.util.Set;

public class GoalEditorActivity extends AppCompatActivity {
    public static final String LOGTAG = "GoalEditorActivity";

    // Behind the scenes
    public static final String EXTRA_ID = "com.cloud7831.goaltracker.EXTRA_ID";

    // Overview
    public static final String EXTRA_TITLE = "com.cloud7831.goaltracker.EXTRA_TITLE";
    public static final String EXTRA_INTENTION = "com.cloud7831.goaltracker.EXTRA_INTENTION";
    public static final String EXTRA_PRIORITY = "com.cloud7831.goaltracker.EXTRA_PRIORITY";
    public static final String EXTRA_CLASSIFICATION = "com.cloud7831.goaltracker.EXTRA_CLASSIFICATION";
    public static final String EXTRA_IS_PINNED = "com.cloud7831.goaltracker.EXTRA_IS_PINNED";

    // Schedule
    public static final String EXTRA_FREQUENCY = "com.cloud7831.goaltracker.EXTRA_FREQUENCY";
    public static final String EXTRA_DEADLINE = "com.cloud7831.goaltracker.EXTRA_DEADLINE";
    public static final String EXTRA_DURATION= "com.cloud7831.goaltracker.EXTRA_DURATION";
    public static final String EXTRA_SESSIONS = "com.cloud7831.goaltracker.EXTRA_SESSIONS";
    public static final String EXTRA_SCHEDULED_TIME = "com.cloud7831.goaltracker.EXTRA_SCHEDULED_TIME";

    // Measurement
    public static final String EXTRA_IS_MEASUREABLE = "com.cloud7831.goaltracker.EXTRA_IS_MESURABLE";
    public static final String EXTRA_QUOTA = "com.cloud7831.goaltracker.EXTRA_QUOTA";
    public static final String EXTRA_UNITS = "com.cloud7831.goaltracker.EXTRA_UNITS";

    private boolean goalHasChanged = false;


    /** EditText field to enter the goal's name */
    private EditText titleEditText;
    private EditText quotaEditText;
    private TextView quotaUnitsTextView;
    private EditText sessionsEditText;
    private TextView sessionsUnitsTextView;

    private Spinner frequencySpinner;
    private int frequencySelected = GoalsContract.GoalEntry.WEEKLYGOAL;

    private Spinner intentionSpinner;
    private int intentionSelected = GoalsContract.GoalEntry.BUILDING;

    private int prioritySelected = 3;

    private Spinner unitsSpinner;
    private String unitsSelected;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_editor);

        // Find all relevant views that we will need to read user input from:
        // Overview
        titleEditText = findViewById(R.id.edit_goal_name);
        intentionSpinner = findViewById(R.id.spinner_goal_intention);
        pinnedCheckBox = findViewById(R.id.pinCheckBox);

        // Schedule
        frequencySpinner = findViewById(R.id.spinner_goal_frequency);

        // Measurement
        measurableCheckBox = findViewById(R.id.measurableCheckBox);
        unitsSpinner = findViewById(R.id.spinner_goal_units);
        quotaEditText = findViewById(R.id.edit_goal_quota);
        quotaUnitsTextView = findViewById(R.id.label_quota_units);
        sessionsEditText = findViewById(R.id.edit_goal_sessions);
        sessionsUnitsTextView = findViewById(R.id.label_sessions_units);

        // Set the OnTouchListener so we know if they've modified data.
        titleEditText.setOnTouchListener(touchListener);
        frequencySpinner.setOnTouchListener(touchListener);

        // Set the default value of unitsSelected. This can't be done before onCreate, because otherwise the resource R is null.
        unitsSelected = getString(R.string.goal_unit_minutes);

        setupFrequencySpinner();
        setupIntentionSpinner();
        setupUnitsSpinner();

        Intent intent = getIntent();
        if (!intent.hasExtra(EXTRA_ID)){
            // This is a new goal, so change the app bar to say "Add a New Goal".
            setTitle(getString(R.string.editor_activity_title_new_goal));

            // It doesn't make sense to delete a pet so we can hide that option.
            invalidateOptionsMenu();
        }
        else{
            // This is an existing pet, so change the app bar to say "Edit Pet".
            setTitle(getString(R.string.editor_activity_title_edit_goal));

            // Fill in all the edit texts
            titleEditText.setText(intent.getStringExtra(EXTRA_TITLE));
            quotaEditText.setText(Integer.toString(intent.getIntExtra(EXTRA_QUOTA, 0)));

            prefillCheckBoxes(intent.getIntExtra(EXTRA_IS_PINNED, 0), intent.getIntExtra(EXTRA_IS_MEASUREABLE, 0));

            //Set the frequency spinner
            int freq = intent.getIntExtra(EXTRA_FREQUENCY, 0);
            // subtract 1, because undefined (0) is not an option shown to the user.
            frequencySpinner.setSelection(freq - 1);

            // Set the intention spinner
            int intention = intent.getIntExtra(EXTRA_INTENTION, 0);
            intentionSpinner.setSelection(intention - 1);

            // Set the units spinner
            String units = intent.getStringExtra(EXTRA_UNITS);
            // Instead of doing the -1 I'm just going to ignore Undefined for units.
            Log.i(LOGTAG, "units - " + units);
            if(units == null){
                unitsSelected = "minutes";
            } else if(units.equals(getString(R.string.goal_unit_minutes))){
                unitsSpinner.setSelection(GoalEntry.MINUTES);
            }
            else if(units.equals(getString(R.string.goal_unit_hours))){
                unitsSpinner.setSelection(GoalEntry.HOURS);
            }
            else{
                Log.i(LOGTAG, "The units were not recognized.");
            }

            int sessions = intent.getIntExtra(EXTRA_SESSIONS, 0);

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

    /**
     * Setup the dropdown spinner that allows the user to select the frequency of the goal.
     */
    private void setupFrequencySpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter frequencySpinnerAdapter = ArrayAdapter.createFromResource(this,
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
        ArrayAdapter unitsSpinnerAdapter = ArrayAdapter.createFromResource(this,
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
                    if (selection.equals(getString(R.string.goal_unit_minutes))) {
                        unitsSelected = getString(R.string.goal_unit_minutes);
                    } else if (selection.equals(getString(R.string.goal_unit_hours))) {
                        unitsSelected = getString(R.string.goal_unit_hours);
                    } else {
                        unitsSelected = "UNDEFINED";
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                unitsSelected = "UNDEFINED";
            }
        });
    }

    /**
     * Setup the dropdown spinner that allows the user to select the intention of the goal.
     */
    private void setupIntentionSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use.
        // the spinner will use the default layout
        ArrayAdapter intentionSpinnerAdapter = ArrayAdapter.createFromResource(this,
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

    @Override
    public void onBackPressed(){
        // If the goal hasn't changed, continue with handling back button press
        if(!goalHasChanged){
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save the goal data to the database.
                saveGoal();
            case R.id.action_delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Are you sure you want to delete this goal?");
                builder.setNegativeButton("No take me back",
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
                                finish();
                            }
                        });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                return true;
            case android.R.id.home:
                // If the goal hasn't changed, continue with navigating up to parent activity
                if(!goalHasChanged){
                    // Navigate back to parent activity (The Goal List Fragment/Main Activity)
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }

                // There are unsaved changes potentially so warn the user.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(GoalEditorActivity.this);
                            }
                        };

                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveGoal(){
        // Get all the data the user entered and send it back as an intent.
        //TODO: it's probably better to make this a fragment that communicates with the Viewmodel itself.
        Intent data = new Intent();

        int id = getIntent().getIntExtra(EXTRA_ID, -1);
        if (id != -1){
            // If we are editing a goal, an ID was provided
            data.putExtra(EXTRA_ID, id);
        }

        // --------------------------------- TITLE -------------------------------------
        String titleString = titleEditText.getText().toString().trim();

        if(TextUtils.isEmpty(titleString)){
            Toast.makeText(this, "You cannot save a goal without a name.", Toast.LENGTH_SHORT).show();
            return;
        }

        data.putExtra(EXTRA_TITLE, titleString);

        // --------------------------------- INTENTION -------------------------------------
        // Note: setting the variables happens in the onClickListeners created in the onCreate method.
        data.putExtra(EXTRA_INTENTION, intentionSelected);

        // --------------------------------- PRIORITY -------------------------------------
        // Note: setting the variables happens in the onClickListeners created in the onCreate method.
        data.putExtra(EXTRA_PRIORITY, prioritySelected);

        // --------------------------------- CLASSIFICATION -------------------------------------
        // Note: setting the variables happens in the onClickListeners created in the onCreate method.
        data.putExtra(EXTRA_INTENTION, 0); //TODO: make a classification spinner

        // --------------------------------- IS PINNED ---------------------------------
        // Note: Do not use a boolean, because SQLite can't store booleans.
        if(pinnedCheckBox.isChecked()){
            isPinned = 1;
        }
        else{
            isPinned = 0;
        }

        data.putExtra(EXTRA_IS_PINNED, isPinned);

        // --------------------------------- QUOTA -------------------------------------
        String quotaString = quotaEditText.getText().toString().trim();
        int quota = 0;

        if(!TextUtils.isEmpty(quotaString)){
            quota = Integer.parseInt(quotaString);
        }

        data.putExtra(EXTRA_QUOTA, quota);

        // --------------------------------- UNITS -------------------------------------
        // Note: setting the variables happens in the onClickListeners created in the onCreate method.
        data.putExtra(EXTRA_UNITS, unitsSelected);

        // --------------------------------- FREQUENCY -------------------------------------
        // Note: setting the variables happens in the onClickListeners created in the onCreate method.
        data.putExtra(EXTRA_FREQUENCY, frequencySelected);

        // --------------------------------- IS MEASURABLE ---------------------------------
        // Note: Do not use a boolean, because SQLite can't store booleans.
        if(measurableCheckBox.isChecked()){
            isMeasurable = 1;
        }
        else{
            isMeasurable = 0;
        }

        data.putExtra(EXTRA_IS_MEASUREABLE, isMeasurable);

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
        data.putExtra(EXTRA_SESSIONS, sessions);

        if(TextUtils.isEmpty(titleString)){
            Toast.makeText(this, "You cannot save a goal without a name.", Toast.LENGTH_SHORT).show();
            return;
        }

        data.putExtra(EXTRA_TITLE, titleString);

        setResult(RESULT_OK, data);
        finish();

    }

    private void prefillCheckBoxes(int pinned, int measurable){
        // Fill in the checkboxes with the data from the intent
        if(pinned != 0){
            isPinned = 1;
        }
        pinnedCheckBox.setChecked(isPinned == 1);

        if(measurable != 0){
            isMeasurable = 1;
        }
        measurableCheckBox.setChecked(isMeasurable == 1);
    }



    private void deleteGoal(){
//        getContentResolver().delete(currentGoalUri, null, null);
//
//        Toast.makeText(this, "The goal has been deleted.", Toast.LENGTH_SHORT).show();
    }
}