package com.javgon.wakeme.Activities;


import android.app.ProgressDialog;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;
import com.javgon.wakeme.R;

public class BaseActivity extends AppCompatActivity {
    protected final static String LOG_TAG = BaseActivity.class.getSimpleName();

    public ProgressDialog mProgressDialog;
    LocationManager mLocationManager;

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
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

    public void showSnack(String msg){
        Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG)
                .setAction("OK", null)
                .setActionTextColor(Color.RED)
                .show();
    }



    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }




}