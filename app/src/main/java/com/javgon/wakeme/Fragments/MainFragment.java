package com.javgon.wakeme.Fragments;

import android.animation.LayoutTransition;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.javgon.wakeme.Model.Alarm;
import com.javgon.wakeme.Model.LCoordinates;
import com.javgon.wakeme.Services.AlarmService;
import com.javgon.wakeme.Services.DatabaseServices;
import com.javgon.wakeme.Model.MyUserData;
import com.javgon.wakeme.R;

import java.util.ArrayList;

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
    MyUserData mUserData;
    public static MainFragment newInstance(){
        MainFragment newFrag = new MainFragment();
        Bundle args = new Bundle();
        newFrag.setArguments(args);
        return newFrag;
    }


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mUserData=mUserData.getInstance(getActivity());
        readOwnAlarms();
        readAlarms();
        addAlarmService();
    }

    public void addAlarmService(){

        Intent intent= AlarmService.newIntent(getActivity());
        getActivity().startService(intent);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.frag_main, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){

        mapView = (RelativeLayout)view.findViewById(R.id.fl_map_view);
        alarmView= (LinearLayout) view.findViewById(R.id.layout_alarm_clocks);
        worldMapSurfaceView = new WorldMapSurfaceView(getActivity());
        addAlarmLayout = (LinearLayout) view.findViewById(R.id.layout_add_alarm);
        addAlarmLayout.setVisibility(View.GONE);
        addAlarmLayout.setOnClickListener(this);
        mapView.addView(worldMapSurfaceView);
        alarmView.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);

    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        displayAlarms();
        worldMapSurfaceView.onResumeMySurfaceView();
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        worldMapSurfaceView.onPauseMySurfaceView();
    }


    /**
     * Get the location of where the other users set their alarms
     * @param alarms takes in a list of alarms to read location from user list
     */
    @Override
    protected void getAlarmLocations(ArrayList<Alarm> alarms){
        DatabaseServices post = DatabaseServices.getInstance(getActivity());
        showProgressDialog();
        post.getUserLocationFromAlarm(alarms,new DatabaseServices.AlarmLocationCallBack(){
            @Override
            public void onSuccess(ArrayList<LCoordinates> locations){
                hideProgressDialog();
                worldMapSurfaceView.setCoordinates(locations);
                for (LCoordinates location:locations)
                    Log.d("Alarms locations", location.toString());

            }
            @Override
            public void onFail(String msg){
                Log.e("FAIL", "getAlarmLocations() "+ msg);
                Toast.makeText(getActivity(), msg,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Read alarms set by user (may have set more than one)
     */
    private void readOwnAlarms(){
        DatabaseServices post = DatabaseServices.getInstance(getActivity());
        post.readOwnAlarms(new DatabaseServices.AlarmCallback(){
            @Override
            public void onSuccess(ArrayList<Alarm> alarms){
                mUserData.setAlarmList(alarms);
                displayAlarms();
            }
            @Override
            public void onFail(String msg){
                Log.e("FAIL","readownalarms() "+ msg);
                Toast.makeText(getActivity(), msg,
                        Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * Display list of alarms set by user once alarm list has been retrieved
     */
    private void displayAlarms(){
        ArrayList<Alarm> alarms =mUserData.getAlarmList();
        if (alarms.size()<3)  //if no alarms are set by user,
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
                DatabaseServices post = DatabaseServices.getInstance(getActivity());
                String uid=mUserData.getUserID();
                int alarmId=mUserData.getAvailableID();
                Alarm alarm = new Alarm(uid,alarmId,12,00);
                ArrayList<Integer> repeat = new ArrayList<>(7);
                repeat.add(1);
                repeat.add(2);
                alarm.setRepeatDays(repeat);
                mUserData.addAlarm(alarm);
                Log.d("ALARMCLOCK",mUserData.toString());
                post.writeAlarm(alarm);
                addAlarmToView(alarm,alarmId);
                if (mUserData.getAlarmSize()>=3)
                    addAlarmLayout.setVisibility(View.GONE);
                break;
            default:
                break;

        }
    }


}
