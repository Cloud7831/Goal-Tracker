package com.cloud7831.goaltracker.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.cloud7831.goaltracker.HelperClasses.TimeHelper;
import com.cloud7831.goaltracker.R;
import com.cloud7831.goaltracker.Workers.NightlyUpdateWorker;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.concurrent.TimeUnit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

public class MainActivity extends AppCompatActivity {
    private static final int PROFILE_FRAG = 1;
    private static final int GOAL_LIST_FRAG = 2;
    private static final int WORKOUT_FRAG = 3;
    private static final int LISTS_FRAG = 4;

    private int currentFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // TODO: might want to set this to the goal list fragment layout.

        // Set the bottom navigation bar
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        // Set up a worker to update the goal database every night at a specific time.
        String nightlyUpdateStr = "NightlyUpdateWorker";
        WorkManager workManager = WorkManager.getInstance(this);
        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(NightlyUpdateWorker.class).setInitialDelay(TimeHelper.calcMilliUntilNewDay(), TimeUnit.MILLISECONDS).build();

        workManager.enqueueUniqueWork(nightlyUpdateStr, ExistingWorkPolicy.KEEP, workRequest);

        // Prepare the container with the fragments we will need.
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Add in the Goal List fragment
        fragmentTransaction.replace(R.id.fragment_container, new GoalsListFragment());
        currentFragment = GOAL_LIST_FRAG;

        // Commit all the changes.
        fragmentTransaction.commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch(item.getItemId()){
                        case R.id.nav_profile:
                            if(currentFragment == PROFILE_FRAG){
                                // Don't reload the fragment
                                return true;
                            }
                            currentFragment = PROFILE_FRAG;
                            selectedFragment = new ProfileFragment();
                            break;
                        case R.id.nav_goal_list:
                            if(currentFragment == GOAL_LIST_FRAG){
                                // Don't reload the fragment
                                return true;
                            }
                            currentFragment = GOAL_LIST_FRAG;
                            selectedFragment = new GoalsListFragment();
                            break;
                        case R.id.nav_workout:
                            if(currentFragment == WORKOUT_FRAG){
                                // Don't reload the fragment
                                return true;
                            }
                            currentFragment = WORKOUT_FRAG;
                            selectedFragment = new WorkoutFragment();
                            break;
                    }

                    // Prepare the container with the fragments we will need.
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    // Add in the selected fragment
                    fragmentTransaction.replace(R.id.fragment_container, selectedFragment);

                    // Commit all the changes.
                    fragmentTransaction.commit();
                    return true;
                }
            };
}
