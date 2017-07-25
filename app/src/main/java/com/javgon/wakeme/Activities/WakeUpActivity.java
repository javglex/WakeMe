package com.javgon.wakeme.Activities;

import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.javgon.wakeme.R;
import com.javgon.wakeme.Services.AlarmService;
import com.javgon.wakeme.Services.MessageReceiverService;

/**
 * Created by javier gon on 6/8/2017.
 */

public class WakeUpActivity extends BaseActivity {



    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wake_up);
        Log.i(AlarmService.TAG, "launched wake up activity ");
        turnOnScreen();
        MessageReceiverService.receiveMessages(this);

    }


    private void turnOnScreen(){
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
    }



}
