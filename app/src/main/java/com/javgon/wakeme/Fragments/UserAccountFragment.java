package com.javgon.wakeme.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.javgon.wakeme.R;

/**
 * Created by javgon on 6/12/2017.
 */

public class UserAccountFragment extends BaseFragment {


    public static UserAccountFragment newInstance(){

        UserAccountFragment newFrag = new UserAccountFragment();

        return newFrag;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        View rootView = inflater.inflate(R.layout.frag_user_edit, container, false);

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

    }

}
