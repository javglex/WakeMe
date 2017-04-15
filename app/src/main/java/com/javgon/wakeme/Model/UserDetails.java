package com.javgon.wakeme.Model;

/**
 * Created by javgon on 4/13/2017.
 */

public class UserDetails {

    //this field contains the single instance every initialized.

    public String username;
    public String email;
    public String location;

    public UserDetails() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }


    public UserDetails(String username, String email,String location) {
        this.username = username;
        this.email = email;
        this.location=location;
    }




    public void setEmail(String email){
        this.email=email;
    }

    public void setUsername(String username){
        this.username=username;
    }

    public void setLocation(String location) { this.location=location;}

    public String getLocation() { return location; }

    public String getUsername(){
        return username;
    }

    public String getEmail(){
        return email;
    }

}
