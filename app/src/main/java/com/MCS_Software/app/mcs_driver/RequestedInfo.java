package com.MCS_Software.app.mcs_driver;

import com.google.android.gms.maps.model.LatLng;

public class RequestedInfo {

    String name, date, time, fair,destin, origin, userID, car, driver;
    LatLng oriLatLng, desLatLng;


    public RequestedInfo(String date, String time, String fair, String destin, String origin, LatLng oriLatLng, LatLng desLatLng, String name, String userID) {
        this.date = date;
        this.time = time;
        this.fair = fair;
        this.destin = destin;
        this.origin = origin;
        this.oriLatLng = oriLatLng;
        this.desLatLng = desLatLng;
        this.name = name;
        this.userID = userID;
    }
    public RequestedInfo(String date, String time, String fair, String destin, String origin, LatLng oriLatLng, LatLng desLatLng, String name, String userID,
                         String car, String driver) {
        this.date = date;
        this.time = time;
        this.fair = fair;
        this.destin = destin;
        this.origin = origin;
        this.oriLatLng = oriLatLng;
        this.desLatLng = desLatLng;
        this.name = name;
        this.userID = userID;
        this.car =car;
        this.driver = driver;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getCar() {
        return car;
    }

    public void setCar(String car) {
        this.car = car;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getTripid() {
        return userID;
    }

    public void setTripid(String tripid) {
        this.userID = tripid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getFair() {
        return fair;
    }

    public void setFair(String fair) {
        this.fair = fair;
    }

    public String getDestin() {
        return destin;
    }

    public void setDestin(String destin) {
        this.destin = destin;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public LatLng getOriLatLng() {
        return oriLatLng;
    }

    public void setOriLatLng(LatLng oriLatLng) {
        this.oriLatLng = oriLatLng;
    }

    public LatLng getDesLatLng() {
        return desLatLng;
    }

    public void setDesLatLng(LatLng desLatLng) {
        this.desLatLng = desLatLng;
    }
}
