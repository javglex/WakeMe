package com.javgon.wakeme.Controller;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.javgon.wakeme.Model.UserDetails;

/**
 * Created by javgon on 4/13/2017.
 */

public class PostServices {

    private static DatabaseReference mDatabase;

    private PostServices() {
    }

    public static PostServices newInstance(){
        PostServices post = new PostServices();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        return post;
    }

    public void writeNewUser(String userId, String name, String email) {
        UserDetails user = new UserDetails(name, email, "");

            mDatabase.child("users").child(userId).setValue(user);
    }

    public void writeNewUser(String userId, String name, String email, String location) {
        UserDetails user = new UserDetails(name, email, location);
        mDatabase.child("users").child(userId).setValue(user);
    }

    public void writeUpdateUser(String userId, String name, String email) {
        // updating the user via child nodes
        mDatabase.child("users").child(userId).child("name").setValue(name);

        mDatabase.child("users").child(userId).child("email").setValue(email);
    }

    public void writeUpdateUser(String userId, String name, String email, String location) {

        // updating the user via child nodes
        mDatabase.child("users").child(userId).child("name").setValue(name);
        mDatabase.child("users").child(userId).child("email").setValue(email);
        mDatabase.child("users").child(userId).child("location").setValue(location);

    }

    public void writeUpdateUserLocation(String userId, String location) {

        // updating the user via child nodes
        mDatabase.child("users").child(userId).child("location").setValue(location);

    }

}
