package com.cloud7831.goaltracker.Activities;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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
import android.widget.Toast;

import com.cloud7831.goaltracker.R;

public class GoalEditorActivity extends AppCompatActivity{

    /** EditText field to enter the goal's name */
    private EditText nameEditText;

    private Uri currentGoalUri;

    private boolean goalHasChanged = false;

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
}