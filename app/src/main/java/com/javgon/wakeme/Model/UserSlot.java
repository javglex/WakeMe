package com.javgon.wakeme.Model;

import android.net.Uri;

import com.javgon.wakeme.R;

import java.net.URI;

/**
 * Created by javier gonzalez on 5/22/2017.
 * For displaying combination of user + alarm information on UserListFragment
 */

public class UserSlot {

    private String username;
    private String uID;
    private String email;
    private LCoordinates lCoordinates;
    String profilePic;
    int hoursUntilAlarm;    //time until alarm rings

    public UserSlot(){

    }

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LCoordinates getlCoordinates() {
        return lCoordinates;
    }

    public void setlCoordinates(LCoordinates lCoordinates) {
        this.lCoordinates = lCoordinates;
    }

    public Uri getProfilePic()
    {
        Uri result = Uri.parse(profilePic);
        return result;

    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public int getHoursUntilAlarm() {
        return hoursUntilAlarm;
    }

    public void setHoursUntilAlarm(int hoursUntilAlarm) {
        this.hoursUntilAlarm = hoursUntilAlarm;
    }




}
