package com.javgon.wakeme.Model;

import android.util.Log;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by javgon on 4/20/2017.
 */

public class Alarm {

    int alarmTimeHours;  //hour that alarm is set to
    int alarmTimeMinutes; //minute that alarm is set to
    String alarmID;     //User the alarm belongs to
    int hoursUntilAlarm;    //time until alarm rings


    public Alarm(){

    }

    public Alarm(String alarmID, int alarmTimeHours, int alarmTimeMinutes){
        this.alarmID=alarmID;
        this.alarmTimeMinutes=alarmTimeMinutes;
        this.alarmTimeHours=alarmTimeHours;

    }

    public void setAlarmID(String id){
        this.alarmID=id;
    }
    public String getAlarmID(){
        return this.alarmID;
    }

    public void setAlarmTime(JTime t){
        this.alarmTimeHours=t.getHour();
        this.alarmTimeMinutes=t.getMinute();
    }
    public JTime retrieveAlarmTime(){ //named retrieved instead of get so that it wouldn't try to be serialized
        JTime alarmTime = new JTime(alarmTimeHours,alarmTimeMinutes);
        return alarmTime;
    }

    public int getAlarmTimeHours() {
        return alarmTimeHours;
    }

    public void setAlarmTimeHours(int alarmTimeHours) {
        this.alarmTimeHours = alarmTimeHours;
    }

    public int getAlarmTimeMinutes() {
        return alarmTimeMinutes;
    }

    public void setAlarmTimeMinutes(int alarmTimeMinutes) {
        this.alarmTimeMinutes = alarmTimeMinutes;
    }

    public void setHoursUntilAlarm(int hours){
        this.hoursUntilAlarm=hours;
    }
    public int getHoursUntilAlarm(){
        Calendar rightNow = Calendar.getInstance();
        JTime alarmTime = new JTime(alarmTimeHours,alarmTimeMinutes);
        int hour = alarmTime.diffHours(JTime.dateToTime(rightNow));

        return hour;
    }

}
