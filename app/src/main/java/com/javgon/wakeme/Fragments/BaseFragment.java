package com.javgon.wakeme.Fragments;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.javgon.wakeme.Model.Alarm;
import com.javgon.wakeme.Services.DatabaseServices;
import com.javgon.wakeme.Model.MyUserData;
import com.javgon.wakeme.R;

import java.util.ArrayList;

public class BaseFragment extends Fragment {

    public ProgressDialog mProgressDialog;
    private MyUserData mUserData;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mUserData=mUserData.getInstance(getActivity());
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }


    /**
     * Read alarms set by other users world wide
     */
    protected void readAlarms(){
        DatabaseServices post = DatabaseServices.getInstance(getActivity());
        post.readAlarms(new DatabaseServices.AlarmCallback(){
            @Override
            public void onSuccess(ArrayList<Alarm> alarms){
                mUserData.setOthersAlarmList(alarms);
                getAlarmLocations(alarms);
                getUserSlots();
                for (Alarm alarm:alarms)
                    Log.d("readAlarms", alarm.toString());
            }
            @Override
            public void onFail(String msg){
                Log.e("FAIL", "readalarms() "+msg);
                Toast.makeText(getActivity(), msg,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void getAlarmLocations(ArrayList<Alarm> alarms){}
    protected void getUserSlots(){
    }

    public void closeFragment(){
        //fragCallInfo.setVisibility(GONE);
        if (getFragmentManager().beginTransaction() != null)
            getActivity().getSupportFragmentManager().popBackStack();

    }

}