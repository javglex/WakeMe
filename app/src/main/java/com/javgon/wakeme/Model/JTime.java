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
    int weekDay;  //today (monday - sunday)
    boolean [] repeatDays = new boolean[7];  //array of bools each representing a day of the week (index0 = sunday, index6 = saturday)

    public JTime(){
        Calendar date = Calendar.getInstance();
        this.weekDay=date.get(Calendar.DAY_OF_WEEK)-1;
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
        this.weekDay=date.get(Calendar.DAY_OF_WEEK)-1;

    }

    /**
     * calculates the difference between two alarm times
     * @param time
     * @return JTime object diff in hours
     */
    public int diffHours(JTime time) { //difference between this time and parameter time
        int diffHour;
        int diffTime;
        int diffDays;
        int closestWeekDay=weekDay;

        for ( int i =weekDay; i<7; i++)
        {
            if (repeatDays[i]==true){
                closestWeekDay=i;
                break;
            } else closestWeekDay=weekDay;
        }

        diffDays=closestWeekDay-weekDay;
        int totalTimeToday= time.getHour()*60 +time.getMinute();  //total time in minutes today

        int totalTimeNext = (diffDays*24*60)+this.hour*60 + this.minute; //total time for next alarm in minutes
        diffTime=totalTimeNext-totalTimeToday;

        diffHour=Math.round((float)(diffTime/60.0));

        return diffHour;
    }


    public static JTime dateToTime(Calendar date){
        int hour = date.get(Calendar.HOUR_OF_DAY);
        int minute = date.get(Calendar.MINUTE);
        Log.d("until weeksday", minute+ " "+ hour+ " "+(date.get(Calendar.DAY_OF_WEEK)-1) );
        return new JTime(hour,minute);

    }

    public void setHour(int hour){
        this.hour=hour;
    }

    public void setMinute(int minute){
        this.minute=minute;
    }
    public void setRepeatDays(boolean [] days){
        repeatDays=days;
    }
    public JTime setRepeatMonday(boolean b){
        repeatDays[1]=b;
        return this;
    }
    public JTime setRepeatTuesday(boolean b){
        repeatDays[2]=b;
        return this;
    }
    public JTime setRepeatWednesday(boolean b){
        repeatDays[3]=b;
        return this;
    }
    public JTime setRepeatThursday(boolean b){
        repeatDays[4]=b;
        return this;
    }
    public JTime setRepeatFriday(boolean b){
        repeatDays[5]=b;
        return this;
    }
    public JTime setRepeatSaturday(boolean b){
        repeatDays[6]=b;
        return this;
    }
    public JTime setRepeatSunday(boolean b){
        repeatDays[0]=b;
        return this;
    }

    public boolean[] getRepeatDays(boolean [] b){
        return repeatDays;
    }
    public int getHour(){
        return this.hour;
    }
    public int getMinute(){
        return this.minute;
    }
    public int getWeekDay(){
        return this.weekDay;
    }
    public JTime getTime(){
        return this;
    }




}
