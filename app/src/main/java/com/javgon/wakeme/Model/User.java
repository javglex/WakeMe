package com.javgon.wakeme.Model;

import android.net.Uri;

import com.javgon.wakeme.R;

import java.net.URI;

/**
 * Created by javgon on 4/13/2017.
 */

public class User {

    private String username;
    private String email;
    private LCoordinates lCoordinates;
    private String uid;
    private String profilePic;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }


    public User(String username, String email, LCoordinates loc, String uid, String profilePic) {
        this.username = username;
        this.email = email;
        this.lCoordinates=loc;
        this.uid=uid;
       setProfilePic(profilePic);



    }

    public User(User user) {
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.lCoordinates=user.getLCoordinates();
        this.uid=user.getUid();
    }

    public String getProfilePic() {
            return profilePic;
    }

    public void setProfilePic(String profilePic) {

        if (profilePic==""){
            this.profilePic="https://firebasestorage.googleapis.com/v0/b/wakeme-91c98.appspot.com/o/profile_default.jpg?alt=media&token=bc26c03f-fe6a-471e-980d-a39e7e2a7ca2";
        }
        else
            this.profilePic = profilePic;
    }
    public void setUid(String uid){
        this.uid=uid;
    }
    public String getUid(){
        return uid;
    }
    public void setEmail(String email){
        this.email=email;
    }

    public void setUsername(String username){
        this.username=username;
    }

    public void setLCoordinates(LCoordinates loc) {
        this.lCoordinates=loc;
    }

    public LCoordinates getLCoordinates() {

        return this.lCoordinates;
    }

    public String getUsername(){
        return username;
    }

    public String getEmail(){
        return email;
    }

}
