package com.javgon.wakeme.Services;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;


/**
 * Created by http://www.androidhive.info/2012/07/android-gps-location-manager-tutorial/
 * Modified by Javier Gonzalez 4/14/2017
 */

public final class LocationService extends Service implements LocationListener {
    public static LocationService locationService;
    private final Context mContext;
    boolean isGPSEnabled = false;     // flag for GPS status
    boolean isNetworkEnabled = false;     // flag for network status
    boolean isPassiveEnabled=false;
    boolean canGetLocation = false;
    Location location; // location
    double latitude; // latitude
    double longitude; // longitude
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;     // 10 meter difference required to update location
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;     //minimum time before updates
    Location shortLoc =  new Location("USER");

    // Declaring a Location Manager
    protected LocationManager locationManager;

    public LocationService(Context context) {
        this.mContext = context;
    }



    public static LocationService getInstance(Context context) {
        if (locationService == null) {
            locationService = new LocationService(context);
        }
        return locationService;
    }

    public void getLocation(final LocationService.LocationCallBack callback) {
        try {
            locationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);

            // getting GPS status
           /* isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);*/

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            isPassiveEnabled=locationManager
                    .isProviderEnabled(LocationManager.PASSIVE_PROVIDER);

            if (!isNetworkEnabled && !isPassiveEnabled) {       //removed isGPSenabled, app doesn't really require precise location
                // no network provider is enabled
                Log.d("GPS", "no available gps'");
                showSettingsAlert();

            } else
            {
                this.canGetLocation = true;
                // First get location from Network Provider

                if (isNetworkEnabled) {
                    networkProvider(callback);
                } else
                if (isPassiveEnabled) {
                    passiveProvider(callback);
                } //else
                /*if (isGPSEnabled) {
                    gpsProvider(callback);
                }*/

            }

        } catch (Exception e) {
            Log.e("GPS",e.getMessage().toString() );
            e.printStackTrace();
        }

    }



    public void passiveProvider(LocationCallBack callback){
        try {
            locationManager.requestLocationUpdates(
                    LocationManager.PASSIVE_PROVIDER,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
            Log.d("GPS", "Passive");
            if (locationManager != null) {
                location = locationManager
                        .getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                if (location != null) {
                    latitude = location.getLatitude();
                    shortLoc.setLatitude(latitude);
                    longitude = location.getLongitude();
                    shortLoc.setLongitude(longitude);
                    callback.onSuccess(shortLoc);
                }
            }
        }catch(SecurityException e){
            Log.e("GPS",e.getMessage().toString() );
            callback.onFail("Please enable a network");
        }
    }

    public void gpsProvider(LocationCallBack callback){
        if (location == null) {
            try {
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                Log.d("GPS", "GPS Enabled");
                if (locationManager != null) {
                    location = locationManager
                            .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location != null) {
                        latitude = location.getLatitude();
                        shortLoc.setLatitude(latitude);
                        longitude = location.getLongitude();
                        shortLoc.setLongitude(longitude);
                        callback.onSuccess(shortLoc);
                    }
                }
            } catch (SecurityException e){
                Log.e("GPS",e.getMessage().toString() );
                callback.onFail("Please grant GPS permission.");
                e.printStackTrace();
            }
        }
    }

    public void networkProvider(LocationCallBack callback){
        try {
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
            Log.d("GPS", "Network");
            if (locationManager != null) {
                location = locationManager
                        .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (location != null) {
                    latitude = location.getLatitude();
                    shortLoc.setLatitude(latitude);
                    longitude = location.getLongitude();
                    shortLoc.setLongitude(longitude);
                    callback.onSuccess(shortLoc);
                }
            }
        }catch(SecurityException e){
            Log.e("GPS",e.getMessage().toString() );
            callback.onFail("Please enable a network.");
        }
    }

    /**
     * Stop using GPS listener Calling this function will stop using GPS in your
     * app
     */
    public void stopUsingGPS() {
        if (locationManager != null) {
            locationManager.removeUpdates(LocationService.this);
        }
    }

    /**
     * Function to get latitude
     **/
    public double getLatitude(){
        if(location != null){
            latitude = location.getLatitude();
        }

        // return latitude
        return latitude;
    }

    /**
     * Function to get longitude
     * */
    public double getLongitude(){
        if(location != null){
            longitude = location.getLongitude();
        }

        // return longitude
        return longitude;
    }

    /**
     * Function to check GPS/wifi enabled
     *
     * @return boolean
     */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    /**
     * Function to show settings alert dialog On pressing Settings button will
     * lauch Settings Options
     */
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        // Setting Dialog Title
        alertDialog.setTitle("No location");

        // Setting Dialog Message
        alertDialog
                .setMessage("Please enable network or GPS");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        mContext.startActivity(intent);
                    }
                });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    public interface LocationCallBack{
        void onSuccess(Location location);
        void onFail(String msg);
    }

}