package com.javgon.wakeme.Other;

import com.javgon.wakeme.Model.Alarm;
import com.javgon.wakeme.Model.LCoordinates;

import java.util.ArrayList;

/**
 * Created by javier gonzalez on 5/2/2017.
 * Stores main user data such as id, name, location, etc
 */

public class MyUserData {

    private static MyUserData myUserData;
    private LCoordinates myLocation;
    ArrayList<Alarm> alarms=new ArrayList<>();

    private String myName;
    private String myId;

    private MyUserData(){
    }

    public static MyUserData getInstance(){
        if (myUserData==null){
            myUserData = new MyUserData();
        }
        return myUserData;
    }

    public void setUserData(LCoordinates loc, String id, String name){
        this.myLocation=loc;
        this.myId=id;
        this.myName=name;
    }

    public String getUserName(){
        return this.myName;
    }

    public LCoordinates getUserLocation(){
        return this.myLocation;
    }

    public ArrayList<Alarm> getAlarmList(){
        return this.alarms;
    }

    public void setAlarmList(ArrayList<Alarm> alarms){
        this.alarms=alarms;
    }

    public void setAlarm(Alarm alarm, int i){
        alarms.set(i,alarm);
    }
    public Alarm getAlarm(int i){
        return alarms.get(i);
    }
    public void addAlarm(Alarm alarm){
        alarms.add(alarm);
    }
    public void deleteAlarm(int alarmNum){
        alarms.remove(alarmNum);
    }
    public void deleteAlarm(Alarm alarm){
        alarms.remove(alarm);
    }
    public String getUserID(){
        return this.myId;
    }

    //returns number of alarms
    public int getAlarmSize(){
        return alarms.size();
    }




}
