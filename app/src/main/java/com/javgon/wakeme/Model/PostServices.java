package com.javgon.wakeme.Model;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.javgon.wakeme.Activities.MainActivity;
import com.javgon.wakeme.Other.MyUserData;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by javgon on 4/13/2017.
 */

public class PostServices {
    public static PostServices postServices;
    private static DatabaseReference mDatabase;
    private final String READTAG="READPOST";
    private LCoordinates  mLoc = new LCoordinates();
    private static Context mContext;
    private MyUserData mUserData;

    private PostServices(Context context) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        this.mContext = context;
        mUserData=mUserData.getInstance((Activity)context);
    }

    public static PostServices getInstance(Context context) {
        if (postServices == null) {
            postServices = new PostServices(context);
        }
        return postServices;
    }


    public void writeUser(User user) {
        mDatabase.child("users").child(user.getUid()).setValue(user);
    }


    public void writeUserLocation(String userId, LCoordinates location) {

        mDatabase.child("users").child(userId).child("lcoordinates").setValue(location);

    }

    /**
     * writes alarm to database
     * @param alarm takes in alarm object
     */
    public void writeAlarm(Alarm alarm){
        try{
            String uid=mUserData.getUserID();
            mDatabase.child("alarms").child(uid+alarm.getAlarmID()).setValue(alarm);
        } catch ( Exception e){
            Log.e("writeAlarm", e.getMessage().toString());
        }
    }

    public void deleteAlarm(int alarmId){

        try{
            String uid=mUserData.getUserID();
            mDatabase.child("alarms").child(uid+alarmId).removeValue();
            for (int i=0; i<mUserData.getAlarmSize(); i++)   //fix alarm list so that everything is in order again (no holes)
            {
                Alarm alarm=mUserData.getAlarm(i);
                writeAlarm(alarm);
            }
        } catch ( Exception e){

            Log.e("deleteAlarm", e.getMessage().toString());

        }
    }

    /**
     *
     */
    public void readOwnAlarms( final AlarmCallback callback ) {
        DatabaseReference ref = mDatabase.child("alarms");
        String uid =mUserData.getUserID();
        //get alarms that belong to user
        Log.d(READTAG,"got in function, uid: "+mUserData.getUserID());

        ref.orderByKey().startAt(uid)
                .endAt(uid+"\uf8ff").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                try {
                    Log.d(READTAG, "got something " +snapshot.getKey() +" "+ snapshot.getChildrenCount());

                    ArrayList<Alarm> alarms = new ArrayList<>();
                    for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                        Alarm alarm = new Alarm();
                        alarm.setUserID(postSnapshot.child("userID").getValue(String.class));
                        alarm.setAlarmID(postSnapshot.child("alarmID").getValue(int.class));
                        alarm.setAlarmTimeHours(postSnapshot.child("alarmTimeHours").getValue(int.class));
                        alarm.setAlarmTimeMinutes(postSnapshot.child("alarmTimeMinutes").getValue(int.class));
                        alarm.setHoursUntilAlarm(postSnapshot.child("hoursUntilAlarm").getValue(int.class));
                        GenericTypeIndicator<ArrayList<Integer>> genericTypeIndicator =new GenericTypeIndicator<ArrayList<Integer>>(){};
                        alarm.setRepeatDays(postSnapshot.child("repeatDays").getValue(genericTypeIndicator));
                        alarms.add(alarm);
                        Log.d(READTAG, " in readownlarms: "+alarm.getUserID());
                    }
                    callback.onSuccess(alarms);

                } catch ( Exception e){
                    Log.e(READTAG, e.toString());
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



    /**
     * Read other user's alarms that are within an hour of being activated
     */
    public void readAlarms(final AlarmCallback callback){
        DatabaseReference ref = mDatabase.child("alarms");
        //get alarms that are about to ring, limit to first 3
        ref.orderByChild("hoursUntilAlarm").startAt(0).limitToFirst(50).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                try {
                    ArrayList<Alarm> alarms = new ArrayList<>();
                    for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                        Alarm alarm = new Alarm();
                        alarm.setUserID(postSnapshot.child("userID").getValue(String.class));
                        Log.d(READTAG," in readother users alarms : "+alarm.getUserID());
                        alarm.setAlarmID(postSnapshot.child("alarmID").getValue(int.class));
                        Log.d(READTAG," in readother users alarms : "+alarm.getAlarmID());
                        alarm.setAlarmTimeHours(postSnapshot.child("alarmTimeHours").getValue(int.class));
                        Log.d(READTAG," in readother users alarms : "+alarm.getAlarmID());
                        alarm.setAlarmTimeMinutes(postSnapshot.child("alarmTimeMinutes").getValue(int.class));
                        Log.d(READTAG," in readother users alarms : "+alarm.getAlarmTimeMinutes());
                        alarm.setHoursUntilAlarm(postSnapshot.child("hoursUntilAlarm").getValue(int.class));
                        Log.d(READTAG," in readother users alarms : "+alarm.getHoursUntilAlarm());
                        GenericTypeIndicator<ArrayList<Integer>> genericTypeIndicator =new GenericTypeIndicator<ArrayList<Integer>>(){};
                        alarm.setRepeatDays(postSnapshot.child("repeatDays").getValue(genericTypeIndicator));
                        Log.d(READTAG," in readother users alarms : "+alarm.getRepeatDays());

                        alarms.add(alarm);
                    }
                    callback.onSuccess(alarms);

                } catch ( Exception e){
                    Log.e(READTAG, "read alarms "+e.toString());
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
                        Log.d(READTAG, String.valueOf(snapshot.getValue()));

                        User user = new User();
                        user.setEmail(snapshot.child("email").getValue(String.class));
                        user.setLCoordinates(snapshot.child("lcoordinates").getValue(LCoordinates.class));
                        user.setUsername(snapshot.child("username").getValue(String.class));
                        Log.d(READTAG, user.getLCoordinates().toString());
                        //Adding it to a string

                        mLoc.setLongitude(user.getLCoordinates().getLongitude());
                        mLoc.setLatitude(user.getLCoordinates().getLatitude());
                        Log.d(READTAG,mLoc.toString());
                        callback.onSuccess(mLoc);

                    } catch ( Exception e){
                        Log.e(READTAG, e.toString());

                    }

                }

                @Override
                public void onCancelled(DatabaseError firebaseError) {
                    Log.e("The read failed: ", firebaseError.getMessage());
                    callback.onFail(firebaseError.getMessage());
                }

            });
        Log.d(READTAG, mLoc.toString());

        return;

    }

    public void getUserLocationFromAlarm(ArrayList<Alarm> alarms, final AlarmLocationCallBack callback){

        final ArrayList<LCoordinates> locations = new ArrayList<>();
        DatabaseReference ref = mDatabase.child("users");
        boolean success=false;

        for (Alarm alarm: alarms){
            Log.d(READTAG, "in get alarm location :  "+ alarm.getUserID().toString());
            ref.child(alarm.getUserID()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    try {
                        LCoordinates loc = new LCoordinates();
                        loc.setLocation(snapshot.child("lcoordinates").getValue(LCoordinates.class));
                        Log.d(READTAG, loc.toString());
                        locations.add(loc);
                        callback.onSuccess(locations);
                    } catch ( Exception e){
                        Log.e(READTAG, e.toString());
                    }
                }
                @Override
                public void onCancelled(DatabaseError firebaseError) {
                    Log.e("The read failed: ", firebaseError.getMessage());
                    callback.onFail(firebaseError.getMessage());
                }

            });
        }


        return;
    }

    public void getUserInfoFromAlarm(ArrayList<Alarm> alarms, final UsersCallBack callback){
        final ArrayList<UserSlot> users = new ArrayList<>();
        DatabaseReference ref = mDatabase.child("users");
        Log.d(READTAG, "in get user info :  "+ alarms.size());

        for (final Alarm alarm: alarms){
            Log.d(READTAG, "in for loo[p :  "+ alarm.getUserID().toString());
            ref.child(alarm.getUserID()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    try {
                        UserSlot user = new UserSlot();
                        user.setEmail(snapshot.child("email").getValue(String.class));
                        user.setlCoordinates(snapshot.child("lcoordinates").getValue(LCoordinates.class));
                        user.setUsername(snapshot.child("username").getValue(String.class));
                        user.setuID(snapshot.child("uid").getValue(String.class));
                        user.setHoursUntilAlarm(alarm.getHoursUntilAlarm());
                        user.setProfilePic(snapshot.child("profilePic").getValue(String.class));
                        Log.d(READTAG, user.getlCoordinates().toString());

                        users.add(user);
                        callback.onSuccess(users);

                    } catch ( Exception e){
                        Log.e(READTAG, e.toString());
                    }
                }
                @Override
                public void onCancelled(DatabaseError firebaseError) {
                    Log.e("The read failed: ", firebaseError.getMessage());
                    callback.onFail(firebaseError.getMessage());
                }

            });
        }
    }


    public interface LocCallback{
        void onSuccess(LCoordinates loc);
        void onFail(String msg);
    }

    public interface AlarmCallback{
        void onSuccess(ArrayList<Alarm> alarms);
        void onFail(String msg);
    }

    public interface AlarmLocationCallBack{
        void onSuccess(ArrayList<LCoordinates> alarmLocations);
        void onFail(String msg);
    }

    public interface UsersCallBack{
        void onSuccess(ArrayList<UserSlot> users);
        void onFail(String msg);
    }


}
