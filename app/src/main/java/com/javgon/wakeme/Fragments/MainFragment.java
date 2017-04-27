package com.javgon.wakeme.Fragments;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.javgon.wakeme.R;

/**
 * Created by jaavier gonzalez on 4/26/2017.
 * The first fragment the user sees after the user logs on
 * Shows the time until your next alarm.
 * As well as a world map with users who are about to wake up
 */

public class MainFragment extends BaseFragment{

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
//       ((AppCompatActivity) getActivity()).getSupportActionBar().hide();


        return rootView;
    }


}
