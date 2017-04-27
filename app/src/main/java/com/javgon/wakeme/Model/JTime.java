package com.javgon.wakeme.Model;

import android.util.Log;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by javier gonzalez on 4/24/2017.
 * New time class that only takes into account hour and minutes
 * uses 24hour time format
 */

public class JTime {

    int hour;
    int minute;

    public JTime(){
        Calendar date = Calendar.getInstance();
    }

    /**
     * JTime constructor, takes in 24 hour format time in 3 different paramters
     * @param hour 0-23// once it hits 23:59, day is reset
     * @param minute 0-59  //once it hits 60, hours is incremented immediately
     */
    public JTime(int hour, int minute){
        Calendar date = Calendar.getInstance();
        this.hour=hour;
        this.minute=minute;

    }



    public static JTime dateToTime(Calendar date){
        int hour = date.get(Calendar.HOUR_OF_DAY);
        int minute = date.get(Calendar.MINUTE);
        return new JTime(hour,minute);

    }

    public void setHour(int hour){
        this.hour=hour;
    }
    public void setMinute(int minute){
        this.minute=minute;
    }
    public int getHour(){
        return this.hour;
    }
    public int getMinute(){
        return this.minute;
    }
    public JTime getTime(){
        return this;
    }




}
