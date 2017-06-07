package com.javgon.wakeme.Model;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.Serializable;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

/**
 * Created by javier gonzalez on 5/2/2017.
 * Stores main user data such as id, name, location, etc
 * Will be using bootleg observer patters to keep in sync with disk and
 * prevent OS from collecting static instance and wiping it's date
 */

public class MyUserData implements Serializable {
    // Constant with a file name
    public final String FILENAME = "UserData1.ser";
    private static MyUserData myUserData;
    private Activity sActivity;

    private LCoordinates myLocation;
    ArrayList<Alarm> alarms=new ArrayList<>();  //list of own alarms
    ArrayList<Alarm> othersAlarm = new ArrayList<>();       //list of other user's alarms
    private String myName;
    private String myId;

    public static MyUserData getInstance(Activity activity){

        if (myUserData==null){
            myUserData=new MyUserData(activity);
        }

        return myUserData;
    }

    private MyUserData(Activity activity){
        sActivity=activity;
        loadFromPrefs();

    }


    /**************** MODIFIERS ***********************/

    public void setUserData(LCoordinates loc, String id, String name){
        this.myLocation=loc;
        this.myId=id;
        this.myName=name;
        saveToPrefs();
    }

    public void setLocation(LCoordinates loc){
        this.myLocation=loc;
        saveToPrefs();
    }

    public void setAlarmList(ArrayList<Alarm> alarms){
        this.alarms=alarms;
        Log.d("userprefs", "alarmsbeforesave: \n"+alarms.toString());
        saveToPrefs();
    }

    public void setAlarm(Alarm alarm, int alarmId){
        //edit alarm
        for (int i=0; i<getAlarmSize(); i++){
            if (alarms.get(i).getAlarmID()==alarmId){
                alarms.set(i,alarm);

            }
        }
        saveToPrefs();
    }
    public void setOthersAlarmList(ArrayList<Alarm> alarms){
        this.othersAlarm=alarms;
        Log.d("userprefs", "alarmsbeforesave: \n"+othersAlarm.toString());

        saveToPrefs();
    }

    public void addAlarm(Alarm alarm){
        alarms.add(alarm);
        Collections.sort(alarms, new Comparator<Alarm>() {

            public int compare(Alarm o1, Alarm o2) {
                if (o1.getAlarmID()>o2.getAlarmID())
                    return 1;
                else if (o1.getAlarmID()<o2.getAlarmID())
                    return -1;
                else return 0;
            }
        });
        saveToPrefs();
    }
    public void deleteAlarm(int alarmID){
        //remove alarm
        for(Iterator<Alarm> iterator = alarms.iterator(); iterator.hasNext(); ) {
            if(iterator.next().getAlarmID()==alarmID)
                iterator.remove();
        }
        saveToPrefs();
    }

    /*********************GETTERS**********/

    public ArrayList<Alarm> getOthersAlarm(){
        return this.othersAlarm;}

    public String getUserName(){
        return this.myName;
    }

    public LCoordinates getUserLocation(){
        return this.myLocation;
    }

    public ArrayList<Alarm> getAlarmList() {
        return this.alarms;
    }

    public Alarm getAlarm(int i){
        return alarms.get(i);
    }


    public String getUserID(){
        Log.d("USERPREFS", "loadFromPrefs my id: "+myId);

        return this.myId;
    }

    public int getAvailableID(){    //in case a user deleted an alarm in between the list, return available free spot
        for (int i=0; i<getAlarmSize(); i++){
            int id=alarms.get(i).getAlarmID();
            if (id!=i){
                return i;
            }
        }

        return getAlarmSize();
    }

    //returns number of alarms
    public int getAlarmSize(){
        loadFromPrefs();
        return alarms.size();
    }


    @Override
    public String toString(){

        String toString;
        toString="User ID: "+ getUserID();
        toString+="\nUser Location: "+ getUserLocation();
        toString+="\nUser Name: "+ getUserName();
        toString+="\nAlarm List size: "+getAlarmSize();
        toString+="\nAlarms: ";

        for (int i=0; i<getAlarmSize();i++){
            toString+="\n"+alarms.get(i).toString();
        }

        return toString;
    }


    public void saveToPrefs(){

        SharedPreferences sharedpreferences = sActivity.getSharedPreferences("MYUSERDATA", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(myLocation);
        editor.putString("location", json);
        editor.putString("name",myName);
        editor.putString("id",myId);
        json = gson.toJson(alarms);
        editor.putString("myAlarms",json);
        Log.d("USERPREFS","saved alarms \n"+ "other alarms \n"+othersAlarm.toString());
        json = gson.toJson(othersAlarm);
        editor.putString("otherAlarms",json);
        editor.commit();
        editor.apply();


    }

    public void loadFromPrefs(){

        try {

            SharedPreferences sharedpreferences = sActivity.getSharedPreferences("MYUSERDATA", Context.MODE_PRIVATE);

            Gson gson = new Gson();
            JsonReader json = new JsonReader(new StringReader(sharedpreferences.getString("location", "")));
            json.setLenient(true);
            myLocation = gson.fromJson(json, LCoordinates.class);
            json = new JsonReader(new StringReader(sharedpreferences.getString("name", "")));
            json.setLenient(true);

            myName=gson.fromJson(json, String.class);
            json = new JsonReader(new StringReader(sharedpreferences.getString("id", "")));
            json.setLenient(true);

            myId=gson.fromJson(json, String.class);
            json = new JsonReader(new StringReader(sharedpreferences.getString("myAlarms", "")));
            json.setLenient(true);

            Type type = new TypeToken< ArrayList < Alarm >>() {}.getType();
            alarms=gson.fromJson(json, type);
            json = new JsonReader(new StringReader(sharedpreferences.getString("otherAlarms", "")));
            json.setLenient(true);

            othersAlarm=gson.fromJson(json, type);

        }catch (NullPointerException e){
            e.printStackTrace();

        }


    }

}
