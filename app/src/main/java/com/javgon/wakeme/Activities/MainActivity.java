package com.javgon.wakeme.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.javgon.wakeme.Services.DatabaseServices;
import com.javgon.wakeme.Fragments.AuthUserFragment;
import com.javgon.wakeme.Model.LCoordinates;
import com.javgon.wakeme.Model.User;
import com.javgon.wakeme.Services.LocationService;
import com.javgon.wakeme.Model.MyUserData;
import com.javgon.wakeme.R;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


public class MainActivity extends BaseActivity {

    public final int RequestRecordingPermissionCode = 2 ;
    public final int RequestLocationPermissionCode = 1 ;
    TextView tvWelcome;
    FirebaseUser mUser;
    LocationService mLocService;
    final DatabaseServices mPost = DatabaseServices.getInstance(this);
    final LCoordinates mLocation = new LCoordinates();
    MyUserData mUserData;
    String mFirstName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUserData = mUserData.getInstance(this);
        CheckUser();
    }


    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }

    public void CheckUser(){
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mUser != null) {
            grantPermissions();
        }else
            displayLogInPage();
    }


    /**
     * Sets user data in a signleton class for easier access from other classes
     */
    public void prepUserData(){
        // Name, email address, and profile photo Url
        String email = mUser.getEmail();
        String uid = mUser.getUid();
        String profilePic ="";
        String name="";
        if (mUser.getPhotoUrl()!=null)
             profilePic = mUser.getPhotoUrl().toString();
        if (mUser.getDisplayName()!=null)       //if user did not sign up with google
            name = mUser.getDisplayName();
        else name=mFirstName;           //get name provided by user in editbox



        mPost.writeUser(new User(name,email,mLocation, uid, profilePic));

        mUserData.setUserData(mLocation,uid,name);

        //set user location
        getUserLocation();
    }
    /**
     Showing log in details
     */
    private void displayLogInPage(){
        //inflate card page
        AuthUserFragment nextFrag= AuthUserFragment.newInstance();

        if (getSupportFragmentManager().findFragmentByTag("AUTHUSER")==null) { //if fragment does not exist
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_container, nextFrag, "AUTHUSER")
                    .addToBackStack(null)
                    .commit();
        }

    }

    /**
     * Essentially the main screen of the application.
     */
    private void displayMainNavPage(){

        Intent myIntent = new Intent(MainActivity.this, NavigationPage.class);
        MainActivity.this.startActivity(myIntent);

    }

   public void getUserLocation(){
       mLocService= new LocationService(this);
       LocationService.getInstance(this).getLocation(new LocationService.LocationCallBack(){
               @Override
               public void onSuccess(Location location){
                   mLocation.setLatitude(location.getLatitude());
                   mLocation.setLongitude(location.getLongitude());
                   mPost.writeUserLocation(mUser.getUid(),mLocation);
                   mUserData.setLocation(mLocation);
                   Log.d("gps", "gps: "+mLocation.toString());
                   mLocService.stopUsingGPS();
               }
               @Override
               public void onFail(String msg){
                   Log.e("FAIL", "getuserlocation() "+msg);
                   Toast.makeText(MainActivity.this, msg,
                           Toast.LENGTH_SHORT).show();
               }
           });

   }

    public void displayUserLocation(LCoordinates loc){
        String name = mUser.getDisplayName();
        String email = mUser.getEmail();
        tvWelcome.setText(name + " " + email + " "  + "\n Location: " + loc.toString());



    }

    public void setName(String name){
        mFirstName=name;
    }

    public void grantPermissions(){

        Log.d("MAIN", "grantpermission()");
        //if gps permission is not granted
        if (!checkPermission()) {
            Log.d("MAIN", "need permissiom");
            //Request permission
            requestPermissions();
        }else {
            Log.d("MAIN", "no need for permission");

            prepUserData();
            displayMainNavPage();
        }
    }
    private void requestPermissions() {
        requestRecordingPermission();
        requestLocationPermission();

    }

    private void requestRecordingPermission(){
        ActivityCompat.requestPermissions(MainActivity.this, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestRecordingPermissionCode);
    }

    private void requestLocationPermission(){
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{ACCESS_FINE_LOCATION}, RequestLocationPermissionCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case RequestLocationPermissionCode:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this,"GPS permission granted",Toast.LENGTH_LONG).show();

                    //  get Location from your device by some method or code
                    prepUserData();
                    displayMainNavPage();
                } else {
                    Toast.makeText(MainActivity.this,"Location permission denied",Toast.LENGTH_LONG).show();
                }
                break;
            case RequestRecordingPermissionCode:
                if (grantResults.length> 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(MainActivity.this, "Recording Permission granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MainActivity.this,"Recording permission denied",Toast.LENGTH_LONG).show();
                    }
                }
                break;

            default:
                super.onRequestPermissionsResult(requestCode,permissions,grantResults);
                break;
        }
    }

    public boolean checkPermission() {
        int write = ContextCompat.checkSelfPermission(MainActivity.this,
                WRITE_EXTERNAL_STORAGE);
        int record = ContextCompat.checkSelfPermission(MainActivity.this,
                RECORD_AUDIO);
        int location=ContextCompat.checkSelfPermission(MainActivity.this,
                ACCESS_FINE_LOCATION);

        int granted=PackageManager.PERMISSION_GRANTED;


        return write == granted&&
                record == granted && location==granted;
    }
}
