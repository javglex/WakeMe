package com.javgon.wakeme.Activities;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;
import com.javgon.wakeme.R;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class BaseActivity extends AppCompatActivity {
    protected final static String LOG_TAG = BaseActivity.class.getSimpleName();
    public ProgressDialog mProgressDialog;
    LocationManager mLocationManager;




    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }


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



    public void exitDialog(){

        // Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Chain together various setter methods to set the dialog characteristics
        builder.setMessage(R.string.exit_dialog_message)
                .setTitle(R.string.exit_dialog_title);


        // Add the buttons
        builder.setPositiveButton(R.string.exit_dialog_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button, exit app
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        builder.setNegativeButton(R.string.exit_dialog_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                dialog.cancel();
            }
        });


        // Get the AlertDialog from create()
        AlertDialog dialog = builder.create();
        builder.show();
    }




    public void requestPermissions(){
        ActivityCompat.requestPermissions(this, new
                String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO,Manifest.permission.ACCESS_COARSE_LOCATION},
                0);
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case 0:
                if (grantResults.length> 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean LocationPermission = grantResults[2] ==
                            PackageManager.PERMISSION_GRANTED;

                    //id recording permissions granted
                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(this, "Recording permission granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this,"Recording permission denied",Toast.LENGTH_LONG).show();
                    }
                    //id location permission granted
                    if (LocationPermission){
                        Toast.makeText(this, "Location permission granted",
                                Toast.LENGTH_LONG).show();
                    }else
                    {
                        Toast.makeText(this,"Location permission denied",Toast.LENGTH_LONG).show();

                    }
                }
                break;

            default:
                super.onRequestPermissionsResult(requestCode,permissions,grantResults);
                break;
        }
    }

    public boolean hasRecordingPermission() {
        int write = ContextCompat.checkSelfPermission(this,
                WRITE_EXTERNAL_STORAGE);
        int record = ContextCompat.checkSelfPermission(this,
                RECORD_AUDIO);

        int granted=PackageManager.PERMISSION_GRANTED;


        return write == granted&&
                record == granted;
    }

    public boolean hasLocationPermission(){

        int location=ContextCompat.checkSelfPermission(this,
                ACCESS_COARSE_LOCATION);

        int granted=PackageManager.PERMISSION_GRANTED;

        return location == granted;
    }

}