package com.javgon.wakeme.Activities;

import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.javgon.wakeme.Controller.PostServices;
import com.javgon.wakeme.Fragments.AuthUserFragment;
import com.javgon.wakeme.Other.LocationService;
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
        mLoc= new LocationService(this);
    }


    @Override
    protected void onResume(){
        super.onResume();
        CheckUser();

    }


    public void CheckUser(){
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        if (mUser != null) {
            getSupportActionBar().show();

            PostServices post = PostServices.newInstance();
            post.writeNewUser(mUser.getUid(),mUser.getDisplayName(), mUser.getEmail());
            post.writeUpdateUserLocation(mUser.getUid(),"Mexico tepic now lmaooo it lit");
            // Name, email address, and profile photo Url
            String name = mUser.getDisplayName();
            String email = mUser.getEmail();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            String uid = mUser.getUid();
            tvWelcome=(TextView)findViewById(R.id.tv_welcome);
            tvWelcome.setText(name + " " + email + " " + uid );
            mLoc= new LocationService(this);

            if(mLoc.canGetLocation()) { // gps enabled} // return boolean true/false
                tvWelcome.setText(name + " " + email + " " + uid + "\n Location: " +mLoc.getLatitude() + "," +mLoc.getLongitude());
            }else mLoc.showSettingsAlert();

        }else
            displayLogInPage();
    }


    /**
     Showing call log details when slot clicked
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


}
