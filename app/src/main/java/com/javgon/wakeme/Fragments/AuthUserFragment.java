package com.javgon.wakeme.Fragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.javgon.wakeme.Activities.MainActivity;
import com.javgon.wakeme.R;

import java.util.concurrent.Executor;


/**
 * Created by javgon on 4/11/2017.
 */

public class AuthUserFragment extends BaseFragment implements View.OnClickListener,  GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "AuthState";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;
    private Button btnSignIn,btnCreateAccnt, btnNewUser,btnExistingAccnt;
    private SignInButton googleSignInButton;
    private EditText etEmail,etPassword;
    private ImageView imgLogo;
    private String mEmail;
    private String mPassword;

    public static AuthUserFragment newInstance(){

        AuthUserFragment newFrag = new AuthUserFragment();
        Bundle args = new Bundle();
        newFrag.setArguments(args);
        return newFrag;
    }


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.frag_auth_user, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        btnSignIn=(Button) rootView.findViewById(R.id.btn_sign_in);
        btnNewUser=(Button) rootView.findViewById(R.id.btn_new_user);
        btnExistingAccnt=(Button)rootView.findViewById(R.id.btn_existing_account);
        btnCreateAccnt=(Button) rootView.findViewById(R.id.btn_create_account);
        etEmail=(EditText)rootView.findViewById(R.id.et_email);
        etPassword=(EditText) rootView.findViewById(R.id.et_password);
        imgLogo=(ImageView) rootView.findViewById(R.id.img_logo);
        googleSignInButton = (SignInButton)rootView.findViewById(R.id.google_sign_in_button);
        // Set the dimensions of the sign-in button.
        googleSignInButton.setSize(SignInButton.SIZE_STANDARD);

        btnSignIn.setOnClickListener(this);
        btnCreateAccnt.setOnClickListener(this);
        btnNewUser.setOnClickListener(this);
        btnExistingAccnt.setOnClickListener(this);
        googleSignInButton.setOnClickListener(this);

        authCreateInstances();

        return rootView;
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
        ((MainActivity) getActivity()).CheckUser();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_new_user:
                btnCreateAccnt.setVisibility(View.VISIBLE);
                btnExistingAccnt.setVisibility(View.VISIBLE);
                btnSignIn.setVisibility(View.INVISIBLE);
                btnNewUser.setVisibility(View.INVISIBLE);
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

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                //.enableAutoManage(this/* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

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
        mPassword=etEmail.getText().toString();
        try {
            mAuth.signInWithEmailAndPassword(mEmail, mPassword)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                            // If sign in succesfull
                            closeFragment();
                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                Log.w(TAG, "signInWithEmail:failed", task.getException());
                                Toast.makeText(getActivity(), task.getException().getMessage().toString(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } catch (IllegalArgumentException e){
            Toast.makeText(getActivity(),"Fields cannot be empty",
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
        mPassword=etEmail.getText().toString();
        try {
            mAuth.createUserWithEmailAndPassword(mEmail, mPassword)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                            // If sign in succesfull
                            closeFragment();

                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                Toast.makeText(getActivity(), task.getException().getMessage().toString(),
                                        Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
        } catch (IllegalArgumentException e){
            Toast.makeText(getActivity(), "Fields cannot be empty",
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
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        //if sign in succesfull
                        closeFragment();
                        System.out.println(" " + task.getResult().getUser().getDisplayName());
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(getActivity(), "Authentication failed.",
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
        Toast.makeText(getActivity(), "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }



}
