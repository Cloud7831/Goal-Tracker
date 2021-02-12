package com.cloud7831.goaltracker.Workers;

import android.content.Context;
import android.util.Log;
import com.cloud7831.goaltracker.Data.GoalDatabase;
import com.cloud7831.goaltracker.HelperClasses.TimeHelper;

import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class NightlyUpdateWorker extends Worker {
    private static final String LOGTAG = "NightlyUpdateWorker";

    public NightlyUpdateWorker(@NonNull Context context, @NonNull WorkerParameters params){
        super(context, params);
    }

    @Override
    public Result doWork(){
        try{
            Log.i(LOGTAG, "started db update.");
            // Do the work
            GoalDatabase db = GoalDatabase.getInstance(getApplicationContext());
            db.nightlyGoalUpdate();

            Log.i(LOGTAG, "finished db update");

            // Set the next worker to start at midnight tomorrow.
            long ms = TimeHelper.calcMilliUntilNewDay();
            String nightlyUpdateStr = "NightlyUpdateWorker";
            WorkManager workManager = WorkManager.getInstance(getApplicationContext());
            OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(NightlyUpdateWorker.class).setInitialDelay(ms, TimeUnit.MILLISECONDS).build();

            workManager.enqueueUniqueWork(nightlyUpdateStr, ExistingWorkPolicy.REPLACE, workRequest);

            return Result.success();
        } catch (Exception e){
            Log.e(LOGTAG, "worker failed and will try again later.");
            return Result.retry();
        }
    }

}
