package com.javgon.wakeme.Model;

import android.util.Log;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    ArrayList<Integer> repeatDays = new ArrayList(7);  //array of bools each representing a day of the week (index0 = sunday, index6 = saturday)


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
        int hour = diffHours();
        return hour;
    }

    /**
     * calculates the difference between today and alarm time
     * @return JTime object diff in hours
     */
    public int diffHours() { //difference between this time and parameter time
        int diffHour;
        int diffTime;
        int diffDays;
        Calendar today= Calendar.getInstance();
        JTime rightnow = JTime.dateToTime(today);


        diffDays=findNearestDistance(today);

        int totalTimeToday= rightnow.getHour()*60 +rightnow.getMinute();  //total time in minutes today
        int totalTimeNext = (diffDays*24*60)+this.alarmTimeHours*60 + this.alarmTimeMinutes; //total time for next alarm in minutes
        diffTime=totalTimeNext-totalTimeToday;

        diffHour=Math.round((float)(diffTime/60.0));

        return diffHour;
    }

    public void setRepeatDays(ArrayList<Integer> days){
        repeatDays=days;
    }
    public ArrayList<Integer> getRepeatDays(){
        return repeatDays;
    }

    public int findNearestDistance(Calendar today){


        int weekDay=today.get(Calendar.DAY_OF_WEEK)-1; //0-6 (sundat to saturday)
        int nearestDistance=-100;
        int prevNearestDistance=-100;
        for (int i=0; i<repeatDays.size(); i++)
        {
            prevNearestDistance=repeatDays.get(i)-weekDay;
            if (prevNearestDistance>nearestDistance){
                nearestDistance=prevNearestDistance;
            }

        }
        Log.d("alarms distance", ""+nearestDistance);
        if (nearestDistance<0)
            return 7+nearestDistance;
        else return nearestDistance;
    }
}
