package com.cloud7831.goaltracker.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.cloud7831.goaltracker.R;
import com.cloud7831.goaltracker.Data.GoalsContract;

public class GoalEditorActivity extends AppCompatActivity{

    /** EditText field to enter the goal's name */
    private EditText nameEditText;

    private Uri currentGoalUri;

    private boolean goalHasChanged = false;

    private Spinner intervalSpinner;
    private GoalsContract.GoalsInterval intervalSelected;

    private boolean editMode = false; // True if we're in edit mode, false if we're in add mode.


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
        nameEditText = (EditText) findViewById(R.id.edit_goal_name);
        intervalSpinner = (Spinner) findViewById(R.id.spinner_goal_interval);

        // Set the OnTouchListener so we know if they've modified data.
        nameEditText.setOnTouchListener(touchListener);
        intervalSpinner.setOnTouchListener(touchListener);

        setupIntervalSpinner();
    }

    /**
     * Setup the dropdown spinner that allows the user to select the interval of the goal.
     */
    private void setupIntervalSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter intervalSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_interval_options, R.layout.spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        intervalSpinnerAdapter.setDropDownViewResource(R.layout.spinner_item);

        // Apply the adapter to the spinner
        intervalSpinner.setAdapter(intervalSpinnerAdapter);

        // Set the integer mSelected to the constant values
        intervalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.goal_interval_daily))) {
                        intervalSelected = GoalsContract.GoalsInterval.DAILYGOAL;
                    } else if (selection.equals(getString(R.string.goal_interval_weekly))) {
                        intervalSelected = GoalsContract.GoalsInterval.WEEKLYGOAL;
                    } else if (selection.equals(getString(R.string.goal_interval_monthly))) {
                        intervalSelected = GoalsContract.GoalsInterval.MONTHLYGOAL;
                    } else if (selection.equals(getString(R.string.goal_interval_fixed))) {
                        intervalSelected = GoalsContract.GoalsInterval.FIXEDGOAL;
                    } else {
                        intervalSelected = GoalsContract.GoalsInterval.UNDEFINED;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                intervalSelected = GoalsContract.GoalsInterval.UNDEFINED;
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
            case R.id.action_delete:
            case android.R.id.home:
                // If the pet hasn't changed, continue with navigating up to parent activity
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

    private View.OnTouchListener touchListener = new View.OnTouchListener(){
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent){
            goalHasChanged = true;
            return false;
        }
    };
}