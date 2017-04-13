package com.javgon.wakeme.Activities;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.javgon.wakeme.Fragments.AuthUserFragment;
import com.javgon.wakeme.R;


public class MainActivity extends BaseActivity {

    TextView tvWelcome;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CheckUser();


    }


    @Override
    protected void onResume(){
        super.onResume();
        CheckUser();

    }


    public void CheckUser(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            getSupportActionBar().show();

            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            String uid = user.getUid();
            tvWelcome=(TextView)findViewById(R.id.tv_welcome);
            tvWelcome.setText(name + " " + email + " " + uid);


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
