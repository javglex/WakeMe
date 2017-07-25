package com.javgon.wakeme.Services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.WindowManager;

import com.javgon.wakeme.Activities.WakeUpActivity;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Created by javier gon on 6/6/2017.
 */

public class AlarmService extends IntentService {
    public static final String TAG = "AlarmService";   //for logging and passing a name to base class IntentService
    private static final long POLL_INTERVAL_MS = TimeUnit.MINUTES.toMillis(1);
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
        createWakeUpActivity();
    }


    public static void setServiceAlarm(Context context, boolean isOn, int requestCode, Calendar alarmTime){
        Intent intent = AlarmService.newIntent(context);
        PendingIntent pi= PendingIntent.getService(context,requestCode,intent,0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (isOn){
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmTime.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pi);
            Log.i(TAG, "set alarm for requestCode: "+requestCode + "at time: "+alarmTime.getTime().toString());

        }else
        {
            alarmManager.cancel(pi);
            pi.cancel();
        }
    }

    private void createWakeUpActivity(){
        Log.i(TAG, "createWakeUpACtivity() ");
        Intent intent = new Intent(this, WakeUpActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);         //START ACTIVITY IN NEW TASK
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        //intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        //intent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
        intent.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);      //SHOW ACTIVITY EVEN IF SCREEN IS LOCKED
        intent.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);      //TURN SCREEN ON
        intent.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);      //TURN SCREEN ON
        intent.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);      //UNLOCKS PHONE FOR THAT ACTIVITY
        this.startActivity(intent);
    }
}
