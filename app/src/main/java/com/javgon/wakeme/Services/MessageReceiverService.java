package com.javgon.wakeme.Services;

/**
 * Created by javgon on 6/13/2017.
 */


import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.WindowManager;

import com.google.firebase.storage.StorageReference;
import com.javgon.wakeme.Activities.WakeUpActivity;
import com.javgon.wakeme.Model.AudioMessage;
import com.javgon.wakeme.Model.MyUserData;
import com.javgon.wakeme.Model.MyUserMessages;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Created by javier gon on 6/6/2017.
 * Will retrieve messages from database every 30 minutes, in background
 */

public class MessageReceiverService extends IntentService {
    public static final String TAG = "AlarmService";   //for logging and passing a name to base class IntentService
    private static final long POLL_INTERVAL_MS = TimeUnit.MINUTES.toMillis(30);     //run every half hour
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public MessageReceiverService() {
        super(TAG);
    }

    public static Intent newIntent (Context context){
        return new Intent (context, MessageReceiverService.class);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.i(TAG, "received an intent: "+ intent);
        receiveMessages(this);
    }


    public static void setServiceAlarm(Context context, boolean isOn){
        Intent intent = MessageReceiverService.newIntent(context);
        PendingIntent pi= PendingIntent.getService(context,0,intent,0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (isOn){
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(),POLL_INTERVAL_MS,pi);
            Log.i(TAG, "set alarm for requestCode: "+0 + "every "+ POLL_INTERVAL_MS + " minutes.");

        }else
        {
            alarmManager.cancel(pi);
            pi.cancel();
        }
    }

    public static void receiveMessages(final Context context){
        Log.i(TAG, "receiveMessages() ");
        String UID= MyUserData.getInstance(context).getUserID();

        DatabaseServices.getInstance(context).readUserMessages(UID,new DatabaseServices.MessagesCallback() {
            @Override
            public void onSuccess(ArrayList<AudioMessage> msgs) {
                for (AudioMessage m : msgs)
                    downloadAudioMessage(context,m);        //when messages are received, download audio from message Uri
            }

            @Override
            public void onFail(String msg) {
                Log.e(TAG, "receiveMessages() " + msg);
            }
        });
    }


    private static void downloadAudioMessage(final Context context, final AudioMessage msg){

        StorageServices.getInstance(context).downloadAudio(msg, new StorageServices.DownloadCallBack() {
            @Override
            public void onSuccess() {
                storeMessageToPrefs(context,msg);       //if download succesfull, store message in userprefs (app will open this msg later when alarm rings)
            }

            @Override
            public void onFail(String msg) {
                Log.e(TAG, "receiveMessages() " + msg);
            }
        });
    }

    private static void storeMessageToPrefs(final Context context, AudioMessage msg){
        MyUserMessages.getInstance(context).addAudioMessage(msg);
    }
}
