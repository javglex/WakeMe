package com.javgon.wakeme.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.javgon.wakeme.Model.PostServices;
import com.javgon.wakeme.Fragments.AuthUserFragment;
import com.javgon.wakeme.Model.LCoordinates;
import com.javgon.wakeme.Other.DummyData;
import com.javgon.wakeme.Other.LocationService;
import com.javgon.wakeme.Other.MyUserData;
import com.javgon.wakeme.R;



public class MainActivity extends BaseActivity {

    TextView tvWelcome;
    FirebaseUser mUser;
    LocationService mLoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CheckUser();
    }


    @Override
    protected void onResume(){
        super.onResume();
    }


    public void CheckUser(){
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mUser != null) {
            prepUserData();
            displayMainNavPage();
        }else
            displayLogInPage();
    }


    /**
     * Sets user data in a signleton class for easier access from other classes
     */
    public void prepUserData(){
        PostServices post = PostServices.getInstance(this);
        // Name, email address, and profile photo Url
        String name = mUser.getDisplayName();
        String email = mUser.getEmail();
        String uid = mUser.getUid();
        post.writeUser(mUser.getUid(),mUser.getDisplayName(), mUser.getEmail());

        //set user location
        mLoc= new LocationService(this);
        LCoordinates location = new LCoordinates();
        if(mLoc.canGetLocation()) { // gps enabled} // return boolean true/false
            location.setLatitude(mLoc.getLatitude());
            location.setLongitude(mLoc.getLongitude());
            post.writeUserLocation(mUser.getUid(),location);
        }else
            mLoc.showSettingsAlert();

        MyUserData myData = MyUserData.getInstance();
        myData.setUserData(location,uid,name);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyUserPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("username", name);
        editor.putString("uid", uid);
    }
    /**
     Showing log in details
     */
    private void displayLogInPage(){
        //inflate card page
        AuthUserFragment nextFrag= AuthUserFragment.newInstance();

        if (getFragmentManager().findFragmentByTag("AUTHUSER")==null) { //if fragment does not exist
            getFragmentManager().beginTransaction()
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


   /* public void readUserLoc(){
        post.readUserLocation(uid,new PostServices.LocCallback(){
            @Override
            public void onSuccess(LCoordinates loc){
                displayUserLocation(loc); //then display user location
            }
            @Override
            public void onFail(String msg){
                Log.e("FAIL", msg);
                Toast.makeText(MainActivity.this, msg,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }*/

    public void displayUserLocation(LCoordinates loc){
        String name = mUser.getDisplayName();
        String email = mUser.getEmail();
        tvWelcome.setText(name + " " + email + " "  + "\n Location: " + loc.toString());



    }


}
