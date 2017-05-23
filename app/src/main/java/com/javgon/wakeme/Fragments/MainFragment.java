package com.javgon.wakeme.Fragments;

import android.os.Bundle;
import android.provider.AlarmClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.javgon.wakeme.Activities.MainActivity;
import com.javgon.wakeme.Activities.NavigationPage;
import com.javgon.wakeme.Model.Alarm;
import com.javgon.wakeme.Model.JTime;
import com.javgon.wakeme.Model.LCoordinates;
import com.javgon.wakeme.Model.PostServices;
import com.javgon.wakeme.Other.MyUserData;
import com.javgon.wakeme.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by javier gonzalez on 4/26/2017.
 * The first fragment the user sees after the user logs on
 * Shows the time until your next alarm.
 * As well as a world map with users who are about to wake up
 */

public class MainFragment extends BaseFragment implements View.OnClickListener{
    WorldMapSurfaceView worldMapSurfaceView;
    RelativeLayout mapView;
    LinearLayout alarmView;
    LinearLayout addAlarmLayout;


    public static MainFragment newInstance(){
        MainFragment newFrag = new MainFragment();
        Bundle args = new Bundle();
        newFrag.setArguments(args);
        return newFrag;
    }


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.frag_main, container, false);
        readAlarms();
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        mapView = (RelativeLayout)view.findViewById(R.id.fl_map_view);
        alarmView= (LinearLayout) view.findViewById(R.id.layout_alarm_clocks);
        worldMapSurfaceView = new WorldMapSurfaceView(getActivity());
        addAlarmLayout = (LinearLayout) view.findViewById(R.id.layout_add_alarm);
        addAlarmLayout.setVisibility(View.VISIBLE);
        addAlarmLayout.setOnClickListener(this);
        mapView.addView(worldMapSurfaceView);

        readOwnAlarms();

    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        worldMapSurfaceView.onResumeMySurfaceView();
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        worldMapSurfaceView.onPauseMySurfaceView();
    }



    /**
     * Read alarms set by other users world wide
     */
    private void readAlarms(){
        PostServices post = PostServices.getInstance(getActivity());
        post.readAlarms(new PostServices.AlarmCallback(){
            @Override
            public void onSuccess(ArrayList<Alarm> alarms){
                getAlarmLocations(alarms);
                for (Alarm alarm:alarms)
                    Log.d("Alarms map", alarm.toString());
            }
            @Override
            public void onFail(String msg){
                Log.e("FAIL", msg);
                Toast.makeText(getActivity(), msg,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Get the location of where the other users set their alarms
     * @param alarms takes in a list of alarms to read location from user list
     */
    private void getAlarmLocations(ArrayList<Alarm> alarms){
        PostServices post = PostServices.getInstance(getActivity());

        post.getUserLocationFromAlarm(alarms,new PostServices.AlarmLocationCallBack(){
            @Override
            public void onSuccess(ArrayList<LCoordinates> locations){
                worldMapSurfaceView.setCoordinates(locations);
                for (LCoordinates location:locations)
                    Log.d("Alarms locations", location.toString());

            }
            @Override
            public void onFail(String msg){
                Log.e("FAIL", msg);
                Toast.makeText(getActivity(), msg,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Read alarms set by user (may have set more than one)
     */
    private void readOwnAlarms(){
        PostServices post = PostServices.getInstance(getActivity());
        post.readOwnAlarms(new PostServices.AlarmCallback(){
            @Override
            public void onSuccess(ArrayList<Alarm> alarms){
                MyUserData.getInstance().setAlarmList(alarms);
                displayAlarms();
            }
            @Override
            public void onFail(String msg){
                Log.e("FAIL", msg);
                Toast.makeText(getActivity(), msg,
                        Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * Display list of alarms set by user once alarm list has been retrieved
     */
    private void displayAlarms(){
        ArrayList<Alarm> alarms =MyUserData.getInstance().getAlarmList();
        if (alarms.isEmpty())  //if no alarms are set by user,
            addAlarmLayout.setVisibility(View.VISIBLE); //prompt user to add new alarm
        else //else display set alarms
        for (int i=0; i<alarms.size(); i++){
            addAlarmToView(alarms.get(i),i);
        }
    }

    private void addAlarmToView(Alarm alarm, int id){
        FrameLayout alarmFrame = new FrameLayout(getActivity());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        alarmFrame.setLayoutParams(params);
        alarmFrame.setId(id+1);
        alarmView.addView(alarmFrame,0);  //add it to top (0) of view
        alarmView.invalidate();
        createAlarmFrag(alarmFrame.getId(), alarm);
        Log.d("alarmView", ""+alarmFrame.getId());
    }


    /**
     Using fragment instead of recycler view, alarm view is complicated
     */
    private void createAlarmFrag(int id, Alarm alarm){
        //inflate card page
        AlarmClockFragment nextFrag= AlarmClockFragment.newInstance(alarm);
        if (getFragmentManager().findFragmentByTag("ALARMCLOCK"+id)==null) { //if fragment does not exist
            getFragmentManager().beginTransaction()
                    .replace(id, nextFrag, "ALARMCLOCK"+id)
                    .commit();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_add_alarm:
                PostServices post = PostServices.getInstance(getActivity());
                String uid= MyUserData.getInstance().getUserID();
                int alarmId=MyUserData.getInstance().getAvailableID();
                Alarm alarm = new Alarm(uid,alarmId,12,00);
                ArrayList<Integer> repeat = new ArrayList<>(7);
                repeat.add(1);
                repeat.add(2);
                alarm.setRepeatDays(repeat);
                MyUserData.getInstance().addAlarm(alarm);
                Log.d("ALARMCLOCK",MyUserData.getInstance().toString());
                post.writeAlarm(alarm);
                addAlarmToView(alarm,alarmId);
                break;
            default:
                break;

        }
    }

}
