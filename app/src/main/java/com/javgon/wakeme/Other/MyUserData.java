package com.javgon.wakeme.Other;

import com.javgon.wakeme.Model.Alarm;
import com.javgon.wakeme.Model.LCoordinates;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

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

    public void setAlarm(Alarm alarm, int alarmId){
        //edit alarm
        for (int i=0; i<getAlarmSize(); i++){
            if (alarms.get(i).getAlarmID()==alarmId){
                alarms.set(i,alarm);

            }
        }

    }
    public Alarm getAlarm(int i){
        return alarms.get(i);
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
    }
    public void deleteAlarm(int alarmID){
        //remove alarm
        for(Iterator<Alarm> iterator = alarms.iterator(); iterator.hasNext(); ) {
            if(iterator.next().getAlarmID()==alarmID)
                iterator.remove();
        }

    }
    public String getUserID(){
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

        //fix alarm list so that their ID are consecutive (no gaps)
        for (int i=0; i<getAlarmSize();i++){
            toString+="\n"+alarms.get(i).toString();
        }

        return toString;
    }


}
