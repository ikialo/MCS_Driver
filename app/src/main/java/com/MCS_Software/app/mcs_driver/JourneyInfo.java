package com.MCS_Software.app.mcs_driver;

import com.google.android.gms.maps.model.LatLng;

public class JourneyInfo {

    String name, date, time, driver,car, userID, destin, origin, fare;
    LatLng desLatLng, oriLatLng;

    public JourneyInfo(String name, String date, String time, String driver, String car, String userID,
                       String destin, String origin, String fare, LatLng desLatLng, LatLng oriLatLng) {
        this.name = name;
        this.date = date;
        this.time = time;
        this.driver = driver;
        this.car = car;
        this.userID = userID;
        this.destin = destin;
        this.origin = origin;
        this.fare = fare;
        this.desLatLng = desLatLng;
        this.oriLatLng = oriLatLng;
    }

    public LatLng getDesLatLng() {
        return desLatLng;
    }

    public void setDesLatLng(LatLng desLatLng) {
        this.desLatLng = desLatLng;
    }

    public LatLng getOriLatLng() {
        return oriLatLng;
    }

    public void setOriLatLng(LatLng oriLatLng) {
        this.oriLatLng = oriLatLng;
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

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getCar() {
        return car;
    }

    public void setCar(String car) {
        this.car = car;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
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

    public String getFare() {
        return fare;
    }

    public void setFare(String fare) {
        this.fare = fare;
    }
}
