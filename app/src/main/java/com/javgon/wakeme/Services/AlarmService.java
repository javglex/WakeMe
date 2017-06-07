package com.javgon.wakeme.Services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by javgon on 6/6/2017.
 */

public class AlarmService extends IntentService {
    private static final String TAG = "AlarmService";   //for logging and passing a name to base class IntentService

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public AlarmService() {
        super(TAG);
    }

    public static Intent newIntent (Context context){
        return new Intent (context, AlarmService.class);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.i(TAG, "received an intent: "+ intent);
    }
}
