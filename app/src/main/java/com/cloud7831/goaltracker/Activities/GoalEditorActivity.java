package com.cloud7831.goaltracker.Activities;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.core.app.NavUtils;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.cloud7831.goaltracker.Data.GoalDbHelper;
import com.cloud7831.goaltracker.R;
import com.cloud7831.goaltracker.Data.GoalsContract;

import androidx.appcompat.app.AppCompatActivity;

public class GoalEditorActivity extends AppCompatActivity {
    public static final String EXTRA_TITLE = "com.cloud7831.goaltracker.EXTRA_TITLE";
    public static final String EXTRA_QUOTA = "com.cloud7831.goaltracker.EXTRA_QUOTA";
    public static final String EXTRA_FREQUENCY = "com.cloud7831.goaltracker.EXTRA_FREQUENCY";
    public static final String EXTRA_UNITS = "com.cloud7831.goaltracker.EXTRA_UNITS";
    public static final String EXTRA_INTENTION = "com.cloud7831.goaltracker.EXTRA_INTENTION";
    public static final String EXTRA_PRIORITY = "com.cloud7831.goaltracker.EXTRA_PRIORITY";

    /** EditText field to enter the goal's name */
    private EditText nameEditText;
    private EditText quotaEditText;

    private Uri currentGoalUri;

    private boolean goalHasChanged = false;

    private Spinner frequencySpinner;
    private int frequencySelected = GoalsContract.GoalEntry.WEEKLYGOAL;

    private Spinner intentionSpinner;
    private int intentionSelected = GoalsContract.GoalEntry.REGULAR;

    private int prioritySelected = 3;

    private Spinner unitsSpinner;
    private int unitsSelected = GoalsContract.GoalEntry.MINUTES;

    private GoalDbHelper dbHelper;

    private boolean editMode = false; // True if we're in edit mode, false if we're in add mode.

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

        Intent intent = getIntent();
        currentGoalUri = intent.getData();

        if (currentGoalUri == null){
            // This is a new goal, so change the app bar to say "Add a New Goal".
            editMode = false;
            setTitle(getString(R.string.editor_activity_title_new_goal));

            // It doesn't make sense to delete a pet so we can hide that option.
            invalidateOptionsMenu();
        }
        else{
            // This is an existing pet, so change the app bar to say "Edit Pet".
            setTitle(getString(R.string.editor_activity_title_edit_goal));
            editMode = true;

            //TODO: fill in the information with the current data of the goal.
        }


        // Find all relevant views that we will need to read user input from
        nameEditText = findViewById(R.id.edit_goal_name);
        quotaEditText = findViewById(R.id.edit_goal_quota);
        frequencySpinner = findViewById(R.id.spinner_goal_frequency);
        unitsSpinner = findViewById(R.id.spinner_goal_units);
        intentionSpinner = findViewById(R.id.spinner_goal_intention);

        // Set the OnTouchListener so we know if they've modified data.
        nameEditText.setOnTouchListener(touchListener);
        frequencySpinner.setOnTouchListener(touchListener);

        setupFrequencySpinner();
        setupIntentionSpinner();
        setupUnitsSpinner();

        dbHelper = new GoalDbHelper(this);
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
                    if (selection.equals(getString(R.string.goal_unit_hours))) {
                        unitsSelected = GoalsContract.GoalEntry.HOURS;
                    } else if (selection.equals(getString(R.string.goal_unit_minutes))) {
                        unitsSelected = GoalsContract.GoalEntry.MINUTES;
                    } else {
                        unitsSelected = GoalsContract.GoalEntry.UNDEFINED;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                unitsSelected = GoalsContract.GoalEntry.UNDEFINED;
            }
        });
    }

    /**
     * Setup the dropdown spinner that allows the user to select the intention of the goal.
     */
    private void setupIntentionSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
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
                        intentionSelected = GoalsContract.GoalEntry.AVOIDANCE;
                    } else if (selection.equals(getString(R.string.goal_intention_regular))) {
                        intentionSelected = GoalsContract.GoalEntry.REGULAR;
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



        // --------------------------------- TITLE -------------------------------------
        String titleString = nameEditText.getText().toString().trim();

        if(TextUtils.isEmpty(titleString)){
            Toast.makeText(this, "You cannot save a goal without a name.", Toast.LENGTH_SHORT).show();
            return;
        }

        data.putExtra(EXTRA_TITLE, titleString);


        // --------------------------------- QUOTA -------------------------------------
        String quotaString = quotaEditText.getText().toString().trim();
        int quota = 0;

        if(!TextUtils.isEmpty(quotaString)){
            quota = Integer.parseInt(quotaString);
        }

        data.putExtra(EXTRA_QUOTA, quota);

        // --------------------------------- INTENTION -------------------------------------
        // Note: setting the variables happens in the onClickListeners created in the onCreate method.
        data.putExtra(EXTRA_INTENTION, intentionSelected);

        // --------------------------------- UNITS -------------------------------------
        // Note: setting the variables happens in the onClickListeners created in the onCreate method.
        data.putExtra(EXTRA_UNITS, unitsSelected);

        // --------------------------------- FREQUENCY -------------------------------------
        // Note: setting the variables happens in the onClickListeners created in the onCreate method.
        data.putExtra(EXTRA_FREQUENCY, frequencySelected);


        // --------------------------------- PRIORITY -------------------------------------
        // Note: setting the variables happens in the onClickListeners created in the onCreate method.
        data.putExtra(EXTRA_PRIORITY, prioritySelected);

//
//        values.put(GoalsContract.GoalEntry.GOAL_NAME,           nameString);
//        values.put(GoalsContract.GoalEntry.GOAL_UNITS,          unitsSelected);
//        values.put(GoalsContract.GoalEntry.GOAL_FREQUENCY,      frequencySelected);
//        values.put(GoalsContract.GoalEntry.GOAL_INTENTION,      intentionSelected);
//
//        if(editMode){
//            // Edit the details of a goal in the database
//            int rowsAffected = getContentResolver().update(currentGoalUri, values, null, null);
//
//            if(rowsAffected == 0){
//                Toast.makeText(this, "Updating goal failed", Toast.LENGTH_SHORT).show();
//            }
//            else{
//                Toast.makeText(this, "Updating goal successful", Toast.LENGTH_SHORT).show();
//            }
//        }
//        else if(editMode == false){
//            // Add the goal to the database
//
//            Uri uri = getContentResolver().insert(GoalsContract.GoalEntry.CONTENT_URI, values);
//
//            if(uri == null){
//                Toast.makeText(this, "Error with saving goal data.", Toast.LENGTH_SHORT).show();
//            }
//            else{
//                Toast.makeText(this, "Goal saved successfully.", Toast.LENGTH_SHORT).show();
//            }
//        }
//
//        return true;

        setResult(RESULT_OK, data);
        finish();

    }



    private void deleteGoal(){
//        getContentResolver().delete(currentGoalUri, null, null);
//
//        Toast.makeText(this, "The goal has been deleted.", Toast.LENGTH_SHORT).show();
    }
}