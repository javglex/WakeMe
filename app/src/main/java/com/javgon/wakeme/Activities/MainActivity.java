package com.javgon.wakeme.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.javgon.wakeme.Model.Alarm;
import com.javgon.wakeme.Model.PostServices;
import com.javgon.wakeme.Fragments.AuthUserFragment;
import com.javgon.wakeme.Model.LCoordinates;
import com.javgon.wakeme.Other.DummyData;
import com.javgon.wakeme.Other.LocationService;
import com.javgon.wakeme.R;

import java.util.ArrayList;


public class MainActivity extends BaseActivity {

    TextView tvWelcome;
    FirebaseUser mUser;
    LocationService mLoc;
    Alarm alarm = new Alarm();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CheckUser();

        DummyData du = new DummyData();
        //du.writeTestUsers(this,du.generateUsers());
        Log.d("alarms","----------------------------- WRITING TEST ALARMS");
        du.writeTestAlarms(this,du.generateAlarms());

        //Log.d("hours until",""+alarm.getHoursUntilAlarm());

        mLoc= new LocationService(this);
    }


    @Override
    protected void onResume(){
        super.onResume();
    }


    public void CheckUser(){
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        if (mUser != null) {

            Intent myIntent = new Intent(MainActivity.this, NavigationPage.class);
           // myIntent.putExtra("key", value); //Optional parameters
            MainActivity.this.startActivity(myIntent);

            getSupportActionBar().show();

            PostServices post = PostServices.getInstance(this);

            post.writeUser(mUser.getUid(),mUser.getDisplayName(), mUser.getEmail());

            // Name, email address, and profile photo Url
            String name = mUser.getDisplayName();
            String email = mUser.getEmail();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            String uid = mUser.getUid();
            tvWelcome=(TextView)findViewById(R.id.tv_welcome);
            tvWelcome.setText(name + " " + email + " " + uid );
            mLoc= new LocationService(this);

            if(mLoc.canGetLocation()) { // gps enabled} // return boolean true/false
                LCoordinates location = new LCoordinates();
                location.setLatitude(mLoc.getLatitude());
                location.setLongitude(mLoc.getLongitude());
                post.writeUserLocation(mUser.getUid(),location);

                post.readUserLocation(uid,new PostServices.LocCallback(){
                    @Override
                    public void onSuccess(LCoordinates loc){
                        displayUserLocation(loc); //then display list of atms
                    }
                    @Override
                    public void onFail(String msg){
                        Log.e("FAIL", msg);
                        Toast.makeText(MainActivity.this, msg,
                                Toast.LENGTH_SHORT).show();
                    }
                });

            }else
                mLoc.showSettingsAlert();

            Log.d("alarms","------------- READING TEST ALARMS-------------------");

            post.readAlarms(new PostServices.AlarmCallback(){
                @Override
                public void onSuccess(ArrayList<Alarm> alarms){
                    for (Alarm alarm : alarms){
                        Log.d("alarms ", " ID: "+alarm.getAlarmID()+ "  hours until: "+alarm.getHoursUntilAlarm()+ "  alarmtime: "+ alarm.getAlarmTimeHours()+":"+alarm.getAlarmTimeMinutes());
                    }
                }
                @Override
                public void onFail(String msg){
                    Log.e("FAIL", msg);
                    Toast.makeText(MainActivity.this, msg,
                            Toast.LENGTH_SHORT).show();
                }
            });

        }else
            displayLogInPage();
    }


    /**
     Showing log in details
     */

    private void displayLogInPage(){
        //inflate card page

        AuthUserFragment nextFrag= AuthUserFragment.newInstance();

        if (getFragmentManager().findFragmentByTag("AUTHUSER")==null) { //if fragment does not exist
            getFragmentManager().beginTransaction()
                    .replace(R.id.main_container, nextFrag, "AUTHUSER")
                    .addToBackStack(null)
                    .commit();
        }



    }

    public void displayUserLocation(LCoordinates loc){
        String name = mUser.getDisplayName();
        String email = mUser.getEmail();


        tvWelcome.setText(name + " " + email + " "  + "\n Location: " + loc.toString());



    }


}
