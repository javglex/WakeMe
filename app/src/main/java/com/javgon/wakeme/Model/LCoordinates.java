package com.javgon.wakeme.Model;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by javgon on 4/18/2017.
 */

public class LCoordinates implements Serializable {
    private double latitude;
    private double longitude;

    public LCoordinates(){
        latitude=longitude=0;
    }


    public void setLatitude(double latitude){
        this.latitude=latitude;
    }
    public void setLongitude(double longitude){
        this.longitude=longitude;
    }
    public void setLocation(LCoordinates loc){
        this.latitude=loc.getLatitude();
        this.longitude=loc.getLongitude();
    }

    public double getLatitude(){
        return this.latitude;
    }

    public double getLongitude(){
        return this.longitude;
    }

    public String toString(){

        NumberFormat formatter = new DecimalFormat("#0.0");
        String toString =formatter.format(this.latitude) + ", " + formatter.format(this.longitude);

        return toString;
    }

}