package com.javgon.wakeme.Fragments;

import android.os.Bundle;
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
import com.javgon.wakeme.Model.LCoordinates;
import com.javgon.wakeme.Model.PostServices;
import com.javgon.wakeme.R;

import java.util.ArrayList;

/**
 * Created by jaavier gonzalez on 4/26/2017.
 * The first fragment the user sees after the user logs on
 * Shows the time until your next alarm.
 * As well as a world map with users who are about to wake up
 */

public class MainFragment extends BaseFragment{
    WorldMapSurfaceView worldMapSurfaceView;
    RelativeLayout mapView;

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

        worldMapSurfaceView = new WorldMapSurfaceView(getActivity());
        mapView.addView(worldMapSurfaceView);
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

    public void readAlarms(){
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

    public void getAlarmLocations(ArrayList<Alarm> alarms){
        PostServices post = PostServices.getInstance(getActivity());

        post.getAlarmLocation(alarms,new PostServices.AlarmLocationCallBack(){
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

}
