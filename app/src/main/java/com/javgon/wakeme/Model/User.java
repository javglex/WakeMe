package com.javgon.wakeme.Model;

/**
 * Created by javgon on 4/13/2017.
 */

public class User {

    //this field contains the single instance every initialized.

    private String username;
    private String email;
    private LCoordinates lCoordinates;
    private String uid;
    private String alarmTime;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }


    public User(String username, String email, LCoordinates loc, String uid) {
        this.username = username;
        this.email = email;
        this.lCoordinates=loc;
        this.uid=uid;

    }

    public User(User user) {
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.lCoordinates=user.getLCoordinates();
        this.uid=user.getUid();
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
