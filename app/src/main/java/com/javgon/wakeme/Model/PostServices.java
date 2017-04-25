package com.javgon.wakeme.Model;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.javgon.wakeme.Activities.MainActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by javgon on 4/13/2017.
 */

public class PostServices {
    public static PostServices postServices;
    private static DatabaseReference mDatabase;
    private LCoordinates  mLoc = new LCoordinates();
    private static Context mContext;


    private PostServices(Context context) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        this.mContext = context;

    }

    public static PostServices getInstance(Context context) {
        if (postServices == null) {
            postServices = new PostServices(context);
        }
        return postServices;
    }


    public  void writeUser(String userId, String name, String email) {
        LCoordinates loc = new LCoordinates();
        User user = new User(name, email, loc,userId);
        try {
            mDatabase.child("users").child(userId).setValue(user);
        } catch ( Exception e){
            Log.e("writenewuser", e.getMessage().toString());
        }
    }

    public void writeUser(String userId, String name, String email, String location) {
        User user = new User(name, email, new LCoordinates(),userId);
        mDatabase.child("users").child(userId).setValue(user);
    }

    public void writeUser(User user) {
        mDatabase.child("users").child(user.getUid()).setValue(user);
    }


    public void writeUserLocation(String userId, LCoordinates location) {

        mDatabase.child("users").child(userId).child("lcoordinates").setValue(location);

    }

    public void writeAlarm(Alarm alarm){
        try{
            mDatabase.child("alarms").child(alarm.getAlarmID()).setValue(alarm);
        } catch ( Exception e){
            Log.e("writeAlarm", e.getMessage().toString());
        }
    }

    public void readAlarms(final AlarmCallback callback){
        DatabaseReference ref = mDatabase.child("alarms");
        //get alarms that are about to ring, limit to first 3
        ref.orderByChild("hoursUntilAlarm").startAt(0).limitToFirst(3).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                try {
                    ArrayList<Alarm> alarms = new ArrayList<>();
                    for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                        Alarm alarm = new Alarm();
                        alarm.setAlarmID(postSnapshot.child("alarmID").getValue(String.class));
                        alarm.setAlarmTimeHours(postSnapshot.child("alarmTimeHours").getValue(int.class));
                        alarm.setAlarmTimeMinutes(postSnapshot.child("alarmTimeMinutes").getValue(int.class));
                        alarm.setHoursUntilAlarm(postSnapshot.child("hoursUntilAlarm").getValue(int.class));
                        alarms.add(alarm);
                    }
                    callback.onSuccess(alarms);

                } catch ( Exception e){
                    Log.e("readuserloc", e.toString());
                    callback.onFail(e.getMessage());
                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                callback.onFail(firebaseError.getMessage());
            }

        });
        return;
    }

    public void readUserLocation(final String userId, final LocCallback callback){

        DatabaseReference ref = mDatabase.child("users").child(userId);

        //Value event listener for realtime data update
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    try {
                        //for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                        //Getting the data from snapshot
                        Log.d("DB test loc2", String.valueOf(snapshot.getValue()));

                        User user = new User();
                        user.setEmail(snapshot.child("email").getValue(String.class));
                        user.setLCoordinates(snapshot.child("lcoordinates").getValue(LCoordinates.class));
                        user.setUsername(snapshot.child("username").getValue(String.class));
                        Log.d("DB test loc", user.getLCoordinates().toString());
                        //Adding it to a string

                        mLoc.setLongitude(user.getLCoordinates().getLongitude());
                        mLoc.setLatitude(user.getLCoordinates().getLatitude());
                        Log.d("DB test loc3",mLoc.toString());
                        callback.onSuccess(mLoc);

                    } catch ( Exception e){
                        Log.e("readuserloc", e.toString());

                    }

                }

                @Override
                public void onCancelled(DatabaseError firebaseError) {
                    Log.e("The read failed: ", firebaseError.getMessage());
                    callback.onFail(firebaseError.getMessage());
                }

            });
        Log.d("DB test loc4", mLoc.toString());

        return;

    }


    public interface LocCallback{
        void onSuccess(LCoordinates loc);
        void onFail(String msg);
    }

    public interface AlarmCallback{
        void onSuccess(ArrayList<Alarm> alarms);
        void onFail(String msg);

    }


}
