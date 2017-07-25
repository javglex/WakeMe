package com.javgon.wakeme.Activities;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.javgon.wakeme.Services.DatabaseServices;
import com.javgon.wakeme.Model.LCoordinates;
import com.javgon.wakeme.Model.User;
import com.javgon.wakeme.Services.LocationService;
import com.javgon.wakeme.Model.MyUserData;
import com.javgon.wakeme.R;


public class LoginActivity extends BaseActivity  implements View.OnClickListener,  GoogleApiClient.OnConnectionFailedListener{

    TextView tvWelcome;
    FirebaseUser mUser;
    LocationService mLocService;
    final DatabaseServices mPost = DatabaseServices.getInstance(this);
    final LCoordinates mLocation = new LCoordinates();
    MyUserData mUserData;
    private static final String TAG = "AuthState";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;
    private Button btnSignIn,btnCreateAccnt, btnNewUser,btnExistingAccnt;
    private SignInButton googleSignInButton;
    private EditText etEmail,etPassword, etFirstName;
    private TextView tvFirstName;
    private ImageView imgLogo;
    private String mEmail,mPassword,mFirstName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frag_auth_user);


        btnSignIn=(Button) findViewById(R.id.btn_sign_in);
        btnNewUser=(Button) findViewById(R.id.btn_new_user);
        btnExistingAccnt=(Button)findViewById(R.id.btn_existing_account);
        btnCreateAccnt=(Button) findViewById(R.id.btn_create_account);
        etEmail=(EditText)findViewById(R.id.et_email);
        etFirstName=(EditText) findViewById(R.id.et_first_name);
        etPassword=(EditText)findViewById(R.id.et_password);
        etFirstName.setVisibility(View.GONE);
        tvFirstName=(TextView) findViewById(R.id.tv_first_name);
        tvFirstName.setVisibility(View.GONE);
        imgLogo=(ImageView) findViewById(R.id.img_logo);
        googleSignInButton = (SignInButton)findViewById(R.id.google_sign_in_button);
        // Set the dimensions of the sign-in button.
        googleSignInButton.setSize(SignInButton.SIZE_STANDARD);

        btnSignIn.setOnClickListener(this);
        btnCreateAccnt.setOnClickListener(this);
        btnNewUser.setOnClickListener(this);
        btnExistingAccnt.setOnClickListener(this);
        googleSignInButton.setOnClickListener(this);

        authCreateInstances();

        mUserData = mUserData.getInstance(this);
        CheckUser();

    }


    public void CheckUser(){
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mUser != null) {
            prepUserData();
            launchMainNavPage();
        }
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

        User user =new User(name,email,mLocation, uid, profilePic);
        mPost.writeUser(user);      //save to db
        mUserData.setUserData(user);        //store locally

        //set user location
        getUserLocation();
    }


    /**
     * Essentially the main screen of the application.
     */
    private void launchMainNavPage(){

        Intent myIntent = new Intent(LoginActivity.this, NavigationPage.class);
        LoginActivity.this.startActivity(myIntent);

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
                   Toast.makeText(LoginActivity.this, msg,
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



    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    public void onBackPressed(){
        exitDialog();
    }

    @Override
    public void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop(){
        super.onStop();
        if (mAuthListener!=null){  //if listener exists
            mAuth.removeAuthStateListener(mAuthListener);
        }
        mGoogleApiClient.disconnect();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_new_user:
                btnCreateAccnt.setVisibility(View.VISIBLE);
                btnExistingAccnt.setVisibility(View.VISIBLE);
                btnSignIn.setVisibility(View.INVISIBLE);
                btnNewUser.setVisibility(View.INVISIBLE);
                etFirstName.setVisibility(View.VISIBLE);
                tvFirstName.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_sign_in:
                authSignIn();
                break;
            case R.id.btn_create_account:
                authCreateUser();
                break;
            case R.id.btn_existing_account:
                btnCreateAccnt.setVisibility(View.INVISIBLE);
                btnExistingAccnt.setVisibility(View.INVISIBLE);
                btnSignIn.setVisibility(View.VISIBLE);
                btnNewUser.setVisibility(View.VISIBLE);
                etFirstName.setVisibility(View.GONE);
                tvFirstName.setVisibility(View.GONE);
                break;
            case R.id.google_sign_in_button:
                AuthSignInGoogle();
            default:
                break;
        }
    }


    public void authCreateInstances(){

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                //.enableAutoManage(this/* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        //firenbase
        mAuth=FirebaseAuth.getInstance();
        mAuthListener= new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth){
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user!=null){
                    //user is signed in
                    Log.d(TAG,"onAuthStateChanged:signed_in: " + user.getUid());
                }
                else
                {
                    //user is signed out
                    Log.d(TAG,"onAuthStateChanged:signed_out");
                }
            }
        };


    }

    public void authSignIn(){

        mEmail=etEmail.getText().toString();
        mPassword=etPassword.getText().toString();
        try {
            mAuth.signInWithEmailAndPassword(mEmail, mPassword)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                            CheckUser();

                            if (!task.isSuccessful()) {
                                Log.w(TAG, "signInWithEmail:failed", task.getException());
                                Toast.makeText(getApplicationContext(), task.getException().getMessage().toString(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } catch (IllegalArgumentException e){
            Toast.makeText(this,"Fields cannot be empty",
                    Toast.LENGTH_SHORT).show();
        }

    }



    public void authSignOut(){
        mAuth.signOut();
        // Google sign out
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        //  updateUI(null);
                    }
                });
    }

    public void authCreateUser(){

        mEmail=etEmail.getText().toString();
        mPassword=etPassword.getText().toString();
        mFirstName=etFirstName.getText().toString();
        try {
            mAuth.createUserWithEmailAndPassword(mEmail, mPassword)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());


                            if (!task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), task.getException().getMessage().toString(),
                                        Toast.LENGTH_SHORT).show();
                            }else {
                                FirebaseUser user = mAuth.getCurrentUser();
                                Log.d("FIREBASE", user.getEmail() + " " + mFirstName);
                                if (user != null) {
                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(mFirstName).build();
                                    user.updateProfile(profileUpdates);
                                }
                                CheckUser();


                            }

                        }
                    });
        } catch (IllegalArgumentException e){
            Toast.makeText(this, "Fields cannot be empty",
                    Toast.LENGTH_SHORT).show();
        }
    }




    /**
     *
     ***********************SIGN IN WITH GOOGLE METHODS*************************
     *
     */

    /**
     * Sign in with google
     */
    public void AuthSignInGoogle(){
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // [START_EXCLUDE]

                // [END_EXCLUDE]
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        showProgressDialog();
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        //if sign in succesfull
                        CheckUser();
                        System.out.println(" " + task.getResult().getUser().getDisplayName());
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // [START_EXCLUDE]
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

}
