package com.example.carlos.finalproject;

/**
 * Created by Carlos on 17/11/18.
 */

public class ActivityInfo {
    public String actName;
    public String actId;
    public String locName;
    public String lng;
    public String lat;
    public String actTime;

    public String locType;

    public ActivityInfo(String actName, String id, String locName, String lng, String lat, String time) {
        this.actName = actName;
        this.actId = id;
        this.locName = locName;
        this.lng = lng;
        this.lat = lat;
        this.actTime = time;
    }

    public ActivityInfo(String id, String lat, String lng, String time, String locType) {
        this.actId = id;
        this.lng = lng;
        this.lat = lat;
        this.actTime = time;
        this.locType=locType;
    }

}
