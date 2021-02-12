package com.cloud7831.goaltracker.Activities;

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

import java.util.concurrent.TimeUnit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // TODO: might want to set this to the goal list fragment layout.

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

        // Commit all the changes.
        fragmentTransaction.commit();
    }

}
