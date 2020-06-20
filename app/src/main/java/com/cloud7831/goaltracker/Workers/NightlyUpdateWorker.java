package com.cloud7831.goaltracker.Workers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class NightlyUpdateWorker extends Worker {
    public NightlyUpdateWorker(@NonNull Context context, @NonNull WorkerParameters params){
        super(context, params);
    }

    @Override
    public Result doWork(){
        // TODO: write the code for updating the database each night.

        //


        try{
            return Result.success();
        } catch (Exception e){
            return Result.retry();
        }
    }

}
