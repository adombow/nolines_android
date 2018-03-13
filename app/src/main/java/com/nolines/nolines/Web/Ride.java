package com.nolines.nolines.Web;

/**
 * Created by Andrew on 2018-03-12.
 */

public class Ride {
    private String name;
    private Double lat;
    private Double lon;
    private int wait_time;
    private int photoID;

    public Ride(String name, Double lat, Double lon, int wait_time, int photoID) {
        this.name = name;
        this.lat = lat;
        this.lon = lon;
        this.wait_time = wait_time;
        this.photoID = photoID;
    }

    public String getName(){ return this.name; }
    public double getLat(){
        return this.lat;
    }
    public double getLon(){
        return this.lon;
    }
    public int getWait_time(){
        return this.wait_time;
    }
    public int getPhotoID(){
        return this.photoID;
    }
}
